package io.github.lightman314.lightmanscurrency.common.menus;

import io.github.lightman314.lightmanscurrency.common.crafting.RecipeTypes;
import io.github.lightman314.lightmanscurrency.common.crafting.TicketStationRecipe;
import io.github.lightman314.lightmanscurrency.common.menus.slots.ticket.*;
import io.github.lightman314.lightmanscurrency.common.menus.validation.types.BlockEntityValidator;
import io.github.lightman314.lightmanscurrency.common.core.ModMenus;
import io.github.lightman314.lightmanscurrency.common.menus.slots.OutputSlot;
import io.github.lightman314.lightmanscurrency.api.network.LazyPacketData;
import io.github.lightman314.lightmanscurrency.util.InventoryUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import io.github.lightman314.lightmanscurrency.LightmansCurrency;
import io.github.lightman314.lightmanscurrency.common.blockentity.TicketStationBlockEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class TicketStationMenu extends LazyMessageMenu{
	
	private final Container output = new SimpleContainer(1);
	
	public final TicketStationBlockEntity blockEntity;

	public static List<TicketStationRecipe> getAllRecipes(@Nonnull Level level) { return level.getRecipeManager().getAllRecipesFor(RecipeTypes.TICKET.get()); }
	public List<TicketStationRecipe> getAllRecipes() { return getAllRecipes(this.blockEntity.getLevel()); }
	public TicketStationRecipe getRecipe(@Nonnull ResourceLocation recipeID)
	{
		for(TicketStationRecipe recipe : this.getAllRecipes())
		{
			if(recipe.getId().equals(recipeID))
				return recipe;
		}
		return null;
	}
	public TicketStationMenu(int windowId, Inventory inventory, TicketStationBlockEntity blockEntity)
	{
		super(ModMenus.TICKET_MACHINE.get(), windowId, inventory);
		this.blockEntity = blockEntity;
		this.addValidator(BlockEntityValidator.of(this.blockEntity));
		
		//Slots
		this.addSlot(new TicketModifierSlot(this, this.blockEntity.getStorage(), 0, 20, 21));
		this.addSlot(new TicketMaterialSlot(this, this.blockEntity.getStorage(), 1, 56, 21));
		
		this.addSlot(new OutputSlot(this.output, 0, 116, 21));
		
		//Player inventory
		for(int y = 0; y < 3; y++)
		{
			for(int x = 0; x < 9; x++)
			{
				this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 56 + y * 18));
			}
		}
		//Player hotbar
		for(int x = 0; x < 9; x++)
		{
			this.addSlot(new Slot(inventory, x, 8 + x * 18, 114));
		}
	}
	
	@Override
	public void removed(@NotNull Player playerIn)
	{
		super.removed(playerIn);
		this.clearContainer(playerIn,  this.output);
	}
	
	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index)
	{
		
		ItemStack clickedStack = ItemStack.EMPTY;
		
		Slot slot = this.slots.get(index);
		
		if(slot != null && slot.hasItem())
		{
			ItemStack slotStack = slot.getItem();
			clickedStack = slotStack.copy();
			int totalSize = this.blockEntity.getStorage().getContainerSize() + this.output.getContainerSize();
			if(index < totalSize)
			{
				if(!this.moveItemStackTo(slotStack, totalSize, this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if(!this.moveItemStackTo(slotStack, 0, this.blockEntity.getStorage().getContainerSize(), false))
			{
				return ItemStack.EMPTY;
			}
			
			if(slotStack.isEmpty())
			{
				slot.set(ItemStack.EMPTY);
			}
			else
			{
				slot.setChanged();
			}
		}
		
		return clickedStack;
		
	}

	public boolean validInputs() { return this.getAllRecipes().stream().anyMatch(r -> r.matches(this.blockEntity.getStorage(), this.blockEntity.getLevel())); }
	
	public boolean roomForOutput(TicketStationRecipe recipe)
	{
		if(recipe == null)
			return false;
		ItemStack outputStack = this.output.getItem(0);
		if(outputStack.isEmpty())
			return true;
		return InventoryUtil.ItemMatches(recipe.peekAtResult(this.blockEntity.getStorage()), outputStack) && outputStack.getMaxStackSize() > outputStack.getCount();
	}
	
	public void craftTickets(boolean fullStack, @Nonnull ResourceLocation recipeID)
	{
		TicketStationRecipe recipe = this.getRecipe(recipeID);
		if(recipe == null)
			return;

		if(!recipe.matches(this.blockEntity.getStorage(), this.blockEntity.getLevel()))
			return;

		if(!roomForOutput(recipe))
		{
			LightmansCurrency.LogDebug("No room for Ticket Machine outputs. Cannot craft tickets.");
			return;
		}

		int amountToCraft = 1;
		if(fullStack)
			amountToCraft = output.getMaxStackSize();

		for(int i = 0; i < amountToCraft; ++i)
		{
			if(this.assemble(recipe))
				return;
		}
		
	}

	private boolean assemble(@Nonnull TicketStationRecipe recipe)
	{
		if(this.roomForOutput(recipe) && recipe.matches(this.blockEntity.getStorage(), this.blockEntity.getLevel()))
		{
			ItemStack result = recipe.assemble(this.blockEntity.getStorage());
			if(!result.isEmpty() && InventoryUtil.PutItemStack(this.output, result))
			{
				//Remove the consumed items from the input
				if(recipe.consumeModifier())
					this.blockEntity.getStorage().removeItem(0,1);
				this.blockEntity.getStorage().removeItem(1,1);
				return false;
			}
		}
		return true;
	}

	public void SendCraftTicketsMessage(boolean fullStack, @Nonnull ResourceLocation recipe)
	{
		this.SendMessageToServer(LazyPacketData.builder().setBoolean("CraftTickets", fullStack).setResourceLocation("Recipe", recipe));
	}

	@Override
	public void HandleMessage(@Nonnull LazyPacketData message) {
		if(message.contains("CraftTickets"))
			this.craftTickets(message.getBoolean("CraftTickets"), message.getResourceLocation("Recipe"));
	}

}
