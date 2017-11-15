package com.sythiex.hearthstonemod;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChannelPositionedSound extends PositionedSound implements ITickableSound
{
	protected EntityPlayer player;
	protected boolean donePlaying = false;
	protected int uptime = 0;
	
	protected ChannelPositionedSound(EntityPlayer player)
	{
		super(HearthstoneMod.channelSoundEvent, SoundCategory.PLAYERS);
		this.player = player;
		this.repeat = true;
		this.repeatDelay = 0;
		this.xPosF = (float) player.posX;
		this.yPosF = (float) player.posY;
		this.zPosF = (float) player.posZ;
	}

	@Override
	public void update()
	{
		this.uptime++;
		
		// kill sound if running too long
		if(this.uptime >= 200)
			this.donePlaying = true;
		
		ItemStack stack = this.player.getHeldItemMainhand();
		if(stack != null)
		{
			// check if item is a hearthstone
			if(stack.getItem() instanceof ItemHearthstone)
			{
				NBTTagCompound tag = stack.getTagCompound();
				if(tag != null)
				{
					// is player casting
					if(tag.getBoolean("isCasting"));
					{
						return;
					}
				}
			}
		}
		this.donePlaying = true;
	}

	@Override
	public boolean isDonePlaying()
	{
		return this.donePlaying;
	}
}