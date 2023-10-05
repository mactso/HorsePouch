package com.mactso.horsepouch.events;

import org.jetbrains.annotations.NotNull;

import com.mactso.horsepouch.items.HorsePouchItem;
import com.mactso.horsepouch.items.ModItems;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class handleHorsePouchInteraction {
	@SubscribeEvent
	public static void handleHorsePouchOnDonkeys (EntityInteract event) {
		
		
		Player p = (Player) event.getEntity();
		@NotNull
		ItemStack itemStack = event.getItemStack();
		Entity t = event.getTarget();
		
		if (!(itemStack.is(ModItems.HORSE_POUCH))) {
			return;
		}
		
		if (!(t instanceof AbstractHorse)) {
			return;
		}
		
		if (event.getLevel() instanceof ServerLevel) {
			InteractionResult res = itemStack.interactLivingEntity(p, (AbstractHorse) t, event.getHand());
			event.setCancellationResult(res);
			event.setCanceled(true);
		}
		
	}
}
