package Lab5.Commands;

import Lab5.Model.Address;
import Lab5.Model.Coordinates;
import Lab5.Model.Organization;
import Lab5.Model.OrganizationType;
import Lab5.Storage.CollectionManager;
import Lab5.Utils.InputHelper;

import java.util.Scanner;

/**
 * Команда для обновления организации по ID.
 * Поддерживает ввод из скрипта (через args) и интерактивный ввод.
 */
public class UpdateCommand implements Command {
    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (args.length >= 9) {
            // Обработка из args (скрипт)
            try {
                long id = Long.parseLong(args[0]);
                String name = args[1];
                int x = Integer.parseInt(args[2]);
                long y = Long.parseLong(args[3]);
                Integer annualTurnover = args[4].isEmpty() ? null : Integer.parseInt(args[4]);
                int employeesCount = Integer.parseInt(args[5]);
                OrganizationType type = OrganizationType.valueOf(args[6].toUpperCase());
                String street = args[7].isEmpty() ? null : args[7];
                String zipCode = args[8];

                Organization oldOrg = collectionManager.getById(id);
                if (oldOrg == null) {
                    return "Организация с таким ID не найдена.";
                }

                Coordinates coordinates = new Coordinates(x, y);
                Address address = new Address(street, zipCode);
                Organization updatedOrg = new Organization(id, oldOrg.getCreationDate(), name, coordinates, annualTurnover, employeesCount, type, address);

                if (Boolean.parseBoolean(collectionManager.updateById(id, updatedOrg))) {
                    return"Организация обновлена из скрипта.";
                } else {
                    return "Ошибка при обновлении.";
                }
            } catch (Exception e) {
                return "Ошибка при обновлении из скрипта: " + e.getMessage();
            }
        } else {
            // Интерактивный ввод
            Scanner scanner = new Scanner(System.in);
            InputHelper inputHelper = new InputHelper(scanner);

            try {
                long id = inputHelper.readInt("Введите ID организации для обновления: ", true);
                Organization oldOrg = collectionManager.getById(id);
                if (oldOrg == null) {
                    return "Организация с таким ID не найдена.";
                }

                System.out.println("Оставьте поле пустым, чтобы не изменять его.");

                String name = inputHelper.readString(
                        String.format("Введите имя [%s]: ", oldOrg.getName()), true);
                if (name == null) name = oldOrg.getName();

                String xStr = inputHelper.readString(
                        String.format("Введите координату X (int) [%d]: ", oldOrg.getCoordinates().getX()), true);
                int x = (xStr == null) ? oldOrg.getCoordinates().getX() : Integer.parseInt(xStr);

                String yStr = inputHelper.readString(
                        String.format("Введите координату Y (long) [%d]: ", oldOrg.getCoordinates().getY()), true);
                long y = (yStr == null) ? oldOrg.getCoordinates().getY() : Long.parseLong(yStr);

                String turnoverStr = inputHelper.readString(
                        String.format("Введите годовой оборот [%s]: ",
                                oldOrg.getAnnualTurnover() == null ? "null" : oldOrg.getAnnualTurnover()), true);
                Integer annualTurnover = turnoverStr == null ? oldOrg.getAnnualTurnover() : Integer.parseInt(turnoverStr);

                String employeesStr = inputHelper.readString(
                        String.format("Введите количество сотрудников [%d]: ", oldOrg.getEmployeesCount()), true);
                int employeesCount = employeesStr == null ? oldOrg.getEmployeesCount() : Integer.parseInt(employeesStr);

                System.out.println("Возможные типы организаций:");
                OrganizationType type = inputHelper.readEnum(
                        String.format("Введите тип организации [%s]: ", oldOrg.getType()), OrganizationType.class);

                String street = inputHelper.readString(
                        String.format("Введите улицу [%s]: ",
                                oldOrg.getOfficialAddress() != null ? oldOrg.getOfficialAddress().getStreet() : "null"), true);
                if (street == null && oldOrg.getOfficialAddress() != null)
                    street = oldOrg.getOfficialAddress().getStreet();

                String zipCode = inputHelper.readString(
                        String.format("Введите ZIP-код [%s]: ",
                                oldOrg.getOfficialAddress() != null ? oldOrg.getOfficialAddress().getZipCode() : "null"), true);
                if (zipCode == null && oldOrg.getOfficialAddress() != null)
                    zipCode = oldOrg.getOfficialAddress().getZipCode();

                Coordinates coordinates = new Coordinates(x, y);
                Address address = new Address(street, zipCode);

                Organization updatedOrg = new Organization(
                        id, oldOrg.getCreationDate(), name, coordinates, annualTurnover, employeesCount, type, address);

                if (Boolean.parseBoolean(collectionManager.updateById(id, updatedOrg))) {
                    return"Организация обновлена.";
                } else {
                    return"Ошибка при обновлении организации.";
                }
            } catch (Exception e) {
                return"Ошибка: " + e.getMessage();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Обновить организацию по ID";
    }
}
