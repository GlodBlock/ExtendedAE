package com.glodblock.github.ae2netanalyser.common.items;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.ae2netanalyser.common.inventory.DummyItemInventory;
import com.glodblock.github.ae2netanalyser.container.ContainerProfiler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTickAnalyzer extends Item implements IMenuItem {

    public static final TickConfig defaultConfig = new TickConfig(60, true, true, true, true);

    public ItemTickAnalyzer(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
        if (!level.isClientSide() && !p.isShiftKeyDown()) {
            MenuOpener.open(ContainerProfiler.TYPE, p, MenuLocators.forHand(p, hand));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable ItemMenuHost<Item> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new DummyItemInventory(this, player, locator);
    }

    public record TickConfig(int duration, boolean op1, boolean op2, boolean op3, boolean op4) {

        public static final Codec<TickConfig> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                Codec.INT.fieldOf("duration").forGetter(t -> t.duration),
                                Codec.BOOL.fieldOf("op1").forGetter(t -> t.op1),
                                Codec.BOOL.fieldOf("op2").forGetter(t -> t.op2),
                                Codec.BOOL.fieldOf("op3").forGetter(t -> t.op3),
                                Codec.BOOL.fieldOf("op4").forGetter(t -> t.op4)
                        ).apply(builder, TickConfig::new)
        );

        public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull TickConfig> STREAM_CODEC = StreamCodec.of(
                (buf, config) -> config.writeToBytes(buf),
                TickConfig::readFromBytes
        );

        public void writeToBytes(FriendlyByteBuf buf) {
            buf.writeInt(this.duration);
            buf.writeBoolean(this.op1);
            buf.writeBoolean(this.op2);
            buf.writeBoolean(this.op3);
            buf.writeBoolean(this.op4);
        }

        public static TickConfig readFromBytes(FriendlyByteBuf buf) {
            int duration = buf.readInt();
            boolean op1 = buf.readBoolean();
            boolean op2 = buf.readBoolean();
            boolean op3 = buf.readBoolean();
            boolean op4 = buf.readBoolean();
            return new TickConfig(duration, op1, op2, op3, op4);
        }

    }

}
