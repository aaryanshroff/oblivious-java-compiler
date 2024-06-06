package com.example;

public class TargetClass {
    public void exampleMethod() {
        int[] array = new int[10];
        array[0] = 1; // This should be replaced with PathORAM.write
        int value = array[0]; // This should be replaced with PathORAM.read
    }
}