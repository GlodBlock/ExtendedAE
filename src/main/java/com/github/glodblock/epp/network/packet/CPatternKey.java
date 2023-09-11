package com.github.glodblock.epp.network.packet;

import appeng.crafting.pattern.CraftingPatternItem;
import appeng.crafting.pattern.ProcessingPatternItem;
import appeng.crafting.pattern.SmithingTablePatternItem;
import appeng.crafting.pattern.StonecuttingPatternItem;
import com.github.glodblock.epp.container.pattern.ContainerCraftingPattern;
import com.github.glodblock.epp.container.pattern.ContainerPattern;
import com.github.glodblock.epp.container.pattern.ContainerProcessingPattern;
import com.github.glodblock.epp.container.pattern.ContainerSmithingTablePattern;
import com.github.glodblock.epp.container.pattern.ContainerStonecuttingPattern;
import com.github.glodblock.epp.container.pattern.PatternGuiHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CPatternKey implements IMessage<CPatternKey> {

    private ItemStack pattern;
    private static long nextWarning = -1;

    public CPatternKey() {
        // NO-OP
    }

    public CPatternKey(ItemStack stack) {
        this.pattern = stack;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItemStack(this.pattern, false);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.pattern = buf.readItem();
    }

    @Override
    public void onMessage(Player player) {
        if (player.containerMenu instanceof ContainerPattern) {
            return;
        }
        if (this.pattern.getItem() instanceof ProcessingPatternItem) {
            PatternGuiHandler.open(player, ContainerProcessingPattern.ID.toString(), this.pattern);
        } else if (this.pattern.getItem() instanceof CraftingPatternItem) {
            PatternGuiHandler.open(player, ContainerCraftingPattern.ID.toString(), this.pattern);
        } else if (this.pattern.getItem() instanceof StonecuttingPatternItem) {
            PatternGuiHandler.open(player, ContainerStonecuttingPattern.ID.toString(), this.pattern);
        } else if (this.pattern.getItem() instanceof SmithingTablePatternItem) {
            PatternGuiHandler.open(player, ContainerSmithingTablePattern.ID.toString(), this.pattern);
        } else {
            if (nextWarning < System.currentTimeMillis()) {
                nextWarning = System.currentTimeMillis() + 2000;
                player.sendSystemMessage(Component.translatable("chat.pattern_view.error", "https://github.com/GlodBlock/ExtendedAE/issues"));
            }
        }
    }

    @Override
    public Class<CPatternKey> getPacketClass() {
        return CPatternKey.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
