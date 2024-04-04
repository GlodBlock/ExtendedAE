package com.glodblock.github.appflux.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.capabilities.Capabilities;
import appeng.parts.reporting.StorageMonitorPart;
import appeng.util.Platform;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;


import java.util.Optional;

public class PartFEStorageMonitor extends StorageMonitorPart {
    public PartFEStorageMonitor(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (isClientSide()) {
            return true;
        }

        if (!this.getMainNode().isActive()) {
            return false;
        }

        if (!Platform.hasPermissions(this.getHost().getLocation(), player)) {
            return false;
        }

        if (!isLocked()) {
            var eq = player.getItemInHand(hand);
            if (!AEItemKey.matches(this.getDisplayed(), eq)) {
                var cap = eq.getCapability(Capabilities.FORGE_ENERGY);
                if (!cap.resolve().equals(Optional.empty())) {
                    this.setConfiguredItem(FluxKey.of(EnergyType.FE));
                } else {
                    this.setConfiguredItem(null);
                }
            }
            this.configureWatchers();
            this.getHost().markForSave();
            this.getHost().markForUpdate();
        } else {
            return super.onPartActivate(player, hand, pos);
        }

        return true;
    }
}
