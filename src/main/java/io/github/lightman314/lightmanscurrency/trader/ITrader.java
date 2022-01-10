package io.github.lightman314.lightmanscurrency.trader;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import io.github.lightman314.lightmanscurrency.blockentity.interfaces.IPermissions;
import io.github.lightman314.lightmanscurrency.trader.settings.CoreTraderSettings;
import io.github.lightman314.lightmanscurrency.trader.settings.Settings;
import io.github.lightman314.lightmanscurrency.util.MoneyUtil.CoinValue;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface ITrader extends IPermissions {

	public Component getName();
	public Component getTitle();
	public CoinValue getStoredMoney();
	public int getTradeCount();
	public int getTradeCountLimit();
	public int getTradeStock(int index);
	//Settings stuff
	public CoreTraderSettings getCoreSettings();
	public void markCoreSettingsDirty();
	public List<Settings> getAdditionalSettings();
	public default Map<String,Integer> getAllyDefaultPermissions() { return Maps.newHashMap(); }
	//Creative stuff
	public void requestAddOrRemoveTrade(boolean isAdd);
	public void addTrade(Player requestor);
	public void removeTrade(Player requestor);
	
}
