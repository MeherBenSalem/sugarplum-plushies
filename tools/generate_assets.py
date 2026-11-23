#!/usr/bin/env python3
"""Generates every Sugarplum Plushies asset: plushie & furniture models, all textures,
blockstates, item models, animated-part table, recipes, loot, tags, advancements and lang.

    python tools/generate_assets.py

The output folders (assets/sugarplumplushies, data/sugarplumplushies and the
data/minecraft tags) are wiped and fully regenerated on every run.
"""
import shutil
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from assetgen import data, decor, textures2d  # noqa: E402
from assetgen.core import ASSETS, DATA, MODID, ROOT, Img, bounds, model_elements, plan_layout, write_json  # noqa: E402
from assetgen.paint import DECALS, paint_tile  # noqa: E402
from assetgen.plushies import SPECIES, variant_palettes  # noqa: E402

M = MODID + ":"
DECAL_NAMES = set(DECALS)
FACING_Y = {"north": 0, "east": 90, "south": 180, "west": 270}


def paint_texture(layout, tex, pal, face_spec, seed, path):
    img = Img(tex, tex)
    for mat, rect in layout.items():
        paint_tile(img, mat, rect, pal, face_spec, seed)
    img.save(path)


def plushie_display(elements):
    lo, hi = bounds(elements)
    height = hi[1] - lo[1]
    width = max(hi[0] - lo[0], hi[2] - lo[2])
    s = min(1.0, 14.0 / max(height, width * 0.95))
    cy = (lo[1] + hi[1]) / 2
    r = lambda v: round(v, 3)
    return {
        "gui": {"rotation": [20, 200, 0], "translation": [0, r((8 - cy) * s), 0], "scale": [r(s)] * 3},
        "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.4, 0.4, 0.4]},
        "fixed": {"rotation": [0, 0, 0], "translation": [0, r((8 - cy) * s * 0.75), 0], "scale": [r(s * 0.75)] * 3},
        "thirdperson_righthand": {"rotation": [75, 15, 0], "translation": [0, 2.5, 0], "scale": [0.4, 0.4, 0.4]},
        "thirdperson_lefthand": {"rotation": [75, -15, 0], "translation": [0, 2.5, 0], "scale": [0.4, 0.4, 0.4]},
        "firstperson_righthand": {"rotation": [0, 160, 0], "translation": [0, 1, 0], "scale": [0.45, 0.45, 0.45]},
        "firstperson_lefthand": {"rotation": [0, 200, 0], "translation": [0, 1, 0], "scale": [0.45, 0.45, 0.45]},
        "head": {"rotation": [0, 0, 0], "translation": [0, 14, 0], "scale": [0.8, 0.8, 0.8]},
    }


def generate_plushies():
    parts_table = {}
    variant_ids = {}
    for sid, spec in SPECIES.items():
        elements = spec["build"]()
        layout, tex_size = plan_layout(elements, DECAL_NAMES)
        part_names = [p["name"] for p in spec["parts"]]
        for e in elements:
            if e.part is not None and e.part not in part_names:
                raise ValueError(f"{sid}: element tagged with unknown part {e.part}")
        body = [e for e in elements if e.part is None]
        display = plushie_display(elements)
        parts_table[sid] = spec["parts"]
        variants = variant_palettes(spec)
        variant_ids[sid] = [vid for vid, _ in variants]

        blockstate = {}
        for index, (vid, pal) in enumerate(variants):
            tex = f"{M}block/plushie/{sid}/{vid}"
            paint_texture(layout, tex_size, pal, spec["face"], f"{sid}/{vid}",
                          ASSETS / "textures" / "block" / "plushie" / sid / f"{vid}.png")
            base = {"ambientocclusion": False, "render_type": "minecraft:cutout", "textures": {"t": tex, "particle": tex}}
            write_json(ASSETS / "models" / "block" / "plushie" / sid / f"{vid}.json",
                       dict(base, elements=model_elements(body, layout, DECAL_NAMES, tex_size)))
            for name in part_names:
                write_json(ASSETS / "models" / "block" / "plushie" / sid / f"{vid}_{name}.json",
                           dict(base, elements=model_elements([e for e in elements if e.part == name], layout, DECAL_NAMES, tex_size)))
            item = dict(base, gui_light="side", display=display, elements=model_elements(elements, layout, DECAL_NAMES, tex_size))
            if index == 0:
                item["overrides"] = [{"predicate": {f"{M}variant": i}, "model": f"{M}item/plushie/{sid}/{variants[i][0]}"}
                                     for i in range(1, len(variants))]
                write_json(ASSETS / "models" / "item" / f"{sid}.json", item)
            else:
                write_json(ASSETS / "models" / "item" / "plushie" / sid / f"{vid}.json", item)
            blockstate[f"variant={index}"] = {"model": f"{M}block/plushie/{sid}/{vid}"}
        write_json(ASSETS / "blockstates" / f"{sid}.json", {"variants": blockstate})
    write_json(ASSETS / "plushie_parts.json", parts_table)
    return variant_ids


def decor_model(name, elements, pal, render_type="minecraft:cutout", extra_elements=None, face_spec=None):
    """Writes block model + texture for one piece of furniture and returns its layout."""
    all_elements = elements + (extra_elements or [])
    layout, size = plan_layout(all_elements, DECAL_NAMES)
    tex = f"{M}block/{name}"
    paint_texture(layout, size, pal, face_spec or {}, name, ASSETS / "textures" / "block" / f"{name}.png")
    write_json(ASSETS / "models" / "block" / f"{name}.json", {
        "parent": "minecraft:block/block", "render_type": render_type,
        "textures": {"t": tex, "particle": tex}, "elements": model_elements(elements, layout, DECAL_NAMES, size)})
    return layout, size, tex


def facing_blockstate(name, model=None):
    model = model or f"{M}block/{name}"
    variants = {}
    for facing, y in FACING_Y.items():
        v = {"model": model}
        if y:
            v["y"] = y
        variants[f"facing={facing}"] = v
    write_json(ASSETS / "blockstates" / f"{name}.json", {"variants": variants})


def block_item(name, parent=None):
    write_json(ASSETS / "models" / "item" / f"{name}.json", {"parent": parent or f"{M}block/{name}"})


def generate_decor():
    names = []

    def simple(name, elements, pal, face_spec=None):
        decor_model(name, elements, pal, face_spec=face_spec)
        facing_blockstate(name)
        block_item(name)
        names.append(name)

    simple("plushie_workshop", decor.workshop(), decor.WORKSHOP_PAL)
    simple("plushie_shelf", decor.shelf(), decor.SHELF_PAL)
    simple("display_stand", decor.stand(), decor.STAND_PAL)
    for color in decor.COLORS:
        simple(f"{color}_plushie_bed", decor.bed(), decor.BED_PALS[color])
        simple(f"{color}_plushie_box", decor.gift_box(), decor.BOX_PALS[color])

    teddy = SPECIES["pink_teddy_bear"]
    for name, pal in decor.TROPHY_PALS.items():
        simple(name, decor.trophy(teddy["build"]()), pal, face_spec=teddy["face"])

    # Cabinet: opaque frame + translucent glass sharing one texture, combined with multipart.
    frame, glass = decor.cabinet_frame(), decor.cabinet_glass()
    layout, size, tex = decor_model("display_cabinet", frame, decor.CABINET_PAL, extra_elements=glass)
    write_json(ASSETS / "models" / "block" / "display_cabinet_glass.json", {
        "parent": "minecraft:block/block", "render_type": "minecraft:translucent",
        "textures": {"t": tex, "particle": tex}, "elements": model_elements(glass, layout, DECAL_NAMES, size)})
    multipart = []
    for facing, y in FACING_Y.items():
        for model in ("display_cabinet", "display_cabinet_glass"):
            apply = {"model": f"{M}block/{model}"}
            if y:
                apply["y"] = y
            multipart.append({"when": {"facing": facing}, "apply": apply})
    write_json(ASSETS / "blockstates" / "display_cabinet.json", {"multipart": multipart})
    write_json(ASSETS / "models" / "item" / "display_cabinet.json", {
        "parent": "minecraft:block/block", "render_type": "minecraft:translucent",
        "textures": {"t": tex, "particle": tex}, "elements": model_elements(frame + glass, layout, DECAL_NAMES, size)})
    names.append("display_cabinet")
    return names


def generate_items_and_particles():
    for name in textures2d.ITEMS:
        write_json(ASSETS / "models" / "item" / f"{name}.json",
                   {"parent": "minecraft:item/generated", "textures": {"layer0": f"{M}item/{name}"}})
    write_json(ASSETS / "particles" / "pastel_heart.json", {"textures": [f"{M}pastel_heart"]})
    write_json(ASSETS / "particles" / "sleepy_z.json", {"textures": [f"{M}sleepy_z"]})
    write_json(ASSETS / "particles" / "twinkle.json",
               {"textures": [f"{M}twinkle_0", f"{M}twinkle_1", f"{M}twinkle_2", f"{M}twinkle_1", f"{M}twinkle_0"]})


def main():
    for folder in (ASSETS, DATA, ROOT / "data" / "minecraft"):
        if folder.exists():
            shutil.rmtree(folder)
    variant_ids = generate_plushies()
    decor_blocks = generate_decor()
    textures2d.generate()
    generate_items_and_particles()
    data.generate(variant_ids, list(SPECIES) + decor_blocks)

    files = [p for p in ROOT.rglob("*") if p.is_file() and p.suffix in (".json", ".png")]
    print(f"Generated {len(files)} files: "
          f"{sum(1 for p in files if p.suffix == '.png')} textures, "
          f"{sum(1 for p in files if p.suffix == '.json')} json")


if __name__ == "__main__":
    main()
