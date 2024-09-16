package com.glodblock.github.extendedae.util;

import com.glodblock.github.extendedae.xmod.LoadList;
import com.glodblock.github.glodium.util.GlodClientUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FCClientUtil {

    public static AABB rotor(AABB box, Vec3 center, Direction.Axis axis, float a) {
        return new AABB(
                GlodClientUtil.rotor(new Vec3(box.minX, box.minY, box.minZ), center, axis, a),
                GlodClientUtil.rotor(new Vec3(box.maxX, box.maxY, box.maxZ), center, axis, a)
        );
    }

    public static String getModName(String inputText) {
        String trimInput = inputText.trim();
        if (trimInput.isEmpty() || trimInput.endsWith(",")) {
            return "";
        }
        String[] ids = FCUtil.trimSplit(inputText);
        Set<String> uniqueInputs = Arrays.stream(ids).collect(Collectors.toSet());
        for (String mod : LoadList.MOD_NAME) {
            if (uniqueInputs.contains(mod)) {
                continue;
            }
            String modid = ids[ids.length - 1];
            if (mod.startsWith(modid)) {
                int pos = mod.indexOf(modid);
                return mod.substring(pos + modid.length());
            }
        }
        return "";
    }

}
