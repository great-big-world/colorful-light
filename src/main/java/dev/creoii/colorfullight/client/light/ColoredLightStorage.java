package dev.creoii.colorfullight.client.light;

import dev.creoii.colorfullight.client.util.ColorRGB4;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class ColoredLightStorage {
    private ConcurrentHashMap<Long, ColoredLightSection> map = new ConcurrentHashMap<>();

    @Nullable
    public ColorRGB4 getEntry(BlockPos blockPos) { return getEntry(blockPos.getX(), blockPos.getY(), blockPos.getZ()); }
    @Nullable
    public ColorRGB4 getEntry(int x, int y, int z) {
        long sectionPos = ChunkSectionPos.fromBlockPos(BlockPos.asLong(x, y, z));
        ColoredLightSection layer = getSection(sectionPos);
        if(layer == null) return null;
        return layer.get(
                ChunkSectionPos.getLocalCoord(x),
                ChunkSectionPos.getLocalCoord(y),
                ChunkSectionPos.getLocalCoord(z)
        );
    }

    /*public void setEntry(BlockPos blockPos, ColorRGB4 value) { setEntry(blockPos.getX(), blockPos.getY(), blockPos.getZ(), value); }
    public void setEntry(int x, int y, int z, ColorRGB4 value) {
        long sectionPos = SectionPos.blockToSection(BlockPos.asLong(x, y, z));
        map.computeIfPresent(sectionPos, (pos, layer) -> {
            layer.set(
                    SectionPos.sectionRelative(x),
                    SectionPos.sectionRelative(y),
                    SectionPos.sectionRelative(z),
                    value
            );
            return layer;
        });
    }*/
    public void setEntryUnsafe(BlockPos blockPos, ColorRGB4 value) { setEntryUnsafe(blockPos.getX(), blockPos.getY(), blockPos.getZ(), value); }
    public void setEntryUnsafe(int x, int y, int z, ColorRGB4 value) {
        long sectionPos = ChunkSectionPos.fromBlockPos(BlockPos.asLong(x, y, z));
        var layer = map.get(sectionPos);
        if(layer == null) return;
        layer.set(
                ChunkSectionPos.getLocalCoord(x),
                ChunkSectionPos.getLocalCoord(y),
                ChunkSectionPos.getLocalCoord(z),
                value
        );
    }

    public boolean containsEntry(BlockPos blockPos) { return containsEntry(blockPos.getX(), blockPos.getY(), blockPos.getZ()); }
    public boolean containsEntry(int x, int y, int z) {
        return containsSection(ChunkSectionPos.fromBlockPos(BlockPos.asLong(x, y, z)));
    }

    public boolean containsSection(long sectionPos) {
        return map.containsKey(sectionPos);
    }

    public ColoredLightSection getSection(long sectionPos) {
        return map.get(sectionPos);
    }

    public void addSection(long sectionPos) {
        map.put(sectionPos, new ColoredLightSection());
    }

    public void removeSection(long sectionPos) {
        map.remove(sectionPos);
    }

    public void clear() {
        map.clear();
    }
}