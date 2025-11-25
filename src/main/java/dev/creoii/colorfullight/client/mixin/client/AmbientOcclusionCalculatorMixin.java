package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.util.PackedLightData;
import net.minecraft.client.render.block.BlockModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockModelRenderer.AmbientOcclusionCalculator.class)
public class AmbientOcclusionCalculatorMixin {
    @Inject(method = "getAmbientOcclusionBrightness", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$blend(int lightColor0, int lightColor1, int lightColor2, int lightColor3, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(PackedLightData.blend(lightColor0, lightColor1, lightColor2, lightColor3));
    }

    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$blend(int lightColor0, int lightColor1, int lightColor2, int lightColor3, float weight0, float weight1, float weight2, float weight3, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(PackedLightData.blend(lightColor0, lightColor1, lightColor2, lightColor3, weight0, weight1, weight2, weight3));
    }
}