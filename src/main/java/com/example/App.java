package com.example;

public class App {
  public static void main(String[] args) {
    int[] array = new int[10];
    array[0] = 1;
    array[1] = array[0];
    array[2] = 5;
    System.out.println(array[0]);
    System.out.println(array[1]);
    System.out.println(array[2]);
  }
}
