package com.github.glodblock.eae.coremod;

import com.github.glodblock.eae.coremod.transformer.DriveWatcherTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class EAEClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] code) {
        Transform tform = null;
        if (name.equals("appeng.me.storage.DriveWatcher")) {
            tform = DriveWatcherTransformer.INSTANCE;
        }
        if (tform != null) {
            return tform.transformClass(code);
        }
        return code;
    }

    public interface Transform {

        byte[] transformClass(byte[] code);

    }

    public static abstract class ClassMapper implements Transform {

        @Override
        public byte[] transformClass(byte[] code) {
            ClassReader reader = new ClassReader(code);
            ClassWriter writer = new ClassWriter(reader, getWriteFlags());
            reader.accept(getClassMapper(writer), 0);
            return writer.toByteArray();
        }

        protected int getWriteFlags() {
            return 0;
        }

        protected abstract ClassVisitor getClassMapper(ClassVisitor downstream);

    }

}
