package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Model.Organization;
import java.util.Comparator;
import java.util.List;

/**
 * Команда для вывода значений поля type всех организаций в порядке убывания.
 * Использует коллекцию из CollectionManager, сортирует и выводит типы организаций.
 */
public class PrintFieldDescendingTypeCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции, источник данных.
     */
    public PrintFieldDescendingTypeCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду: выводит типы организаций в порядке убывания.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String[] args) {
        List<Organization> organizations = collectionManager.getAsList();
        organizations.stream()
                .map(Organization::getType)
                .sorted(Comparator.reverseOrder())
                .forEach(System.out::println);
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Вывести значения поля type всех элементов в порядке убывания";
    }
}
