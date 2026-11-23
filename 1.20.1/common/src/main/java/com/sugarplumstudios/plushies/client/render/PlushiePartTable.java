package com.sugarplumstudios.plushies.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.math.Axis;
import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Animated sub-parts (wings, tails, halos, gills...) of each plushie, read from
 * {@code assets/sugarplumplushies/plushie_parts.json}, which the asset generator
 * writes alongside the part models so pivots always match the geometry.
 */
public final class PlushiePartTable {
    private static final String PATH = "/assets/" + SugarplumPlushies.MOD_ID + "/plushie_parts.json";

    public record Part(String name, float pivotX, float pivotY, float pivotZ, @Nullable Axis axis,
                       float amplitude, float speed, float phase, float boost,
                       float bobAmplitude, float bobSpeed, float spin) {
    }

    private static final Map<PlushieSpecies, List<Part>> PARTS = load();

    private PlushiePartTable() {
    }

    public static List<Part> parts(PlushieSpecies species) {
        return PARTS.getOrDefault(species, List.of());
    }

    private static Map<PlushieSpecies, List<Part>> load() {
        Map<PlushieSpecies, List<Part>> map = new EnumMap<>(PlushieSpecies.class);
        try (InputStream in = PlushiePartTable.class.getResourceAsStream(PATH)) {
            if (in == null) {
                SugarplumPlushies.LOGGER.warn("Missing {} - plushies will render without animated parts", PATH);
                return map;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            for (PlushieSpecies species : PlushieSpecies.values()) {
                if (!root.has(species.id())) {
                    continue;
                }
                List<Part> parts = new ArrayList<>();
                JsonArray array = root.getAsJsonArray(species.id());
                for (JsonElement element : array) {
                    parts.add(parse(element.getAsJsonObject()));
                }
                map.put(species, List.copyOf(parts));
            }
        } catch (Exception e) {
            SugarplumPlushies.LOGGER.error("Failed to read {}", PATH, e);
        }
        return map;
    }

    private static Part parse(JsonObject json) {
        JsonArray pivot = GsonHelper.getAsJsonArray(json, "pivot");
        Axis axis = switch (GsonHelper.getAsString(json, "axis", "none")) {
            case "x" -> Axis.XP;
            case "y" -> Axis.YP;
            case "z" -> Axis.ZP;
            default -> null;
        };
        return new Part(
                GsonHelper.getAsString(json, "name"),
                pivot.get(0).getAsFloat(), pivot.get(1).getAsFloat(), pivot.get(2).getAsFloat(),
                axis,
                GsonHelper.getAsFloat(json, "amplitude", 0.0F),
                GsonHelper.getAsFloat(json, "speed", 0.1F),
                GsonHelper.getAsFloat(json, "phase", 0.0F),
                GsonHelper.getAsFloat(json, "boost", 1.5F),
                GsonHelper.getAsFloat(json, "bob_amplitude", 0.0F),
                GsonHelper.getAsFloat(json, "bob_speed", 0.1F),
                GsonHelper.getAsFloat(json, "spin", 0.0F));
    }
}
