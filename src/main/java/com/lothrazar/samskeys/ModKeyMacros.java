package com.lothrazar.samskeys;

import java.util.ArrayList; 

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;     

import com.lothrazar.samskeys.proxy.*;  

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
  
@Mod(modid = ModKeyMacros.MODID, useMetadata = true )  
public class ModKeyMacros
{
	public static final String MODID = "samskeys"; 
	public static final String TEXTURE_LOCATION = MODID + ":";
	@Instance(value = MODID)
	public static ModKeyMacros instance;
	@SidedProxy(clientSide="com.lothrazar.samskeys.proxy.ClientProxy", serverSide="com.lothrazar.samskeys.proxy.CommonProxy")
	public static CommonProxy proxy;   
	public static Logger logger; 
	public static SimpleNetworkWrapper network;  
  
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{ 
		logger = event.getModLog();  

    	network = NetworkRegistry.INSTANCE.newSimpleChannel( MODID );     	
    	
    	network.registerMessage(MessageKeyPressed.class, MessageKeyPressed.class, MessageKeyPressed.ID, Side.SERVER);

		this.registerEventHandlers();  
	}
        
	@EventHandler
	public void onInit(FMLInitializationEvent event)
	{      
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandBindMacro());
	}
	
	private void registerEventHandlers() 
	{ 
		FMLCommonHandler.instance().bus().register(instance); 
		MinecraftForge.EVENT_BUS.register(instance);   
	}
	
	public static void incrementPlayerIntegerNBT(EntityPlayer player, String prop, int inc)
	{
		int prev = player.getEntityData().getInteger(prop);
		prev += inc; 
		player.getEntityData().setInteger(prop, prev);
	}
	
	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone event) 
	{ 
		if(event.wasDeath)//false means switched dimensions
		{
			String macro;
			for(int k = CommandBindMacro.KMIN; k <= CommandBindMacro.KMAX; k++)
			{
				macro = CommandBindMacro.getPlayerMacro(event.original,CommandBindMacro.KEY_MACRO_base + k);

				CommandBindMacro.setPlayerMacro(event.entityPlayer, k, macro);
			}
		}
	}
	
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) 
    {   
		int macroNumber = -1;
        if(ClientProxy.keyBindMacro1.isPressed())
        {
        	macroNumber = 1;
        }
        else if(ClientProxy.keyBindMacro2.isPressed())
        {
        	macroNumber = 2;
        }
        else if(ClientProxy.keyBindMacro3.isPressed())
        {
        	macroNumber = 3;
        }
        else if(ClientProxy.keyBindMacro4.isPressed())
        {
        	macroNumber = 4;
        }
        
        if(macroNumber > 0)
        	ModKeyMacros.network.sendToServer( new MessageKeyPressed(macroNumber));
    } 
	
	public static void playSoundAt(World world,BlockPos pos, String sound)
	{ 
		world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, 1.0F, 1.0F, false);
	}
	
	public static ArrayList<Block> getBlockListFromCSV(String csv)
	{
		 ArrayList<Block> blocks = new ArrayList<Block>();
		 String[] ids = csv.split(",");  
		 Block b; 
		 
		 for(String id : ids)
		 {
			 b = Block.getBlockFromName(id);
			 
			 if(b == null)
			 {
				 ModKeyMacros.logger.log(Level.WARN, "getBlockListFromCSV : Block not found : "+id);
			 }
			 else 
			 {
				 blocks.add(b);
			 }
		 } 
		 
		 return blocks;
	}
	public static void playSoundAt(Entity player, String sound)
	{ 
		player.worldObj.playSoundAtEntity(player, sound, 1.0F, 1.0F);
	}
 
	public static String lang(String name)
	{
		return StatCollector.translateToLocal(name);
	}

	public static void addChatMessage(String string) 
	{ 
		addChatMessage(new ChatComponentTranslation(string)); 
	}
	public static void addChatMessage(IChatComponent string) 
	{ 
		 Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(string); 
	}
	public static void addChatMessage(EntityPlayer player,String string) 
	{ 
		player.addChatMessage(new ChatComponentTranslation(string));
	}
	
	public static EnumFacing getPlayerFacing(EntityPlayer player) 
	{
    	int yaw = (int)player.rotationYaw;

        if (yaw<0)              //due to the yaw running a -360 to positive 360
           yaw+=360;    //not sure why it's that way

        yaw += 22;     //centers coordinates you may want to drop this line
        yaw %= 360;  //and this one if you want a strict interpretation of the zones

        int facing = yaw/45;   //  360degrees divided by 45 == 8 zones
       
		return EnumFacing.getHorizontal( facing/2 );
	}
	public static void spawnParticle(World world, EnumParticleTypes type, BlockPos pos)
	{
		spawnParticle(world,type,pos.getX(),pos.getY(),pos.getZ());
	}

	public static void spawnParticle(World world, EnumParticleTypes type, double x, double y, double z)
	{ 
		//http://www.minecraftforge.net/forum/index.php?topic=9744.0
		for(int countparticles = 0; countparticles <= 10; ++countparticles)
		{
			world.spawnParticle(type, x + (world.rand.nextDouble() - 0.5D) * (double)0.8, y + world.rand.nextDouble() * (double)1.5 - (double)0.1, z + (world.rand.nextDouble() - 0.5D) * (double)0.8, 0.0D, 0.0D, 0.0D);
		} 
    }
	public static void execute(EntityPlayer player, String cmd)
	{
		MinecraftServer.getServer().getCommandManager().executeCommand(player, cmd);
	}

}
