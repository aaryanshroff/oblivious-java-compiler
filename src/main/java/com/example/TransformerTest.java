package com.example;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TransformerTest {
  public static void main(String[] args) throws IOException {
    String className = "com/example/TargetClass";
    String classFilePath = "target/classes/" + className.replace('.', '/') + ".class";

    try (FileInputStream fis = new FileInputStream(classFilePath)) {
      ClassReader classReader = new ClassReader(fis);
      ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
      MemoryAccessTransformer transformer = new MemoryAccessTransformer(classWriter);

      classReader.accept(transformer, 0);

      byte[] transformedClass = classWriter.toByteArray();

      // Save the transformed class to a file
      try (FileOutputStream fos = new FileOutputStream("target/classes/" + className + ".class")) {
        fos.write(transformedClass);
      }
    }
  }
}