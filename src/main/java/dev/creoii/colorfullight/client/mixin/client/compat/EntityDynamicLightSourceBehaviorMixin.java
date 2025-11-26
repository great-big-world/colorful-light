package dev.creoii.colorfullight.client.mixin.client.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.engine.source.EntityDynamicLightSourceBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityDynamicLightSourceBehavior.class, remap = false)
public interface EntityDynamicLightSourceBehaviorMixin {
    @Inject(method = "tickEntity", at = @At("TAIL"))
    private static void gbw$tickEntityColoredLightMovement(Entity entity, CallbackInfo ci, @Local(name = "lightSource") EntityDynamicLightSourceBehavior lightSource) {
        if (LambDynLights.get().shouldTick(lightSource)) {
            double x = lightSource.getDynamicLightPrevX() - lightSource.getDynamicLightX();
            double y = lightSource.getDynamicLightPrevY() - lightSource.getDynamicLightY();
            double z = lightSource.getDynamicLightPrevZ() - lightSource.getDynamicLightZ();
            if (x * x + y * y + z * z <= .01d)
                return;

            BlockPos prev = BlockPos.ofFloored(lightSource.getDynamicLightPrevX(), lightSource.getDynamicLightPrevY(), lightSource.getDynamicLightPrevZ());
            ColorRGB4 lightColor = ColoredLightEngine.getInstance().getStorage().getEntry(prev);

            if (lightColor == null)
                return;

            if (lightColor.isZero())
                ColoredLightEngine.getInstance().requestLightPullIn(ColoredLightEngine.getBlockUpdateDecreaseRequests(), prev);
            else ColoredLightEngine.getBlockUpdateDecreaseRequests().add(new ColoredLightEngine.LightUpdateRequest(prev, lightColor, true));
        }
    }
}
