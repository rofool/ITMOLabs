package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для вывода информации о коллекции организаций.
 * Показывает тип коллекции, дату инициализации и количество элементов.
 */
public class InfoCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду вывода информации о коллекции.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public String execute(String[] args) {
        return collectionManager.info();
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }
}
