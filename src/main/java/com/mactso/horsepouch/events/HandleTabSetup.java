package com.mactso.horsepouch.events;

import com.mactso.horsepouch.Main;
import com.mactso.horsepouch.items.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class HandleTabSetup {

	@SubscribeEvent
	public static void handleTabSetup(BuildCreativeModeTabContentsEvent event) {

		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.HORSE_POUCH);
		}

	}
}