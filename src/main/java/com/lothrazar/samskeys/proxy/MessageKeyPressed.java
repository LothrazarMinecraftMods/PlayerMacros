package com.lothrazar.samskeys.proxy;
    
import com.lothrazar.samskeys.CommandBindMacro;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MessageKeyPressed implements IMessage, IMessageHandler<MessageKeyPressed, IMessage>
{
	private byte keyPressed;
	  
	public static final int ID = 0;
	public MessageKeyPressed()
	{ 
	}
	
	public MessageKeyPressed(int keyCode)
	{ 
		this.keyPressed = (byte)keyCode;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.keyPressed = buf.readByte();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(keyPressed);
	}
	
	@Override
	public IMessage onMessage(MessageKeyPressed message, MessageContext ctx)
	{  
		EntityPlayer player = ctx.getServerHandler().playerEntity; 
 
		if( message.keyPressed == ClientProxy.keyBindMacro1.getKeyCode())//TODO: better code structure here?
	 	{
			CommandBindMacro.tryExecuteMacro(player,ClientProxy.keyBind1Name);
	 	}
		else if( message.keyPressed == ClientProxy.keyBindMacro2.getKeyCode())
	 	{
			CommandBindMacro.tryExecuteMacro(player, ClientProxy.keyBind2Name);
	 	}
		else if( message.keyPressed == ClientProxy.keyBindMacro3.getKeyCode())
	 	{
			CommandBindMacro.tryExecuteMacro(player, ClientProxy.keyBind3Name);
	 	}
		else if( message.keyPressed == ClientProxy.keyBindMacro4.getKeyCode())
	 	{
			CommandBindMacro.tryExecuteMacro(player, ClientProxy.keyBind4Name);
	 	}
		
		return null;
	}
}
 
