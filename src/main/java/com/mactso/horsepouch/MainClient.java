package com.mactso.horsepouch;

import com.mactso.horsepouch.items.HorsePouchItem;
import com.mactso.horsepouch.items.ModItems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

	
	@Override
	public void onInitializeClient() {
	  	ItemProperties.register(ModItems.HORSE_POUCH, ResourceLocation.parse("horsepouch:full"), HorsePouchItem::bagModel);
	}
}
