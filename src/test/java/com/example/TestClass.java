package com.example;

public class TestClass {
  private int intField = 100;
  private String stringField = "Test";
  private int[] intArray = new int[] { 50, 52, 54 };
  private String[] stringArray = new String[] { "Hello", "World" };
  private int[][] int2DArray = new int[][] { { 40, 41 }, { 42, 43 } };
  private float[][] float2DArray = new float[][] { { 1.1f, 2.2f }, { 3.14f, 4.4f } };
  private String[][] object2DArray = new String[][] { { "Java", "Python" }, { "ORAM", "Security" } };

  public int readIntArray() {
    return intArray[1];
  }

  public void writeIntArray(int value) {
    intArray[1] = value;
  }

  public String readStringArray() {
    return stringArray[0];
  }

  public void writeStringArray(String value) {
    stringArray[0] = value;
  }

  public int readIntField() {
    return intField;
  }

  public void writeIntField(int value) {
    intField = value;
  }

  public String readStringField() {
    return stringField;
  }

  public void writeStringField(String value) {
    stringField = value;
  }

  public int readInt2DArray() {
    return int2DArray[1][0];
  }

  public void writeInt2DArray(int value) {
    int2DArray[1][0] = value;
  }

  public float readFloat2DArray() {
    return float2DArray[1][0];
  }

  public void writeFloat2DArray(float value) {
    float2DArray[1][0] = value;
  }

  public String readObject2DArray() {
    return object2DArray[1][0];
  }

  public void writeObject2DArray(String value) {
    object2DArray[1][0] = value;
  }
}