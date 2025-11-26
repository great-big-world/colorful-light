package dev.creoii.colorfullight.client.util.compat;

import dev.creoii.colorfullight.client.light.Config;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.lambdaurora.lambdynlights.LambDynLights;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class LambDynLightsUtil {
    public static ColorRGB4 getLivingEntityLightColor(LivingEntity entity) {
        ColorRGB4 lightColor = ColorRGB4.ZERO;
        if (!FabricLoader.getInstance().isModLoaded("lambdynlights"))
            return lightColor;

        if (entity.getType() == EntityType.BLAZE) {
            lightColor = ColorRGB4.fromRGB8(255, 0, 0);
        } else if (entity.getType() == EntityType.ALLAY) {
            lightColor = ColorRGB4.fromRGB8(0, 0, 255);
        } else if (entity.getType() == EntityType.VEX) {
            lightColor = ColorRGB4.fromRGB8(63, 63, 255);
        } else if (entity.getType() == EntityType.EXPERIENCE_ORB) {
            lightColor = ColorRGB4.fromRGB8(175, 255, 0);
        } else if (entity.getType() == EntityType.GLOW_SQUID) {
            lightColor = ColorRGB4.fromRGB8(40, 255, 200);
        }

        boolean submergedInFluid = isEyeSubmergedInFluid(entity);

        for(EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
            ItemStack equipped = entity.getEquippedStack(equipmentSlot);
            if (equipped.hasEnchantments()) {
                lightColor = ColorRGB4.max(lightColor, ColorRGB4.ENCHANTED);
            } else if (equipped.getItem() instanceof BlockItem blockItem) {
                int luminance = LambDynLights.get().itemLightSourceManager().getLuminance(equipped, submergedInFluid);
                ColorRGB4 color = Config.getLightColor(blockItem.getBlock().getRegistryEntry()).mul(luminance / 16f);
                lightColor = ColorRGB4.max(lightColor, color);
            }
        }

        return lightColor;
    }

    private static boolean isEyeSubmergedInFluid(LivingEntity entity) {
        if (!LambDynLights.get().config.getWaterSensitiveCheck().get()) {
            return false;
        } else {
            BlockPos eyePos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
            return !entity.getEntityWorld().getFluidState(eyePos).isEmpty();
        }
    }
}
