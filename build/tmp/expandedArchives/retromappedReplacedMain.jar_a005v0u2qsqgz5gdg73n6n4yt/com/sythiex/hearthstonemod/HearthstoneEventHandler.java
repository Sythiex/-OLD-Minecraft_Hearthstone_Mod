package com.sythiex.hearthstonemod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HearthstoneEventHandler
{
	/** cancels hearthstone channel on damage */
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public void onEvent(LivingHurtEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			ItemStack currentItem = player.field_71071_by.func_70448_g();
			if(currentItem != null)
			{
				if(currentItem.func_77973_b() instanceof ItemHearthstone)
				{
					ItemHearthstone hearthstone = (ItemHearthstone) currentItem.func_77973_b();
					hearthstone.stopCasting();
				}
			}
		}
	}
}