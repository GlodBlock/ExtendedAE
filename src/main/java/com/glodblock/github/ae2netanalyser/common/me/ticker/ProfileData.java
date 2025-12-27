package com.glodblock.github.ae2netanalyser.common.me.ticker;

import com.glodblock.github.ae2netanalyser.AEAnalyser;
import com.glodblock.github.glodium.client.render.ColorData;
import com.glodblock.github.glodium.util.GlodUtil;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ProfileData {

    public ATick[] ticks;
    private boolean isCorrupt;

    public ProfileData() {
        this.isCorrupt = false;
    }

    public ProfileData(ATick[] ticks) {
        this.ticks = ticks;
        this.isCorrupt = false;
    }

    public boolean isCorrupt() {
        return isCorrupt;
    }

    public static ProfileData readBytes(FriendlyByteBuf buf) {
        var data = new ProfileData();
        try (var stream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteBufInputStream(buf))))) {
            int len = stream.readInt();
            data.ticks = new ATick[len];
            for (int i = 0; i < len; i ++) {
                var dim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(stream.readUTF()));
                var pos = BlockPos.of(stream.readLong());
                var rate = stream.readDouble();
                data.ticks[i] = new ATick(new GlobalPos(dim, pos), rate, getColor(rate));
            }
        } catch (Exception e) {
            AEAnalyser.LOGGER.error("Fail to profile ticks. The packet is corrupted!", e);
            data.isCorrupt = true;
        }
        // clear corrupted byte
        buf.clear();
        return data;
    }

    public static ColorData getColor(double rate) {
        float gradient = (float) GlodUtil.clamp(rate / 100.0, 0.0, 1.0);
        return new ColorData((float) GlodUtil.clamp(gradient, 0.07, 0.7), gradient, 1 - gradient, 0);
    }

    public void writeBytes(FriendlyByteBuf buf) {
        try (var stream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(new ByteBufOutputStream(buf))))) {
            stream.writeInt(this.ticks.length);
            for (var t : this.ticks) {
                var dim = t.pos.dimension().location();
                var pos = t.pos.pos();
                var rate = t.rate();
                stream.writeUTF(dim.toString());
                stream.writeLong(pos.asLong());
                stream.writeDouble(rate);
            }
        } catch (Exception e) {
            AEAnalyser.LOGGER.error("Fail to profile ticks. The packet is corrupted!", e);
            this.isCorrupt = true;
        }
    }

    public record ATick(GlobalPos pos, double rate, ColorData color) {

        public ATick(GlobalPos pos, double rate) {
            this(pos, rate, new ColorData(1, 1, 1));
        }

    }

}
