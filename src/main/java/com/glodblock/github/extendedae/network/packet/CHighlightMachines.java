package com.glodblock.github.extendedae.network.packet;

import appeng.api.parts.IPart;
import appeng.api.stacks.AEItemKey;
import appeng.menu.me.networktool.NetworkStatusMenu;
import appeng.parts.AEBasePart;
import appeng.parts.BusCollisionHelper;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.client.render.highlight.HighlightHandler;
import com.glodblock.github.glodium.network.packet.IMessage;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class CHighlightMachines implements IMessage {

    private AEItemKey key;
    private boolean missingChannel;

    public CHighlightMachines() {
        // NO-OP
    }

    public CHighlightMachines(AEItemKey key, boolean missingChannel) {
        this.key = key;
        this.missingChannel = missingChannel;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        this.key.writeToPacket(buf);
        buf.writeBoolean(this.missingChannel);
    }

    @Override
    public void fromBytes(RegistryFriendlyByteBuf buf) {
        this.key = AEItemKey.fromPacket(buf);
        this.missingChannel = buf.readBoolean();
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof NetworkStatusMenu networkMenu && player instanceof ServerPlayer serverPlayer) {
            var grid = Ae2Reflect.getNetworkGrid(networkMenu);
            List<HighlightHandler.HighlightData> data = new ArrayList<>();
            if (grid != null) {
                for (var type : grid.getMachineClasses()) {
                    for (var node : grid.getMachineNodes(type)) {
                        if (node.meetsChannelRequirements() != this.missingChannel) {
                            if (this.key.equals(node.getVisualRepresentation())) {
                                var machine = node.getOwner();
                                if (machine instanceof BlockEntity tile) {
                                    if (tile.getLevel() != null) {
                                        data.add(new HighlightHandler.HighlightData(
                                                tile.getBlockPos(),
                                                null, 0,
                                                tile.getLevel().dimension(),
                                                new AABB(tile.getBlockPos()),
                                                null, null));
                                    }
                                } else if (machine instanceof AEBasePart part) {
                                    var host = part.getHost().getBlockEntity();
                                    if (host.getLevel() != null) {
                                        data.add(new HighlightHandler.HighlightData(
                                                host.getBlockPos(),
                                                part.getSide(), 0,
                                                host.getLevel().dimension(),
                                                getPartBox(part, part.getSide(), new AABB(host.getBlockPos())),
                                                null, null
                                        ));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!data.isEmpty()) {
                EAENetworkHandler.INSTANCE.sendTo(new SHighlightRequest(data), serverPlayer);
                player.sendSystemMessage(Component.translatable("chat.network_status.highlight", data.size(), this.key.getDisplayName()));
            }
        }
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public ResourceLocation id() {
        return ExtendedAE.id("highlight_machines");
    }

    public static AABB getPartBox(IPart part, Direction side, AABB defaultBox) {
        List<AABB> boxes = new ArrayList<>();
        var helper = new BusCollisionHelper(boxes, side, false);
        part.getBoxes(helper);
        if (boxes.isEmpty()) {
            return defaultBox;
        }
        var box = boxes.getFirst();
        for (var b : boxes) {
            box = box.minmax(b);
        }
        return box.move(defaultBox.getMinPosition());
    }

}
