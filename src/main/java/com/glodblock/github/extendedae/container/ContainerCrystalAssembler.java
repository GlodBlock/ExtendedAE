package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import appeng.util.ConfigMenuInventory;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.tileentities.TileCrystalAssembler;
import com.glodblock.github.extendedae.container.helper.DirectionSet;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContainerCrystalAssembler extends UpgradeableMenu<TileCrystalAssembler> implements IProgressProvider, IActionHolder {

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;

    @GuiSync(9)
    public DirectionSet outputSides = new DirectionSet(new ArrayList<>());

    private final ActionMap actions = ActionMap.create();

    private final AppEngSlot tank;

    public static final MenuType<ContainerCrystalAssembler> TYPE = MenuTypeBuilder
            .create(ContainerCrystalAssembler::new, TileCrystalAssembler.class)
            .buildUnregistered(ExtendedAE.id("crystal_assembler"));

    public ContainerCrystalAssembler(int id, Inventory ip, TileCrystalAssembler host) {
        super(TYPE, id, ip, host);
        for (int x = 0; x < TileCrystalAssembler.SLOTS; x ++) {
            this.addSlot(new AppEngSlot(host.getInput(), x), SlotSemantics.MACHINE_INPUT);
        }
        this.addSlot(tank = new AppEngSlot(new ConfigMenuInventory(host.getTank()), 0), SlotSemantics.STORAGE);
        this.addSlot(new OutputSlot(host.getOutput(), 0, null), SlotSemantics.MACHINE_OUTPUT);
        tank.setEmptyTooltip(() -> List.of(
                Component.translatable("gui.extendedae.crystal_assembler.tank_empty"),
                Component.translatable("gui.extendedae.crystal_assembler.amount", 0, TileCrystalAssembler.TANK_CAP).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT)
        ));
        this.actions.put("set_side", o -> this.setOutputSide(o.get(0), o.get(1)));
    }

    private void setOutputSide(String name, boolean value) {
        var side = Direction.byName(name);
        if (value) {
            this.getHost().getOutputSides().add(side);
        } else {
            this.getHost().getOutputSides().remove(side);
        }
    }

    public boolean isTank(Slot slot) {
        return slot == this.tank;
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = cm.getSetting(Settings.AUTO_EXPORT);
        this.outputSides.clear();
        this.outputSides.addAll(this.getHost().getOutputSides());
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.processingTime = getHost().getProgress();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return TileCrystalAssembler.MAX_PROGRESS;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public List<Direction> getOutputSides() {
        return outputSides.sides();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }

}
