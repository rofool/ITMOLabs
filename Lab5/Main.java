//package Lab5;
//
//import Lab5.Commands.CommandManager;
//import Lab5.Model.Organization;
//import Lab5.Storage.CollectionManager;
//import Lab5.Utils.JsonHandler;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Основной класс приложения для управления коллекцией организаций.
// * Загружает коллекцию из файла, затем принимает команды пользователя через консоль.
// */
//public class Main {
//
//    /**
//     * Точка входа в программу.
//     * Загружает данные из файла, путь к которому задаётся через переменную окружения ORGANIZATION_FILE,
//     * после чего запускает цикл обработки команд пользователя.
//     *
//     * @param args аргументы командной строки (не используются).
//     */
//    public static void main(String[] args) {
//        String filePath = System.getenv("ORGANIZATION_FILE");
//        if (filePath == null) {
//            System.err.println("Переменная окружения 'ORGANIZATION_FILE' не установлена.");
//            return;
//        }
//
//        List<Organization> loadedOrganizations = Collections.emptyList();
//        CollectionManager collectionManager;
//
//        try {
//            loadedOrganizations = JsonHandler.loadFromFile(filePath);
//            collectionManager = new CollectionManager(loadedOrganizations);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Ошибка загрузки коллекции: " + e.getMessage());
//            return;
//        } catch (IOException e) {
//            System.err.println("Ошибка при чтении файла: " + e.getMessage());
//            return;
//        }
//
//        CommandManager commandManager = new CommandManager(collectionManager, filePath);
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Введите команду (help для справки):");
//
//        while (true) {
//            System.out.print("> ");
//            String inputLine = scanner.nextLine().trim();
//            if (inputLine.isEmpty()) {
//                continue;
//            }
//            String[] parts = inputLine.split("\\s+");
//            String commandName = parts[0];
//            String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
//
//            boolean success =Boolean.parseBoolean(commandManager.executeCommand(commandName, commandArgs));
//            if (!success) {
//                System.out.println("Попробуйте ввести 'help' для списка доступных команд.");
//            }
//        }
//    }
//}
