package main.java.Utils;

import java.util.Scanner;

/**
 * Утилита для последовательного и корректного интерактивного ввода данных пользователем.
 */
public class InputHelper {
    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Читает строку с приглашением, проверяет, что строка не пустая если allowEmpty == false.
     * Если пустая, просит ввести заново.
     *
     * @param prompt     приглашение для пользователя
     * @param allowEmpty разрешить ли пустую строку (которая будет интерпретироваться как null)
     * @return введённая строка или null (если пустая и allowEmpty == true)
     */
    public String readString(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                if (allowEmpty) {
                    return null;
                } else {
                    System.out.println("Это поле не может быть пустым. Попробуйте снова.");
                }
            } else {
                return input;
            }
        }
    }

    /**
     * Читает целое число с приглашением.
     * Проверяет, что число положительное, если positiveOnly == true.
     *
     * @param prompt      приглашение
     * @param positiveOnly если true, значение должно быть больше 0
     * @return введённое число
     */
    public int readInt(String prompt, boolean positiveOnly) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (positiveOnly && val <= 0) {
                    System.out.println("Число должно быть положительным. Попробуйте снова.");
                } else {
                    return val;
                }
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное целое число.");
            }
        }
    }

    /**
     * Читает число типа long с приглашением.
     *
     * @param prompt приглашение
     * @return введённое число
     */
    public long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Введите корректное число.");
            }
        }
    }

    /**
     * Читает значение enum с приглашением.
     * Выводит список всех возможных значений.
     *
     * @param prompt    приглашение
     * @param enumClass класс enum
     * @param <E>       тип enum
     * @return выбранное значение enum
     */
    public <E extends Enum<E>> E readEnum(String prompt, Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        System.out.println("Возможные значения:");
        for (E constant : constants) {
            System.out.println(" - " + constant.name());
        }
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Введите одно из указанных значений.");
            }
        }
    }
}
