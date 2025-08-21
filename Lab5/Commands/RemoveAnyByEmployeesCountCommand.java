package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для удаления из коллекции одного элемента,
 * у которого значение поля employeesCount равно заданному.
 */
public class RemoveAnyByEmployeesCountCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public RemoveAnyByEmployeesCountCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет удаление организации с заданным количеством сотрудников.
     * Ожидает один аргумент — количество сотрудников.
     *
     * @param args Аргументы команды, первый элемент — employeesCount.
     */
    @Override
    public String execute(String[] args) {
        try {
            int count = Integer.parseInt(args[0]);
            boolean removed =Boolean.getBoolean(collectionManager.removeAnyByEmployeesCount(count));
            if (removed) {
                return   "Организация с employeesCount = " + count + " удалена.";
            } else {
                return"Организация с таким количеством сотрудников не найдена.";
            }
        } catch (Exception e) {
            return"Ошибка: " + e.getMessage();
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Удалить элемент по количеству сотрудников";
    }
}
