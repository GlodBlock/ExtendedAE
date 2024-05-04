package com.glodblock.github.ae2netanalyser.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public final class ClientUtil {

    public static Vec3 getLawVec(Vec3 a, Vec3 b) {
        var normal = a.subtract(b);
        normal = normal.normalize();
        if (normal.equals(Vec3.ZERO)) {
            return Vec3.ZERO;
        }
        return new Vec3(normal.y - normal.z, normal.z - normal.x, normal.x - normal.y).normalize();
    }

    public static Vec3 getLawVec2(Vec3 a, Vec3 b) {
        var normal = a.subtract(b);
        normal = normal.normalize();
        if (normal.equals(Vec3.ZERO)) {
            return Vec3.ZERO;
        }
        double x = normal.x;
        double y = normal.y;
        double z = normal.z;
        return new Vec3(
                z*z-x*z+y*y-x*y,
                z*z-y*z-x*y+x*x,
                y*y+x*x-y*z-x*z
        ).normalize();
    }

    public static Vec3 getCenter(BlockPos a, BlockPos b) {
        var ac = a.getCenter();
        var bc = b.getCenter();
        return ac.add(bc).scale(0.5);
    }

}
