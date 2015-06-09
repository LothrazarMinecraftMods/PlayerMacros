package com.lothrazar.samskeys;

import java.util.ArrayList; 

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;   
    
import com.lothrazar.samskeys.proxy.*;  

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
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
  
@Mod(modid = ModKeyMacros.MODID, version = ModKeyMacros.VERSION,	name = ModKeyMacros.NAME, useMetadata = true )  
public class ModKeyMacros
{
	public static final String MODID = "samskeys"; 
	public static final String TEXTURE_LOCATION = MODID + ":";
	public static final String VERSION = "1.8-1.0.0";
	public static final String NAME = "Sam's Keys and Macros";
	@Instance(value = MODID)
	public static ModKeyMacros instance;
	@SidedProxy(clientSide="com.lothrazar.samskeys.proxy.ClientProxy", serverSide="com.lothrazar.samskeys.proxy.CommonProxy")
	public static CommonProxy proxy;   
	public static Logger logger; 
	//public static ConfigMacros cfg;
	public static SimpleNetworkWrapper network;  
  
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{ 
		logger = event.getModLog();  
		
		//cfg = new ConfigMacros(new Configuration(event.getSuggestedConfigurationFile()));
	  
    	network = NetworkRegistry.INSTANCE.newSimpleChannel( MODID );     	
    	
    	network.registerMessage(MessageKeyPressed.class, MessageKeyPressed.class, MessageKeyPressed.ID, Side.SERVER);
    //	network.registerMessage(MessagePotion.class, MessagePotion.class, MessagePotion.ID, Side.CLIENT);
  
		this.registerEventHandlers();  
	}
        
	@EventHandler
	public void onInit(FMLInitializationEvent event)
	{      
		proxy.registerRenderers();
	}
	
	private void registerEventHandlers() 
	{ 
		FMLCommonHandler.instance().bus().register(instance); 
		MinecraftForge.EVENT_BUS.register(instance); 
	//	MinecraftForge.TERRAIN_GEN_BUS.register(h);
	//	MinecraftForge.ORE_GEN_BUS.register(h); 
	      
	}
	public static void incrementPlayerIntegerNBT(EntityPlayer player, String prop, int inc)
	{
		int prev = player.getEntityData().getInteger(prop);
		prev += inc; 
		player.getEntityData().setInteger(prop, prev);
	}
   
	@SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) 
    {   
		//TODO: key handler class? maybe a better way to do this than copying the same code??
        if(ClientProxy.keyShiftUp.isPressed() )
        { 	     
        	 ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyShiftUp.getKeyCode()));  
        }        
        else if(ClientProxy.keyShiftDown.isPressed() )
        { 	      
        	 ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyShiftDown.getKeyCode()));  
        }      
        else if(ClientProxy.keyBarDown.isPressed() )
        { 	      
        	 ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyBarDown.getKeyCode()));  
        }  
        else if(ClientProxy.keyBarUp.isPressed() )
        { 	      
        	 ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyBarUp.getKeyCode()));  
        }   
        else if(ClientProxy.keyBindMacro1.isPressed())
        {
       		ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyBindMacro1.getKeyCode()));
        }
        else if(ClientProxy.keyBindMacro2.isPressed())
        {
       		ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyBindMacro2.getKeyCode()));
        }
        else if(ClientProxy.keyPush.isPressed())
        {
       		ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyPush.getKeyCode()));
        }
        else if(ClientProxy.keyPull.isPressed())
        {
       		ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyPull.getKeyCode()));
        }
        else if(ClientProxy.keyTransform.isPressed())
        {
       		ModKeyMacros.network.sendToServer( new MessageKeyPressed(ClientProxy.keyTransform.getKeyCode()));
        } 
    } 
	public static void playSoundAt(World world,BlockPos pos, String sound)
	{ 
		world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, 1.0F, 1.0F, false);
	}
	public static String getDirectionsString(EntityPlayer player, BlockPos pos  )
	{     
		int xDist,yDist,zDist;
		
		xDist = (int) player.posX - pos.getX();
		yDist = (int) player.posY - pos.getY();
		zDist = (int) player.posZ - pos.getZ();
		
		//in terms of directon copmass:
		//North is -z;  south is +z		
		//east is +x, west is -x
		
		//so for Distances: 
		
		boolean isNorth = (zDist > 0);
		boolean isSouth = (zDist < 0);
		
		boolean isWest = (xDist > 0);
		boolean isEast = (xDist < 0);

		boolean isUp   = (yDist < 0);
		boolean isDown = (zDist > 0);
		
		String xStr = "";
		String yStr = "";
		String zStr = "";

		if(isWest) xStr = Math.abs(xDist) + " west ";
		if(isEast) xStr = Math.abs(xDist) + " east ";
		
		if(isNorth) zStr = Math.abs(zDist) + " north ";
		if(isSouth) zStr = Math.abs(zDist) + " south ";

		if(isUp)   yStr = Math.abs(yDist) + " up ";
		if(isDown) yStr = Math.abs(yDist) + " down ";
		 
		return xStr +  yStr +  zStr ;
	}
	
	public static ArrayList<Item> getItemListFromCSV(String csv)
	{
		ArrayList<Item> items = new ArrayList<Item>();
		String[] ids = csv.split(","); 
		Item isItNull = null;
		Block b;
		
		for(int i = 0; i < ids.length; i++)
		{
			isItNull = Item.getByNameOrId(ids[i]);
			if(isItNull == null)  //try to get block version  
			{ 
				b = Block.getBlockFromName(ids[i]);
				if(b != null)	isItNull = Item.getItemFromBlock(b); 
			} 
			 
			if(isItNull == null)
			{
				ModKeyMacros.logger.log(Level.WARN, "Item not found : "+ ids[i]);
			}
			else
			{
				items.add(isItNull);
			} 
		}  
		
		return items;
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
 
	public static void decrHeldStackSize(EntityPlayer entityPlayer) 
	{
		decrHeldStackSize(entityPlayer,1);
	}
	public static void decrHeldStackSize(EntityPlayer entityPlayer, int by) 
	{
		if (entityPlayer.capabilities.isCreativeMode == false)
        {
			entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, by);
        }
	}
	public static void setMaxHealth(EntityLivingBase living,int max)
	{	
		living.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(max);
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
		if(pos != null)
			spawnParticle(world,type,pos.getX(),pos.getY(),pos.getZ());
    }	
 
	public static String posToString(BlockPos position) 
	{ 
		return "["+ position.getX() + ", "+position.getY()+", "+position.getZ()+"]";
	} 
	public static void teleportWallSafe(EntityLivingBase player, World world, BlockPos coords)
	{
		player.setPositionAndUpdate(coords.getX(), coords.getY(), coords.getZ()); 

		moveEntityWallSafe(player, world);
	}
	public static void moveEntityWallSafe(EntityLivingBase entity, World world) 
	{
		while (!world.getCollidingBoundingBoxes(entity, entity.getEntityBoundingBox()).isEmpty())
		{
			entity.setPositionAndUpdate(entity.posX, entity.posY + 1.0D, entity.posZ);
		}
	}
	
	public static double distanceBetweenHorizontal(BlockPos start, BlockPos end)
	{
		int xDistance =  Math.abs(start.getX() - end.getX() );
		int zDistance =  Math.abs(start.getZ() - end.getZ() );
		//ye olde pythagoras
		return Math.sqrt(xDistance * xDistance + zDistance * zDistance);
	}

	public static String getCoordsOrReduced(EntityPlayer player, BlockPos pos)
	{
		boolean showCoords = !player.worldObj.getGameRules().getGameRuleBooleanValue("reducedDebugInfo");
		
		if(showCoords)
			return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
		else
			return getDirectionsString(player, pos); 
	}
	public static BlockPos getBedLocationSafe(World world, EntityPlayer player) 
	{
		 BlockPos realBedPos = null;
		
		 BlockPos coords = player.getBedLocation(0);
		  
		 if(coords != null)
		 { 
			 Block block = world.getBlockState(coords).getBlock();
			 
			 if (block.equals(Blocks.bed) || block.isBed(world, coords, player))
			 {
				 //then move over according to how/where the bed wants me to spawn
				 realBedPos = block.getBedSpawnPosition(world, coords, player); 
			 }
		 }
		 
		 return realBedPos;
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
