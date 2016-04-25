package com.sythiex.hearthstonemod;

import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityParticleChannelLeaf extends EntityFX
{
	public EntityPlayer player;
	
	public final float degreesPerTick = 6;
	private float degrees = 0;
	
	public EntityParticleChannelLeaf(EntityPlayer player, World world, double x, double y, double z, double motionX, double motionY,
			double motionZ)
	{
		super(world, x, y, z, motionX, motionY, motionZ);
		this.particleRed = 0x00;
		this.particleGreen = 0x88;
		this.particleBlue = 0x00;
		this.setParticleTextureIndex(128 + world.rand.nextInt(8));
		this.setSize(0.02F, 0.02F);
		this.particleScale = 1.0F;
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.particleMaxAge = 60;
		this.noClip = true;
		this.player = player;
	}
	
	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		degrees += degreesPerTick;
		posX = prevPosX + (Math.cos(degrees) / 5);
		posZ = prevPosZ + (Math.sin(degrees) / 5);
		
		ItemStack currentItem = player.inventory.getCurrentItem();
		ItemHearthstone hearthstone = (ItemHearthstone) currentItem.getItem();
		if(hearthstone != null)
		{
			NBTTagCompound tagCompound = currentItem.getTagCompound();
			if(!tagCompound.getBoolean("isCasting"))
			{
				this.setDead();
			}
		}
		
		if(this.particleMaxAge-- <= 0)
		{
			this.setDead();
		}
	}
}
