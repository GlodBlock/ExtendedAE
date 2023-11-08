package com.github.glodblock.epp.common.blocks;

import appeng.api.orientation.BlockOrientation;
import appeng.api.orientation.IOrientationStrategy;
import appeng.api.orientation.OrientationStrategies;
import appeng.api.orientation.RelativeSide;
import appeng.api.util.AEAxisAlignedBB;
import appeng.client.render.effects.LightningArcParticleData;
import appeng.core.AEConfig;
import appeng.core.AppEngClient;
import com.github.glodblock.epp.common.tileentities.TileExCharger;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BlockExCharger extends BlockBaseGui<TileExCharger> {

    public BlockExCharger() {
        super(metalProps().noOcclusion());
    }

    @Override
    public IOrientationStrategy getOrientationStrategy() {
        return OrientationStrategies.full();
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 2;
    }

    @Override
    public void openGui(TileExCharger tile, Player p) {
        tile.activate(p);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource r) {
        if (!AEConfig.instance().isEnableEffects()) {
            return;
        }
        var blockEntity = this.getBlockEntity(level, pos);
        if (blockEntity != null && blockEntity.isWorking()) {
            if (r.nextFloat() < 0.5) {
                return;
            }
            var rotation = BlockOrientation.get(blockEntity);
            for (int bolts = 0; bolts < 3; bolts++) {
                // Slightly offset the lightning arc on the x/z plane
                var xOff = Mth.randomBetween(r, -0.15f, 0.15f);
                var zOff = Mth.randomBetween(r, -0.15f, 0.15f);

                // Compute two points in the charger block. One at the bottom, and one on the top.
                // Account for the rotation while doing this.
                var center = new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
                var origin = new Vector3f(xOff, -0.3f, zOff);
                origin.rotate(rotation.getQuaternion());
                origin.add(center);
                var target = new Vector3f(xOff, 0.3f, zOff);
                target.rotate(rotation.getQuaternion());
                target.add(center);

                // Split the arcs between arc coming from the top/bottom of the charger since it's symmetrical
                if (r.nextBoolean()) {
                    var tmp = target;
                    target = origin;
                    origin = tmp;
                }

                if (AppEngClient.instance().shouldAddParticles(r)) {
                    Minecraft.getInstance().particleEngine.createParticle(
                            new LightningArcParticleData(new Vec3(target)),
                            origin.x(),
                            origin.y(),
                            origin.z(),
                            0.0, 0.0, 0.0);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        var orientation = getOrientation(state);
        var up = orientation.getSide(RelativeSide.TOP);
        var forward = orientation.getSide(RelativeSide.FRONT);
        var twoPixels = 2.0 / 16.0;

        var bb = new AEAxisAlignedBB(0, 0, 0, 1.0,
                1.00, 1.0);

        switch (forward) {
            case UP -> bb.maxY = 1.0 - twoPixels;
            case DOWN -> bb.minY = 0.0 + twoPixels;
            case SOUTH -> bb.maxZ = 1.0 - twoPixels;
            case NORTH -> bb.minZ = 0.0 + twoPixels;
            case WEST -> bb.minX = 0.0 + twoPixels;
            case EAST -> bb.maxX = 1.0 - twoPixels;
            default -> {
            }
        }

        return Shapes.create(bb.getBoundingBox());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.create(new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0));
    }

}
