package com.github.glodblock.extendedae.network;

import appeng.core.sync.BasePacket;
import appeng.core.sync.network.TargetPoint;
import com.github.glodblock.extendedae.EAE;
import com.github.glodblock.extendedae.EAEServer;
import com.github.glodblock.extendedae.network.packet.CGenericPacket;
import com.github.glodblock.extendedae.network.packet.CPatternKey;
import com.github.glodblock.extendedae.network.packet.CUpdatePage;
import com.github.glodblock.extendedae.network.packet.IMessage;
import com.github.glodblock.extendedae.network.packet.SAssemblerAnimation;
import com.github.glodblock.extendedae.network.packet.SExPatternInfo;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class EPPNetworkHandler {

    private static final ResourceLocation channel = EAE.id("network");
    static int id = 0;

    public static final EPPNetworkHandler INSTANCE = new EPPNetworkHandler();
    private final Int2ObjectMap<Supplier<IMessage<?>>> packetFactoryMap = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<Class<?>> packetIDMap = new Object2IntOpenHashMap<>();

    public EPPNetworkHandler() {
        ServerPlayNetworking.registerGlobalReceiver(channel, this::serverPacket);
        ClientPlayNetworking.registerGlobalReceiver(channel, this::clientPacket);
    }

    public void init() {
        registerPacket(SExPatternInfo.class, SExPatternInfo::new);
        registerPacket(SAssemblerAnimation.class, SAssemblerAnimation::new);
        registerPacket(CPatternKey.class, CPatternKey::new);
        registerPacket(CUpdatePage.class, CUpdatePage::new);
        registerPacket(CGenericPacket.class, CGenericPacket::new);
    }

    private void registerPacket(Class<?> clazz, Supplier<IMessage<?>> factory) {
        this.packetIDMap.put(clazz, id);
        this.packetFactoryMap.put(id, factory);
        id ++;
    }

    public void serverPacket(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf payload, PacketSender responseSender) {
        try {
            var packet = this.packetFactoryMap.get(payload.readVarInt()).get();
            if (!packet.isClient()) {
                packet.fromBytes(payload);
                server.execute(() -> packet.onMessage(player));
            }
        } catch (final RunningOnDifferentThreadException ignored) {
        }
    }

    public void clientPacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf payload, PacketSender responseSender) {
        try {
            var packet = this.packetFactoryMap.get(payload.readVarInt()).get();
            if (packet.isClient()) {
                packet.fromBytes(payload);
                client.execute(() -> {
                    try {
                        packet.onMessage(client.player);
                    } catch (IllegalArgumentException e) {
                        EAE.LOGGER.warn(e.getMessage());
                    }
                });
            }
        } catch (RunningOnDifferentThreadException ignored) {
        }
    }

    public void sendToAll(IMessage<?> message) {
        var server = EAEServer.getServer();
        if (server != null) {
            PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, BasePacket.CHANNEL, toBytes(message)));
        }
    }

    public void sendTo(IMessage<?> message, ServerPlayer player) {
        ServerPlayNetworking.send(player, channel, toBytes(message));
    }

    public void sendToAllAround(IMessage<?> message, TargetPoint point) {
        PlayerLookup.around((ServerLevel) point.level, new Vec3(point.x, point.y, point.z), point.radius)
                .forEach(player -> {
                    if (player != point.excluded) {
                        ServerPlayNetworking.send(player, channel, toBytes(message));
                    }
                });
    }

    public void sendToServer(IMessage<?> message) {
        ClientPlayNetworking.send(channel, toBytes(message));
    }

    public FriendlyByteBuf toBytes(IMessage<?> message) {
        var bytes = new FriendlyByteBuf(Unpooled.buffer(1024));
        var id = this.packetIDMap.getOrDefault(message.getPacketClass(), -1);
        if (id == -1) {
            EAE.LOGGER.error(String.format("Unregistered Packet: %s", message.getPacketClass()));
        }
        bytes.writeVarInt(id);
        message.toBytes(bytes);
        return bytes;
    }

}
