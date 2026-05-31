package com.glodblock.github.extendedae.client.model;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.blocks.matrix.BlockAssemblerMatrixGlass;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class AssemblerGlassBakedModel implements DynamicBlockStateModel {

    private static final Object2ReferenceMap<FaceCorner, List<Vector3f>> V_MAP = createVertexMap();
    private static final EnumMap<Direction, List<Vector3f>> F_MAP = createFaceMap();
    private static final int LU = 0;
    private static final int RU = 1;
    private static final int LD = 2;
    private static final int RD = 4;
    private static final Material SIDE = new Material(ExtendedAE.id("block/assembler_matrix/glass/sides"));
    private static final Material[] FACES = new Material[] {
            new Material(ExtendedAE.id("block/assembler_matrix/glass/face_a")),
            new Material(ExtendedAE.id("block/assembler_matrix/glass/face_b")),
            new Material(ExtendedAE.id("block/assembler_matrix/glass/face_c"))
    };

    private final Material.Baked glassSide;
    private final Material.Baked[] glassFaces;

    public AssemblerGlassBakedModel(MaterialBaker bakedTextureGetter) {
        ModelDebugName debugName = getClass()::toString;
        this.glassSide = bakedTextureGetter.get(SIDE, debugName);
        this.glassFaces = Arrays.stream(FACES).map(face -> bakedTextureGetter.get(face, debugName)).toArray(Material.Baked[]::new);
    }

    @Override
    public void collectParts(@NotNull BlockAndTintGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull RandomSource random, @NotNull List<BlockStateModelPart> parts) {
        var connect = new Connect();
        connect.init(pos);
        for (int x = -1; x <= 1; x ++) {
            for (int y = -1; y <= 1; y ++) {
                for (int z = -1; z <= 1; z ++) {
                    var offset = pos.offset(x, y, z);
                    if (world.getBlockState(offset).getAppearance(world, offset, Direction.NORTH, state, pos).getBlock() instanceof BlockAssemblerMatrixGlass) {
                        connect.set(x, y, z);
                    }
                }
            }
        }
        var quads = new QuadCollection.Builder();
        for (var side : Direction.values()) {
            this.addQuad(quads, side, connect.getIndex(side, LU), LU);
            this.addQuad(quads, side, connect.getIndex(side, RU), RU);
            this.addQuad(quads, side, connect.getIndex(side, LD), LD);
            this.addQuad(quads, side, connect.getIndex(side, RD), RD);
            this.addQuad(quads, side, connect.getFace(side));
        }
        parts.add(new SimpleModelWrapper(quads.build(), false, glassSide));
    }

    private List<Vector3f> calculateCorners(Direction face, int corner) {
        return V_MAP.get(new FaceCorner(face, corner));
    }

    private void addQuad(QuadCollection.Builder quads, Direction side, int index) {
        if (index < 0) {
            return;
        }
        var builder = new QuadBakingVertexConsumer();
        var sprite = this.glassFaces[index];
        var cons = F_MAP.get(side);
        builder.setSprite(sprite);
        builder.setDirection(side);
        builder.setShade(true);
        var normal = side.getUnitVec3i();
        var c1 = cons.get(0);
        var c2 = cons.get(1);
        var c3 = cons.get(2);
        var c4 = cons.get(3);
        this.putVertex(builder, sprite, normal, c1.x(), c1.y(), c1.z(), 0, 0);
        this.putVertex(builder, sprite, normal, c2.x(), c2.y(), c2.z(), 0, 1);
        this.putVertex(builder, sprite, normal, c3.x(), c3.y(), c3.z(), 1, 1);
        this.putVertex(builder, sprite, normal, c4.x(), c4.y(), c4.z(), 1, 0);
        quads.addCulledFace(side, builder.bakeQuad());
    }

    private void addQuad(QuadCollection.Builder quads, Direction side, int index, int corner) {
        if (index < 0) {
            return;
        }
        var builder = new QuadBakingVertexConsumer();
        var cons = this.calculateCorners(side, corner);
        builder.setSprite(this.glassSide);
        builder.setDirection(side);
        builder.setShade(true);
        var normal = side.getUnitVec3i();
        var c1 = cons.get(0);
        var c2 = cons.get(1);
        var c3 = cons.get(2);
        var c4 = cons.get(3);
        float u0 = this.getU0(index);
        float u1 = this.getU1(index);
        float v0 = this.getV0(index);
        float v1 = this.getV1(index);
        switch (corner) {
            case LU -> {
                this.putVertex(builder, this.glassSide, normal, c1.x(), c1.y(), c1.z(), u0, v0);
                this.putVertex(builder, this.glassSide, normal, c2.x(), c2.y(), c2.z(), u0, v1);
                this.putVertex(builder, this.glassSide, normal, c3.x(), c3.y(), c3.z(), u1, v1);
                this.putVertex(builder, this.glassSide, normal, c4.x(), c4.y(), c4.z(), u1, v0);
            }
            case RU -> {
                this.putVertex(builder, this.glassSide, normal, c1.x(), c1.y(), c1.z(), u1, v0);
                this.putVertex(builder, this.glassSide, normal, c2.x(), c2.y(), c2.z(), u1, v1);
                this.putVertex(builder, this.glassSide, normal, c3.x(), c3.y(), c3.z(), u0, v1);
                this.putVertex(builder, this.glassSide, normal, c4.x(), c4.y(), c4.z(), u0, v0);
            }
            case LD -> {
                this.putVertex(builder, this.glassSide, normal, c1.x(), c1.y(), c1.z(), u0, v1);
                this.putVertex(builder, this.glassSide, normal, c2.x(), c2.y(), c2.z(), u0, v0);
                this.putVertex(builder, this.glassSide, normal, c3.x(), c3.y(), c3.z(), u1, v0);
                this.putVertex(builder, this.glassSide, normal, c4.x(), c4.y(), c4.z(), u1, v1);
            }
            case RD -> {
                this.putVertex(builder, this.glassSide, normal, c1.x(), c1.y(), c1.z(), u1, v1);
                this.putVertex(builder, this.glassSide, normal, c2.x(), c2.y(), c2.z(), u1, v0);
                this.putVertex(builder, this.glassSide, normal, c3.x(), c3.y(), c3.z(), u0, v0);
                this.putVertex(builder, this.glassSide, normal, c4.x(), c4.y(), c4.z(), u0, v1);
            }
        }
        quads.addCulledFace(side, builder.bakeQuad());
    }

    private static EnumMap<Direction, List<Vector3f>> createFaceMap() {
        EnumMap<Direction, List<Vector3f>> map = new EnumMap<>(Direction.class);
        map.put(Direction.EAST, List.of(new Vector3f(1, 1, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)));
        map.put(Direction.WEST, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)).reversed());
        map.put(Direction.UP, List.of(new Vector3f(1, 1, 1), new Vector3f(1, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1, 1)));
        map.put(Direction.DOWN, List.of(new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1)).reversed());
        map.put(Direction.SOUTH, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 1, 1)));
        map.put(Direction.NORTH, List.of(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)).reversed());
        return map;
    }

    private static Object2ReferenceMap<FaceCorner, List<Vector3f>> createVertexMap() {
        Object2ReferenceMap<FaceCorner, List<Vector3f>> map = new Object2ReferenceOpenHashMap<>();
        map.put(new FaceCorner(Direction.EAST, LU), List.of(new Vector3f(1, 1, 1), new Vector3f(1, 0.5f, 1), new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 1, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RU), List.of(new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 0.5f, 0), new Vector3f(1, 1, 0)));
        map.put(new FaceCorner(Direction.EAST, LD), List.of(new Vector3f(1, 0.5f, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0.5f), new Vector3f(1, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RD), List.of(new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 0, 0.5f), new Vector3f(1, 0, 0), new Vector3f(1, 0.5f, 0)));
        map.put(new FaceCorner(Direction.WEST, LU), List.of(new Vector3f(0, 1, 0), new Vector3f(0, 0.5f, 0), new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 1, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RU), List.of(new Vector3f(0, 1, 0.5f), new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 0.5f, 1), new Vector3f(0, 1, 1)));
        map.put(new FaceCorner(Direction.WEST, LD), List.of(new Vector3f(0, 0.5f, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RD), List.of(new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0, 1), new Vector3f(0, 0.5f, 1)));
        map.put(new FaceCorner(Direction.SOUTH, LU), List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0.5f, 1), new Vector3f(0.5f, 0.5f, 1), new Vector3f(0.5f, 1, 1)));
        map.put(new FaceCorner(Direction.SOUTH, RU), List.of(new Vector3f(0.5f, 1, 1), new Vector3f(0.5f, 0.5f, 1), new Vector3f(1, 0.5f, 1), new Vector3f(1, 1, 1)));
        map.put(new FaceCorner(Direction.SOUTH, LD), List.of(new Vector3f(0, 0.5f, 1), new Vector3f(0, 0, 1), new Vector3f(0.5f, 0, 1), new Vector3f(0.5f, 0.5f, 1)));
        map.put(new FaceCorner(Direction.SOUTH, RD), List.of(new Vector3f(0.5f, 0.5f, 1), new Vector3f(0.5f, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0.5f, 1)));
        map.put(new FaceCorner(Direction.NORTH, LU), List.of(new Vector3f(1, 1, 0), new Vector3f(1, 0.5f, 0), new Vector3f(0.5f, 0.5f, 0), new Vector3f(0.5f, 1, 0)));
        map.put(new FaceCorner(Direction.NORTH, RU), List.of(new Vector3f(0.5f, 1, 0), new Vector3f(0.5f, 0.5f, 0), new Vector3f(0, 0.5f, 0), new Vector3f(0, 1, 0)));
        map.put(new FaceCorner(Direction.NORTH, LD), List.of(new Vector3f(1, 0.5f, 0), new Vector3f(1, 0, 0), new Vector3f(0.5f, 0, 0), new Vector3f(0.5f, 0.5f, 0)));
        map.put(new FaceCorner(Direction.NORTH, RD), List.of(new Vector3f(0.5f, 0.5f, 0), new Vector3f(0.5f, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0.5f, 0)));
        map.put(new FaceCorner(Direction.UP, LU), List.of(new Vector3f(0, 1, 1), new Vector3f(0.5f, 1, 1), new Vector3f(0.5f, 1, 0.5f), new Vector3f(0, 1, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RU), List.of(new Vector3f(0, 1, 0.5f), new Vector3f(0.5f, 1, 0.5f), new Vector3f(0.5f, 1, 0), new Vector3f(0, 1, 0)));
        map.put(new FaceCorner(Direction.UP, LD), List.of(new Vector3f(0.5f, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0.5f), new Vector3f(0.5f, 1, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RD), List.of(new Vector3f(0.5f, 1, 0.5f), new Vector3f(1, 1, 0.5f), new Vector3f(1, 1, 0), new Vector3f(0.5f, 1, 0)));
        map.put(new FaceCorner(Direction.DOWN, LU), List.of(new Vector3f(1, 0, 1), new Vector3f(0.5f, 0, 1), new Vector3f(0.5f, 0, 0.5f), new Vector3f(1, 0, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RU), List.of(new Vector3f(1, 0, 0.5f), new Vector3f(0.5f, 0, 0.5f), new Vector3f(0.5f, 0, 0), new Vector3f(1, 0, 0)));
        map.put(new FaceCorner(Direction.DOWN, LD), List.of(new Vector3f(0.5f, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 0.5f), new Vector3f(0.5f, 0, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RD), List.of(new Vector3f(0.5f, 0, 0.5f), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0, 0)));
        return map;
    }

    private void putVertex(QuadBakingVertexConsumer builder, Material.Baked backed, Vec3i normal, float x, float y, float z, float u, float v) {
        try (var sprite = backed.sprite()) {
            builder.addVertex(x, y, z);
            builder.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            builder.setNormal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
            u = sprite.getU(u);
            v = sprite.getV(v);
            builder.setUv(u, v);
        }
    }

    private float getU0(int index) {
        return switch (index) {
            case 1, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getU1(int index) {
        return switch (index) {
            case 1, 3 -> 1;
            default -> 0.5f;
        };
    }

    private float getV0(int index) {
        return switch (index) {
            case 2, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getV1(int index) {
        return switch (index) {
            case 2, 3 -> 1;
            default -> 0.5f;
        };
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return BakedQuad.FLAG_TRANSLUCENT;
    }

    @Override
    public @NotNull Material.Baked particleMaterial() {
        return this.glassSide;
    }

    public static class Connect {

        private final boolean[][][] connects = new boolean[3][3][3];
        private int face;

        int getFace(Direction face) {
            if (blocked(face)) {
                return -1;
            }
            return this.face;
        }

        void init(BlockPos pos) {
            this.face = Math.abs((pos.getX() ^ pos.getY() ^ pos.getZ()) % 3);
        }

        void set(int x, int y, int z) {
            this.connects[x + 1][y + 1][z + 1] = true;
        }

        int getIndex(Direction face, int corner) {
            if (blocked(face)) {
                return -1;
            }
            return switch (face) {
                case WEST, EAST: {
                    yield getIndexX(face, corner);
                }
                case DOWN, UP: {
                    yield getIndexY(face, corner);
                }
                case NORTH, SOUTH: {
                    yield getIndexZ(face, corner);
                }
            };
        }

        boolean blocked(Direction face) {
            var pos = face.getUnitVec3i().offset(1, 1, 1);
            return this.connects[pos.getX()][pos.getY()][pos.getZ()];
        }

        int getIndexX(Direction face, int corner) {
            int x = face.getStepX();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][1+x], this.connects[1][2][1], this.connects[1][2][1+x]);
                case RU -> getIndex(this.connects[1][1][1-x], this.connects[1][2][1], this.connects[1][2][1-x]);
                case LD -> getIndex(this.connects[1][1][1+x], this.connects[1][0][1], this.connects[1][0][1+x]);
                case RD -> getIndex(this.connects[1][1][1-x], this.connects[1][0][1], this.connects[1][0][1-x]);
                default -> -1;
            };
        }

        int getIndexZ(Direction face, int corner) {
            int z = face.getStepZ();
            return switch (corner) {
                case LU -> getIndex(this.connects[1-z][1][1], this.connects[1][2][1], this.connects[1-z][2][1]);
                case RU -> getIndex(this.connects[1+z][1][1], this.connects[1][2][1], this.connects[1+z][2][1]);
                case LD -> getIndex(this.connects[1-z][1][1], this.connects[1][0][1], this.connects[1-z][0][1]);
                case RD -> getIndex(this.connects[1+z][1][1], this.connects[1][0][1], this.connects[1+z][0][1]);
                default -> -1;
            };
        }

        int getIndexY(Direction face, int corner) {
            int y = face.getStepY();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][2], this.connects[1-y][1][1], this.connects[1-y][1][2]);
                case RU -> getIndex(this.connects[1][1][0], this.connects[1-y][1][1], this.connects[1-y][1][0]);
                case LD -> getIndex(this.connects[1][1][2], this.connects[1+y][1][1], this.connects[1+y][1][2]);
                case RD -> getIndex(this.connects[1][1][0], this.connects[1+y][1][1], this.connects[1+y][1][0]);
                default -> -1;
            };
        }

        /**
         * cbc <br>
         * axa <br>
         * cbc <br>
         */
        @SuppressWarnings("ConstantValue")
        int getIndex(boolean a, boolean b, boolean c) {
            if (!a && !b) {
                return 0;
            }
            if (a && b && !c) {
                return 1;
            }
            if (!a && b) {
                return 2;
            }
            if (a && !b) {
                return 3;
            }
            return -1;
        }

    }

    private record FaceCorner(Direction face, int corner) {

    }

}
