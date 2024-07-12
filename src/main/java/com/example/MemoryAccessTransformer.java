package com.example;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class MemoryAccessTransformer {
  private static int totalArrayElements = 0;
  private static Set<String> uniqueFields = new HashSet<>();

  public static void main(String[] args) {
    if (args.length != 3) {
      System.out.println("Usage: java MemoryAccessTransformer <className> <classFilePath> <outputPath>");
      System.exit(1);
    }

    String className = args[0];
    String classFilePath = args[1];
    String outputPath = args[2];

    System.out.println(args);

    try {
      byte[] classBytes = Files.readAllBytes(Paths.get(classFilePath));
      MemoryAccessTransformer.transformClass(className, classBytes, outputPath);
      System.out.println("Class " + className + " transformed and saved to " + outputPath);
    } catch (Exception e) {
      System.err.println("Error transforming class: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void transformClass(String className, byte[] classBytes, String outputPath) throws IOException {
    // First pass: count array elements and unique fields
    countArrayElementsAndFields(classBytes);

    // Calculate the total number of blocks needed
    int numBlocks = totalArrayElements + uniqueFields.size();
    System.out.println("Num blocks " + numBlocks);

    // Initialize PathORAM with the counted number of blocks
    ORAMAccessHelper.initializeORAM(numBlocks);

    // Second pass: transform the class
    ClassReader classReader = new ClassReader(classBytes);
    ClassWriter classWriter = new ClassWriter(classReader, 0);
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
                // Check if it's a 2D array access
                mv.visitInsn(DUP2);
                mv.visitTypeInsn(INSTANCEOF, "[[I");
                Label labelIALOAD = new Label();
                mv.visitJumpInsn(IFEQ, labelIALOAD);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readInt2DArray", "([[III)I", false);
                mv.visitInsn(GOTO);
                Label endIALOAD = new Label();
                mv.visitLabel(endIALOAD);
                mv.visitJumpInsn(GOTO, endIALOAD);
                // 1D array access
                mv.visitLabel(labelIALOAD);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readIntArray", "([II)I", false);
                mv.visitLabel(endIALOAD);
                break;
              case IASTORE:
                // Check if it's a 2D array access
                mv.visitInsn(DUP2_X1);
                mv.visitTypeInsn(INSTANCEOF, "[[I");
                Label labelIASTORE = new Label();
                mv.visitJumpInsn(IFEQ, labelIASTORE);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeInt2DArray", "([[IIII)V", false);
                mv.visitInsn(GOTO);
                Label endIASTORE = new Label();
                mv.visitLabel(endIASTORE);
                mv.visitJumpInsn(GOTO, endIASTORE);
                // 1D array access
                mv.visitLabel(labelIASTORE);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeIntArray", "([III)V", false);
                mv.visitLabel(endIASTORE);
                break;
              case FALOAD:
                // Check if it's a 2D array access
                mv.visitInsn(DUP2);
                mv.visitTypeInsn(INSTANCEOF, "[[F");
                Label labelFALOAD = new Label();
                mv.visitJumpInsn(IFEQ, labelFALOAD);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readFloat2DArray", "([[FII)F", false);
                mv.visitInsn(GOTO);
                Label endFALOAD = new Label();
                mv.visitLabel(endFALOAD);
                mv.visitJumpInsn(GOTO, endFALOAD);
                // 1D array access
                mv.visitLabel(labelFALOAD);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readFloatArray", "([FI)F", false);
                mv.visitLabel(endFALOAD);
                break;
              case FASTORE:
                // Check if it's a 2D array access
                mv.visitInsn(DUP2_X1);
                mv.visitTypeInsn(INSTANCEOF, "[[F");
                Label labelFASTORE = new Label();
                mv.visitJumpInsn(IFEQ, labelFASTORE);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeFloat2DArray", "([[FIIF)V",
                    false);
                mv.visitInsn(GOTO);
                Label endFASTORE = new Label();
                mv.visitLabel(endFASTORE);
                mv.visitJumpInsn(GOTO, endFASTORE);
                // 1D array access
                mv.visitLabel(labelFASTORE);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeFloatArray", "([FIF)V", false);
                mv.visitLabel(endFASTORE);
                break;
              case AALOAD:
                // Check if it's a 2D array access
                mv.visitInsn(DUP2);
                mv.visitTypeInsn(INSTANCEOF, "[[Ljava/lang/Object;");
                Label labelAALOAD = new Label();
                mv.visitJumpInsn(IFEQ, labelAALOAD);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readObject2DArray",
                    "([[Ljava/lang/Object;II)Ljava/lang/Object;", false);
                mv.visitInsn(GOTO);
                Label endAALOAD = new Label();
                mv.visitLabel(endAALOAD);
                mv.visitJumpInsn(GOTO, endAALOAD);
                // 1D array access
                mv.visitLabel(labelAALOAD);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "readObjectArray",
                    "([Ljava/lang/Object;I)Ljava/lang/Object;", false);
                mv.visitLabel(endAALOAD);
                break;
              case AASTORE:
                // Check if it's a 2D array access
                mv.visitInsn(DUP2_X1);
                mv.visitTypeInsn(INSTANCEOF, "[[Ljava/lang/Object;");
                Label labelAASTORE = new Label();
                mv.visitJumpInsn(IFEQ, labelAASTORE);
                // 2D array access
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeObject2DArray",
                    "([[Ljava/lang/Object;IILjava/lang/Object;)V", false);
                mv.visitInsn(GOTO);
                Label endAASTORE = new Label();
                mv.visitLabel(endAASTORE);
                mv.visitJumpInsn(GOTO, endAASTORE);
                // 1D array access
                mv.visitLabel(labelAASTORE);
                mv.visitMethodInsn(INVOKESTATIC, "com/example/ORAMAccessHelper", "writeObjectArray",
                    "([Ljava/lang/Object;ILjava/lang/Object;)V", false);
                mv.visitLabel(endAASTORE);
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
    File file = new File(outputPath);
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(transformedClassBytes);
    }
  }

  private static void countArrayElementsAndFields(byte[] classBytes) {
    ClassReader classReader = new ClassReader(classBytes);
    ClassVisitor classVisitor = new ClassVisitor(ASM9) {
      @Override
      public FieldVisitor visitField(int access, String name, String descriptor,
          String signature, Object value) {
        uniqueFields.add(name);
        return super.visitField(access, name, descriptor, signature, value);
      }

      @Override
      public MethodVisitor visitMethod(int access, String name, String descriptor,
          String signature,
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

          // TODO: Multi-dimensional arrays
        };
      }
    };
    classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
  }
}