package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.ColorRGB8;
import dev.creoii.colorfullight.client.util.PackedLightData;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "getLightmapCoordinates(Lnet/minecraft/client/render/WorldRenderer$BrightnessGetter;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$getLightColor(WorldRenderer.BrightnessGetter brightnessGetter, BlockRenderView world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int skyLight = LightmapTextureManager.getSkyLightCoordinates(brightnessGetter.packedBrightness(world, pos));
        if(state.hasEmissiveLighting(world, pos)) {
            World levelAccessor = MinecraftClient.getInstance().world;
            if(levelAccessor == null) {
                cir.setReturnValue(PackedLightData.packData(15, 255, 255, 255));
                return;
            }
            var emission = Config.getLightColor(state);
            cir.setReturnValue(PackedLightData.packData(skyLight, ColorRGB8.fromRGB4(emission)));
            return;
        }

        ColorRGB4 color = ColoredLightEngine.getInstance().sampleLightColor(pos);
        cir.setReturnValue(PackedLightData.packData(skyLight, ColorRGB8.fromRGB4(color)));
    }
}
