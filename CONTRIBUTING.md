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

- `common/`: the decorations (`block/`, `catalog/`), items, the collection book and its tracking, seasons, config,
  network packets and the client screen. Nearly all changes happen here.
- `fabric/` and `neoforge/` (`forge/` on 1.20.1): entry points that register content, packets, the creative tab,
  block tints and events through the `platform/` services. The 1.20.1 Forge module also holds the GameTests and the
  showcase harness (development only, left out of the jar).

## Decorations and assets

Decorations are defined once, in `tools/clgen/`. Never edit the generated textures, models, blockstates, loot tables,
recipes, tags, language file or `DecorCatalog.java` by hand:

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
scripts/server-smoke-test.sh 26.3 fabric               # a dev server that places every decoration
```

If you change gameplay, try it on every workspace you touched, for Fabric and NeoForge/Forge.

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
