package com.glodblock.github.epp.common.hooks;

import appeng.util.InteractionUtil;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.common.tiles.TileWirelessConnector;
import com.glodblock.github.epp.config.EPPConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

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
                        var otherPos = BlockPos.fromLong(nbt.getLong("bind"));
                        if (!checkPos(otherPos, te.getPos())) {
                            player.sendMessage(Text.translatable("chat.wireless.error.01").formatted(Formatting.RED), true);
                            return ActionResult.FAIL;
                        }
                        if (!checkDis(otherPos, te.getPos())) {
                            player.sendMessage(Text.translatable("chat.wireless.error.02").formatted(Formatting.RED), true);
                            return ActionResult.FAIL;
                        }
                        if (!checkDim(nbt.getString("world"), te.getWorld().getRegistryKey().toString())) {
                            player.sendMessage(Text.translatable("chat.wireless.error.03").formatted(Formatting.RED), true);
                            return ActionResult.FAIL;
                        }
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
                        nbt.putString("world", connector.getWorld().getRegistryKey().toString());
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

    private static boolean checkPos(BlockPos here, BlockPos other) {
        return !here.equals(other);
    }

    private static boolean checkDis(BlockPos here, BlockPos other) {
        return Math.sqrt(here.getSquaredDistance(other)) <= EPPConfig.INSTANCE.wirelessConnectorMaxRange;
    }

    private static boolean checkDim(String here, String other) {
        return Objects.equals(here, other);
    }

}
