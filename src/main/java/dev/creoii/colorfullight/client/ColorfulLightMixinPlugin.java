package dev.creoii.colorfullight.client;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ColorfulLightMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if ("dev.creoii.colorfullight.client.mixin.client.compat.EntityDynamicLightSourceBehaviorMixin".equals(mixinClassName)) {
            return FabricLoader.getInstance().isModLoaded("lambdynlights");
        }
        else if ("dev.creoii.colorfullight.client.mixin.client.compat.LambDynLightsMixin".equals(mixinClassName)) {
            return FabricLoader.getInstance().isModLoaded("lambdynlights");
        }
        if ("dev.creoii.colorfullight.client.mixin.client.compat.FlatLightPipelineMixin".equals(mixinClassName)) {
            return FabricLoader.getInstance().isModLoaded("sodium");
        }
        else if ("dev.creoii.colorfullight.client.mixin.client.compat.SmoothLightPipelineMixin".equals(mixinClassName)) {
            return FabricLoader.getInstance().isModLoaded("sodium");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}