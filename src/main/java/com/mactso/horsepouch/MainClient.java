package com.mactso.horsepouch;

import java.lang.reflect.Field;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs.LateBoundIdMapper;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import com.mojang.serialization.MapCodec;

import com.mactso.horsepouch.items.Full;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

	
	@SuppressWarnings("unchecked")
	@Override
	public void onInitializeClient() {
		
		Field field_ID_MAPPER = null;

        MappingResolver mapper = FabricLoader.getInstance().getMappingResolver();
        String name = mapper.mapFieldName("intermediary", "net.minecraft.class_10459", "field_55372", "Lnet/minecraft/class_5699$class_10388;"); // ID_MAPPER
        
		try {
			field_ID_MAPPER = ConditionalItemModelProperties.class.getDeclaredField(name);
			field_ID_MAPPER.setAccessible(true);
		} catch (Exception e) {
			// e.printStackTrace();
			// return;
		}
		
//		if (field_ID_MAPPER == null) {
//			try {
//				field_ID_MAPPER = ConditionalItemModelProperties.class.getDeclaredField("ID_MAPPER");
//				field_ID_MAPPER.setAccessible(true);
//			} catch (Exception e) {
//				e.printStackTrace();
//				// return;
//			}
//		}
		

		LateBoundIdMapper<ResourceLocation, MapCodec<? extends ConditionalItemModelProperty>> theirIdMapper;
		int debug = 3;
			try {
				// this causes an unchecked cast warning
				theirIdMapper = (LateBoundIdMapper<ResourceLocation, MapCodec<? extends ConditionalItemModelProperty>>) field_ID_MAPPER
						.get(null);
				theirIdMapper.put(ResourceLocation.parse("horsepouch:full"), Full.MAP_CODEC);
			} catch (Exception e) {
				e.printStackTrace();
			}

		
		
	  	// old way ItemProperties.register(ModItems.HORSE_POUCH, ResourceLocation.parse("horsepouch:full"), HorsePouchItem::bagModel);
	}
}
