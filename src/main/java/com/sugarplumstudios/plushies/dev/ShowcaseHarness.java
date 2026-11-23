package com.sugarplumstudios.plushies.dev;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.PastelColor;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieBoxBlock;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.block.PlushieInteractions;
import com.sugarplumstudios.plushies.block.entity.PlushieBlockEntity;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.client.ClientAccess;
import com.sugarplumstudios.plushies.client.screen.CollectionBookScreen;
import com.sugarplumstudios.plushies.client.screen.PlushieWorkshopScreen;
import com.sugarplumstudios.plushies.collection.CollectionEvents;
import com.sugarplumstudios.plushies.collection.CollectionMilestone;
import com.sugarplumstudios.plushies.collection.PlushieCollection;
import com.sugarplumstudios.plushies.config.PlushieConfig;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Development only (stripped from the release jar). Launch the client with
 * {@code ./gradlew runClient -Pshowcase} and it builds a flat creative world, sets out the
 * plushies, their colourways and the furniture, screenshots each scene plus the collection
 * book and the Plushie Workshop into {@code run/screenshots}, then quits.
 */
@Mod.EventBusSubscriber(modid = SugarplumPlushies.MOD_ID, value = Dist.CLIENT)
public final class ShowcaseHarness {
    private static final boolean ENABLED = Boolean.getBoolean("sugarplumplushies.showcase");
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int Y = -60;
    private static final int FACE_SOUTH = 8;
    private static final int SHOT_TICKS = 80;
    private static final int TRIGGER_AT = 70;
    private static final PlushieSpecies[] ALL = PlushieSpecies.values();

    /**
     * One camera position. {@code setup} runs on the server when the camera arrives, {@code trigger}
     * a few ticks before the screenshot (to catch a cuddle mid-animation) and {@code after} once it is taken.
     */
    private record Shot(String name, double x, double y, double z, float yaw, float pitch,
                        @Nullable Consumer<ServerLevel> setup, @Nullable Consumer<ServerLevel> trigger,
                        @Nullable Runnable after) {
        Shot(String name, double x, double y, double z, float yaw, float pitch) {
            this(name, x, y, z, yaw, pitch, null, null, null);
        }
    }

    private static boolean worldRequested;
    private static boolean built;
    private static int tick;
    private static final List<Shot> SHOTS = new ArrayList<>();
    private static int shotIndex;
    private static int guiStage;

    private ShowcaseHarness() {
    }

    private static void createWorld(Minecraft mc) {
        worldRequested = true;
        mc.getWindow().setWindowed(WIDTH, HEIGHT);
        mc.options.hideGui = true;
        LevelSettings settings = new LevelSettings("Plushie Showcase", GameType.CREATIVE, false, Difficulty.PEACEFUL, true,
                new GameRules(), WorldDataConfiguration.DEFAULT);
        mc.createWorldOpenFlows().createFreshLevel("plushie_showcase_" + System.currentTimeMillis(), settings,
                new WorldOptions(1L, false, false),
                access -> access.registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions());
    }

    private static void grab(Minecraft mc, String name) {
        Screenshot.grab(mc.gameDirectory, "plushies_" + name + ".png", mc.getMainRenderTarget(), msg -> {
        });
    }

    @Nullable
    private static ServerPlayer serverPlayer(MinecraftServer server) {
        return server.getPlayerList().getPlayers().isEmpty() ? null : server.getPlayerList().getPlayers().get(0);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (!ENABLED || event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        // on a fresh run folder the first menu is the accessibility onboarding screen, not the title screen
        if (!worldRequested && mc.level == null && mc.screen != null && mc.getOverlay() == null) {
            mc.options.onboardAccessibility = false;
            mc.options.save();
            createWorld(mc);
            return;
        }
        MinecraftServer server = mc.getSingleplayerServer();
        if (mc.player == null || mc.level == null || server == null) {
            return;
        }
        tick++;
        if (tick == 40 && !built) {
            built = true;
            server.execute(() -> {
                build(server.overworld());
                ServerPlayer player = serverPlayer(server);
                if (player != null) {
                    fillCollection(player);
                }
            });
            mc.player.getAbilities().flying = true;
            return;
        }
        if (tick < 160) {
            return;
        }
        int phase = tick - 160;
        if (shotIndex < SHOTS.size()) {
            Shot shot = SHOTS.get(shotIndex);
            int step = phase % SHOT_TICKS;
            if (step == 0) {
                if (shot.setup != null) {
                    server.execute(() -> shot.setup.accept(server.overworld()));
                }
                mc.player.getAbilities().flying = true;
                mc.player.moveTo(shot.x, shot.y, shot.z, shot.yaw, shot.pitch);
                mc.player.setDeltaMovement(0, 0, 0);
            } else if (step == TRIGGER_AT && shot.trigger != null) {
                server.execute(() -> shot.trigger.accept(server.overworld()));
            } else if (step == SHOT_TICKS - 1) {
                grab(mc, shot.name);
                if (shot.after != null) {
                    shot.after.run();
                }
                shotIndex++;
            }
            return;
        }
        guiScreens(mc, server);
    }

    // ------------------------------------------------------------------ screens

    private static void click(Minecraft mc, double x, double y) {
        if (mc.screen != null) {
            mc.screen.mouseClicked(x, y, 0);
        }
    }

    private static void guiScreens(Minecraft mc, MinecraftServer server) {
        guiStage++;
        int bookLeft = 0;
        int bookTop = 0;
        if (mc.screen != null) {
            bookLeft = (mc.screen.width - 256) / 2;
            bookTop = (mc.screen.height - 180) / 2;
        }
        switch (guiStage) {
            case 1 -> {
                server.execute(() -> server.overworld().setDayTime(6000));
                GLFW.glfwSetCursorPos(mc.getWindow().getWindow(), 2, 2);
                mc.getToasts().clear();
                mc.setScreen(new CollectionBookScreen());
            }
            // select the Sugarplum Dragon: grid cell 10 is row 2, column 0
            case 10 -> click(mc, bookLeft + 16 + 9, bookTop + 28 + 2 * 27 + 9);
            case 30 -> grab(mc, "book");
            // the Rewards tab
            case 35 -> click(mc, bookLeft + 16 + 50 + 4 + 25, bookTop + 150 + 7);
            case 55 -> grab(mc, "book_rewards");
            case 60 -> {
                mc.setScreen(null);
                mc.player.moveTo(WORKSHOP_X + 0.5, Y, 2.5, 180, 20);
                mc.player.setDeltaMovement(0, 0, 0);
            }
            case 65 -> {
                mc.player.setDeltaMovement(0, 0, 0);
                server.execute(() -> {
                    ServerPlayer player = serverPlayer(server);
                    if (player != null) {
                        giveWorkshopMaterials(player);
                        BlockPos workshop = new BlockPos(WORKSHOP_X, Y, 0);
                        BlockState state = server.overworld().getBlockState(workshop);
                        player.openMenu(state.getMenuProvider(server.overworld(), workshop));
                    }
                });
            }
            // the Angel Bunny's classic pattern: recipe 20 is row 4, column 0 of the pattern grid
            case 75 -> {
                if (mc.screen instanceof PlushieWorkshopScreen screen) {
                    int left = (screen.width - 236) / 2;
                    int top = (screen.height - 222) / 2;
                    click(mc, left + 10 + 9, top + 20 + 4 * 18 + 9);
                }
                ClientAccess.handleUnlock(PlushieSpecies.ANGEL_BUNNY.ordinal(), 0);
            }
            case 95 -> grab(mc, "workshop");
            case 110 -> mc.stop();
            default -> {
            }
        }
    }

    // ------------------------------------------------------------------ collection

    /** Twelve plushies collected, some colourways and three Sparkle Editions, two rewards claimed. */
    private static void fillCollection(ServerPlayer player) {
        for (int s = 0; s < 12; s++) {
            PlushieCollection.unlock(player, ALL[s], 0);
        }
        for (int s : new int[]{0, 1, 2, 4, 6, 7}) {
            PlushieCollection.unlock(player, ALL[s], 1);
        }
        for (int s : new int[]{0, 1, 5}) {
            PlushieCollection.unlock(player, ALL[s], 2);
        }
        for (int s : new int[]{0, 5, 10}) {
            PlushieCollection.unlock(player, ALL[s], PlushieSpecies.SPARKLE);
        }
        PlushieCollection.setClaimed(player, CollectionMilestone.CUDDLE_STARTER.bit() | CollectionMilestone.SHELF_STYLIST.bit());
        CollectionEvents.sync(player);
    }

    private static void giveWorkshopMaterials(ServerPlayer player) {
        player.getInventory().clearContent();
        player.getInventory().add(new ItemStack(ModItems.SOFT_FABRIC.get(), 24));
        player.getInventory().add(new ItemStack(ModItems.PLUSH_STUFFING.get(), 16));
        player.getInventory().add(new ItemStack(ModItems.BUTTON_EYES.get(), 8));
        player.getInventory().add(new ItemStack(ModItems.EMBROIDERY_THREAD.get(), 12));
        player.getInventory().add(new ItemStack(ModItems.SATIN_RIBBON.get(), 6));
        player.getInventory().add(new ItemStack(Items.FEATHER, 8));
        player.getInventory().add(new ItemStack(Items.GOLD_INGOT, 2));
        player.getInventory().add(new ItemStack(ModItems.SUGAR_STAR.get(), 2));
        player.getInventory().add(new ItemStack(Items.SWEET_BERRIES, 6));
    }

    // ------------------------------------------------------------------ placing

    private static void block(ServerLevel level, int x, int y, int z, Block block) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState(), Block.UPDATE_ALL);
    }

    private static void fill(ServerLevel level, int x0, int y0, int z0, int x1, int y1, int z1, Block block) {
        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                for (int z = z0; z <= z1; z++) {
                    block(level, x, y, z, block);
                }
            }
        }
    }

    private static BlockPos plushie(ServerLevel level, int x, int y, int z, PlushieSpecies species, int variant, int rotation) {
        BlockPos pos = new BlockPos(x, y, z);
        level.setBlock(pos, ModBlocks.PLUSHIES.get(species).get().defaultBlockState()
                .setValue(PlushieBlock.VARIANT, variant).setValue(PlushieBlock.ROTATION, rotation & 15), Block.UPDATE_ALL);
        return pos;
    }

    private static void named(ServerLevel level, BlockPos pos, String name) {
        if (level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie) {
            plushie.setCustomName(Component.literal(name));
        }
    }

    private static void display(ServerLevel level, int x, int y, int z, Block block, Direction facing, ItemStack... plushies) {
        BlockPos pos = new BlockPos(x, y, z);
        level.setBlock(pos, block.defaultBlockState().setValue(PlushieDisplayBlock.FACING, facing), Block.UPDATE_ALL);
        if (level.getBlockEntity(pos) instanceof PlushieDisplayBlockEntity display) {
            for (int i = 0; i < plushies.length; i++) {
                display.setItem(i, plushies[i]);
            }
        }
    }

    private static void facing(ServerLevel level, int x, int y, int z, Block block, Direction facing) {
        level.setBlock(new BlockPos(x, y, z), block.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing), Block.UPDATE_ALL);
    }

    private static void box(ServerLevel level, int x, int y, int z, PastelColor color, Direction facing) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = ModBlocks.BOXES.get(color).get().defaultBlockState();
        level.setBlock(pos, state.setValue(PlushieBoxBlock.FACING, facing), Block.UPDATE_ALL);
    }

    private static ItemStack p(PlushieSpecies species, int variant) {
        return PlushieItem.create(species, variant);
    }

    /** Cuddles a placed plushie exactly as a right-click does: animation, sound and particles. */
    private static void cuddle(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof PlushieBlock block && level.getBlockEntity(pos) instanceof PlushieBlockEntity plushie) {
            PlushieSpecies species = block.getSpecies();
            level.blockEvent(pos, block, PlushieInteractions.EVENT_CUDDLE, 0);
            Vec3 at = Vec3.atBottomCenterOf(pos).add(0.0D, species.height() / 16.0D * 0.8D, 0.0D);
            PlushieInteractions.react(level, pos, at, species, state.getValue(PlushieBlock.VARIANT), null, plushie.getDisplayName());
        }
    }

    // ------------------------------------------------------------------ scenes

    private static final int WORKSHOP_X = 107;

    private static void build(ServerLevel level) {
        level.setDayTime(6000);
        level.setWeatherParameters(6000, 0, false, false);
        level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, level.getServer());
        lineup(level);
        colourways(level);
        bedroom(level);
        cuddles(level);
        names(level);
        furniture(level);
        night(level);
    }

    /** All fifteen plushies in their first colourway, on two tiers in front of a pink wall. */
    private static void lineup(ServerLevel level) {
        fill(level, -2, Y - 1, -2, 16, Y - 1, 4, Blocks.WHITE_WOOL);
        fill(level, -2, Y, -1, 16, Y + 4, -1, Blocks.PINK_WOOL);
        fill(level, -1, Y, 0, 15, Y, 0, Blocks.SMOOTH_QUARTZ);
        for (int i = 0; i < 8; i++) {
            plushie(level, i * 2, Y + 1, 0, ALL[i], 0, FACE_SOUTH);
        }
        for (int i = 8; i < 15; i++) {
            plushie(level, (i - 8) * 2 + 1, Y, 1, ALL[i], 0, FACE_SOUTH);
        }
        SHOTS.add(new Shot("lineup", 7.5, Y + 1.7, 7.2, 180, 10));
    }

    /** All sixty variants: one column per plushie, one row per colourway, Sparkle Editions in front. */
    private static void colourways(ServerLevel level) {
        int x0 = 24;
        fill(level, x0 - 2, Y - 1, -2, x0 + 16, Y - 1, 6, Blocks.WHITE_CONCRETE);
        fill(level, x0 - 2, Y, -2, x0 + 16, Y + 3, -2, Blocks.LIGHT_BLUE_WOOL);
        for (int s = 0; s < ALL.length; s++) {
            for (int v = 0; v < PlushieSpecies.VARIANT_COUNT; v++) {
                plushie(level, x0 + s, Y, v, ALL[s], v, FACE_SOUTH);
            }
        }
        SHOTS.add(new Shot("colourways", x0 + 7.5, Y + 4.6, 10.2, 180, 30));
    }

    /** A pastel bedroom with every kind of furniture in use. */
    private static void bedroom(ServerLevel level) {
        int x0 = 50;
        fill(level, x0 - 1, Y - 1, -1, x0 + 10, Y - 1, 8, Blocks.BIRCH_PLANKS);
        fill(level, x0 - 1, Y, -1, x0 + 10, Y + 4, -1, Blocks.PINK_WOOL);
        fill(level, x0 - 1, Y, -1, x0 - 1, Y + 4, 8, Blocks.PINK_WOOL);
        fill(level, x0 + 7, Y + 1, -1, x0 + 8, Y + 2, -1, Blocks.GLASS);
        fill(level, x0 - 1, Y + 4, -1, x0 + 10, Y + 4, -1, Blocks.WHITE_WOOL);
        // wall shelves
        display(level, x0 + 1, Y + 2, 0, ModBlocks.PLUSHIE_SHELF.get(), Direction.SOUTH,
                p(PlushieSpecies.STRAWBERRY_BUNNY, 0), p(PlushieSpecies.LAVENDER_KITTY, 0));
        display(level, x0 + 3, Y + 2, 0, ModBlocks.PLUSHIE_SHELF.get(), Direction.SOUTH,
                p(PlushieSpecies.LITTLE_DUCK, 0), p(PlushieSpecies.HONEY_BEE, 0));
        display(level, x0 + 5, Y + 2, 0, ModBlocks.PLUSHIE_SHELF.get(), Direction.SOUTH,
                p(PlushieSpecies.BABY_DEER, 0), p(PlushieSpecies.PASTEL_AXOLOTL, 0));
        // cabinet against the west wall, trophy on top
        display(level, x0, Y, 2, ModBlocks.DISPLAY_CABINET.get(), Direction.EAST,
                p(PlushieSpecies.CLOUD_PUPPY, 0), p(PlushieSpecies.CHERRY_BLOSSOM_FOX, 0),
                p(PlushieSpecies.STRAWBERRY_COW, 0), p(PlushieSpecies.HEART_BEAR, 0));
        facing(level, x0, Y + 1, 2, ModBlocks.GOLDEN_COLLECTOR_TROPHY.get(), Direction.EAST);
        display(level, x0, Y, 4, ModBlocks.DISPLAY_CABINET.get(), Direction.EAST,
                p(PlushieSpecies.PINK_TEDDY_BEAR, 1), p(PlushieSpecies.LAVENDER_KITTY, 2),
                p(PlushieSpecies.CLOUD_PUPPY, 3), p(PlushieSpecies.LITTLE_DUCK, 1));
        facing(level, x0, Y + 1, 4, ModBlocks.RAINBOW_COLLECTOR_TROPHY.get(), Direction.EAST);
        // beds
        display(level, x0 + 2, Y, 1, ModBlocks.BEDS.get(PastelColor.PINK).get(), Direction.SOUTH, p(PlushieSpecies.SLEEPY_PANDA, 0));
        display(level, x0 + 3, Y, 1, ModBlocks.BEDS.get(PastelColor.LAVENDER).get(), Direction.SOUTH, p(PlushieSpecies.MOON_CAT, 0));
        display(level, x0 + 4, Y, 1, ModBlocks.BEDS.get(PastelColor.MINT).get(), Direction.SOUTH, p(PlushieSpecies.SLEEPY_PANDA, 2));
        // the dragon on a turning display stand
        display(level, x0 + 5, Y, 4, ModBlocks.DISPLAY_STAND.get(), Direction.SOUTH, p(PlushieSpecies.SUGARPLUM_DRAGON, 0));
        // rug and loose plushies
        fill(level, x0 + 2, Y, 3, x0 + 4, Y, 5, Blocks.PINK_CARPET);
        plushie(level, x0 + 7, Y, 4, PlushieSpecies.ANGEL_BUNNY, 0, 7);
        plushie(level, x0 + 6, Y, 6, PlushieSpecies.PINK_TEDDY_BEAR, 0, 9);
        plushie(level, x0 + 3, Y + 1, 4, PlushieSpecies.STRAWBERRY_BUNNY, 3, FACE_SOUTH);
        // gift boxes and the workshop by the window
        box(level, x0 + 9, Y, 1, PastelColor.PINK, Direction.WEST);
        box(level, x0 + 9, Y, 2, PastelColor.MINT, Direction.WEST);
        box(level, x0 + 9, Y + 1, 1, PastelColor.SKY, Direction.WEST);
        box(level, x0 + 9, Y, 3, PastelColor.LAVENDER, Direction.WEST);
        facing(level, x0 + 7, Y, 0, ModBlocks.PLUSHIE_WORKSHOP.get(), Direction.SOUTH);
        SHOTS.add(new Shot("bedroom", x0 + 10.2, Y + 2.5, 9.4, 135, 22));
        SHOTS.add(new Shot("bedroom_closeup", x0 + 6.6, Y + 1.7, 6.9, 150, 20));
    }

    /** Three plushies caught mid-cuddle: petals, a shower of hearts and music notes. */
    private static void cuddles(ServerLevel level) {
        int x0 = 74;
        block(level, x0 - 1, Y, 0, Blocks.PINK_TULIP);
        block(level, x0 + 3, Y, 0, Blocks.ALLIUM);
        block(level, x0 + 4, Y, 1, Blocks.OXEYE_DAISY);
        block(level, x0 - 1, Y, 2, Blocks.CORNFLOWER);
        BlockPos fox = plushie(level, x0, Y, 1, PlushieSpecies.CHERRY_BLOSSOM_FOX, 0, 7);
        BlockPos bear = plushie(level, x0 + 1, Y, 1, PlushieSpecies.HEART_BEAR, 0, FACE_SOUTH);
        BlockPos duck = plushie(level, x0 + 2, Y, 1, PlushieSpecies.LITTLE_DUCK, 0, 9);
        SHOTS.add(new Shot("cuddle", x0 + 1.5, Y + 1.15, 3.7, 180, 14, null, l -> {
            cuddle(l, fox);
            cuddle(l, bear);
            cuddle(l, duck);
        }, null));
    }

    /** Named plushies with their name plates showing. */
    private static void names(ServerLevel level) {
        int x0 = 84;
        fill(level, x0 - 2, Y - 1, -1, x0 + 4, Y - 1, 3, Blocks.MOSS_BLOCK);
        named(level, plushie(level, x0, Y, 1, PlushieSpecies.PINK_TEDDY_BEAR, 0, 7), "Mochi");
        named(level, plushie(level, x0 + 1, Y, 1, PlushieSpecies.CLOUD_PUPPY, 1, FACE_SOUTH), "Biscuit");
        named(level, plushie(level, x0 + 2, Y, 1, PlushieSpecies.STRAWBERRY_BUNNY, 0, 9), "Princess Puff");
        SHOTS.add(new Shot("names", x0 + 1.5, Y + 1.25, 3.9, 180, 12,
                l -> PlushieConfig.CLIENT.nameplates.set(PlushieConfig.NameplateMode.ALWAYS), null,
                () -> PlushieConfig.CLIENT.nameplates.set(PlushieConfig.NameplateMode.LOOKING)));
    }

    /** Every furniture block in a row, each one in use. */
    private static void furniture(ServerLevel level) {
        int x0 = 94;
        fill(level, x0 - 2, Y - 1, -1, x0 + 15, Y - 1, 6, Blocks.SMOOTH_QUARTZ);
        fill(level, x0 - 2, Y, -1, x0 + 15, Y + 3, -1, Blocks.PINK_WOOL);
        display(level, x0, Y + 1, 0, ModBlocks.PLUSHIE_SHELF.get(), Direction.SOUTH,
                p(PlushieSpecies.STRAWBERRY_BUNNY, 1), p(PlushieSpecies.HONEY_BEE, 1));
        display(level, x0 + 1, Y, 0, ModBlocks.DISPLAY_STAND.get(), Direction.SOUTH, p(PlushieSpecies.CHERRY_BLOSSOM_FOX, 1));
        PastelColor[] colors = PastelColor.values();
        PlushieSpecies[] sleepers = {PlushieSpecies.SLEEPY_PANDA, PlushieSpecies.BABY_DEER, PlushieSpecies.PASTEL_AXOLOTL, PlushieSpecies.CLOUD_PUPPY};
        for (int i = 0; i < colors.length; i++) {
            display(level, x0 + 2 + i, Y, 0, ModBlocks.BEDS.get(colors[i]).get(), Direction.SOUTH, p(sleepers[i], i % 3));
        }
        display(level, x0 + 6, Y, 0, ModBlocks.DISPLAY_CABINET.get(), Direction.SOUTH,
                p(PlushieSpecies.MOON_CAT, 1), p(PlushieSpecies.ANGEL_BUNNY, 1),
                p(PlushieSpecies.HEART_BEAR, 2), p(PlushieSpecies.STRAWBERRY_COW, 1));
        for (int i = 0; i < colors.length; i++) {
            box(level, x0 + 7 + i, Y, 0, colors[i], Direction.SOUTH);
        }
        facing(level, x0 + 11, Y, 0, ModBlocks.GOLDEN_COLLECTOR_TROPHY.get(), Direction.SOUTH);
        facing(level, x0 + 12, Y, 0, ModBlocks.RAINBOW_COLLECTOR_TROPHY.get(), Direction.SOUTH);
        facing(level, WORKSHOP_X, Y, 0, ModBlocks.PLUSHIE_WORKSHOP.get(), Direction.SOUTH);
        SHOTS.add(new Shot("furniture", x0 + 6.5, Y + 1.9, 6.4, 180, 12));
    }

    /** At night: the glowing Moon Cat and Angel Bunny with Sparkle Editions around them. */
    private static void night(ServerLevel level) {
        int x0 = 120;
        block(level, x0 - 2, Y, 0, Blocks.LILY_OF_THE_VALLEY);
        block(level, x0 + 5, Y, 1, Blocks.AZURE_BLUET);
        plushie(level, x0, Y, 1, PlushieSpecies.HEART_BEAR, PlushieSpecies.SPARKLE, 7);
        plushie(level, x0 + 1, Y, 0, PlushieSpecies.ANGEL_BUNNY, 0, 7);
        plushie(level, x0 + 2, Y, 0, PlushieSpecies.MOON_CAT, 0, FACE_SOUTH);
        plushie(level, x0 + 3, Y, 0, PlushieSpecies.SUGARPLUM_DRAGON, PlushieSpecies.SPARKLE, 9);
        plushie(level, x0 + 4, Y, 1, PlushieSpecies.PINK_TEDDY_BEAR, PlushieSpecies.SPARKLE, 9);
        plushie(level, x0 + 2, Y, 2, PlushieSpecies.MOON_CAT, 2, FACE_SOUTH);
        SHOTS.add(new Shot("night", x0 + 2.5, Y + 1.35, 4.6, 180, 12, l -> {
            l.setDayTime(18000);
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().options.gamma().set(1.0D));
        }, null, () -> Minecraft.getInstance().options.gamma().set(0.5D)));
    }
}
