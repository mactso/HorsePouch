package com.mactso.horsepouch.items;

import com.mactso.horsepouch.Main;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;


public class ModItems {
	
	public static final Item HORSE_POUCH = new HorsePouchItem(new FabricItemSettings().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.COMMON).stacksTo(1));
			
	
	public static void register() {
		register("horse_pouch", HORSE_POUCH);
	}


//	private static Item registerItem(String name, Item item) {
//		
//		ResourceLocation resourceLocation = new ResourceLocation(Main.MOD_ID,name);
//		return Registry.register(
//				Registry.ITEM,
//				resourceLocation.toString(),
//				item);
//	}
    
	private static void register(String key, Item item)
	{
		Registry.register(Registry.ITEM, new ResourceLocation(Main.MOD_ID, key), item);
	}
}
