package laba1;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        // Указываем путь к основной папке
        String folderPath = "D:\\Proj\\ITMOLabs\\Lab5";

        // Указываем путь к выходному файлу
        String outputFile = "D:\\Proj\\ITMOLabs\\Lab5\\combined.txt";

        // Создаем объект для записи в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Получаем все .java файлы в указанной папке и её подпапках
            List<Path> javaFiles = getJavaFiles(Paths.get(folderPath));

            // Для каждого файла записываем его содержимое в выходной файл
            for (Path file : javaFiles) {
                writer.write("\n\n--- Start of " + file.toString() + " ---\n");
                List<String> content = Files.readAllLines(file);
                for (String line : content) {
                    writer.write(line + "\n");
                }
                writer.write("\n--- End of " + file.toString() + " ---\n");
            }

            System.out.println("Содержимое всех .java файлов собрано в " + outputFile);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файлов: " + e.getMessage());
        }
    }

    // Рекурсивно находит все .java файлы в папке и её подпапках
    private static List<Path> getJavaFiles(Path dir) throws IOException {
        List<Path> javaFiles = new ArrayList<>();

        Files.walk(dir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(javaFiles::add);

        return javaFiles;
    }
}
