package com.mactso.horsepouch.items;

import com.mactso.horsepouch.Main;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;


public class ModItems {
	
	public static final Item HORSE_POUCH = new HorsePouchItem(new FabricItemSettings().rarity(Rarity.COMMON).maxCount(1));
			
	
	public static void register() {
		register("horse_pouch", HORSE_POUCH);
	}
	public static void setupTabs()
	{
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((c) -> {
			c.accept(HORSE_POUCH);
		});
	}		

	private static Item registerItem(String name, Item item) {
		
		ResourceLocation resourceLocation = new ResourceLocation(Main.MOD_ID,name);
		return Registry.register(
				BuiltInRegistries.ITEM,
				ResourceKey.create(BuiltInRegistries.ITEM.key(),
				resourceLocation), item);
	}
    
	private static void register(String key, Item item)
	{
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Main.MOD_ID, key), item);
	}
}
