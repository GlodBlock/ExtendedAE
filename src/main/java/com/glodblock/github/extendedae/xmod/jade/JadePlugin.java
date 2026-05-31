package com.glodblock.github.extendedae.xmod.jade;

import appeng.api.integrations.igtooltip.PartTooltips;
import com.glodblock.github.extendedae.common.parts.PartSmartAnnihilationPlane;
import com.glodblock.github.extendedae.common.tileentities.TileCaner;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    private static final Identifier[] CHEMICALS = {
            Identifier.fromNamespaceAndPath("mekanism", "chemical")
    };

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(JadeDateSender.INSTANCE, BlockEntity.class);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        PartTooltips.addBody(PartSmartAnnihilationPlane.class, PlaneTooltip.INSTANCE);
        PartTooltips.addServerData(PartSmartAnnihilationPlane.class, PlaneTooltip.INSTANCE);
        registration.registerBlockComponent(WirelessConnectorTooltip.INSTANCE, Block.class);
        registration.registerBlockComponent(CrystalFixerTooltip.INSTANCE, Block.class);
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
