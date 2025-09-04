package com.mactso.horsepouch;

import org.jetbrains.annotations.NotNull;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.items.ModItems;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("horsepouch")
public class Main {

	    public static final String MODID = "horsepouch"; 
		// private static final Logger LOGGER = LogManager.getLogger();
	    
		public Main(FMLJavaModLoadingContext context) {
			context.registerConfig(ModConfig.Type.COMMON, MyConfig.COMMON_SPEC);
			Utility.debugMsg(0, MODID + ": Registering Mod"); 	        
	    }
	    
		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

		    @SubscribeEvent
		    public static void onRegister(final RegisterEvent event)
		    {
	    	
		    	@NotNull
				ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

		    	if (key.equals(ForgeRegistries.Keys.ITEMS)) {
		    		ModItems.register();
		    	}
		    }
		    
	    }


}

