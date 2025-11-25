package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.util.PackedLightData;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapCoordinatesRetriever.class)
public class LightmapCoordinatesRetrieverMixin {
    @Inject(method = "getFromBoth(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/entity/BlockEntity;)Lit/unimi/dsi/fastutil/ints/Int2IntFunction;", at = @At("HEAD"), cancellable = true)
    private <S extends BlockEntity> void colorfullighting$acceptDouble(S first, S second, CallbackInfoReturnable<Int2IntFunction> cir) {
        cir.setReturnValue(value -> {
            int firstLight = WorldRenderer.getLightmapCoordinates(first.getWorld(), first.getPos());
            int secondLight = WorldRenderer.getLightmapCoordinates(second.getWorld(), second.getPos());
            return PackedLightData.max(firstLight, secondLight);
        });
    }
}