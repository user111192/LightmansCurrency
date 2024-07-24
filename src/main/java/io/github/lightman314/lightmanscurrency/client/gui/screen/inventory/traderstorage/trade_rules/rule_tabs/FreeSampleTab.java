package io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.rule_tabs;

import io.github.lightman314.lightmanscurrency.LCText;
import io.github.lightman314.lightmanscurrency.api.misc.EasyText;
import io.github.lightman314.lightmanscurrency.api.misc.client.rendering.EasyGuiGraphics;
import io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.TradeRuleSubTab;
import io.github.lightman314.lightmanscurrency.client.gui.screen.inventory.traderstorage.trade_rules.TradeRulesClientTab;
import io.github.lightman314.lightmanscurrency.client.gui.widget.TimeInputWidget;
import io.github.lightman314.lightmanscurrency.client.util.TextInputUtil;
import io.github.lightman314.lightmanscurrency.common.util.IconData;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyAddonHelper;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyButton;
import io.github.lightman314.lightmanscurrency.client.gui.widget.easy.EasyTextButton;
import io.github.lightman314.lightmanscurrency.client.util.ScreenArea;
import io.github.lightman314.lightmanscurrency.client.util.TextRenderUtil;
import io.github.lightman314.lightmanscurrency.common.traders.rules.types.FreeSample;
import io.github.lightman314.lightmanscurrency.common.util.IconUtil;
import io.github.lightman314.lightmanscurrency.util.MathUtil;
import io.github.lightman314.lightmanscurrency.util.TimeUtil;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class FreeSampleTab extends TradeRuleSubTab<FreeSample> {

    public FreeSampleTab(@Nonnull TradeRulesClientTab<?> parent) { super(parent, FreeSample.TYPE); }

    @Nonnull
    @Override
    public IconData getIcon() { return IconUtil.ICON_FREE_SAMPLE; }

    EditBox limitInput;
    EasyButton buttonSetLimit;
    EasyButton buttonClearMemory;

    TimeInputWidget timeInput;

    @Override
    public void initialize(ScreenArea screenArea, boolean firstOpen) {

        this.limitInput = this.addChild(new EditBox(this.getFont(), screenArea.x + 10, screenArea.y + 19, 30, 20, EasyText.empty()));
        this.limitInput.setMaxLength(3);
        FreeSample rule = this.getRule();
        if(rule != null)
            this.limitInput.setValue(Integer.toString(rule.getLimit()));

        this.buttonSetLimit = this.addChild(new EasyTextButton(screenArea.pos.offset(41, 19), 40, 20, LCText.BUTTON_SET.get(), this::PressSetLimitButton));
        this.buttonClearMemory = this.addChild(new EasyTextButton(screenArea.pos.offset(10, 55), screenArea.width - 20, 20, LCText.BUTTON_FREE_SAMPLE_RESET.get(), this::PressClearMemoryButton)
                .withAddons(EasyAddonHelper.tooltip(LCText.TOOLTIP_FREE_SAMPLE_RESET)));

        this.timeInput = this.addChild(new TimeInputWidget(screenArea.pos.offset(63, 92), 10, TimeUtil.TimeUnit.DAY, TimeUtil.TimeUnit.MINUTE, this::onTimeSet));
        this.timeInput.setTime(this.getRule().getTimeLimit());

    }

    @Override
    public void renderBG(@Nonnull EasyGuiGraphics gui) {

        FreeSample rule = this.getRule();
        if(rule != null)
        {
            gui.drawString(LCText.GUI_FREE_SAMPLE_INFO.get(rule.getLimit()), 10, 9, 0xFFFFFF);
            TextRenderUtil.drawCenteredMultilineText(gui, LCText.GUI_FREE_SAMPLE_PLAYER_COUNT.get(rule.getSampleCount()), 10, this.screen.getXSize() - 20, 42, 0x404040);
        }

        Component text = this.getRule().getTimeLimit() > 0 ? LCText.GUI_PLAYER_TRADE_LIMIT_DURATION.get(new TimeUtil.TimeData(this.getRule().getTimeLimit()).getShortString()) : LCText.GUI_PLAYER_TRADE_LIMIT_NO_DURATION.get();
        TextRenderUtil.drawCenteredText(gui, text, this.screen.getXSize() / 2, 80, 0xFFFFFF);

    }

    void PressSetLimitButton(EasyButton button)
    {
        int limit = MathUtil.clamp(TextInputUtil.getIntegerValue(this.limitInput), 1, 100);
        FreeSample rule = this.getRule();
        if(rule != null)
            rule.setLimit(limit);
        this.sendUpdateMessage(this.builder().setInt("Limit", limit));
    }

    void PressClearMemoryButton(EasyButton button)
    {
        FreeSample rule = this.getRule();
        if(rule != null)
            rule.resetMemory();
        this.sendUpdateMessage(this.builder().setFlag("ClearMemory"));
    }

    public void onTimeSet(TimeUtil.TimeData newTime) {
        long timeLimit = MathUtil.clamp(newTime.miliseconds, 0, Long.MAX_VALUE);
        FreeSample rule = this.getRule();
        if(rule != null)
            rule.setTimeLimit(timeLimit);
        this.sendUpdateMessage(this.builder().setLong("TimeLimit", timeLimit));
    }

}
