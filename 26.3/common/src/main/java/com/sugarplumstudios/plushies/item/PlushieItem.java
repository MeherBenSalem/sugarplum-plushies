package com.sugarplumstudios.plushies.item;

import net.minecraft.world.item.Item;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.ChatFormatting;
import com.sugarplumstudios.plushies.block.PlushieBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A collectible plushie. The variant is stored in the vanilla {@code minecraft:block_state} component
 * so that placing the item automatically produces the matching block state.
 */
public class PlushieItem extends BlockItem {
    private final PlushieSpecies species;

    public PlushieItem(Block block, PlushieSpecies species, Properties properties) {
        super(block, properties);
        this.species = species;
    }

    public PlushieSpecies getSpecies() {
        return species;
    }

    public static int getVariant(ItemStack stack) {
        BlockItemStateProperties props = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        Integer variant = props.get(PlushieBlock.VARIANT);
        return variant == null ? 0 : PlushieSpecies.clampVariant(variant);
    }

    public static void setVariant(ItemStack stack, int variant) {
        BlockItemStateProperties props = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
        stack.set(DataComponents.BLOCK_STATE, props.with(PlushieBlock.VARIANT, PlushieSpecies.clampVariant(variant)));
    }

    public static ItemStack create(PlushieSpecies species, int variant) {
        ItemStack stack = new ItemStack(ModItems.PLUSHIES.get(species).get());
        setVariant(stack, variant);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        int variant = getVariant(stack);
        MutableComponent base = Component.translatable(getDescriptionId());
        MutableComponent name = variant == 0 ? base
                : Component.translatable("item.sugarplumplushies.plushie_variant_name", base, species.variantName(variant));
        int color = species.rarity(variant).color();
        return name.withStyle(style -> style.withColor(TextColor.fromRgb(color)));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getVariant(stack) == PlushieSpecies.SPARKLE || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        int variant = getVariant(stack);
        PlushieRarity rarity = species.rarity(variant);
        tooltip.accept(Component.translatable("tooltip.sugarplumplushies.rarity", rarity.starLine(), rarity.displayName()));
        tooltip.accept(Component.translatable("tooltip.sugarplumplushies.variant", species.variantName(variant)).withStyle(ChatFormatting.GRAY));
        tooltip.accept(species.description().withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        tooltip.accept(Component.translatable("tooltip.sugarplumplushies.plushie_hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
