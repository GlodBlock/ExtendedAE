package com.github.glodblock.extendedae.client.model;

import appeng.client.render.model.DriveModelData;
import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExDriveBakedModel extends ForwardingBakedModel implements FabricBakedModel {
    private final Map<Item, BakedModel> cellModels;
    private final Map<Item, Mesh> bakedCells;
    private final BakedModel defaultCellModel;
    private final Mesh defaultCell;

    private final RenderContext.QuadTransform[] slotTransforms;

    public ExDriveBakedModel(Transformation rotation, BakedModel bakedBase, Map<Item, BakedModel> cellModels,
                           BakedModel defaultCell) {
        this.wrapped = bakedBase;
        this.defaultCellModel = defaultCell;
        this.slotTransforms = buildSlotTransforms(rotation);
        this.cellModels = cellModels;
        this.bakedCells = convertCellModels(cellModels);
        this.defaultCell = convertCellModel(defaultCell);
    }

    /**
     * Calculates the origin of a drive slot for positioning a cell model into it.
     */
    public static void getSlotOrigin(int row, int col, int disk, Vector3f translation) {
        // Position this drive model copy at the correct slot. The transform is based on
        // the cell-model being in slot 0,0,0 while the upper left slot's origin is at
        // 9,13,1
        float xOffset = (9 - col * 8) / 16.0f;
        float yOffset = (13 - row * 3) / 16.0f;
        float zOffset = disk == 0 ? (1 / 16.0f) : (10 / 16.0f);
        translation.set(xOffset, yOffset, zOffset);
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        Item[] cells = getCells(blockView, pos);

        // Add cell models on top of the base model, if possible
        if (cells != null) {
            for (int disk = 0; disk < 2; disk++) {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 2; col++) {
                        int slot = getSlotIndex(row, col, disk);
                        // Add the cell chassis
                        Item cell = slot < cells.length ? cells[slot] : null;
                        BakedModel cellChassisModel = getCellChassisModel(cell);
                        context.pushTransform(slotTransforms[slot]);
                        context.bakedModelConsumer().accept(cellChassisModel, null);
                        context.meshConsumer().accept(getCellChassisMesh(cell));
                        context.popTransform();
                    }
                }
            }
        }
    }

    private static Item[] getCells(BlockAndTintGetter blockView, BlockPos pos) {
        if (!(blockView instanceof RenderAttachedBlockView)) {
            return null;
        }
        Object attachedData = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
        if (!(attachedData instanceof DriveModelData)) {
            return null;
        }

        return ((DriveModelData) attachedData).getCells();
    }

    @Override
    public boolean useAmbientOcclusion() {
        // We have faces inside the chassis that are facing east, but should not receive
        // ambient occlusion from the east-side, but sadly this cannot be fine-tuned on
        // a face-by-face basis.
        return false;
    }

    // Determine which drive chassis to show based on the used cell
    public BakedModel getCellChassisModel(Item cell) {
        if (cell == null) {
            return cellModels.get(Items.AIR);
        }
        final BakedModel model = cellModels.get(cell);

        return model != null ? model : defaultCellModel;
    }

    public Mesh getCellChassisMesh(Item cell) {
        if (cell == null) {
            return bakedCells.get(Items.AIR);
        }
        final Mesh model = bakedCells.get(cell);
        return model != null ? model : defaultCell;
    }

    private RenderContext.QuadTransform[] buildSlotTransforms(Transformation rotation) {
        RenderContext.QuadTransform[] result = new RenderContext.QuadTransform[5 * 2 * 2];
        for (int disk = 0; disk < 2; disk++) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 2; col++) {

                    Vector3f translation = new Vector3f();
                    getSlotOrigin(row, col, disk, translation);
                    rotation.getLeftRotation().transform(translation);

                    result[getSlotIndex(row, col, disk)] = new ExDriveBakedModel.QuadTranslator(translation.x(), translation.y(),
                            translation.z());
                }
            }
        }

        return result;
    }

    private static int getSlotIndex(int row, int col, int disk) {
        return row * 2 + col + disk * 10;
    }

    private Map<Item, Mesh> convertCellModels(Map<Item, BakedModel> cellModels) {
        Map<Item, Mesh> result = new IdentityHashMap<>();

        for (Map.Entry<Item, BakedModel> entry : cellModels.entrySet()) {
            result.put(entry.getKey(), convertCellModel(entry.getValue()));
        }

        return result;
    }

    private Mesh convertCellModel(BakedModel bakedModel) {
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        RandomSource random = RandomSource.create();
        assert renderer != null;
        MeshBuilder meshBuilder = renderer.meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();
        emitter.material(renderer.materialFinder().disableDiffuse(false).ambientOcclusion(TriState.FALSE).find());

        for (int i = 0; i <= ModelHelper.NULL_FACE_ID; i++) {
            Direction face = ModelHelper.faceFromIndex(i);
            List<BakedQuad> quads = bakedModel.getQuads(null, face, random);
            for (BakedQuad quad : quads) {
                emitter.fromVanilla(quad.getVertices(), 0);
                emitter.cullFace(face);
                emitter.nominalFace(face);
                emitter.emit();
            }
        }
        return meshBuilder.build();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    private record QuadTranslator(float x, float y, float z) implements RenderContext.QuadTransform {

        @Override
        public boolean transform(MutableQuadView quad) {
                Vector3f target = new Vector3f();
                for (int i = 0; i < 4; i++) {
                    quad.copyPos(i, target);
                    target.add(x, y, z);
                    quad.pos(i, target);
                }
                return true;
            }
        }
}
