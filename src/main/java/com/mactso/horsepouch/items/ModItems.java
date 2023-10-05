package com.mactso.horsepouch.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	public static final Item HORSE_POUCH = new HorsePouchItem(new Item.Properties().rarity(Rarity.COMMON));
        
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		forgeRegistry.register("horse_pouch", HORSE_POUCH);
	}
}
