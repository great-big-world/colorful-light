package dev.creoii.colorfullight.client.mixin.client;

import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.ColorRGB8;
import dev.creoii.colorfullight.client.util.PackedLightData;
import dev.creoii.colorfullight.client.util.compat.LambDynLightsUtil;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.engine.source.EntityDynamicLightSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Unique
    private static final Set<EntityType<?>> FIRE_LIT_ENTITIES = new HashSet<>(Arrays.asList(
            EntityType.BLAZE,
            EntityType.MAGMA_CUBE
    ));
    @Unique
    private static final Set<EntityType<?>> LIT_ENTITIES = new HashSet<>(Arrays.asList(
            EntityType.ALLAY,
            EntityType.DRAGON_FIREBALL,
            EntityType.EXPERIENCE_ORB,
            EntityType.GLOW_SQUID,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.SHULKER_BULLET,
            EntityType.EYE_OF_ENDER,
            EntityType.FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.VEX,
            EntityType.WITHER,
            EntityType.WITHER_SKULL
    ));

    @Inject(method = "getLight", at = @At("HEAD"), cancellable = true)
    private <T extends Entity>void colorfullighting$getPackedLightCoords(T entity, float partialTicks, CallbackInfoReturnable<Integer> cir) {
        BlockPos blockpos = BlockPos.ofFloored(entity.getClientCameraPosVec(partialTicks));
        int skyLight = entity.getEntityWorld().getLightLevel(LightType.SKY, blockpos);
        ColorRGB8 color = ColoredLightEngine.getInstance().sampleTrilinearLightColor(entity.getClientCameraPosVec(partialTicks));
        if(entity.isOnFire() || FIRE_LIT_ENTITIES.contains(entity.getType())) {
            ColorRGB8 fireColor = ColorRGB8.fromRGB4(Config.getLightColor(Blocks.FIRE.getRegistryEntry()));
            color = ColorRGB8.fromRGB8(
                    Math.max(fireColor.red, color.red),
                    Math.max(fireColor.green, color.green),
                    Math.max(fireColor.blue, color.blue)
            );
        }
        if(LIT_ENTITIES.contains(entity.getType())) {
            color = ColorRGB8.fromRGB8(255, 255, 255);
        }

        if (FabricLoader.getInstance().isModLoaded("lambdynlights")) {
            if (LambDynLights.get().config.getDynamicLightsMode().isEnabled()) {
                int entityLuminance = ((EntityDynamicLightSource)entity).getLuminance();
                if (entityLuminance >= 15) {
                    cir.setReturnValue(PackedLightData.packData(skyLight, ColorRGB8.fromRGB8(255, 255, 255))); // colored entity light
                    return;
                }
                float c = ((float) LambDynLights.get().getDynamicLightLevel(entity.getBlockPos()) / 16f);
                if (entity instanceof LivingEntity living) {
                    ColorRGB4 entityColor = LambDynLightsUtil.getLivingEntityLightColor(living).mul(c);
                    cir.setReturnValue(PackedLightData.packData(skyLight, ColorRGB8.max(color, ColorRGB8.fromRGB4(entityColor))));
                    return;
                }
                cir.setReturnValue(PackedLightData.packData(skyLight, ColorRGB8.max(color, ColorRGB8.fromRGBFloat(c, c, c))));
                return;
            }
        }

        cir.setReturnValue(PackedLightData.packData(skyLight, color));
    }

    @Redirect(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;getBlockLight(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)I"))
    private <T extends Entity>int colorfullighting$extractRenderState(EntityRenderer instance, T entity, BlockPos pos) {
        int skyLight = entity.getEntityWorld().getLightLevel(LightType.SKY, pos);
        if(LIT_ENTITIES.contains(entity.getType())) {
            return PackedLightData.packData(skyLight, ColorRGB8.fromRGB8(255, 255, 255));
        }

        ColorRGB8 color = ColoredLightEngine.getInstance().sampleTrilinearLightColor(pos.toCenterPos());
        if(entity.isOnFire() || FIRE_LIT_ENTITIES.contains(entity.getType())) {
            ColorRGB8 fireColor = ColorRGB8.fromRGB4(Config.getLightColor(Blocks.FIRE.getRegistryEntry())); // cache this value
            color = ColorRGB8.fromRGB8(
                    Math.max(fireColor.red, color.red),
                    Math.max(fireColor.green, color.green),
                    Math.max(fireColor.blue, color.blue)
            );
        }

        if (FabricLoader.getInstance().isModLoaded("lambdynlights")) {
            if (LambDynLights.get().config.getDynamicLightsMode().isEnabled()) {
                int entityLuminance = ((EntityDynamicLightSource)entity).getLuminance();
                if (entityLuminance >= 15) {
                    color = ColorRGB8.fromRGB8(255, 255, 255); // colored entity light
                } else {
                    float c = ((float) LambDynLights.get().getDynamicLightLevel(entity.getBlockPos()) / 16f);
                    if (entity instanceof LivingEntity living) {
                        ColorRGB4 entityColor = LambDynLightsUtil.getLivingEntityLightColor(living).mul(c);
                        color = ColorRGB8.max(color, ColorRGB8.fromRGB4(entityColor));
                    }
                    else color = ColorRGB8.max(color, ColorRGB8.fromRGBFloat(c, c, c));
                }
            }
        }

        return PackedLightData.packData(skyLight, color);
    }
}