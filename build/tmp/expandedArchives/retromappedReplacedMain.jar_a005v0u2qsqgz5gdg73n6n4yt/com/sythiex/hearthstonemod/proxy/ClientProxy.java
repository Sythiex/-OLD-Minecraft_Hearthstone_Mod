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
		EntityFX particleLeaf1 = new EntityParticleChannelLeaf(player, player.field_70170_p, player.field_70165_t + 0.15,
				player.field_70163_u + 0.5 + player.field_70170_p.field_73012_v.nextFloat() * player.field_70131_O, player.field_70161_v + 0.75, 0, 0, 0);
		Minecraft.func_71410_x().field_71452_i.func_78873_a(particleLeaf1);
	}
}