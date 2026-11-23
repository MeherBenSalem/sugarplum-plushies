"""Core helpers for the Sugarplum Plushies asset generator: PNG writing, colours,
the cuboid model DSL, UV packing and JSON output."""
import colorsys
import json
import math
import struct
import zlib
from pathlib import Path

MODID = "sugarplumplushies"
ROOT = Path(__file__).resolve().parents[2] / "1.20.1" / "common" / "src" / "main" / "resources"
ASSETS = ROOT / "assets" / MODID
DATA = ROOT / "data" / MODID
PX = 4            # texels per model unit on plushie/decor textures
TEX = 64          # texture size in texels (covers UV 0..16)

DIRS = ("north", "south", "east", "west", "up", "down")


# --------------------------------------------------------------------------- output

def write_json(path, obj):
    path = Path(path)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


# --------------------------------------------------------------------------- colours

def C(value, alpha=255):
    """Accepts '#RRGGBB', (r,g,b) or (r,g,b,a) and returns an RGBA tuple."""
    if isinstance(value, str):
        v = value.lstrip("#")
        return (int(v[0:2], 16), int(v[2:4], 16), int(v[4:6], 16), alpha)
    if len(value) == 3:
        return (value[0], value[1], value[2], alpha)
    return tuple(value)


def clamp8(v):
    return max(0, min(255, int(round(v))))


def mix(a, b, t):
    a, b = C(a), C(b)
    return tuple(clamp8(a[i] + (b[i] - a[i]) * t) for i in range(4))


def lighten(c, t):
    c = C(c)
    return mix(c, (255, 255, 255, c[3]), t)


def darken(c, t):
    c = C(c)
    return mix(c, (0, 0, 0, c[3]), t)


def scale(c, f):
    c = C(c)
    return (clamp8(c[0] * f), clamp8(c[1] * f), clamp8(c[2] * f), c[3])


def hue_shift(c, dh, ds=0.0, dv=0.0):
    c = C(c)
    h, s, v = colorsys.rgb_to_hsv(c[0] / 255, c[1] / 255, c[2] / 255)
    r, g, b = colorsys.hsv_to_rgb((h + dh) % 1.0, max(0, min(1, s + ds)), max(0, min(1, v + dv)))
    return (clamp8(r * 255), clamp8(g * 255), clamp8(b * 255), c[3])


def pastel_hue(h, s=0.32, v=1.0):
    r, g, b = colorsys.hsv_to_rgb(h % 1.0, s, v)
    return (clamp8(r * 255), clamp8(g * 255), clamp8(b * 255), 255)


# --------------------------------------------------------------------------- images

class Img:
    def __init__(self, w, h, fill=(0, 0, 0, 0)):
        self.w, self.h = w, h
        self.data = bytearray(bytes(C(fill)) * (w * h))

    def get(self, x, y):
        i = (y * self.w + x) * 4
        return tuple(self.data[i:i + 4])

    def set(self, x, y, c):
        if 0 <= x < self.w and 0 <= y < self.h:
            i = (int(y) * self.w + int(x)) * 4
            self.data[i:i + 4] = bytes(C(c))

    def blend(self, x, y, c, alpha=None):
        if not (0 <= x < self.w and 0 <= y < self.h):
            return
        c = C(c)
        a = (c[3] / 255) if alpha is None else alpha
        under = self.get(int(x), int(y))
        if under[3] == 0:
            self.set(x, y, (c[0], c[1], c[2], clamp8(255 * a)))
            return
        out = tuple(clamp8(under[i] + (c[i] - under[i]) * a) for i in range(3)) + (max(under[3], clamp8(255 * a)),)
        self.set(x, y, out)

    def rect(self, x0, y0, x1, y1, c):
        for y in range(max(0, y0), min(self.h, y1)):
            for x in range(max(0, x0), min(self.w, x1)):
                self.set(x, y, c)

    def ellipse(self, cx, cy, rx, ry, c, alpha=None):
        for y in range(int(math.floor(cy - ry)), int(math.ceil(cy + ry)) + 1):
            for x in range(int(math.floor(cx - rx)), int(math.ceil(cx + rx)) + 1):
                dx = (x + 0.5 - cx) / max(rx, 0.01)
                dy = (y + 0.5 - cy) / max(ry, 0.01)
                if dx * dx + dy * dy <= 1.0:
                    if alpha is None:
                        self.set(x, y, c)
                    else:
                        self.blend(x, y, c, alpha)

    def stamp(self, x0, y0, rows, palette):
        for dy, row in enumerate(rows):
            for dx, ch in enumerate(row):
                if ch in palette and palette[ch] is not None:
                    col = C(palette[ch])
                    if col[3] < 255:
                        self.blend(x0 + dx, y0 + dy, col)
                    else:
                        self.set(x0 + dx, y0 + dy, col)

    def save(self, path):
        path = Path(path)
        path.parent.mkdir(parents=True, exist_ok=True)
        raw = bytearray()
        stride = self.w * 4
        for y in range(self.h):
            raw.append(0)
            raw += self.data[y * stride:(y + 1) * stride]

        def chunk(tag, payload):
            return struct.pack(">I", len(payload)) + tag + payload + struct.pack(">I", zlib.crc32(tag + payload) & 0xFFFFFFFF)

        png = b"\x89PNG\r\n\x1a\n"
        png += chunk(b"IHDR", struct.pack(">IIBBBBB", self.w, self.h, 8, 6, 0, 0, 0))
        png += chunk(b"IDAT", zlib.compress(bytes(raw), 9))
        png += chunk(b"IEND", b"")
        path.write_bytes(png)


def ascii_image(rows, palette):
    img = Img(len(rows[0]), len(rows))
    img.stamp(0, 0, rows, palette)
    return img


# --------------------------------------------------------------------------- model DSL

class El:
    """One cuboid element. `mats` maps face direction -> material string (None hides the face)."""

    def __init__(self, frm, to, mat, faces=None, rot=None, part=None):
        self.frm = [float(v) for v in frm]
        self.to = [float(v) for v in to]
        self.mats = {d: mat for d in DIRS}
        if faces:
            self.mats.update(faces)
        self.rot = rot          # (axis, angle, (ox, oy, oz))
        self.part = part

    def dims(self, d):
        dx = self.to[0] - self.frm[0]
        dy = self.to[1] - self.frm[1]
        dz = self.to[2] - self.frm[2]
        if d in ("north", "south"):
            return dx, dy
        if d in ("east", "west"):
            return dz, dy
        return dx, dz

    def copy(self):
        e = El(self.frm, self.to, None, dict(self.mats), self.rot, self.part)
        return e

    def transformed(self, s, offset, mat_map=None):
        """Scale around (8, 0, 8) and translate — used for the trophy statues."""
        def tp(p):
            return [8 + (p[0] - 8) * s + offset[0], p[1] * s + offset[1], 8 + (p[2] - 8) * s + offset[2]]
        e = self.copy()
        e.frm, e.to = tp(self.frm), tp(self.to)
        if self.rot:
            axis, angle, origin = self.rot
            e.rot = (axis, angle, tuple(tp(origin)))
        if mat_map:
            e.mats = {d: (mat_map(m) if m else m) for d, m in e.mats.items()}
        e.part = None
        return e


def box(frm, to, mat, faces=None, rot=None, part=None):
    return [El(frm, to, mat, faces, rot, part)]


def rbox(frm, to, mat, r=0.5, front=None, faces=None, part=None):
    """A soft, bevelled cuboid built from three overlapping boxes."""
    x0, y0, z0 = frm
    x1, y1, z1 = to
    r = min(r, max(0.125, min(x1 - x0, y1 - y0, z1 - z0) / 2 - 0.125))
    a = El((x0 + r, y0 + r, z0), (x1 - r, y1 - r, z1), mat, faces, None, part)
    if front:
        a.mats["north"] = front
    b = El((x0, y0 + r, z0 + r), (x1, y1 - r, z1 - r), mat, faces, None, part)
    c = El((x0 + r, y0, z0 + r), (x1 - r, y1, z1 - r), mat, faces, None, part)
    if faces and "north" in faces:
        b.mats["north"] = mat
        c.mats["north"] = mat
    return [a, b, c]


def mirror_el(e, part=None):
    m = e.copy()
    m.frm = [16 - e.to[0], e.frm[1], e.frm[2]]
    m.to = [16 - e.frm[0], e.to[1], e.to[2]]
    m.mats["east"], m.mats["west"] = e.mats["west"], e.mats["east"]
    if e.rot:
        axis, angle, origin = e.rot
        m.rot = (axis, angle if axis == "x" else -angle, (16 - origin[0], origin[1], origin[2]))
    m.part = part if part is not None else e.part
    return m


def sym(els, part=None):
    """Elements plus their mirror image across x = 8."""
    return list(els) + [mirror_el(e, part) for e in els]


def mirrored(els, part=None):
    return [mirror_el(e, part) for e in els]


def flower(x, y, z, size, depth=0.25):
    """A flat five-petal flower facing north (transparent outside the petals)."""
    return box((x, y, z), (x + size, y + size, z + depth), "fabric/petal",
               faces={"north": "flower", "south": "flower", "east": None, "west": None, "up": None, "down": None})


# --------------------------------------------------------------------------- UV packing

def _ceil_px(v):
    return max(1, int(math.ceil(round(v * PX, 4))))


def is_decal(mat, decals):
    return mat.split("/")[0] in decals


def plan_layout(elements, decals, sizes=(64, 128)):
    """Assigns each material a tile on the texture.
    Returns ({mat: (x, y, w, h)} in texels, texture size)."""
    usage = {}
    for e in elements:
        for d, mat in e.mats.items():
            if not mat:
                continue
            w, h = e.dims(d)
            cw, ch = usage.get(mat, (0, 0))
            usage[mat] = (max(cw, w), max(ch, h))

    for tex in sizes:
        caps = (8, 6, 5, 4, 3) if tex <= 64 else (16, 12, 10, 8, 6, 4)
        for cap in caps:
            for pad in (1, 0):
                tiles = []
                for mat, (w, h) in usage.items():
                    if is_decal(mat, decals):
                        tiles.append((mat, _ceil_px(w), _ceil_px(h)))
                    else:
                        tiles.append((mat, _ceil_px(min(cap, max(w, 0.5))), _ceil_px(min(cap, max(h, 0.5)))))
                placed = _shelf_pack(tiles, pad, tex)
                if placed is not None:
                    return placed, tex
    raise RuntimeError("Could not fit textures for materials: %s" % sorted(usage))


def _shelf_pack(tiles, pad, tex):
    tiles = sorted(tiles, key=lambda t: (-t[2], -t[1], t[0]))
    x = y = shelf = 0
    out = {}
    for mat, w, h in tiles:
        if w > tex or h > tex:
            return None
        if x + w > tex:
            x = 0
            y += shelf + pad
            shelf = 0
        if y + h > tex:
            return None
        out[mat] = (x, y, w, h)
        x += w + pad
        shelf = max(shelf, h)
    return out


def face_uv(e, d, layout, decals, tex):
    mat = e.mats[d]
    tx, ty, tw, th = layout[mat]
    k = 16.0 / tex
    if is_decal(mat, decals):
        return [tx * k, ty * k, (tx + tw) * k, (ty + th) * k]
    w, h = e.dims(d)
    return [tx * k, ty * k, (tx + min(w * PX, tw)) * k, (ty + min(h * PX, th)) * k]


def model_elements(elements, layout, decals, tex, texture_var="t"):
    out = []
    for e in elements:
        faces = {}
        for d in DIRS:
            mat = e.mats[d]
            if not mat:
                continue
            uv = [round(v, 4) for v in face_uv(e, d, layout, decals, tex)]
            faces[d] = {"uv": uv, "texture": "#" + texture_var}
        if not faces:
            continue
        el = {"from": [round(v, 4) for v in e.frm], "to": [round(v, 4) for v in e.to], "faces": faces}
        if e.rot:
            axis, angle, origin = e.rot
            el["rotation"] = {"angle": angle, "axis": axis, "origin": [round(v, 4) for v in origin]}
        out.append(el)
    return out


def bounds(elements):
    lo = [99.0, 99.0, 99.0]
    hi = [-99.0, -99.0, -99.0]
    for e in elements:
        for i in range(3):
            lo[i] = min(lo[i], e.frm[i], e.to[i])
            hi[i] = max(hi[i], e.frm[i], e.to[i])
    return lo, hi
