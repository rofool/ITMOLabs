package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Model.*;

/**
 * Команда для обновления существующей организации в коллекции по её ID.
 * Обновляет все поля, кроме ID и даты создания.
 */
public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет обновление организации по заданному ID.
     * Ожидает аргументы:
     * id, name, x, y, annualTurnover, employeesCount, type, street, zipCode.
     *
     * @param args Аргументы команды.
     */
    @Override
    public void execute(String[] args) {
        try {
            long id = Long.parseLong(args[0]);
            Organization oldOrg = collectionManager.getById(id);

            if (oldOrg == null) {
                System.out.println("Организация с таким ID не найдена.");
                return;
            }

            String name = args[1];
            int x = Integer.parseInt(args[2]);
            long y = Long.parseLong(args[3]);
            Integer annualTurnover = args[4].isEmpty() ? null : Integer.parseInt(args[4]);
            int employeesCount = Integer.parseInt(args[5]);
            OrganizationType type = OrganizationType.valueOf(args[6].toUpperCase());
            String street = args[7].isEmpty() ? null : args[7];
            String zipCode = args[8];

            Coordinates coordinates = new Coordinates(x, y);
            Address address = new Address(street, zipCode);

            Organization updated = new Organization(id, oldOrg.getCreationDate(), name, coordinates, annualTurnover, employeesCount, type, address);

            if (collectionManager.updateById(id, updated)) {
                System.out.println("Организация обновлена.");
            } else {
                System.out.println("Ошибка при обновлении.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Обновить элемент по ID";
    }
}
