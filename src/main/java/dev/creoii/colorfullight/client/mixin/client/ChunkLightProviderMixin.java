package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {
    @Inject(method = "doLightUpdates", at = @At("TAIL"))
    private void colorfullighting$runLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if(!MinecraftClient.getInstance().isOnThread()) return; // only client side
        ColoredLightEngine.getInstance().onLightUpdate();
    }

    @Inject(method = "needsLightUpdate", at = @At("HEAD"), cancellable = true)
    private static void colorfullighting$hasDifferentLightProperties(BlockState oldState, BlockState newState, CallbackInfoReturnable<Boolean> cir) {
        if(!MinecraftClient.getInstance().isOnThread())
            return; // only client side

        ClientWorld clientLevel = MinecraftClient.getInstance().world;
        if(clientLevel == null) return;
        ColorRGB4 color1 = Config.getColorEmission(clientLevel, oldState);
        ColorRGB4 color2 = Config.getColorEmission(clientLevel, newState);
        if(!color1.equals(color2)) {
            cir.setReturnValue(true);
            return;
        }
        color1 = Config.getColoredLightTransmittance(clientLevel, oldState);
        color2 = Config.getColoredLightTransmittance(clientLevel, newState);
        if(!color1.equals(color2)) {
            cir.setReturnValue(true);
        }
    }
}