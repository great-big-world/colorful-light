package dev.creoii.colorfullight.client.mixin.client.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.ColorRGB8;
import dev.creoii.colorfullight.client.util.PackedLightData;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.caffeinemc.mods.sodium.client.model.light.flat.FlatLightPipeline;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FlatLightPipeline.class, remap = false)
public abstract class FlatLightPipelineMixin {
    @Redirect(method = "getOffsetLightmap", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/light/data/LightDataAccess;unpackBL(I)I"), remap = false)
    private static int colorfullighting$getLightColorSodiumFlat(int word, @Local(argsOnly = true, name = "arg1") BlockPos pos) {
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
        return LightDataAccess.unpackBL(word);
    }
}
