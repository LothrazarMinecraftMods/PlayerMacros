package com.lothrazar.samskeys.proxy;

import org.lwjgl.input.Keyboard;   

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.GameSettings;
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
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro1);

        keyBindMacro4 = new KeyBinding(keyBind4Name, Keyboard.KEY_PERIOD, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro2);
        
         
	} 

	public static String getKeyDescription(int key)
	{
		//getKeyDescription gets something like 'key.macro1' like lang file data
		
		//thanks http://stackoverflow.com/questions/10893455/getting-keytyped-from-the-keycode
	 //could do getFromName("key.macro"+key)//if that existed
		KeyBinding binding = null;
		switch(key)//TODO:...maybe find better way. switch for now
		{
		case 1:
			binding = keyBindMacro1;
			break;
		case 2:
			binding = keyBindMacro2;
			break;
		case 3:
			binding = keyBindMacro3;
			break;
		case 4:
			binding = keyBindMacro4;
			break;
		}
		
		 
		if(binding == null)
			return "";
		else
			return GameSettings.getKeyDisplayString(binding.getKeyCode());
			//return I18n.format(binding.getKeyDescription(), new Object[0]);
			//return java.awt.event.KeyEvent.getKeyText(binding.getKeyCode());
	}
}
