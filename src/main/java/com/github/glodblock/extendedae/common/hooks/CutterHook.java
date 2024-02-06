package com.github.glodblock.extendedae.common.hooks;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.items.tools.quartz.QuartzCuttingKnifeItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.util.InteractionUtil;
import com.github.glodblock.extendedae.container.ContainerRenamer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class CutterHook {

    public static final CutterHook INSTANCE = new CutterHook();

    private CutterHook() {
        // NO-OP
    }

    public InteractionResult onPlayerUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isSpectator() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        var itemStack = player.getItemInHand(hand);
        if (!InteractionUtil.isInAlternateUseMode(player) && itemStack.getItem() instanceof QuartzCuttingKnifeItem) {
            var pos = hitResult.getBlockPos();
            var tile = level.getBlockEntity(pos);
            if (tile instanceof AEBaseBlockEntity) {
                if (tile instanceof CableBusBlockEntity cable) {
                    var hitVec = hitResult.getLocation();
                    Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                    var part = cable.selectPartLocal(hitInBlock).part;
                    if (part instanceof AEBasePart p) {
                        if (!level.isClientSide) {
                            MenuOpener.open(ContainerRenamer.TYPE, player, MenuLocators.forPart(p));
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                } else {
                    if (!level.isClientSide) {
                        MenuOpener.open(ContainerRenamer.TYPE, player, MenuLocators.forBlockEntity(tile));
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return InteractionResult.PASS;
    }

}
