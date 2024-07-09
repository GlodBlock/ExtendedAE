package com.glodblock.github.extendedae.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.BooleanSupplier;

public class ConfigCondition implements ICondition {

    private final static Object2ReferenceMap<String, BooleanSupplier> CONFIG_MAP = new Object2ReferenceOpenHashMap<>();
    public final static MapCodec<ConfigCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    Codec.STRING.fieldOf("config_id").forGetter(ir -> ir.id)
            ).apply(builder, ConfigCondition::new)
    );

    private final String id;

    static {
        CONFIG_MAP.put(IDs.ASSEMBLER_CIRCUIT, () -> EAEConfig.allowAssemblerCircuits);
    }

    public ConfigCondition(String configID) {
        if (!CONFIG_MAP.containsKey(configID)) {
            throw new IllegalArgumentException("Unregistered ID: " + configID);
        }
        this.id = configID;
    }

    @Override
    public boolean test(@NotNull IContext context) {
        return CONFIG_MAP.get(this.id).getAsBoolean();
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    public static class IDs {

        public static final String ASSEMBLER_CIRCUIT = "assembler_circuit";

    }

}
