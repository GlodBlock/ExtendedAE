package com.glodblock.github.appflux.common.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import com.glodblock.github.appflux.common.me.key.type.FluxKeyType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluxKey extends AEKey {

    @NotNull
    private final EnergyType type;

    private FluxKey(@NotNull EnergyType type) {
        this.type = type;
    }

    @Nullable
    public static FluxKey of(EnergyType type) {
        if (type != null) {
            return new FluxKey(type);
        }
        return null;
    }

    public EnergyType getEnergyType() {
        return this.type;
    }

    @Override
    public AEKeyType getType() {
        return FluxKeyType.TYPE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        var tag = new CompoundTag();
        tag.putString("t", this.type.name());
        return tag;
    }

    @Override
    public Object getPrimaryKey() {
        return this.type;
    }

    @Override
    public ResourceLocation getId() {
        return this.type.id();
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        data.writeEnum(this.type);
    }

    @Override
    public String getModId() {
        return this.type.from();
    }

    @Override
    protected Component computeDisplayName() {
        return this.type.translate();
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
        // NO-OP
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        var that = (FluxKey) o;
        return that.type == this.type;
    }

    @Override
    public int hashCode() {
        return this.type.ordinal();
    }

    @Override
    public String toString() {
        return "FluxKey{" + "energy=" + type.name() + '}';
    }

}
