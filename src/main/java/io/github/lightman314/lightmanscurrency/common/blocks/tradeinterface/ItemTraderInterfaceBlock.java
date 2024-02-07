package io.github.lightman314.lightmanscurrency.common.blocks.tradeinterface;

import java.util.List;

import io.github.lightman314.lightmanscurrency.common.blockentity.ItemTraderInterfaceBlockEntity;
import io.github.lightman314.lightmanscurrency.api.trader_interface.blockentity.TraderInterfaceBlockEntity;
import io.github.lightman314.lightmanscurrency.api.trader_interface.blocks.TraderInterfaceBlock;
import io.github.lightman314.lightmanscurrency.common.core.ModBlockEntities;
import io.github.lightman314.lightmanscurrency.common.items.tooltips.LCTooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;

public class ItemTraderInterfaceBlock extends TraderInterfaceBlock {

	public ItemTraderInterfaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ItemTraderInterfaceBlockEntity(pos, state);
	}

	@Override
	protected BlockEntityType<?> interfaceType() {
		return ModBlockEntities.TRADER_INTERFACE_ITEM.get();
	}
	
	@Override
	protected NonNullSupplier<List<Component>> getItemTooltips() { return LCTooltips.ITEM_TRADER_INTERFACE; }

	@Override
	protected void onInvalidRemoval(BlockState state, Level level, BlockPos pos, TraderInterfaceBlockEntity trader) { }
	
}
