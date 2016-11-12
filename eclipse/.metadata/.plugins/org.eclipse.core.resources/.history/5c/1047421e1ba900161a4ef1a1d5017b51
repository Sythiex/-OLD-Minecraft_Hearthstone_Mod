package com.sythiex.hearthstonemod;

import com.sythiex.hearthstonemod.proxy.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = HearthstoneMod.MODID, version = HearthstoneMod.VERSION)
public class HearthstoneMod
{
	@SidedProxy(clientSide = "com.sythiex.hearthstonemod.proxy.ClientProxy", serverSide = "com.sythiex.hearthstonemod.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "hearthstonemod";
	public static final String VERSION = "0.3.0";
	
	public static boolean difficulty;
	
	public static Item hearthstone;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		difficulty = config.getBoolean("Easy Recipe", config.CATEGORY_GENERAL, false, "Change to true to use lapis instead of diamonds in the Hearthstone recipe");
		
		config.save();
		
		MinecraftForge.EVENT_BUS.register(new HearthstoneEventHandler());
		
		hearthstone = new ItemHearthstone();
		GameRegistry.registerItem(hearthstone, "hearthstone");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(hearthstone, 0, new ModelResourceLocation(MODID + ":" + "hearthstone", "inventory"));
		}
		
		if(difficulty)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone), new Object[] { "SLS", "LCL", "SLS", 'S', "stone", 'L', "gemLapis", 'C', new ItemStack(Items.compass) }));
		}
		else
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone), new Object[] { "SDS", "DCD", "SDS", 'S', "stone", 'D', "gemDiamond", 'C', new ItemStack(Items.compass) }));
		}
	}
}