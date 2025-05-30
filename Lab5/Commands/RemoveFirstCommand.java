package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для удаления первого элемента из коллекции организаций.
 */
public class RemoveFirstCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public RemoveFirstCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление первого элемента коллекции.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String[] args) {
        boolean removed = collectionManager.removeFirst();
        if (removed) {
            System.out.println("Первый элемент удалён.");
        } else {
            System.out.println("Коллекция пуста, нечего удалять.");
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Удалить первый элемент коллекции";
    }
}
