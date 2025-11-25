package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkBlockLightProvider.class)
public abstract class ChunkBlockLightProviderMixin {
    @Inject(method = "checkForLightUpdate", at = @At("TAIL"))
    private void colorfullighting$checkNode(long packedPos, CallbackInfo ci) {
        if(!MinecraftClient.getInstance().isOnThread()) return; // only client side
        ColoredLightEngine.getInstance().onBlockLightPropertiesChanged(BlockPos.fromLong(packedPos));
    }
}