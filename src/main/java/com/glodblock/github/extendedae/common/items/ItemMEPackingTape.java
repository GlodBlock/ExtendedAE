package com.glodblock.github.extendedae.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.util.Platform;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;

public class ItemMEPackingTape extends Item {

    private static final ObjectSet<Identifier> WHITE_LIST = new ObjectOpenHashSet<>();

    public ItemMEPackingTape(Properties properties) {
        super(properties.durability(64));
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        var player = context.getPlayer();
        var pack = EAESingletons.PACKAGE.toStack();
        var success = false;
        if (tile != null && player != null) {
            if (tile instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part != null) {
                    pack.set(EAESingletons.IS_PART, true);
                    var partItem = part.getPartItem().asItem();
                    var id = BuiltInRegistries.ITEM.getKey(partItem);
                    if (!WHITE_LIST.contains(id)) {
                        return InteractionResult.PASS;
                    }
                    try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(ExtendedAE.LOGGER)) {
                        var output = TagValueOutput.createWithContext(reporter, world.registryAccess());
                        part.writeToNBT(output);
                        pack.set(EAESingletons.TAPE_PART_DATA, new PartPackageData(id, output.buildResult()));
                        cable.removePart(part);
                        success = true;
                    }
                }
            } else {
                pack.set(EAESingletons.IS_PART, false);
                var state = tile.getBlockState();
                var blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
                if (!WHITE_LIST.contains(blockId)) {
                    return InteractionResult.PASS;
                }
                var id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tile.getType());
                var ctxTag = tile.saveWithFullMetadata(world.registryAccess());
                pack.set(EAESingletons.TAPE_TILE_DATA, new TilePackageData(id, blockId, NbtUtils.writeBlockState(state), ctxTag));
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
                success = true;
            }
            if (success) {
                Platform.spawnDrops(world, pos, Collections.singletonList(pack));
                context.getItemInHand().hurtAndBreak(1, player, context.getHand());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static void registerPackableDevice(Identifier id) {
        WHITE_LIST.add(id);
    }

    public record PartPackageData(Identifier id, CompoundTag context) {

        public static final Codec<PartPackageData> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                Identifier.CODEC.fieldOf("id").forGetter(o -> o.id),
                                CompoundTag.CODEC.fieldOf("ctx").forGetter(o -> o.context)
                        ).apply(builder, PartPackageData::new)
        );
        public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull PartPackageData> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC,
                o -> o.id,
                GlodCodecs.NBT_STREAM_CODEC,
                o -> o.context,
                PartPackageData::new
        );

    }

    public record TilePackageData(Identifier id, Identifier block, CompoundTag state, CompoundTag context) {

        public static final Codec<TilePackageData> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                Identifier.CODEC.fieldOf("id").forGetter(o -> o.id),
                                Identifier.CODEC.fieldOf("block_id").forGetter(o -> o.block),
                                CompoundTag.CODEC.fieldOf("state").forGetter(o -> o.state),
                                CompoundTag.CODEC.fieldOf("ctx").forGetter(o -> o.context)
                        ).apply(builder, TilePackageData::new)
        );
        public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull TilePackageData> STREAM_CODEC = StreamCodec.composite(
                Identifier.STREAM_CODEC,
                o -> o.id,
                Identifier.STREAM_CODEC,
                o -> o.block,
                GlodCodecs.NBT_STREAM_CODEC,
                o -> o.state,
                GlodCodecs.NBT_STREAM_CODEC,
                o -> o.context,
                TilePackageData::new
        );

    }

}
