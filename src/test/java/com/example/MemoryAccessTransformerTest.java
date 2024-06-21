package com.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.Method;

public class MemoryAccessTransformerTest {

  private static Class<?> transformedClass;

  @BeforeAll
  public static void setup() throws IOException {
    // Transform the TestClass
    MemoryAccessTransformer.transformClass("com.example.TestClass");

    // Load the transformed class
    try {
      transformedClass = Class.forName("com.example.TestClass");
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