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
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/example/PathORAM", "read", "([II)I", false);
                break;
              default:
                super.visitInsn(opcode);
                break;
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
}