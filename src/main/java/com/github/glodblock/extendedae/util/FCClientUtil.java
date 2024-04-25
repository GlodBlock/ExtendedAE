package com.github.glodblock.extendedae.util;

import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FCClientUtil {

    private static final Set<String> MOD_NAME = FabricLoaderImpl.INSTANCE.getAllMods().stream().flatMap(x -> Stream.of(x.getMetadata().getId(), x.getMetadata().getName())).collect(Collectors.toSet());

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

    public static String getModName(String inputText) {
        if (inputText.isEmpty()) return "";
        for (String mod : MOD_NAME) {
            if (mod.startsWith(inputText)) {
                int pos = mod.indexOf(inputText);
                return mod.substring(pos + inputText.length());
            }
        }
        return "";
    }

}
