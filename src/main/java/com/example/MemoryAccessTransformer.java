package com.example;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MemoryAccessTransformer extends ClassVisitor implements Opcodes {
  public MemoryAccessTransformer(ClassVisitor cv) {
    super(ASM9, cv);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    return new MethodVisitor(ASM9, mv) {
      @Override
      public void visitInsn(int opcode) {
        switch (opcode) {
          case IALOAD:
            // Replace IALOAD with PathORAM.read
            mv.visitInsn(SWAP); // Swap array reference and index
            mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "read", "(I)I", false);
            break;
          case IASTORE:
            // Replace IASTORE with PathORAM.write
            mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "write", "(II)V", false);
            break;
          default:
            super.visitInsn(opcode);
        }
      }
    };
  }
}