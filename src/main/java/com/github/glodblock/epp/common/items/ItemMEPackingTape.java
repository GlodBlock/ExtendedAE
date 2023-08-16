package com.github.glodblock.epp.common.items;

import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.util.Platform;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ItemMEPackingTape extends Item {

    private static final ObjectSet<ResourceLocation> WHITE_LIST = new ObjectOpenHashSet<>();

    public ItemMEPackingTape() {
        super(new Item.Properties().durability(64).tab(EPPItemAndBlock.TAB));
    }

    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var side = context.getClickedFace();
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var tile = world.getBlockEntity(pos);
        if (tile != null) {
            var tag = new CompoundTag();
            if (tile instanceof CableBusBlockEntity cable) {
                var part = cable.getPart(side);
                if (part != null) {
                    tag.putBoolean("part", true);
                    var partItem = part.getPartItem().asItem();
                    var id = ForgeRegistries.ITEMS.getKey(partItem);
                    if (!WHITE_LIST.contains(id)) {
                        return InteractionResult.PASS;
                    }
                    assert id != null;
                    tag.putString("id", id.toString());
                    var ctxTag = new CompoundTag();
                    part.writeToNBT(ctxTag);
                    ctxTag.putBoolean("exae_reload", true);
                    tag.put("ctx", ctxTag);
                    cable.removePart(part);
                }
            } else {
                tag.putBoolean("part", false);
                var state = tile.getBlockState();
                var blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
                if (!WHITE_LIST.contains(blockId)) {
                    return InteractionResult.PASS;
                }
                var id = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(tile.getType());
                assert id != null;
                assert blockId != null;
                tag.putString("id", id.toString());
                tag.putString("block_id", blockId.toString());
                tag.put("state", NbtUtils.writeBlockState(state));
                var ctxTag = tile.serializeNBT();
                tag.put("ctx", ctxTag);
                world.removeBlockEntity(pos);
                world.removeBlock(pos, false);
            }
            if (!tag.isEmpty()) {
                var pack = new ItemStack(EPPItemAndBlock.PACKAGE);
                pack.setTag(tag);
                Platform.spawnDrops(world, pos, Collections.singletonList(pack));
                context.getItemInHand().hurt(1, RandomSource.create(), null);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack is, Level world, @NotNull List<Component> lines, @NotNull TooltipFlag adv) {
        lines.add(Component.translatable("me_packing_tape.tooltip").withStyle(ChatFormatting.GRAY));
    }

    public static void registerPackableDevice(ResourceLocation id) {
        WHITE_LIST.add(id);
    }

}
