package io.github.lightman314.lightmanscurrency.common.taxes;

import io.github.lightman314.lightmanscurrency.api.traders.TraderData;

import javax.annotation.Nonnull;
import java.util.List;

public class TaxManager {

    public static List<TaxEntry> GetTaxesForTrader(@Nonnull TraderData trader) { return GetTaxes(trader.isClient(), trader); }
    public static List<TaxEntry> GetPossibleTaxesForTrader(@Nonnull TraderData trader) { return GetPossibleTaxes(trader.isClient(), trader); }

    public static List<TaxEntry> GetTaxes(boolean isClient, @Nonnull ITaxable taxable) { return TaxSaveData.GetAllTaxEntries(isClient).stream().filter(e -> e.ShouldTax(taxable)).toList(); }

    public static List<TaxEntry> GetPossibleTaxes(boolean isClient, @Nonnull ITaxable taxable) { return TaxSaveData.GetAllTaxEntries(isClient).stream().filter(e -> e.IsInArea(taxable)).toList(); }

}
