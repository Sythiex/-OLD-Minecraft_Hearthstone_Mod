package com.sythiex.hearthstonemod;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemHearthstone extends Item
{
	public static int maxCooldown = 36000; // 30min
	public static int maxCastTime = 200; // 10sec
	
	private boolean castFlag = false; // used to stop casting
	
	private double prevX = 0;
	private double prevY = 0;
	private double prevZ = 0;
	
	public ItemHearthstone()
	{
		super();
		setUnlocalizedName("hearthstone");
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if(!world.isRemote)
		{
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			if(tagCompound != null)
			{
				int cooldown = tagCompound.getInteger("cooldown");
				if(cooldown > 0)
				{
					cooldown--;
					tagCompound.setInteger("cooldown", cooldown);
				}
			}
			else
			{
				tagCompound = new NBTTagCompound();
				tagCompound.setInteger("cooldown", 0);
				tagCompound.setInteger("castTime", 0);
				tagCompound.setInteger("bedX", 0);
				tagCompound.setInteger("bedY", 0);
				tagCompound.setInteger("bedZ", 0);
				tagCompound.setInteger("bedDimension", 0);
				tagCompound.setBoolean("locationSet", false);
				tagCompound.setBoolean("isCasting", false);
				// tagCompound.setInteger("distance", 0);
			}
			
			if(tagCompound.getBoolean("isCasting") && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				int castTime = tagCompound.getInteger("castTime") + 1;
				tagCompound.setInteger("castTime", castTime);
				
				if(player.ticksExisted % 5 == 0)
				{
					HearthstoneMod.proxy.generateChannelParticles(player);
				}
				
				double diffX = Math.abs(prevX - player.posX);
				double diffY = Math.abs(prevY - player.posY);
				double diffZ = Math.abs(prevZ - player.posZ);
				// if player moves cancel cast
				if(((diffX > 0.05 || diffY > 0.05 || diffZ > 0.05) && prevX != 0) || castFlag)
				{
					// Minecraft.getMinecraft().getSoundHandler().stopSound(channelSound);
					tagCompound.setInteger("castTime", 0);
					tagCompound.setBoolean("isCasting", false);
					player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneCastCanceled.txt"));
					
					if(castFlag)
					{
						castFlag = false;
					}
				}
				
				// initiate tp after casting
				if(tagCompound.getInteger("castTime") >= maxCastTime)
				{
					// Minecraft.getMinecraft().getSoundHandler().stopSound(channelSound);
					tagCompound.setInteger("castTime", 0);
					tagCompound.setBoolean("isCasting", false);
					
					world.playSoundEffect(prevX, prevY, prevZ, "hearthstonemod:hearthstoneCast", 1, 1);
					
					int dimension = tagCompound.getInteger("bedDimension");
					// if player is not in same dimension as bed, travel to that dimension
					if(dimension != player.dimension)
					{
						player.travelToDimension(dimension);
					}
					
					int bedX = tagCompound.getInteger("bedX");
					int bedY = tagCompound.getInteger("bedY");
					int bedZ = tagCompound.getInteger("bedZ");
					BlockPos bedPos = new BlockPos(bedX, bedY, bedZ);
					
					// checks if bed is still there
					if(player.worldObj.getBlockState(bedPos).getBlock().isBed(player.worldObj, bedPos, player))
					{
						Material material1 = player.worldObj.getBlockState(bedPos.north()).getBlock().getMaterial();
						Material material2 = player.worldObj.getBlockState(bedPos.north().up()).getBlock().getMaterial();
						
						Material material3 = player.worldObj.getBlockState(bedPos.east()).getBlock().getMaterial();
						Material material4 = player.worldObj.getBlockState(bedPos.east().up()).getBlock().getMaterial();
						
						Material material5 = player.worldObj.getBlockState(bedPos.south()).getBlock().getMaterial();
						Material material6 = player.worldObj.getBlockState(bedPos.south().up()).getBlock().getMaterial();
						
						Material material7 = player.worldObj.getBlockState(bedPos.west()).getBlock().getMaterial();
						Material material8 = player.worldObj.getBlockState(bedPos.west().up()).getBlock().getMaterial();
						
						// finds open space around bed and tps player
						if(!material1.isSolid() && !material1.isLiquid() && !material2.isSolid() && !material2.isLiquid())
						{
							player.setPositionAndUpdate(bedX - 1 + 0.5, bedY, bedZ + 0.5);
						}
						else if(!material3.isSolid() && !material3.isLiquid() && !material4.isSolid() && !material4.isLiquid())
						{
							player.setPositionAndUpdate(bedX + 1 + 0.5, bedY, bedZ + 0.5);
						}
						else if(!material5.isSolid() && !material5.isLiquid() && !material6.isSolid() && !material6.isLiquid())
						{
							player.setPositionAndUpdate(bedX + 0.5, bedY, bedZ - 1 + 0.5);
						}
						else if(!material7.isSolid() && !material7.isLiquid() && !material8.isSolid() && !material8.isLiquid())
						{
							player.setPositionAndUpdate(bedX + 0.5, bedY, bedZ + 1 + 0.5);
						}
						// defaults to tp player on top of bed
						else
						{
							player.setPositionAndUpdate(bedX + 0.5, bedY + 1, bedZ + 0.5);
						}
						
						world.playSoundEffect(entity.posX, entity.posY, entity.posZ, "hearthstonemod:hearthstoneImpact", 1, 1);
						tagCompound.setInteger("cooldown", maxCooldown); // sets hearthstone on
																			// cooldown
					}
					// tps player to where bed was, then breaks link
					else
					{
						player.setPositionAndUpdate(bedX + 0.5, bedY + 1, bedZ + 0.5);
						world.playSoundEffect(entity.posX, entity.posY, entity.posZ, "hearthstonemod:hearthstoneImpact", 1, 1);
						tagCompound.setInteger("cooldown", maxCooldown); // sets hearthstone on
																			// cooldown
						tagCompound.setBoolean("locationSet", false);
						// informs player of broken link
						player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneMissingBed.txt"));
					}
				}
			}
			
			prevX = entity.posX;
			prevY = entity.posY;
			prevZ = entity.posZ;
			
			itemStack.setTagCompound(tagCompound);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
		{
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			// not sneaking
			if(!player.isSneaking())
			{
				// location is set
				if(tagCompound.getBoolean("locationSet"))
				{
					int cooldown = tagCompound.getInteger("cooldown");
					// off cooldown
					if(cooldown <= 0)
					{
						// if player is not casting, start casting
						if(!tagCompound.getBoolean("isCasting"))
						{
							tagCompound.setBoolean("isCasting", true);
							// channelSound = new TargetedEntitySound(player, new
							// ResourceLocation("hearthstonemod:hearthstoneChannel"));
							// Minecraft.getMinecraft().getSoundHandler().playSound(channelSound);
						}
					}
					// on cooldown
					else
					{
						player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneOnCooldown.txt"));
					}
				}
				// location is not set
				else
				{
					player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneNoBed.txt"));
				}
			}
			itemStack.setTagCompound(tagCompound);
		}
		return itemStack;
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos blockPos, EnumFacing side, float sideX,
			float sideY, float sideZ)
	{
		if(!world.isRemote)
		{
			NBTTagCompound tagCompound = itemStack.getTagCompound();
			// sneaking and hearthstone is not linked
			if(player.isSneaking())
			{
				// checks if block right clicked is bed, then links hearthstone
				if(world.getBlockState(blockPos).getBlock().isBed(world, blockPos, player))
				{
					tagCompound.setInteger("bedX", blockPos.getX());
					tagCompound.setInteger("bedY", blockPos.getY());
					tagCompound.setInteger("bedZ", blockPos.getZ());
					tagCompound.setInteger("bedDimension", player.dimension);
					tagCompound.setBoolean("locationSet", true);
					player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneLinked.txt"));
				}
			}
			itemStack.setTagCompound(tagCompound);
			return true;
		}
		else
			return false;
	}
	
	public void stopCasting()
	{
		this.castFlag = true;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack itemStack)
	{
		NBTTagCompound tagCompound = itemStack.getTagCompound();
		if(tagCompound != null)
		{
			return tagCompound.getInteger("cooldown") > 0 || tagCompound.getInteger("castTime") > 0;
		}
		else
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
			// list.add("Distance teleported: " + tagCompound.getInteger("distance"));
			/*
			 * sprinting adds .1 exhaustion per meter, jumping adds .2 exhaustion
			 * assuming the player jumps often while traveling, every 5m traveled adds ~.7 exhaustion, or .14 exhaustion per
			 * meter
			 * every 4.0 exhaustion subtracts 1 point of saturation, so every 28.57m consumes 1 saturation (4.0/.14)
			 * steak adds 12.8 saturation, so traveling 365.71m consumes a steak's worth of saturation (28.57*12.8)
			 * distance traveled * (1/365.71) gives the number of steaks that would be used to travel that distance
			 */
			// list.add("Steaks saved: " + df.format(tagCompound.getInteger("distance") * .002734));
		}
	}
}
