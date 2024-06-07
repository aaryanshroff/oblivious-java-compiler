package com.example;

import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;

public class MemoryAccessTransformer {
  public static void main(String[] args) throws IOException {
    String className = "com/example/App";
    byte[] classBytes = Files.readAllBytes(Paths.get("target/classes/" + className.replace('.', '/') + ".class"));
    ClassReader classReader = new ClassReader(classBytes);
    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
    ClassVisitor classVisitor = new ClassVisitor(ASM9, classWriter) {
      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
          String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(ASM9, mv) {
          @Override
          public void visitInsn(int opcode) {
            switch (opcode) {
              case IALOAD:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "readIntArray", "([II)I", false);
                break;
              case IASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "writeIntArray", "([III)V", false);
                break;
              case FALOAD:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "readFloatArray", "([FI)F", false);
                break;
              case FASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "writeFloatArray", "([FIF)V", false);
                break;
              // Add cases for other types, e.g., LALOAD, LASTORE, etc.
              case AALOAD:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "readObjectArray",
                    "([Ljava/lang/Object;I)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                break;
              case AASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/PathORAM", "writeObjectArray",
                    "([Ljava/lang/Object;ILjava/lang/Object;)V", false);
                break;
              default:
                super.visitInsn(opcode);
                break;
            }
          }

          @Override
          public void visitIntInsn(int opcode, int operand) {
            // Handle array initialization opcodes
            if (opcode == Opcodes.NEWARRAY || opcode == Opcodes.ANEWARRAY) {
              // Push 0 onto the stack for array size
              super.visitInsn(Opcodes.ICONST_0);
              // Call the appropriate array creation instruction
              super.visitIntInsn(opcode, operand);
            } else {
              super.visitIntInsn(opcode, operand);
            }
          }

          @Override
          public void visitTypeInsn(int opcode, String type) {
            if (opcode == Opcodes.ANEWARRAY) {
              // Push 0 onto the stack for array size
              super.visitInsn(Opcodes.ICONST_0);
            }
            super.visitTypeInsn(opcode, type);
          }

        };
      }
    };
    classReader.accept(classVisitor, 0);
    byte[] transformedClassBytes = classWriter.toByteArray();
    try (FileOutputStream fos = new FileOutputStream("target/classes/" + className.replace('.', '/') + ".class")) {
      fos.write(transformedClassBytes);
    }
  }
}