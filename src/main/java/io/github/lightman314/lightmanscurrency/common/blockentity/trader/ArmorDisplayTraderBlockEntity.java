package io.github.lightman314.lightmanscurrency.common.blockentity.trader;

import java.util.List;
import java.util.UUID;

import io.github.lightman314.lightmanscurrency.common.core.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import io.github.lightman314.lightmanscurrency.common.blocks.templates.interfaces.IRotatableBlock;
import io.github.lightman314.lightmanscurrency.common.traders.item.ItemTraderData;
import io.github.lightman314.lightmanscurrency.common.traders.item.ItemTraderDataArmor;
import io.github.lightman314.lightmanscurrency.common.traders.item.tradedata.ItemTradeData;
import io.github.lightman314.lightmanscurrency.common.traders.item.tradedata.restrictions.EquipmentRestriction;
import io.github.lightman314.lightmanscurrency.common.traders.item.tradedata.restrictions.ItemTradeRestriction;
import org.jetbrains.annotations.NotNull;

public class ArmorDisplayTraderBlockEntity extends ItemTraderBlockEntity {

	public static final int TRADE_COUNT = 4;
	private static final int TICK_DELAY = 20;
	
	UUID armorStandID = null;
	
	int updateTimer = TICK_DELAY;
	
	private boolean loaded = false;
	public void flagAsLoaded() { this.loaded = true; }
	
	public ArmorDisplayTraderBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.ARMOR_TRADER.get(), pos, state, TRADE_COUNT);
	}
	
	@Override
	public ItemTraderData buildNewTrader() { return new ItemTraderDataArmor(this.level, this.worldPosition); }
	
	@Override
	public void serverTick()
	{
		
		super.serverTick();
		
		if(!this.loaded)
			return;
		
		if(this.updateTimer <= 0)
		{
			this.updateTimer = TICK_DELAY;
			this.validateArmorStand();
			this.validateArmorStandValues();
			this.updateArmorStandArmor();
			this.killIntrudingArmorStands();
		}
		else
			this.updateTimer--;
	}
	
	/**
	 * Validates the armor stands existence, the local ArmorStandID, and gets the local reference to the armor stand.
	 * Logical Server only.
	 */
	public void validateArmorStand() {
		if(this.isClient())
			return;
		ArmorStand armorStand = this.getArmorStand();
		if(armorStand == null || armorStand.isRemoved())
		{
			//Spawn a new armor stand
			this.spawnArmorStand();
		}
	}
	
	private void spawnArmorStand()
	{
		if(this.level == null || this.isClient())
			return;
		
		ArmorStand armorStand = new ArmorStand(this.level, this.worldPosition.getX() + 0.5d, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5d);
		armorStand.moveTo(this.worldPosition.getX() + 0.5d, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5d, this.getStandRotation(), 0.0F);
		
		armorStand.setInvulnerable(true);
		armorStand.setNoGravity(true);
		armorStand.setSilent(true);
		CompoundTag compound = armorStand.saveWithoutId(new CompoundTag());
		compound.putBoolean("Marker", true);
		compound.putBoolean("NoBasePlate", true);
		armorStand.load(compound);
		
		this.level.addFreshEntity(armorStand);
		
		this.armorStandID = armorStand.getUUID();
		this.setChanged();
		
	}
	
	protected void updateArmorStandArmor() {
		ArmorStand armorStand = this.getArmorStand();
		if(armorStand != null)
		{
			ItemTraderData trader = this.getTraderData();
			if(trader != null)
			{
				List<ItemTradeData> trades = trader.getTradeData();
				for(int i = 0; i < 4 && i < trades.size(); i++)
				{
					ItemTradeData thisTrade = trades.get(i);
					//Trade restrictions shall determine the slot type
					ItemTradeRestriction r = thisTrade.getRestriction();
					EquipmentSlot slot = null;
					if(r instanceof EquipmentRestriction er)
					{
						slot = er.getEquipmentSlot();
					}
					if(slot != null)
					{
						if(thisTrade.hasStock(trader) || trader.isCreative())
							armorStand.setItemSlot(slot, thisTrade.getSellItem(0));
						else
							armorStand.setItemSlot(slot, ItemStack.EMPTY);
					}
				}
			}
			
		}
	}
	
	public void killIntrudingArmorStands() {
		ArmorStand armorStand = this.getArmorStand();
		if(this.level != null && armorStand != null)
		{
			this.level.getEntitiesOfClass(ArmorStand.class, this.getBlockState().getShape(this.level, this.worldPosition).bounds()).forEach(as ->{
				//Delete any armor stands in the exact coordinates as our armor stand.
				//Should delete any old duplicates from previously buggy armor stands.
				if(as.position().equals(armorStand.position()))
					as.remove(Entity.RemovalReason.DISCARDED);
			});
		}
	}
	
	protected void validateArmorStandValues()
	{
		ArmorStand armorStand = this.getArmorStand();
		if(armorStand == null)
			return;
		armorStand.moveTo(this.worldPosition.getX() + 0.5d, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5f, this.getStandRotation(), 0f);
		if(!armorStand.isInvulnerable())
			armorStand.setInvulnerable(true);
		if(armorStand.isInvisible())
			armorStand.setInvisible(false);
		if(!armorStand.noPhysics)
			armorStand.setNoGravity(true);
		if(!armorStand.isSilent())
			armorStand.setSilent(true);
		if(!armorStand.isMarker() || !armorStand.isNoBasePlate())
		{
			CompoundTag compound = armorStand.saveWithoutId(new CompoundTag());
			if(!armorStand.isMarker())
				compound.putBoolean("Marker", true);
			if(!armorStand.isNoBasePlate())
				compound.putBoolean("NoBasePlate", true);
			armorStand.load(compound);
		}
	}
	
	@Override
	public void saveAdditional(@NotNull CompoundTag compound)
	{
		this.writeArmorStandData(compound);
		
		super.saveAdditional(compound);
	}
	
	protected CompoundTag writeArmorStandData(CompoundTag compound)
	{
		if(this.armorStandID != null)
			compound.putUUID("ArmorStand", this.armorStandID);
		return compound;
	}
	
	@Override
	public void load(@NotNull CompoundTag compound)
	{
		this.loaded = true;
		if(compound.contains("ArmorStand"))
			this.armorStandID = compound.getUUID("ArmorStand");
		super.load(compound);
	}
	
	protected ArmorStand getArmorStand()
	{
		if(this.level instanceof ServerLevel)
		{
			Entity entity = ((ServerLevel)level).getEntity(this.armorStandID);
			if(entity instanceof ArmorStand)
				return (ArmorStand)entity;
		}
		
		return null;
	}
	
	public void destroyArmorStand()
	{
		ArmorStand armorStand = this.getArmorStand();
		if(armorStand != null)
			armorStand.kill();
	}
	
	protected float getStandRotation()
	{
		Direction facing = Direction.NORTH;
		if(this.getBlockState().getBlock() instanceof IRotatableBlock)
			facing = ((IRotatableBlock)this.getBlockState().getBlock()).getFacing(this.getBlockState());
		if(facing == Direction.SOUTH)
			return 180f;
		else if(facing == Direction.NORTH)
			return 0f;
		else if(facing == Direction.WEST)
			return -90f;
		else if(facing == Direction.EAST)
			return 90f;
		return 0f;
	}
	
	@Override @Deprecated
	protected ItemTraderData createTraderFromOldData(CompoundTag compound) {
		ItemTraderDataArmor newTrader = new ItemTraderDataArmor(this.level, this.worldPosition);
		newTrader.loadOldUniversalTraderData(compound);
		this.tradeCount = newTrader.getTradeCount();
		return newTrader;
	}
	
}
