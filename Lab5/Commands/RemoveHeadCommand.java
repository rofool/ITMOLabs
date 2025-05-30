package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Model.Organization;

/**
 * Команда для вывода и удаления первого элемента из коллекции.
 */
public class RemoveHeadCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     */
    public RemoveHeadCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду: выводит и удаляет первый элемент коллекции.
     * Если коллекция пуста, выводит соответствующее сообщение.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String[] args) {
        Organization head = collectionManager.removeHead();
        if (head != null) {
            System.out.println("Удалённый элемент:");
            System.out.println(head);
        } else {
            System.out.println("Коллекция пуста. Нечего удалять.");
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Вывести и удалить первый элемент коллекции";
    }
}
