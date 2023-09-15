package com.mactso.horsepouch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.items.ModItems;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

@Mod("horsepouch")
public class Main {

	    public static final String MODID = "horsepouch"; 
		private static final Logger LOGGER = LogManager.getLogger();
	    
	    public Main()
	    {
	    	Utility.debugMsg(0,MODID + ": Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
			
	    }

	    
		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

		    @SubscribeEvent
		    public static void onRegister(final RegisterEvent event)
		    {
		    	@Nullable
				IForgeRegistry<Object> fr = event.getForgeRegistry();
		    	
		    	@NotNull
				ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

		    	if (key.equals(ForgeRegistries.Keys.ITEMS))
		    		ModItems.register(event.getForgeRegistry());
		    }
		    
	    }


}

