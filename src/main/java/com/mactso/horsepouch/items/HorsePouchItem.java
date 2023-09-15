package com.mactso.horsepouch.items;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
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
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HorsePouchItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public HorsePouchItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity entity,
			InteractionHand hand) {

		Level world = player.level;

		if (world.isClientSide) {
			return InteractionResult.PASS;
		}

		Utility.debugMsg(1, "Horse Pouch Storing Horse");
		if (!(entity instanceof Horse)) {
			return InteractionResult.PASS;
		}

		Horse targetHorse = (Horse) entity;

		if ((MyConfig.isMustBeOwner()) && (!player.getUUID().equals(targetHorse.getOwnerUUID()))) {
			return InteractionResult.PASS;
		}

		if ((MyConfig.isMustBeSaddled()) && !(targetHorse.isSaddled())) {
			return InteractionResult.PASS;
		}

		targetHorse.ejectPassengers();
		CompoundTag entityData = entity.serializeNBT();
		entityData.remove("Pos");
		entityData.remove("UUID");
		// entityData.removeTag("Dimension"); was in 12.
		itemStack.getOrCreateTag().put("StoredEntityData", entityData);
		entity.remove(RemovalReason.DISCARDED);

		world.playSound(null, player.blockPosition(), SoundEvents.HORSE_ARMOR, SoundSource.PLAYERS, 1.0F, 1.0F);
		return InteractionResult.CONSUME;

	}

	@Override
	public void appendHoverText(ItemStack itemStack, Level world, List<Component> tiplist, TooltipFlag p_41424_) {
		CompoundTag nbtTag = itemStack.getTag(); // Correct method

		if ((nbtTag == null) || (!(nbtTag.contains("StoredEntityData")))) {
			tiplist.add(Component.literal("Not holding a steed."));
			return;
		}

		CompoundTag entityData = nbtTag.getCompound("StoredEntityData");
		EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
		String description = entityType.builtInRegistryHolder().key().location().getPath();
		MutableComponent bagTip = Component.literal("Holding a " + description + ".");
		if (entityData.contains("CustomName", 8)) {
			MutableComponent name = Component.literal(" ?Name? ");
			try {
				name = Component.Serializer.fromJson(entityData.getString("CustomName"));
			} catch (Exception e) {
				// failure to parse steed name isn't fatal.
			}
			bagTip = Component.literal("Holding a " + description + " named ");
			bagTip.append(name);
		}
		tiplist.add(bagTip);
	}

	@Override
	public InteractionResult useOn(UseOnContext iuc) {

		Level level = iuc.getLevel();
		if (!level.isClientSide) {
			Level world = iuc.getLevel();
			Player player = iuc.getPlayer();
			InteractionHand hand = iuc.getHand();
			ItemStack itemStack = iuc.getItemInHand();
			BlockPos blockpos = iuc.getClickedPos();
			Direction direction = iuc.getClickedFace();
			BlockState blockstate = world.getBlockState(blockpos);

			BlockPos blockpos1;
			if (blockstate.getCollisionShape(world, blockpos).isEmpty()) {
				blockpos1 = blockpos;
			} else {
				blockpos1 = blockpos.relative(direction);
			}

			CompoundTag entityData = itemStack.getOrCreateTag().getCompound("StoredEntityData");
			if (entityData.contains("id", 8)) {
				Utility.debugMsg(1, "Restore Horse");
				EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
				if (entityType != null) {
					CompoundTag newEntityData = new CompoundTag();
					newEntityData.put("EntityTag", entityData);
					Entity newEntity = entityType.create((ServerLevel) world, newEntityData, null, blockpos1, MobSpawnType.MOB_SUMMONED, false, false);

					// note: boolean returned by this is unreliable;
					boolean bool = ((Level) world).addFreshEntity(newEntity);
					itemStack.getOrCreateTag().remove("StoredEntityData");

					Component component = Component.literal("Your steed restored nearby.");
					component.getStyle().withBold(false);
					component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN));
					player.sendSystemMessage(component);

					return InteractionResult.CONSUME;
				}
			}
		}
		return super.useOn(iuc);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		Utility.debugMsg(1, "Use HorsePouch");

		if (world.isClientSide) {
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		ItemStack itemStack = player.getItemInHand(hand);
		CompoundTag entityData = itemStack.getOrCreateTag().getCompound("StoredEntityData");

		if (entityData.contains("id", 8)) {
			Utility.debugMsg(1, "Restore Horse");
			BlockPos blockpos = player.blockPosition();

			EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
			if (entityType != null) {
				CompoundTag newEntityData = new CompoundTag();
				newEntityData.put("EntityTag", entityData);
				Entity newEntity = entityType.create((ServerLevel) world, newEntityData, null, blockpos, MobSpawnType.MOB_SUMMONED, false, false);

				// note: boolean returned by this is unreliable;
				boolean bool = ((Level) world).addFreshEntity(newEntity);
				int i = 4;
				itemStack.getOrCreateTag().remove("StoredEntityData");

				Component component = Component.literal("Your steed restored where you are.");
				component.getStyle().withBold(false);
				component.getStyle().withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_GREEN));
				player.sendSystemMessage(component);

				return InteractionResultHolder.consume(itemStack);
			}
		}

		return InteractionResultHolder.pass(player.getItemInHand(hand));

	}

}
