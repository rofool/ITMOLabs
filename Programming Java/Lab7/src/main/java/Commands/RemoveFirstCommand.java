package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.db.DataBaseManager;

public class RemoveFirstCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public RemoveFirstCommand(CollectionManager collectionManager) { this.collectionManager = collectionManager; }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        try {
            var headOpt = DataBaseManager.findHead();
            if (headOpt.isEmpty()) return "Коллекция пуста, нечего удалять.";
            var head = headOpt.get();
            if (head.ownerId != ownerId) return "Первый элемент вам не принадлежит. Удаление запрещено.";
            boolean ok = DataBaseManager.deleteOrganization(head.org.getId(), ownerId);
            if (!ok) return "Не удалось удалить первый элемент (в БД).";

            collectionManager.replaceAll(DataBaseManager.loadAllOrganizations());
            return "Первый элемент удалён.";
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        }
    }

    @Override public String getDescription() { return "Удалить первый элемент коллекции (если он ваш)"; }
}
