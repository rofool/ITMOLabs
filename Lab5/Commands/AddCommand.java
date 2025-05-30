package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Model.*;

/**
 * Команда для добавления новой организации в коллекцию.
 * Парсит аргументы, создает объект Organization и добавляет его через CollectionManager.
 */
public class AddCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции, через который добавляются организации.
     */
    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду добавления организации.
     * Ожидает аргументы в следующем порядке:
     * name, x, y, annualTurnover, employeesCount, type, street, zipCode.
     *
     * @param args Аргументы команды для создания организации.
     */
    @Override
    public void execute(String[] args) {
        try {
            String name = args[0];
            int x = Integer.parseInt(args[1]);
            long y = Long.parseLong(args[2]);
            Integer annualTurnover = args[3].isEmpty() ? null : Integer.parseInt(args[3]);
            int employeesCount = Integer.parseInt(args[4]);
            OrganizationType type = OrganizationType.valueOf(args[5].toUpperCase());
            String street = args[6].isEmpty() ? null : args[6];
            String zipCode = args[7];

            Coordinates coordinates = new Coordinates(x, y);
            Address address = new Address(street, zipCode);
            Organization org = new Organization(name, coordinates, annualTurnover, employeesCount, type, address);

            collectionManager.add(org);
            System.out.println("Организация добавлена.");
        } catch (Exception e) {
            System.out.println("Ошибка при добавлении организации: " + e.getMessage());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Добавить новый элемент в коллекцию";
    }
}
