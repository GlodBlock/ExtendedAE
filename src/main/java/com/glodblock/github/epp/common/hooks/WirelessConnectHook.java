package com.glodblock.github.epp.common.hooks;

import appeng.util.InteractionUtil;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.tiles.TileWirelessConnector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class WirelessConnectHook {

    public static ActionResult onPlayerUseBlock(PlayerEntity player, World level, Hand hand, BlockHitResult hitResult) {
        if (!player.isSpectator() && hand == Hand.MAIN_HAND) {
            var stack = player.getStackInHand(hand);
            if (!InteractionUtil.isInAlternateUseMode(player) && isValidTool(stack)) {
                var te = level.getBlockEntity(hitResult.getBlockPos());
                if (te instanceof TileWirelessConnector connector) {
                    var nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
                    var pos = te.getPos();
                    assert nbt != null;
                    if (nbt.getLong("freq") != 0) {
                        connector.setFreq(nbt.getLong("freq"));
                        nbt.putLong("freq", 0);
                        nbt.putLong("bind", 0);
                        stack.setNbt(nbt);
                        player.sendMessage(Text.translatable("chat.wireless.connect", pos.getX(), pos.getY(), pos.getZ()), true);
                    } else {
                        var f = TileWirelessConnector.G.genFreq();
                        connector.setFreq(f);
                        nbt.putLong("freq", f);
                        nbt.putLong("bind", connector.getPos().asLong());
                        stack.setNbt(nbt);
                        player.sendMessage(Text.translatable("chat.wireless.bind", pos.getX(), pos.getY(), pos.getZ()), true);
                    }
                    return ActionResult.success(level.isClient);
                }
            }
        }
        return ActionResult.PASS;
    }

    private static boolean isValidTool(ItemStack stack) {
        return stack.getItem() == EPPItemAndBlock.WIRELESS_TOOL;
    }

}
