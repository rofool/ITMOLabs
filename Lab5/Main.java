package Lab5;

import Lab5.Storage.CollectionManager;
import Lab5.Model.*;
import Lab5.Utils.JsonHandler;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * Главный класс программы для управления коллекцией организаций.
 * Содержит основной метод для запуска программы и взаимодействия с пользователем через командный интерфейс.
 */
public class Main {

    /**
     * Основной метод программы, который запускает приложение.
     * Загружает данные из файла, предоставляет пользователю командный интерфейс для работы с коллекцией организаций.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        String filePath = System.getenv("ORGANIZATION_FILE");

        if (filePath == null) {
            System.err.println("Переменная окружения 'ORGANIZATION_FILE' не установлена.");
            return;
        }

        try {

            List<Organization> organizations = JsonHandler.loadFromFile(filePath);
            CollectionManager manager = new CollectionManager(organizations);

            System.out.println("Загружено организаций: " + manager.size());

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("\nВведите команду (show, add, exit): ");
                String command = scanner.nextLine().trim().toLowerCase();


                switch (command) {
                    case "show":
                        manager.show();
                        break;

                    case "add":

                        try {
                            System.out.print("Введите имя: ");
                            String name = scanner.nextLine();

                            System.out.print("Введите координату X (int): ");
                            int x = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите координату Y (long): ");
                            long y = Long.parseLong(scanner.nextLine());

                            System.out.print("Введите оборот (может быть пусто): ");
                            String turnoverInput = scanner.nextLine();
                            Integer turnover = turnoverInput.isEmpty() ? null : Integer.parseInt(turnoverInput);

                            System.out.print("Введите количество сотрудников (int): ");
                            int employees = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY): ");
                            OrganizationType type = OrganizationType.valueOf(scanner.nextLine().trim().toUpperCase());

                            System.out.print("Введите улицу (может быть пусто): ");
                            String street = scanner.nextLine();
                            street = street.isEmpty() ? null : street;

                            System.out.print("Введите ZIP-код: ");
                            String zipCode = scanner.nextLine();

                            Coordinates coordinates = new Coordinates(x, y);
                            Address address = new Address(street, zipCode);

                            Organization newOrg = new Organization(name, coordinates, turnover, employees, type, address);
                            manager.add(newOrg);
                        } catch (Exception e) {
                            System.out.println("Ошибка при добавлении организации: " + e.getMessage());
                        }
                        break;

                    case "save":

                        try {
                            JsonHandler.saveToFile(manager.getAsList(), filePath);
                            System.out.println("Коллекция сохранена в файл.");
                        } catch (Exception e) {
                            System.out.println("Ошибка при сохранении: " + e.getMessage());
                        }
                        break;

                    case "clear":
                        manager.clear();
                        System.out.println("Коллекция очищена.");
                        break;

                    case "info":
                        manager.info();
                        break;

                    case "help":

                        System.out.println("Список команд:");
                        System.out.println("  show — показать коллекцию");
                        System.out.println("  add — добавить новую организацию");
                        System.out.println("  save — сохранить коллекцию в файл");
                        System.out.println("  clear — очистить коллекцию");
                        System.out.println("  info — информация о коллекции");
                        System.out.println("  help — показать список команд");
                        System.out.println("  exit — выход из программы");
                        System.out.println("  add_if_max — добавить организацию, если она больше всех в коллекции");
                        System.out.println("  update id — обновить элемент по ID");
                        System.out.println("  remove_first — удалить первый элемент коллекции");
                        System.out.println("  remove_head — вывести и удалить первый элемент коллекции");
                        System.out.println("  add_if_min — добавить организацию, если она меньше всех");
                        System.out.println("  remove_any_by_employees_count employeesCount — удалить организацию с заданным количеством сотрудников");
                        System.out.println("  filter_by_annual_turnover value — вывести элементы с заданным annualTurnover");
                        System.out.println("  print_field_descending_type — вывести значения поля type в порядке убывания");
                        System.out.println("  execute_script file_name — выполнить команды из файла");
                        break;

                    case "remove_by_id":

                        try {
                            System.out.print("Введите ID для удаления: ");
                            long id = Long.parseLong(scanner.nextLine());

                            boolean removed = manager.removeById(id);
                            if (removed) {
                                System.out.println("Организация с ID " + id + " удалена.");
                            } else {
                                System.out.println("Организация с таким ID не найдена.");
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                        break;

                    case "add_if_max":

                        try {

                            System.out.print("Введите имя: ");
                            String name = scanner.nextLine();

                            System.out.print("Введите координату X (int): ");
                            int x = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите координату Y (long): ");
                            long y = Long.parseLong(scanner.nextLine());

                            System.out.print("Введите оборот (может быть пусто): ");
                            String turnoverInput = scanner.nextLine();
                            Integer turnover = turnoverInput.isEmpty() ? null : Integer.parseInt(turnoverInput);

                            System.out.print("Введите количество сотрудников (int): ");
                            int employees = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY): ");
                            OrganizationType type = OrganizationType.valueOf(scanner.nextLine().trim().toUpperCase());

                            System.out.print("Введите улицу (может быть пусто): ");
                            String street = scanner.nextLine();
                            street = street.isEmpty() ? null : street;

                            System.out.print("Введите ZIP-код: ");
                            String zipCode = scanner.nextLine();

                            Coordinates coordinates = new Coordinates(x, y);
                            Address address = new Address(street, zipCode);

                            Organization newOrg = new Organization(name, coordinates, turnover, employees, type, address);
                            manager.addIfMax(newOrg);
                        } catch (Exception e) {
                            System.out.println("Ошибка при добавлении организации: " + e.getMessage());
                        }
                        break;

                    case "update":

                        try {
                            System.out.print("Введите ID обновляемой организации: ");
                            long id = Long.parseLong(scanner.nextLine());

                            Organization oldOrg = manager.getById(id);

                            if (oldOrg == null) {
                                System.out.println("Организация с таким ID не найдена.");
                                break;
                            }


                            System.out.print("Введите имя: ");
                            String name = scanner.nextLine();

                            System.out.print("Введите координату X (int): ");
                            int x = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите координату Y (long): ");
                            long y = Long.parseLong(scanner.nextLine());

                            System.out.print("Введите оборот (может быть пусто): ");
                            String turnoverInput = scanner.nextLine();
                            Integer turnover = turnoverInput.isEmpty() ? null : Integer.parseInt(turnoverInput);

                            System.out.print("Введите количество сотрудников (int): ");
                            int employees = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY): ");
                            OrganizationType type = OrganizationType.valueOf(scanner.nextLine().trim().toUpperCase());

                            System.out.print("Введите улицу (может быть пусто): ");
                            String street = scanner.nextLine();
                            street = street.isEmpty() ? null : street;

                            System.out.print("Введите ZIP-код: ");
                            String zipCode = scanner.nextLine();

                            Coordinates coordinates = new Coordinates(x, y);
                            Address address = new Address(street, zipCode);

                            Organization updated = new Organization(
                                    oldOrg.getId(),
                                    oldOrg.getCreationDate(),
                                    name,
                                    coordinates,
                                    turnover,
                                    employees,
                                    type,
                                    address
                            );

                            if (manager.updateById(id, updated)) {
                                System.out.println("Организация обновлена.");
                            } else {
                                System.out.println("Ошибка при обновлении.");
                            }

                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                        break;

                    case "remove_first":
                        if (manager.removeFirst()) {
                            System.out.println("Первый элемент удалён.");
                        } else {
                            System.out.println("Коллекция пуста. Удалять нечего.");
                        }
                        break;

                    case "remove_head":
                        Organization head = manager.removeHead();
                        if (head != null) {
                            System.out.println("Удалённый элемент:");
                            System.out.println(head);
                        } else {
                            System.out.println("Коллекция пуста. Нечего удалять.");
                        }
                        break;

                    case "add_if_min":

                        try {

                            System.out.print("Введите имя: ");
                            String name = scanner.nextLine();

                            System.out.print("Введите координату X (int): ");
                            int x = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите координату Y (long): ");
                            long y = Long.parseLong(scanner.nextLine());

                            System.out.print("Введите оборот (может быть пусто): ");
                            String turnoverInput = scanner.nextLine();
                            Integer turnover = turnoverInput.isEmpty() ? null : Integer.parseInt(turnoverInput);

                            System.out.print("Введите количество сотрудников (int): ");
                            int employees = Integer.parseInt(scanner.nextLine());

                            System.out.print("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY): ");
                            OrganizationType type = OrganizationType.valueOf(scanner.nextLine().trim().toUpperCase());

                            System.out.print("Введите улицу (может быть пусто): ");
                            String street = scanner.nextLine();
                            street = street.isEmpty() ? null : street;

                            System.out.print("Введите ZIP-код: ");
                            String zipCode = scanner.nextLine();

                            Coordinates coordinates = new Coordinates(x, y);
                            Address address = new Address(street, zipCode);

                            Organization newOrg = new Organization(name, coordinates, turnover, employees, type, address);
                            manager.addIfMin(newOrg);
                        } catch (Exception e) {
                            System.out.println("Ошибка при добавлении организации: " + e.getMessage());
                        }
                        break;

                    case "remove_any_by_employees_count":

                        try {
                            System.out.print("Введите количество сотрудников: ");
                            int count = Integer.parseInt(scanner.nextLine());

                            boolean removed = manager.removeAnyByEmployeesCount(count);
                            if (removed) {
                                System.out.println("Организация с employeesCount = " + count + " удалена.");
                            } else {
                                System.out.println("Организация с таким количеством сотрудников не найдена.");
                            }
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                        break;

                    case "filter_by_annual_turnover":
                        try {
                            System.out.print("Введите значение annualTurnover: ");
                            int turnover = Integer.parseInt(scanner.nextLine());
                            manager.filterByAnnualTurnover(turnover);
                        } catch (Exception e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                        break;

                    case "print_field_descending_type":
                        manager.printFieldDescendingType();
                        break;

                    case "execute_script":
                        try {
                            System.out.print("Введите имя файла: ");
                            String fileName = scanner.nextLine().trim();
                            executeScript(fileName, manager);
                        } catch (Exception e) {
                            System.out.println("Ошибка при выполнении скрипта: " + e.getMessage());
                        }
                        break;

                    case "exit":

                        System.out.println("Завершение программы...");
                        return;

                    default:
                        System.out.println("Неизвестная команда.");
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }

    /**
     * Выполняет команды из указанного файла.
     *
     * @param filePath Путь к файлу с командами.
     * @param manager  Менеджер коллекции для выполнения команд.
     */
    private static void executeScript(String filePath, CollectionManager manager) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    System.out.println("Выполняется команда: " + line);
                    processCommand(line, manager);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает команду, переданную как строка.
     *
     * @param command Команда для выполнения.
     * @param manager Менеджер коллекции для выполнения команды.
     */
    private static void processCommand(String command, CollectionManager manager) {
        switch (command.toLowerCase()) {
            case "show":
                manager.show();
                break;
            case "clear":
                manager.clear();
                break;
            case "info":
                manager.info();
                break;
            case "save":
                try {
                    JsonHandler.saveToFile(manager.getAsList(), System.getenv("ORGANIZATION_FILE"));
                    System.out.println("Коллекция сохранена в файл.");
                } catch (Exception e) {
                    System.out.println("Ошибка при сохранении: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Неизвестная команда в скрипте: " + command);
                break;
        }
    }
}
