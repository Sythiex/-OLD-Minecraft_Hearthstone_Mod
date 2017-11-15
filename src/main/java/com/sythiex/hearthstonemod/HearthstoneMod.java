package com.sythiex.hearthstonemod;

import org.apache.logging.log4j.Logger;

import com.sythiex.hearthstonemod.proxy.CommonProxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid = HearthstoneMod.MODID, name = HearthstoneMod.NAME, version = HearthstoneMod.VERSION, acceptedMinecraftVersions = "[1.10.2]")
public class HearthstoneMod
{
	@SidedProxy(clientSide = "com.sythiex.hearthstonemod.proxy.ClientProxy", serverSide = "com.sythiex.hearthstonemod.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final String MODID = "hearthstonemod";
	public static final String NAME = "Hearthstone Mod";
	public static final String VERSION = "0.4.1";
	
	@Instance(MODID)
	public static HearthstoneMod instance;
	
	public static Logger logger;
	
	public static int recipeDifficulty;
	
	public static Item hearthstone;
	
	public static SoundEvent channelSoundEvent;
	public static SoundEvent castSoundEvent;
	public static SoundEvent impactSoundEvent;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		logger.info("Initializing");
		
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		recipeDifficulty = config.getInt("Recipe Difficulty", config.CATEGORY_GENERAL, 1, 0, 2, "0 = lapis; 1 = diamonds (default); 2 = ender pearls");
		
		config.save();
		
		MinecraftForge.EVENT_BUS.register(new HearthstoneEventHandler());
		
		hearthstone = new ItemHearthstone();
		GameRegistry.register(hearthstone);
		
		channelSoundEvent = new SoundEvent(new ResourceLocation("hearthstonemod", "hearthstoneChannel")).setRegistryName("hearthstoneChannel");
		GameRegistry.register(channelSoundEvent);
		
		castSoundEvent = new SoundEvent(new ResourceLocation("hearthstonemod", "hearthstoneCast")).setRegistryName("hearthstoneCast");
		GameRegistry.register(castSoundEvent);
		
		impactSoundEvent = new SoundEvent(new ResourceLocation("hearthstonemod", "hearthstoneImpact")).setRegistryName("hearthstoneImpact");
		GameRegistry.register(impactSoundEvent);
		
		proxy.preInit(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if(recipeDifficulty == 0)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone), new Object[] { "SLS", "LCL", "SLS", 'S', "stone", 'L', "gemLapis", 'C', new ItemStack(Items.COMPASS) }));
		}
		else if(recipeDifficulty == 1)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone), new Object[] { "SDS", "DCD", "SDS", 'S', "stone", 'D', "gemDiamond", 'C', new ItemStack(Items.COMPASS) }));
		}
		else if(recipeDifficulty == 2)
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(hearthstone), new Object[] { "SES", "ECE", "SES", 'S', "stone", 'E', "enderpearl", 'C', new ItemStack(Items.COMPASS) }));
		}
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
}