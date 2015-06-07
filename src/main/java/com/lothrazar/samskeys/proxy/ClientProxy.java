package com.lothrazar.samskeys.proxy;

import org.lwjgl.input.Keyboard;   

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class ClientProxy extends CommonProxy 
{  
	public static KeyBinding keyShiftUp;
	public static KeyBinding keyShiftDown; 
	public static KeyBinding keyBarUp;
	public static KeyBinding keyBarDown; 
	public static KeyBinding keyBindMacro1;
	public static KeyBinding keyBindMacro2;
	public static KeyBinding keyPush;
	public static KeyBinding keyPull; 
	public static KeyBinding keyTransform; 
	public static final String keyUpName = "key.columnshiftup";
	public static final String keyDownName = "key.columnshiftdown"; 
	public static final String keyBarUpName = "key.columnbarup";
	public static final String keyBarDownName = "key.columnbardown"; 
	public static final String keyPlayerFlipName = "key.playerflip";
	public static final String keyBind1Name = "key.macro1";
	public static final String keyBind2Name = "key.macro2";
	public static final String keyPushName = "key.push";
	public static final String keyPullName = "key.pull";
	public static final String keyTransformName = "key.transform";
 
	public static final String keyCategoryInventory = "key.categories.inventorycontrol";
	public static final String keyCategoryMacro = "key.categories.macro"; 
	public static final String keyCategoryBlocks = "key.categories.blocks";
	
    @Override
    public void registerRenderers() 
    {  
    	registerKeyBindings();  
    }
  
	private void registerKeyBindings() 
	{
		keyShiftUp = new KeyBinding(keyUpName, Keyboard.KEY_Y, keyCategoryInventory);
        ClientRegistry.registerKeyBinding(ClientProxy.keyShiftUp);
    
		keyShiftDown = new KeyBinding(keyDownName, Keyboard.KEY_H, keyCategoryInventory); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyShiftDown); 

        keyBarUp = new KeyBinding(keyBarUpName, Keyboard.KEY_U, keyCategoryInventory);
        ClientRegistry.registerKeyBinding(ClientProxy.keyBarUp);
         
        keyBarDown = new KeyBinding(keyBarDownName, Keyboard.KEY_J, keyCategoryInventory); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBarDown);

        keyBindMacro1 = new KeyBinding(keyBind1Name, Keyboard.KEY_N, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro1);

        keyBindMacro2 = new KeyBinding(keyBind2Name, Keyboard.KEY_M, keyCategoryMacro); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyBindMacro2);

        keyTransform = new KeyBinding(keyTransformName, Keyboard.KEY_V, keyCategoryBlocks); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyTransform);
 
        keyPush = new KeyBinding(keyPushName, Keyboard.KEY_G, keyCategoryBlocks); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyPush);
        keyPull = new KeyBinding(keyPullName, Keyboard.KEY_B,  keyCategoryBlocks); 
        ClientRegistry.registerKeyBinding(ClientProxy.keyPull);
         
	} 

	public static String getKeyDescription(int key)
	{
		//getKeyDescription gets something like 'key.macro1' like lang file data
		
		//thanks http://stackoverflow.com/questions/10893455/getting-keytyped-from-the-keycode
	 
		KeyBinding binding = null;
		switch(key)//TODO:...maybe find better way. switch for now
		{
		case 1:
			binding = keyBindMacro1;
			break;
		case 2:
			binding = keyBindMacro2;
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
