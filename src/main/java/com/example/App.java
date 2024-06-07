package com.example;

public class App {
  public static void main(String[] args) {
    int[] array = new int[0];
    int[] array2 = new int[0];
    String[] stringArray = new String[5];
    stringArray[1] = "Hello world";
    array[0] = 1;
    array[1] = array[0];
    array[2] = 5;
    array2[0] = 25;
    System.out.println(array2[0]);
    System.out.println(array[0]);
    System.out.println(array[1]);
    System.out.println(array[2]);
    System.out.println(stringArray[1]);
  }
}
