package com.sugarplumstudios.plushies.gametest;

import com.sugarplumstudios.plushies.SugarplumPlushies;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import com.sugarplumstudios.plushies.block.PlushieDisplayBlock;
import com.sugarplumstudios.plushies.block.entity.PlushieDisplayBlockEntity;
import com.sugarplumstudios.plushies.collection.CollectionMilestone;
import com.sugarplumstudios.plushies.collection.PlushieCollection;
import com.sugarplumstudios.plushies.item.PlushieItem;
import com.sugarplumstudios.plushies.menu.PlushieWorkshopMenu;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.recipe.WorkshopRecipe;
import com.sugarplumstudios.plushies.registry.ModBlocks;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import java.util.List;

/** Server-side behaviour checks. Run with {@code ./gradlew runGameTestServer}. */
@GameTestHolder(SugarplumPlushies.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PlushieGameTests {
    private static final String TEMPLATE = "empty";

    private PlushieGameTests() {
    }

    private static void use(GameTestHelper helper, BlockPos relative, Player player) {
        BlockPos pos = helper.absolutePos(relative);
        BlockState state = helper.getLevel().getBlockState(pos);
        state.use(helper.getLevel(), player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.NORTH, pos, false));
    }

    @GameTest(template = TEMPLATE)
    public static void workshopRecipesLoad(GameTestHelper helper) {
        List<WorkshopRecipe> recipes = WorkshopRecipe.sorted(helper.getLevel().getRecipeManager());
        helper.assertTrue(recipes.size() == PlushieSpecies.TOTAL_VARIANTS, "Expected 60 workshop recipes, got " + recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ItemStack result = recipes.get(i).getResult();
            PlushieSpecies expected = PlushieSpecies.values()[i / PlushieSpecies.VARIANT_COUNT];
            helper.assertTrue(result.getItem() instanceof PlushieItem item && item.getSpecies() == expected
                    && PlushieItem.getVariant(result) == i % PlushieSpecies.VARIANT_COUNT, "Recipe order/result mismatch at " + i);
        }
        helper.assertTrue(helper.getLevel().getRecipeManager().byKey(SugarplumPlushies.id("plushie_workshop")).isPresent(),
                "Workshop crafting recipe missing");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void plushieCuddleRotatePickUp(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        Block block = ModBlocks.PLUSHIES.get(PlushieSpecies.MOON_CAT).get();
        helper.setBlock(pos, block.defaultBlockState().setValue(PlushieBlock.VARIANT, 2));
        Player player = helper.makeMockPlayer();

        use(helper, pos, player);
        helper.assertBlockPresent(block, pos);

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BRUSH));
        use(helper, pos, player);
        helper.assertBlockProperty(pos, PlushieBlock.ROTATION, 1);

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);
        use(helper, pos, player);
        helper.assertBlockNotPresent(block, pos);
        boolean found = false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof PlushieItem item && item.getSpecies() == PlushieSpecies.MOON_CAT
                    && PlushieItem.getVariant(stack) == 2) {
                found = true;
            }
        }
        helper.assertTrue(found, "Picked-up plushie should keep its variant");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void plushieDropsKeepVariant(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        ServerLevel level = helper.getLevel();
        BlockState state = ModBlocks.PLUSHIES.get(PlushieSpecies.SUGARPLUM_DRAGON).get().defaultBlockState()
                .setValue(PlushieBlock.VARIANT, PlushieSpecies.SPARKLE);
        level.setBlockAndUpdate(pos, state);
        List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos));
        helper.assertTrue(drops.size() == 1 && PlushieItem.getVariant(drops.get(0)) == PlushieSpecies.SPARKLE,
                "Broken Sparkle plushie should drop a Sparkle plushie, got " + drops);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void shelfDisplaysAndReturnsPlushie(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.PLUSHIE_SHELF.get().defaultBlockState().setValue(PlushieDisplayBlock.FACING, Direction.NORTH));
        Player player = helper.makeMockPlayer();
        player.setItemInHand(InteractionHand.MAIN_HAND, PlushieItem.create(PlushieSpecies.HONEY_BEE, 1));
        use(helper, pos, player);
        PlushieDisplayBlockEntity shelf = (PlushieDisplayBlockEntity) helper.getBlockEntity(pos);
        helper.assertTrue(shelf.occupiedCount() == 1, "Shelf should hold the plushie");
        helper.assertTrue(player.getMainHandItem().isEmpty(), "Plushie should leave the hand");

        use(helper, pos, player);
        helper.assertTrue(shelf.occupiedCount() == 1, "Cuddling must not remove the plushie");

        player.setShiftKeyDown(true);
        use(helper, pos, player);
        helper.assertTrue(shelf.occupiedCount() == 0, "Sneak-use should take the plushie back");
        helper.assertTrue(PlushieItem.getVariant(player.getMainHandItem()) == 1, "Returned plushie keeps its variant");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void workshopSewsFromInventory(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        player.getInventory().add(new ItemStack(ModItems.SOFT_FABRIC.get(), 3));
        player.getInventory().add(new ItemStack(ModItems.PLUSH_STUFFING.get(), 2));
        player.getInventory().add(new ItemStack(ModItems.BUTTON_EYES.get(), 1));
        player.getInventory().add(new ItemStack(ModItems.SATIN_RIBBON.get(), 1));
        player.getInventory().add(new ItemStack(Items.PINK_DYE, 1));

        PlushieWorkshopMenu menu = new PlushieWorkshopMenu(0, player.getInventory(), ContainerLevelAccess.NULL);
        int teddy = PlushieSpecies.PINK_TEDDY_BEAR.ordinal() * PlushieSpecies.VARIANT_COUNT;
        helper.assertFalse(menu.clickMenuButton(player, teddy + 1), "Honey colourway needs brown dye");
        helper.assertTrue(menu.clickMenuButton(player, teddy), "Classic teddy should be craftable");
        helper.assertTrue(player.getInventory().countItem(ModItems.SOFT_FABRIC.get()) == 0, "Fabric consumed");
        helper.assertTrue(player.getInventory().countItem(ModItems.PLUSHIES.get(PlushieSpecies.PINK_TEDDY_BEAR).get()) == 1,
                "Teddy sewn");
        helper.assertFalse(menu.clickMenuButton(player, teddy), "Second teddy lacks materials");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void collectionTracksMilestones(GameTestHelper helper) {
        Player player = helper.makeMockPlayer();
        helper.assertTrue(PlushieCollection.unlock(player, PlushieSpecies.HEART_BEAR, 0), "First unlock is new");
        helper.assertFalse(PlushieCollection.unlock(player, PlushieSpecies.HEART_BEAR, 0), "Repeat unlock is not new");
        PlushieCollection.unlock(player, PlushieSpecies.LITTLE_DUCK, PlushieSpecies.SPARKLE);
        PlushieCollection.unlock(player, PlushieSpecies.HONEY_BEE, 1);
        int[] masks = PlushieCollection.masks(player);
        helper.assertTrue(PlushieCollection.speciesCount(masks) == 3, "3 species collected");
        helper.assertTrue(PlushieCollection.variantCount(masks) == 3, "3 variants collected");
        helper.assertTrue(PlushieCollection.sparkleCount(masks) == 1, "1 sparkle collected");
        helper.assertTrue(CollectionMilestone.CUDDLE_STARTER.reached(masks), "Cuddle Starter reached at 3 species");
        helper.assertFalse(CollectionMilestone.SHELF_STYLIST.reached(masks), "Shelf Stylist needs 6 species");
        helper.succeed();
    }
}
