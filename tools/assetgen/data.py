"""Recipes, loot tables, tags, advancements and English translations."""
from .core import ASSETS, DATA, MODID, ROOT, write_json
from .decor import COLORS

M = MODID + ":"
SPECIES_ORDER = [
    "strawberry_bunny", "pink_teddy_bear", "lavender_kitty", "cloud_puppy", "cherry_blossom_fox",
    "angel_bunny", "sleepy_panda", "heart_bear", "little_duck", "moon_cat", "sugarplum_dragon",
    "pastel_axolotl", "baby_deer", "honey_bee", "strawberry_cow",
]

FABRIC, STUFF, EYES, THREAD, RIBBON, STAR = (M + "soft_fabric", M + "plush_stuffing", M + "button_eyes",
                                            M + "embroidery_thread", M + "satin_ribbon", M + "sugar_star")

# Workshop pattern costs: (base ingredients, dye for colourway 1, dye for colourway 2)
WORKSHOP = {
    "strawberry_bunny": ([(FABRIC, 3), (STUFF, 2), (EYES, 1), ("minecraft:sweet_berries", 2)],
                         "minecraft:white_dye", "minecraft:blue_dye"),
    "pink_teddy_bear": ([(FABRIC, 3), (STUFF, 2), (EYES, 1), (RIBBON, 1), ("minecraft:pink_dye", 1)],
                        "minecraft:brown_dye", "minecraft:light_blue_dye"),
    "lavender_kitty": ([(FABRIC, 3), (STUFF, 2), (EYES, 1), (THREAD, 1), ("minecraft:allium", 1)],
                       "minecraft:orange_dye", "minecraft:black_dye"),
    "cloud_puppy": ([(FABRIC, 4), (STUFF, 3), (EYES, 1), (THREAD, 1), ("minecraft:bone", 1), ("minecraft:white_wool", 1)],
                    "minecraft:orange_dye", "minecraft:gray_dye"),
    "cherry_blossom_fox": ([(FABRIC, 4), (STUFF, 3), (EYES, 1), (THREAD, 1), ("minecraft:pink_petals", 2)],
                           "minecraft:white_dye", "minecraft:purple_dye"),
    "angel_bunny": ([(FABRIC, 5), (STUFF, 3), (EYES, 1), (THREAD, 2), (RIBBON, 1), ("minecraft:feather", 4),
                     ("minecraft:gold_ingot", 1)], "minecraft:pink_dye", "minecraft:light_blue_dye"),
    "sleepy_panda": ([(FABRIC, 4), (STUFF, 3), (EYES, 1), (THREAD, 1), ("minecraft:bamboo", 4)],
                     "minecraft:pink_dye", "minecraft:lime_dye"),
    "heart_bear": ([(FABRIC, 5), (STUFF, 3), (EYES, 1), (THREAD, 2), (RIBBON, 2), ("minecraft:rose_bush", 1)],
                   "minecraft:red_dye", "minecraft:magenta_dye"),
    "little_duck": ([(FABRIC, 3), (STUFF, 2), (EYES, 1), ("minecraft:feather", 2), ("minecraft:yellow_dye", 1)],
                    "minecraft:white_dye", "minecraft:lime_dye"),
    "moon_cat": ([(FABRIC, 5), (STUFF, 3), (EYES, 1), (THREAD, 2), (RIBBON, 1), ("minecraft:glowstone_dust", 2),
                  ("minecraft:lapis_lazuli", 2)], "minecraft:pink_dye", "minecraft:purple_dye"),
    "sugarplum_dragon": ([(FABRIC, 6), (STUFF, 4), (EYES, 1), (THREAD, 3), (RIBBON, 1), (STAR, 1),
                          ("minecraft:phantom_membrane", 2), ("minecraft:amethyst_shard", 2)],
                         "minecraft:cyan_dye", "minecraft:pink_dye"),
    "pastel_axolotl": ([(FABRIC, 4), (STUFF, 3), (EYES, 1), (THREAD, 1), ("minecraft:lily_pad", 1), ("minecraft:pink_dye", 1)],
                       "minecraft:lime_dye", "minecraft:purple_dye"),
    "baby_deer": ([(FABRIC, 4), (STUFF, 3), (EYES, 1), (THREAD, 1), ("minecraft:leather", 1), ("minecraft:oxeye_daisy", 1)],
                  "minecraft:pink_dye", "minecraft:light_blue_dye"),
    "honey_bee": ([(FABRIC, 3), (STUFF, 2), (EYES, 1), ("minecraft:honeycomb", 1), ("minecraft:yellow_dye", 1)],
                  "minecraft:pink_dye", "minecraft:lime_dye"),
    "strawberry_cow": ([(FABRIC, 5), (STUFF, 3), (EYES, 1), (THREAD, 2), (RIBBON, 1), ("minecraft:sweet_berries", 3),
                        ("minecraft:leather", 1), ("minecraft:gold_nugget", 2)], "minecraft:cocoa_beans", "minecraft:blue_dye"),
}

SPARKLE_COST = [(STAR, 1), (THREAD, 2), ("minecraft:gold_nugget", 4)]


def ing(item):
    """An ingredient; a list of items/tags means any of them."""
    if isinstance(item, list):
        return [ing(i) for i in item]
    return {"tag": item[1:]} if item.startswith("#") else {"item": item}


def workshop_recipe(species, variant, ingredients):
    result = {"item": M + species, "nbt": '{BlockStateTag:{variant:"%d"}}' % variant}
    return {
        "type": M + "plushie_workshop",
        "ingredients": [{"ingredient": ing(item), "count": count} for item, count in ingredients],
        "result": result,
    }


def shaped(pattern, key, result, count=1):
    return {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern,
            "key": {k: ing(v) for k, v in key.items()}, "result": {"item": result, "count": count}}


def shapeless(ingredients, result, count=1):
    return {"type": "minecraft:crafting_shapeless", "category": "misc",
            "ingredients": [ing(i) for i in ingredients], "result": {"item": result, "count": count}}


DYES = {"pink": "minecraft:pink_dye", "lavender": "minecraft:purple_dye", "mint": "minecraft:lime_dye",
        "sky": "minecraft:light_blue_dye"}
WOOLS = {"pink": "minecraft:pink_wool", "lavender": "minecraft:magenta_wool", "mint": "minecraft:lime_wool",
         "sky": "minecraft:light_blue_wool"}


def recipes(variant_ids):
    out = {}
    for species in SPECIES_ORDER:
        base, dye1, dye2 = WORKSHOP[species]
        ids = variant_ids[species]
        out["workshop/%s_%s" % (species, ids[0])] = workshop_recipe(species, 0, base)
        out["workshop/%s_%s" % (species, ids[1])] = workshop_recipe(species, 1, base + [(dye1, 1)])
        out["workshop/%s_%s" % (species, ids[2])] = workshop_recipe(species, 2, base + [(dye2, 1)])
        out["workshop/%s_sparkle" % species] = workshop_recipe(species, 3, [(M + species, 1)] + SPARKLE_COST)

    out["soft_fabric"] = shapeless(["#minecraft:wool", "#minecraft:wool", "minecraft:string"], FABRIC, 3)
    out["plush_stuffing"] = shapeless(["#minecraft:wool", "minecraft:feather"], STUFF, 4)
    out["button_eyes"] = shapeless(["minecraft:black_dye", "#minecraft:buttons"], EYES, 4)
    out["embroidery_thread"] = shapeless(["minecraft:string", ["#forge:dyes", "#c:dyes"]], THREAD, 2)  # Forge and Fabric dye tags
    out["satin_ribbon"] = shaped(["SPS"], {"S": "minecraft:string", "P": "minecraft:pink_dye"}, RIBBON, 3)
    out["sugar_star"] = shaped([" A ", "GSG", " A "], {"A": "minecraft:amethyst_shard", "G": "minecraft:glowstone_dust",
                                                       "S": "minecraft:sugar"}, STAR, 1)
    out["plushie_workshop"] = shaped(["FIF", "PTP", "PPP"], {"F": "#minecraft:wool", "I": "minecraft:iron_ingot",
                                                            "T": "minecraft:crafting_table", "P": "#minecraft:planks"},
                                     M + "plushie_workshop")
    out["plushie_collection_book"] = shapeless(["minecraft:book", FABRIC, RIBBON], M + "plushie_collection_book")
    out["plushie_shelf"] = shaped(["LRL", "S S"], {"L": "#minecraft:wooden_slabs", "R": RIBBON, "S": "minecraft:stick"},
                                  M + "plushie_shelf", 2)
    out["display_stand"] = shaped(["F", "P", "L"], {"F": FABRIC, "P": "#minecraft:planks", "L": "#minecraft:wooden_slabs"},
                                  M + "display_stand")
    out["display_cabinet"] = shaped(["PGP", "GSG", "PGP"], {"P": "#minecraft:planks", "G": "minecraft:glass_pane",
                                                           "S": M + "plushie_shelf"}, M + "display_cabinet")
    for color in COLORS:
        out["%s_plushie_bed" % color] = shaped(["SFS", "WWW"], {"S": STUFF, "F": FABRIC, "W": WOOLS[color]},
                                               M + "%s_plushie_bed" % color)
        out["%s_plushie_box" % color] = shaped(["PRP", "PDP", "PPP"], {"P": "minecraft:paper", "R": RIBBON, "D": DYES[color]},
                                               M + "%s_plushie_box" % color)
    return out


def loot_simple(block):
    return {"type": "minecraft:block", "pools": [{
        "rolls": 1, "bonus_rolls": 0,
        "entries": [{"type": "minecraft:item", "name": M + block}],
        "conditions": [{"condition": "minecraft:survives_explosion"}]}]}


def loot_plushie(block):
    return {"type": "minecraft:block", "pools": [{
        "rolls": 1, "bonus_rolls": 0,
        "entries": [{"type": "minecraft:item", "name": M + block, "functions": [
            {"function": "minecraft:copy_name", "source": "block_entity"},
            {"function": "minecraft:copy_state", "block": M + block, "properties": ["variant"]}]}],
        "conditions": [{"condition": "minecraft:survives_explosion"}]}]}


def loot_named(block):
    table = loot_simple(block)
    table["pools"][0]["entries"][0]["functions"] = [{"function": "minecraft:copy_name", "source": "block_entity"}]
    return table


def advancements():
    def adv(parent, icon, key, frame, criteria, hidden=False, nbt=None, requirements=None):
        icon_obj = {"item": icon}
        if nbt:
            icon_obj["nbt"] = nbt
        out = {"display": {"icon": icon_obj, "title": {"translate": "advancements.%s.%s.title" % (MODID, key)},
                           "description": {"translate": "advancements.%s.%s.description" % (MODID, key)},
                           "frame": frame, "show_toast": True, "announce_to_chat": True, "hidden": hidden},
               "criteria": criteria}
        if parent:
            out["parent"] = parent
        else:
            out["display"]["background"] = "minecraft:textures/block/pink_wool.png"
            out["display"]["announce_to_chat"] = False
        if requirements:
            out["requirements"] = requirements
        return out

    def has(items):
        return {"trigger": "minecraft:inventory_changed", "conditions": {"items": items}}

    plushie_tag = {"tag": MODID + ":plushies"}
    return {
        "root": adv(None, M + "plushie_workshop", "root", "task",
                    {"fabric": has([{"items": [FABRIC]}]), "workshop": has([{"items": [M + "plushie_workshop"]}])},
                    requirements=[["fabric", "workshop"]]),
        "first_plushie": adv(M + "root", M + "pink_teddy_bear", "first_plushie", "task", {"plushie": has([plushie_tag])}),
        "collection_book": adv(M + "first_plushie", M + "plushie_collection_book", "collection_book", "task",
                               {"book": has([{"items": [M + "plushie_collection_book"]}])}),
        "sparkle_edition": adv(M + "first_plushie", STAR, "sparkle_edition", "goal",
                               {"sparkle": has([dict(plushie_tag, nbt='{BlockStateTag:{variant:"3"}}')])}),
        "sugarplum_dragon": adv(M + "first_plushie", M + "sugarplum_dragon", "sugarplum_dragon", "goal",
                                {"dragon": has([{"items": [M + "sugarplum_dragon"]}])}),
        "all_species": adv(M + "collection_book", M + "golden_collector_trophy", "all_species", "challenge",
                           {"collected": {"trigger": "minecraft:impossible"}}),
        "complete_collection": adv(M + "all_species", M + "rainbow_collector_trophy", "complete_collection", "challenge",
                                   {"collected": {"trigger": "minecraft:impossible"}}),
    }


def empty_structure(size):
    """Gzipped NBT structure with no blocks — the arena used by the GameTests."""
    import gzip
    import struct

    def name(s):
        b = s.encode()
        return struct.pack(">H", len(b)) + b

    out = b"\x0a" + name("")
    out += b"\x03" + name("DataVersion") + struct.pack(">i", 3465)
    out += b"\x09" + name("size") + b"\x03" + struct.pack(">i", 3) + b"".join(struct.pack(">i", v) for v in size)
    for key in ("palette", "blocks", "entities"):
        out += b"\x09" + name(key) + b"\x00" + struct.pack(">i", 0)
    out += b"\x00"
    return gzip.compress(out)


def generate(variant_ids, block_names):
    structure = DATA / "structures" / "empty.nbt"
    structure.parent.mkdir(parents=True, exist_ok=True)
    structure.write_bytes(empty_structure((5, 4, 5)))
    for name, recipe in recipes(variant_ids).items():
        write_json(DATA / "recipes" / f"{name}.json", recipe)
    for block in block_names:
        if block in SPECIES_ORDER:
            table = loot_plushie(block)
        elif block.endswith("_plushie_box"):
            table = loot_named(block)
        else:
            table = loot_simple(block)
        write_json(DATA / "loot_tables" / "blocks" / f"{block}.json", table)

    plushies = [M + s for s in SPECIES_ORDER]
    write_json(DATA / "tags" / "items" / "plushies.json", {"replace": False, "values": plushies})
    write_json(DATA / "tags" / "blocks" / "plushies.json", {"replace": False, "values": plushies})
    mc = ROOT / "data" / "minecraft" / "tags" / "blocks"
    write_json(mc / "mineable" / "axe.json", {"replace": False, "values": [
        M + "plushie_workshop", M + "plushie_shelf", M + "display_stand", M + "display_cabinet"]})
    write_json(mc / "mineable" / "pickaxe.json", {"replace": False, "values": [
        M + "golden_collector_trophy", M + "rainbow_collector_trophy"]})
    for name, adv in advancements().items():
        write_json(DATA / "advancements" / f"{name}.json", adv)
    write_json(ASSETS / "lang" / "en_us.json", lang(variant_ids))


SPECIES_TEXT = {
    "strawberry_bunny": ("Strawberry Bunny", "A bubbly bunny in a berry-sweet hood. Smells faintly of strawberry jam.",
                         ["%s wiggles its strawberry hood happily!", "%s gives you a soft, berry-scented hug.",
                          "%s hops in place. Boing, boing!"],
                         {"classic": "Classic", "vanilla": "Vanilla Cream", "blueberry": "Blueberry"}),
    "pink_teddy_bear": ("Pink Teddy Bear", "The classic cuddle companion, always ready for a big squeeze.",
                        ["%s squishes into a big warm hug.", "%s's little bow bounces as it squeaks!",
                         "You feel cosier just holding %s."],
                        {"pink": "Bubblegum", "honey": "Honey", "snow": "Snowflake"}),
    "lavender_kitty": ("Lavender Kitty", "A dreamy kitty who naps in lavender fields and purrs in pastel.",
                       ["%s purrs softly and flicks its tail.", "%s does a happy little wiggle. Mrrp!",
                        "%s kneads the air with tiny paws."],
                       {"lavender": "Lavender", "peach": "Peach", "midnight": "Midnight"}),
    "cloud_puppy": ("Cloud Puppy", "Fluffy as a summer cloud and twice as bouncy. Very good dog.",
                    ["%s wags its fluffy cloud tail!", "%s bounces like a puff of cotton candy.",
                     "%s pants happily. Who's a good cloud?"],
                    {"sky": "Sky", "sunset": "Sunset", "storm": "Storm"}),
    "cherry_blossom_fox": ("Cherry Blossom Fox", "A graceful fox who carries spring wherever it twirls.",
                           ["%s twirls in a flurry of cherry petals!", "%s's blossoms sway as it spins.",
                            "%s swishes its tail with a soft giggle."],
                           {"sakura": "Sakura", "snow": "Snow Blossom", "twilight": "Twilight"}),
    "angel_bunny": ("Angel Bunny", "A gentle guardian bunny with feathery wings and a tiny golden halo.",
                    ["%s floats up and flutters its wings!", "%s's halo sparkles warmly.",
                     "%s sends you a little blessing."],
                    {"seraph": "Seraph", "blush": "Blush", "sky": "Sky"}),
    "sleepy_panda": ("Sleepy Panda", "Has never been fully awake. Professional napper, champion snuggler.",
                     ["%s lets out the biggest little yawn...", "%s mumbles something about bamboo. Zzz...",
                      "%s hugs its pillow tighter and snores softly."],
                     {"classic": "Classic", "strawberry": "Strawberry", "mint": "Mint"}),
    "heart_bear": ("Heart Bear", "Its heart is stitched with love, and you can feel it beat when you hug it.",
                   ["%s's heart goes ba-dum, ba-dum!", "%s showers you with love!",
                    "%s squeezes its heart pillow just for you."],
                   {"rose": "Rose", "ruby": "Ruby", "lilac": "Lilac"}),
    "little_duck": ("Little Duck", "A tiny duckling with a big waddle and a flower clip.",
                    ["%s waddles in a happy circle! Peep!", "%s flaps its tiny wings.", "%s quacks a little song."],
                    {"sunny": "Sunny", "cream": "Cream", "mint": "Mint"}),
    "moon_cat": ("Moon Cat", "Born under a crescent moon. Its little moon orbits it while it dreams.",
                 ["%s twirls beneath its little moon.", "%s's eyes twinkle like starlight.",
                  "The moon above %s glows a little brighter."],
                 {"midnight": "Midnight", "dawn": "Dawn", "dusk": "Dusk"}),
    "sugarplum_dragon": ("Sugarplum Dragon", "The Sugarplum Studios mascot! Breathes sugar sparkles instead of fire.",
                         ["%s flaps its wings and puffs out sugar sparkles!", "%s lets out a tiny, adorable rawr!",
                          "%s proudly shows off its sparkly wings."],
                         {"sugarplum": "Sugarplum", "seafoam": "Seafoam", "rose_quartz": "Rose Quartz"}),
    "pastel_axolotl": ("Pastel Axolotl", "A smiley axolotl who is always happy to see you.",
                       ["%s wiggles its frilly gills!", "%s blows a happy bubble.", "%s gives you its widest smile."],
                       {"blossom": "Blossom", "mint": "Mint", "lavender": "Lavender"}),
    "baby_deer": ("Baby Deer", "A shy little fawn with a flower crown and the softest ears.",
                  ["%s nods shyly and blushes.", "%s's flower crown wobbles adorably.", "%s nuzzles your hand."],
                  {"fawn": "Fawn", "blossom": "Blossom", "frost": "Frost"}),
    "honey_bee": ("Honey Bee Plushie", "A round, buzzy friend who's as sweet as honey.",
                  ["%s buzzes around excitedly! Bzz!", "%s shares a drop of sweet honey.", "%s does a little waggle dance."],
                  {"honey": "Honey", "strawberry": "Strawberry", "mint": "Mint"}),
    "strawberry_cow": ("Strawberry Cow", "A strawberry-milk cow with a jingly bell and a berry hat.",
                       ["%s lets out a tiny 'moo~'!", "%s's little bell jingles happily.", "%s smells like strawberry milk."],
                       {"strawberry": "Strawberry", "chocolate": "Chocolate", "blueberry": "Blueberry"}),
}


def lang(variant_ids):
    L = {"itemGroup." + MODID: "Sugarplum Plushies"}
    for species in SPECIES_ORDER:
        name, desc, messages, variants = SPECIES_TEXT[species]
        L["block.%s.%s" % (MODID, species)] = name
        L["plushie.%s.%s.desc" % (MODID, species)] = desc
        for i, msg in enumerate(messages):
            L["message.%s.%s.%d" % (MODID, species, i)] = msg
        for vid in variant_ids[species][:3]:
            L["variant.%s.%s.%s" % (MODID, species, vid)] = variants[vid]
        L["variant.%s.%s.sparkle" % (MODID, species)] = "Sparkle Edition"

    blocks = {
        "plushie_workshop": "Plushie Workshop", "plushie_shelf": "Plushie Shelf", "display_stand": "Plushie Display Stand",
        "display_cabinet": "Miniature Display Cabinet", "golden_collector_trophy": "Golden Collector's Trophy",
        "rainbow_collector_trophy": "Rainbow Collector's Trophy",
    }
    for color in COLORS:
        nice = color.capitalize()
        blocks["%s_plushie_bed" % color] = "%s Plushie Bed" % nice
        blocks["%s_plushie_box" % color] = "%s Plushie Gift Box" % nice
    for key, value in blocks.items():
        L["block.%s.%s" % (MODID, key)] = value

    items = {
        "soft_fabric": ("Soft Fabric", "Pastel minky fabric for sewing plushies."),
        "plush_stuffing": ("Plush Stuffing", "Fluffy filling that makes plushies extra huggable."),
        "button_eyes": ("Button Eyes", "Shiny buttons for embroidered eyes and noses."),
        "embroidery_thread": ("Embroidery Thread", "For stitching cute faces and tiny details."),
        "satin_ribbon": ("Satin Ribbon", "For bows, collars and fancy trims."),
        "sugar_star": ("Sugar Star", "A glittering star used to sew Sparkle Edition plushies."),
        "plushie_collection_book": ("Plushie Collection Book", "Right-click to browse your plushie collection and claim rewards."),
    }
    for key, (name, tip) in items.items():
        L["item.%s.%s" % (MODID, key)] = name
        L["item.%s.%s.tooltip" % (MODID, key)] = tip

    L.update({
        "item.%s.plushie_variant_name" % MODID: "%s (%s)",
        "rarity.%s.common" % MODID: "Common",
        "rarity.%s.uncommon" % MODID: "Uncommon",
        "rarity.%s.rare" % MODID: "Rare",
        "rarity.%s.epic" % MODID: "Epic",
        "rarity.%s.sparkle" % MODID: "Sparkle Edition",
        "tooltip.%s.rarity" % MODID: "%s %s",
        "tooltip.%s.variant" % MODID: "Variant: %s",
        "tooltip.%s.plushie_hint" % MODID: "Right-click: cuddle  •  Sneak + right-click: pick up",
        "message.%s.reward_claimed" % MODID: "✦ Collection reward claimed: %s!",
        "container.%s.plushie_workshop" % MODID: "Plushie Workshop",
        "container.%s.plushie_box" % MODID: "Plushie Gift Box",
        "gui.%s.workshop.sew" % MODID: "Sew!",
        "gui.%s.workshop.select" % MODID: "Pick a plushie pattern from the list to see what you need.",
        "gui.%s.workshop.have" % MODID: "You have %s / need %s",
        "gui.%s.workshop.shift" % MODID: "Shift-click to sew up to 16 at once",
        "gui.%s.workshop.new" % MODID: "✦ Not in your collection yet!",
        "gui.%s.book.title" % MODID: "Plushie Collection",
        "gui.%s.book.species" % MODID: "Species: %s/%s",
        "gui.%s.book.variants" % MODID: "Variants: %s/%s",
        "gui.%s.book.variant_count" % MODID: "%s/%s variants collected",
        "gui.%s.book.collection" % MODID: "Plushies",
        "gui.%s.book.rewards" % MODID: "Rewards",
        "gui.%s.book.rewards_title" % MODID: "Collection Rewards",
        "gui.%s.book.claim" % MODID: "Claim",
        "gui.%s.book.claimed" % MODID: "✔ Done",
        "gui.%s.book.progress" % MODID: "Progress: %s/%s",
        "gui.%s.book.reward_list" % MODID: "Rewards:",
        "gui.%s.book.unknown" % MODID: "???",
        "gui.%s.book.hint" % MODID: "You haven't met this plushie yet. Sew it at a Plushie Workshop to add it to your collection!",
        "gui.%s.book.collected" % MODID: "Collected",
        "gui.%s.book.not_collected" % MODID: "Not collected yet",
        "gui.%s.book.sparkle_hint" % MODID: "Upgrade any colourway with a Sugar Star at the Workshop.",
        "toast.%s.unlocked" % MODID: "New plushie collected!",
        "milestone.%s.cuddle_starter" % MODID: "Cuddle Starter",
        "milestone.%s.shelf_stylist" % MODID: "Shelf Stylist",
        "milestone.%s.sweet_collector" % MODID: "Sweet Collector",
        "milestone.%s.plushie_curator" % MODID: "Plushie Curator",
        "milestone.%s.sugarplum_master" % MODID: "Sugarplum Master",
        "milestone.%s.sparkle_hunter" % MODID: "Sparkle Hunter",
        "milestone.%s.grand_collector" % MODID: "Grand Collector",
        "milestone.%s.requirement.species" % MODID: "Collect %s different plushies",
        "milestone.%s.requirement.variants" % MODID: "Collect %s plushie variants",
        "milestone.%s.requirement.sparkles" % MODID: "Collect %s Sparkle Editions",
        "advancements.%s.root.title" % MODID: "Sugarplum Plushies",
        "advancements.%s.root.description" % MODID: "Get some Soft Fabric or a Plushie Workshop",
        "advancements.%s.first_plushie.title" % MODID: "A New Friend",
        "advancements.%s.first_plushie.description" % MODID: "Sew your very first plushie",
        "advancements.%s.collection_book.title" % MODID: "Dear Diary...",
        "advancements.%s.collection_book.description" % MODID: "Make a Plushie Collection Book",
        "advancements.%s.sparkle_edition.title" % MODID: "Sparkle Edition!",
        "advancements.%s.sparkle_edition.description" % MODID: "Upgrade a plushie with a Sugar Star",
        "advancements.%s.sugarplum_dragon.title" % MODID: "Here Be (Cute) Dragons",
        "advancements.%s.sugarplum_dragon.description" % MODID: "Sew the Sugarplum Dragon",
        "advancements.%s.all_species.title" % MODID: "Sugarplum Master",
        "advancements.%s.all_species.description" % MODID: "Collect all 15 plushies",
        "advancements.%s.complete_collection.title" % MODID: "Every Last Stitch",
        "advancements.%s.complete_collection.description" % MODID: "Collect all 60 plushie variants",
    })
    return L
