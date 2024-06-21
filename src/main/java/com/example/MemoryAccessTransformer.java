package com.example;

import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class MemoryAccessTransformer {
  private static int totalArrayElements = 0;
  private static Set<String> uniqueFields = new HashSet<>();

  public static void transformClass(String className, byte[] classBytes) throws IOException {
    // First pass: count array elements and unique fields
    countArrayElementsAndFields(classBytes);

    // Calculate the total number of blocks needed
    int numBlocks = totalArrayElements + uniqueFields.size();

    // Initialize PathORAM with the counted number of blocks
    ORAMAccessHelper.initializeORAM(numBlocks);

    // Second pass: transform the class
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

  private static void countArrayElementsAndFields(byte[] classBytes) {
    ClassReader classReader = new ClassReader(classBytes);
    ClassVisitor classVisitor = new ClassVisitor(ASM9) {
      @Override
      public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        uniqueFields.add(name);
        return super.visitField(access, name, descriptor, signature, value);
      }

      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
          String[] exceptions) {
        return new MethodVisitor(ASM9) {
          private int stackSize = 0;

          @Override
          public void visitIntInsn(int opcode, int operand) {
            if (opcode == BIPUSH || opcode == SIPUSH) {
              stackSize = operand;
            } else if (opcode == NEWARRAY) {
              if (stackSize > 0) {
                totalArrayElements += stackSize;
                stackSize = 0;
              }
            }
            super.visitIntInsn(opcode, operand);
          }

          @Override
          public void visitTypeInsn(int opcode, String type) {
            if (opcode == ANEWARRAY) {
              if (stackSize > 0) {
                totalArrayElements += stackSize;
                stackSize = 0;
              }
            }
            super.visitTypeInsn(opcode, type);
          }

          @Override
          public void visitInsn(int opcode) {
            if (opcode >= ICONST_0 && opcode <= ICONST_5) {
              stackSize = opcode - ICONST_0;
            } else if (opcode == ICONST_M1) {
              stackSize = -1;
            }
            super.visitInsn(opcode);
          }

          @Override
          public void visitLdcInsn(Object value) {
            if (value instanceof Integer) {
              stackSize = (Integer) value;
            }
            super.visitLdcInsn(value);
          }

          @Override
          public void visitVarInsn(int opcode, int var) {
            if (opcode == ILOAD) {
              stackSize = -1; // We don't know the exact size, but it's on the stack
            }
            super.visitVarInsn(opcode, var);
          }

        };
      }
    };
    classReader.accept(classVisitor, 0);
  }
}