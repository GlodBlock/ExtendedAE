package com.github.glodblock.extendedae.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class FCClientUtil {

    public static Vec3 rotor(Vec3 point, Vec3 center, Direction.Axis axis, float a) {
        Vec3 normal = Vec3.ZERO;
        switch (axis) {
            case X -> normal = point.subtract(center).xRot(a);
            case Y -> normal = point.subtract(center).yRot(a);
            case Z -> normal = point.subtract(center).zRot(a);
        }
        return normal.add(center);
    }

    public static Vector3f rotor(Vector3f point, Vector3f center, Direction.Axis axis, float a) {
        Vector3f normal = new Vector3f(0, 0, 0);
        switch (axis) {
            case X -> normal = point.sub(center).rotateX(a);
            case Y -> normal = point.sub(center).rotateY(a);
            case Z -> normal = point.sub(center).rotateZ(a);
        }
        return normal.add(center);
    }

}
