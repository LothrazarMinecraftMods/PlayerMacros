package com.lothrazar.samskeys.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.lothrazar.samskeys.PlayerPowerups;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.DimensionManager;

public class CommandTodoList implements ICommand
{   
	public static boolean REQUIRES_OP = false; 

	private ArrayList<String> aliases = new ArrayList<String>(); 

	//it still functions with flat files if you turn this to false
	//but set to true uses IExtended properties which is recommended
	private static final boolean useProps = true;

	public CommandTodoList()
	{  
	    this.aliases.add("TODO");   
	}

	private static String filenameForPlayer(String playerName)
	{
		return "todo_"+playerName +".dat";
	}
	@Override
	public String getCommandUsage(ICommandSender s) 
	{ 
		return "/" + getName()+" <+|-> <text>";
	}
   
	public static String GetTodoForPlayer(EntityPlayer player)
	{
		String todoCurrent = "" ; 
		if(useProps)
		{
			PlayerPowerups props = PlayerPowerups.get(player);
			
			todoCurrent = props.getStringTodo();
		}
		else
		{  
			String fileName = filenameForPlayer(player.getDisplayName().getUnformattedText());
	   
			try 
		 	{
				File myFile = new File(DimensionManager.getCurrentSaveRootDirectory(), fileName); 
				if(!myFile.exists()) myFile.createNewFile(); 
				FileInputStream  fis = new FileInputStream(myFile);
				DataInputStream instream = new DataInputStream(fis);
				
				String val;
				while((val = instream.readLine()) != null)todoCurrent += val; 
				instream.close(); 
				fis.close();
		 	} catch (FileNotFoundException e) { 
			//	Relay.addChatMessage(p, "Error with "+fileName);
				e.printStackTrace();
			} //this makes it per-world
			catch (IOException e) {
			//	Relay.addChatMessage(p, "Error with "+fileName);
				e.printStackTrace();
			}

		}
		return todoCurrent;
	}
	
	public static void SetTodoForPlayer(EntityPlayer player, String todoCurrent)
	{
		if(useProps)
		{
			PlayerPowerups props = PlayerPowerups.get(player);
			
			props.setStringTodo(todoCurrent);
		}
		else
		{ 
			String fileName = filenameForPlayer(player.getDisplayName().getUnformattedText());
			
			try{
				 
				File myFile = new File(DimensionManager.getCurrentSaveRootDirectory(), fileName); 
				if(!myFile.exists()) myFile.createNewFile(); 
				FileOutputStream fos = new FileOutputStream(myFile);
				
				DataOutputStream stream = new DataOutputStream(fos);
				stream.writeBytes(todoCurrent);
				stream.close();
				fos.close();
				  
			} catch (FileNotFoundException e) { 
			//	Relay.addChatMessage(p, "Error with "+fileName);
				e.printStackTrace();
			} //this makes it per-world
			catch (IOException e) {
			//	Relay.addChatMessage(p, "Error with "+fileName);
				e.printStackTrace();
			} 
		}
	}
	
	@Override
	public void execute(ICommandSender icommandsender, String[] args)
	{ 
		EntityPlayer player = (EntityPlayer)icommandsender; 
  
		String todoCurrent = GetTodoForPlayer(player );
 
		 //is the first argument empty
		 if(args == null || args.length == 0 || args[0] == null || args[0].isEmpty())
		 {
			 player.addChatMessage(new ChatComponentTranslation(getCommandUsage(icommandsender))); 
			
			 return; 
		 }
		 
		 String message = "";
	   
		 if(args[0].equals("-"))
		 { 
			 todoCurrent = "";
			 args[0] = "";//remove the plus sign 
		 } 
		 else if(args[0].equals("+"))
		 {
			 for(int i = 1; i < args.length; i++)
			 {
				 message += " " + args[i];
			 } 
			 
			 todoCurrent += " " + message;//so append
		 }
		 else 
		 {
			 //they just did /todo blah blah
			 for(int i = 0; i < args.length; i++)
			 {
				 message += " " + args[i];
			 } 
			 
			 todoCurrent = message;//so replace
		 }
 
		 SetTodoForPlayer(player, todoCurrent); 
	}
	 
 
	@Override
	public List getAliases() 
	{ 
		return aliases;
	}
	 

	@Override
	public String getName() 
	{ 
		return "todo";
	}

	@Override
	public int compareTo(Object arg0)
	{ 
		return 0;
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

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{ 
		return false;
	}
}



