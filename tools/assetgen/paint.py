"""Texture painters: plush fabrics, patterns and the embroidered decals (faces,
snouts, paw pads, belly patches, flowers...)."""
import math
import random

from .core import C, Img, clamp8, darken, hue_shift, lighten, mix, pastel_hue, scale

WHITE = (255, 255, 255, 255)

# Fallbacks for palette keys a species might not define.
FALLBACK = {
    "light": lambda p: lighten(p["main"], 0.55),
    "inner": lambda p: "#FFB3C9",
    "accent": lambda p: "#FF8FBF",
    "dark": lambda p: darken(p["main"], 0.35),
    "eye": lambda p: "#3B2440",
    "iris": lambda p: mix(col(p, "eye"), "#C77DB5", 0.55),
    "blush": lambda p: "#FF8FB3",
    "nose": lambda p: "#6E3A55",
    "pad": lambda p: mix(col(p, "inner"), WHITE, 0.25),
    "stitch": lambda p: darken(col(p, "light"), 0.18),
    "tongue": lambda p: "#FF8FAF",
    "gold": lambda p: "#FFD36E",
    "horn": lambda p: "#FFF1C9",
    "leaf": lambda p: "#86D08A",
    "leaf_dark": lambda p: "#5FAE6B",
    "petal": lambda p: "#FFE1EE",
    "flower_center": lambda p: "#FFCD5A",
    "wing": lambda p: "#FFFFFF",
    "membrane": lambda p: lighten(p["main"], 0.45),
    "hoof": lambda p: darken(p["main"], 0.4),
    "spots": lambda p: "#FFF6EC",
    "spot": lambda p: darken(p["main"], 0.3),
    "beak": lambda p: "#FFAD5C",
    "beak_dark": lambda p: "#F08C42",
    "pillow": lambda p: "#FFE1EC",
    "muzzle": lambda p: lighten(p["main"], 0.4),
    "berry": lambda p: "#F0506E",
    "seed": lambda p: "#FFE48A",
    "star": lambda p: "#FFF4C2",
    "wing_edge": lambda p: "#BFD7EE",
}


def col(pal, key):
    if key in pal:
        return C(pal[key])
    if key in FALLBACK:
        return C(FALLBACK[key](pal))
    raise KeyError(key)


def rng_for(*parts):
    return random.Random("|".join(str(p) for p in parts))


# --------------------------------------------------------------------------- base fills

def fill_fabric(img, x0, y0, w, h, base, rng, pal=None, soft=1.0):
    """Minky plush: low-contrast noise with a faint knit rib."""
    metallic = pal is not None and pal.get("metallic")
    rainbow = pal is not None and pal.get("rainbow")
    for y in range(h):
        for x in range(w):
            c = base
            if rainbow:
                c = pastel_hue((y * 0.045 + x * 0.012) % 1.0, 0.36, 1.0)
            if metallic:
                t = y / max(1, h - 1)
                c = mix(lighten(base, 0.35), darken(base, 0.18), t)
                if (x + y) % 9 in (0, 1):
                    c = lighten(c, 0.25)
            n = (rng.random() - 0.5) * 0.07 * soft
            rib = 0.012 if x % 2 == 0 else -0.008
            img.set(x0 + x, y0 + y, scale(c, 1.0 + n + rib))


def fill_satin(img, x0, y0, w, h, base, rng):
    for y in range(h):
        for x in range(w):
            band = (x + y) % 8
            f = 1.12 if band in (0, 1) else (1.05 if band == 2 else 0.97)
            img.set(x0 + x, y0 + y, scale(base, f + (rng.random() - 0.5) * 0.03))


def fill_gold(img, x0, y0, w, h, base, rng):
    for y in range(h):
        t = y / max(1, h - 1)
        for x in range(w):
            c = mix(lighten(base, 0.4), darken(base, 0.15), t)
            if (x + y) % 7 == 0:
                c = lighten(c, 0.45)
            img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.04))


# --------------------------------------------------------------------------- patterns

def p_fabric(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, args[0] if args else "main"), rng, pal)


def p_satin(img, x0, y0, w, h, pal, rng, args):
    fill_satin(img, x0, y0, w, h, col(pal, args[0] if args else "accent"), rng)


def p_gold(img, x0, y0, w, h, pal, rng, args):
    fill_gold(img, x0, y0, w, h, col(pal, args[0] if args else "gold"), rng)


def p_berry(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, "berry"), rng, pal)
    seed = col(pal, "seed")
    for y in range(1, h - 1, 4):
        offset = 2 if (y // 4) % 2 else 0
        for x in range(offset + 1, w, 4):
            img.set(x0 + x, y0 + y, seed)
            img.blend(x0 + x, y0 + y + 1, darken(col(pal, "berry"), 0.2), 0.6)


def p_stripes(img, x0, y0, w, h, pal, rng, args):
    a = col(pal, args[0] if args else "main")
    b = col(pal, args[1] if len(args) > 1 else "dark")
    period = 6
    for y in range(h):
        base = a if (y // period) % 2 == 0 else b
        for x in range(w):
            img.set(x0 + x, y0 + y, scale(base, 1.0 + (rng.random() - 0.5) * 0.06 + (0.01 if x % 2 else -0.01)))


def p_spots(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, args[0] if args else "main"), rng, pal)
    spot = col(pal, args[1] if len(args) > 1 else "spot")
    seed = pal.get("spot_seed")
    blobs = [(rng.uniform(0, w), rng.uniform(0, h), rng.uniform(2.2, 4.2)) for _ in range(max(2, w * h // 110))]
    for y in range(h):
        for x in range(w):
            for bx, by, r in blobs:
                wobble = 0.8 * math.sin(x * 1.3 + by) + 0.6 * math.cos(y * 1.1 + bx)
                if (x - bx) ** 2 + (y - by) ** 2 < (r + wobble * 0.5) ** 2:
                    c = spot
                    if seed and (x + 2 * y) % 5 == 0:
                        c = C(seed)
                    img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.05))
                    break


def p_dots(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, args[0] if args else "main"), rng, pal)
    dot = col(pal, args[1] if len(args) > 1 else "spots")
    for y in range(1, h - 1, 5):
        offset = 2 if (y // 5) % 2 else 0
        for x in range(offset + 1, w - 1, 5):
            jx = x + rng.randint(-1, 1)
            img.set(x0 + jx, y0 + y, dot)
            img.set(x0 + jx + 1, y0 + y, dot)
            img.set(x0 + jx, y0 + y + 1, dot)
            img.set(x0 + jx + 1, y0 + y + 1, scale(dot, 0.95))


def p_starry(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, args[0] if args else "main"), rng, pal)
    star = col(pal, "star")
    for _ in range(max(2, w * h // 40)):
        x, y = rng.randrange(w), rng.randrange(h)
        img.set(x0 + x, y0 + y, star)
        if rng.random() < 0.3 and 0 < x < w - 1 and 0 < y < h - 1:
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                img.blend(x0 + x + dx, y0 + y + dy, star, 0.55)


def p_feather(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "wing")
    shade = mix(base, "#C9C3E8", 0.35)
    tip = col(pal, "gold") if pal.get("sparkle") else lighten(base, 0.5)
    for y in range(h):
        row = y // 4
        for x in range(w):
            lx = (x + (row % 2) * 2) % 4
            ly = y % 4
            c = base
            if ly == 3 or (lx == 0 and ly >= 2):
                c = shade
            elif ly == 0 and lx in (1, 2):
                c = tip
            img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.04))


def p_membrane(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, "membrane")
    vein = darken(col(pal, "accent"), 0.05)
    for y in range(h):
        for x in range(w):
            c = base
            if (x + y // 2) % 6 == 0:
                c = mix(base, vein, 0.7)
            img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.05))


def p_scales(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "main")
    for y in range(h):
        row = y // 3
        for x in range(w):
            f = 1.0
            if (x + (row % 2) * 2) % 4 == 0 or y % 3 == 2:
                f = 0.95
            img.set(x0 + x, y0 + y, scale(base, f + (rng.random() - 0.5) * 0.05))


def p_bands(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "light")
    for y in range(h):
        for x in range(w):
            f = 0.9 if y % 4 == 3 else 1.0
            img.set(x0 + x, y0 + y, scale(base, f + (rng.random() - 0.5) * 0.04))


def p_fluff(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "main")
    for y in range(h):
        for x in range(w):
            f = 1.0 + 0.035 * math.sin(x * 0.9) * math.cos(y * 0.8) + (rng.random() - 0.5) * 0.06
            img.set(x0 + x, y0 + y, scale(base, f - (0.03 if (x // 3 + y // 3) % 2 else 0)))


def p_frill(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "accent")
    for y in range(h):
        for x in range(w):
            c = lighten(base, 0.25) if x % 3 == 0 else (base if x % 3 == 1 else darken(base, 0.06))
            img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.04))


def p_wing_clear(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, "wing")
    edge = col(pal, "wing_edge")
    for y in range(h):
        for x in range(w):
            c = base
            if x == 0 or y == 0 or x == w - 1 or y == h - 1:
                c = edge
            elif (x + y) % 5 == 0:
                c = mix(base, edge, 0.5)
            img.set(x0 + x, y0 + y, c)


def p_lace(img, x0, y0, w, h, pal, rng, args):
    base = C(pal.get("lace", "#FFFFFF"))
    for y in range(h):
        for x in range(w):
            if x % 4 == 1 and y % 4 == 1:
                img.set(x0 + x, y0 + y, (0, 0, 0, 0))
            else:
                img.set(x0 + x, y0 + y, scale(base, 0.97 if (x + y) % 2 else 1.0))


def p_solid(img, x0, y0, w, h, pal, rng, args):
    fill_fabric(img, x0, y0, w, h, col(pal, args[0]), rng, pal, soft=0.4)


# Decor patterns --------------------------------------------------------------

def p_wood(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "wood")
    for y in range(h):
        plank = y // 8
        seam_x = (plank * 13) % 24
        for x in range(w):
            grain = 0.04 * math.sin((x * 0.35) + plank * 2.1 + math.sin(y * 1.7) * 0.8)
            f = 1.0 + grain + (rng.random() - 0.5) * 0.04
            if y % 8 == 7 or (x - seam_x) % 24 == 0:
                f -= 0.12
            img.set(x0 + x, y0 + y, scale(base, f))


def p_painted(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0])
    for y in range(h):
        for x in range(w):
            img.set(x0 + x, y0 + y, scale(base, 1.0 + (rng.random() - 0.5) * 0.025))


def p_velvet(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0])
    for y in range(h):
        for x in range(w):
            f = 1.0 + 0.05 * math.sin(y * 0.5 + x * 0.15) + (rng.random() - 0.5) * 0.05
            img.set(x0 + x, y0 + y, scale(base, f))


def p_glass(img, x0, y0, w, h, pal, rng, args):
    for y in range(h):
        for x in range(w):
            a = 55
            c = (236, 246, 255)
            if x == 0 or y == 0 or x == w - 1 or y == h - 1:
                a = 140
            elif (x - y) % 23 in (0, 1) or (x - y) % 23 == 4:
                a = 150
                c = (255, 255, 255)
            img.set(x0 + x, y0 + y, (c[0], c[1], c[2], a))


def p_wallpaper(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "wallpaper")
    heart = col(pal, "accent")
    for y in range(h):
        for x in range(w):
            c = lighten(base, 0.12) if x % 8 < 4 else base
            img.set(x0 + x, y0 + y, c)
    for y in range(3, h - 3, 10):
        for x in range(2 + (y // 10 % 2) * 4, w - 3, 8):
            img.stamp(x0 + x, y0 + y, [".#.#.", "#####", ".###.", "..#.."][:3], {"#": mix(heart, base, 0.35)})


def p_metal(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "metal")
    for y in range(h):
        for x in range(w):
            f = 1.08 - 0.18 * (y / max(1, h - 1)) + (0.1 if (x + y) % 6 == 0 else 0)
            img.set(x0 + x, y0 + y, scale(base, f))


def p_marble(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "marble")
    vein = darken(base, 0.15)
    for y in range(h):
        for x in range(w):
            v = math.sin(x * 0.4 + y * 0.9 + math.sin(y * 0.3) * 2)
            c = mix(base, vein, 0.6) if abs(v) < 0.08 else base
            img.set(x0 + x, y0 + y, scale(c, 1.0 + (rng.random() - 0.5) * 0.03))


def p_polka(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "box")
    dot = col(pal, args[1] if len(args) > 1 else "dot")
    for y in range(h):
        for x in range(w):
            img.set(x0 + x, y0 + y, scale(base, 1.0 + (rng.random() - 0.5) * 0.025))
    for y in range(2, h - 1, 8):
        for x in range(2 + (y // 8 % 2) * 4, w - 1, 8):
            img.ellipse(x0 + x + 1, y0 + y + 1, 1.3, 1.3, dot)


def p_spool(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "thread")
    for y in range(h):
        for x in range(w):
            img.set(x0 + x, y0 + y, scale(base, 0.88 if y % 2 else 1.04))


def p_cushion(img, x0, y0, w, h, pal, rng, args):
    base = col(pal, args[0] if args else "cushion")
    for y in range(h):
        for x in range(w):
            f = 1.0 + (0.05 if x % 6 == 0 else 0) + (rng.random() - 0.5) * 0.04
            img.set(x0 + x, y0 + y, scale(base, f))


PATTERNS = {
    "fabric": p_fabric, "satin": p_satin, "gold": p_gold, "berry": p_berry, "stripes": p_stripes,
    "spots": p_spots, "dots": p_dots, "starry": p_starry, "feather": p_feather, "membrane": p_membrane,
    "scales": p_scales, "bands": p_bands, "fluff": p_fluff, "frill": p_frill, "wing_clear": p_wing_clear,
    "lace": p_lace, "solid": p_solid, "wood": p_wood, "painted": p_painted, "velvet": p_velvet,
    "glass": p_glass, "wallpaper": p_wallpaper, "metal": p_metal, "marble": p_marble, "polka": p_polka,
    "spool": p_spool, "cushion": p_cushion,
}

# --------------------------------------------------------------------------- face sprites

EYES = {
    "sparkle_big": [
        ".###.",
        "#WW##",
        "#WW##",
        "####w",
        "#iii#",
        ".###.",
    ],
    "sparkle": [
        ".##.",
        "#W##",
        "###w",
        "#ii#",
        ".##.",
    ],
    "sparkle_small": [
        "#W#",
        "###",
        "#i#",
    ],
    "star_big": [
        ".###.",
        "##S##",
        "#SSS#",
        "##S#w",
        "#iii#",
        ".###.",
    ],
    "star": [
        ".##.",
        "#S##",
        "SSS#",
        "#S#w",
        ".##.",
    ],
    "closed": [
        "#...#",
        ".###.",
    ],
    "closed_light": [
        "L...L",
        ".LLL.",
    ],
    "happy": [
        ".###.",
        "#...#",
    ],
    "dot": [
        "##",
        "#W",
    ],
}

MOUTHS = {
    "w": ["m.m.m", ".m.m."],
    "small": ["m.m", ".m."],
    "smile": ["m...m", ".mmm."],
    "wide": ["m.....m", ".mmmmm."],
    "o": [".mm.", "mppm", ".mm."],
    "open": ["mmmmm", "mpppm", ".mmm."],
    "none": [],
}

HEART5 = [".#.#.", "#####", "#####", ".###.", "..#.."]
HEART3 = ["#.#", "###", ".#."]
HEART7 = [
    ".##.##.",
    "#######",
    "#######",
    ".#####.",
    "..###..",
    "...#...",
]
STAR5 = ["..#..", ".###.", "#####", ".###.", "#...#"]
MOON5 = [".###.", "##...", "#....", "##...", ".###."]
BOLT = ["..##", ".##.", "####", ".##.", "##.."]
CLOUD = [".##..", "#####", "#####"]
SUN = ["#.#.#", ".###.", "#####", ".###.", "#.#.#"]


def pick_eye(style, h):
    if style == "sparkle":
        return EYES["sparkle_big"] if h >= 18 else (EYES["sparkle"] if h >= 13 else EYES["sparkle_small"])
    if style == "star":
        return EYES["star_big"] if h >= 18 else EYES["star"]
    return EYES[style]


def paint_face(img, x0, y0, w, h, pal, spec, rng):
    base = col(pal, spec.get("base", "main"))
    fill_fabric(img, x0, y0, w, h, base, rng, pal)
    extras = spec.get("extras", ())
    eye_col = col(pal, "eye")
    colors = {
        "#": eye_col, "W": WHITE, "w": (255, 255, 255, 210), "i": col(pal, "iris"),
        "S": col(pal, "gold") if "gold" in pal else C("#FFE58A"), "L": lighten(col(pal, "light"), 0.4),
        "m": mix(eye_col, col(pal, "blush"), 0.25), "p": col(pal, "tongue"),
    }

    if "muzzle" in extras:
        img.ellipse(x0 + w / 2, y0 + h * spec.get("mouth_y", 0.75), w * 0.2, h * 0.17, lighten(base, 0.35))

    sprite = pick_eye(spec.get("eyes", "sparkle"), h)
    if spec.get("eyes") == "closed" and "panda_patches" in extras:
        sprite = EYES["closed_light"]
    ew, eh = len(sprite[0]), len(sprite)
    cy = y0 + spec.get("eye_y", 0.45) * h
    eye_dx = spec.get("eye_dx", 0.42)
    eye_centers = []
    for side in (-1, 1):
        cx = x0 + w / 2 + side * eye_dx * w / 2
        eye_centers.append(cx)
        if "panda_patches" in extras:
            img.ellipse(cx - side * 0.3, cy + 0.6, ew / 2 + 1.6, eh / 2 + 2.6, col(pal, "dark"))
        if "eye_spot" in extras and side == -1:
            img.ellipse(cx - 1, cy, ew / 2 + 2.8, eh / 2 + 2.6, col(pal, "spot"))
        img.stamp(int(round(cx - ew / 2)), int(round(cy - eh / 2)), sprite, colors)

    if spec.get("blush", True):
        blush = col(pal, "blush")
        bw = max(2.0, w * 0.085)
        for side, cx in zip((-1, 1), eye_centers):
            bx = cx + side * spec.get("blush_out", 1.5)
            by = cy + eh / 2 + spec.get("blush_dy", 1.6)
            img.ellipse(bx, by, bw, 1.25, blush, 0.75)
            if h >= 16:
                for k in range(3):
                    img.blend(int(bx - 1.5 + k * 1.5), int(by - 0.5), darken(blush, 0.12), 0.6)

    mouth = MOUTHS[spec.get("mouth", "none")]
    my = y0 + spec.get("mouth_y", spec.get("eye_y", 0.45) + 0.28) * h
    if mouth:
        mw = len(mouth[0])
        img.stamp(int(round(x0 + w / 2 - mw / 2)), int(round(my)), mouth, colors)
    if spec.get("nose"):
        nose = col(pal, "nose_face") if "nose_face" in pal else mix(col(pal, "blush"), "#E05A8A", 0.4)
        img.set(int(x0 + w / 2) - 1, int(round(my)) - 1, nose)
        img.set(int(x0 + w / 2), int(round(my)) - 1, nose)

    if "whiskers" in extras:
        wc = mix(eye_col, base, 0.45)
        for side, cx in zip((-1, 1), eye_centers):
            wx = int(round(x0 + w / 2 + side * w * 0.36))
            wy = int(round(my))
            for k in (-1, 1):
                for j in range(3):
                    img.blend(wx + side * j, wy + k + (j if k > 0 else -j) // 2, wc, 0.7)
    if "moon_mark" in extras:
        img.stamp(int(x0 + w / 2 - 2), int(y0 + 1), MOON5, {"#": col(pal, "gold")})
    if "freckles" in extras:
        fc = lighten(base, 0.45)
        for cx in eye_centers:
            for dx, dy in ((-1, 0), (1, 1), (2, -0)):
                img.set(int(cx + dx), int(cy + eh / 2 + 3 + dy), fc)
    if "heart_mark" in extras:
        img.stamp(int(x0 + w / 2 - 1), int(y0 + 1), HEART3, {"#": col(pal, "accent")})


def paint_snout(img, x0, y0, w, h, pal, spec, rng):
    base = col(pal, spec.get("snout_base", "light"))
    fill_fabric(img, x0, y0, w, h, base, rng, pal, soft=0.7)
    nose = col(pal, "nose")
    style = spec.get("nose_style", "oval")
    cx = x0 + w / 2
    top = y0 + 1
    bottom = top
    if style in ("oval", "big"):
        nw = max(3.0, w * (0.24 if style == "oval" else 0.3))
        nh = max(1.5, h * (0.16 if style == "oval" else 0.22))
        img.ellipse(cx, top + nh, nw, nh, nose)
        img.set(int(cx - nw / 2), int(top + nh * 0.6), lighten(nose, 0.55))
        bottom = int(top + nh * 2)
    elif style == "heart":
        img.stamp(int(cx - 2.5), int(top), HEART5, {"#": nose})
        img.set(int(cx - 2), int(top + 1), lighten(nose, 0.5))
        bottom = top + 5
    elif style == "dots":
        for side in (-1, 1):
            img.ellipse(cx + side * w * 0.2, top + 1.5, 1.2, 1.0, darken(base, 0.35))
        bottom = top + 3
    mouth_style = spec.get("snout_mouth", "w")
    mouth = MOUTHS[mouth_style]
    colors = {"m": mix(nose, base, 0.15), "p": col(pal, "tongue")}
    if mouth:
        mw = len(mouth[0])
        if mouth_style == "w" and style in ("oval", "big", "heart"):
            img.set(int(cx), bottom, colors["m"])
            bottom += 1
        img.stamp(int(round(cx - mw / 2)), int(bottom), mouth, colors)
        if spec.get("fang"):
            img.set(int(round(cx - mw / 2)) + 1, int(bottom) + 1, WHITE)


def paint_nostrils(img, x0, y0, w, h, pal, spec, rng):
    base = col(pal, "muzzle")
    fill_fabric(img, x0, y0, w, h, base, rng, pal, soft=0.7)
    hole = darken(base, 0.28)
    cx = x0 + w / 2
    for side in (-1, 1):
        img.ellipse(cx + side * w * 0.22, y0 + h * 0.32, 1.6, 1.2, hole)
    img.stamp(int(cx - 2.5), int(y0 + h * 0.62), MOUTHS["smile"], {"m": darken(base, 0.35)})


def paint_paw(img, x0, y0, w, h, pal, args, rng):
    base = col(pal, args[0] if args else "main")
    fill_fabric(img, x0, y0, w, h, base, rng, pal)
    pad = col(pal, "pad")
    cx = x0 + w / 2
    img.ellipse(cx, y0 + h * 0.68, w * 0.27, h * 0.24, pad)
    for k in (-1, 0, 1):
        img.set(int(cx + k * 2 - 0.5), int(y0 + h * 0.3), pad)


def paint_hoof(img, x0, y0, w, h, pal, args, rng):
    fill_fabric(img, x0, y0, w, h, col(pal, "main"), rng, pal)
    hoof = col(pal, "hoof")
    top = int(y0 + h * 0.58)
    img.rect(x0, top, x0 + w, y0 + h, hoof)
    for y in range(top, y0 + h):
        img.set(int(x0 + w / 2), y, darken(hoof, 0.2))


def paint_claw(img, x0, y0, w, h, pal, args, rng):
    fill_fabric(img, x0, y0, w, h, col(pal, "main"), rng, pal)
    claw = col(pal, "horn")
    for k in (-1, 0, 1):
        x = int(x0 + w / 2 + k * 2)
        img.set(x, y0 + h - 1, claw)
        img.set(x, y0 + h - 2, claw)


def paint_belly(img, x0, y0, w, h, pal, args, rng):
    base = col(pal, "light")
    fill_fabric(img, x0, y0, w, h, base, rng, pal)
    stitch = col(pal, "stitch")
    for x in range(1, w - 1):
        if x % 2 == 0:
            img.set(x0 + x, y0 + 1, stitch)
            img.set(x0 + x, y0 + h - 2, stitch)
    for y in range(1, h - 1):
        if y % 2 == 0:
            img.set(x0 + 1, y0 + y, stitch)
            img.set(x0 + w - 2, y0 + y, stitch)
    motif = pal.get("motif")
    sprites = {"heart": HEART7 if w >= 12 else HEART5, "star": STAR5, "bolt": BOLT, "cloud": CLOUD, "sun": SUN, "moon": MOON5}
    if motif == "rainbow":
        for i, hue in enumerate((0.95, 0.08, 0.16, 0.33, 0.55, 0.75)):
            r = 4.5 - i * 0.6
            for a in range(0, 180, 6):
                px = x0 + w / 2 + math.cos(math.radians(a)) * r
                py = y0 + h * 0.62 - math.sin(math.radians(a)) * r * 0.8
                img.set(int(px), int(py), pastel_hue(hue, 0.45))
    elif motif in sprites:
        sp = sprites[motif]
        color = col(pal, "motif_color") if "motif_color" in pal else col(pal, "accent")
        img.stamp(int(x0 + w / 2 - len(sp[0]) / 2), int(y0 + h / 2 - len(sp) / 2), sp, {"#": color})


def paint_flower(img, x0, y0, w, h, pal, args, rng):
    petal = col(pal, "petal")
    center = col(pal, "flower_center")
    cx, cy = w / 2, h / 2
    radius = min(w, h) / 2
    for y in range(h):
        for x in range(w):
            dx, dy = x + 0.5 - cx, y + 0.5 - cy
            d = math.hypot(dx, dy)
            ang = math.atan2(dy, dx) + math.pi / 2
            limit = radius * (0.62 + 0.38 * abs(math.cos(2.5 * ang)))
            if d <= radius * 0.32:
                img.set(x0 + x, y0 + y, center)
            elif d <= limit:
                img.set(x0 + x, y0 + y, darken(petal, 0.08) if d > limit - 0.8 else petal)


def paint_ear_heart(img, x0, y0, w, h, pal, args, rng):
    fill_fabric(img, x0, y0, w, h, col(pal, "inner"), rng, pal)
    img.stamp(int(x0 + w / 2 - 1.5), int(y0 + h / 2 - 1.5), HEART3, {"#": col(pal, "accent")})


def paint_pillow_front(img, x0, y0, w, h, pal, args, rng):
    base = col(pal, "pillow")
    fill_fabric(img, x0, y0, w, h, base, rng, pal)
    for x in range(w):
        if x % 2 == 0:
            img.set(x0 + x, y0, WHITE)
            img.set(x0 + x, y0 + h - 1, WHITE)
    for y in range(h):
        if y % 2 == 0:
            img.set(x0, y0 + y, WHITE)
            img.set(x0 + w - 1, y0 + y, WHITE)
    sp = HEART5 if min(w, h) >= 9 else HEART3
    img.stamp(int(x0 + w / 2 - len(sp[0]) / 2), int(y0 + h / 2 - len(sp) / 2), sp, {"#": col(pal, "pillow_heart") if "pillow_heart" in pal else col(pal, "blush")})


def paint_drawer(img, x0, y0, w, h, pal, args, rng):
    base = col(pal, "wood")
    p_wood(img, x0, y0, w, h, pal, rng, ["wood"])
    trim = darken(base, 0.22)
    half = h // 2
    for top in (0, half):
        for x in range(1, w - 1):
            img.set(x0 + x, y0 + top + 1, trim)
            img.set(x0 + x, y0 + top + half - 2, trim)
        for y in range(top + 1, top + half - 1):
            img.set(x0 + 1, y0 + y, trim)
            img.set(x0 + w - 2, y0 + y, trim)
        img.stamp(int(x0 + w / 2 - 1.5), int(y0 + top + half / 2 - 1.5), HEART3, {"#": col(pal, "accent")})


def paint_plaque(img, x0, y0, w, h, pal, args, rng):
    fill_gold(img, x0, y0, w, h, col(pal, "gold"), rng)
    img.stamp(int(x0 + w / 2 - 2.5), int(y0 + h / 2 - 2.5), STAR5, {"#": darken(col(pal, "gold"), 0.25)})


def paint_heart_cushion(img, x0, y0, w, h, pal, args, rng):
    base = col(pal, "cushion")
    p_cushion(img, x0, y0, w, h, pal, rng, ["cushion"])
    sp = HEART7 if min(w, h) >= 12 else HEART5
    img.stamp(int(x0 + w / 2 - len(sp[0]) / 2), int(y0 + h / 2 - len(sp) / 2), sp, {"#": darken(base, 0.12)})


DECALS = {
    "face": None, "snout": None, "nostrils": None,
    "paw": paint_paw, "hoof": paint_hoof, "claw": paint_claw, "belly": paint_belly, "flower": paint_flower,
    "ear_heart": paint_ear_heart, "pillow_front": paint_pillow_front, "drawer": paint_drawer,
    "plaque": paint_plaque, "heart_cushion": paint_heart_cushion,
}

NO_GLITTER = {"face", "snout", "nostrils", "paw", "hoof", "claw", "flower"}


def paint_tile(img, mat, rect, pal, face_spec, seed):
    x, y, w, h = rect
    name, _, arg = mat.partition("/")
    args = arg.split("+") if arg else []
    rng = rng_for(seed, mat)
    if name == "face":
        paint_face(img, x, y, w, h, pal, face_spec, rng)
    elif name == "snout":
        paint_snout(img, x, y, w, h, pal, face_spec, rng)
    elif name == "nostrils":
        paint_nostrils(img, x, y, w, h, pal, face_spec, rng)
    elif name in DECALS:
        DECALS[name](img, x, y, w, h, pal, args, rng)
    else:
        PATTERNS[name](img, x, y, w, h, pal, rng, args)
    if pal.get("sparkle") and name not in NO_GLITTER:
        shimmer(img, rect, rng)


def shimmer(img, rect, rng):
    """Sparkle Edition finish: pearly hue drift plus scattered glitter."""
    x0, y0, w, h = rect
    for y in range(h):
        for x in range(w):
            c = img.get(x0 + x, y0 + y)
            if c[3] == 0:
                continue
            c = hue_shift(c, 0.035 * math.sin((x + y) * 0.35), 0.0, 0.0)
            r = rng.random()
            if r < 0.025:
                c = (255, 255, 255, c[3])
            elif r < 0.04:
                c = (255, 225, 140, c[3])
            img.set(x0 + x, y0 + y, c)
