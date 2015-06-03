package com.lothrazar.samskeys.command;

import java.util.ArrayList;
import java.util.List;

import com.lothrazar.samskeys.ModMain;
import com.lothrazar.samskeys.proxy.ClientProxy; 

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class CommandBindMacro implements ICommand
{
	public static boolean REQUIRES_OP = false;//not in config on purpose
	private static String KEY_MACRO_base = "key.macro";
	private ArrayList<String> aliases = new ArrayList<String>();
	private static int KMIN=1;
	private static int KMAX=2;
	public CommandBindMacro()
	{
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
		return "bind";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/"+getName() +" list OR /"+getName()+" <letter> <command> [args]";
	}

	@Override
	public List getAliases()
	{
		return aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)		throws CommandException
	{ 
		EntityPlayer player = (EntityPlayer)sender;
		if(player == null){return;}//not allowed for command blocks

		if(args.length == 0 || args[0] == null)
		{ 
			ModMain.addChatMessage(player, getCommandUsage(sender));
			return;
		}
		
		if(args[0].equalsIgnoreCase("list"))
		{
			String mList;
			for(int k = KMIN; k <= KMAX; k++)
			{
				mList = getPlayerMacro(player,KEY_MACRO_base + k);
				if(mList==null || mList.isEmpty()) {mList = ModMain.lang("command.bind.empty");}
				// ClientProxy.keyBind1 real name not number
				String keyname = ClientProxy.getKeyDescription(k);
				ModMain.addChatMessage(player, keyname+" : "+mList);
			}
			
			return;
		}
		//so it wasnt list, and not empty
		String inKey = args[0];
		
		if(inKey.length() != 1)
		{
			ModMain.addChatMessage(player, getCommandUsage(sender));
			return;
		}
	 
		
		String full = "/";
		for(int i = 1; i < args.length; i++)
		{
			full += args[i]+" ";
		}
		 
		full = full.replace("//", "/");//in case it is typed in for us
		
		int match = 0;
		//String oldLetter = KEY_MACRO_base + mac;
		String s;
		for(int k = KMIN; k <= KMAX; k++)
		{
			if(inKey.equalsIgnoreCase( ClientProxy.getKeyDescription(k)))
			{
				match = k;
				break;//end loop
			}
		}
		 
		if(match == 0)
		{
			//letter not bound
			ModMain.lang("command.bind.empty");
			return;
		}
		
		player.getEntityData().setString(KEY_MACRO_base + match, full);

		ModMain.addChatMessage(player, ModMain.lang("command.bind.done")+" "+inKey+" "+full);
		
	}
	  
	public static String getPlayerMacro(EntityPlayer player,String macro)
	{
		return player.getEntityData().getString(macro);
	}

	public static void tryExecuteMacro(EntityPlayer player,String macro)
	{
		String cmd = CommandBindMacro.getPlayerMacro(player,macro);

		if(cmd==null||cmd.isEmpty())
		{
			ModMain.addChatMessage(player, ModMain.lang("command.bind.empty"));
			return;
		}
		
	 
		ModMain.execute(player,cmd);
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender)
	{ 
		return (REQUIRES_OP) ? sender.canUseCommand(2, this.getName()) : true; 
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,		BlockPos pos)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}
