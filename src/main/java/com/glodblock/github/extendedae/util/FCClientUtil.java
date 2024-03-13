package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.xmod.LoadList;
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

    public static String getModName(String inputText) {
        if (inputText.isEmpty()) return "";
        for (String mod : LoadList.MOD_NAME) {
            if (mod.startsWith(inputText)) {
                int pos = mod.indexOf(inputText);
                return mod.substring(pos + inputText.length());
            }
        }
        return "";
    }

}
