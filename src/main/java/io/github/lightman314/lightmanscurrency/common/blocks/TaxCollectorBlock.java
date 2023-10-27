package io.github.lightman314.lightmanscurrency.common.blocks;

import com.google.common.collect.ImmutableList;
import io.github.lightman314.lightmanscurrency.Config;
import io.github.lightman314.lightmanscurrency.LightmansCurrency;
import io.github.lightman314.lightmanscurrency.common.blockentity.TaxBlockEntity;
import io.github.lightman314.lightmanscurrency.common.blocks.interfaces.IEasyEntityBlock;
import io.github.lightman314.lightmanscurrency.common.blocks.interfaces.IOwnableBlock;
import io.github.lightman314.lightmanscurrency.common.blocks.templates.RotatableBlock;
import io.github.lightman314.lightmanscurrency.common.core.ModBlockEntities;
import io.github.lightman314.lightmanscurrency.common.easy.EasyText;
import io.github.lightman314.lightmanscurrency.common.items.TooltipItem;
import io.github.lightman314.lightmanscurrency.common.items.tooltips.LCTooltips;
import io.github.lightman314.lightmanscurrency.common.menus.validation.types.BlockEntityValidator;
import io.github.lightman314.lightmanscurrency.common.taxes.TaxEntry;
import io.github.lightman314.lightmanscurrency.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class TaxCollectorBlock extends RotatableBlock implements IOwnableBlock, IEasyEntityBlock {

    public TaxCollectorBlock(Properties properties) { super(properties); }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity entity, @Nonnull ItemStack stack) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof TaxBlockEntity taxBlock && entity instanceof Player player)
            taxBlock.initialize(player);
    }

    @Override
    public void playerWillDestroy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
        if(level.getBlockEntity(pos) instanceof TaxBlockEntity be)
        {
            TaxEntry entry = be.getTaxEntry();
            if(entry != null)
            {
                if(entry.getOwner().isAdmin(player))
                {
                    be.flagAsValidBreak();
                    InventoryUtil.dumpContents(level, pos, be.getContents(!player.isCreative()));
                }
                else
                    return;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) { return PushReaction.BLOCK; }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean flag) {
        if(level.getBlockEntity(pos) instanceof TaxBlockEntity taxBlock)
            taxBlock.onRemove();
        super.onRemove(state, level, pos, newState, flag);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        if(!level.isClientSide && level.getBlockEntity(pos) instanceof TaxBlockEntity taxBlock)
        {
            TaxEntry entry = taxBlock.getTaxEntry();
            if(entry == null)
            {
                LightmansCurrency.LogWarning("Tax Entry for block at " + pos.getX() + "," + pos.getY() + "," + pos.getZ() + " had to be re-initialized on interaction.");
                EasyText.sendMessage(player, EasyText.translatable("tax_block.warning.reinitialized").withStyle(ChatFormatting.RED));
                taxBlock.initialize(player);
                entry = taxBlock.getTaxEntry();
            }
            if(entry != null)
            {
                if(entry.canAccess(player))
                    entry.openMenu(player, BlockEntityValidator.of(taxBlock));
                else
                    EasyText.sendMessage(player, EasyText.translatable("tax_block.warning.no_access"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canBreak(Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        if(level.getBlockEntity(pos) instanceof TaxBlockEntity taxBlock)
        {
            TaxEntry entry = taxBlock.getTaxEntry();
            if(entry != null)
                return entry.getOwner().isAdmin(player);
        }
        return true;
    }

    @Override
    public Collection<BlockEntityType<?>> getAllowedTypes() { return ImmutableList.of(ModBlockEntities.TAX_BLOCK.get()); }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) { return new TaxBlockEntity(pos, state); }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable BlockGetter level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
        TooltipItem.addTooltip(tooltips, LCTooltips.TAX_COLLECTOR);
        if(Config.SERVER.taxMachinesAdminOnly.get())
            tooltips.add(EasyText.translatable("tooltip.lightmanscurrency.tax_collector.admin_only").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD));
    }
}