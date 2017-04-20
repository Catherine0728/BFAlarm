package com.example;

public class MyClass {

    public static void main(String[] arg) {
        for (int i = 1; i < 301; i++) {
            System.out.println(" <dimen name=\"d" + i + "\">" + String.format("%.1f", (i * 1.4)) + "dp</dimen>");
        }
        for (int i = 6; i < 52; i++) {
            System.out.println(" <dimen name=\"text_size_" + i + "\">" + String.format("%.1f", (i * 1.4)) + "sp</dimen>");
        }
    }
}
