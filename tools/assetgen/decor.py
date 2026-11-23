"""Decorative furniture: workshop, shelf, display stand, beds, cabinet, gift boxes, trophies."""
from .core import box, rbox

COLORS = ("pink", "lavender", "mint", "sky")


def workshop():
    e = []
    e += box((1, 0, 1), (15, 12, 15), "wood/wood", faces={"north": "drawer"})
    e += box((0.25, 11.25, 0.25), (15.75, 12, 15.75), "lace")
    e += box((0, 12, 0), (16, 13.5, 16), "painted/top")
    e += box((2, 13.5, 5), (11, 14.5, 11), "painted/machine")
    e += box((8.5, 14.5, 6), (11, 19, 10), "painted/machine")
    e += box((3, 17, 6.5), (11, 19, 9.5), "painted/machine")
    e += box((3.5, 15.75, 7.25), (5, 17, 8.75), "painted/machine")
    e += box((4, 14.5, 7.75), (4.5, 15.75, 8.25), "metal/metal")
    e += box((11, 16, 6.75), (11.75, 18.75, 9.25), "metal/metal")
    e += box((5, 14.5, 6.25), (8.5, 14.75, 9.75), "fabric/cloth")
    e += box((12.5, 13.5, 2.5), (14.5, 13.75, 4.5), "wood/wood")
    e += box((12.75, 13.75, 2.75), (14.25, 15.75, 4.25), "spool/thread")
    e += box((12.5, 15.75, 2.5), (14.5, 16, 4.5), "wood/wood")
    e += rbox((12, 13.5, 10.5), (15, 15, 13.5), "cushion/pin", r=0.4)
    e += box((13, 15, 11.5), (13.25, 16, 11.75), "metal/metal")
    e += box((14, 15, 12.5), (14.25, 15.75, 12.75), "metal/metal")
    e += box((1.5, 13.5, 12), (10, 14.75, 14), "fabric/cloth")
    return e


WORKSHOP_PAL = dict(wood="#F3D2BE", top="#FBD3E2", machine="#BDEBDD", metal="#D9DEE8", thread="#FF8FBF",
                    pin="#FF7A9C", cloth="#D9C3F2", accent="#FF7FB0", main="#FBD3E2")


def shelf():
    e = []
    e += box((0.5, 2, 8), (15.5, 3.5, 16), "painted/shelf")
    e += box((0.5, 1.25, 7.75), (15.5, 2, 8), "lace")
    e += box((2, 0, 14), (3.5, 2, 16), "painted/shelf_dark")
    e += box((12.5, 0, 14), (14, 2, 16), "painted/shelf_dark")
    e += box((1, 3.5, 15.5), (15, 4.25, 16), "painted/shelf_dark")
    return e


SHELF_PAL = dict(shelf="#FFE4EE", shelf_dark="#F6BFD4", main="#FFE4EE")


def stand():
    e = []
    e += rbox((3, 0, 3), (13, 1.25, 13), "painted/stand", r=0.75)
    e += rbox((5, 1.25, 5), (11, 4, 11), "painted/stand_dark", r=0.5)
    e += rbox((3.5, 4, 3.5), (12.5, 5, 12.5), "velvet/cushion", r=0.75)
    e += box((3.25, 4.25, 3.25), (12.75, 4.75, 12.75), "lace")
    return e


STAND_PAL = dict(stand="#FFF7FB", stand_dark="#F3DCEA", cushion="#F7A8C8", main="#FFF7FB")


def bed():
    e = []
    e += rbox((2, 0, 2), (14, 2, 14), "velvet/bed", r=0.75)
    e += box((2, 2, 2), (14, 4, 4.25), "velvet/bed")
    e += box((2, 2, 11), (14, 5.5, 14), "velvet/bed")
    e += box((2, 2, 4.25), (4.25, 4.5, 11), "velvet/bed")
    e += box((11.75, 2, 4.25), (14, 4.5, 11), "velvet/bed")
    e += box((4.25, 2, 4.25), (11.75, 2.5, 11), "cushion/cushion", faces={"up": "heart_cushion"})
    e += rbox((5, 2.5, 9), (11, 4.25, 11), "fabric/pillow", r=0.5)
    return e


BED_PALS = {
    "pink": dict(bed="#F7A8C8", cushion="#FFE1EC", pillow="#FFFFFF", main="#F7A8C8"),
    "lavender": dict(bed="#C9B0F0", cushion="#F1E8FF", pillow="#FFFFFF", main="#C9B0F0"),
    "mint": dict(bed="#9FE3C8", cushion="#E6FBF2", pillow="#FFFFFF", main="#9FE3C8"),
    "sky": dict(bed="#A8D4FF", cushion="#EAF5FF", pillow="#FFFFFF", main="#A8D4FF"),
}


def cabinet_frame():
    e = []
    for x0, z0 in ((0, 0), (14.5, 0), (0, 14.5), (14.5, 14.5)):
        e += box((x0, 0, z0), (x0 + 1.5, 16, z0 + 1.5), "wood/wood")
    e += box((0, 0, 0), (16, 1.5, 16), "wood/wood")
    e += box((0, 14.5, 0), (16, 16, 16), "wood/wood")
    e += box((1.5, 7.5, 1.5), (14.5, 8.5, 14.5), "wood/wood_light")
    e += box((1.5, 1.5, 14.75), (14.5, 14.5, 15.5), "wallpaper")
    return e


def cabinet_glass():
    e = []
    e += box((1.5, 1.5, 0.5), (14.5, 14.5, 0.75), "glass")
    e += box((0.5, 1.5, 1.5), (0.75, 14.5, 14.5), "glass")
    e += box((15.25, 1.5, 1.5), (15.5, 14.5, 14.5), "glass")
    return e


CABINET_PAL = dict(wood="#F6C9D9", wood_light="#FBE1EA", wallpaper="#FFE9F1", accent="#FF9FC4", main="#F6C9D9")


def gift_box():
    e = []
    e += box((2, 0, 2), (14, 9, 14), "polka/box+dot")
    e += box((1.5, 9, 1.5), (14.5, 11, 14.5), "polka/lid+dot")
    e += box((7, 0, 1.95), (9, 9.05, 14.05), "satin/ribbon")
    e += box((1.95, 0, 7), (14.05, 9.05, 9), "satin/ribbon")
    e += box((7, 9, 1.45), (9, 11.05, 14.55), "satin/ribbon")
    e += box((1.45, 9, 7), (14.55, 11.05, 9), "satin/ribbon")
    e += box((7, 11, 7), (9, 12, 9), "satin/ribbon")
    e += box((4, 11, 7.25), (7, 13, 8.75), "satin/ribbon", rot=("z", 22.5, (7, 11.5, 8)))
    e += box((9, 11, 7.25), (12, 13, 8.75), "satin/ribbon", rot=("z", -22.5, (9, 11.5, 8)))
    return e


BOX_PALS = {
    "pink": dict(box="#F9BCD3", lid="#F7AECA", dot="#FFFFFF", ribbon="#B88BE6", main="#F9BCD3"),
    "lavender": dict(box="#D3BFF3", lid="#C8B1EE", dot="#FFFFFF", ribbon="#FF8FBF", main="#D3BFF3"),
    "mint": dict(box="#B5EBD4", lid="#A6E3CA", dot="#FFFFFF", ribbon="#FF9FC0", main="#B5EBD4"),
    "sky": dict(box="#B9DCFF", lid="#A9D2FF", dot="#FFFFFF", ribbon="#FFC94D", main="#B9DCFF"),
}


def trophy(teddy_elements):
    e = []
    e += rbox((3.5, 0, 3.5), (12.5, 2, 12.5), "marble", r=0.5, front="plaque")
    e += box((4.5, 2, 4.5), (11.5, 3.5, 11.5), "gold/gold")
    e += [el.transformed(0.6, (0, 3.5, 0)) for el in teddy_elements]
    return e


TROPHY_PALS = {
    "golden_collector_trophy": dict(main="#F7C948", light="#FFE08A", inner="#F2B33D", accent="#E8A21C",
                                    nose="#8A5A1C", eye="#6B4A12", iris="#A87A2A", blush="#F2A33D",
                                    pad="#FFE08A", stitch="#D9A030", metallic=True, gold="#FFD36E",
                                    marble="#FFF8F2"),
    "rainbow_collector_trophy": dict(main="#FFC4DD", light="#FFFFFF", inner="#FFB3C9", accent="#FFD36E",
                                     nose="#6E3A55", eye="#3B2440", rainbow=True, gold="#FFD36E",
                                     marble="#FFF8F2", stitch="#E8C8E0"),
}
