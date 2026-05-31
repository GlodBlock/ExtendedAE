package com.glodblock.github.extendedae.container;

import appeng.me.helpers.IGridConnectedBlockEntity;
import appeng.menu.guisync.GuiSync;
import appeng.menu.guisync.PacketWritable;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.me.wireless.WirelessStatus;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ContainerWirelessHub extends UpgradeableMenu<TileWirelessHub> implements IActionHolder {

    public static final MenuType<@NotNull ContainerWirelessHub> TYPE = MenuTypeBuilder
            .create(ContainerWirelessHub::new, TileWirelessHub.class)
            .buildUnregistered(ExtendedAE.id("wireless_hub"));

    @GuiSync(7)
    public double powerUse;
    @GuiSync(8)
    public int usedChannel;
    @GuiSync(9)
    public int maxChannel;
    @GuiSync(10)
    public PortStatus status = new PortStatus();
    private final ActionMap actions = ActionMap.create();

    public ContainerWirelessHub(int id, Inventory ip, TileWirelessHub host) {
        super(TYPE, id, ip, host);
        this.actions.put("disconnect", o -> this.disconnect(o.getInt()));
    }

    private void disconnect(int port) {
        this.getHost().killPort(port);
    }

    public WirelessStatus getStatus(int port) {
        return this.status.getStatus(port).status;
    }

    public BlockPos getRemotePosition(int port) {
        return BlockPos.of(this.status.getStatus(port).pos);
    }

    public int getRemoteChannel(int port) {
        return this.status.getStatus(port).channel;
    }

    @Override
    public void broadcastChanges() {
        this.powerUse = this.getHost().getPowerUse();
        var node = this.getHost().getMainNode().getNode();
        if (node != null) {
            this.usedChannel = node.getUsedChannels();
            this.maxChannel = node.getMaxChannels();
        } else {
            this.usedChannel = 0;
            this.maxChannel = 0;
        }
        var hostStatus = new PortStatus();
        for (int i = 0; i < TileWirelessHub.MAX_PORT; i++) {
            var otherSide = this.getHost().getOtherSide(i);
            var localStatus = this.status.getStatus(i).status;
            var localChannel = this.status.getStatus(i).channel;
            var localPos = this.status.getStatus(i).pos;
            if (otherSide == null) {
                localPos = 0;
                localChannel = 0;
                localStatus = this.getHost().getFrequency(i) == 0 ? WirelessStatus.UNCONNECTED : WirelessStatus.REMOTE_ERROR;
            } else {
                var remote = this.getHost().getLevel().getBlockEntity(otherSide);
                if (remote instanceof IGridConnectedBlockEntity gridHost) {
                    var remoteNode = gridHost.getMainNode().getNode();
                    if (remoteNode != null) {
                        localChannel = remoteNode.getUsedChannels();
                    }
                }
                localPos = otherSide.asLong();

                localStatus = WirelessStatus.WORKING;
            }
            if (!this.getHost().getMainNode().isPowered() && localStatus == WirelessStatus.WORKING) {
                localStatus = WirelessStatus.NO_POWER;
            }
            hostStatus.setStatus(i, new PortStatus.Status(localPos, localChannel, localStatus));
        }
        this.status = hostStatus;
        super.broadcastChanges();
    }

    @Override
    public @NotNull ActionMap getActionMap() {
        return this.actions;
    }

    @SuppressWarnings("unused")
    public record PortStatus(Status[] statuses) implements PacketWritable {

        public PortStatus() {
            this(new Status[TileWirelessHub.MAX_PORT]);
            Arrays.fill(this.statuses, new Status(0, 0, WirelessStatus.UNCONNECTED));
        }

        public PortStatus(RegistryFriendlyByteBuf data) {
            this(new Status[TileWirelessHub.MAX_PORT]);
            for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
                this.statuses[i] = Status.readFromPacket(data);
            }
        }

        private Status getStatus(int port) {
            return this.statuses[port];
        }

        private void setStatus(int port, Status status) {
            this.statuses[port] = status;
        }

        @Override
        public void writeToPacket(RegistryFriendlyByteBuf data) {
            for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
                this.statuses[i].writeToPacket(data);
            }
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.statuses);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof PortStatus(Status[] that)) {
                return Arrays.equals(this.statuses, that);
            }
            return false;
        }

        private record Status(long pos, int channel, WirelessStatus status) implements PacketWritable {

            static Status readFromPacket(RegistryFriendlyByteBuf data) {
                return new Status(data.readLong(), data.readInt(), data.readEnum(WirelessStatus.class));
            }

            @Override
            public void writeToPacket(RegistryFriendlyByteBuf data) {
                data.writeLong(this.pos);
                data.writeInt(this.channel);
                data.writeEnum(this.status);
            }

        }

    }

}
