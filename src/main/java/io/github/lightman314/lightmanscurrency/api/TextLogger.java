package io.github.lightman314.lightmanscurrency.api;

import java.util.ArrayList;
import java.util.List;

import io.github.lightman314.lightmanscurrency.Config;
import io.github.lightman314.lightmanscurrency.money.CoinValue;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public abstract class TextLogger {
	
	public final List<MutableComponent> logText = new ArrayList<>();
	protected final String tag;
	
	protected TextLogger(String tagName)
	{
		this.tag = tagName;
	}
	
	public final void clear()
	{
		this.logText.clear();
	}
	
	protected final void AddLog(MutableComponent text)
	{
		if(text != null)
		{
			this.logText.add(text);
			this.fitToSizeLimit();
		}
	}
	
	public void write(CompoundTag compound)
	{
		ListTag list = new ListTag();
		for(int i = 0; i < logText.size(); i++)
		{
			Component text = logText.get(i);
			CompoundTag thisCompound = new CompoundTag();
			thisCompound.putString("value", Component.Serializer.toJson(text));
			list.add(thisCompound);
		}
		compound.put(this.tag, list);
	}
	
	public void read(CompoundTag compound)
	{
		if(compound.contains(this.tag, Tag.TAG_LIST))
		{
			ListTag list = compound.getList(this.tag, Tag.TAG_COMPOUND);
			this.logText.clear();
			for(int i = 0; i < list.size(); i++)
			{
				String jsonText = list.getCompound(i).getString("value");
				MutableComponent text = Component.Serializer.fromJson(jsonText);
				if(text != null)
					this.logText.add(text);
			}
			//Fit to size limit on load to ensure that changing the config option will correct any log size issues.
			this.fitToSizeLimit();
		}
	}
	
	public final void fitToSizeLimit() {
		while(this.logText.size() > Config.SERVER.logLimit.get() && this.logText.size() > 0)
			this.logText.remove(0);
	}
	
	/** @deprecated Use getCostText(CoinValue cost) */
	@Deprecated 
	public static Component getCostText(boolean isFree, CoinValue cost)  { return getCostText(cost); }
	
	public static Component getCostText(CoinValue cost)
	{
		return new TextComponent(cost.getString()).withStyle(ChatFormatting.YELLOW);
	}
	
}
