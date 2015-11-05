package com.sythiex.hearthstonemod;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemHearthstone extends Item
{
	public static int maxCooldown = 24000;
	public static int maxCastTime = 25;
	
	public ItemHearthstone()
	{
		super();
		setUnlocalizedName("hearthstone");
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabTools);
		setTextureName(HearthstoneMod.MODID + ":hearthstone");
	}
	
	/** used to update cooldown and to set default tag values to a new item */
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		if (itemStack.stackTagCompound != null)
		{
			int cooldown = itemStack.stackTagCompound.getInteger("cooldown");
			if(cooldown > 0)
			{
				cooldown--;
				itemStack.stackTagCompound.setInteger("cooldown", cooldown);
			}
		}
		else
		{
			itemStack.stackTagCompound = new NBTTagCompound();
		    itemStack.stackTagCompound.setInteger("cooldown", 0);
		    itemStack.stackTagCompound.setInteger("castTime", 0);
		    itemStack.stackTagCompound.setInteger("distance", 0);
		}
	}
	
	/** teleports the player */
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
    {
		if(!world.isRemote)
		{
			ChunkCoordinates bedCoords = player.getBedLocation(0);
			if(bedCoords != null)
			{
				if(itemStack.stackTagCompound.getInteger("cooldown") == 0)
				{
					if(player.dimension == 0)
						itemStack.stackTagCompound.setInteger("distance", (int) (itemStack.stackTagCompound.getInteger("distance") + (player.getDistance(bedCoords.posX + .5, bedCoords.posY + 1, bedCoords.posZ + .5))));
					else
						player.travelToDimension(0);
					
					player.setPositionAndUpdate(bedCoords.posX + .5, bedCoords.posY + 1, bedCoords.posZ + .5);
					itemStack.stackTagCompound.setInteger("cooldown", maxCooldown);
				}
				
				else
				{
					player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneOnCooldown.txt"));
				}
				return itemStack;
			}
			else
			{
				player.addChatMessage(new ChatComponentTranslation("msg.hearthstoneNoBed.txt"));
				return itemStack;
			}
		}
		else
			return itemStack;
    }
	
	public int getMaxItemUseDuration(ItemStack itemStack)
    {
        return 100;
    }
	
	public boolean showDurabilityBar(ItemStack itemStack)
    {
		if(itemStack.stackTagCompound != null)
			return itemStack.stackTagCompound.getInteger("castTime") > 0 || itemStack.stackTagCompound.getInteger("cooldown") > 0;
		else
			return false;
    }
	
	public double getDurabilityForDisplay(ItemStack itemStack)
    {
		if(itemStack.stackTagCompound.getInteger("cooldown") > 0)
			return (double)itemStack.stackTagCompound.getInteger("cooldown") / (double)maxCooldown;
		else
			return (double)itemStack.stackTagCompound.getInteger("castTime") / (double)maxCastTime;
    }
	
	/*
	public EnumAction getItemUseAction(ItemStack itemStack)
    {
        return EnumAction.bow;
    }
    */
	
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4)
	{
		if(itemStack.stackTagCompound != null)
		{
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			int cooldown = itemStack.stackTagCompound.getInteger("cooldown");
			float minutesExact, secondsExact;
			int minutes, seconds;
			minutesExact = cooldown / 1200;
			minutes = (int)minutesExact;
			secondsExact = cooldown / 20;
			seconds = (int)(secondsExact - (minutes * 60));
			list.add("Cooldown: " + minutes + " minutes " + seconds + " seconds");
			list.add("Distance teleported: " + itemStack.stackTagCompound.getInteger("distance"));
			/*
			 * sprinting adds .1 exhaustion per meter, jumping adds .2 exhaustion
			 * assuming the player jumps often while traveling, every 5m traveled adds ~.7 exhaustion, or .14 exhaustion per meter
			 * every 4.0 exhaustion subtracts 1 point of saturation, so every 28.57m consumes 1 saturation (4.0/.14)
			 * steak adds 12.8 saturation, so traveling 365.71m consumes a steak's worth of saturation (28.57*12.8)
			 * distance traveled * (1/365.71) gives the number of steaks that would be used to travel that distance
			 */
			list.add("Steaks saved: " + df.format(itemStack.stackTagCompound.getInteger("distance") * .002734));
		}
	}
}