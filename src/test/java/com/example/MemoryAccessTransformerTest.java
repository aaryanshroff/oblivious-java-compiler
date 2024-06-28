package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MemoryAccessTransformerTest {

  private static Class<?> transformedClass;

  @BeforeAll
  public static void setup() throws Exception {
    String className = "com.example.TestClass";
    String classPath = className.replace('.', '/') + ".class";
    byte[] classBytes = Files.readAllBytes(Paths.get("target/test-classes/" + classPath));

    // Transform the TestClass
    MemoryAccessTransformer.transformClass(className, classBytes);

    // Load the transformed class
    try {
      transformedClass = Class.forName(className);
    } catch (ClassNotFoundException e) {
      fail("Failed to load transformed class: " + e.getMessage());
    }
  }

  @Test
  public void testIntArrayTransformation() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method method = transformedClass.getMethod("testIntArray");
    int result = (int) method.invoke(instance);
    assertEquals(52, result);
  }

  @Test
  public void testStringArrayTransformation() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method method = transformedClass.getMethod("testStringArray");
    String result = (String) method.invoke(instance);
    assertEquals("Hello World", result);
  }

  @Test
  public void testIntFieldTransformation() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method method = transformedClass.getMethod("testIntField");
    int result = (int) method.invoke(instance);
    assertEquals(100, result);
  }

  @Test
  public void testStringFieldTransformation() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method method = transformedClass.getMethod("testStringField");
    String result = (String) method.invoke(instance);
    assertEquals("Test", result);
  }
}