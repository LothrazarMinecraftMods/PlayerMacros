package com.lothrazar.samskeys;
 
import net.minecraftforge.common.config.Configuration;  
import com.lothrazar.samskeys.command.*;   

public class ConfigRegistry
{ 
	private Configuration instance;
	private String category = "";
	
	public Configuration instance()
	{
		return instance;
	}
	
	public ConfigRegistry(Configuration c)
	{
		instance = c; 
		instance.load();
 
		category = "commands";
 
		cmd_ping = instance.getBoolean("ping",category, true,
    			"A command that simply displays your coordinates in chat.  Also use '/ping nether' to show the nether version of your coords.  Useful only if your F3 coordinates are hidden, for example with reducedDebugInfo = true gamerule.");
		
		PlaceLib.allowedFromConfig = instance.getString("place.filter",category, "minecraft:dirt,samscontent:block_fragile",
    			"Filter which blocks can be placed with ALL place commands.  Empty string in this filter means everything is allowed.");
		//PlaceLib.XP_COST_PER_PLACE = instance.getInt("place.xp_cost", category, 0, 0, 99, 
		//		"Experience drained each time a block is placed with one of these commands.");
 
		cmd_place_blocks = instance.getBoolean("place",category, true,
    			"Use /place to put blocks in the world from your survival inventory.  It will only replace air blocks, and can skip blocks with its arguments.");

		CommandPlaceBlocks.REQUIRES_OP = instance.getBoolean("place.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
 
		cmd_kit = instance.getBoolean("kit",category, true,
    			"Use /kit to give yourself kit items.  Can only be done once each time you die.");

		String csv = instance.getString("kit.items",category, "minecraft:wooden_pickaxe,minecraft:wooden_axe,minecraft:crafting_table",
    			"Using /kit gives the following item.  Each must have minecraft:item or modname:item, no spaces and split by commas.");
		CommandKit.setItemsFromString(csv); 
		
		CommandHome.REQUIRES_OP = instance.getBoolean("home.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");

		cmd_home = instance.getBoolean("home",category, true,
    			"Use /home to go to the players spawn point, as defined by a bed."); 
		
		CommandHome.REQUIRES_OP = instance.getBoolean("home.needs_op",category, true,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");

		worldhome = instance.getBoolean("worldhome",category, true,
    			"Use /worldhome to go to the worlds global spawn point.");  
		
		CommandWorldHome.REQUIRES_OP = instance.getBoolean("worldhomehome.needs_op",category, true,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
		
		cmd_searchspawner = instance.getBoolean("searchspawner",category, true,
    			"Players can search for spawners placed in the world.  Result is only chat output.");
 
		CommandSearchSpawner.REQUIRES_OP = instance.getBoolean("searchspawner.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
		
		cmd_searchtrade = instance.getBoolean("searchtrade",category, true,
    			"Players can search the trades of nearby villagers.  Result is only chat output.");
		
		CommandSearchTrades.REQUIRES_OP = instance.getBoolean("searchtrade.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
 
		cmd_searchitem = instance.getBoolean("searchitem",category, true,
    			"Players can search nearby chests for items.   Result is only chat output."    		); 
		
		CommandSearchItem.REQUIRES_OP = instance.getBoolean("searchitem.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
 
		cmd_enderchest = instance.getBoolean("enderchest",category, true,
    			"Players can open their enderchest with a command, no item needed."    		); 
		
		CommandEnderChest.REQUIRES_OP = instance.getBoolean("enderchest.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
		 
		cmd_simplewaypoint = instance.getBoolean("simplewaypoint",category, true,
    			"Command that lets players save waypoints that then show up in the F3 debug screen, so we can navigate back to it (no tp)."    		); 
		
		CommandSimpleWaypoints.REQUIRES_OP = instance.getBoolean("simplewaypoint.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
 
		cmd_todo = instance.getBoolean("todo",category, true,
    			"Command that lets players use /todo myreminder text, which will then show whatever text they put on the F3 debug screen."); 
		
		CommandTodoList.REQUIRES_OP = instance.getBoolean("todo.needs_op",category, false,
    			"Command is restricted to players with OP (or single player worlds with cheats enabled).");
	
		cmd_recipe = instance.getBoolean("recipe",category, true,
				"Command to display recipe of the players held item in chat."); 
		
		cmd_uses = instance.getBoolean("uses",category, true,
				"Command to display uses of the players held item in chat.");

		if(instance.hasChanged()){ instance.save(); }
	
	}
	 
	public boolean cmd_searchtrade;
	public boolean cmd_searchitem;
	public boolean cmd_searchspawner;
	public boolean cmd_enderchest;
	public boolean cmd_simplewaypoint;
	public boolean cmd_todo;
	public boolean cmd_kit; 
	public boolean cmd_home;
    
	public boolean cmd_place_blocks;  
	public boolean worldhome;  
	public boolean cmd_recipe;
	public boolean cmd_uses; 
	//public boolean cmd_effectpay;
	public boolean cmd_ping;  
}
