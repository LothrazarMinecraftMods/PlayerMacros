package com.lothrazar.samskeys;

 
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class PlayerPowerups implements IExtendedEntityProperties
{
	private final static String EXT_PROP_NAME = "PlayerPowerups";
	private final EntityPlayer player;//we get one of these powerup classes for each player

	private static final int WAYPOINT_WATCHER = 20;
	private static final String NBT_WAYPOINT = "samWaypoints"; 

	private static final int TODO_WATCHER = 21;
	private static final String NBT_TODO = "samTodo";  
 
	public PlayerPowerups(EntityPlayer player)
	{
		this.player = player;  
		this.player.getDataWatcher().addObject(WAYPOINT_WATCHER, 0);
		this.player.getDataWatcher().addObject(TODO_WATCHER, 0);  
	}
	
	public static final void register(EntityPlayer player)
	{
		player.registerExtendedProperties(PlayerPowerups.EXT_PROP_NAME, new PlayerPowerups(player));
	}

	public static final PlayerPowerups get(EntityPlayer player)
	{
		return (PlayerPowerups) player.getExtendedProperties(EXT_PROP_NAME);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) 
	{
		// We need to create a new tag compound that will save everything for our Extended Properties
		NBTTagCompound properties = new NBTTagCompound(); 
	
		properties.setString(NBT_WAYPOINT,   this.getStringSafe(WAYPOINT_WATCHER)); 
		properties.setString(NBT_TODO,       this.getStringSafe(TODO_WATCHER)); 

		compound.setTag(EXT_PROP_NAME, properties); 
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{ 
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		if(properties == null){ properties = new NBTTagCompound(); }
		
		this.player.getDataWatcher().updateObject(WAYPOINT_WATCHER, properties.getString(NBT_WAYPOINT)); 
		this.player.getDataWatcher().updateObject(TODO_WATCHER,      properties.getString(NBT_TODO));  
 	}

	public final String getStringTodo() 
	{
		return this.getStringSafe(TODO_WATCHER);
	}
	public final void setStringTodo(String todo) 
	{
		this.player.getDataWatcher().updateObject(TODO_WATCHER, todo);
	}
	public final String getStringWaypoints() 
	{
		return this.getStringSafe(WAYPOINT_WATCHER);
	}
	public final void setWaypoints(String waypointsCSV) 
	{
		this.player.getDataWatcher().updateObject(WAYPOINT_WATCHER, waypointsCSV);
	}
	
	//http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/1571567-forge-1-6-4-1-8-eventhandler-and

	public void copy(PlayerPowerups props) 
	{
		//thanks for the help https://github.com/coolAlias/Tutorial-Demo/blob/master/src/main/java/tutorial/entity/ExtendedPlayer.java

		//set in the player
		player.getDataWatcher().updateObject(WAYPOINT_WATCHER, props.getStringWaypoints());
		player.getDataWatcher().updateObject(TODO_WATCHER, props.getStringTodo());
		//set here
		this.setWaypoints(props.getStringWaypoints());
		this.setStringTodo(props.getStringTodo());  
	}
	@Override
	public void init(Entity entity, World world) 
	{ 
	}
	public String getStringSafe(int WATCHER)
	{
		//we used to get these exceptions when our "copy" function wanst in here saving us to persist data on player death.
		//doesnt seem to happen anymore, but keeing the try catch because it couldn't hurt.
		try
		{
			//why is this giving  java.lang.Integer cannot be cast to java.lang.String
			return this.player.getDataWatcher().getWatchableObjectString(WATCHER); 
		}
		catch(ClassCastException e)
		{
			return "";
		}
	}
}