package com.mactso.horsepouch.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.item.Item.Properties;

public class ModItems {
	public static final Item HORSE_POUCH = new HorsePouchItem(new Properties().tab(ItemGroup.TAB_TOOLS).rarity(Rarity.UNCOMMON)).setRegistryName("horse_pouch");

	public static void register(IForgeRegistry<Item> forgeRegistry)
	{

		forgeRegistry.registerAll(HORSE_POUCH);
	}
}
