package com.example;

public class PathORAM {
    public static int read(int[] arrayref, int index) {
        System.out.println("Path ORAM read at address: " + index);
        return arrayref[index];
    }

    public static void write(int[] arrayref, int index, int value) {
        System.out.println("Path ORAM write at address: " + index + " with value: " + value);
        arrayref[index] = value;
    }
}