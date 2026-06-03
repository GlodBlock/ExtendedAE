package com.glodblock.github.extendedae.util;

import appeng.api.inventories.InternalInventory;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FCUtil {

    public static void replaceTile(Level world, BlockPos pos, BlockEntity oldTile, BlockEntity newTile, BlockState newBlock) {
        var contents = oldTile.saveWithFullMetadata(world.registryAccess());
        world.removeBlockEntity(pos);
        world.removeBlock(pos, false);
        world.setBlock(pos, newBlock, 3);
        world.setBlockEntity(newTile);
        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(newTile.problemPath(), ExtendedAE.LOGGER)) {
            newTile.loadWithComponents(TagValueInput.create(reporter, world.registryAccess(), contents));
        }
        if (newTile instanceof AEBaseBlockEntity aeTile) {
            aeTile.markForUpdate();
        } else {
            newTile.setChanged();
        }
    }

    public static boolean ejectInv(Level world, BlockPos pos, InternalInventory inv, Set<Direction> outputSides, Predicate<? super BlockEntity> shouldIgnore) {
        for (var dir : outputSides) {
            var te = world.getBlockEntity(pos.relative(dir));
            if (te == null || shouldIgnore.test(te)) {
                continue;
            }
            var target = InternalInventory.wrapExternal(world, pos.relative(dir), dir.getOpposite());
            if (target != null) {
                int startItems = inv.getStackInSlot(0).getCount();
                inv.insertItem(0, target.addItems(inv.extractItem(0, 64, false)), false);
                int endItems = inv.getStackInSlot(0).getCount();
                if (startItems != endItems) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int speedCardMap(int card) {
        return speedCardMap(card, 1);
    }

    public static int speedCardMap(int card, int multi) {
        return multi * switch (card) {
            case 1 -> 3;
            case 2 -> 5;
            case 3 -> 10;
            case 4 -> 50;
            default -> 2;
        };
    }

    public static String[] trimSplit(String str) {
        var sp = str.split(",");
        for (int i = 0; i < sp.length; i ++) {
            sp[i] = sp[i].trim();
        }
        return sp;
    }

    @Nullable
    public static <T> T findDevice(Class<T> clazz, Level world, BlockPos pos, Vec3 clicked) {
        var tile = world.getBlockEntity(pos);
        if (clazz.isInstance(tile)) {
            return clazz.cast(tile);
        }
        if (tile instanceof CableBusBlockEntity cable) {
            Vec3 hitInBlock = new Vec3(clicked.x - pos.getX(), clicked.y - pos.getY(), clicked.z - pos.getZ());
            var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
            if (clazz.isInstance(part)) {
                return clazz.cast(part);
            }
        }
        return null;
    }

    public static List<String> tokenize(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        text = text.trim().toLowerCase();
        List<String> tokens = new ArrayList<>();
        for (var token : text.split(" ")) {
            if (!token.isBlank()) {
                tokens.add(token.trim());
            }
        }
        return tokens;
    }

    public static boolean compareTokens(List<String> filter, List<String> target) {
        int p = 0;
        while (p <= target.size() - filter.size()) {
            int q = p, f = 0;
            while (q < target.size() && f < filter.size()) {
                var tt = target.get(q);
                var ft = filter.get(f);
                if (tt.contains(ft)) {
                    q++;
                    f++;
                } else {
                    q++;
                }
            }
            if (f >= filter.size()) {
                return true;
            } else {
                p++;
            }
        }
        return false;
    }

    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<>() {

            private T value;
            private boolean init = false;

            @Override
            public T get() {
                if (!this.init) {
                    this.init = true;
                    this.value = supplier.get();
                }
                return this.value;
            }

        };
    }

}
