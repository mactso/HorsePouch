package com.mactso.horsepouch.items;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.mactso.horsepouch.Main;
import com.mojang.serialization.MapCodec;

import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.ExtraCodecs.LateBoundIdMapper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModItems {
	
// this would be Item::new if it were a food or some generic item.
	public static final Item HORSE_POUCH = register ("horse_pouch", HorsePouchItem::new, new Properties().rarity(Rarity.COMMON));
//	public static final Item EXAMPLE_BLOCK_ITEM = register(ModBlocks.EXAMPLE_BLOCK_ITEM, BlockItem::new, new Properties());
//  if the block has a special class (like PylonBlock) then it would be PylonBlock::new;


	private static Item register(String name, Function<Properties, Item> func, Properties prop)
	{
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Main.MODID, name));
		return Items.registerItem(key, func, prop);
	}
	
	@SuppressWarnings("unchecked")
	public static void register() {


		Field field_ID_MAPPER = null;

		try {
			field_ID_MAPPER = ConditionalItemModelProperties.class.getDeclaredField("ID_MAPPER");
			field_ID_MAPPER.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		LateBoundIdMapper<ResourceLocation, MapCodec<? extends ConditionalItemModelProperty>> theirIdMapper;
		if (FMLEnvironment.dist == Dist.CLIENT) {
			try {
				// this causes an unchecked cast warning
				theirIdMapper = (ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends ConditionalItemModelProperty>>) field_ID_MAPPER
						.get(null);
				theirIdMapper.put(ResourceLocation.parse("horsepouch:full"), Full.MAP_CODEC);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
	
	@SuppressWarnings("unused")  // no blocks in this mod.
	private static Item register(Block block, BiFunction<Block, Properties, Item> func, Properties prop)
	{
		return Items.registerBlock(block, func, prop);
	}
	
}
