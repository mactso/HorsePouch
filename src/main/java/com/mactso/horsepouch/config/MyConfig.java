package com.mactso.horsepouch.config;

import com.mactso.horsepouch.util.Reference;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid=Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class MyConfig {

	@Ignore
	public static boolean serverSide = false;
	
			@Comment ( {"Horse Pouch Control Values" } )
			@Name ("section heading")
			public static boolean sectionheading = true;
			
			@Comment ( { "Print Debugging Messages to Log" } )
			@Name ( "0: logging off, 1= logging minimal, 2 = logging maximal" )
			@RangeInt (min=0, max=2)
			public static int debugLevel = 0;

			
			@Comment ( { "Must Be Owner" } )
			@Name ("True: Must be horse owner to store the horse")
			public static boolean mustBeOwner = true;		

			@Comment ( { "Must Be Saddled" } )
			@Name ("True: Horse must be saddled to be stored.")
			public static boolean mustBeSaddled = true;

			
			public static int getDebugLevel() {
				return debugLevel;
			}

			public static boolean isMustBeOwner() {
				return mustBeOwner;
			}

			public static boolean isMustBeSaddled() {
				return mustBeSaddled;
			}		
			
			@SubscribeEvent
			public static void onModConfigEvent(OnConfigChangedEvent event)
			{

				if(event.getModID().equals(Reference.MOD_ID))
				{
					ConfigManager.sync (event.getModID(), Config.Type.INSTANCE);
					// TODO: server side variables?
				}
			}

}
