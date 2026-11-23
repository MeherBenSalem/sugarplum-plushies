# Sugarplum Plushies 1.0.0: release files

One jar, for Minecraft 1.20.1 on Forge. Install it on both the client and the server. On CurseForge, tick Minecraft 1.20.1, Forge and Java 17. It needs no other mods.

| File | Minecraft | Loader | Java | Required dependencies |
|---|---|---|---|---|
| `sugarplumplushies-forge-1.20.1-1.0.0.jar` | 1.20.1 | Forge 47 or newer | 17 | None |

Built by the Build release jar workflow (`.github/workflows/build-release.yml`), which runs `./gradlew build` and commits the jar here only after CI passed on the same commit:

- **Jar checks:** the file name, `Implementation-Version` and the `mods.toml` version are 1.0.0, the classes are SRG-mapped for Forge, and the showcase harness is left out.
- **GameTests:** `./gradlew runGameTestServer` runs the mod's 6 in-game tests.
- **Server boot:** a real Forge 1.20.1 dedicated server with this jar in its `mods` folder starts, places all 29 blocks with `/setblock` (each of the 15 plushies in all four variants, 74 placements), runs for 20 seconds and stops, with no crash and no error naming the mod (`scripts/server-boot-test.sh`).
- **Client launch:** a dev client starts under Xvfb, builds the showcase world, takes its 11 screenshots and quits, with no crash and no error naming the mod.
