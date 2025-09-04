package com.mactso.horsepouch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.items.ModItems;
import net.fabricmc.api.ModInitializer;

public class Main implements ModInitializer {

	public static final String MOD_ID = "horsepouch";
	private static final Logger LOGGER = LogManager.getLogger();

	public void onInitialize() {
		LOGGER.info("Registering " + MOD_ID + ".");
		ModItems.register();
		MyConfig.registerConfigs();

	}

}
