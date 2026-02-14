package com.glodblock.github.extendedae.container;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Settings;
import appeng.api.config.StorageFilter;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import com.glodblock.github.extendedae.common.parts.PartTagStorageBus;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import com.glodblock.github.glodium.network.packet.sync.Paras;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public class ContainerTagStorageBus extends UpgradeableMenu<PartTagStorageBus> implements IActionHolder {

    private final Map<String, Consumer<Paras>> actions = createHolder();
    private static final String ACTION_PARTITION = "partition";

    public static final MenuType<ContainerTagStorageBus> TYPE = MenuTypeBuilder
            .create(ContainerTagStorageBus::new, PartTagStorageBus.class)
            .withInitialData((host, buf) -> {
                buf.writeUtf(host.getTagFilter(true));
                buf.writeUtf(host.getTagFilter(false));
            }, (host, container, buf) -> {
                container.exp = buf.readUtf();
                container.exp2 = buf.readUtf();
            })
            .build("tag_storage_bus");

    @GuiSync(3)
    public AccessRestriction rwMode = AccessRestriction.READ_WRITE;

    @GuiSync(4)
    public StorageFilter storageFilter = StorageFilter.EXTRACTABLE_ONLY;

    @GuiSync(7)
    public YesNo filterOnExtract = YesNo.YES;

    @GuiSync(8)
    @Nullable
    public Component connectedTo;

    public String exp;
    public String exp2;

    public ContainerTagStorageBus(int id, Inventory ip, PartTagStorageBus te) {
        super(TYPE, id, ip, te);
        registerClientAction(ACTION_PARTITION, this::partition);
        this.actions.put("set", o -> this.setExp(o.get(0), o.get(1)));
        this.connectedTo = te.getConnectedToDescription();
        this.exp = te.getTagFilter(true);
        this.exp2 = te.getTagFilter(false);
    }

    @Override
    protected void setupConfig() {
        // NO-OP
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        this.connectedTo = getHost().getConnectedToDescription();
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setReadWriteMode(cm.getSetting(Settings.ACCESS));
        this.setStorageFilter(cm.getSetting(Settings.STORAGE_FILTER));
        this.setFilterOnExtract(cm.getSetting(Settings.FILTER_ON_EXTRACT));
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        return false;
    }

    public void partition() {
        if (isClientSide()) {
            sendClientAction(ACTION_PARTITION);
            return;
        }
        this.broadcastChanges();
    }

    public void setExp(String exp, boolean isWhite) {
        if (isWhite) {
            this.exp = exp;
        } else {
            this.exp2 = exp;
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        this.getHost().setTagFilter(this.exp, true);
        this.getHost().setTagFilter(this.exp2, false);
        super.removed(player);
    }

    public AccessRestriction getReadWriteMode() {
        return this.rwMode;
    }

    private void setReadWriteMode(AccessRestriction rwMode) {
        this.rwMode = rwMode;
    }

    public StorageFilter getStorageFilter() {
        return this.storageFilter;
    }

    private void setStorageFilter(StorageFilter storageFilter) {
        this.storageFilter = storageFilter;
    }

    public YesNo getFilterOnExtract() {
        return this.filterOnExtract;
    }

    public void setFilterOnExtract(YesNo filterOnExtract) {
        this.filterOnExtract = filterOnExtract;
    }

    @Nullable
    public Component getConnectedTo() {
        return connectedTo;
    }

    @NotNull
    @Override
    public Map<String, Consumer<Paras>> getActionMap() {
        return this.actions;
    }
}
