package dev.creoii.colorfullight.client.mixin.client.compat;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.colorfullight.client.light.ColoredLightEngine;
import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.ColorRGB8;
import dev.creoii.colorfullight.client.util.PackedLightData;
import dev.creoii.colorfullight.client.util.compat.LambDynLightsUtil;
import dev.lambdaurora.lambdynlights.LambDynLights;
import dev.lambdaurora.lambdynlights.engine.source.DeferredDynamicLightSource;
import dev.lambdaurora.lambdynlights.engine.source.DynamicLightSource;
import dev.lambdaurora.lambdynlights.engine.source.EntityDynamicLightSource;
import dev.lambdaurora.lambdynlights.engine.source.EntityDynamicLightSourceBehavior;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LambDynLights.class)
public abstract class LambDynLightsMixin {
    @Shadow
    public abstract boolean shouldTick(EntityDynamicLightSource entity);

    @Shadow
    @Final
    private List<DynamicLightSource> toClear;

    @Inject(method = "getLightmapWithDynamicLight(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;I)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void gbw$applyColorLightToDynamicLights(BlockRenderView world, BlockPos pos, int lightmap, CallbackInfoReturnable<Integer> cir) {
        BlockState state = world.getBlockState(pos);
        if(state.hasEmissiveLighting(world, pos)) {
            var emission = Config.getLightColor(state);
            cir.setReturnValue(PackedLightData.packData(15, ColorRGB8.fromRGB4(emission)));
        }

        ColorRGB4 color = ColoredLightEngine.getInstance().sampleLightColor(pos);
        cir.setReturnValue(PackedLightData.packData(world.getLightLevel(LightType.SKY, pos), ColorRGB8.fromRGB4(color)));
    }

    @Inject(method = "onEndLevelTick", at = @At(value = "INVOKE", target = "Ldev/lambdaurora/lambdynlights/engine/scheduler/ChunkRebuildScheduler;update(Ldev/lambdaurora/lambdynlights/engine/source/DynamicLightSource;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;)V"))
    private void gbw$addDynamicColoredLightSource(CallbackInfo ci, @Local(name = "lightSource") DynamicLightSource lightSource) {
        if(!MinecraftClient.getInstance().isOnThread())
            return; // only client side

        ClientWorld world = MinecraftClient.getInstance().world;
        if (lightSource instanceof EntityDynamicLightSource entityDynamicLightSource && entityDynamicLightSource.isDynamicLightEnabled() && shouldTick(entityDynamicLightSource)) {
            BlockPos pos = BlockPos.ofFloored(entityDynamicLightSource.getDynamicLightX(), entityDynamicLightSource.getDynamicLightY(), entityDynamicLightSource.getDynamicLightZ());

            Entity entity = world.getOtherEntities(null, Box.from(new Vec3d(pos))).getFirst();
            if (entity instanceof LivingEntity living) {
                ColoredLightEngine.BlockRequests blockRequests = new ColoredLightEngine.BlockRequests(pos);

                blockRequests.increaseRequests.add(new ColoredLightEngine.LightUpdateRequest(pos, LambDynLightsUtil.getLivingEntityLightColor(living), false));
                ColoredLightEngine.getBlockUpdateIncreaseRequests().add(blockRequests);
            }
        }/* else if (lightSource instanceof DeferredDynamicLightSource deferredDynamicLightSource && shouldTick(deferredDynamicLightSource)) {
            DynamicLightBehavior.BoundingBox box = deferredDynamicLightSource.behavior().getBoundingBox();
            BlockPos.iterate(box.startX(), box.startY(), box.startZ(), box.endX(), box.endY(), box.endZ()).forEach(blockPos -> {
                ColoredLightEngine.BlockRequests blockRequests = new ColoredLightEngine.BlockRequests(blockPos);
                blockRequests.increaseRequests.add(new ColoredLightEngine.LightUpdateRequest(blockPos, Config.getColorEmission(world, blockPos), false));
                ColoredLightEngine.getBlockUpdateIncreaseRequests().add(blockRequests);
            });
        }*/
    }

    @Inject(method = "removeLightSource", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void gbw$removeDynamicColoredLightSource(EntityDynamicLightSourceBehavior lightSource, CallbackInfo ci) {
        if(!MinecraftClient.getInstance().isOnThread())
            return; // only client side

        BlockPos pos = BlockPos.ofFloored(lightSource.getDynamicLightPrevX(), lightSource.getDynamicLightPrevY(), lightSource.getDynamicLightPrevZ());
        ColorRGB4 lightColor = ColoredLightEngine.getInstance().getStorage().getEntry(pos);

        if (lightColor.isZero())
            ColoredLightEngine.getInstance().requestLightPullIn(ColoredLightEngine.getBlockUpdateDecreaseRequests(), pos);
        else ColoredLightEngine.getBlockUpdateDecreaseRequests().add(new ColoredLightEngine.LightUpdateRequest(pos, lightColor, true));
    }
}
