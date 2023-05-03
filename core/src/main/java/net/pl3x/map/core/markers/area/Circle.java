package net.pl3x.map.core.markers.area;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.pl3x.map.core.util.Mathf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Circle implements Area {
    private final int centerX;
    private final int centerZ;
    private final int radius;

    public Circle(int centerX, int centerZ, int radius) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be positive, but was " + radius);
        }
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
    }

    public int getCenterX() {
        return this.centerX;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    public int getRadius() {
        return this.radius;
    }

    @Override
    public boolean containsBlock(int blockX, int blockZ) {
        return Mathf.distanceSquared(blockX, blockZ, getCenterX(), getCenterZ()) <= Mathf.square(getRadius());
    }

    @Override
    public boolean containsChunk(int chunkX, int chunkZ) {
        return containsBlock(offset(chunkX << 4, getCenterX(), 0x1F), offset(chunkZ << 4, getCenterZ(), 0x1F));
    }

    @Override
    public boolean containsRegion(int regionX, int regionZ) {
        return containsBlock(offset(regionX << 9, getCenterX(), 0x1FF), offset(regionZ << 9, getCenterZ(), 0x1FF));
    }

    private int offset(int a, int b, int c) {
        return a < b ? a + Math.min(c, b - a) : a;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("center-x", getCenterX());
        map.put("center-z", getCenterZ());
        map.put("radius", getRadius());
        return map;
    }

    public static @NonNull Circle deserialize(Map<String, Object> map) {
        return new Circle(
                (int) map.get("center-x"),
                (int) map.get("center-z"),
                (int) map.get("radius")
        );
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Circle other = (Circle) o;
        return getCenterX() == other.getCenterX() &&
                getCenterZ() == other.getCenterZ() &&
                getRadius() == other.getRadius();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCenterX(), getCenterZ(), getRadius());
    }

    @Override
    public @NonNull String toString() {
        return "Circle{"
                + "centerX=" + getCenterX()
                + ",centerZ=" + getCenterZ()
                + ",radius=" + getRadius()
                + "}";
    }
}