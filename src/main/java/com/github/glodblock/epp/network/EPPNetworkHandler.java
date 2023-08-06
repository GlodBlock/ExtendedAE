package com.github.glodblock.epp.network;

import appeng.core.sync.network.TargetPoint;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.network.packet.IMessage;
import com.github.glodblock.epp.network.packet.SExPatternInfo;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EPPNetworkHandler {

    private static final ResourceLocation channel = EPP.id("network");
    static int id = 0;

    public static final EPPNetworkHandler INSTANCE = new EPPNetworkHandler();
    private final Consumer<IMessage<?>> clientHandler;
    private final Int2ObjectMap<Supplier<IMessage<?>>> packetFactoryMap = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<Class<?>> packetIDMap = new Object2IntOpenHashMap<>();

    public EPPNetworkHandler() {
        EventNetworkChannel ec = NetworkRegistry.ChannelBuilder.named(channel)
                .networkProtocolVersion(() -> "1").clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true)
                .eventNetworkChannel();
        ec.addListener(this::clientPacket);
        ec.addListener(this::serverPacket);

        this.clientHandler = DistExecutor.unsafeRunForDist(() -> () -> EPPNetworkHandler::onClientPacketData,
                () -> () -> pkt -> {
                });
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientPacketData(IMessage<?> packet) {
        if (packet.isClient()) {
            packet.onMessage(Minecraft.getInstance().player);
        }
    }

    public void init() {
        registerPacket(SExPatternInfo.class, SExPatternInfo::new);
    }

    private void registerPacket(Class<?> clazz, Supplier<IMessage<?>> factory) {
        this.packetIDMap.put(clazz, id);
        this.packetFactoryMap.put(id, factory);
        id ++;
    }

    public void serverPacket(NetworkEvent.ClientCustomPayloadEvent ev) {
        try {
            NetworkEvent.Context ctx = ev.getSource().get();
            ctx.setPacketHandled(true);
            var bytes = ev.getPayload();
            var packet = this.packetFactoryMap.get(bytes.readVarInt()).get();
            packet.fromBytes(bytes);
            var player = ctx.getSender();
            ctx.enqueueWork(
                    () -> {
                        try {
                            packet.onMessage(player);
                        } catch (final IllegalArgumentException e) {
                            EPP.LOGGER.warn(e.getMessage());
                        }
                    });
        } catch (final RunningOnDifferentThreadException ignored) {
        }
    }

    public void clientPacket(NetworkEvent.ServerCustomPayloadEvent ev) {
        if (ev instanceof NetworkEvent.ServerCustomPayloadLoginEvent) {
            return;
        }
        if (this.clientHandler != null) {
            try {
                NetworkEvent.Context ctx = ev.getSource().get();
                ctx.setPacketHandled(true);
                var bytes = ev.getPayload();
                var packet = this.packetFactoryMap.get(bytes.readVarInt()).get();
                packet.fromBytes(bytes);
                ctx.enqueueWork(() -> this.clientHandler.accept(packet));
            } catch (RunningOnDifferentThreadException ignored) {
            }
        }
    }

    public void sendToAll(IMessage<?> message) {
        var server = EPP.INSTANCE.getServer();
        if (server != null) {
            server.getPlayerList().broadcastAll(toFMLPacket(message, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    public void sendTo(IMessage<?> message, ServerPlayer player) {
        player.connection.send(toFMLPacket(message, NetworkDirection.PLAY_TO_CLIENT));
    }

    public void sendToAllAround(IMessage<?> message, TargetPoint point) {
        var server = EPP.INSTANCE.getServer();
        if (server != null) {
            Packet<?> pkt = toFMLPacket(message, NetworkDirection.PLAY_TO_CLIENT);
            server.getPlayerList().broadcast(point.excluded, point.x, point.y, point.z, point.r2,
                    point.level.dimension(), pkt);
        }
    }

    public void sendToServer(IMessage<?> message) {
        assert Minecraft.getInstance().getConnection() != null;
        Minecraft.getInstance().getConnection().send(toFMLPacket(message, NetworkDirection.PLAY_TO_SERVER));
    }

    public Packet<?> toFMLPacket(IMessage<?> message, NetworkDirection direction) {
        var bytes = new FriendlyByteBuf(Unpooled.buffer(1024));
        var id = this.packetIDMap.getOrDefault(message.getPacketClass(), -1);
        if (id == -1) {
            EPP.LOGGER.error(String.format("Unregistered Packet: %s", message.getPacketClass()));
        }
        bytes.writeVarInt(id);
        message.toBytes(bytes);
        return direction.buildPacket(Pair.of(bytes, 0), channel).getThis();
    }

}
