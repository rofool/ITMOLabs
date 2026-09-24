package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.db.DataBaseManager;

import java.util.List;

public class ClearCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) { this.collectionManager = collectionManager; }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        try {
            int deleted = DataBaseManager.deleteAllByOwner(ownerId);

            List<main.java.Model.Organization> fresh = DataBaseManager.loadAllOrganizations();
            collectionManager.replaceAll(fresh);
            return "Удалено ваших объектов: " + deleted + ".";
        } catch (Exception e) {
            return "Ошибка при очистке: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() { return "Очистить коллекцию (удаляет только ваши объекты в БД)"; }
}
