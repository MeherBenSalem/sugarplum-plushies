#!/usr/bin/env python3
"""Renders the plushies and furniture the artwork shows from the mod's own models and textures,
with transparent backgrounds, into marketing/source/renders/. Uses preview_models.py next to
this file, so it needs no Minecraft client. Run from the repository root:

    python marketing/source/renders.py

Every plushie is drawn in all four of its variants by swapping its texture: the item model is
the classic variant, and <species>@<variant>.png are the others (the Sparkle Edition included).
A few furniture pieces borrow vanilla textures; set SP_VANILLA to an extracted 1.20.1 client
jar's assets/minecraft folder to draw them, or they are skipped.
"""
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(ROOT / "tools"))
sys.path.insert(0, str(HERE))
from assetgen.core import ASSETS, MODID  # noqa: E402
from preview_models import render  # noqa: E402

OUT = HERE / "renders"
SCALE = 10
FURNITURE = ["plushie_workshop", "plushie_shelf", "display_stand", "display_cabinet",
             "pink_plushie_bed", "lavender_plushie_bed", "mint_plushie_bed", "sky_plushie_bed",
             "pink_plushie_box", "lavender_plushie_box", "mint_plushie_box", "sky_plushie_box",
             "golden_collector_trophy", "rainbow_collector_trophy"]


def main():
    OUT.mkdir(exist_ok=True)
    species = sorted(p.name for p in (ASSETS / "textures" / "block" / "plushie").iterdir() if p.is_dir())
    jobs = []
    for s in species:
        for tex in sorted((ASSETS / "textures" / "block" / "plushie" / s).glob("*.png")):
            jobs.append((f"{MODID}:item/{s}", f"{s}@{tex.stem}",
                         {"t": f"{MODID}:block/plushie/{s}/{tex.stem}"}))
    jobs += [(f"{MODID}:item/{f}", f, None) for f in FURNITURE]
    skipped = []
    for ref, name, retex in jobs:
        try:
            render(ref, SCALE, retex=retex).save(OUT / f"{name}.png")
        except FileNotFoundError:
            skipped.append(name)
    print(f"rendered {len(jobs) - len(skipped)} of {len(jobs)} into {OUT}")
    if skipped:
        print("skipped (set SP_VANILLA):", " ".join(skipped))


if __name__ == "__main__":
    main()
