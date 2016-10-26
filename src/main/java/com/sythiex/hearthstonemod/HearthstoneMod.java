package com.sythiex.hearthstonemod;

import com.sythiex.hearthstonemod.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = HearthstoneMod.MODID, version = HearthstoneMod.VERSION)
public class HearthstoneMod
{
	@SidedProxy(clientSide = "com.sythiex.hearthstonemod.proxy.ClientProxy", serverSide = "com.sythiex.hearthstonemod.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "hearthstonemod";
	public static final String VERSION = "0.3.1";
	
	public static boolean difficulty;
	
	public static Item hearthstone;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		difficulty = config.getBoolean("Easy Recipe", config.CATEGORY_GENERAL, false,
				"Change to true to use lapis instead of diamonds in the Hearthstone recipe");
		
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new HearthstoneEventHandler());
		
		hearthstone = new ItemHearthstone();
		GameRegistry.registerItem(hearthstone, MODID + "_hearthstone");
		
		if(difficulty)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone),
					new Object[] { "SLS", "LCL", "SLS", 'S', "stone", 'L', "gemLapis", 'C', new ItemStack(Items.compass) }));
		}
		else
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone),
					new Object[] { "SDS", "DCD", "SDS", 'S', "stone", 'D', "gemDiamond", 'C', new ItemStack(Items.compass) }));
		}
	}
}