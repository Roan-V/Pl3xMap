package net.pl3x.map.addon.worldborder.border;

import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public class VanillaBorder extends Border {
    public VanillaBorder(@NotNull MapWorld mapWorld) {
        super(mapWorld, BorderType.VANILLA);
    }

    @Override
    public void update() {
        WorldBorder border = getMapWorld().getLevel().getWorldBorder();

        int x = (int) border.getCenterX();
        int z = (int) border.getCenterZ();
        int r = (int) border.getSize() / 2;

        this.marker = Marker.polyline(
                Point.of(x - r, z - r),
                Point.of(x + r, z - r),
                Point.of(x + r, z + r),
                Point.of(x - r, z + r),
                Point.of(x - r, z - r)
        );
    }
}