package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.util.PackedLightData;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @Inject(method = "getLight", at = @At("HEAD"), cancellable = true)
    private void colorfullighting$getLightColor(BlockRenderView world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int lightColor = WorldRenderer.getLightmapCoordinates(world, pos);
        int lightColorAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());

        cir.setReturnValue(PackedLightData.max(lightColor, lightColorAbove));
    }
}