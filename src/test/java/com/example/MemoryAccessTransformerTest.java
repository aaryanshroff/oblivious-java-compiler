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
    MemoryAccessTransformer.transformClass(className, classBytes, "target/classes/" + classPath);

    // Load the transformed class
    try {
      transformedClass = Class.forName(className);
    } catch (ClassNotFoundException e) {
      fail("Failed to load transformed class: " + e.getMessage());
    }
  }

  @Test
  public void testIntArrayReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readIntArray");
    Method writeMethod = transformedClass.getMethod("writeIntArray", int.class);

    int initialValue = (int) readMethod.invoke(instance);
    assertEquals(52, initialValue);

    writeMethod.invoke(instance, 100);
    int newValue = (int) readMethod.invoke(instance);
    assertEquals(100, newValue);
  }

  @Test
  public void testStringArrayReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readStringArray");
    Method writeMethod = transformedClass.getMethod("writeStringArray", String.class);

    String initialValue = (String) readMethod.invoke(instance);
    assertEquals("Hello", initialValue);

    writeMethod.invoke(instance, "ORAM");
    String newValue = (String) readMethod.invoke(instance);
    assertEquals("ORAM", newValue);
  }

  @Test
  public void testIntFieldReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readIntField");
    Method writeMethod = transformedClass.getMethod("writeIntField", int.class);

    int initialValue = (int) readMethod.invoke(instance);
    assertEquals(100, initialValue);

    writeMethod.invoke(instance, 200);
    int newValue = (int) readMethod.invoke(instance);
    assertEquals(200, newValue);
  }

  @Test
  public void testStringFieldReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readStringField");
    Method writeMethod = transformedClass.getMethod("writeStringField", String.class);

    String initialValue = (String) readMethod.invoke(instance);
    assertEquals("Test", initialValue);

    writeMethod.invoke(instance, "ORAM");
    String newValue = (String) readMethod.invoke(instance);
    assertEquals("ORAM", newValue);
  }

  @Test
  public void testInt2DArrayReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readInt2DArray");
    Method writeMethod = transformedClass.getMethod("writeInt2DArray", int.class);

    int initialValue = (int) readMethod.invoke(instance);
    assertEquals(42, initialValue);

    writeMethod.invoke(instance, 99);
    int newValue = (int) readMethod.invoke(instance);
    assertEquals(99, newValue);
  }

  @Test
  public void testFloat2DArrayReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readFloat2DArray");
    Method writeMethod = transformedClass.getMethod("writeFloat2DArray", float.class);

    float initialValue = (float) readMethod.invoke(instance);
    assertEquals(3.14f, initialValue, 0.001f);

    writeMethod.invoke(instance, 2.718f);
    float newValue = (float) readMethod.invoke(instance);
    assertEquals(2.718f, newValue, 0.001f);
  }

  @Test
  public void testObject2DArrayReadWrite() throws Exception {
    Object instance = transformedClass.getDeclaredConstructor().newInstance();
    Method readMethod = transformedClass.getMethod("readObject2DArray");
    Method writeMethod = transformedClass.getMethod("writeObject2DArray", String.class);

    String initialValue = (String) readMethod.invoke(instance);
    assertEquals("ORAM", initialValue);

    writeMethod.invoke(instance, "Security");
    String newValue = (String) readMethod.invoke(instance);
    assertEquals("Security", newValue);
  }
}