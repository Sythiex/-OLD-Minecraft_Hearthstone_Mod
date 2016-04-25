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
		this.field_70552_h = 0x00;
		this.field_70553_i = 0x88;
		this.field_70551_j = 0x00;
		this.func_70536_a(128 + world.field_73012_v.nextInt(8));
		this.func_70105_a(0.02F, 0.02F);
		this.field_70544_f = 1.0F;
		this.field_70159_w = 0;
		this.field_70181_x = 0;
		this.field_70179_y = 0;
		this.field_70547_e = 60;
		this.field_70145_X = true;
		this.player = player;
	}
	
	/**
	 * Called to update the entity's position/logic.
	 */
	public void func_70071_h_()
	{
		this.field_70169_q = this.field_70165_t;
		this.field_70167_r = this.field_70163_u;
		this.field_70166_s = this.field_70161_v;
		
		degrees += degreesPerTick;
		field_70165_t = field_70169_q + (Math.cos(degrees) / 5);
		field_70161_v = field_70166_s + (Math.sin(degrees) / 5);
		
		ItemStack currentItem = player.field_71071_by.func_70448_g();
		ItemHearthstone hearthstone = (ItemHearthstone) currentItem.func_77973_b();
		if(hearthstone != null)
		{
			NBTTagCompound tagCompound = currentItem.func_77978_p();
			if(!tagCompound.func_74767_n("isCasting"))
			{
				this.func_70106_y();
			}
		}
		
		if(this.field_70547_e-- <= 0)
		{
			this.func_70106_y();
		}
	}
}
