package com.example;

public class TestClass {
  public int intField;
  public String stringField;

  public int testIntArray() {
    int[] array = new int[3];
    array[0] = 42;
    array[1] = 10;
    return array[0] + array[1];
  }

  public String testStringArray() {
    String[] array = new String[2];
    array[0] = "Hello";
    array[1] = "World";
    return array[0] + " " + array[1];
  }

  public int testIntField() {
    intField = 100;
    return intField;
  }

  public String testStringField() {
    stringField = "Test";
    return stringField;
  }
}