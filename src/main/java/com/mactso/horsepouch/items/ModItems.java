package com.mactso.horsepouch.items;

import java.util.function.Function;

import com.mactso.horsepouch.Main;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;


public class ModItems {
	
	// public static final Item BEE_POLLEN = register("bee_pollen", Item::new, new Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.6F).build(),
	//		Consumables.defaultFood().consumeSeconds(0.8F).onConsume(new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.LEVITATION, 40, 6)), 1.0F)).build()).stacksTo(16));
	
	public static final Item HORSE_POUCH = register ("horse_pouch", HorsePouchItem::new, new Properties().rarity(Rarity.COMMON).stacksTo(1));
	
	public static void register()
	{
	}
		
	private static Item register(String name, Function<Properties, Item> func, Properties prop)
	{
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, name));
		return Items.registerItem(key, func, prop);
	}
	

	
	
	public static void setupTabs()
	{
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((c) -> {
			c.accept(HORSE_POUCH);
		});
	}		

	

	

}
