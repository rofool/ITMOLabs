package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.db.DataBaseManager;

public class RemoveHeadCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public RemoveHeadCommand(CollectionManager collectionManager) { this.collectionManager = collectionManager; }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        try {
            var headOpt = DataBaseManager.findHead();
            if (headOpt.isEmpty()) return "Коллекция пуста, нечего удалять.";
            var head = headOpt.get();
            String printed = head.org.toString();
            if (head.ownerId != ownerId) return "Первый элемент: \n" + printed + "\nНо он вам не принадлежит. Удаление запрещено.";
            boolean ok = DataBaseManager.deleteOrganization(head.org.getId(), ownerId);
            if (!ok) return "Первый элемент: \n" + printed + "\nУдалить не удалось (в БД).";
            collectionManager.replaceAll(DataBaseManager.loadAllOrganizations());
            return "Удалён первый элемент: \n" + printed;
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @Override public String getDescription() { return "Вывести и удалить первый элемент (если он ваш)"; }
}
