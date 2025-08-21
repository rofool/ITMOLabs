package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для удаления элемента из коллекции по его уникальному ID.
 */
public class RemoveByIdCommand implements Command {
    private final CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            return "Ошибка: Укажите ID организации для удаления.";
        }

        try {
            long id = Long.parseLong(args[0]);
            boolean removed = collectionManager.removeById(id);
            if (removed) {
                return "Организация с ID " + id + " удалена.";
            } else {
                return "Организация с таким ID не найдена.";
            }
        } catch (NumberFormatException e) {
            return "Ошибка при удалении: Введён некорректный ID (ожидается число).";
        } catch (Exception e) {
            return "Ошибка при удалении: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Удалить элемент из коллекции по его ID";
    }
}