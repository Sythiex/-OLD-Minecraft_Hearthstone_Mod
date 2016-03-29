package com.sythiex.hearthstonemod.proxy;

import com.sythiex.hearthstonemod.EntityParticleChannelLeaf;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy
{
	@Override
	public void generateChannelParticles(EntityPlayer player)
	{
		EntityFX particleLeaf1 = new EntityParticleChannelLeaf(player, player.worldObj, player.posX + 0.15,
				player.posY + 0.5 + player.worldObj.rand.nextFloat() * player.height, player.posZ + 0.75, 0, 0, 0);
		Minecraft.getMinecraft().effectRenderer.addEffect(particleLeaf1);
	}
}