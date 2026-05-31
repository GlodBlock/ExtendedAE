package com.glodblock.github.extendedae.common.hooks;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.items.tools.quartz.QuartzCuttingKnifeItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.container.ContainerRenamer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public final class CutterHook {

    public static final CutterHook INSTANCE = new CutterHook();

    private CutterHook() {
        // NO-OP
    }

    public static void addTooltip() {
        NeoForge.EVENT_BUS.addListener((ItemTooltipEvent evt) -> hookTooltip(evt.getItemStack(), evt.getToolTip()));
    }

    private static void hookTooltip(ItemStack stack, List<Component> tooltip) {
        if (stack.getItem() instanceof QuartzCuttingKnifeItem) {
            tooltip.add(Component.translatable("cutter.tooltip").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @SubscribeEvent
    public void onPlayerUseBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }
        var result = onPlayerUseBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
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
                        if (!level.isClientSide()) {
                            MenuOpener.open(ContainerRenamer.TYPE, player, MenuLocators.forPart(p));
                        }
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    if (!level.isClientSide()) {
                        MenuOpener.open(ContainerRenamer.TYPE, player, MenuLocators.forBlockEntity(tile));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

}
