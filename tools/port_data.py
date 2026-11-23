#!/usr/bin/env python3
"""Derives a newer Minecraft version's common resources from the 1.20.1 ones.

1.20.1/common/src/main/resources holds the hand-made and tools/generate_assets.py resources. This script copies them
into another workspace and rewrites the data (and, from 1.21.4 on, adds item model definitions) in that version's
formats:

    python3 tools/port_data.py 1.21.1
    python3 tools/port_data.py 26.2
    python3 tools/port_data.py 26.3

Standard library only. The target's common/src/main/resources/{assets,data} are replaced; pack.mcmeta is rewritten.
"""
import json
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SOURCE = ROOT / "1.20.1" / "common" / "src" / "main" / "resources"
MODID = "sugarplumplushies"
import re

PLUSHIE_VARIANT_NBT = re.compile(r'^\{BlockStateTag:\{variant:"(\d)"\}\}$')

# (pack_format, min, max) for the mod's pack.mcmeta; 26.x uses a min/max format range only.
PACK = {"1.21.1": (34, 34, 48), "26.2": (None, 88, 107), "26.3": (None, 97, 121)}
# Data folders renamed in 1.21.
RENAMED = {"loot_tables": "loot_table", "recipes": "recipe", "structures": "structure", "advancements": "advancement"}
RENAMED_TAGS = {"items": "item", "blocks": "block", "entity_types": "entity_type", "fluids": "fluid", "game_events": "game_event"}


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def dump(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2) + "\n", encoding="utf-8")


def is_26(version):
    return version.startswith("26.")


# ----------------------------------------------------------------------- recipes

def ingredient_26(ing):
    """1.21.2+: an ingredient is an item id, a #tag, or a list of those."""
    if isinstance(ing, list):
        return [ingredient_26(i) for i in ing]
    if "item" in ing:
        return ing["item"]
    if "tag" in ing:
        return "#" + ing["tag"]
    return ing


def variant_components(nbt):
    """A plushie's 1.20.1 BlockStateTag nbt becomes the minecraft:block_state component."""
    match = PLUSHIE_VARIANT_NBT.match(nbt)
    if not match:
        raise SystemExit(f"unsupported nbt {nbt!r}")
    return {"minecraft:block_state": {"variant": match.group(1)}}


def recipe(obj, version):
    if "result" in obj and isinstance(obj["result"], dict) and "item" in obj["result"]:
        obj["result"]["id"] = obj["result"].pop("item")
        if "nbt" in obj["result"]:
            obj["result"]["components"] = variant_components(obj["result"].pop("nbt"))
    if is_26(version):
        if "ingredients" in obj:
            # Workshop recipes nest each ingredient as {"ingredient": ..., "count": n}.
            obj["ingredients"] = [dict(i, ingredient=ingredient_26(i["ingredient"])) if isinstance(i, dict) and "ingredient" in i
                                  else ingredient_26(i) for i in obj["ingredients"]]
        if "key" in obj:
            obj["key"] = {k: ingredient_26(v) for k, v in obj["key"].items()}
    return obj


# ----------------------------------------------------------------------- 26.3 condition/modifier format

def condition_263(cond):
    """26.3: a loot condition is {"type": ...}; block_state_property became match_block."""
    cond = dict(cond)
    if "condition" in cond:
        cond["type"] = cond.pop("condition")
    if cond.get("type") == "minecraft:block_state_property":
        cond["type"] = "minecraft:match_block"
        cond["blocks"] = cond.pop("block")
        if "properties" in cond:
            cond["state"] = cond.pop("properties")
    for key in ("term", "terms"):
        if key in cond:
            cond[key] = condition_263(cond[key]) if isinstance(cond[key], dict) else [condition_263(t) for t in cond[key]]
    return cond


def condition_list_263(conds):
    """26.3: a list of conditions is one condition (all_of when there are several)."""
    conds = [condition_263(c) for c in conds]
    return conds[0] if len(conds) == 1 else {"type": "minecraft:all_of", "terms": conds}


def modifiers_263(holder):
    """26.3: "functions" became "modifier", "conditions" became "condition", and a function's key is "type"."""
    if "functions" in holder:
        fns = []
        for fn in holder.pop("functions"):
            fn = dict(fn)
            fn["type"] = fn.pop("function")
            if "conditions" in fn:
                fn["condition"] = condition_list_263(fn.pop("conditions"))
            fns.append(fn)
        holder["modifier"] = fns
    if "conditions" in holder:
        holder["condition"] = condition_list_263(holder.pop("conditions"))


# ----------------------------------------------------------------------- loot tables

def loot_table(obj, version):
    if version == "26.3":
        for pool in obj.get("pools", []):
            for entry in pool.get("entries", []):
                modifiers_263(entry)
            modifiers_263(pool)
        modifiers_263(obj)
    return obj


# ----------------------------------------------------------------------- advancements

def item_predicate(pred):
    """1.21: "tag" became "items": "#tag", and the plushie variant nbt became a component match."""
    pred = dict(pred)
    if "tag" in pred:
        pred["items"] = "#" + pred.pop("tag")
    if "nbt" in pred:
        pred["components"] = variant_components(pred.pop("nbt"))
    return pred


def advancement(obj, version):
    display = obj.get("display")
    if display:
        icon = display.get("icon")
        if icon and "item" in icon:
            icon["id"] = icon.pop("item")
        bg = display.get("background")
        if bg and is_26(version):
            # 1.21.5+: the background is a texture id without the textures/ prefix and .png suffix.
            ns, path = bg.split(":", 1)
            display["background"] = ns + ":" + path.removeprefix("textures/").removesuffix(".png")
    for crit in obj.get("criteria", {}).values():
        items = crit.get("conditions", {}).get("items")
        if items:
            crit["conditions"]["items"] = [item_predicate(p) for p in items]
    return obj


# ----------------------------------------------------------------------- item model definitions (1.21.4+)

def item_definitions(assets):
    """1.21.4+: every item needs assets/<ns>/items/<id>.json. A plushie item (its model has "overrides" on the
    sugarplumplushies:variant property) selects its variant model from the minecraft:block_state component."""
    for model in sorted((assets / "models" / "item").glob("*.json")):
        obj = load(model)
        base = {"type": "minecraft:model", "model": f"{MODID}:item/{model.stem}"}
        overrides = obj.get("overrides")
        if overrides:
            cases = []
            for o in overrides:
                v = o.get("predicate", {}).get(f"{MODID}:variant")
                if v is None:
                    continue
                variant = str(int(v))
                cases.append({"when": variant, "model": {"type": "minecraft:model", "model": o["model"]}})
            definition = {"type": "minecraft:select", "property": "minecraft:block_state",
                          "block_state_property": "variant", "cases": cases, "fallback": base}
            obj.pop("overrides")
            dump(model, obj)
        else:
            definition = base
        dump(assets / "items" / model.name, {"model": definition})


# ----------------------------------------------------------------------- 26.x assets

def block_models_26(assets, version):
    """26.1+: a quad's render layer comes from its texture's transparency, so Forge's "render_type" hint is dropped.

    26.3: an element's "shade": false became "shade_direction_override": "up" ("shade": true is the default)."""
    for path in sorted((assets / "models").glob("**/*.json")):
        model = load(path)
        changed = model.pop("render_type", None) is not None
        if version == "26.3":
            for element in model.get("elements", []):
                if "shade" in element:
                    if not element.pop("shade"):
                        element["shade_direction_override"] = "up"
                    changed = True
        if changed:
            dump(path, model)


# ----------------------------------------------------------------------- driver

def port(version):
    target = ROOT / version / "common" / "src" / "main" / "resources"
    for sub in ("assets", "data"):
        shutil.rmtree(target / sub, ignore_errors=True)
        shutil.copytree(SOURCE / sub, target / sub)
    shutil.copy2(SOURCE / f"{MODID}_logo.png", target / f"{MODID}_logo.png")

    for ns_dir in (target / "data").iterdir():
        for old, new in RENAMED.items():
            if (ns_dir / old).exists():
                (ns_dir / old).rename(ns_dir / new)
        tags = ns_dir / "tags"
        if tags.exists():
            for old, new in RENAMED_TAGS.items():
                if (tags / old).exists():
                    (tags / old).rename(tags / new)

    data = target / "data"
    for path in data.glob("*/recipe/**/*.json"):
        dump(path, recipe(load(path), version))
    for path in data.glob("*/loot_table/**/*.json"):
        dump(path, loot_table(load(path), version))
    for path in data.glob("*/advancement/**/*.json"):
        dump(path, advancement(load(path), version))

    if is_26(version):
        item_definitions(target / "assets" / MODID)
        block_models_26(target / "assets" / MODID, version)

    fmt, lo, hi = PACK[version]
    pack = {"pack": {"description": {"text": "Sugarplum Plushies resources"}}}
    if fmt is not None:
        pack["pack"]["pack_format"] = fmt
        pack["pack"]["supported_formats"] = {"min_inclusive": lo, "max_inclusive": hi}
    else:
        # 1.21.9+: a format range instead of pack_format.
        pack["pack"]["min_format"] = lo
        pack["pack"]["max_format"] = hi
    dump(target / "pack.mcmeta", pack)
    print(f"ported resources to {target}")


if __name__ == "__main__":
    if len(sys.argv) != 2 or sys.argv[1] not in PACK:
        sys.exit(f"usage: {sys.argv[0]} {{{','.join(PACK)}}}")
    port(sys.argv[1])
