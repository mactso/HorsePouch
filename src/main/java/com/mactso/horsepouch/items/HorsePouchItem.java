package com.mactso.horsepouch.items;

import java.util.List;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HorsePouchItem extends Item {
	private static final String STORED_ENTITY_DATA_TAG = "StoredEntityData";

	public HorsePouchItem(Item.Properties properties) {
		super(properties);
	}


	// this should only be called on the client side.
	public static float bagModel(ItemStack itemStackIn, Level level, LivingEntity le, int i) {
		
		CustomData data = itemStackIn.get(DataComponents.CUSTOM_DATA);
		if (data != null && data.contains(STORED_ENTITY_DATA_TAG)) {
			return 1; // bag full of horse
		}
		return 0; // bag empty
	}
	
	// entity#save (error if it has passengers)
	// entity#saveaspassenger
	
    public CompoundTag serializeFabricEntityNBT(LivingEntity entity, RegistryAccess registryAccess)
    {
    	CompoundTag ret = new CompoundTag();
        ResourceLocation resourceLocation = EntityType.getKey(entity.getType());
        if (resourceLocation != null)         {
            ret.putString("id", resourceLocation.toString());
        }
        return entity.saveWithoutId(ret);
    }
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStackIn, Player player, LivingEntity entity,
			InteractionHand hand) {

		Level world = player.level();

		if (world.isClientSide) {
			return InteractionResult.SUCCESS;
		}

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

		if ((targetHorse.getOwnerUUID() != null) && (MyConfig.isMustBeOwner())
				&& (!player.getUUID().equals(targetHorse.getOwnerUUID()))) {
			return InteractionResult.CONSUME;
		}

		if ((targetHorse.isSaddleable()) && (MyConfig.isMustBeSaddled()) && !(targetHorse.isSaddled())) {
			return InteractionResult.CONSUME;
		}

		Utility.debugMsg(1, "Horse Pouch Storing Horse");

		targetHorse.ejectPassengers();

		// entity#save (error if it has passengers)
		// entity#saveaspassenger

		CompoundTag entityDataTag = serializeFabricEntityNBT(entity, entity.registryAccess());
		entityDataTag.remove("Pos");
		entityDataTag.remove("UUID");
		// entityData.removeTag("Dimension"); was in 12.
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, t -> {
			// CompoundTag tag = new CompoundTag();
			t.put(STORED_ENTITY_DATA_TAG, entityDataTag);
		});
		entity.remove(RemovalReason.DISCARDED);

		world.playSound(null, player.blockPosition(), SoundEvents.HORSE_ARMOR, SoundSource.PLAYERS, 1.0F, 1.0F);

		return InteractionResult.CONSUME;

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
	
	
	private boolean holdsSteed(CustomData data) {
		if (data != null && data.contains(STORED_ENTITY_DATA_TAG))
			return true;
		return false;
	}
	
	private void emptyTheHorsePouch(ItemStack itemStack) {
		CustomData.update(DataComponents.CUSTOM_DATA, itemStack, t -> {
			t.remove(STORED_ENTITY_DATA_TAG);
		});
	}
	
	private void restoreTheSteed(Level level, BlockPos blockpos1, CompoundTag entityData, EntityType<?> entityType) {
		Entity newEntity = entityType.create((ServerLevel) level, null, blockpos1, MobSpawnType.MOB_SUMMONED, false,
				false);
		EntityType.updateCustomEntityTag(level, null, newEntity, CustomData.of(entityData)); // New
		
		level.addFreshEntity(newEntity); // note: boolean returned by this is unreliable;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void appendHoverText(ItemStack itemStackIn, TooltipContext context, List<Component> tiplist,
			TooltipFlag ttflag_) {		
	
			CustomData data = itemStackIn.get(DataComponents.CUSTOM_DATA);
			if (!holdsSteed(data)) {
				tiplist.add(Component.literal("Not holding a steed."));
				return;
			}

			CompoundTag entityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG);
			EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
			String description = entityType.builtInRegistryHolder().key().location().getPath();
			MutableComponent bagTip = Component.literal("Holding a " + description + ".");
			if (entityData.contains("CustomName", 8)) {
				MutableComponent name = Component.literal(" ?Name? ");
				try {
					name = Component.Serializer.fromJson(entityData.getString("CustomName"), RegistryAccess.EMPTY);
				} catch (Exception e) {
					// failure to parse steed name isn't fatal.
				}
				bagTip = Component.literal("Holding a " + description + " named ");
				bagTip.append(name);
			}
			tiplist.add(bagTip);
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
		CompoundTag entityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG);
		if (entityData.contains("id", 8)) {
			EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
			if (entityType != null) {
				restoreTheSteed(level, blockpos1, entityData, entityType);
				emptyTheHorsePouch(itemStack);
				Utility.sendChat(player, "Your steed was restored nearby.", ChatFormatting.DARK_GREEN);
			}
		}

		return InteractionResult.CONSUME;
	}


	@SuppressWarnings("deprecation")
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

		Utility.debugMsg(1, "Use HorsePouch to restore Steed at player location.");

		if (level.isClientSide()) 
			return InteractionResultHolder.consume(player.getItemInHand(hand));

		
		ItemStack itemStack = player.getItemInHand(hand);
		CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
		if (!holdsSteed(data))
			return InteractionResultHolder.consume(player.getItemInHand(hand));

		
		CompoundTag entityData = data.getUnsafe().getCompound(STORED_ENTITY_DATA_TAG);
		if (entityData.contains("id", 8)) {
			Utility.debugMsg(1, "Restore Horse");
			BlockPos blockpos = player.blockPosition();
			EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
			if (entityType != null) {
				restoreTheSteed(level, blockpos, entityData, entityType);
				emptyTheHorsePouch(itemStack);
				Utility.sendChat(player, "Your steed was restored where you are.", ChatFormatting.DARK_GREEN);
				return InteractionResultHolder.consume(itemStack);
			}
		}

		return InteractionResultHolder.consume(player.getItemInHand(hand));

	}

}
