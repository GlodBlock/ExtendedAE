package com.glodblock.github.extendedae.mixins;

import appeng.api.networking.IGrid;
import appeng.helpers.patternprovider.PatternContainer;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PatternAccessTermMenu;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.network.EPPNetworkHandler;
import com.glodblock.github.extendedae.network.packet.SExPatternInfo;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.extendedae.xmod.LoadList;
import com.glodblock.github.extendedae.xmod.gregtech.MetaTileResolver;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(PatternAccessTermMenu.class)
public abstract class MixinPatternAccessTermMenu extends AEBaseMenu {

    @Final
    @Shadow(remap = false)
    private Map<PatternContainer, Object> diList;

    @Inject(
            method = "sendFullUpdate",
            at = @At("TAIL"),
            remap = false
    )
    private void sendTileInfo(IGrid grid, CallbackInfo ci) {
        if (this.getPlayer() instanceof ServerPlayer player) {
            for (var inv : diList.values()) {
                var id = Ae2Reflect.getContainerID(inv);
                var container = Ae2Reflect.getContainer(inv);
                if (container instanceof BlockEntity te) {
                    EPPNetworkHandler.INSTANCE.sendTo(new SExPatternInfo(id, te.getBlockPos(), Objects.requireNonNull(te.getLevel()).dimension()), player);
                } else if (container instanceof AEBasePart part) {
                    EPPNetworkHandler.INSTANCE.sendTo(new SExPatternInfo(id, part.getBlockEntity().getBlockPos(), Objects.requireNonNull(part.getLevel()).dimension(), part.getSide()), player);
                } else if (LoadList.GT && MetaTileResolver.check(container)) {
                    EPPNetworkHandler.INSTANCE.sendTo(new SExPatternInfo(id, MetaTileResolver.getBlockPos(container), MetaTileResolver.getLevel(container).dimension()), player);
                }
            }
        }
    }

    public MixinPatternAccessTermMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

}
