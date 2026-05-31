package com.glodblock.github.extendedae.common.items.tools;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.stacks.GenericStack;
import appeng.helpers.IConfigInvHost;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.itemhost.HostConfigModifier;
import com.glodblock.github.extendedae.container.ContainerConfigModifier;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.glodium.util.GlodCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Locale;

public class ItemConfigModifier extends AEBaseItem implements IMenuItem {

    public ItemConfigModifier(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(ContainerConfigModifier.TYPE, p, MenuLocators.forHand(p, hand));
        }
        return InteractionResult.SUCCESS;
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        var world = context.getLevel();
        var player = context.getPlayer();
        var device = FCUtil.findDevice(IConfigInvHost.class, world, context.getClickedPos(), context.getClickLocation());
        var settings = stack.getOrDefault(EAESingletons.MODIFIER_CONFIG_SETTINGS, ConfigSettings.DEFAULT);
        if (device != null) {
            var config = device.getConfig();
            var min = config.getMode() == GenericStackInv.Mode.CONFIG_TYPES ? 0 : 1;
            for (int slot = 0; slot < config.size(); slot ++) {
                var configStack = config.getStack(slot);
                if (configStack != null) {
                    var amt = settings.modify(configStack.amount(), min, config.getMaxAmount(configStack.what()));
                    if (amt < 0) {
                        config.setStack(slot, null);
                    } else {
                        config.setStack(slot, new GenericStack(configStack.what(), amt));
                    }
                }
            }
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("chat.config_modifier.success", this.findName(device)));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private Component findName(Object obj) {
        if (obj instanceof Nameable name) {
            return name.getDisplayName();
        }
        if (obj instanceof BlockEntity te) {
            return te.getBlockState().getBlock().getName();
        }
        return Component.literal(obj.getClass().getSimpleName());
    }

    @Override
    public @Nullable ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new HostConfigModifier(this, player, locator);
    }

    public record ConfigSettings(Mode mode, long data) {

        public static ConfigSettings DEFAULT = new ConfigSettings(Mode.MUL, 1);

        public static final Codec<ConfigSettings> CODEC = RecordCodecBuilder.create(
                builder -> builder
                        .group(
                                Mode.CODEC.fieldOf("mode").forGetter(o -> o.mode),
                                Codec.LONG.fieldOf("data").forGetter(o -> o.data)
                        ).apply(builder, ConfigSettings::new)
        );
        public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ConfigSettings> STREAM_CODEC = StreamCodec.composite(
                Mode.STREAM_CODEC,
                o -> o.mode,
                ByteBufCodecs.VAR_LONG,
                o -> o.data,
                ConfigSettings::new
        );

        public enum Mode implements StringRepresentable {
            ADD, SUB, MUL, DIV, MAX, MIN, SET, RMV;

            static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
            static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull Mode> STREAM_CODEC = GlodCodecs.enumerate(Mode.class);

            @Override
            public @NotNull String getSerializedName() {
                return this.name().toLowerCase(Locale.US);
            }

            public Mode getNext() {
                return values()[(this.ordinal() + 1) % values().length];
            }

        }

        public long modify(long value, long min, long max) {
            return switch (this.mode) {
                case ADD -> Math.min(value + this.data, max);
                case SUB -> Math.max(value - this.data, min);
                case MUL -> Math.min(value * this.data, max);
                case DIV -> Math.max(value / Math.max(this.data, 1), min);
                case MAX -> max;
                case MIN -> min;
                case SET -> Math.clamp(this.data, min, max);
                case RMV -> -1;
            };
        }

    }

}
