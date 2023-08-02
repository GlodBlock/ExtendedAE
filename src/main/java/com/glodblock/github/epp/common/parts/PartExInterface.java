package com.glodblock.github.epp.common.parts;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.core.AppEngBase;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import appeng.parts.AEBasePart;
import appeng.parts.PartModel;
import com.glodblock.github.epp.EPP;
import com.glodblock.github.epp.container.ContainerExInterface;
import com.glodblock.github.epp.util.mixinutil.ExtendedInterfaceLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class PartExInterface extends AEBasePart implements InterfaceLogicHost {

    public static List<Identifier> MODELS = Arrays.asList(
            new Identifier(EPP.MODID, "part/ex_interface"),
            new Identifier(AppEngBase.MOD_ID, "part/interface_on"),
            new Identifier(AppEngBase.MOD_ID, "part/interface_off"),
            new Identifier(AppEngBase.MOD_ID, "part/interface_has_channel")
    );

    public static final PartModel MODELS_OFF = new PartModel(MODELS.get(0), MODELS.get(2));
    public static final PartModel MODELS_ON = new PartModel(MODELS.get(0), MODELS.get(1));
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODELS.get(0), MODELS.get(3));

    private static final IGridNodeListener<PartExInterface> NODE_LISTENER = new AEBasePart.NodeListener<>() {
        @Override
        public void onGridChanged(PartExInterface nodeOwner, IGridNode node) {
            super.onGridChanged(nodeOwner, node);
            nodeOwner.getInterfaceLogic().gridChanged();
        }
    };

    private final InterfaceLogic logic;

    public PartExInterface(IPartItem<?> partItem) {
        super(partItem);
        this.logic = new InterfaceLogic(this.getMainNode(), this, partItem.asItem());
        ((ExtendedInterfaceLogic) this.logic).extend();
    }

    @Override
    public void openMenu(PlayerEntity player, MenuLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(PlayerEntity player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE, player, subMenu.getLocator());
    }

    @Override
    public void saveChanges() {
        getHost().markForSave();
    }

    @Override
    protected IManagedGridNode createMainNode() {
        return GridHelper.createManagedNode(this, NODE_LISTENER);
    }

    @Override
    protected void onMainNodeStateChanged(IGridNodeListener.State reason) {
        super.onMainNodeStateChanged(reason);
        if (getMainNode().hasGridBooted()) {
            this.logic.notifyNeighbors();
        }
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        bch.addBox(2, 2, 14, 14, 14, 16);
        bch.addBox(5, 5, 12, 11, 11, 14);
    }

    @Override
    public void readFromNBT(NbtCompound data) {
        super.readFromNBT(data);
        this.logic.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NbtCompound data) {
        super.writeToNBT(data);
        this.logic.writeToNBT(data);
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        this.logic.addDrops(drops);
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        return 4;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.logic.getConfigManager();
    }

    @Override
    public boolean onPartActivate(PlayerEntity p, Hand hand, Vec3d pos) {
        if (!p.getEntityWorld().isClient()) {
            openMenu(p, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public InterfaceLogic getInterfaceLogic() {
        return this.logic;
    }

    @Override
    public int getPriority() {
        return this.logic.getPriority();
    }

    @Override
    public void setPriority(int newValue) {
        this.logic.setPriority(newValue);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(Identifier id) {
        if (id.equals(UPGRADES)) {
            return logic.getUpgrades();
        }
        return super.getSubInventory(id);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

}
