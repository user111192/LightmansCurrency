package io.github.lightman314.lightmanscurrency.client.renderer.blockentity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import io.github.lightman314.lightmanscurrency.client.resourcepacks.data.item_trader.ItemPositionBlockManager;
import io.github.lightman314.lightmanscurrency.client.resourcepacks.data.item_trader.ItemPositionData;
import io.github.lightman314.lightmanscurrency.common.blockentity.AuctionStandBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuctionStandBlockEntityRenderer implements BlockEntityRenderer<AuctionStandBlockEntity> {


    private final ItemRenderer itemRenderer;
    public AuctionStandBlockEntityRenderer(BlockEntityRendererProvider.Context context) { this.itemRenderer = context.getItemRenderer(); }

    @Override
    public void render(@NotNull AuctionStandBlockEntity blockEntity, float partialTicks, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int lightLevel, int id) {

        ImmutableList<ItemStack> displayItems = AuctionStandBlockEntity.getDisplayItems();
        if(displayItems.size() < 1)
            return;

        BlockState state = blockEntity.getBlockState();

        ItemPositionData data = ItemPositionBlockManager.getDataForBlock(state);
        List<Vector3f> positions = data.getPositions(state, 0);
        if(positions.size() == 0)
            return;

        pose.pushPose();
        Vector3f pos = positions.get(0);
        pose.translate(pos.x(), pos.y(), pos.z());

        for(Quaternion r : data.getRotation(state, 0, partialTicks))
            pose.mulPose(r);

        float scale = data.getScale(0);
        pose.scale(scale,scale,scale);

        if(displayItems.size() < 2)
        {
            //Only render 1 item
            this.itemRenderer.renderStatic(displayItems.get(0), ItemTransforms.TransformType.FIXED, lightLevel, OverlayTexture.NO_OVERLAY, pose, buffer, id);
        }
        else
        {
            //Render Item 1
            pose.pushPose();
            pose.translate(-0.55f,0f,0f);
            this.itemRenderer.renderStatic(displayItems.get(0), ItemTransforms.TransformType.FIXED, lightLevel, OverlayTexture.NO_OVERLAY, pose, buffer, id);
            pose.popPose();

            //Render Item 2
            pose.pushPose();
            pose.translate(0.55f, 0f, 0f);
            this.itemRenderer.renderStatic(displayItems.get(1),  ItemTransforms.TransformType.FIXED, lightLevel, OverlayTexture.NO_OVERLAY, pose, buffer, id);
            pose.popPose();
        }
        pose.popPose();

    }

}