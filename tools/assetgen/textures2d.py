"""Hand-drawn pixel art (items, particles) and the pastel GUI sheets."""
from .core import ASSETS, Img, ascii_image, darken, lighten

ITEMS = {
    "soft_fabric": ([
        "................",
        "................",
        "..############..",
        ".#PPPPPPPPPPPP#.",
        ".#PppppppppPPP#.",
        ".#PPsPsPsPsPPP#.",
        ".#PPPPPPPPPPPP#.",
        ".#LLLLLLLLLLLL#.",
        ".#LllllllllLLL#.",
        ".#LLsLsLsLsLLL#.",
        ".#MMMMMMMMMMMM#.",
        ".#MmmmmmmmmMMM#.",
        ".#MMsMsMsMsMMM#.",
        "..############..",
        "................",
        "................",
    ], {"P": "#F7A8C8", "p": "#FFD3E4", "L": "#CDB4EE", "l": "#E7DBFA", "M": "#A8E6CF", "m": "#D3F5E6",
        "s": "#FFFFFF", "#": "#8A4A6E"}),
    "plush_stuffing": ([
        "................",
        "......####......",
        "....##WWWW##....",
        "...#WWWWWWWW#...",
        "..#WWWWWWWWwW#..",
        "..#WWwWWWWWWW#..",
        ".#WWWWWWWWWWWW#.",
        ".#WWWWWWWWWWWs#.",
        ".#WWWWWWwWWWss#.",
        ".#WWWWWWWWWWWs#.",
        "..#WWWWWWWWss#..",
        "..#sWWWWWWWss#..",
        "...#ssWWWsss#...",
        "....##ssss##....",
        "......####......",
        "................",
    ], {"W": "#FFFFFF", "w": "#F2E8FF", "s": "#DCCFE8", "#": "#9A86AE"}),
    "button_eyes": ([
        "................",
        "................",
        "................",
        "..####....####..",
        ".#DDDD#..#DDDD#.",
        "#DwwDDD##DwwDDD#",
        "#DwDhDh##DwDhDh#",
        "#DDDDDD##DDDDDD#",
        "#DDDhDh##DDDhDh#",
        ".#DDDD#..#DDDD#.",
        "..####....####..",
        "................",
        "...pp......pp...",
        "..p..p....p..p..",
        "................",
        "................",
    ], {"D": "#3B2440", "w": "#8C6A94", "h": "#1C1020", "#": "#1C1020", "p": "#FF8FBF"}),
    "embroidery_thread": ([
        "................",
        "....########....",
        "....#WWWWWW#....",
        "....########....",
        ".....#PPPP#.....",
        ".....#PppP#.....",
        ".....#PPPP#.....",
        ".....#pPPp#.....",
        ".....#PPPP#.....",
        ".....#PppP#.....",
        "....########....",
        "....#WWWWWW#....",
        "....########....",
        "......#P........",
        ".......#PP......",
        "................",
    ], {"W": "#F3D2BE", "#": "#8A5A4A", "P": "#FF8FBF", "p": "#FFC2DB"}),
    "satin_ribbon": ([
        "................",
        "................",
        ".##..........##.",
        "#RR##......##RR#",
        "#RrRR#....#RRrR#",
        "#RRrRR####RRrRR#",
        "#RRRRRKKKKRRRRR#",
        "#RRRRRKkKKRRRRR#",
        "#RRrRR####RRrRR#",
        "#RrRR#.##.#RRrR#",
        "#RR##.#RR#.##RR#",
        ".##..#RR#R#.##..",
        "....#RR#.#R#....",
        "....#R#...#R#...",
        ".....#.....##...",
        "................",
    ], {"R": "#FF7FB0", "r": "#FFC2DB", "K": "#E05A93", "k": "#FFB0D0", "#": "#9A3A66"}),
    "sugar_star": ([
        "................",
        ".......##.......",
        "......#YY#......",
        "......#YW#......",
        ".....#YYWY#.....",
        "######YYYY######",
        "#YYYYYYYYYYYYYY#",
        ".#YYYYYYYYYYYY#.",
        ".#YYYeYYYYeYYY#.",
        "..#YbYYYYYYbY#..",
        "...#YYYmmYYY#...",
        "...#YYYYYYYY#...",
        "..#YYYY##YYYY#..",
        "..#YYY#..#YYY#..",
        ".#YY#......#YY#.",
        ".###........###.",
    ], {"Y": "#FFE27A", "W": "#FFFFFF", "e": "#5A3A30", "b": "#FF9FBF", "m": "#E07A5A", "#": "#C98A1C"}),
    "plushie_collection_book": ([
        "................",
        "..############..",
        ".#PPPPPPPPPPPP#.",
        ".#PpPPPPPPPPPP#c",
        ".#PPPPHHPHHPPP#c",
        ".#PPPHHHHHHHPP#c",
        ".#PPPHHHHHHHPP#c",
        ".#PPPPHHHHHPPP#c",
        ".#PPPPPHHHPPPP#c",
        ".#PPPPPPHPPPPP#c",
        ".#PPPPPPPPPPPP#c",
        ".#PppPPPPPPPPP#c",
        ".#PPPPPPPPPPPP#c",
        "..############c.",
        "...cccccccccccc.",
        "................",
    ], {"P": "#F29BBE", "p": "#FFC7DD", "H": "#FFFFFF", "c": "#FFF4E8", "#": "#9A3A66"}),
}

PARTICLES = {
    "pastel_heart": ([
        ".##.##..",
        "#PP#PP#.",
        "#PwPPP#.",
        "#PPPPP#.",
        ".#PPP#..",
        "..#P#...",
        "...#....",
        "........",
    ], {"P": "#FF9FC6", "w": "#FFFFFF", "#": "#E0578F"}),
    "twinkle_0": ([
        "........",
        "........",
        "...Y....",
        "..YWY...",
        "...Y....",
        "........",
        "........",
        "........",
    ], {"Y": "#FFE58A", "W": "#FFFFFF"}),
    "twinkle_1": ([
        "...Y....",
        "...Y....",
        "..YWY...",
        "YYWWWYY.",
        "..YWY...",
        "...Y....",
        "...Y....",
        "........",
    ], {"Y": "#FFE58A", "W": "#FFFFFF"}),
    "twinkle_2": ([
        "........",
        ".Y...Y..",
        "..YWY...",
        "..WWW...",
        "..YWY...",
        ".Y...Y..",
        "........",
        "........",
    ], {"Y": "#FFD0F0", "W": "#FFFFFF"}),
    "sleepy_z": ([
        "........",
        ".ZZZZZ..",
        ".zzzZz..",
        "....Z...",
        "...Z....",
        "..zZzzz.",
        ".ZZZZZ..",
        "........",
    ], {"Z": "#B9A7F0", "z": "#E3DBFF"}),
}

PINK = "#FDE7F0"
BORDER = "#B0628A"
SLOT_DARK = "#C28AA6"
SLOT_FILL = "#F6D3E3"
HEART = [".##.##.", "#######", "#######", ".#####.", "..###..", "...#..."]


def panel(img, x0, y0, x1, y1, fill, border, light="#FFFFFF", shadow=None):
    shadow = shadow or darken(fill, 0.08)
    img.rect(x0 + 1, y0, x1 - 1, y1, border)
    img.rect(x0, y0 + 1, x1, y1 - 1, border)
    img.rect(x0 + 1, y0 + 1, x1 - 1, y1 - 1, fill)
    img.rect(x0 + 2, y0 + 1, x1 - 2, y0 + 2, light)
    img.rect(x0 + 1, y0 + 2, x0 + 2, y1 - 2, light)
    img.rect(x0 + 2, y1 - 2, x1 - 2, y1 - 1, shadow)
    img.rect(x1 - 2, y0 + 2, x1 - 1, y1 - 2, shadow)


def recess(img, x0, y0, w, h, fill=SLOT_FILL, dark=SLOT_DARK, light="#FFFFFF"):
    img.rect(x0, y0, x0 + w, y0 + h, fill)
    img.rect(x0, y0, x0 + w - 1, y0 + 1, dark)
    img.rect(x0, y0, x0 + 1, y0 + h - 1, dark)
    img.rect(x0 + 1, y0 + h - 1, x0 + w, y0 + h, light)
    img.rect(x0 + w - 1, y0 + 1, x0 + w, y0 + h, light)


def dashed(img, x0, y0, x1, y1, color):
    for x in range(x0, x1):
        if x % 4 < 2:
            img.set(x, y0, color)
            img.set(x, y1, color)
    for y in range(y0, y1 + 1):
        if y % 4 < 2:
            img.set(x0, y, color)
            img.set(x1, y, color)


def workshop_gui():
    img = Img(256, 256)
    panel(img, 0, 0, 236, 222, PINK, BORDER)
    dashed(img, 4, 4, 231, 217, "#F2B3CC")
    recess(img, 9, 19, 92, 110, "#F9DDE9")
    for r in range(6):
        for c in range(5):
            recess(img, 10 + c * 18, 20 + r * 18, 18, 18)
    recess(img, 101, 19, 10, 110, "#F3CADB")
    panel(img, 115, 18, 230, 130, "#FFF8FB", "#E7AFC8")
    recess(img, 118, 22, 36, 36, "#FCE9F1")
    for r in range(2):
        for c in range(5):
            recess(img, 118 + c * 22, 62 + r * 20, 18, 18)
    for r in range(3):
        for c in range(9):
            recess(img, 37 + c * 18, 139 + r * 18, 18, 18)
    for c in range(9):
        recess(img, 37 + c * 18, 197, 18, 18)
    img.stamp(222, 7, HEART, {"#": "#F48DBC"})
    img.stamp(212, 9, HEART[:5], {"#": "#FFC2DB"})
    img.save(ASSETS / "textures" / "gui" / "plushie_workshop.png")


def book_gui():
    img = Img(256, 256)
    cover = "#F29BBE"
    panel(img, 0, 0, 256, 180, cover, "#B85A86", light="#F9C2D8", shadow="#D9739F")
    for x in range(6, 250, 10):
        img.stamp(x, 1 if (x // 10) % 2 else 174, ["#.#", "###", ".#."], {"#": "#FFD0E3"})
    page = "#FFF7EE"
    for x0, x1 in ((8, 126), (130, 248)):
        img.rect(x0, 6, x1, 174, page)
        img.rect(x0, 6, x1, 7, "#F2E1D2")
        img.rect(x0, 173, x1, 174, "#EAD3C1")
    for i in range(6):
        shade = darken(page, 0.02 * (6 - i))
        img.rect(126 - i, 6, 127 - i, 174, shade)
        img.rect(130 + i, 6, 131 + i, 174, shade)
    img.rect(126, 4, 130, 176, "#D9739F")
    for y in range(24, 172, 12):
        for x in range(14, 122, 3):
            img.set(x, y, "#F7EADF")
    img.rect(232, 0, 238, 22, "#B88BE6")
    img.stamp(232, 22, ["######", "##..##", "#....#"], {"#": "#B88BE6"})
    img.stamp(118, 8, HEART[:5], {"#": "#FFC2DB"})
    img.stamp(236, 160, HEART, {"#": "#FFC2DB"})
    img.save(ASSETS / "textures" / "gui" / "collection_book.png")


def toast_gui():
    img = Img(256, 256)  # GuiGraphics.blit assumes a 256x256 sheet
    panel(img, 0, 0, 160, 32, "#FFF0F6", "#E58FB5")
    img.ellipse(16, 16, 12, 12, "#FFD9E8")
    for x, y in ((150, 5), (154, 9), (146, 25)):
        img.set(x, y, "#FFD36E")
        img.set(x + 1, y, "#FFFFFF")
    img.save(ASSETS / "textures" / "gui" / "toast.png")


def generate():
    for name, (rows, pal) in ITEMS.items():
        ascii_image(rows, pal).save(ASSETS / "textures" / "item" / f"{name}.png")
    for name, (rows, pal) in PARTICLES.items():
        ascii_image(rows, pal).save(ASSETS / "textures" / "particle" / f"{name}.png")
    workshop_gui()
    book_gui()
    toast_gui()
