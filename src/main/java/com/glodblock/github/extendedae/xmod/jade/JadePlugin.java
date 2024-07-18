package com.glodblock.github.extendedae.xmod.jade;

import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final ResourceLocation[] CHEMICALS = {
            ResourceLocation.fromNamespaceAndPath("mekanism", "chemical")
    };

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(JadeDateSender.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(WirelessConnectorTooltip.INSTANCE, Block.class);
        registration.registerBlockComponent(CrystalGrowthChamberTooltip.INSTANCE, Block.class);
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();
            for (var loc : CHEMICALS) {
                if (target instanceof TileIngredientBuffer ||
                    target instanceof TileCaner ||
                    target instanceof TileCrystalAssembler) {
                    tooltip.getTooltip().remove(loc);
                }
            }
        });

    }

}
