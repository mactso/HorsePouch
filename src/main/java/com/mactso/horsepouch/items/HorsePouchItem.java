package com.mactso.horsepouch.items;

import java.util.List;

import com.mactso.horsepouch.Main;
import com.mactso.horsepouch.config.MyConfig;
import com.mactso.horsepouch.init.ModItems;
import com.mactso.horsepouch.util.Reference;
import com.mactso.horsepouch.util.interfaces.IHasModel;
import com.mactso.horsepouch.utility.Utility;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class HorsePouchItem extends Item implements IHasModel {
//	private static final Logger LOGGER = LogManager.getLogger();

	public HorsePouchItem(String name) {
        setUnlocalizedName(Reference.MOD_ID + "."+ name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.TOOLS);
		ModItems.ITEMS.add(this);
		maxStackSize = 1;
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");

	}

	@Override
	public boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase target,
			EnumHand hand) {

		Utility.debugMsg(1, "HorsePouch: Trying to save horse.");

		if (player.world.isRemote) {
			return false;
		}
		WorldServer serverWorld = (WorldServer) player.world;
		if (!(target instanceof EntityHorse)) {
			return false;
		}
		EntityHorse targetHorse = (EntityHorse) target;

		// Check HorsePouch is empty.
		NBTTagCompound entityData = itemStack.getSubCompound("StoredEntityData");
		if (entityData != null && !entityData.hasNoTags()) {
			return false;
		}

		if ((MyConfig.isMustBeOwner()) && (!player.getUniqueID().equals(targetHorse.getOwnerUniqueId()))) {
			return false;
		}
		
		if ((MyConfig.isMustBeSaddled()) && !(targetHorse.isHorseSaddled())) {
			return false;
		}

		targetHorse.removePassengers();
		entityData = targetHorse.serializeNBT();

		// TODO verify these tags were the same name in 1.12.2
		entityData.removeTag("Pos");
		entityData.removeTag("UUIDLeast");
		entityData.removeTag("UUIDMost");
		entityData.removeTag("Dimension");
		
		itemStack.getOrCreateSubCompound("StoredEntityData").merge(entityData);
		serverWorld.removeEntity(target);
		player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_HORSE_ARMOR, SoundCategory.PLAYERS, 1.0F,
				1.0F);
		return true;

	}

	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {

		// Check if the ItemStack has the "StoredEntityData" tag
		NBTTagCompound nbtTag = stack.getTagCompound(); // Correct method
		if (nbtTag != null && nbtTag.hasKey("StoredEntityData")) { // Correct method
			NBTTagCompound entityData = nbtTag.getCompoundTag("StoredEntityData");
			// Extract the custom name if it exists
			if (entityData.hasKey("CustomName", 8)) { // Correct method and tag type
				String customName = entityData.getString("CustomName"); // Correct method and tag type
				tooltip.add("Holding a horse named " + customName); // Add the custom name to the tooltip
			} else {
				tooltip.add("Holding a horse.");
			}
		} else {
			tooltip.add("Not holding a horse.");
		}

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos blockPos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		Utility.debugMsg(1, "HorsePouch: Restore near player");

		ItemStack itemStack = player.getHeldItem(hand);
		if (world.isRemote) {
			return EnumActionResult.PASS;
		}

		NBTTagCompound entityData = itemStack.getSubCompound("StoredEntityData");
		if (entityData == null || entityData.hasNoTags()) {
			return EnumActionResult.PASS;
		}

		String optionalNamePrefix = "Your horse ";
		if (entityData.hasKey("CustomName", 8)) { // Correct method and tag type
			optionalNamePrefix = entityData.getString("CustomName"); // Correct method and tag type
		}
		
		EntityHorse horseEntity = new EntityHorse(world);
		horseEntity.deserializeNBT(entityData);
		// get "safespawn" code from spawnegg.
		// ItemMonsterPlacer
        BlockPos pos = blockPos.offset(facing);
		horseEntity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		if (horseEntity.isEntityInsideOpaqueBlock()) {
	        pos = pos.offset(facing);
			horseEntity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		}

		world.spawnEntity(horseEntity);

		itemStack.removeSubCompound("StoredEntityData");

		player.sendMessage(new TextComponentString(optionalNamePrefix + " has been restored near you."));
		return EnumActionResult.SUCCESS;

	}


	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

		Utility.debugMsg(1, "HorsePouch: Restore at player");

		if (world.isRemote) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
		}

		ItemStack itemStack = player.getHeldItem(hand);

		NBTTagCompound entityData = itemStack.getSubCompound("StoredEntityData");
		if (entityData == null || entityData.hasNoTags()) {
			return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
		}
		
		String optionalNamePrefix = "Your horse ";
		if (entityData.hasKey("CustomName", 8)) { // Correct method and tag type
			optionalNamePrefix = entityData.getString("CustomName"); // Correct method and tag type
		}
		
		EntityHorse horseEntity = new EntityHorse(world);
		horseEntity.deserializeNBT(entityData);
		
		horseEntity.setPosition(player.posX, player.posY, player.posZ);
		int x = 4;
		world.spawnEntity(horseEntity);
		itemStack.removeSubCompound("StoredEntityData");
		player.sendMessage(new TextComponentString(optionalNamePrefix + " has been restored at your location."));

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));

	}

}
