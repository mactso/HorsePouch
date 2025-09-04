package com.mactso.horsepouch.config;

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.horsepouch.Main;
import com.mactso.horsepouch.utility.Utility;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;



@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {
	
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	public static int TICKS_PER_MINUTE = 1200;

	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}	
	
	
	public static int getDebugLevel() {
		return debugLevel;
	}

	public static void setDebugLevel(int debugLevel) {
		MyConfig.debugLevel = debugLevel;
	}

	public static int getRecipeDifficulty() {
		return recipeDifficulty;
	}

	public static boolean isMustBeOwner() {
		return mustBeOwner;
	}

	public static boolean isMustBeSaddled() {
		return mustBeSaddled;
	}

	private static int     debugLevel;
	private static int     recipeDifficulty;
	private static boolean mustBeOwner;
	private static boolean mustBeSaddled;		
	
	@SubscribeEvent
	public static void onModConfigEvent(final ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	

	public static void pushDebugValue() {
		Utility.debugMsg(1, "Happy Trails Debug Level:"+MyConfig.debugLevel);
		COMMON.debugLevel.set( MyConfig.debugLevel);
	}

	public static void bakeConfig()
	{
	
		
		debugLevel = COMMON.debugLevel.get();
		recipeDifficulty = COMMON.recipeDifficulty.get();
		mustBeOwner = COMMON.mustBeOwner.get();
		mustBeSaddled = COMMON.mustBeSaddled.get();

		if (debugLevel > 0) {
			System.out.println("Horse Pouch Debug: " + debugLevel );
		}
	}
	
	public static class Common {

		public final IntValue     	debugLevel;
		public final IntValue     	recipeDifficulty;		
		public final BooleanValue   mustBeOwner;
		public final BooleanValue   mustBeSaddled;
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Happy Trail Control Values");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			recipeDifficulty = builder
					.comment("How difficult is recipe.")
					.translation(Main.MODID + ".config." + "recipeDifficulty")
					.defineInRange("recipeDifficulty", () -> 0, 0, 2);
			
			mustBeOwner = builder
					.comment("Only Owner can store Horse, Donkey, or Mule.")
		            .define("mustBeOwner", true);

			mustBeSaddled = builder
					.comment("The Steed must be saddled to be stored.")
		            .define("mustBeSaddled", true);
			builder.pop();			
		}
	}
}
