package com.glodblock.github.extendedae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.util.SettingsFrom;
import appeng.util.prioritylist.IPartitionList;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.taglist.TagPriorityList;
import com.glodblock.github.extendedae.common.parts.base.PartSpecialStorageBus;
import com.glodblock.github.extendedae.container.ContainerTagStorageBus;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PartTagStorageBus extends PartSpecialStorageBus {

    @NotNull
    private String oreExpWhite = "";
    @NotNull
    private String oreExpBlack = "";

    public PartTagStorageBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public void readFromNBT(ValueInput data) {
        super.readFromNBT(data);
        this.oreExpWhite = data.getStringOr("oreExp", "");
        this.oreExpBlack = data.getStringOr("oreExp2", "");
    }

    @Override
    public void writeToNBT(ValueOutput data) {
        super.writeToNBT(data);
        data.putString("oreExp", this.oreExpWhite);
        data.putString("oreExp2", this.oreExpBlack);
    }

    public MenuType<?> getMenuType() {
        return ContainerTagStorageBus.TYPE;
    }

    @Override
    protected final int getUpgradeSlots() {
        return 2;
    }

    public void setTagFilter(String exp, boolean isWhite) {
        if (isWhite) {
            if (!exp.equals(this.oreExpWhite)) {
                this.oreExpWhite = exp;
                this.filter = null;
                this.forceUpdate();
                this.getHost().markForSave();
            }
        } else {
            if (!exp.equals(this.oreExpBlack)) {
                this.oreExpBlack = exp;
                this.filter = null;
                this.forceUpdate();
                this.getHost().markForSave();
            }
        }
    }

    public String getTagFilter(boolean isWhite) {
        return isWhite ? this.oreExpWhite : this.oreExpBlack;
    }

    protected IPartitionList createFilter() {
        if (this.filter == null) {
            this.filter = new TagPriorityList(this.oreExpWhite, this.oreExpBlack);
        }
        return this.filter;
    }

    @Override
    public void importSettings(SettingsFrom mode, DataComponentMap input, @Nullable Player player) {
        super.importSettings(mode, input, player);
        var oreExps = input.get(EAESingletons.TAG_EXPRESS);
        if (oreExps != null) {
            this.setTagFilter(oreExps.left(), true);
            this.setTagFilter(oreExps.right(), false);
        }
    }

    @Override
    public void exportSettings(SettingsFrom mode, DataComponentMap.Builder output) {
        super.exportSettings(mode, output);
        if (mode == SettingsFrom.MEMORY_CARD) {
            output.set(EAESingletons.TAG_EXPRESS, Pair.of(this.oreExpWhite, this.oreExpBlack));
        }
    }

}
