package com.mactso.horsepouch.events;

import com.mactso.horsepouch.Main;
import com.mactso.horsepouch.items.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class HandleTabSetup {

	@SubscribeEvent
	public static void handleTabSetup(CreativeModeTabEvent.BuildContents event) {

		if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.HORSE_POUCH);
		}

	}
}