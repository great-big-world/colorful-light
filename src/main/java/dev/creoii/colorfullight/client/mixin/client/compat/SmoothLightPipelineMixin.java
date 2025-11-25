package dev.creoii.colorfullight.client.mixin.client.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.ColorRGB8;
import dev.creoii.colorfullight.client.util.PackedLightData;
import net.caffeinemc.mods.sodium.client.model.light.smooth.SmoothLightPipeline;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SmoothLightPipeline.class, remap = false)
public abstract class SmoothLightPipelineMixin {
    @Shadow(remap = false)
    private static int getLightMapCoord(float sl, float bl) {
        return 0;
    }

    @Redirect(method = "applyAlignedPartialFaceVertex", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/smooth/SmoothLightPipeline;getLightMapCoord(FF)I"), remap = false)
    private static int colorfullighting$getLightColorSodiumAligned(float sl, float bl, @Local(argsOnly = true, name = "arg1") BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            if(state.hasEmissiveLighting(world, pos)) {
                World levelAccessor = MinecraftClient.getInstance().world;
                if(levelAccessor == null) {
                    return PackedLightData.packData(15, 255, 255, 255);
                }
                var emission = Config.getLightColor(state);
                return PackedLightData.packData(15, ColorRGB8.fromRGB4(emission));
            }

            ColorRGB4 color = ColoredLightEngine.getInstance().sampleLightColor(pos);
            return PackedLightData.packData(15, ColorRGB8.fromRGB4(color));
        }
        return getLightMapCoord(sl, bl);
    }

    @Redirect(method = "applyInsetPartialFaceVertex", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/smooth/SmoothLightPipeline;getLightMapCoord(FF)I"), remap = false)
    private static int colorfullighting$getLightColorSodiumInset(float sl, float bl, @Local(argsOnly = true, name = "arg1") BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            if(state.hasEmissiveLighting(world, pos)) {
                World levelAccessor = MinecraftClient.getInstance().world;
                if(levelAccessor == null) {
                    return PackedLightData.packData(15, 255, 255, 255);
                }
                var emission = Config.getLightColor(state);
                return PackedLightData.packData(15, ColorRGB8.fromRGB4(emission));
            }

            ColorRGB4 color = ColoredLightEngine.getInstance().sampleLightColor(pos);
            return PackedLightData.packData(15, ColorRGB8.fromRGB4(color));
        }
        return getLightMapCoord(sl, bl);
    }
}
