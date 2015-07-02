package com.lothrazar.samskeys.proxy;
    
import com.lothrazar.samskeys.CommandBindMacro;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessageKeyPressed implements IMessage, IMessageHandler<MessageKeyPressed, IMessage>
{
	private int macroNumber;
	  
	public static final int ID = 0;
	public MessageKeyPressed()
	{ 
	}
	
	public MessageKeyPressed(int macroNum)
	{ 
		this.macroNumber = macroNum;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.macroNumber = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(macroNumber);
	}
	
	@Override
	public IMessage onMessage(MessageKeyPressed message, MessageContext ctx)
	{  
		EntityPlayer player = ctx.getServerHandler().playerEntity; 
 
		CommandBindMacro.tryExecuteMacro(player,"key.macro"+message.macroNumber);

		return null;
	}
}
 
