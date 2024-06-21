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
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readIntArray", "([II)I", false);
                break;
              case IASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeIntArray", "([III)V", false);
                break;
              case FALOAD:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readFloatArray", "([FI)F", false);
                break;
              case FASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeFloatArray", "([FIF)V", false);
                break;
              case AALOAD:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readObjectArray",
                    "([Ljava/lang/Object;I)Ljava/lang/Object;", false);
                mv.visitTypeInsn(CHECKCAST, "java/lang/String");
                break;
              case AASTORE:
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeObjectArray",
                    "([Ljava/lang/Object;ILjava/lang/Object;)V", false);
                break;
              default:
                super.visitInsn(opcode);
                break;
            }
          }

          @Override
          public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            switch (opcode) {
              case GETFIELD:
                // Push the field name onto the stack
                super.visitLdcInsn(name);
                switch (descriptor) {
                  case "I":
                  case "B":
                  case "C":
                  case "S":
                  case "Z":
                    super.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readIntField",
                        "(Ljava/lang/Object;Ljava/lang/String;)I", false);
                    break;
                  case "Ljava/lang/String;":
                    super.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readStringField",
                        "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;", false);
                    break;
                  default:
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }
                break;
              case PUTFIELD:
                // Push the field name onto the stack
                super.visitLdcInsn(name);
                // Swap the object reference and field name on the stack
                super.visitInsn(SWAP);
                // Switch on the type of the value
                switch (descriptor) {
                  case "I":
                  case "B":
                  case "C":
                  case "S":
                  case "Z":
                    super.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeIntField",
                        "(Ljava/lang/Object;Ljava/lang/String;I)V", false);
                    break;
                  case "Ljava/lang/String;":
                    super.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeStringField",
                        "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;)V", false);
                    break;
                  default:
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }
                break;
              default:
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }
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

  public static void transformClass(String className) throws IOException {
    String classPath = className.replace('.', '/') + ".class";
    byte[] classBytes = Files.readAllBytes(Paths.get("target/test-classes/" + classPath));
    ClassReader classReader = new ClassReader(classBytes);
    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
    ClassVisitor classVisitor = new ClassVisitor(ASM9, classWriter) {
      // ... existing visitor code ...
    };
    classReader.accept(classVisitor, 0);
    byte[] transformedClassBytes = classWriter.toByteArray();
    try (FileOutputStream fos = new FileOutputStream("target/test-classes/" + classPath)) {
      fos.write(transformedClassBytes);
    }
  }
}