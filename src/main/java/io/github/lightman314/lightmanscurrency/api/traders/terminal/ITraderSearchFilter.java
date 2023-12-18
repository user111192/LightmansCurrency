package io.github.lightman314.lightmanscurrency.api.traders.terminal;

import io.github.lightman314.lightmanscurrency.api.traders.TraderData;

import javax.annotation.Nonnull;

public interface ITraderSearchFilter {
	
	boolean filter(@Nonnull TraderData data, @Nonnull String searchText);
	
}
