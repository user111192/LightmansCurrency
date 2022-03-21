package io.github.lightman314.lightmanscurrency.trader.tradedata.rules;

import java.util.List;

import javax.annotation.Nullable;

import io.github.lightman314.lightmanscurrency.events.TradeEvent.PostTradeEvent;
import io.github.lightman314.lightmanscurrency.events.TradeEvent.PreTradeEvent;
import io.github.lightman314.lightmanscurrency.events.TradeEvent.TradeCostEvent;
import io.github.lightman314.lightmanscurrency.trader.tradedata.TradeRule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public interface ITradeRuleHandler {

	public void beforeTrade(PreTradeEvent event);
	public void tradeCost(TradeCostEvent event);
	public void afterTrade(PostTradeEvent event);
	
	public List<TradeRule> getRules();
	//public void addRule(TradeRule newRule);
	//public void removeRule(TradeRule rule);
	public void clearRules();
	/**
	 * Note: Does not mark anything dirty or send update messages to the client.
	 */
	public default void updateRule(ResourceLocation type, CompoundNBT updateInfo)
	{
		TradeRule rule = TradeRule.getRule(type, this.getRules());
		if(rule == null)
		{
			if(TradeRule.isCreateMessage(updateInfo))
			{
				TradeRule newRule = TradeRule.CreateRule(type);
				if(newRule == null)
					return;
				this.getRules().add(newRule);
			}
		}
		if(rule != null)
		{
			if(TradeRule.isRemoveMessage(updateInfo))
			{
				this.getRules().remove(rule);
			}
			else
			{
				rule.handleUpdateMessage(updateInfo);
			}
		}
	}
	public void markRulesDirty();
	
	public static <T extends ITradeRuleHandler> void savePersistentRuleData(CompoundNBT data, @Nullable ITradeRuleHandler trader, @Nullable List<T> trades) {
		if(trader != null && trader.getRules().size() > 0)
			TradeRule.writePersistentData(data, trader.getRules(), "TraderRuleData");
		if(trades != null)
		{
			ListNBT tradeData = new ListNBT();
			for(int i = 0; i < trades.size(); ++i)
			{
				T trade = trades.get(i);
				if(trade.getRules().size() > 0)
				{
					CompoundNBT ruleData = new CompoundNBT();
					if(TradeRule.writePersistentData(ruleData, trade.getRules(), "Data"))
					{
						ruleData.putInt("index", i);
						tradeData.add(ruleData);
					}
				}
			}
			if(tradeData.size() > 0)
				data.put("TradeRuleData", tradeData);
		}
	}

	public static void readPersistentRuleData(CompoundNBT data, @Nullable ITradeRuleHandler trader, @Nullable List<? extends ITradeRuleHandler> trades) {

		if(trader != null)
			TradeRule.readPersistentData(data, trader.getRules(), "TraderRuleData");
		if(trades != null && data.contains("TradeRuleData", Constants.NBT.TAG_LIST))
		{
			ListNBT tradeData = data.getList("TradeRuleData", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < tradeData.size(); ++i)
			{
				CompoundNBT ruleData = tradeData.getCompound(i);
				int index = ruleData.getInt("index");
				if(index >= 0 && index < trades.size())
					TradeRule.readPersistentData(ruleData, trades.get(index).getRules(), "Data");
			}
		}

	}
	
	public interface ITradeRuleMessageHandler
	{
		public void receiveTradeRuleMessage(PlayerEntity player, int index, ResourceLocation ruleType, CompoundNBT updateInfo);
	}
	
}
