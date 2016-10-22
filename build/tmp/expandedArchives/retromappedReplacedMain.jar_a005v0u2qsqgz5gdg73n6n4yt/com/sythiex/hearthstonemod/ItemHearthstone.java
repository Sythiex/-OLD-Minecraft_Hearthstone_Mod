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
	
	// public TargetedEntitySound channelSound;
	
	public ItemHearthstone()
	{
		super();
		func_77655_b("hearthstone");
		func_77625_d(1);
		func_77637_a(CreativeTabs.field_78040_i);
	}
	
	@Override
	public void func_77663_a(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if(!world.field_72995_K)
		{
			NBTTagCompound tagCompound = itemStack.func_77978_p();
			if(tagCompound != null)
			{
				int cooldown = tagCompound.func_74762_e("cooldown");
				if(cooldown > 0)
				{
					cooldown--;
					tagCompound.func_74768_a("cooldown", cooldown);
				}
			}
			else
			{
				tagCompound = new NBTTagCompound();
				tagCompound.func_74768_a("cooldown", 0);
				tagCompound.func_74768_a("castTime", 0);
				tagCompound.func_74768_a("bedX", 0);
				tagCompound.func_74768_a("bedY", 0);
				tagCompound.func_74768_a("bedZ", 0);
				tagCompound.func_74768_a("bedDimension", 0);
				tagCompound.func_74757_a("locationSet", false);
				tagCompound.func_74757_a("isCasting", false);
				// tagCompound.setInteger("distance", 0);
			}
			
			if(tagCompound.func_74767_n("isCasting") && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) entity;
				int castTime = tagCompound.func_74762_e("castTime") + 1;
				tagCompound.func_74768_a("castTime", castTime);
				
				if(player.field_70173_aa % 5 == 0)
				{
					HearthstoneMod.proxy.generateChannelParticles(player);
				}
				
				double diffX = Math.abs(prevX - player.field_70165_t);
				double diffY = Math.abs(prevY - player.field_70163_u);
				double diffZ = Math.abs(prevZ - player.field_70161_v);
				// if player moves cancel cast
				if(((diffX > 0.05 || diffY > 0.05 || diffZ > 0.05) && prevX != 0) || castFlag)
				{
					// Minecraft.getMinecraft().getSoundHandler().stopSound(channelSound);
					tagCompound.func_74768_a("castTime", 0);
					tagCompound.func_74757_a("isCasting", false);
					player.func_145747_a(new ChatComponentTranslation("msg.hearthstoneCastCanceled.txt"));
					
					if(castFlag)
					{
						castFlag = false;
					}
				}
				
				// initiate tp after casting
				if(tagCompound.func_74762_e("castTime") >= maxCastTime)
				{
					// Minecraft.getMinecraft().getSoundHandler().stopSound(channelSound);
					tagCompound.func_74768_a("castTime", 0);
					tagCompound.func_74757_a("isCasting", false);
					
					world.func_72908_a(prevX, prevY, prevZ, "hearthstonemod:hearthstoneCast", 1, 1);
					
					int dimension = tagCompound.func_74762_e("bedDimension");
					// if player is not in same dimension as bed, travel to that dimension
					if(dimension != player.field_71093_bK)
					{
						player.func_71027_c(dimension);
					}
					
					int bedX = tagCompound.func_74762_e("bedX");
					int bedY = tagCompound.func_74762_e("bedY");
					int bedZ = tagCompound.func_74762_e("bedZ");
					BlockPos bedPos = new BlockPos(bedX, bedY, bedZ);
					
					// checks if bed is still there
					if(player.field_70170_p.func_180495_p(bedPos).func_177230_c().isBed(player.field_70170_p, bedPos, player))
					{
						Material material1 = player.field_70170_p.func_180495_p(bedPos.func_177978_c()).func_177230_c().func_149688_o();
						Material material2 = player.field_70170_p.func_180495_p(bedPos.func_177978_c().func_177984_a()).func_177230_c().func_149688_o();
						
						Material material3 = player.field_70170_p.func_180495_p(bedPos.func_177974_f()).func_177230_c().func_149688_o();
						Material material4 = player.field_70170_p.func_180495_p(bedPos.func_177974_f().func_177984_a()).func_177230_c().func_149688_o();
						
						Material material5 = player.field_70170_p.func_180495_p(bedPos.func_177968_d()).func_177230_c().func_149688_o();
						Material material6 = player.field_70170_p.func_180495_p(bedPos.func_177968_d().func_177984_a()).func_177230_c().func_149688_o();
						
						Material material7 = player.field_70170_p.func_180495_p(bedPos.func_177976_e()).func_177230_c().func_149688_o();
						Material material8 = player.field_70170_p.func_180495_p(bedPos.func_177976_e().func_177984_a()).func_177230_c().func_149688_o();
						
						// finds open space around bed and tps player
						if(!material1.func_76220_a() && !material1.func_76224_d() && !material2.func_76220_a() && !material2.func_76224_d())
						{
							player.func_70634_a(bedX - 1 + 0.5, bedY, bedZ + 0.5);
						}
						else if(!material3.func_76220_a() && !material3.func_76224_d() && !material4.func_76220_a() && !material4.func_76224_d())
						{
							player.func_70634_a(bedX + 1 + 0.5, bedY, bedZ + 0.5);
						}
						else if(!material5.func_76220_a() && !material5.func_76224_d() && !material6.func_76220_a() && !material6.func_76224_d())
						{
							player.func_70634_a(bedX + 0.5, bedY, bedZ - 1 + 0.5);
						}
						else if(!material7.func_76220_a() && !material7.func_76224_d() && !material8.func_76220_a() && !material8.func_76224_d())
						{
							player.func_70634_a(bedX + 0.5, bedY, bedZ + 1 + 0.5);
						}
						// defaults to tp player on top of bed
						else
						{
							player.func_70634_a(bedX + 0.5, bedY + 1, bedZ + 0.5);
						}
						
						world.func_72908_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, "hearthstonemod:hearthstoneImpact", 1, 1);
						tagCompound.func_74768_a("cooldown", maxCooldown); // sets hearthstone on
																			// cooldown
					}
					// tps player to where bed was, then breaks link
					else
					{
						player.func_70634_a(bedX + 0.5, bedY + 1, bedZ + 0.5);
						world.func_72908_a(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v, "hearthstonemod:hearthstoneImpact", 1, 1);
						tagCompound.func_74768_a("cooldown", maxCooldown); // sets hearthstone on
																			// cooldown
						tagCompound.func_74757_a("locationSet", false);
						// informs player of broken link
						player.func_145747_a(new ChatComponentTranslation("msg.hearthstoneMissingBed.txt"));
					}
				}
			}
			
			prevX = entity.field_70165_t;
			prevY = entity.field_70163_u;
			prevZ = entity.field_70161_v;
			
			itemStack.func_77982_d(tagCompound);
		}
	}
	
	@Override
	public ItemStack func_77659_a(ItemStack itemStack, World world, EntityPlayer player)
	{
		if(!world.field_72995_K)
		{
			NBTTagCompound tagCompound = itemStack.func_77978_p();
			// not sneaking
			if(!player.func_70093_af())
			{
				// location is set
				if(tagCompound.func_74767_n("locationSet"))
				{
					int cooldown = tagCompound.func_74762_e("cooldown");
					// off cooldown
					if(cooldown <= 0)
					{
						// if player is not casting, start casting
						if(!tagCompound.func_74767_n("isCasting"))
						{
							tagCompound.func_74757_a("isCasting", true);
							// channelSound = new TargetedEntitySound(player, new
							// ResourceLocation("hearthstonemod:hearthstoneChannel"));
							// Minecraft.getMinecraft().getSoundHandler().playSound(channelSound);
						}
					}
					// on cooldown
					else
					{
						player.func_145747_a(new ChatComponentTranslation("msg.hearthstoneOnCooldown.txt"));
					}
				}
				// location is not set
				else
				{
					player.func_145747_a(new ChatComponentTranslation("msg.hearthstoneNoBed.txt"));
				}
			}
			itemStack.func_77982_d(tagCompound);
		}
		return itemStack;
	}
	
	@Override
	public boolean func_180614_a(ItemStack itemStack, EntityPlayer player, World world, BlockPos blockPos, EnumFacing side, float sideX,
			float sideY, float sideZ)
	{
		if(!world.field_72995_K)
		{
			NBTTagCompound tagCompound = itemStack.func_77978_p();
			// sneaking and hearthstone is not linked
			if(player.func_70093_af())
			{
				// checks if block right clicked is bed, then links hearthstone
				if(world.func_180495_p(blockPos).func_177230_c().isBed(world, blockPos, player))
				{
					tagCompound.func_74768_a("bedX", blockPos.func_177958_n());
					tagCompound.func_74768_a("bedY", blockPos.func_177956_o());
					tagCompound.func_74768_a("bedZ", blockPos.func_177952_p());
					tagCompound.func_74768_a("bedDimension", player.field_71093_bK);
					tagCompound.func_74757_a("locationSet", true);
					player.func_145747_a(new ChatComponentTranslation("msg.hearthstoneLinked.txt"));
				}
			}
			itemStack.func_77982_d(tagCompound);
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
		NBTTagCompound tagCompound = itemStack.func_77978_p();
		if(tagCompound != null)
		{
			return tagCompound.func_74762_e("cooldown") > 0 || tagCompound.func_74762_e("castTime") > 0;
		}
		else
			return false;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack itemStack)
	{
		NBTTagCompound tagCompound = itemStack.func_77978_p();
		if(tagCompound.func_74762_e("cooldown") > 0)
			return (double) tagCompound.func_74762_e("cooldown") / (double) maxCooldown;
		else
			return (double) 1 - (tagCompound.func_74762_e("castTime") / (double) maxCastTime);
	}
	
	@Override
	public void func_77624_a(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
	{
		NBTTagCompound tagCompound = itemStack.func_77978_p();
		if(tagCompound != null)
		{
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			int cooldown = tagCompound.func_74762_e("cooldown");
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
