package com.mactso.horsepouch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.horsepouch.proxy.CommonProxy;
import com.mactso.horsepouch.util.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {

	private static final Logger LOGGER = LogManager.getLogger();

	@Instance
	public static Main instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	@EventHandler
	private static void PreInit(FMLPreInitializationEvent event) {
		
	}

	@EventHandler
	private static void init(FMLInitializationEvent event) {
		
	}
	
	@EventHandler
	private static void Postinit(FMLPostInitializationEvent event) {
		
	}
	
	public Main() {
		LOGGER.info( " Horsepouch 1.12.2 Startup" );
	}

}
