package com.mactso.horsepouch.items;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class HorsePouchItem extends Item {
	private static final Logger LOGGER = LogManager.getLogger();

	public HorsePouchItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity entity,
			Hand hand) {

		World world = player.level;

		if (world.isClientSide) {
			return ActionResultType.PASS;
		}

		Utility.debugMsg(1, "Horse Pouch Storing Horse");
		if (!(entity instanceof HorseEntity)) {
			return ActionResultType.PASS;
		}

		HorseEntity targetHorse = (HorseEntity) entity;

		if ((MyConfig.isMustBeOwner()) && (!player.getUUID().equals(targetHorse.getOwnerUUID()))) {
			return ActionResultType.PASS;
		}

		if ((MyConfig.isMustBeSaddled()) && !(targetHorse.isSaddled())) {
			return ActionResultType.PASS;
		}

		targetHorse.ejectPassengers();
		CompoundNBT entityData = entity.serializeNBT();
		entityData.remove("Pos");
		entityData.remove("UUID");
		// entityData.removeTag("Dimension"); was in 12.
		itemStack.getOrCreateTag().put("StoredEntityData", entityData);
		entity.remove();

		world.playSound(null, player.blockPosition(), SoundEvents.HORSE_ARMOR, SoundCategory.PLAYERS, 1.0F, 1.0F);
		return ActionResultType.CONSUME;

	}

	@Override
	public void appendHoverText(ItemStack itemStack, World world, List<ITextComponent> tiplist,
			ITooltipFlag p_77624_4_) {

		CompoundNBT nbtTag = itemStack.getTag(); // Correct method

		if ((nbtTag == null) || (!(nbtTag.contains("StoredEntityData")))) {
			tiplist.add(new StringTextComponent("Not holding a steed."));
			return;
		}

		CompoundNBT entityData = nbtTag.getCompound("StoredEntityData");
		EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
		StringTextComponent bagTip = new StringTextComponent(
				"Holding a " + entityType.getRegistryName().getPath() + ".");
		if (entityData.contains("CustomName", 8)) {
			IFormattableTextComponent name = new StringTextComponent(" ?Name? ");
			try {
				name = ITextComponent.Serializer.fromJson(entityData.getString("CustomName"));
			} catch (Exception e) {
				// failure to parse steed name isn't fatal.
			}
			bagTip = new StringTextComponent("Holding a " + entityType.getRegistryName().getPath() + " named ");
			bagTip.append(name);
		}
		tiplist.add(bagTip);
	}

	@Override
	public ActionResultType useOn(ItemUseContext iuc) {

		World level = iuc.getLevel();
		if (!level.isClientSide) {
			World world = iuc.getLevel();
			PlayerEntity player = iuc.getPlayer();
			Hand hand = iuc.getHand();
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

			CompoundNBT entityData = itemStack.getOrCreateTag().getCompound("StoredEntityData");
			if (entityData.contains("id", 8)) {
				Utility.debugMsg(1, "Restore Horse");
				EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
				if (entityType != null) {
					CompoundNBT newEntityData = new CompoundNBT();
					newEntityData.put("EntityTag", entityData);
					Entity newEntity = entityType.create((ServerWorld) world, newEntityData, (ITextComponent) null,
							(PlayerEntity) null, blockpos1, SpawnReason.MOB_SUMMONED, false, false);

					// note: boolean returned by this is unreliable;
					boolean bool = ((ServerWorld) world).addFreshEntity(newEntity);
					itemStack.getOrCreateTag().remove("StoredEntityData");

					StringTextComponent component = new StringTextComponent("Your steed restored nearby.");
					component.getStyle().withBold(true);
					component.getStyle().withColor(Color.fromLegacyFormat(TextFormatting.DARK_GREEN));
					player.sendMessage(component, player.getUUID());

					return ActionResultType.CONSUME;
				}
			}
		}
		return super.useOn(iuc);
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {

		Utility.debugMsg(1, "Use HorsePouch");

		if (!world.isClientSide) {
			ItemStack itemStack = player.getItemInHand(hand);
			CompoundNBT entityData = itemStack.getOrCreateTag().getCompound("StoredEntityData");
			if (entityData.contains("id", 8)) {
				Utility.debugMsg(1, "Restore Horse");

				EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
				if (entityType != null) {
					CompoundNBT newEntityData = new CompoundNBT();
					newEntityData.put("EntityTag", entityData);
					Entity newEntity = entityType.create((ServerWorld) world, newEntityData, (ITextComponent) null,
							(PlayerEntity) null, player.blockPosition(), SpawnReason.MOB_SUMMONED, false, false);

					// note: boolean returned by this is unreliable;
					boolean bool = ((ServerWorld) world).addFreshEntity(newEntity);
					int i = 4;
					itemStack.getOrCreateTag().remove("StoredEntityData");

					StringTextComponent component = new StringTextComponent("Your steed restored where you are.");
					component.getStyle().withBold(true);
					component.getStyle().withColor(Color.fromLegacyFormat(TextFormatting.DARK_GREEN));
					player.sendMessage(component, player.getUUID());

					return ActionResult.consume(itemStack);
				}
			}

		}
		return ActionResult.pass(player.getItemInHand(hand));

	}

}
