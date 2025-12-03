package dev.creoii.colorfullight.client.mixin.client.compat;

import dev.creoii.colorfullight.client.util.PackedLightData;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AoCalculator.class)
public class AoCalculatorMixin {
    @Inject(method = "vanillaMeanLight", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$blendVanilla(int lightColor0, int lightColor1, int lightColor2, int lightColor3, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(PackedLightData.blend(lightColor0, lightColor1, lightColor2, lightColor3));
    }

    @Inject(method = "meanInnerLight", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$blend(int lightColor0, int lightColor1, int lightColor2, int lightColor3, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(PackedLightData.blend(lightColor0, lightColor1, lightColor2, lightColor3));
    }

}
