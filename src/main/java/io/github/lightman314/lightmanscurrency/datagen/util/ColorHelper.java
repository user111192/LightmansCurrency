package io.github.lightman314.lightmanscurrency.datagen.util;

import io.github.lightman314.lightmanscurrency.common.core.variants.Color;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ColorHelper {

    public static Item GetConcretePowderOfColor(Color color)
    {
        return switch (color) {
            case LIGHT_GRAY -> Items.LIGHT_GRAY_CONCRETE_POWDER;
            case GRAY -> Items.GRAY_CONCRETE_POWDER;
            case BLACK -> Items.BLACK_CONCRETE_POWDER;
            case BROWN -> Items.BROWN_CONCRETE_POWDER;
            case RED -> Items.RED_CONCRETE_POWDER;
            case ORANGE -> Items.ORANGE_CONCRETE_POWDER;
            case YELLOW -> Items.YELLOW_CONCRETE_POWDER;
            case LIME -> Items.LIME_CONCRETE_POWDER;
            case GREEN -> Items.GREEN_CONCRETE_POWDER;
            case CYAN -> Items.CYAN_CONCRETE_POWDER;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_CONCRETE_POWDER;
            case BLUE -> Items.BLUE_CONCRETE_POWDER;
            case PURPLE -> Items.PURPLE_CONCRETE_POWDER;
            case MAGENTA -> Items.MAGENTA_CONCRETE_POWDER;
            case PINK -> Items.PINK_CONCRETE_POWDER;
            default -> Items.WHITE_CONCRETE_POWDER;
        };
    }
}