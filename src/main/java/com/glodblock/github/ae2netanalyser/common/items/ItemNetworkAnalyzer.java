package com.glodblock.github.ae2netanalyser.common.items;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.me.InWorldGridNode;
import appeng.me.helpers.IGridConnectedBlockEntity;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.ae2netanalyser.common.AEAComponents;
import com.glodblock.github.ae2netanalyser.common.AEAItems;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.common.me.AnalyserMode;
import com.glodblock.github.ae2netanalyser.common.me.NetworkData;
import com.glodblock.github.ae2netanalyser.common.me.netdata.FlagType;
import com.glodblock.github.ae2netanalyser.common.me.netdata.LinkFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.NodeFlag;
import com.glodblock.github.ae2netanalyser.common.me.netdata.State;
import com.glodblock.github.ae2netanalyser.common.me.tracker.PlayerTracker;
import com.glodblock.github.ae2netanalyser.container.ContainerAnalyser;
import com.glodblock.github.ae2netanalyser.network.AEANetworkHandler;
import com.glodblock.github.ae2netanalyser.network.packets.SNetworkDataUpdate;
import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ItemNetworkAnalyzer extends Item implements IMenuItem {

    public static final Reference2ObjectMap<Enum<?>, ColorData> defaultColors = new Reference2ObjectOpenHashMap<>();
    public static final AnalyserConfig defaultConfig;

    // default colors
    static {
        defaultColors.put(NodeFlag.NORMAL, new ColorData(0.8f, 0f, 0f, 1f));
        defaultColors.put(NodeFlag.DENSE, new ColorData(0.8f, 1f, 1f, 0f));
        defaultColors.put(NodeFlag.MISSING, new ColorData(0.8f, 1f, 0f, 0f));
        defaultColors.put(LinkFlag.NORMAL, new ColorData(0.8f, 0f, 0f, 1f));
        defaultColors.put(LinkFlag.DENSE, new ColorData(0.8f, 1f, 1f, 0f));
        defaultColors.put(LinkFlag.COMPRESSED, new ColorData(0.8f, 1f, 0f, 1f));
        defaultConfig = new AnalyserConfig(AnalyserMode.FULL, 0.4f, defaultColors);
    }

    public ItemNetworkAnalyzer() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
        if (!level.isClientSide() && !p.isShiftKeyDown()) {
            MenuOpener.open(ContainerAnalyser.TYPE, p, MenuLocators.forHand(p, hand));
        }
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), p.getItemInHand(hand));
    }

    @Override
    public @NotNull InteractionResult useOn(@Nonnull UseOnContext context) {
        if (!context.getLevel().isClientSide && context.getPlayer() instanceof ServerPlayer player) {
            var tool = player.getMainHandItem();
            if (tool.getItem() == AEAItems.ANALYSER) {
                tool.set(AEAComponents.GLOBAL_POS, GlobalPos.of(context.getLevel().dimension(), context.getClickedPos()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity instanceof ServerPlayer player) {
            if (stack == player.getMainHandItem() && player.getMainHandItem().getItem() == AEAItems.ANALYSER) {
                var pos = stack.get(AEAComponents.GLOBAL_POS);
                if (pos != null && pos.dimension().equals(world.dimension()) && PlayerTracker.needUpdate(player, pos)) {
                    var host = GridHelper.getNodeHost(world, pos.pos());
                    if (host != null) {
                        var node = host.getGridNode(null);
                        if (node == null && host instanceof IGridConnectedBlockEntity gct) {
                            node = gct.getGridNode();
                        }
                        if (node != null) {
                            var connections = new ObjectOpenHashSet<IGridConnection>();
                            var nodes = extractNodes(node.getGrid(), connections, world);
                            var links = new ObjectOpenHashSet<NetworkData.ALink>();
                            for (var c : connections) {
                                var a = wrapGridNode(c.a());
                                var b = wrapGridNode(c.b());
                                if (a != null && b != null && !Objects.equals(a, b)) {
                                    var state = new State<>(LinkFlag.NORMAL);
                                    if (c.a().hasFlag(GridFlags.DENSE_CAPACITY) && c.b().hasFlag(GridFlags.DENSE_CAPACITY)) {
                                        state.set(LinkFlag.DENSE);
                                    }
                                    if (c.a().hasFlag(GridFlags.CANNOT_CARRY_COMPRESSED) && c.b().hasFlag(GridFlags.CANNOT_CARRY_COMPRESSED)) {
                                        state.set(LinkFlag.COMPRESSED);
                                    }
                                    links.add(new NetworkData.ALink(a, b, (short) c.getUsedChannels(), state));
                                }
                            }
                            AEANetworkHandler.INSTANCE.sendTo(new SNetworkDataUpdate(new NetworkData(nodes.toArray(new NetworkData.ANode[0]), links.toArray(new NetworkData.ALink[0]))), player);
                        }
                    }
                }
            }
        }
    }

    private Set<NetworkData.ANode> extractNodes(IGrid grid, Set<IGridConnection> connections, Level world) {
        if (grid != null) {
            var nodes = new ObjectOpenHashSet<NetworkData.ANode>();
            for (var node : grid.getNodes()) {
                var dim = node.getLevel().dimension();
                if (dim.equals(world.dimension())) {
                    var a = wrapGridNode(node);
                    if (a != null) {
                        connections.addAll(node.getConnections());
                        nodes.add(wrapGridNode(node));
                    }
                }
            }
            return nodes;
        }
        return Set.of();
    }

    @Nullable
    private NetworkData.ANode wrapGridNode(IGridNode node) {
        if (node instanceof InWorldGridNode worldNode) {
            var pos = worldNode.getLocation();
            var state = new State<>(NodeFlag.NORMAL);
            if (!checkChannel(worldNode)) {
                state.set(NodeFlag.MISSING);
            }
            if (node.hasFlag(GridFlags.DENSE_CAPACITY)) {
                state.set(NodeFlag.DENSE);
            }
            return new NetworkData.ANode(pos, state);
        }
        return null;
    }

    private boolean checkChannel(@NotNull InWorldGridNode node) {
        var world = node.getLevel();
        var pos = node.getLocation();
        var tile = world.getBlockEntity(pos);
        if (tile instanceof CableBusBlockEntity cable) {
            for (var face : Direction.values()) {
                var part = cable.getPart(face);
                if (part instanceof IActionHost host) {
                    if (host.getActionableNode() != null && !host.getActionableNode().isOnline()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return node.isOnline();
    }

    @Override
    public @Nullable ItemMenuHost<ItemNetworkAnalyzer> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new DummyItemInventory(this, player, locator);
    }

    public record AnalyserConfig(AnalyserMode mode, float nodeSize, Map<Enum<?>, ColorData> colors) {


        public static final Codec<AnalyserConfig> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                AnalyserMode.CODEC.fieldOf("analyzer_mode").forGetter(a -> a.mode),
                                Codec.FLOAT.fieldOf("node_size").forGetter(a -> a.nodeSize),
                                GlodCodecs.map(FlagType.CODEC, ColorData.CODEC).fieldOf("color_map").forGetter(a -> a.colors)
                        ).apply(builder, AnalyserConfig::new)
        );

        public static final StreamCodec<FriendlyByteBuf, AnalyserConfig> STREAM_CODEC = StreamCodec.of(
                (buf, config) -> config.writeToBytes(buf),
                AnalyserConfig::readFromBytes
        );

        public void writeToBytes(FriendlyByteBuf buf) {
            buf.writeByte(mode.ordinal());
            buf.writeFloat(nodeSize);
            buf.writeByte(colors.size());
            for (var entry : colors.entrySet()) {
                var type = entry.getKey();
                var color = entry.getValue();
                if (type.getClass() == LinkFlag.class) {
                    buf.writeByte(FlagType.LINK.ordinal());
                    buf.writeByte(type.ordinal());
                    buf.writeInt(color.toARGB());
                }
                if (type.getClass() == NodeFlag.class) {
                    buf.writeByte(FlagType.NODE.ordinal());
                    buf.writeByte(type.ordinal());
                    buf.writeInt(color.toARGB());
                }
            }
        }

        public static AnalyserConfig readFromBytes(FriendlyByteBuf buf) {
            var mode = AnalyserMode.byIndex(buf.readByte());
            var node_size = buf.readFloat();
            var colors = new Reference2ObjectOpenHashMap<Enum<?>, ColorData>();
            var size = buf.readByte();
            for (int i = 0; i < size; i ++) {
                var type = FlagType.byIndex(buf.readByte());
                var name = buf.readByte();
                var color = new ColorData(buf.readInt());
                switch (type) {
                    case LINK -> colors.put(LinkFlag.byIndex(name), color);
                    case NODE -> colors.put(NodeFlag.byIndex(name), color);
                }
            }
            return new AnalyserConfig(mode, node_size, colors);
        }

    }

}