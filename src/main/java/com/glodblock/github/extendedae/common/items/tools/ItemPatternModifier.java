package com.glodblock.github.extendedae.common.items.tools;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import com.glodblock.github.extendedae.common.me.itemhost.HostPatternModifier;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class ItemPatternModifier extends AEBaseItem implements IMenuItem {


    public ItemPatternModifier() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
        if (!level.isClientSide()) {
            MenuOpener.open(ContainerPatternModifier.TYPE, p, MenuLocators.forHand(p, hand));
        }
        return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), p.getItemInHand(hand));
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext context) {
        var world = context.getLevel();
        var pp = this.findPatternProvider(world, context.getClickedPos(), context.getClickLocation());
        var player = context.getPlayer();
        boolean didSomething = false;
        if (!world.isClientSide() && player != null && !InteractionUtil.isInAlternateUseMode(player) && pp != null) {
            var inv = this.getMenuHost(player, MenuLocators.forStack(stack), null).getInventoryByName("patternInv");
            for (int slot = 0; slot < inv.size(); slot ++) {
                var pattern = inv.getStackInSlot(slot);
                if (!pattern.isEmpty()) {
                    var overflow = pp.getPatternInv().addItems(pattern);
                    if (overflow.isEmpty()) {
                        inv.setItemDirect(slot, ItemStack.EMPTY);
                        didSomething = true;
                    }
                }
            }
            return didSomething ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Nullable
    private PatternProviderLogic findPatternProvider(Level world, BlockPos pos, Vec3 clicked) {
        var tile = world.getBlockEntity(pos);
        if (tile instanceof PatternProviderLogicHost host) {
            return host.getLogic();
        }
        if (tile instanceof CableBusBlockEntity cable) {
            Vec3 hitInBlock = new Vec3(clicked.x - pos.getX(), clicked.y - pos.getY(), clicked.z - pos.getZ());
            var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
            if (part instanceof PatternProviderLogicHost host) {
                return host.getLogic();
            }
        }
        return null;
    }

    @Override
    @NotNull
    public HostPatternModifier getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new HostPatternModifier(this, player, locator);
    }

}
