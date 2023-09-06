package com.mactso.horsepocket;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod("horsepouch")
public class Main {

	    public static final String MODID = "horsepouch"; 
	    
	    public Main()
	    {
			
	    }

	   // Register ourselves for server and other game events we are interested in
		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {

			
		}       

//	    @Mod.EventBusSubscriber()
//	    public static class ForgeEvents
//	    {


//    }

}
