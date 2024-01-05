package com.glodblock.github.extendedae.util;

import com.glodblock.github.glodium.util.GlodClientUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FCClientUtil {

    public static AABB rotor(AABB box, Vec3 center, Direction.Axis axis, float a) {
        return new AABB(
                GlodClientUtil.rotor(new Vec3(box.minX, box.minY, box.minZ), center, axis, a),
                GlodClientUtil.rotor(new Vec3(box.maxX, box.maxY, box.maxZ), center, axis, a)
        );
    }

}
