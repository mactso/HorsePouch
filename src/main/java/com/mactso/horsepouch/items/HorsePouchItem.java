package com.mactso.horsepouch.items;

import java.util.Optional;
import java.util.function.Consumer;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HorsePouchItem extends Item {
	// private static final Logger LOGGER = LogManager.getLogger();
	public static final String STORED_ENTITY_DATA_TAG = "StoredEntityData";



	public HorsePouchItem(Properties properties) {
		super(properties);

	}

	@SuppressWarnings("deprecation")
	@Override
    public void appendHoverText(ItemStack itemStackIn, TooltipContext context, TooltipDisplay tooltipDisplay,
            Consumer<Component> tooltipOutput, TooltipFlag tooltipFlag) {
		
		CustomData data = itemStackIn.get(DataComponents.CUSTOM_DATA);
		if (!holdsSteed(data)) {
			tooltipOutput.accept(Component.literal("Not holding a steed."));
			return;
		}

		Optional<CompoundTag> optEntityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG);
		if (optEntityData.isEmpty()) {
			tooltipOutput.accept(Component.literal("Not holding a steed."));
			return;
		}		
		
		CompoundTag entityData = optEntityData.get();
		String idString = getOptionalStringOrBlank(entityData.getString("id"));
		EntityType<?> entityType = EntityType.byString(idString).orElse(null);
		String description = entityType.builtInRegistryHolder().key().location().getPath();
		MutableComponent bagTip = Component.literal("Holding a " + description + ".");
		if (entityData.contains("CustomName")) {
			int i = entityData.getId();
			Component name = entityData.read("CustomName",ComponentSerialization.CODEC).orElse(Component.literal(" ?Name? "));
			bagTip = Component.literal("Holding a " + description + " named ");
			bagTip.append(name);
		}
		tooltipOutput.accept(bagTip);

}
	


	private String getOptionalStringOrBlank(Optional<String> optStringIn) {
		if (optStringIn.isPresent()) {
			return optStringIn.get();
		}
		return "";
	}

	private void emptyTheHorsePouch(ItemStack itemStack) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, t -> {
			t.remove(STORED_ENTITY_DATA_TAG);
		});
	}

	private BlockPos getRestorePos(Level level, BlockPos blockpos, Direction direction, BlockState blockstate) {
		BlockPos blockpos1;
		if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
			blockpos1 = blockpos;
		} else {
			blockpos1 = blockpos.relative(direction);
		}
		return blockpos1;
	}

	// NOTE:  "getUnsafe()" is returning a pointer- changing it changes the real data.
	// 
	@SuppressWarnings("deprecation")
	private boolean holdsSteed(CustomData data) {
		if (data != null && data.contains(STORED_ENTITY_DATA_TAG) && data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG).isPresent())
			return true;
		return false;
	}
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStackIn, Player player, LivingEntity entity,
			InteractionHand hand) {

		Level world = player.level();

		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		

		if (!(player instanceof ServerPlayer)) {
			return InteractionResult.SUCCESS;
		}
	
		ServerPlayer serverPlayer = (ServerPlayer) player;
		ServerLevel serverLevel = serverPlayer.level();

		if (!(entity instanceof AbstractHorse)) {
			return InteractionResult.CONSUME;
		}

		AbstractHorse targetHorse = (AbstractHorse) entity;

		// Prevent losing horse in Creative Mode.
		ItemStack itemStack = player.getItemInHand(hand);
		if (!itemStack.is(this)) {
			return InteractionResult.CONSUME;
		}

		// check if horse in bag already
		CustomData data = itemStackIn.get(DataComponents.CUSTOM_DATA);
		if (data != null && data.contains(STORED_ENTITY_DATA_TAG)) {
			return InteractionResult.CONSUME;
		}

		if (!(targetHorse.isTamed())) {
			return InteractionResult.CONSUME;
		}

		
		if ((targetHorse.getOwner() != null) && (targetHorse.getOwner().getUUID() != null) && (MyConfig.isMustBeOwner())
				&& (!player.getUUID().equals(targetHorse.getOwner().getUUID()))) {
			return InteractionResult.CONSUME;
		}

		if ((isSaddleable(targetHorse)) && (MyConfig.isMustBeSaddled()) && !(targetHorse.isSaddled())) {
			return InteractionResult.CONSUME;
		}

		Utility.debugMsg(1, "Horse Pouch Storing Horse");

		targetHorse.ejectPassengers();

		CompoundTag entityDataTag = entity.serializeNBT(entity.registryAccess());
		entityDataTag.remove("Pos");
		entityDataTag.remove("UUID");
		// entityData.removeTag("Dimension"); was in 12.
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, t -> {
			// CompoundTag tag = new CompoundTag();
			t.put(STORED_ENTITY_DATA_TAG, entityDataTag);
		});
		entity.remove(RemovalReason.DISCARDED);

		serverLevel.playSound(serverPlayer, serverPlayer.blockPosition(), SoundEvents.HORSE_ARMOR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

		return InteractionResult.CONSUME;

	}

	private boolean isSaddleable (AbstractHorse targetHorse) {

	        return targetHorse.isAlive() && !targetHorse.isBaby() && targetHorse.isTamed();
	}
	
	private void restoreTheSteed(Level level, BlockPos blockpos1, CompoundTag entityData, EntityType<?> entityType) {
		Entity newEntity = entityType.create((ServerLevel) level, null, blockpos1, EntitySpawnReason.MOB_SUMMONED, false,
				false);
		EntityType.updateCustomEntityTag(level, null, newEntity, CustomData.of(entityData)); // New
		
		level.addFreshEntity(newEntity); // note: boolean returned by this is unreliable;
	}



	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {

		Utility.debugMsg(1, "Use HorsePouch to restore Steed at player location.");

		if (level.isClientSide())
			return InteractionResult.CONSUME;
//			return InteractionResult.consume(player.getItemInHand(hand));

		if (!(player instanceof ServerPlayer)) {
			return InteractionResult.CONSUME;
		}
	
		ServerPlayer serverPlayer = (ServerPlayer) player;
		ServerLevel serverLevel = serverPlayer.level();

		
		ItemStack itemStack = player.getItemInHand(hand);
		CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
		if (!holdsSteed(data))
			return InteractionResult.CONSUME;
//			return InteractionResultHolder.consume(player.getItemInHand(hand));

		// TODO : Now Optional - may need to put in optional.isPresent() test 
		CompoundTag entityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG).get();
		if (entityData.contains("id")) {
			Utility.debugMsg(1, "Restore Horse");
			BlockPos blockpos = player.blockPosition();
			EntityType<?> entityType = EntityType.byString(entityData.getString("id").get()).orElse(null);
			if (entityType != null) {
				restoreTheSteed(serverLevel, blockpos, entityData, entityType);
				emptyTheHorsePouch(itemStack);
				Utility.sendChat(serverPlayer, "Your steed was restored where you are.", ChatFormatting.DARK_GREEN);
				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.CONSUME;

	}


	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult useOn(UseOnContext iuc) {

		Level level = iuc.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.CONSUME;
		}

		Utility.debugMsg(1, "Use HorsePouch to restore Steed on block player is looking at.");

		Player player = iuc.getPlayer();
		ItemStack itemStack = iuc.getItemInHand();
		BlockPos blockpos = iuc.getClickedPos();
		Direction direction = iuc.getClickedFace();
		BlockState blockstate = level.getBlockState(blockpos);
		BlockPos blockpos1 = getRestorePos(level, blockpos, direction, blockstate);

		CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
		if (!holdsSteed(data))
			return InteractionResult.CONSUME;
		CompoundTag entityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG).get();
		if (entityData.contains("id")) {
			EntityType<?> entityType = EntityType.byString(entityData.getString("id").get()).orElse(null);
			if (entityType != null) {
				restoreTheSteed(level, blockpos1, entityData, entityType);
				emptyTheHorsePouch(itemStack);
				Utility.sendChat((ServerPlayer)player, "Your steed was restored nearby.", ChatFormatting.DARK_GREEN);
			}
		}

		return InteractionResult.CONSUME;
	}

}
