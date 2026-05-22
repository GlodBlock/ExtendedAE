package com.glodblock.github.ae2netanalyser.client.render.buffer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class CachedRender {

    private final RenderPipeline pipeline;
    private final String name;

    private GpuBuffer VBO;
    private MeshData.DrawState state;

    public CachedRender(RenderPipeline pipeline, String name) {
        this.pipeline = pipeline;
        this.name = name;
    }

    public void upload(BufferBuilder buf) {
        if (this.VBO != null) {
            this.VBO.close();
        }
        try (var rendered = buf.build()) {
            if (rendered == null) {
                return;
            }
            this.state = rendered.drawState();
            this.VBO = RenderSystem.getDevice().createBuffer(() -> this.name + " VBO", 40, rendered.vertexBuffer());
        }
    }

    public void render(Matrix4f modifier) {
        var color = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        if (color == null || !this.ready()) {
            return;
        }
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        if (modifier != null) {
            modelViewStack.pushMatrix();
            modelViewStack.identity();
            RenderSystem.getModelViewStack().mul(modifier);
        }
        var dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), TextureTransform.DEFAULT_TEXTURING.getMatrix());
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> this.name, color, OptionalInt.empty(), Minecraft.getInstance().getMainRenderTarget().getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(this.pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", dynamicTransforms);
            renderPass.setVertexBuffer(0, this.VBO);
            var autoIndices = RenderSystem.getSequentialBuffer(this.state.mode());
            var indices = autoIndices.getBuffer(this.state.indexCount());
            var indexType = autoIndices.type();
            renderPass.setIndexBuffer(indices, indexType);
            renderPass.drawIndexed(0, 0, this.state.indexCount(), 1);
        }
        if (modifier != null) {
            modelViewStack.popMatrix();
        }
    }

    public boolean ready() {
        return this.VBO != null && !this.VBO.isClosed() && this.state != null;
    }

}
