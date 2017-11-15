package com.sythiex.hearthstonemod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HearthstoneEventHandler
{
	/** cancels hearthstone channel on damage */
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public void onEvent(LivingHurtEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			ItemStack currentItem = player.inventory.getCurrentItem();
			if(currentItem != null)
			{
				if(currentItem.getItem() instanceof ItemHearthstone)
				{
					NBTTagCompound tagCompound = currentItem.getTagCompound();
					tagCompound.setBoolean("stopCasting", true);
					currentItem.setTagCompound(tagCompound);
				}
			}
		}
	}
}