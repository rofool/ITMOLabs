package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для удаления элемента из коллекции по его уникальному ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление организации по заданному ID.
     * Ожидает один аргумент — ID организации.
     *
     * @param args Аргументы команды, первый элемент — ID.
     */
    @Override
    public void execute(String[] args) {
        try {
            long id = Long.parseLong(args[0]);
            boolean removed = collectionManager.removeById(id);
            if (removed) {
                System.out.println("Организация с ID " + id + " удалена.");
            } else {
                System.out.println("Организация с таким ID не найдена.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при удалении: " + e.getMessage());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Удалить элемент из коллекции по его ID";
    }
}
