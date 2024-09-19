package io.github.lightman314.lightmanscurrency.api.money.types;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.github.lightman314.lightmanscurrency.api.capability.money.IMoneyHandler;
import io.github.lightman314.lightmanscurrency.api.money.input.MoneyInputHandler;
import io.github.lightman314.lightmanscurrency.api.money.value.MoneyValueParser;
import io.github.lightman314.lightmanscurrency.api.money.value.MoneyView;
import io.github.lightman314.lightmanscurrency.api.money.value.MoneyValue;
import io.github.lightman314.lightmanscurrency.common.util.IClientTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * A MoneyType class for use with registering
 */
public abstract class CurrencyType {

    protected CurrencyType(@Nonnull ResourceLocation type) {
        this.type = type;
    }

    private final ResourceLocation type;

    public final ResourceLocation getType() {
        return this.type;
    }

    /**
     * A quick method to sum a list of money values for {@link MoneyView} purposes
     * Can be modified by the {@link CurrencyType} to make the math more efficient, as this often requires adding 20-30 values
     * together when querying the contents of a container.
     */
    public final MoneyValue sumValues(@Nonnull List<MoneyValue> values)
    {
        if(values.isEmpty())
            return MoneyValue.empty();
        if(values.size() == 1)
            return values.getFirst();
        return this.sumValuesInternal(values);
    }

    /**
     * Method used by {@link #sumValues(List)} when the list size is greater than 1
     * Override this to optimize your addition calculations should your {@link MoneyValue} init
     * function do complicated math.
     */
    @Nonnull
    protected abstract MoneyValue sumValuesInternal(@Nonnull List<MoneyValue> values);

    /**
     * Method used by {@link io.github.lightman314.lightmanscurrency.api.money.MoneyAPI#GetPlayersMoneyHandler(Player) MoneyAPI#GetPlayersMoneyHandler(Player)} to create the universal {@link io.github.lightman314.lightmanscurrency.common.impl.PlayerMoneyHolder PlayerMoneyHolder} for said player.<br>
     * Method is {@link Nullable} and should return null if it is not possible for the player to <b>ever</b> handle money of this type.
     */
    @Nullable
    public abstract IPlayerMoneyHandler createMoneyHandlerForPlayer(@Nonnull Player player);

    /**
     * Method used by {@link io.github.lightman314.lightmanscurrency.api.money.MoneyAPI#GetContainersMoneyHandler(Container, Consumer, IClientTracker) MoneyAPI#GetContainersMoneyHandler(Container, Consumer)} to create a combined {@link IMoneyHandler} for said container using the provided {@link Consumer itemOverflowHandler} to handle any items that won't fit in the container<br>
     * Method is {@link Nullable} and should return null if it is not possible for money of this type to be stored or handled in an item form that doesn't have the {@link io.github.lightman314.lightmanscurrency.api.capability.money.CapabilityMoneyHandler#MONEY_HANDLER_ITEM IMoneyHandler} item capability
     */
    @Nullable
    public abstract IMoneyHandler createMoneyHandlerForContainer(@Nonnull Container container, @Nonnull Consumer<ItemStack> overflowHandler, @Nonnull IClientTracker tracker);

    /**
     * Method used by {@link io.github.lightman314.lightmanscurrency.api.money.MoneyAPI#GetATMMoneyHandler(Player, Container) MoneyAPI#GetATMMoneyHandler(Player, Container)} to create a combined {@link IMoneyHandler} for the ATM's container for use with depositing & withdrawing money from a players bank account<br>
     * Default implementation returns the results of {@link #createMoneyHandlerForContainer(Container,Consumer,IClientTracker)}<br>
     * Override if your mods money is directly attached to the player in some non-item method
     */
    @Nullable
    public IMoneyHandler createMoneyHandlerForATM(@Nonnull Player player, @Nonnull Container container) { return createMoneyHandlerForContainer(container, s -> ItemHandlerHelper.giveItemToPlayer(player,s), IClientTracker.entityWrapper(player)); }

    /**
     * Whether the given item can be placed in a {@link io.github.lightman314.lightmanscurrency.api.misc.menus.MoneySlot MoneySlot} to be potentially used as payment
     */
    public boolean allowItemInMoneySlot(@Nonnull Player player, @Nonnull ItemStack item) { return false; }

    public void addMoneySlotBackground(@Nonnull Consumer<Pair<ResourceLocation,ResourceLocation>> consumer, @Nonnull Consumer<ResourceLocation> lazyConsumer) {}

    /**
     * Function to load a money value saved to NBT.
     * Should load the value saved by {@link MoneyValue#save()}
     */
    public abstract MoneyValue loadMoneyValue(@Nonnull CompoundTag valueTag);

    /**
     * Function to load a money value saved to JSON.
     * Should load the value saved by {@link MoneyValue#toJson()}
     */
    public abstract MoneyValue loadMoneyValueJson(@Nonnull JsonObject json);

    /**
     * Returns a {@link MoneyValueParser} for this money type, allowing it to be used in commands and config options
     */
    @Nonnull
    public abstract MoneyValueParser getValueParser();

    /**
     * Only in {@link Dist#CLIENT}<br>
     * Return a list of each {@link MoneyInputHandler} required for this mod!<br>
     * {@link MoneyInputHandler}s are used by {@link io.github.lightman314.lightmanscurrency.api.money.input.MoneyValueWidget MoneyValueWidget} to allow defining prices for your mods' currency<br>
     * See {@link io.github.lightman314.lightmanscurrency.api.money.input.templates.SimpleDisplayInput SimpleDisplayInput} for a simple text template, or {@link io.github.lightman314.lightmanscurrency.api.money.input.builtin.CoinValueInput CoinValueInput} to see how my mod handles this for coins<br>
     * Returns a list as it's possible for a single currency type to contain several variants (such as different coin chains, etc.)
     */
    @OnlyIn(Dist.CLIENT)
    public abstract List<Object> getInputHandlers(@Nullable Player player);

}
