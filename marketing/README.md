# Marketing assets: 1.0

Listing copy and artwork for CurseForge and Modrinth. Nothing here ships in the mod JAR.

## Listing

| File | Use |
| --- | --- |
| [`curseforge/description.md`](curseforge/description.md) | Full CurseForge project description (Markdown) |
| [`curseforge/summary.txt`](curseforge/summary.txt) | Short CurseForge project summary (220 characters) |
| [`modrinth/description.md`](modrinth/description.md) | Full Modrinth project body. The same copy as CurseForge, but the screenshots go in the project's Gallery tab instead of the page |
| [`modrinth/summary.txt`](modrinth/summary.txt) | Modrinth summary (220 characters; Modrinth allows 256) |
| `images/icon.png` | Project avatar on both sites |

Image links in both descriptions point at `raw.githubusercontent.com/MeherBenSalem/sugarplum-plushies/main/marketing/images/...`. **They only work for visitors once this folder is on `main` and the repository is public.** While the repository is private, upload the images to each site instead (the CurseForge description editor and the Modrinth gallery both take uploads) and replace each URL.

Both descriptions lead with the pitch, then the plushies, colourways and Sparkle Editions, cuddles, furniture, the Plushie Workshop, the Collection Book, naming, glowing plushies, crafting, configuration, everything else, downloads and compatibility, the FAQ and (CurseForge only) a gallery.

### Modrinth project settings

- **Environment:** required on the client and on the server.
- **Loaders and versions:** Fabric and Forge for Minecraft 1.20.1; Fabric and NeoForge for 1.21.1, 26.2 and 26.3.
- **Gallery:** upload `images/screenshots/*.jpg` with the captions used in the CurseForge gallery (the alt texts at the end of `curseforge/description.md`), and `images/banner.png` as the featured image.

## Adding a Minecraft version

The supported versions are listed in these places:

1. `VERSIONS` at the top of [`source/render.cjs`](source/render.cjs). One line per Minecraft version, for example `{ minecraft: '1.21.1', loaders: ['Fabric', 'NeoForge'] },`. The banner's loader pills and version line are built from it. Then rerun the script for `banner`.
2. The table under **Downloads & Compatibility** in `curseforge/description.md` and `modrinth/description.md`. Add one row per loader.
3. The FAQ answer **Is there a Fabric or NeoForge version?** in both files.

No other copy or artwork names a Minecraft version. A new release number goes in `RELEASE` in `render.cjs` and in the banner's alt text on the first line of both descriptions.

## Images

| File | Size | Use |
| --- | --- | --- |
| `images/banner.png` | 1800×600, 951 KB | Description header: the plushie bedroom, the title, "Craft, collect, display and cuddle.", the loader pill and version line |
| `images/icon.png` | 512×512, 145 KB | Project avatar: the Pink Teddy Bear in a polka-dot ring |
| `images/card-plushies.png` | 1600×900, 735 KB | All 15 plushies with their rarities; 15 plushies, 60 variants, 4 rarities, 29 blocks |
| `images/card-variants.png` | 1600×900, 693 KB | The dragon, fox, Heart Bear and Honey Bee in all four variants |
| `images/card-cuddle.png` | 1600×900, 899 KB | The cuddle capture, plus five plushies with their cuddle animations |
| `images/card-furniture.png` | 1600×900, 1306 KB | The whole bedroom, plus the shelf, stand, cabinet, bed, gift box and trophy |
| `images/card-workshop.png` | 1600×900, 797 KB | The Plushie Workshop with the Angel Bunny pattern picked |
| `images/card-book.png` | 1600×900, 876 KB | The Collection Book at the Sugarplum Dragon and its Rewards tab |
| `images/card-names.png` | 1600×900, 957 KB | Mochi, Biscuit and Princess Puff with their name plates |
| `images/card-glow.png` | 1600×900, 903 KB | The glowing plushies and trophies at dusk, with their light levels |
| `images/card-config.png` | 1600×900, 557 KB | Excerpts of `sugarplumplushies-common.toml` and `sugarplumplushies-client.toml` |
| `images/header-*.png` | 1400×120, transparent, 33–58 KB | 14 section headings for the descriptions |
| `images/divider.png` | 1400×40, transparent | Section divider |
| `images/screenshots/*.jpg` | 1920×1080, 153–464 KB | 11 captures: gallery uploads and card art |

### Where the pictures come from

- **Screenshots** are the unedited captures in `docs/screenshots/`, converted to JPEG. They were taken by the showcase harness (`./gradlew runClient -Pshowcase`, `dev/ShowcaseHarness.java`): a flat creative world in Peaceful, 1920×1080, HUD hidden. The harness builds the bedroom, the lineup, the colourway grid, the furniture row, the cuddle, naming and night scenes, fills a sample collection and opens the Collection Book and the Workshop itself, so everything in them is placed and drawn by the mod's own code. The `dev/` package is left out of the release jar.
- **Plushie and furniture renders** on the cards, banner and icon are isometric renders of the mod's own models and textures, made without Minecraft by [`source/renders.py`](source/renders.py) (which uses [`source/preview_models.py`](source/preview_models.py)). They are in `source/renders/`: every plushie in all four variants (`<plushie>@<variant>.png`) and the 14 furniture blocks.

The world shots are taken in a flat world. Captures in a generated world, or in a real build, would make the gallery and the furniture and night cards stronger: retake them, drop them into `images/screenshots/` under the same names, and adjust the `region()` crops in `render.cjs`, which are pixel regions of the 1920×1080 captures.

## Regenerating the artwork

The cards, banner, icon and headers are HTML rendered with Playwright from the screenshots, the model renders and the bundled fonts:

```bash
python marketing/source/renders.py
```

```bash
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs            # everything
NODE_PATH="$(npm root -g)" node marketing/source/render.cjs banner icon  # selected assets
```

- `renders.py` uses `tools/assetgen/core.py` for the asset paths. Everything it draws uses the mod's own textures, so it needs no Minecraft files; `SP_VANILLA` is only read if a model borrows a vanilla texture.
- Set `CHROMIUM=/path/to/chromium` to render with a Chromium other than Playwright's own download.
- `source/backdrop.jpg` is the blurred card background, a copy of `screenshots/bedroom-closeup.jpg`.
- `source/logo.png` is the mod-list logo (`src/main/resources/sugarplumplushies_logo.png`).
- `source/fonts/` holds Fredoka and Nunito with their SIL Open Font License 1.1 texts. The script uses only these files.

Plushie names, classic colourways and rarities on the cards are written at the top of `render.cjs`, copied from `plushie/PlushieSpecies.java`, the lang file and the project README. Update them when those change.

## Wording

Every number in the copy and on the cards is a 1.0.0 default from `config/PlushieConfig.java`, a recipe, a light level, a trade in `event/ModEvents.java`, a milestone in the collection code, or `plushie/PlushieSpecies.java` (15 plushies × 4 variants = 60; 29 blocks). Update the copy when those change. Call the three non-Sparkle looks **colourways**, all four **variants**, the rare one the **Sparkle Edition**, and the book the **Plushie Collection Book** (the **Collection Book** after first mention).
