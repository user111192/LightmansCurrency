package io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.rule_tabs;

import io.github.lightman314.lightmanscurrency.api.misc.client.rendering.EasyGuiGraphics;
import io.github.lightman314.lightmanscurrency.api.network.LazyPacketData;
import io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.TradeRuleSubTab;
import io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.TradeRulesClientTab;
import io.github.lightman314.lightmanscurrency.client.gui.widget.TimeInputWidget;
import io.github.lightman314.lightmanscurrency.client.gui.widget.button.icon.IconData;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyButton;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyTextButton;
import io.github.lightman314.lightmanscurrency.client.util.IconAndButtonUtil;
import io.github.lightman314.lightmanscurrency.client.util.ScreenArea;
import io.github.lightman314.lightmanscurrency.client.util.TextInputUtil;
import io.github.lightman314.lightmanscurrency.client.util.TextRenderUtil;
import io.github.lightman314.lightmanscurrency.common.easy.EasyText;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.PriceFluctuation;
import io.github.lightman314.lightmanscurrency.util.TimeUtil;
import net.minecraft.client.gui.components.EditBox;

import javax.annotation.Nonnull;

public class PriceFluctuationTab extends TradeRuleSubTab<PriceFluctuation> {

    public PriceFluctuationTab(@Nonnull TradeRulesClientTab<?> parent) { super(parent, PriceFluctuation.TYPE); }

    @Nonnull
    @Override
    public IconData getIcon() { return IconAndButtonUtil.ICON_PRICE_FLUCTUATION; }

    EditBox fluctuationInput;
    EasyButton buttonSetFluctuation;

    TimeInputWidget durationInput;

    @Override
    public void initialize(ScreenArea screenArea, boolean firstOpen) {

        this.fluctuationInput = this.addChild(new EditBox(this.getFont(), screenArea.x + 25, screenArea.y + 9, 20, 20, EasyText.empty()));
        this.fluctuationInput.setMaxLength(2);
        PriceFluctuation rule = this.getRule();
        if(rule != null)
            this.fluctuationInput.setValue(Integer.toString(rule.getFluctuation()));

        this.buttonSetFluctuation = this.addChild(new EasyTextButton(screenArea.pos.offset(125, 10), 50, 20, EasyText.translatable("gui.button.lightmanscurrency.discount.set"), this::PressSetFluctuationButton));

        this.durationInput = this.addChild(new TimeInputWidget(screenArea.pos.offset(63, 75), 10, TimeUtil.TimeUnit.DAY, TimeUtil.TimeUnit.MINUTE, this::onTimeSet));
        this.durationInput.setTime(this.getRule().getDuration());

    }

    @Override
    public void renderBG(@Nonnull EasyGuiGraphics gui) {

        PriceFluctuation rule = this.getRule();
        if(rule == null)
            return;

        gui.pushOffset(this.fluctuationInput);
        gui.drawString(EasyText.translatable("gui.lightmanscurrency.fluctuation.tooltip"), this.fluctuationInput.getWidth() + 4, 3, 0xFFFFFF);
        gui.popOffset();

        TextRenderUtil.drawCenteredMultilineText(gui, EasyText.translatable("gui.button.lightmanscurrency.price_fluctuation.info", this.getRule().getFluctuation(), new TimeUtil.TimeData(this.getRule().getDuration()).getShortString()), 10, this.screen.getXSize() - 20, 35, 0xFFFFFF);

    }

    @Override
    public void tick() { TextInputUtil.whitelistInteger(this.fluctuationInput, 1, Integer.MAX_VALUE); }

    void PressSetFluctuationButton(EasyButton button)
    {
        int fluctuation = TextInputUtil.getIntegerValue(this.fluctuationInput, 1);
        PriceFluctuation rule = this.getRule();
        if(rule != null)
            rule.setFluctuation(fluctuation);
        this.sendUpdateMessage(LazyPacketData.simpleInt("Fluctuation", fluctuation));
    }

    public void onTimeSet(TimeUtil.TimeData newTime)
    {
        PriceFluctuation rule = this.getRule();
        if(rule != null)
            rule.setDuration(newTime.miliseconds);
        this.sendUpdateMessage(LazyPacketData.simpleLong("Duration", newTime.miliseconds));
    }


}
