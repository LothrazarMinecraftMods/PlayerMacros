package com.lothrazar.samskeys.command;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;     

import com.lothrazar.samskeys.Location;
import com.lothrazar.samskeys.ModMain;
import com.lothrazar.samskeys.PlayerPowerups;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP; 
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.DimensionManager;

public class CommandSimpleWaypoints  implements ICommand
{
	public static boolean REQUIRES_OP; 
		
	//it still functions with flat files if you turn this to false
	//but set to true uses IExtended properties which is recommended
	private static final boolean useProps = true; 
	private ArrayList<String> aliases = new ArrayList<String>();

	public CommandSimpleWaypoints()
	{  
		this.aliases.add("swp"); 
		this.aliases.add("SWP");
		this.aliases.add(getName().toUpperCase());
	}
	
	@Override
	public int compareTo(Object arg0) 
	{ 
		return 0;
	}

	@Override
	public String getName() 
	{ 
		return "simplewp";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) 
	{ 
		//TODO: MODE_TELEPORT - and have it cost xp - same xp drain as spells
		return "/" + getName()+" <"+MODE_LIST + "|" + MODE_SAVE + "|"  +MODE_CLEAR + "|" + MODE_HIDEDISPLAY + "|" + MODE_DISPLAY + "|" + MODE_TP + "> [displayname | showindex]";
	}

	@Override
	public List getAliases() 
	{ 
		return this.aliases;
	}

	private static String MODE_TP = "tp"; 
	private static String MODE_DISPLAY = "show"; 
	private static String MODE_HIDEDISPLAY = "hide";
	private static String MODE_LIST = "list";
	private static String MODE_SAVE = "save";
	private static String MODE_CLEAR = "delete"; 
	private static String KEY_CURRENT = "simplewp_current";
	
	@Override
	public void execute(ICommandSender icommandsender, String[] args) 
	{  
		EntityPlayer p = (EntityPlayer)icommandsender;
		
		if(args == null || args.length == 0 || args[0] == null || args[0].length() == 0)
		{ 
			p.addChatMessage(new ChatComponentTranslation(getCommandUsage(icommandsender))); 
	 
			return;//not enough args
		}
		
		 //so length at args is at least 1, so [0] exists
		//try
		//{
		if(args[0].equals(MODE_LIST))
		{
			executeList(p);
			return;
		} 
		
		if(args[0].equals(MODE_HIDEDISPLAY))
		{
			executeHide(p);
			return;
		}

		if(args[0].equals(MODE_SAVE))
		{
			String n = "";
			if(args.length > 1) n = args[1];
			executeSave(p, n);
			return;
		} 
		//clear current
		if(args[0].equals(MODE_CLEAR))
		{
			executeClear(p);
			return;
		} 
		
		 //so its outside the scope of commands that do not have a number
		int index = -1;
		
		try{
		index = Integer.parseInt(args[1]);//TODO: trycatch on this , it might not be integer
		}
		catch(Exception e)
		{
			p.addChatMessage(new ChatComponentTranslation(getCommandUsage(icommandsender))); 
			return;
		}
		if(index <= 0 ) //invalid number, or int parse failed
		{
			// ZERO NOT ALLOWED
			p.addChatMessage(new ChatComponentTranslation(getCommandUsage(icommandsender))); 
			return;
		}
		
		if(args[0].equals(MODE_DISPLAY))
		{ 
			executeDisplay(p,index);
			return;
		} 

		if(args[0].equals(MODE_TP))
		{
			executeTp(p, index);
			return;
		} 
		//}
		//catch(Exception e) //NumberFormat not anymore, could be IOOB
		//{ 
		//    addChatMessage(p,getCommandUsage(icommandsender));
		//	return;//not enough args
		//} 
		
//if nothing else
		p.addChatMessage(new ChatComponentTranslation(getCommandUsage(icommandsender))); 
	}
	
	private void executeSave(EntityPlayer p, String name) 
	{ 
		ArrayList<String> lines = getForPlayer(p);
		
		
		if(name == null) name = "";
		
		//never putr a loc in index zero
		
		if(lines.size() == 0) lines.add("0");
		
		Location here = new Location(lines.size(), p , name);
		
		lines.add( here.toCSV());
		 
		overwriteForPlayer(p,lines);
	} 

	private void executeHide(EntityPlayer p) 
	{
		ArrayList<String> lines = getForPlayer(p);
		
		if(lines.size() < 1){return;}
		lines.set(0,"0");
		overwriteForPlayer(p,lines); 
	}
	public static int EXP_COST_TP = 100;//TODO: CONFIG
	private void executeTp(EntityPlayer player,int index) 
	{
		/*if(ModMain.getExpTotal(player) < EXP_COST_TP)
		{
			ModMain.addChatMessage(player, ModMain.lang("waypoints.tp.exp")+EXP_COST_TP);
			return;
		}
		*/
		Location loc = getSingleForPlayer(player,index);

		//System.out.println("try and teleport to loc "+index);
		
		
		if(loc == null)
		{
			ModMain.addChatMessage(player, "waypoints.tp.notfound");
		}
		else
		{
			if(player.dimension != loc.dimension)
			{
				ModMain.addChatMessage(player, "waypoints.tp.dimension");
			}
			else
			{
				ModMain.teleportWallSafe(player, player.worldObj, new BlockPos(loc.X,loc.Y,loc.Z));
				//TODO:
				//ModMain.drainExp(player, EXP_COST_TP);
			}
		}
	}
	private void executeClear(EntityPlayer p) 
	{
		ArrayList<String> lines = getForPlayer(p);
		
		if(lines.size() <= 1){return;}
		
		String sindex = lines.get(0);
		
		if(sindex == null || sindex.isEmpty()) {return;}
		
		int index = Integer.parseInt(sindex);
		
		ArrayList<String> newLines = new ArrayList<String>();
		
		int i = 0;
		for(String line : lines)
		{
			if(i == index)
			{
				i++;
				continue;//skip this line
			}
			i++;

			
			newLines.add(line);
		}
	 
		newLines.set(0,"0");
		overwriteForPlayer(p,newLines);
	}
	
	private void executeDisplay(EntityPlayer p, int index) 
	{  
		SetCurrentForPlayer(p,index);
	}
	
	private void executeList(EntityPlayer p) 
	{ 
		boolean showCoords = !p.worldObj.getGameRules().getGameRuleBooleanValue("reducedDebugInfo");
		
		ArrayList<String> lines = getForPlayer(p);
		
		int i = 0;
		String msg = "";
		Location loc;
		
		for(String line : lines)
		{ 
			if(i == 0){i++;continue;}//just a weird bug that happens, since we index by 1
			
			if(line == null || line.isEmpty()) {continue;}
			msg = EnumChatFormatting.WHITE+"";//overworld and all other dimensions
			loc = new Location(line);
			
		 
			if(loc.dimension == 1)//END
				msg = EnumChatFormatting.DARK_PURPLE+"";
			else if(loc.dimension == -1)//NETHER
				msg = EnumChatFormatting.RED+"";
			
			msg += "<" + i + "> ";
			
			if(showCoords)  
				msg += loc.toDisplay();
			else
				msg += loc.name;
				
			p.addChatMessage(new ChatComponentTranslation(msg)); 
			
			i++;
		}
	}
	 
	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) 
	{ 
		return false;
	} 
	 
	private void SetCurrentForPlayer(EntityPlayer player, int current)
	{
		//String playerName = player.getDisplayName().getUnformattedText();
		
		ArrayList<String> lines = getForPlayer(player);
		
		lines.set(0, current+"");//overwrite the current index
 
		overwriteForPlayer(player, lines);
	}
	
	private static String filenameForPlayer(String playerName)
	{
		return "swp_"+playerName +".dat";
	}
	
	private void overwriteForPlayer(EntityPlayer player, ArrayList<String> lines)
	{
		if(useProps)
		{
			PlayerPowerups props = PlayerPowerups.get(player);
			String csv="";
			for(String line : lines) 
			{ 
				csv += line + System.lineSeparator();
			}
			props.setWaypoints(csv);
			
			//System.out.println("overwrite for player:");
			//System.out.println(csv);
			//System.out.println("arraylist.size() = "+lines.size());
			
		}
		else
		{
			String playerName = player.getDisplayName().getUnformattedText();
			
			String fileName = filenameForPlayer(playerName);
			try
			{
				File myFile = new File(DimensionManager.getCurrentSaveRootDirectory(), fileName);
				if(!myFile.exists()) myFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(myFile);
				DataOutputStream stream = new DataOutputStream(fos);
				 
				for(String line : lines) 
				{
					stream.writeBytes(line);
					stream.writeBytes(System.lineSeparator());
				}
				
				stream.close();
				fos.close();
			} catch (FileNotFoundException e) {
	
				e.printStackTrace();
			} //this makes it per-world
			catch (IOException e) {
	
				e.printStackTrace();
			} 
		}
	}
	public static Location getSingleForPlayer(EntityPlayer player, int index)
	{
		if(index <= 0){return null;}
		
		
		ArrayList<String> saved = getForPlayer(player);//.getDisplayName().getUnformattedText()


		if(saved.size() <= index) {return null;}

    	//loc = getSingleForPlayer(p,index);
    	
		String sloc = saved.get(index);
		
		if(sloc == null || sloc.isEmpty()) {return null;}

		Location loc = null;
		
		if( index < saved.size() && saved.get(index) != null) 
		{
			loc = new Location(sloc);//this still might be null..?
		}
		
		
		return loc;
	}
	public static ArrayList<String> getForPlayer(EntityPlayer player)
	{ 
		ArrayList<String> lines = new ArrayList<String>();
		
		if(useProps)
		{
			PlayerPowerups props = PlayerPowerups.get(player);
			 
			String csv = props.getStringWaypoints();
			
			//System.out.println("getforplayer : ");
			//System.out.println(csv);
			//System.out.println("arraylist.size() = "+lines.size());
			 
			lines = new ArrayList<String>(Arrays.asList(csv.split(System.lineSeparator())));
			

		}
		else
		{
			
			String playerName = player.getDisplayName().getUnformattedText();
			String fileName = filenameForPlayer(playerName);
			
		 
			try
			{
				//TODO: is there another way to do this? without files?
				File myFile = new File(DimensionManager.getCurrentSaveRootDirectory(), fileName);
				if(!myFile.exists()) myFile.createNewFile();
				FileInputStream fis = new FileInputStream(myFile);
				DataInputStream instream = new DataInputStream(fis);
				String val;
				
				while((val = instream.readLine()) != null) lines.add(val);
				
				instream.close();
				fis.close();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} //this makes it per-world
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
		}
		return lines;
	} 
	
	public static void AddWaypointInfo(RenderGameOverlayEvent.Text event) 
	{
		EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
	 
    	ArrayList<String> saved = getForPlayer(Minecraft.getMinecraft().thePlayer);//.getDisplayName().getUnformattedText()

    	
    	if(saved.size() > 0 && saved.get(0) != null)
    	{ 
    		//find what index is selected currently
    		int index = 0;
    		try
    		{
	    		index = Integer.parseInt( saved.get(0) );
    		}
    		catch(NumberFormatException e) 
    		{ 
    			return;
    		}// do nothing, its allowed to be a string
    		
    		 
    		Location loc = null;
 
        	loc = getSingleForPlayer(p,index);
        	 
    		if(loc != null)
    		{ 
    			if(p.dimension != loc.dimension)
    			{ 
    				return;//hide it, we are in wrong dimension to display this
    			}
    			
    			double dX = p.posX - loc.X;
    			double dZ = p.posZ - loc.Z;
    			
    			int dist = MathHelper.floor_double(Math.sqrt( dX*dX + dZ*dZ));
    			int dY = MathHelper.floor_double(loc.Y - p.posY);
    			  
    			String height = " [" + dY + "]";
    			
    			String showName = dist + "m to <"+index+"> " + loc.name + height;	
			 
				event.right.add(showName); 
    		} 
    	}
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender ic)
	{
		return (REQUIRES_OP) ? ic.canUseCommand(2, this.getName()) : true; 
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
	{ 
		return null;
	}
}
