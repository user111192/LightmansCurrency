package io.github.lightman314.lightmanscurrency;

import java.util.List;

import net.minecraft.world.item.Item;

@Deprecated
public interface IItemSet<T> {

	public Item getItem(T key);
	public List<Item> getAllItems();
	
}
