package com.example;

import java.nio.ByteBuffer;
import java.io.*;
import java.util.Optional;
import java.util.function.Function;

public class ORAMAccessHelper {
  private static PathORAM oram; // Assuming PathORAM has a no-arg constructor

  public static void initializeORAM(int numBlocks) {
    oram = new PathORAM(numBlocks);
  }

  private static String getBlockId(Object obj, String identifier) {
    return obj.hashCode() + "_" + identifier;
  }

  private static <T> T readValue(Object obj, String identifier, Function<byte[], T> decoder, T defaultValue) {
    String blockId = getBlockId(obj, identifier);
    Optional<byte[]> data = oram.access(blockId, Optional.empty(), false);
    return data.map(decoder).orElse(defaultValue);
  }

  private static void writeValue(Object obj, String identifier, byte[] bytes) {
    String blockId = getBlockId(obj, identifier);
    oram.access(blockId, Optional.of(bytes), true);
  }

  // Array access methods
  public static int readIntArray(int[] array, int index) {
    return readValue(array, String.valueOf(index), bytes -> ByteBuffer.wrap(bytes).getInt(), 0);
  }

  public static void writeIntArray(int[] array, int index, int value) {
    writeValue(array, String.valueOf(index), ByteBuffer.allocate(4).putInt(value).array());
  }

  public static float readFloatArray(float[] array, int index) {
    return readValue(array, String.valueOf(index), bytes -> ByteBuffer.wrap(bytes).getFloat(), 0f);
  }

  public static void writeFloatArray(float[] array, int index, float value) {
    writeValue(array, String.valueOf(index), ByteBuffer.allocate(4).putFloat(value).array());
  }

  public static Object readObjectArray(Object[] array, int index) {
    return readValue(array, String.valueOf(index), ORAMAccessHelper::deserializeObject, null);
  }

  public static void writeObjectArray(Object[] array, int index, Object value) {
    writeValue(array, String.valueOf(index), serializeObject(value));
  }

  // Field access methods
  public static int readIntField(Object obj, String fieldName) {
    return readValue(obj, fieldName, bytes -> ByteBuffer.wrap(bytes).getInt(), 0);
  }

  public static void writeIntField(Object obj, String fieldName, int value) {
    writeValue(obj, fieldName, ByteBuffer.allocate(4).putInt(value).array());
  }

  public static String readStringField(Object obj, String fieldName) {
    return readValue(obj, fieldName, String::new, null);
  }

  public static void writeStringField(Object obj, String fieldName, String value) {
    writeValue(obj, fieldName, value != null ? value.getBytes() : new byte[0]);
  }

  private static byte[] serializeObject(Object obj) {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(obj);
      return bos.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to serialize object", e);
    }
  }

  private static Object deserializeObject(byte[] bytes) {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis)) {
      return ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Failed to deserialize object", e);
    }
  }
}