package com.glodblock.github.appflux.common.me.inventory;

import appeng.api.behaviors.GenericInternalInventory;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class FEGenericStackInvStorage implements EnergyHandler {

    private final GenericInternalInventory inv;
    private final GenericStackEnergyHandler handler;

    public FEGenericStackInvStorage(GenericInternalInventory inv) {
        this.inv = inv;
        this.handler = new GenericStackEnergyHandler(inv);
    }

    @Override
    public long getAmountAsLong() {
        long cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if (stack != null) {
                if (FluxKey.of(EnergyType.FE).equals(stack.what())) {
                    cnt += stack.amount();
                }
            }
        }
        return cnt;
    }

    @Override
    public long getCapacityAsLong() {
        int cnt = 0;
        for (int slot = 0; slot < this.inv.size(); slot ++) {
            var stack = this.inv.getStack(slot);
            if (stack != null) {
                if (FluxKey.of(EnergyType.FE).equals(stack.what())) {
                    cnt ++;
                }
            } else {
                cnt ++;
            }
        }
        return cnt * this.inv.getMaxAmount(FluxKey.of(EnergyType.FE));
    }

    @Override
    public int insert(int amount, @NotNull TransactionContext transaction) {
        return this.handler.insert(EnergyResource.INSTANCE, amount, transaction);
    }

    @Override
    public int extract(int amount, @NotNull TransactionContext transaction) {
        return this.handler.extract(EnergyResource.INSTANCE, amount, transaction);
    }

}
