package dev.creoii.colorfullight.client.light;

import com.google.gson.JsonElement;
import dev.creoii.colorfullight.client.util.ColorRGB4;
import dev.creoii.colorfullight.client.util.JsonHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Config {
    public static final ColorRGB4 defaultColor = ColorRGB4.fromRGB4(15, 15, 15);
    private static HashMap<Identifier, ColorEmitter> colorEmitters = new HashMap<>();
    private static HashMap<Identifier, ColorFilter> colorFilters = new HashMap<>();

    public static void setColorEmitters(HashMap<Identifier, ColorEmitter> colors) {
        colorEmitters = colors;
    }

    public static void setColorFilters(HashMap<Identifier, ColorFilter> filters) {
        colorFilters = filters;
    }

    public static ColorRGB4 getColorEmission(@NotNull World level, BlockPos pos) { return getColorEmission(level, level.getBlockState(pos)); }
    public static ColorRGB4 getColorEmission(@NotNull World level, @NotNull BlockState blockState) {
        float lightEmission = blockState.getLuminance()/15.0f;

        RegistryEntry<Block> blockResourceKey = blockState.getRegistryEntry();

        if(blockResourceKey != null) {
            ColorEmitter config = colorEmitters.get(blockResourceKey.getKey().get().getValue());
            if(config != null)
                return config.color().mul(config.overriddenBrightness4 < 0 ? lightEmission : config.overriddenBrightness4 /15.0f);
        }
        return defaultColor.mul(lightEmission);
    }
    public static ColorRGB4 getLightColor(BlockState blockState) {
        return getLightColor(blockState.getRegistryEntry());
    }
    public static ColorRGB4 getLightColor(@Nullable RegistryEntry<Block> blockLocation) {
        if(blockLocation != null) {
            ColorEmitter config = colorEmitters.get(blockLocation.getKey().get().getValue());
            if(config != null) {
                return config.color();
            }
        }
        return defaultColor;
    }

    public static ColorRGB4 getColoredLightTransmittance(@NotNull World level, BlockPos pos, ColorRGB4 defaultValue) {
        var blockState = level.getBlockState(pos);
        return blockState == null ? defaultValue : getColoredLightTransmittance(level, blockState);
    }
    public static ColorRGB4 getColoredLightTransmittance(World level, @NotNull BlockState blockState) {
        RegistryEntry<Block> blockResourceKey = blockState.getRegistryEntry();
        if(blockResourceKey == null) return ColorRGB4.fromRGB4(15, 15, 15);
        ColorFilter config = colorFilters.get(blockResourceKey.getKey().get().getValue());
        if(config == null) return ColorRGB4.fromRGB4(15, 15, 15);
        return config.transmittance;
    }

    public static int getEmissionBrightness(@NotNull World level, BlockPos pos, int defaultValue) {
        var blockState = level.getBlockState(pos);
        return blockState == null ? defaultValue : getEmissionBrightness(level, pos, blockState);
    }
    public static int getEmissionBrightness(@NotNull World level, BlockPos pos, @NotNull BlockState blockState) {
        RegistryEntry<Block> blockResourceKey = blockState.getRegistryEntry();

        if(blockResourceKey != null) {
            ColorEmitter config = colorEmitters.get(blockResourceKey.getKey().get().getValue());
            if(config != null && config.overriddenBrightness4 >= 0)
                return config.overriddenBrightness4;
        }
        return blockState.getLuminance();
    }
    public static int getEmissionBrightness(BlockState blockState) {
        RegistryEntry<Block> blockResourceKey = blockState.getRegistryEntry();
        if(blockResourceKey != null) {
            ColorEmitter config = colorEmitters.get(blockResourceKey.getKey().get().getValue());
            if(config != null && config.overriddenBrightness4 >= 0)
                return config.overriddenBrightness4;
        }
        return blockState.getLuminance();
    }

    /**
     * @param color light color
     * @param overriddenBrightness4 4 bit value in range 0..15, by which light color is multiplied, if -1, vanilla emission for given block is used
     */
    public record ColorEmitter(ColorRGB4 color, int overriddenBrightness4) {
        public static ColorEmitter fromJsonElement(JsonElement value) throws IllegalArgumentException {
            ColorRGB4 color = getColorFromJsonElement(value);
            Integer brightness = getBrightnessFromJsonElement(value);
            if(color == null) throw new IllegalArgumentException("Invalid color.");
            if(brightness == null) throw new IllegalArgumentException("Invalid brightness.");
            return new ColorEmitter(color, brightness);
        }

        private static ColorRGB4 getColorFromJsonElement(JsonElement value) {
            if(value.isJsonArray()) {
                var array = value.getAsJsonArray();
                if(array.size() < 3) return null;
                return JsonHelper.getColor4FromJsonElements(array.get(0), array.get(1), array.get(2));
            }
            return JsonHelper.getColor4FromString(value.getAsString().split(";")[0]);
        }

        private static Integer getBrightnessFromJsonElement(JsonElement value) {
            if(value.isJsonArray()) {
                var array = value.getAsJsonArray();
                if(array.size() < 4) return -1;
                return JsonHelper.getInt4FromJsonElement(array.get(3));
            }
            String[] args = value.getAsString().split(";");
            if(args.length < 2) return -1;
            try {
                int brightness = Integer.parseInt(args[1], 16);
                if(brightness >= 0 && brightness <= 15) return brightness;
                return null;
            }
            catch (NumberFormatException ignore) {
                return null;
            }
        }
    }
    public record ColorFilter(ColorRGB4 transmittance) {
        public static ColorFilter fromJsonElement(JsonElement value) throws IllegalArgumentException {
            ColorRGB4 color = getColorFromJsonElement(value);
            if(color == null) throw new IllegalArgumentException("Invalid color.");
            return new ColorFilter(color);
        }

        private static ColorRGB4 getColorFromJsonElement(JsonElement value) {
            if(value.isJsonArray()) {
                var array = value.getAsJsonArray();
                if(array.size() < 3) return null;
                return JsonHelper.getColor4FromJsonElements(array.get(0), array.get(1), array.get(2));
            }
            return JsonHelper.getColor4FromString(value.getAsString());
        }
    }
}