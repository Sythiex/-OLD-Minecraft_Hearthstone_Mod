package com.sythiex.hearthstonemod;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class TargetedEntitySound extends MovingSound
{
	private final Entity entity;
	private static int maxPlayTime = ItemHearthstone.maxCastTime; // 10sec
	private int count = 0;
	
	public TargetedEntitySound(Entity entity, ResourceLocation resourceLocation)
	{
		super(resourceLocation);
		this.entity = entity;
		this.field_147660_d = (float) entity.field_70165_t;
		this.field_147661_e = (float) entity.field_70163_u;
		this.field_147658_f = (float) entity.field_70161_v;
		this.field_147659_g = true;
		this.field_147662_b = 1.0F;
		this.field_147665_h = 0;
	}
	
	@Override
	public void func_73660_a()
	{
		count++;
		if(count > maxPlayTime)
		{
			this.field_147668_j = true;
		}
	}
	
	@Override
	public boolean func_147667_k()
	{
		return this.field_147668_j;
	}
}