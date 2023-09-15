package com.mactso.horsepouch.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.horsepouch.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Utility {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	// support for any color chattext
	public static void sendChat(Player p, String chatMessage, TextColor color) {
		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withColor(color);
		p.sendMessage(component, p.getUUID());
	}
	
	// support for any color, optionally bold text.
	public static void sendBoldChat(Player p, String chatMessage, TextColor color) {
		TextComponent component = new TextComponent (chatMessage);

		component.getStyle().withBold(true);
		component.getStyle().withColor(color);
		p.sendMessage(component, p.getUUID());
	}
	
	public static void debugMsg(int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + ":" + dMsg);
		}

	}

	public static void debugMsg(int level, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + " (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "): " + dMsg);
		}

	}
	
	public static void debugMsg(int level, LivingEntity le, String dMsg) {

		if (MyConfig.getDebugLevel() > level - 1) {
			LOGGER.info("L" + level + " (" 
					+ le.blockPosition().getX() + "," 
					+ le.blockPosition().getY() + ","
					+ le.blockPosition().getZ() + "): " + dMsg);
		}

	}
		
}
	



