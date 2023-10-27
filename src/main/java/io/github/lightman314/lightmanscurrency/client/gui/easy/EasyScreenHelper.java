package io.github.lightman314.lightmanscurrency.client.gui.easy;

import io.github.lightman314.lightmanscurrency.client.gui.easy.interfaces.ITooltipSource;
import io.github.lightman314.lightmanscurrency.client.gui.easy.rendering.EasyGuiGraphics;
import io.github.lightman314.lightmanscurrency.common.easy.IEasyTickable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

public class EasyScreenHelper {

    @Nullable
    public static IEasyTickable getWidgetTicker(@Nullable Object widget)
    {
        if(widget instanceof EditBox e)
            return e::tick;
        if(widget instanceof IEasyTickable t)
            return t;
        return null;
    }

    public static void RenderTooltips(EasyGuiGraphics gui, List<ITooltipSource> tooltipSources)
    {
        for(ITooltipSource tooltipSource : tooltipSources)
            tooltipSource.renderTooltip(gui);
    }

    public static List<Component> getTooltipFromItem(ItemStack item)
    {
        Minecraft mc = Minecraft.getInstance();
        return item.getTooltipLines(mc.player, mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
    }

}
