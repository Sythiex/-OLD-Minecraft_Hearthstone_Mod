package com.sythiex.hearthstonemod.proxy;

import com.sythiex.hearthstonemod.HearthstoneMod;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		ModelLoader.setCustomModelResourceLocation(HearthstoneMod.hearthstone, 0, new ModelResourceLocation(HearthstoneMod.MODID + ":hearthstone"));
	}
}
