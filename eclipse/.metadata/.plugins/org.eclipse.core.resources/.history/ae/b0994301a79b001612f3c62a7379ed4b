package com.sythiex.hearthstonemod;

import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class SoundCasting extends PositionedSound
{
	public SoundCasting(double posX, double posY, double posZ)
	{
		super(new ResourceLocation(HearthstoneMod.MODID + ":hearthstoneChannel"));
		this.xPosF = (float) posX;
		this.yPosF = (float) posY;
		this.zPosF = (float) posZ;
		this.repeat = true;
		this.volume = 1.0F;
		this.field_147665_h = 0; // repeat delay
	}
}