package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.db.DataBaseManager;

public class RemoveByIdCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) { this.collectionManager = collectionManager; }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        if (args.length == 0) return "Ошибка: Укажите ID организации для удаления.";
        try {
            long id = Long.parseLong(args[0]);
            boolean ok = DataBaseManager.deleteOrganization(id, ownerId);
            if (!ok) return "Не удалось удалить: либо нет прав, либо ID не существует.";
            boolean removed = collectionManager.removeById(id);
            return removed ? ("Организация с ID " + id + " удалена.")
                    : "Удалено в БД, но не найдено в памяти (перезапустите сервер для синхронизации).";
        } catch (NumberFormatException e) {
            return "Ошибка: некорректный ID (ожидается число).";
        } catch (Exception e) {
            return "Ошибка при удалении: " + e.getMessage();
        }
    }

    @Override public String getDescription() { return "Удалить элемент по ID"; }
}
