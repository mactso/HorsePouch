package com.mactso.horsepouch.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.horsepouch.Main;
import com.mojang.datafixers.util.Pair;

public class MyConfig {

	private static final Logger LOGGER = LogManager.getLogger();
	public static SimpleConfig CONFIG;
	private static ModConfigProvider configs;

	public static final Boolean BOLD = true;

	private static int     debugLevel;
	private static String mustBeOwner;
	private static String mustBeSaddled;	

	public static int getDebugLevel() {
		return debugLevel;
	}

	public static void setDebugLevel(int debugLevel) {
		MyConfig.debugLevel = debugLevel;
	}

	public static boolean isMustBeOwner() {
		if (mustBeOwner.equals("true")) {
			return true;
		}
		return false;
	}
	public static boolean isMustBeSaddled() {
		if (mustBeSaddled.equals("true")) {
			return true;
		}
		return false;
	}


	public static void registerConfigs() {
		configs = new ModConfigProvider();
		createConfigs();
		CONFIG = SimpleConfig.of(Main.MOD_ID + "config").provider(configs).request();
		assignConfigs();
	}

	private static void createConfigs() {
		configs.addKeyValuePair(new Pair<>("key.debugLevel", 0), "int");
		configs.addKeyValuePair(new Pair<>("key.mustbeowner", "true"), "String");
		configs.addKeyValuePair(new Pair<>("key.mustbesaddled", "true"), "String");
	}

	private static void assignConfigs() {

		debugLevel = CONFIG.getOrDefault("key.debugLevel", 0);
		mustBeOwner = CONFIG.getOrDefault("key.mustbeowner", "true");
		mustBeSaddled = CONFIG.getOrDefault("key.mustbesaddled", "true");
		LOGGER.info("All " + configs.getConfigsList().size() + " have been set properly");
	}

	
	public static void diskSaveDebugValue() {
		// don't save debug value from session to session.
	}

	public static void diskSaveMustBeOwnerValue() {
		String particlesOn = "false";
		if (isMustBeOwner()) {
			mustBeOwner = "true";
		}
		configs.setKeyValuePair("key.particleson", particlesOn);
		CONFIG.diskSaveConfig();
	}

	public static void diskSaveMustBeSaddledValue() {
		String particlesOn = "false";
		if (isMustBeSaddled()) {
			mustBeSaddled = "true";
		}
		configs.setKeyValuePair("key.particleson", particlesOn);
		CONFIG.diskSaveConfig();
	}
	

}
