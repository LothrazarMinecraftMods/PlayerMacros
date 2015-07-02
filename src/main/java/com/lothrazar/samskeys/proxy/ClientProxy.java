package com.lothrazar.samskeys.proxy;

import org.lwjgl.input.Keyboard;   

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class ClientProxy extends CommonProxy 
{   
	public static KeyBinding keyBindMacro1;
	public static KeyBinding keyBindMacro2;  
	public static KeyBinding keyBindMacro3;
	public static KeyBinding keyBindMacro4;  
	public static final String keyBind1Name = "key.macro1";
	public static final String keyBind2Name = "key.macro2"; 
	public static final String keyBind3Name = "key.macro3";
	public static final String keyBind4Name = "key.macro4"; 
	public static final String keyCategoryMacro = "key.categories.macro";  
	
    @Override
    public void registerRenderers() 
    {  
    	registerKeyBindings();  
    }
  
	private void registerKeyBindings() 
	{
		 
        keyBindMacro1 = new KeyBinding(keyBind1Name, Keyboard.KEY_N, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro1);

        keyBindMacro2 = new KeyBinding(keyBind2Name, Keyboard.KEY_M, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro2);

        keyBindMacro3 = new KeyBinding(keyBind3Name, Keyboard.KEY_COMMA, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro3);

        keyBindMacro4 = new KeyBinding(keyBind4Name, Keyboard.KEY_PERIOD, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro4);
	} 
}
