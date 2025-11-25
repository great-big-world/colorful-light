package dev.creoii.colorfullight.client.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.LeashCommandRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LeashCommandRenderer.class)
public class LeashFeatureRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/render/VertexConsumer;Lorg/joml/Matrix4f;FFFFFFIZLnet/minecraft/client/render/entity/state/EntityRenderState$LeashData;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/LightmapTextureManager;pack(II)I"))
    private static void colorfullighting$addVertexPair(VertexConsumer vertexConsumer, Matrix4f matrix, float offsetX, float offsetY, float offsetZ, float yOffset, float sideOffset, float perpendicularOffset, int segmentIndex, boolean backside, EntityRenderState.LeashData data, CallbackInfo ci, @Local(ordinal = 3) LocalIntRef k) {
        k.set(data.leashHolderBlockLight);
    }
}