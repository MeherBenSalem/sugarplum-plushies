"""The fifteen Sugarplum plushies: geometry, palettes (4 variants each), embroidered
face specs and animated parts. Front of every plushie faces north (low z)."""
from .core import box, flower, mirrored, rbox, sym


def sparkle(pal, **over):
    """Sparkle Edition: pearly, lighter fabrics with gold trims and glitter."""
    from .core import lighten, mix
    out = dict(pal)
    for key in ("main", "light", "inner", "accent", "dark", "spot", "membrane", "wing", "berry"):
        if key in pal:
            out[key] = mix(lighten(pal[key], 0.25), "#FFF4FB", 0.18)
    out["gold"] = "#FFD36E"
    out["seed"] = "#FFD36E"
    out["sparkle"] = True
    out["motif"] = pal.get("motif") and "star"
    out["motif_color"] = "#FFC94D"
    out.update(over)
    return out


# ----------------------------------------------------------------------------- 1
def strawberry_bunny():
    e = []
    e += rbox((5, 0, 6.5), (11, 5, 10.5), "fabric/main")
    e += box((6, 0.5, 6.1), (10, 4.25, 6.5), "fabric/light", faces={"north": "belly"})
    e += sym(box((5.25, 0, 4.25), (7.25, 1.75, 6.75), "fabric/main", faces={"north": "paw"}))
    e += sym(box((4.25, 1.5, 7), (5.25, 4.5, 9), "fabric/main", rot=("z", -22.5, (4.75, 4.5, 8))))
    e += rbox((7, 1, 10.5), (9, 3, 12), "fabric/light", r=0.25)
    e += rbox((4, 5, 5), (12, 11, 10.5), "fabric/main", front="face")
    e += rbox((3.6, 9.25, 4.6), (12.4, 12.25, 11), "berry")
    e += sym(box((5.25, 11.5, 7.25), (6.75, 16, 8.25), "fabric/main", faces={"north": "fabric/inner"},
                 rot=("z", 22.5, (6, 12, 7.75))))
    e += box((5.5, 12.25, 7.5), (10.5, 12.75, 8.5), "fabric/leaf")
    e += box((7.5, 12.25, 5.5), (8.5, 12.75, 10.5), "fabric/leaf")
    e += box((6.5, 12.25, 6.5), (9.5, 12.6, 9.5), "fabric/leaf")
    e += box((7.6, 12.75, 7.6), (8.4, 13.75, 8.4), "fabric/leaf_dark")
    return e


STRAWBERRY_BUNNY = dict(
    build=strawberry_bunny, parts=[],
    face=dict(eyes="sparkle", eye_y=0.5, eye_dx=0.46, mouth="w", mouth_y=0.8, nose=True, extras=("muzzle",)),
    variants={
        "classic": dict(main="#F9C9D6", light="#FFEAF0", inner="#FF9FB8", berry="#F0506E", seed="#FFE48A",
                        motif="heart", accent="#FF7FA6"),
        "vanilla": dict(main="#FFF3E0", light="#FFFDF6", inner="#FFC1CF", berry="#FF9DB8", seed="#FFFFFF",
                        motif="heart", accent="#FFA8C5"),
        "blueberry": dict(main="#D9D4F7", light="#F2F0FF", inner="#BDB4F4", berry="#6F7BDB", seed="#D6E4FF",
                          leaf="#8FD1A8", motif="heart", accent="#8E8BE8", iris="#7C78D8"),
    },
    sparkle=dict(berry="#FF9CC0"),
)


# ----------------------------------------------------------------------------- 2
def pink_teddy_bear():
    e = []
    e += rbox((4.75, 0, 6), (11.25, 5.5, 10.5), "fabric/main")
    e += box((6, 0.75, 5.6), (10, 4.5, 6), "fabric/light", faces={"north": "belly"})
    e += sym(box((4.75, 0, 3.75), (7.25, 2, 6.5), "fabric/main", faces={"north": "paw"}))
    e += sym(box((3.75, 1.75, 6.5), (5, 5, 8.5), "fabric/main", rot=("z", -22.5, (4.4, 5, 7.5))))
    e += rbox((4, 5.5, 5), (12, 11.5, 10.5), "fabric/main", front="face")
    e += box((6.5, 6, 4.25), (9.5, 8, 5), "fabric/light", faces={"north": "snout"})
    e += sym(rbox((4, 10.5, 7), (6.25, 12.75, 8.5), "fabric/main", r=0.4, front="fabric/inner"))
    e += box((7.5, 4.75, 5.1), (8.5, 5.75, 5.6), "satin/accent")
    e += box((6, 4.5, 5.25), (7.5, 6, 5.6), "satin/accent")
    e += box((8.5, 4.5, 5.25), (10, 6, 5.6), "satin/accent")
    e += box((7.25, 1, 10.5), (8.75, 2.5, 11.25), "fabric/main")
    return e


PINK_TEDDY_BEAR = dict(
    build=pink_teddy_bear, parts=[],
    face=dict(eyes="sparkle", eye_y=0.36, eye_dx=0.5, blush_dy=1.0, nose_style="oval", snout_mouth="w"),
    variants={
        "pink": dict(main="#F6B8CB", light="#FFE3EC", inner="#FF9DB9", accent="#FF6FA3", nose="#6B3A4F", motif=None),
        "honey": dict(main="#E8B98A", light="#FBE5C8", inner="#F7C6A3", accent="#FF8FB0", nose="#5E3A2C",
                      pad="#F6D9B8", iris="#B07A55"),
        "snow": dict(main="#F9F9FF", light="#FFFFFF", inner="#DCE8FF", accent="#8EC5FF", nose="#6C5A7A",
                     pad="#E5EEFF", stitch="#C8D6F0", iris="#7FA7E0"),
    },
    sparkle=dict(accent="#FFCF5C"),
)


# ----------------------------------------------------------------------------- 3
def lavender_kitty():
    e = []
    e += rbox((5, 0, 6), (11, 5, 10.5), "fabric/main")
    e += box((6.25, 0.75, 5.6), (9.75, 4.25, 6), "fabric/light")
    e += sym(box((5.5, 0, 4.5), (7.25, 1.5, 6.25), "fabric/main", faces={"north": "paw"}))
    e += rbox((4, 5, 5), (12, 10.5, 10.5), "fabric/main", front="face")
    ear = box((4.5, 10.5, 7), (6.75, 11.5, 8.25), "fabric/main", faces={"north": "fabric/inner"})
    ear += box((5, 11.5, 7.25), (6.25, 12.5, 8), "fabric/main", faces={"north": "fabric/inner"})
    ear += box((5.4, 12.5, 7.4), (5.9, 13, 7.85), "fabric/main")
    e += sym(ear)
    e += box((10.25, 10.75, 6.6), (11, 11.5, 7.1), "satin/accent")
    e += box((9.25, 10.5, 6.75), (10.25, 11.75, 7.1), "satin/accent")
    e += box((11, 10.5, 6.75), (12, 11.75, 7.1), "satin/accent")
    e += box((7.5, 0.5, 10.25), (8.5, 1.5, 13), "fabric/main", part="tail")
    e += box((7.5, 1.5, 12), (8.5, 6, 13), "fabric/main", part="tail")
    e += box((7.5, 6, 11.25), (8.5, 7, 12.5), "fabric/light", part="tail")
    return e


LAVENDER_KITTY = dict(
    build=lavender_kitty,
    parts=[dict(name="tail", pivot=[8, 1, 10.5], axis="y", amplitude=12, speed=0.07, boost=2.5)],
    face=dict(eyes="sparkle", eye_y=0.42, eye_dx=0.5, mouth="w", mouth_y=0.74, nose=True, extras=("whiskers",)),
    variants={
        "lavender": dict(main="#D9C3F2", light="#F4EBFF", inner="#FFB7D0", accent="#FF8FBF", iris="#A57AD6"),
        "peach": dict(main="#FFD3B8", light="#FFF0E5", inner="#FFB2B2", accent="#7FD6C2", iris="#E39A6F"),
        "midnight": dict(main="#77739F", light="#B4B0DB", inner="#FFB7D0", accent="#FFB0D3", eye="#241B33",
                         iris="#FFE27A", stitch="#5E5A88"),
    },
    sparkle=dict(accent="#FFCF5C"),
)


# ----------------------------------------------------------------------------- 4
def cloud_puppy():
    e = []
    e += rbox((5, 0.5, 5.5), (11, 5.5, 11), "fluff/main", r=0.75)
    e += box((4.25, 1, 6.5), (11.75, 4.5, 10), "fluff/main")
    e += box((5.5, 0, 6), (10.5, 1, 10.5), "fluff/main")
    e += box((6.5, 1.25, 5.1), (9.5, 4.25, 5.6), "fabric/light", faces={"north": "belly"})
    e += sym(box((5.25, 0, 3.75), (7.25, 1.75, 6), "fluff/main", faces={"north": "paw"}))
    e += rbox((4, 5, 4.75), (12, 11, 10), "fluff/main", front="face")
    e += box((5, 11, 6), (11, 11.75, 9), "fluff/main")
    e += box((6.5, 5.5, 4), (9.5, 7.5, 4.75), "fabric/light", faces={"north": "snout"})
    e += sym(box((2.75, 7.25, 6), (4.25, 10.75, 8.5), "fabric/accent", rot=("z", -22.5, (4, 10.5, 7.25))))
    e += box((7.25, 3, 11), (8.75, 4.5, 13), "fluff/main", part="tail")
    e += box((7, 4, 12), (9, 6, 13.5), "fluff/main", part="tail")
    return e


CLOUD_PUPPY = dict(
    build=cloud_puppy,
    parts=[dict(name="tail", pivot=[8, 3.5, 11], axis="y", amplitude=24, speed=0.3, boost=1.5)],
    face=dict(eyes="sparkle", eye_y=0.36, eye_dx=0.5, blush_dy=1.0, nose_style="big", snout_mouth="open"),
    variants={
        "sky": dict(main="#FAFCFF", light="#FFFFFF", accent="#A9D4FF", inner="#FFC4D6", nose="#4A3B5C",
                    motif="cloud", motif_color="#B9DCFF", stitch="#CFE2F5", iris="#7FB6EA"),
        "sunset": dict(main="#FFF0E6", light="#FFFAF5", accent="#FFB49A", inner="#FFC4D6", nose="#5A3B45",
                       motif="sun", motif_color="#FFCB5C", stitch="#F5D3C3", iris="#F09A7A"),
        "storm": dict(main="#D9DDE8", light="#F2F4FA", accent="#8E9AB8", inner="#FFC4D6", nose="#3E3A52",
                      motif="bolt", motif_color="#FFE36E", stitch="#B5BDD0", iris="#6F7FA8"),
    },
    sparkle=dict(motif="rainbow"),
)


# ----------------------------------------------------------------------------- 5
def cherry_blossom_fox():
    e = []
    e += rbox((5, 0, 6), (11, 5, 10.5), "fabric/main")
    e += box((6, 0.75, 5.6), (10, 4.75, 6), "fabric/light")
    e += sym(box((5.5, 0, 4.5), (7.25, 1.5, 6.25), "fabric/dark", faces={"north": "paw/dark"}))
    e += rbox((4, 5, 5), (12, 10.5, 10.5), "fabric/main", front="face")
    e += sym(box((3.25, 5.5, 6), (4.25, 7.5, 8.5), "fabric/light"))
    e += box((6.5, 5.5, 4), (9.5, 7.5, 5), "fabric/light", faces={"north": "snout"})
    ear = box((4.25, 10.5, 7), (6.75, 12, 8.25), "fabric/main", faces={"north": "fabric/inner"})
    ear += box((4.75, 12, 7.25), (6.25, 13.5, 8), "fabric/main", faces={"north": "fabric/inner"})
    ear += box((5.1, 13.5, 7.4), (5.9, 14.25, 7.85), "fabric/dark")
    e += sym(ear)
    e += flower(9.75, 10.6, 6.6, 1.75)
    e += flower(11.0, 11.4, 6.8, 1.25)
    e += box((6.75, 1, 10.5), (9.25, 3.5, 13.5), "fabric/main", part="tail")
    e += box((7, 3.5, 12.5), (9, 6.5, 14.5), "fabric/main", part="tail")
    e += box((7.25, 6.5, 12.75), (8.75, 8, 14.25), "fabric/light", part="tail")
    return e


CHERRY_BLOSSOM_FOX = dict(
    build=cherry_blossom_fox,
    parts=[dict(name="tail", pivot=[8, 1.5, 10.5], axis="y", amplitude=14, speed=0.08, boost=2.0)],
    face=dict(eyes="sparkle", eye_y=0.4, eye_dx=0.5, blush_dy=0.8, nose_style="oval", snout_mouth="w"),
    variants={
        "sakura": dict(main="#F7AFC6", light="#FFF2F6", dark="#C9668C", inner="#FFE0EA", petal="#FFFFFF",
                       nose="#7A3550", iris="#D9729A"),
        "snow": dict(main="#FAF7FB", light="#FFFFFF", dark="#E5A3BD", inner="#FFE0EA", petal="#FF9DC0",
                     nose="#7A4A60", pad="#FFD7E5", iris="#E08DB0"),
        "twilight": dict(main="#C7A6E8", light="#F3EAFF", dark="#8C6BB8", inner="#FFE0F2", petal="#FFD0E6",
                         nose="#4B3366", iris="#9C7AD8"),
    },
    sparkle=dict(petal="#FFE59A"),
)


# ----------------------------------------------------------------------------- 6
def angel_bunny():
    e = []
    e += rbox((5, 0, 6), (11, 5, 10.5), "fabric/main")
    e += box((6, 0.75, 5.6), (10, 4.25, 6), "fabric/light", faces={"north": "belly"})
    e += sym(box((5.25, 0, 4.5), (7.25, 1.5, 6.5), "fabric/main", faces={"north": "paw"}))
    e += sym(box((4.25, 1.5, 7), (5.25, 4.5, 9), "fabric/main", rot=("z", -22.5, (4.75, 4.5, 8))))
    e += rbox((4, 5, 5), (12, 11, 10.5), "fabric/main", front="face")
    e += sym(box((3, 6.25, 6.75), (4, 10.75, 8.75), "fabric/main", faces={"north": "fabric/inner"},
                 rot=("z", -22.5, (4, 10.75, 7.75))))
    e += rbox((7, 1, 10.5), (9, 3, 11.75), "fabric/light", r=0.25)
    wing = box((1.5, 4.5, 10.5), (6, 9, 11), "feather/wing", part="wing_left")
    wing += box((0.5, 7, 10.5), (2, 10.25, 11), "feather/wing", part="wing_left")
    wing += box((2, 8.75, 10.5), (5.5, 9.75, 11), "feather/wing", part="wing_left")
    e += wing + mirrored(wing, part="wing_right")
    e += box((5.5, 12.25, 5.75), (10.5, 12.75, 6.25), "gold/gold", part="halo")
    e += box((5.5, 12.25, 9.75), (10.5, 12.75, 10.25), "gold/gold", part="halo")
    e += box((5.5, 12.25, 6.25), (6, 12.75, 9.75), "gold/gold", part="halo")
    e += box((10, 12.25, 6.25), (10.5, 12.75, 9.75), "gold/gold", part="halo")
    return e


ANGEL_BUNNY = dict(
    build=angel_bunny,
    parts=[
        dict(name="wing_left", pivot=[6, 6.75, 10.75], axis="y", amplitude=18, speed=0.12, boost=2.5),
        dict(name="wing_right", pivot=[10, 6.75, 10.75], axis="y", amplitude=-18, speed=0.12, boost=2.5),
        dict(name="halo", pivot=[8, 12.5, 8], bob_amplitude=0.6, bob_speed=0.1, spin=1.2),
    ],
    face=dict(eyes="sparkle", eye_y=0.45, eye_dx=0.46, mouth="small", mouth_y=0.76, nose=True),
    variants={
        "seraph": dict(main="#FFFDFB", light="#FFFFFF", inner="#FFD1DE", wing="#FFFFFF", motif="star",
                       motif_color="#FFD36E", stitch="#EADFE8", iris="#C99AD6"),
        "blush": dict(main="#FFE3EC", light="#FFF5F8", inner="#FFB9CE", wing="#FFF4F8", motif="heart",
                      accent="#FF8FB3", iris="#E58AAE"),
        "sky": dict(main="#E3F1FF", light="#F6FBFF", inner="#FFD1E2", wing="#F3F9FF", motif="star",
                    motif_color="#FFD36E", stitch="#C7DDF2", iris="#7FB0E5"),
    },
    sparkle=dict(main="#FFF8FC", wing="#FFF6E0"),
)


# ----------------------------------------------------------------------------- 7
def sleepy_panda():
    e = []
    e += rbox((4.5, 0, 5.5), (11.5, 5, 11), "fabric/main")
    e += box((4.45, 3.5, 5.45), (11.55, 5.05, 11.05), "fabric/dark")
    e += sym(box((4.5, 0, 3.5), (7, 2, 6), "fabric/dark", faces={"north": "paw/dark"}))
    e += sym(box((3.5, 1.5, 6), (5, 4.5, 8.5), "fabric/dark"))
    e += rbox((6, 1.25, 4.25), (10, 4.25, 5.75), "fabric/pillow", r=0.4, front="pillow_front")
    e += rbox((4, 5, 5), (12, 10.5, 10.5), "fabric/main", front="face")
    e += box((6.75, 5.5, 4.5), (9.25, 7, 5), "fabric/light", faces={"north": "snout"})
    e += sym(rbox((3.25, 9, 7), (5, 10.75, 8.5), "fabric/dark", r=0.35))
    e += box((4.9, 10.25, 5.9), (11.1, 11, 10.1), "lace")
    e += box((5.25, 11, 6.25), (10.75, 12, 9.75), "stripes/accent+cap_light")
    e += box((6, 12, 6.75), (10, 12.75, 9.25), "stripes/accent+cap_light")
    e += box((7, 12.75, 7.25), (9.75, 13.5, 8.75), "stripes/accent+cap_light")
    e += box((9.5, 12.75, 7.5), (11.25, 13.25, 8.5), "stripes/accent+cap_light")
    e += rbox((11, 12.25, 7.25), (12.5, 13.75, 8.75), "fluff/light", r=0.25)
    return e


SLEEPY_PANDA = dict(
    build=sleepy_panda, parts=[],
    face=dict(eyes="closed", eye_y=0.42, eye_dx=0.5, blush_dy=2.2, extras=("panda_patches",),
              nose_style="oval", snout_mouth="o"),
    variants={
        "classic": dict(main="#FBF8F6", light="#FFFFFF", dark="#4A4256", accent="#A9C8FF", cap_light="#E4EEFF",
                        pillow="#FFE1EC", nose="#3E3646", pad="#B49AB8"),
        "strawberry": dict(main="#FFF9FA", light="#FFFFFF", dark="#F497B4", accent="#FF8FA8", cap_light="#FFE3EA",
                           pillow="#FFF2C7", pillow_heart="#FF8FB3", nose="#8A4A62", pad="#FFD3E0"),
        "mint": dict(main="#FBFFFD", light="#FFFFFF", dark="#7FCDB5", accent="#C8B3F2", cap_light="#F0E8FF",
                     pillow="#E1F0FF", nose="#3F6B5E", pad="#C8EEDF"),
    },
    sparkle=dict(dark="#C9B6F0", accent="#FFD36E", cap_light="#FFF4D2"),
)


# ----------------------------------------------------------------------------- 8
def heart_bear():
    e = []
    e += rbox((4.5, 0, 6), (11.5, 5.5, 10.5), "fabric/main")
    e += sym(box((4.75, 0, 3.75), (7.25, 2, 6.5), "fabric/main", faces={"north": "paw"}))
    e += sym(box((4, 2.25, 5.25), (5.5, 4.75, 7.75), "fabric/main", rot=("y", -22.5, (4.75, 3.5, 6.5))))
    e += box((5.75, 3, 4.5), (7.75, 4.75, 5.5), "fabric/accent")
    e += box((8.25, 3, 4.5), (10.25, 4.75, 5.5), "fabric/accent")
    e += box((6.25, 2, 4.5), (9.75, 4, 5.5), "fabric/accent")
    e += box((6.9, 1.25, 4.5), (9.1, 2, 5.5), "fabric/accent")
    e += box((7.5, 0.75, 4.5), (8.5, 1.25, 5.5), "fabric/accent")
    e += box((6.25, 3.75, 4.4), (7, 4.25, 4.5), "fabric/accent_light")
    e += rbox((4, 5.5, 5), (12, 11.5, 10.5), "fabric/main", front="face")
    e += box((6.5, 6, 4.25), (9.5, 8, 5), "fabric/light", faces={"north": "snout"})
    e += sym(rbox((4, 10.5, 7), (6.25, 12.75, 8.5), "fabric/main", r=0.4, front="ear_heart"))
    e += box((7.25, 1, 10.5), (8.75, 2.5, 11.25), "fabric/main")
    return e


HEART_BEAR = dict(
    build=heart_bear, parts=[],
    face=dict(eyes="sparkle", eye_y=0.36, eye_dx=0.5, blush_dy=1.0, nose_style="heart", snout_mouth="small",
              extras=("heart_mark",)),
    variants={
        "rose": dict(main="#F7A9C4", light="#FFE0EA", inner="#FFC1D5", accent="#FF5C8F", accent_light="#FFB3CC",
                     nose="#D93D72", iris="#E0628F"),
        "ruby": dict(main="#F48A9E", light="#FFDDE3", inner="#FFC0CB", accent="#E8365F", accent_light="#FF9AB0",
                     nose="#B8244C", iris="#D9455F"),
        "lilac": dict(main="#CDB4EE", light="#F1E8FF", inner="#FFC6E0", accent="#FF7FB2", accent_light="#FFC2DB",
                      nose="#D9457F", iris="#A982DB"),
    },
    sparkle=dict(accent="#FFC94D", accent_light="#FFF0B8", nose="#E8A21C"),
)


# ----------------------------------------------------------------------------- 9
def little_duck():
    e = []
    e += rbox((4.5, 0, 5.5), (11.5, 5, 11.5), "fabric/main", r=0.75)
    e += box((7, 3, 11.25), (9, 4.5, 12.75), "fabric/main", rot=("x", 22.5, (8, 3, 11.5)))
    e += sym(box((3.75, 1.5, 6.75), (4.5, 4, 10), "feather/wing_color"))
    e += sym(box((5.25, 0, 3.75), (7.25, 0.5, 6), "fabric/beak"))
    e += rbox((5, 4.5, 5.5), (11, 10, 10.5), "fabric/main", front="face")
    e += box((6.5, 6, 4.25), (9.5, 7, 5.5), "fabric/beak")
    e += box((6.75, 5.5, 4.5), (9.25, 6, 5.5), "fabric/beak_dark")
    e += box((7.75, 10, 7), (8.25, 11.5, 8), "fabric/main")
    e += box((8.25, 11, 7), (9, 11.5, 8), "fabric/main")
    e += flower(9.25, 9.0, 5.25, 1.75)
    return e


LITTLE_DUCK = dict(
    build=little_duck, parts=[],
    face=dict(eyes="sparkle", eye_y=0.33, eye_dx=0.62, blush_dy=0.6, blush_out=0.5),
    variants={
        "sunny": dict(main="#FFE680", light="#FFF5C2", wing_color="#FFD95A", beak="#FFAA5C", beak_dark="#F08A3E",
                      petal="#FFFFFF", flower_center="#FF9FC0", iris="#9C7A3A"),
        "cream": dict(main="#FFF6DC", light="#FFFDF2", wing_color="#F5E6C4", beak="#FFB37A", beak_dark="#F09456",
                      petal="#FFB3CE", iris="#B08A6A"),
        "mint": dict(main="#C9F0DA", light="#EBFBF2", wing_color="#ABE3C5", beak="#FFB37A", beak_dark="#F09456",
                     petal="#FFD1E3", iris="#5FA387"),
    },
    sparkle=dict(beak="#FFC27A", wing_color="#FFF1B8"),
)


# ----------------------------------------------------------------------------- 10
def moon_cat():
    e = []
    e += rbox((4.5, 0, 6), (11.5, 4.5, 13), "starry/main")
    e += sym(box((5, 0, 2.5), (7, 1.25, 4.25), "fabric/main", faces={"north": "paw"}))
    e += rbox((4.25, 2.5, 3), (11.75, 8.5, 8.5), "fabric/main", front="face")
    ear = box((4.75, 8.5, 4.75), (6.75, 9.5, 6), "fabric/main", faces={"north": "fabric/inner"})
    ear += box((5.25, 9.5, 5), (6.5, 10.5, 5.75), "fabric/main", faces={"north": "fabric/inner"})
    e += sym(ear)
    e += box((11.5, 0, 8), (12.5, 1, 13), "starry/main")
    e += box((10, 0, 6.5), (12.5, 1, 8), "starry/main")
    e += box((9.75, 0, 5.75), (11, 1, 6.75), "fabric/light")
    e += box((1.75, 9, 7.6), (2.5, 11.5, 8.4), "gold/gold", part="moon")
    e += box((2.5, 8.5, 7.6), (3.5, 9.25, 8.4), "gold/gold", part="moon")
    e += box((2.5, 11.25, 7.6), (3.5, 12, 8.4), "gold/gold", part="moon")
    e += box((3.75, 12.25, 7.85), (4.25, 12.75, 8.15), "gold/gold", part="moon")
    return e


MOON_CAT = dict(
    build=moon_cat,
    parts=[dict(name="moon", pivot=[8, 10, 8], spin=1.5, bob_amplitude=0.7, bob_speed=0.08)],
    face=dict(eyes="star", eye_y=0.52, eye_dx=0.5, mouth="w", mouth_y=0.82, nose=True,
              extras=("moon_mark", "whiskers")),
    variants={
        "midnight": dict(main="#55528F", light="#8581C7", inner="#F5B8D5", gold="#FFE08A", eye="#1E1838",
                         iris="#FFE58A", star="#FFF4C2", blush="#F59AC4"),
        "dawn": dict(main="#F6C2D9", light="#FFE6F0", inner="#FFD9E8", gold="#F2F4FF", eye="#4A2B48",
                     iris="#FFFFFF", star="#FFFFFF", blush="#FF8FB3"),
        "dusk": dict(main="#A994D9", light="#D7CBF5", inner="#FFC6E0", gold="#FFE08A", eye="#2B1F45",
                     iris="#FFE58A", star="#FFF4C2"),
    },
    sparkle=dict(main="#8C86D8", gold="#FFE7A0", star="#FFFFFF"),
)


# ----------------------------------------------------------------------------- 11
def sugarplum_dragon():
    e = []
    e += rbox((4.5, 0, 6), (11.5, 6, 11), "scales/main")
    e += box((5.75, 0.5, 5.6), (10.25, 5.25, 6), "bands/light")
    e += sym(box((4.5, 0, 3.75), (7, 2, 6.25), "scales/main", faces={"north": "claw"}))
    e += sym(box((3.75, 2.25, 6.25), (4.75, 4.75, 8), "scales/main", rot=("z", -22.5, (4.25, 4.75, 7.1))))
    e += rbox((4, 6, 5), (12, 11.75, 10.5), "scales/main", front="face")
    e += box((6, 6.25, 3.75), (10, 8.25, 5), "fabric/light", faces={"north": "snout"})
    e += sym(box((5, 11.75, 8), (6, 13.25, 9), "fabric/horn") + box((5.25, 13.25, 8.25), (5.75, 14.25, 8.75), "fabric/horn"))
    e += sym(box((3.25, 8.75, 7), (4, 10.5, 9), "fabric/accent"))
    e += box((7.5, 11.75, 8.5), (8.5, 12.5, 9.5), "fabric/accent")
    e += box((7.5, 9.5, 10.5), (8.5, 10.5, 11.25), "fabric/accent")
    e += box((7.5, 5.5, 11), (8.5, 6.5, 11.75), "fabric/accent")
    e += box((7.5, 3, 11), (8.5, 4, 11.75), "fabric/accent")
    e += box((7, 0, 11), (9, 2, 14), "scales/main")
    e += box((7.25, 0, 14), (8.75, 1.5, 15.5), "scales/main")
    e += box((6.75, 0, 15.5), (9.25, 1, 16), "fabric/accent")
    e += box((7.6, 2, 12), (8.4, 2.75, 13), "fabric/accent")
    wing = box((1.5, 6, 10.75), (5, 10, 11.25), "membrane", part="wing_left")
    wing += box((1, 9.5, 10.65), (5.25, 10.25, 11.35), "fabric/accent", part="wing_left")
    wing += box((0.75, 7.5, 10.65), (1.5, 10.25, 11.35), "fabric/accent", part="wing_left")
    e += wing + mirrored(wing, part="wing_right")
    return e


SUGARPLUM_DRAGON = dict(
    build=sugarplum_dragon,
    parts=[
        dict(name="wing_left", pivot=[5, 8, 11], axis="y", amplitude=22, speed=0.1, boost=3.0),
        dict(name="wing_right", pivot=[11, 8, 11], axis="y", amplitude=-22, speed=0.1, boost=3.0),
    ],
    face=dict(eyes="sparkle", eye_y=0.38, eye_dx=0.5, blush_dy=0.8, nose_style="dots", snout_mouth="smile",
              fang=True),
    variants={
        "sugarplum": dict(main="#B98AE0", light="#F3D9FF", accent="#FF9FCF", membrane="#E7CCFF", horn="#FFF1C9",
                          iris="#9F6FD0"),
        "seafoam": dict(main="#8FD9C3", light="#E3FFF5", accent="#A6C8FF", membrane="#CFF5EA", horn="#FFF6DA",
                        iris="#4FA88F"),
        "rose_quartz": dict(main="#F4AFC8", light="#FFE6EF", accent="#C9A6F2", membrane="#FFD9E7", horn="#FFF1E0",
                            iris="#D9729A"),
    },
    sparkle=dict(accent="#FFD36E", horn="#FFE7A0"),
)


# ----------------------------------------------------------------------------- 12
def pastel_axolotl():
    e = []
    e += rbox((5.5, 0, 7), (10.5, 3.25, 13), "fabric/main")
    e += rbox((3.75, 0, 3), (12.25, 4.75, 8), "fabric/main", front="face")
    e += box((7.75, 3.25, 8), (8.25, 3.75, 12.5), "fabric/accent")
    e += sym(box((4.75, 0, 7.75), (5.5, 1, 9.25), "fabric/main"))
    e += sym(box((5, 0, 11), (5.75, 1, 12.5), "fabric/main"))
    e += box((7.25, 0.25, 13), (8.75, 2.75, 16), "fabric/main")
    e += box((7.75, 2.75, 12.5), (8.25, 3.5, 15.5), "fabric/accent")
    gill = box((2.25, 3.75, 5.5), (3.75, 4.5, 6.25), "frill/accent", part="gills_left")
    gill += box((1.75, 2.75, 5.5), (3.75, 3.5, 6.25), "frill/accent", part="gills_left")
    gill += box((2.25, 1.75, 5.5), (3.75, 2.5, 6.25), "frill/accent", part="gills_left")
    e += gill + mirrored(gill, part="gills_right")
    return e


PASTEL_AXOLOTL = dict(
    build=pastel_axolotl,
    parts=[
        dict(name="gills_left", pivot=[3.75, 3, 5.9], axis="y", amplitude=12, speed=0.12, boost=2.5),
        dict(name="gills_right", pivot=[12.25, 3, 5.9], axis="y", amplitude=-12, speed=0.12, boost=2.5, phase=1.2),
    ],
    face=dict(eyes="dot", eye_y=0.36, eye_dx=0.62, mouth="wide", mouth_y=0.62, blush_dy=0.8, blush_out=1.8),
    variants={
        "blossom": dict(main="#FFC4D9", light="#FFE6EF", accent="#FF7FAF", eye="#4A2140"),
        "mint": dict(main="#BFF0DE", light="#E6FFF5", accent="#6FD3B0", eye="#21483B", blush="#FF9FBF"),
        "lavender": dict(main="#DCCBFA", light="#F2EBFF", accent="#A88AE6", eye="#2E2148"),
    },
    sparkle=dict(accent="#FFC94D"),
)


# ----------------------------------------------------------------------------- 13
def baby_deer():
    e = []
    e += rbox((5, 0, 6), (11, 5, 11), "dots/main+spots")
    e += box((6.25, 0.75, 5.6), (9.75, 4.25, 6), "fabric/light")
    e += sym(box((5, 0, 3.75), (7, 1.5, 6.25), "dots/main+spots", faces={"north": "hoof"}))
    e += rbox((4.5, 5, 5.25), (11.5, 10.5, 10.5), "fabric/main", front="face")
    e += box((6.75, 5.5, 4.25), (9.25, 7.25, 5.25), "fabric/light", faces={"north": "snout"})
    e += sym(box((2.25, 8.5, 7), (4.5, 9.75, 8.25), "fabric/main", faces={"north": "fabric/inner"},
                 rot=("z", -22.5, (4.5, 9.1, 7.6))))
    e += sym(box((6, 10.5, 7.5), (6.75, 11.75, 8.25), "fabric/horn"))
    e += flower(5.0, 10.4, 5.1, 1.6)
    e += flower(7.2, 10.6, 4.95, 1.6)
    e += flower(9.4, 10.4, 5.1, 1.6)
    e += box((6.6, 10.4, 5.2), (7.3, 10.9, 5.5), "fabric/leaf")
    e += box((8.8, 10.4, 5.2), (9.5, 10.9, 5.5), "fabric/leaf")
    e += box((7.25, 2, 11), (8.75, 3.5, 11.75), "fabric/light")
    return e


BABY_DEER = dict(
    build=baby_deer, parts=[],
    face=dict(eyes="sparkle", eye_y=0.42, eye_dx=0.52, blush_dy=0.6, extras=("freckles",), nose_style="oval",
              snout_mouth="smile"),
    variants={
        "fawn": dict(main="#E3B184", light="#FFF0DF", spots="#FFF6EA", inner="#FFC9C9", hoof="#8B5E4A",
                     nose="#5A3A30", iris="#A8714A"),
        "blossom": dict(main="#F5BFCF", light="#FFF2F6", spots="#FFFFFF", inner="#FFD9E3", hoof="#C9849C",
                        nose="#8A4A62", petal="#FFF6B8", iris="#D97A9B"),
        "frost": dict(main="#D8E8F7", light="#F5FAFF", spots="#FFFFFF", inner="#FFD6E4", hoof="#8A9CC0",
                      nose="#4A5A7A", petal="#E8D9FF", iris="#7F9CC9"),
    },
    sparkle=dict(hoof="#E0B86A", petal="#FFE59A"),
)


# ----------------------------------------------------------------------------- 14
def honey_bee():
    e = []
    e += rbox((4, 0.5, 5.5), (12, 8, 12), "stripes/main+dark", r=0.75)
    e += rbox((4.5, 1, 3.75), (11.5, 7.5, 6), "fabric/main", front="face")
    e += box((7.5, 3.5, 12), (8.5, 4.5, 13), "fabric/dark")
    e += sym(box((6, 7.5, 4.75), (6.5, 10, 5.25), "fabric/dark", rot=("z", 22.5, (6.25, 7.5, 5)))
             + box((5.25, 9.75, 4.5), (6.5, 11, 5.5), "fabric/dark", rot=("z", 22.5, (6.25, 7.5, 5))))
    e += sym(box((5.5, 0, 4.5), (7, 0.75, 6), "fabric/dark"))
    e += sym(box((3.25, 2.5, 5), (4.25, 4.25, 6.5), "fabric/main"))
    wing = box((1.5, 7.5, 7.75), (6, 11.5, 8.25), "wing_clear", part="wing_left")
    e += wing + mirrored(wing, part="wing_right")
    return e


HONEY_BEE = dict(
    build=honey_bee,
    parts=[
        dict(name="wing_left", pivot=[6, 8, 8], axis="z", amplitude=16, speed=0.6, boost=1.5),
        dict(name="wing_right", pivot=[10, 8, 8], axis="z", amplitude=-16, speed=0.6, boost=1.5),
    ],
    face=dict(eyes="sparkle", eye_y=0.42, eye_dx=0.5, mouth="smile", mouth_y=0.72),
    variants={
        "honey": dict(main="#FFE27A", light="#FFF3B8", dark="#7A5A4A", wing="#F2FAFF", iris="#B08A3A"),
        "strawberry": dict(main="#FFC2D6", light="#FFE6EF", dark="#8A4B62", wing="#FFF5FA", wing_edge="#F0BFD3",
                           iris="#D9729A"),
        "mint": dict(main="#C3F2D9", light="#E8FFF2", dark="#5E7F72", wing="#F2FFFA", wing_edge="#B5E0CF",
                     iris="#5FA387"),
    },
    sparkle=dict(dark="#C99A5A", wing="#FFFBEA", wing_edge="#FFD36E"),
)


# ----------------------------------------------------------------------------- 15
def strawberry_cow():
    e = []
    e += rbox((4.5, 0, 6), (11.5, 5.5, 11), "spots/main+spot")
    e += box((6, 0.75, 5.6), (10, 4.5, 6), "fabric/light")
    e += sym(box((4.75, 0, 3.75), (7, 2, 6.25), "spots/main+spot", faces={"north": "hoof"}))
    e += sym(box((3.75, 2, 6.5), (4.75, 4.75, 8.5), "spots/main+spot", rot=("z", -22.5, (4.25, 4.75, 7.5))))
    e += rbox((4, 5.5, 5), (12, 11, 10.5), "fabric/main", front="face")
    e += box((5.75, 5.75, 4), (10.25, 8, 5), "fabric/muzzle", faces={"north": "nostrils"})
    e += sym(box((5, 11, 7.5), (5.75, 12.25, 8.25), "fabric/horn"))
    e += sym(box((2.5, 9, 7), (4, 10, 8.5), "fabric/main", faces={"north": "fabric/inner"},
                 rot=("z", -22.5, (4, 9.5, 7.75))))
    e += box((4.4, 5, 5.9), (11.6, 5.75, 11.1), "satin/accent")
    e += rbox((7.25, 3.9, 5.1), (8.75, 5.4, 5.9), "gold/gold", r=0.25)
    e += rbox((8.75, 11, 7.25), (10.75, 12.75, 9.25), "berry", r=0.35)
    e += box((8.5, 12.75, 7), (11, 13.2, 9.5), "fabric/leaf")
    e += box((9.5, 13.2, 8), (10, 13.8, 8.5), "fabric/leaf_dark")
    e += box((7.5, 1.25, 11), (8.5, 4.25, 11.6), "fabric/main", part="tail")
    e += box((7.25, 0.5, 11.1), (8.75, 1.5, 11.85), "fabric/spot", part="tail")
    return e


STRAWBERRY_COW = dict(
    build=strawberry_cow,
    parts=[dict(name="tail", pivot=[8, 4.25, 11.3], axis="z", amplitude=15, speed=0.08, boost=2.0)],
    face=dict(eyes="sparkle", eye_y=0.36, eye_dx=0.5, blush_dy=0.8, extras=("eye_spot",)),
    variants={
        "strawberry": dict(main="#FFD3E0", light="#FFF0F5", spot="#FF7FA2", spot_seed="#FFE48A", muzzle="#FFB8CB",
                           hoof="#E87D9C", accent="#FF5C8A", inner="#FFB3C9", iris="#E0628F"),
        "chocolate": dict(main="#EBCDB3", light="#FBEFE4", spot="#8A5A44", muzzle="#F6D9C4", hoof="#7A4E3A",
                          accent="#FF8FB3", inner="#F6C1C1", iris="#9A6A4A"),
        "blueberry": dict(main="#DAD6FA", light="#F2F0FF", spot="#7B83E0", spot_seed="#D6E4FF", muzzle="#EDE9FF",
                          hoof="#7A7FC0", accent="#8FB3FF", inner="#F6C9E6", berry="#6F7BDB", seed="#D6E4FF",
                          iris="#7C78D8"),
    },
    sparkle=dict(spot="#FFB3CB", spot_seed="#FFD36E", accent="#FFD36E"),
)


SPECIES = {
    "strawberry_bunny": STRAWBERRY_BUNNY,
    "pink_teddy_bear": PINK_TEDDY_BEAR,
    "lavender_kitty": LAVENDER_KITTY,
    "cloud_puppy": CLOUD_PUPPY,
    "cherry_blossom_fox": CHERRY_BLOSSOM_FOX,
    "angel_bunny": ANGEL_BUNNY,
    "sleepy_panda": SLEEPY_PANDA,
    "heart_bear": HEART_BEAR,
    "little_duck": LITTLE_DUCK,
    "moon_cat": MOON_CAT,
    "sugarplum_dragon": SUGARPLUM_DRAGON,
    "pastel_axolotl": PASTEL_AXOLOTL,
    "baby_deer": BABY_DEER,
    "honey_bee": HONEY_BEE,
    "strawberry_cow": STRAWBERRY_COW,
}


def variant_palettes(spec):
    """Ordered (variant_id, palette) pairs, Sparkle Edition last."""
    out = list(spec["variants"].items())
    first = out[0][1]
    out.append(("sparkle", sparkle(first, **spec.get("sparkle", {}))))
    return out
