package com.lothrazar.samskeys;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class CommandBindMacro implements ICommand
{
	public static boolean REQUIRES_OP = false;//not in config on purpose
	public static String KEY_MACRO_base = "key.macro";
	private ArrayList<String> aliases = new ArrayList<String>();
	public static final int KMIN=1;
	public static final int KMAX=4;
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
		return "/"+getName() +" list OR /"+getName()+" <number> <command> [args]";
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
			ModKeyMacros.addChatMessage(player, getCommandUsage(sender));
			return;
		}
		
		if(args[0].equalsIgnoreCase("list"))
		{
			String mList;
			for(int k = KMIN; k <= KMAX; k++)
			{
				mList = getPlayerMacro(player,KEY_MACRO_base + k);
				if(mList==null || mList.isEmpty()) {mList = ModKeyMacros.lang("command.bind.empty");}
				// ClientProxy.keyBind1 real name not number
				//String keyname = getKeyDescription(k);
				ModKeyMacros.addChatMessage(player, k+" : "+mList);
			}
			
			return;
		}
		//so it wasnt list, and not empty
		//String inKey = args[0];
		/*
		if(inKey.length() != 1)
		{
			ModKeyMacros.addChatMessage(player, getCommandUsage(sender));
			return;
		}*/
	// System.out.println("searching for "+inKey);
		
		
		int key = 0;
		try
		{			
			key = Integer.parseInt(args[0]);
		}
		catch(Exception e)
		{
			ModKeyMacros.addChatMessage(player, getCommandUsage(sender));
			return;
		}
		
		if(key < KMIN || key > KMAX)
		{
			ModKeyMacros.addChatMessage(player, getCommandUsage(sender));
			return;
		}
		
		String full = "/";
		for(int i = 1; i < args.length; i++)
		{
			full += args[i]+" ";
		}
		 
		full = full.replace("//", "/");//in case it is typed in for us
		/*
		int match = 0;
		//String oldLetter = KEY_MACRO_base + mac;
		String s;
		for(int k = KMIN; k <= KMAX; k++)
		{
			if(inKey.equalsIgnoreCase( getKeyDescription(k)))
			{
				match = k;
				break;//end loop
			}
		}
		 
		if(match == 0)
		{
			//letter not bound
			ModKeyMacros.lang("command.bind.empty");
			return;
		}*/
		
		setPlayerMacro(player,key,full);

		ModKeyMacros.addChatMessage(player, ModKeyMacros.lang("command.bind.done")+" "+key+" "+full);
		
	}
/*
	public static String getKeyDescription(int key)
	{
		//getKeyDescription gets something like 'key.macro1' like lang file data
		
		//thanks http://stackoverflow.com/questions/10893455/getting-keytyped-from-the-keycode
	 //could do getFromName("key.macro"+key)//if that existed
		KeyBinding binding = null;
		switch(key)//TODO:...maybe find better way. switch for now
		{
		case 1:
			binding = ClientProxy.keyBindMacro1;
			break;
		case 2:
			binding = ClientProxy.keyBindMacro2;
			break;
		case 3:
			binding = ClientProxy.keyBindMacro3;
			break;
		case 4:
			binding = ClientProxy.keyBindMacro4;
			break;
		}
		
		 
		if(binding == null)
			return "";
		else
			return GameSettings.getKeyDisplayString(binding.getKeyCode());
			//return I18n.format(binding.getKeyDescription(), new Object[0]);
			//return java.awt.event.KeyEvent.getKeyText(binding.getKeyCode());
	}*/
	public static void setPlayerMacro(EntityPlayer player,int macroNumber, String full)
	{
		player.getEntityData().setString(KEY_MACRO_base + macroNumber, full);
	}
	public static String getPlayerMacro(EntityPlayer player,int macroNumber)
	{
		return player.getEntityData().getString(KEY_MACRO_base + macroNumber);
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
			ModKeyMacros.addChatMessage(player, ModKeyMacros.lang("command.bind.empty"));
			return;
		}
		
	 
		ModKeyMacros.execute(player,cmd);
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
