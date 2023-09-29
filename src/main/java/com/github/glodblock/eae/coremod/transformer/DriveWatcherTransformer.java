package com.github.glodblock.eae.coremod.transformer;

import com.github.glodblock.eae.coremod.EAEClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DriveWatcherTransformer extends EAEClassTransformer.ClassMapper {

    public static final DriveWatcherTransformer INSTANCE = new DriveWatcherTransformer();

    private DriveWatcherTransformer() {
        // NO-OP
    }

    @Override
    protected ClassVisitor getClassMapper(ClassVisitor downstream) {
        return new TransformDriveWatcher(Opcodes.ASM5, downstream);
    }

    private static class TransformDriveWatcher extends ClassVisitor {

        public TransformDriveWatcher(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ("injectItems".equals(name) || "extractItems".equals(name)) {
                return new TransformHandlerCheck(api, super.visitMethod(access, name, desc, signature, exceptions));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

    private static class TransformHandlerCheck extends MethodVisitor {

        public TransformHandlerCheck(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.INSTANCEOF) {
                super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/github/glodblock/eae/coremod/CoreHooks",
                        "isCreativeHandler",
                        "(Lappeng/api/storage/ICellHandler;)Z",
                        false);
            } else {
                super.visitTypeInsn(opcode, type);
            }
        }

    }

}
