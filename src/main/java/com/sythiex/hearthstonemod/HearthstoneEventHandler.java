package com.sythiex.hearthstonemod;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class HearthstoneEventHandler
{
	/** cancels hearthstone channel on damage */
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public void onEvent(LivingHurtEvent event)
	{
		if(event.entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.entity;
			ItemStack currentItem = player.inventory.getCurrentItem();
			ItemHearthstone hearthstone = (ItemHearthstone) currentItem.getItem();
			if(hearthstone != null)
			{
				hearthstone.stopCasting();
			}
		}
	}
}
