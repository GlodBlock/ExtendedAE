package com.glodblock.github.extendedae.xmod.jade;

import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class CrystalFixerTooltip implements IBlockComponentProvider {
    static final CrystalFixerTooltip INSTANCE = new CrystalFixerTooltip();

    @Override
    public void appendTooltip(@NotNull ITooltip tooltip, BlockAccessor accessor, @NotNull IPluginConfig iPluginConfig) {
        var target = accessor.getServerData();
        if (target.contains(ExtendedAE.MODID)) {
            target.getCompound(ExtendedAE.MODID).flatMap(data -> data.getCompound("crystal_fixer")).ifPresent(data -> {
                var progress = data.getIntOr("progress", 0);
                tooltip.add(Component.translatable("jade.crystal_chamber.progress", progress));
            });
        }
    }

    @Override
    public @NotNull Identifier getUid() {
        return ExtendedAE.id("jade_chamber");
    }

}
