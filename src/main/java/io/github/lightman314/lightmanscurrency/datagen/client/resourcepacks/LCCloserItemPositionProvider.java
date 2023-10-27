package io.github.lightman314.lightmanscurrency.datagen.client.resourcepacks;

import com.mojang.math.Vector3f;
import io.github.lightman314.lightmanscurrency.LightmansCurrency;
import io.github.lightman314.lightmanscurrency.client.resourcepacks.data.item_trader.RotationHandler;
import io.github.lightman314.lightmanscurrency.datagen.client.builders.ItemPositionBuilder;
import io.github.lightman314.lightmanscurrency.datagen.client.generators.ItemPositionProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class LCCloserItemPositionProvider extends ItemPositionProvider {


    public LCCloserItemPositionProvider(@Nonnull DataGenerator output) { super(output, LightmansCurrency.MODID, "CloserItemsPack"); }

    @Override
    protected void addEntries() {
        //Card Display
        this.addData(new ResourceLocation(LightmansCurrency.MODID, "card_display"),ItemPositionBuilder.builder()
                .withGlobalScale(0.4f)
                .withGlobalRotationType(RotationHandler.FACING_UP)
                .withGlobalExtraCount(2)
                .withGlobalExtraOffset(new Vector3f(MO, 0.1f, MO))
                .withEntry(new Vector3f(5f/16f, 9f/16f,4.5f/16f))
                .withEntry(new Vector3f(11f/16f, 9f/16f,4.5f/16f))
                .withEntry(new Vector3f(5f/16f, 12f/16f,12f/16f))
                .withEntry(new Vector3f(11f/16f, 12f/16f,12f/16f))
        );
        //Display Case
        //No change needed for this as it only draws 1 item regardless
        //Freezer
        this.addData(new ResourceLocation(LightmansCurrency.MODID,"freezer"), ItemPositionBuilder.builder()
                .withGlobalScale(0.4f)
                .withGlobalRotationType(RotationHandler.FACING)
                .withGlobalExtraCount(5)
                .withGlobalExtraOffset(new Vector3f(MO,MO,0.1f))
                .withEntry(new Vector3f(5f/16f,28f/16f,6f/16f))
                .withEntry(new Vector3f(11f/16f,28f/16f,6f/16f))
                .withEntry(new Vector3f(5f/16f,21f/16f,6f/16f))
                .withEntry(new Vector3f(11f/16f,21f/16f,6f/16f))
                .withEntry(new Vector3f(5f/16f,14f/16f,6f/16f))
                .withEntry(new Vector3f(11f/16f,14f/16f,6f/16f))
                .withEntry(new Vector3f(5f/16f,7f/16f,6f/16f))
                .withEntry(new Vector3f(11f/16f,7f/16f,6f/16f))
        );
        //Shelf
        this.addData(new ResourceLocation(LightmansCurrency.MODID,"shelf"), ItemPositionBuilder.builder()
                .withGlobalScale(14f/16f)
                .withGlobalRotationType(RotationHandler.FACING)
                .withGlobalExtraCount(1)
                .withGlobalExtraOffset(new Vector3f(MO,MO,-0.1f))
                .withEntry(new Vector3f(0.5f, 9f/16f,14.5f/16f))
        );
        //Shelf 2x2
        this.addData(new ResourceLocation(LightmansCurrency.MODID,"shelf_2x2"), ItemPositionBuilder.builder()
                .withGlobalScale(5.5f/16f)
                .withGlobalRotationType(RotationHandler.FACING)
                .withGlobalExtraCount(1)
                .withGlobalExtraOffset(new Vector3f(MO,MO,-0.1f))
                .withEntry(new Vector3f(0.25f, 13f/16f,14.5f/16f))
                .withEntry(new Vector3f(0.75f, 13f/16f,14.5f/16f))
                .withEntry(new Vector3f(0.25f, 5f/16f,14.5f/16f))
                .withEntry(new Vector3f(0.75f, 5f/16f,14.5f/16f))
        );
        //Vending Machine
        this.addData(new ResourceLocation(LightmansCurrency.MODID, "vending_machine"), ItemPositionBuilder.builder()
                .withGlobalScale(0.3f)
                .withGlobalRotationType(RotationHandler.FACING)
                .withGlobalExtraCount(5)
                .withGlobalExtraOffset(new Vector3f(MO,MO,0.1f))
                .withEntry(new Vector3f(3.5f/16f,27f/16f,6/16f))
                .withEntry(new Vector3f(9.5f/16f,27f/16f,6/16f))
                .withEntry(new Vector3f(3.5f/16f,20f/16f,6/16f))
                .withEntry(new Vector3f(9.5f/16f,20f/16f,6/16f))
                .withEntry(new Vector3f(3.5f/16f,13f/16f,6/16f))
                .withEntry(new Vector3f(9.5f/16f,13f/16f,6/16f))
        );
        //Large Vending Machine
        this.addData(new ResourceLocation(LightmansCurrency.MODID,"large_vending_machine"), ItemPositionBuilder.builder()
                .withGlobalScale(0.3f)
                .withGlobalRotationType(RotationHandler.FACING)
                .withGlobalExtraCount(5)
                .withGlobalExtraOffset(new Vector3f(MO,MO,0.1f))
                .withEntry(new Vector3f(3.5f/16f,27f/16f,6f/16f))
                .withEntry(new Vector3f(10.5f/16f,27f/16f,6f/16f))
                .withEntry(new Vector3f(17.5f/16f,27f/16f,6f/16f))
                .withEntry(new Vector3f(24.5f/16f,27f/16f,6f/16f))
                .withEntry(new Vector3f(3.5f/16f,20f/16f,6f/16f))
                .withEntry(new Vector3f(10.5f/16f,20f/16f,6f/16f))
                .withEntry(new Vector3f(17.5f/16f,20f/16f,6f/16f))
                .withEntry(new Vector3f(24.5f/16f,20f/16f,6f/16f))
                .withEntry(new Vector3f(3.5f/16f,13f/16f,6f/16f))
                .withEntry(new Vector3f(10.5f/16f,13f/16f,6f/16f))
                .withEntry(new Vector3f(17.5f/16f,13f/16f,6f/16f))
                .withEntry(new Vector3f(24.5f/16f,13f/16f,6f/16f))
        );

        //Auction House
        //No change needed for this as it only draws 1 item regardless

    }

}