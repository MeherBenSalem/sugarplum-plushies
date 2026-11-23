# Contributing

Thanks for helping improve Sugarplum Plushies.

## How to contribute

1. Fork the repository and create a branch from `main`.
2. Make focused changes in the relevant Minecraft version workspace (`1.20.1/`, `1.21.1/`, `26.2/` or `26.3/`).
   A gameplay change usually belongs in all four: the `common/` code is nearly identical between them, and only the
   version-specific APIs differ.
3. Build the workspace you changed (`./gradlew build` from that folder: Gradle on Java 21 for 1.20.1 and 1.21.1,
   Java 25 for 26.x), or all four with `./gradlew buildAll` from the root (the 8 loader jars land in `releases/`).
4. Open a pull request describing the change and how you tested it.

## Layout

Each workspace is a MultiLoader Gradle build:

- `common/`: the plushies (`plushie/`, `block/`, `item/`), the Plushie Workshop recipe and menu, the collection and
  its milestones, config, network messages and the client renderers, screens, particles and toast. Nearly all
  changes happen here.
- `fabric/` and `neoforge/` (`forge/` on 1.20.1): entry points that register content, messages, the creative tab,
  renderers, models and events through the `platform/` services. The 1.20.1 Forge module also holds the GameTests and
  the showcase harness (development only, left out of the jar).

## Models, textures and data

Every model, texture, blockstate, recipe, loot table, tag and the language file are generated from
`tools/assetgen/`. Never edit them by hand:

```bash
python3 tools/generate_assets.py     # writes everything into the 1.20.1 workspace and validates it
python3 tools/port_data.py 1.21.1    # copies it into the other workspaces in their format
python3 tools/port_data.py 26.2
python3 tools/port_data.py 26.3
```

## Testing in game

```bash
cd 1.20.1 && ./gradlew :forge:runGameTestServer        # the automated in-game tests
cd 1.20.1 && ./gradlew :forge:runClient -Pshowcase     # screenshots the showcase world and quits
scripts/server-smoke-test.sh 26.3 fabric               # a dev server that places every block state
```

If you change gameplay, try it on every workspace you touched, for Fabric and NeoForge/Forge. The README's in-game
test checklist covers the main features.

## Coding expectations

- Prefer small, reviewable PRs over large mixed changes.
- Match existing naming, package layout, and MultiLoader patterns.
- Keep all state on the server; the client only shows it.
- Do not commit build output, IDE metadata, or local secrets.
- When fixing a bug, say which version/loaders you verified.

## Issues

Use GitHub Issues for bugs and feature requests. Include Minecraft version, mod loader, mod version, and steps to
reproduce when reporting bugs.

## Licensing

By contributing, you agree that your contributions are licensed under the Apache License, Version 2.0, unless you
state otherwise in the pull request.
