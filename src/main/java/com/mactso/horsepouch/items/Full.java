package com.mactso.horsepouch.items;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;


public record Full() implements ConditionalItemModelProperty {
    public static final MapCodec<Full> MAP_CODEC = MapCodec.unit(new Full());

    // The JSON line '"property": "horsepouch:full" ' returns true or false based on the boolean get below.
    @Override
    public boolean get(
        ItemStack itemStackIn, @Nullable ClientLevel level, @Nullable LivingEntity le, int i, ItemDisplayContext ctx) {
        CustomData data = itemStackIn.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.contains(HorsePouchItem.STORED_ENTITY_DATA_TAG)) {
            return true; // bag full of horse
        }
        return false; // bag empty
    }

    @Override
    public MapCodec<Full> type() {
        return MAP_CODEC;
    }
}
