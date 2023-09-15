package com.mactso.horsepouch.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.CreativeModeTab;

public class ModItems {
	public static final Item HORSE_POUCH = new HorsePouchItem(new Properties().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.UNCOMMON)).setRegistryName("horse_pouch");

	public static void register(IForgeRegistry<Item> forgeRegistry)
	{

		forgeRegistry.registerAll(HORSE_POUCH);
	}
}
