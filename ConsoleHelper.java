package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String text = "";
        while (true) {
            try {
                text = reader.readLine();
                break;
                
            } catch (IOException ex) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return text;
    }

    public static int readInt() {
        int x = 0;
        while (true) {
            try {
                x = Integer.parseInt(readString());
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
                            }
        }
        return x;
    }
}
