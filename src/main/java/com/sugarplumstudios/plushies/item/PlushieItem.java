package com.sugarplumstudios.plushies.item;

import com.sugarplumstudios.plushies.plushie.PlushieRarity;
import com.sugarplumstudios.plushies.plushie.PlushieSpecies;
import com.sugarplumstudios.plushies.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
 * A collectible plushie. The variant is stored in the vanilla {@code BlockStateTag}
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
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(BLOCK_STATE_TAG)) {
            return 0;
        }
        try {
            return PlushieSpecies.clampVariant(Integer.parseInt(tag.getCompound(BLOCK_STATE_TAG).getString("variant")));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static void setVariant(ItemStack stack, int variant) {
        stack.getOrCreateTagElement(BLOCK_STATE_TAG).putString("variant", Integer.toString(PlushieSpecies.clampVariant(variant)));
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int variant = getVariant(stack);
        PlushieRarity rarity = species.rarity(variant);
        tooltip.add(Component.translatable("tooltip.sugarplumplushies.rarity", rarity.starLine(), rarity.displayName()));
        tooltip.add(Component.translatable("tooltip.sugarplumplushies.variant", species.variantName(variant)).withStyle(ChatFormatting.GRAY));
        tooltip.add(species.description().withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
        tooltip.add(Component.translatable("tooltip.sugarplumplushies.plushie_hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
