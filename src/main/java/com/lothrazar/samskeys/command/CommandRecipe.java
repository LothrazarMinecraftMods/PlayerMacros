package com.lothrazar.samskeys.command;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import com.lothrazar.samskeys.ModKeyMacros; 

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CommandRecipe  implements ICommand
{
	public static boolean REQUIRES_OP;  
	private ArrayList<String> aliases = new ArrayList<String>();
	
	public CommandRecipe()
	{
		this.aliases.add(getName().toUpperCase());
	}
	@Override
	public int compareTo(Object o)
	{
		return 0;
	}

	@Override
	public String getName()
	{
		return "recipe";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/"+getName();
	}

	@Override
	public List getAliases()
	{
		return aliases;
	}

	@Override
	public void execute(ICommandSender sender, String[] args)		throws CommandException
	{ 
		World world = sender.getEntityWorld();
		if(! (sender instanceof EntityPlayer)){return;}//does not work from command blocks and such
		
		EntityPlayer player = (EntityPlayer)sender;
		ItemStack held = player.inventory.getCurrentItem();
		
		if(held == null && world.isRemote)
		{
			ModKeyMacros.addChatMessage(player, "command.recipes.empty");
			return;
		}

		List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

		ItemStack recipeResult;

		boolean foundSomething = false;

		for (IRecipe recipe : recipes)
		{
		    recipeResult = recipe.getRecipeOutput();
		   
		    //compare ignoring stack size. not null, and the same item
			if( recipeResult == null || recipeResult.getItem() == null){continue;} 
		    if(held.getItem() != recipeResult.getItem()){continue;}
		    if(held.getMetadata() != recipeResult.getMetadata()){continue;}

			//TODO  seperator btw recipes: one item can have multiple.
		    
		    //for each recipe, we need an array/list of the input, then we pass it off to get printed
		    //recipe is either shaped or shapeless
		    //on top of that, some use Forge ore dictionary, and some dont
		    //so 4 cases total
		    
		    //TODO: refactor and use  ItemStack[] ingred = Util.getRecipeInput(recipe);
		    //to save reuse

		    if(recipe instanceof ShapedRecipes)
		    { 
		    	ShapedRecipes r = ((ShapedRecipes)recipe);
		    	boolean isInventory = (r.recipeHeight < 3 && r.recipeWidth < 3);
		    	
		    	//System.out.println("isInventory is from : "+r.recipeHeight+" "+r.recipeWidth);

		    	ModKeyMacros.addChatMessage(player, "command.recipes.found");
		    	addChatShapedRecipe(player, getRecipeInput(recipe), isInventory);
		    	foundSomething = true;
		    }
		    else if(recipe instanceof ShapedOreRecipe) //it uses forge ore dictionary
		    {
		    	ShapedOreRecipe r = (ShapedOreRecipe)recipe;
			
			    ItemStack[] recipeItems = getRecipeInput(recipe);
	
		    	//only because r.width//r.height is private
		    	boolean isInventory = false;
		    	for(Field f : ShapedOreRecipe.class.getDeclaredFields())
		    	{
			        f.setAccessible(true);
			        //works since we know that the only integers in the class are the width/height
			        if(f.getType() == int.class)
			        {
			        	try
						{
							isInventory = isInventory || f.getInt(r) < 3;
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						}
						catch (IllegalAccessException e)
						{
							e.printStackTrace();
						}
			        }
		    	}

		    	ModKeyMacros.addChatMessage(player, "command.recipes.found");
		    	addChatShapedRecipe(player,recipeItems, isInventory);
		    	foundSomething = true;
		    } 
		    else if(recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe)
			{
		    	ModKeyMacros.addChatMessage(player, "command.recipes.found");
				addChatShapelessRecipe(player,getRecipeInput(recipe));
		    	foundSomething = true;
		    }
		    else 
		    {
		    	//TODO: furnace?
		    	//TODO: brewing stand?
		    	
		    	//for example, if its from some special crafting block/furnace from another mod
		    	//Util.addChatMessage(player, "Recipe type not supported, class = " + recipe.getClass().getName());
		    	
		    }
	    }//end main recipe loop
		
		if(foundSomething == false)
		{
			ModKeyMacros.addChatMessage(player, "command.recipes.notfound");
		}
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender)
	{ 
		return (REQUIRES_OP) ? sender.canUseCommand(2, this.getName()) : true; 
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args,	BlockPos pos)
	{
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
	public static ItemStack[] getRecipeInput(IRecipe recipe)
	{
		ItemStack[] recipeItems = null;
	    if(recipe instanceof ShapedRecipes)
	    { 
	    	ShapedRecipes r = ((ShapedRecipes)recipe);
	    	recipeItems = r.recipeItems;
	    }
	    else if(recipe instanceof ShapedOreRecipe)
	    {
	    	ShapedOreRecipe r = (ShapedOreRecipe)recipe;
		
		    recipeItems = new ItemStack[r.getInput().length];
	    	
	    	for(int i = 0; i < r.getInput().length; i++) 
	    	{
	    		Object o = r.getInput()[i];
	    		if(o == null){continue;}
	    		if(o instanceof ItemStack)
	    		{
	    			recipeItems[i] = (ItemStack)o;
	    		}
	    		else
	    		{
				    List<ItemStack> c = (List<ItemStack>)o;
				    	
				    if(c != null && c.size() > 0)
				    {
				    	recipeItems[i] = c.get(0);
				    }
	    		}
	    	} 
	    } 
	    else if(recipe instanceof ShapelessRecipes)
		{
	    	ShapelessRecipes r = (ShapelessRecipes)recipe;
	    	
	    	recipeItems = new ItemStack[r.recipeItems.size()];
    		
	    	for(int i = 0; i < r.recipeItems.size(); i++) 
	    	{
	    		Object o = r.recipeItems.get(i);
	    		if(o != null && o instanceof ItemStack)
	    		{
	    			recipeItems[i] = (ItemStack)o;
	    		}
	    	} 
		}
	    else if(recipe instanceof ShapelessOreRecipe)
	    {
	    	ShapelessOreRecipe r = (ShapelessOreRecipe)recipe;

	    	recipeItems = new ItemStack[r.getInput().size()];
    		
	    	for(int i = 0; i < r.getInput().size(); i++) 
	    	{
	    		Object o = r.getInput().get(i);
	    		if(o == null){continue;}
	    		if(o instanceof ItemStack)
	    		{
	    			recipeItems[i] = (ItemStack)o;
	    		}
	    		else
	    		{
				    List<ItemStack> c = (List<ItemStack>)o;
				    	
				    if(c != null && c.size() > 0)
				    {
				    	recipeItems[i] = c.get(0);
				    }
	    		}
	    	} 
	    }

		return recipeItems;
	}
	public static void addChatShapelessRecipe(EntityPlayer player,	ItemStack[]  recipeItems)
	{
    	for(int i = 0; i < recipeItems.length; i++) 
    	{
    		ItemStack is = recipeItems[i];
    		
    		//list.add(is.getDisplayName());
        	ModKeyMacros.addChatMessage(player, " - "+is.getDisplayName());
    		
    	}
    	//TODO: cleanup/make ncer,etc
    	//Util.addChatMessage(player, "SHAPELESS " +String.join(" + ", list));
	}
	public static void addChatShapedRecipe(EntityPlayer player,	ItemStack[] recipeItems, boolean isInventory )
	{ 
		int size;
		
		//needed only becuase MC forge stores as a flat array not a 2D
		if(isInventory) size = 4;
		else size = 9;
		String[] grid = new String[size];
		for(int i = 0; i < grid.length; i++)grid[i] = "- ";
 
		
		for(int i = 0; i < recipeItems.length; i++)
    	{
    		if(recipeItems[i] != null)
    		{
    			ModKeyMacros.addChatMessage(player, i+" : "+recipeItems[i].getDisplayName());
    			grid[i] = i+" ";
    		}
    	}
		
		if(isInventory)
		{
			ModKeyMacros.addChatMessage(player, grid[0]+grid[1]);
			ModKeyMacros.addChatMessage(player, grid[2]+grid[3]);
		}
		else
		{
			ModKeyMacros.addChatMessage(player, grid[0]+grid[1]+grid[2]);
			ModKeyMacros.addChatMessage(player, grid[3]+grid[4]+grid[5]);
			ModKeyMacros.addChatMessage(player, grid[6]+grid[7]+grid[8]);
		}
	}
}
