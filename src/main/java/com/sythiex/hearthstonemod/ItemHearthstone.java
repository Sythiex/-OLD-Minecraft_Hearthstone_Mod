package com.sythiex.hearthstonemod;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHearthstone extends Item
{
	public static String name = "hearthstone";
	
	public static final int maxCooldown = 36000; // 30min
	public static final int maxCastTime = 200; // 10sec
	
	public ItemHearthstone()
	{
		super();
		setUnlocalizedName(name);
		setRegistryName(name);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		// server side
		if(!world.isRemote)
		{
			NBTTagCompound tag = itemStack.getTagCompound();
			
			// if item has tag, decrement cooldown
			if(tag != null)
			{
				int cooldown = tag.getInteger("cooldown");
				if(cooldown > 0)
				{
					cooldown--;
					tag.setInteger("cooldown", cooldown);
				}
			}
			// if no tag, add a tag
			else
			{
				tag = new NBTTagCompound();
				tag.setInteger("cooldown", 0);
				tag.setInteger("castTime", 0);
				tag.setInteger("bedX", 0);
				tag.setInteger("bedY", 0);
				tag.setInteger("bedZ", 0);
				tag.setInteger("bedDimension", 0);
				tag.setDouble("prevX", -1);
				tag.setDouble("prevY", -1);
				tag.setDouble("prevZ", -1);
				tag.setBoolean("locationSet", false);
				tag.setBoolean("isCasting", false);
				tag.setBoolean("stopCasting", false);
			}
			
			// if player is casting
			if(tag.getBoolean("isCasting") && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				
				// check if player is holding hearthstone
				ItemStack heldItem = player.getHeldItemMainhand();
				if(heldItem != null)
				{
					if(heldItem != itemStack)
					{
						tag.setBoolean("stopCasting", true);
					}
				}
				else
					tag.setBoolean("stopCasting", true);
				
				// detect player movement
				double diffX = Math.abs(tag.getDouble("prevX") - player.posX);
				double diffY = Math.abs(tag.getDouble("prevY") - player.posY);
				double diffZ = Math.abs(tag.getDouble("prevZ") - player.posZ);
				// if player moves or swaps items cancel cast
				if(((diffX > 0.05 || diffY > 0.05 || diffZ > 0.05) && tag.getDouble("prevY") != -1) || tag.getBoolean("stopCasting"))
				{
					tag.setInteger("castTime", 0);
					tag.setBoolean("isCasting", false);
					tag.setBoolean("stopCasting", false);
					player.addChatMessage(new TextComponentTranslation("msg.hearthstoneCastCanceled.txt"));
				}
				else
				{
					// increment cast time
					tag.setInteger("castTime", tag.getInteger("castTime") + 1);
				}
				
				// initiate tp after casting
				if(tag.getInteger("castTime") >= maxCastTime)
				{
					// stop and reset cast time
					tag.setInteger("castTime", 0);
					tag.setBoolean("isCasting", false);
					
					world.playSound(null, player.posX, player.posY, player.posZ, HearthstoneMod.castSoundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
					
					// if player is not in same dimension as bed, travel to that dimension
					int dimension = tag.getInteger("bedDimension");
					if(dimension != player.dimension)
					{
						player.changeDimension(dimension);
					}
					
					// get bed location
					int bedX = tag.getInteger("bedX");
					int bedY = tag.getInteger("bedY");
					int bedZ = tag.getInteger("bedZ");
					BlockPos bedPos = new BlockPos(bedX, bedY, bedZ);
					IBlockState state = world.getBlockState(bedPos);
					Block block = state.getBlock();
					
					// checks if bed is still there
					if(block.isBed(state, world, bedPos, player))
					{
						// find open spaces around bed
						boolean north = player.worldObj.getBlockState(bedPos.north()).getBlock().canSpawnInBlock();
						boolean north1 = player.worldObj.getBlockState(bedPos.north().up()).getBlock().canSpawnInBlock();
						
						boolean east = player.worldObj.getBlockState(bedPos.east()).getBlock().canSpawnInBlock();
						boolean east1 = player.worldObj.getBlockState(bedPos.east().up()).getBlock().canSpawnInBlock();
						
						boolean south = player.worldObj.getBlockState(bedPos.south()).getBlock().canSpawnInBlock();
						boolean south1 = player.worldObj.getBlockState(bedPos.south().up()).getBlock().canSpawnInBlock();
						
						boolean west = player.worldObj.getBlockState(bedPos.west()).getBlock().canSpawnInBlock();
						boolean west1 = player.worldObj.getBlockState(bedPos.west().up()).getBlock().canSpawnInBlock();
						
						// tp player next to bed
						if(north && north1)
						{
							player.setPositionAndUpdate(bedPos.north().getX() + 0.5, bedPos.north().getY(), bedPos.north().getZ() + 0.5);
						}
						else if(east && east1)
						{
							player.setPositionAndUpdate(bedPos.east().getX() + 0.5, bedPos.east().getY(), bedPos.east().getZ() + 0.5);
						}
						else if(south && south1)
						{
							player.setPositionAndUpdate(bedPos.south().getX() + 0.5, bedPos.south().getY(), bedPos.south().getZ() + 0.5);
						}
						else if(west && west1)
						{
							player.setPositionAndUpdate(bedPos.west().getX() + 0.5, bedPos.west().getY(), bedPos.west().getZ() + 0.5);
						}
						// if no open space, tp player on top of bed
						else
						{
							player.setPositionAndUpdate(bedX + 0.5, bedY + 1, bedZ + 0.5);
						}
						
						world.playSound(null, player.posX, player.posY, player.posZ, HearthstoneMod.impactSoundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
						
						// sets hearthstone on cooldown
						tag.setInteger("cooldown", maxCooldown);
					}
					// tps player to where bed was, then breaks link
					else
					{
						player.setPositionAndUpdate(bedX + 0.5, bedY + 1, bedZ + 0.5);
						world.playSound(null, player.posX, player.posY, player.posZ, HearthstoneMod.impactSoundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
						// sets hearthstone on cooldown
						tag.setInteger("cooldown", maxCooldown);
						tag.setBoolean("locationSet", false);
						// informs player of broken link
						player.addChatMessage(new TextComponentTranslation("msg.hearthstoneMissingBed.txt"));
					}
				}
			}
			// record position of player for detecting movement
			tag.setDouble("prevX", entity.posX);
			tag.setDouble("prevY", entity.posY);
			tag.setDouble("prevZ", entity.posZ);
			
			// save tag
			itemStack.setTagCompound(tag);
		}
		// client side
		/*
		else if(world.isRemote)
		{
			if(entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				NBTTagCompound tag = itemStack.getTagCompound();
				if(tag != null)
				{
					// if cast has started play channel sound
					if(tag.getInteger("castTime") == 1)
					{
						System.out.println("playing sound");
						// TODO doesnt always trigger on client
						Minecraft.getMinecraft().getSoundHandler().playSound(new ChannelPositionedSound(player));
					}
				}
			}
		}
		*/
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote)
		{
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			
			// if not sneaking
			if(!player.isSneaking())
			{
				// if location is set
				if(tagCompound.getBoolean("locationSet"))
				{
					int cooldown = tagCompound.getInteger("cooldown");
					
					// if off cooldown
					if(cooldown <= 0)
					{
						// if player is not casting, start casting
						if(!tagCompound.getBoolean("isCasting"))
						{
							tagCompound.setBoolean("isCasting", true);
						}
					}
					// if on cooldown
					else
					{
						player.addChatMessage(new TextComponentTranslation("msg.hearthstoneOnCooldown.txt"));
					}
				}
				// if location is not set
				else
				{
					player.addChatMessage(new TextComponentTranslation("msg.hearthstoneNoBed.txt"));
				}
			}
			// save tag
			itemStack.setTagCompound(tagCompound);
		}
		return new ActionResult(EnumActionResult.PASS, itemStack);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos blockPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			
			// if sneaking
			if(player.isSneaking())
			{
				// checks if block right clicked is bed
				IBlockState state = world.getBlockState(blockPos);
				if(world.getBlockState(blockPos).getBlock().isBed(state, world, blockPos, player))
				{
					// links bed to hearthstone
					tagCompound.setInteger("bedX", blockPos.getX());
					tagCompound.setInteger("bedY", blockPos.getY());
					tagCompound.setInteger("bedZ", blockPos.getZ());
					tagCompound.setInteger("bedDimension", player.dimension);
					tagCompound.setBoolean("locationSet", true);
					player.addChatMessage(new TextComponentTranslation("msg.hearthstoneLinked.txt"));
				}
				// save tag
				itemStack.setTagCompound(tagCompound);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack itemStack)
	{
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if(tagCompound != null)
		{
			return tagCompound.getInteger("cooldown") > 0 || tagCompound.getInteger("castTime") > 0;
		}
		
		return false;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack itemStack)
	{
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if(tagCompound.getInteger("cooldown") > 0)
			return (double) tagCompound.getInteger("cooldown") / (double) maxCooldown;
		else
			return (double) 1 - (tagCompound.getInteger("castTime") / (double) maxCastTime);
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
	{
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if(tagCompound != null)
		{
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			int cooldown = tagCompound.getInteger("cooldown");
			float minutesExact, secondsExact;
			int minutes, seconds;
			minutesExact = cooldown / 1200;
			minutes = (int) minutesExact;
			secondsExact = cooldown / 20;
			seconds = (int) (secondsExact - (minutes * 60));
			list.add("Cooldown: " + minutes + " minutes " + seconds + " seconds");
		}
	}
}