#!/usr/bin/env bash
# Boots a real Forge 1.20.1 dedicated server with one built Sugarplum Plushies jar in its mods folder, places every
# block of the mod with /setblock from a datapack (each plushie in all four variants), lets the world run for 20
# seconds and stops. Fails if the server crashes, the mod does not load, the datapack function does not load (an
# unknown block id or block state makes it fail to parse), or the log has an error that names the mod.
#   usage: scripts/server-boot-test.sh <path to the mod jar>
set -u
JAR="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MC=$(grep '^minecraft_version=' "$ROOT/1.20.1/gradle.properties" | cut -d= -f2)
FORGE=$(grep '^forge_version=' "$ROOT/1.20.1/gradle.properties" | cut -d= -f2)
STATES="$ROOT/1.20.1/common/src/main/resources/assets/sugarplumplushies/blockstates"
SERVER="$ROOT/build/boot-test-server"
rm -rf "$SERVER"; mkdir -p "$SERVER/mods"
cd "$SERVER"

curl -fsSL -o installer.jar "https://maven.minecraftforge.net/net/minecraftforge/forge/$MC-$FORGE/forge-$MC-$FORGE-installer.jar"
java -jar installer.jar --installServer > install.log 2>&1 || { tail -40 install.log; exit 1; }
cp "$JAR" mods/

echo "eula=true" > eula.txt
cat > server.properties <<PROPS
online-mode=false
function-permission-level=4
spawn-protection=0
view-distance=4
simulation-distance=4
level-type=minecraft\:flat
level-seed=20261123
initial-enabled-packs=vanilla,file/plushie_boot
PROPS
PACK="world/datapacks/plushie_boot"
mkdir -p "$PACK/data/plushie_boot/functions" "$PACK/data/minecraft/tags/functions"
echo '{"pack":{"pack_format":15,"description":"Sugarplum Plushies boot test"}}' > "$PACK/pack.mcmeta"
echo '{"values":["plushie_boot:start"]}' > "$PACK/data/minecraft/tags/functions/load.json"
printf 'forceload add 0 0 63 63\nschedule function plushie_boot:place 100t\n' > "$PACK/data/plushie_boot/functions/start.mcfunction"
# every block state to place: each plushie (its blockstate file lists variant=0..3) in all four variants, every
# other block facing south
EXPECTED=0
{
  i=0
  for f in "$STATES"/*.json; do
    id=$(basename "$f" .json)
    if grep -q '"variant=3' "$f"; then
      states="[variant=0,rotation=8] [variant=1,rotation=8] [variant=2,rotation=8] [variant=3,rotation=8]"
    elif grep -q 'facing=south' "$f"; then
      states="[facing=south]"
    else
      states="[]"
    fi
    for s in $states; do
      [ "$s" = "[]" ] && s=""
      echo "setblock $(( (i % 20) * 3 )) -60 $(( (i / 20) * 3 )) sugarplumplushies:$id$s"
      i=$((i + 1))
    done
  done
  echo "say PLUSHIE_BOOT_PLACED $i"
  echo 'schedule function plushie_boot:finish 400t'
} > "$PACK/data/plushie_boot/functions/place.mcfunction"
EXPECTED=$(grep -c '^setblock' "$PACK/data/plushie_boot/functions/place.mcfunction")
printf 'say PLUSHIE_BOOT_OK\nstop\n' > "$PACK/data/plushie_boot/functions/finish.mcfunction"

LOG="$SERVER/boot.log"
bash run.sh nogui < /dev/null > "$LOG" 2>&1 &
PID=$!
result=timeout
for _ in $(seq 1 120); do
  sleep 5
  if grep -q "PLUSHIE_BOOT_OK" "$LOG"; then result=ok; break; fi
  if grep -q -E "This crash report has been saved|Encountered an unexpected exception|Failed to start the minecraft server|Failed to load function plushie_boot" "$LOG"; then result=crash; break; fi
  if ! kill -0 $PID 2>/dev/null; then result=exited; break; fi
done
sleep 10
pkill -P $PID 2>/dev/null; kill $PID 2>/dev/null; wait $PID 2>/dev/null

grep -n -i -E "PLUSHIE_BOOT|sugarplumplushies|Sugarplum Plushies|Done \(" "$LOG" | head -40
for f in crash-reports/*.txt; do [ -f "$f" ] && { echo "=== $f"; head -80 "$f"; }; done
grep -n -E "ERROR|Exception|Caused by" "$LOG" | head -60
echo "Result: $result"
[ "$result" = ok ] || exit 1
grep -q "This crash report has been saved" "$LOG" && exit 1
grep -q -E "Done \([0-9.]+s\)!" "$LOG" || { echo "The server never finished starting"; exit 1; }
grep -q "PLUSHIE_BOOT_PLACED $EXPECTED" "$LOG" || { echo "Not every block was placed (expected $EXPECTED)"; exit 1; }
if grep -E "ERROR|Exception|Caused by" "$LOG" | grep -q -E 'com.sugarplumstudios|sugarplumplushies([^-.a-z]|$)'; then echo "The log has errors naming Sugarplum Plushies"; exit 1; fi
echo "Server boot test passed ($EXPECTED block states placed)"
