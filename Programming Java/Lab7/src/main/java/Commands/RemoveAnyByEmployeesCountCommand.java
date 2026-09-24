package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.db.DataBaseManager;

/** Удалить ЛЮБОЙ ОДИН объект пользователя по employeesCount */
public class RemoveAnyByEmployeesCountCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public RemoveAnyByEmployeesCountCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {

        if (args.length == 0 || args[0].isBlank()) {
            return "Ошибка: укажите количество сотрудников.";
        }
        try {
            int count = Integer.parseInt(args[0].trim());


            var idOpt = DataBaseManager.findAnyByOwnerAndEmployees(ownerId, count);
            if (idOpt.isEmpty()) {
                return "У вас нет организации с employeesCount = " + count;
            }
            long id = idOpt.get();


            boolean ok = DataBaseManager.deleteOrganization(id, ownerId);
            if (!ok) return "Не удалось удалить (похоже, уже удалено или нет прав).";

            collectionManager.replaceAll(DataBaseManager.loadAllOrganizations());
            return "Удалена ваша организация (ID " + id + ") с employeesCount = " + count + ".";
        } catch (NumberFormatException e) {
            return "Ошибка: employeesCount должен быть целым числом.";
        } catch (Exception e) {
            return "Ошибка при удалении: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Удалить любой объект по employeesCount (только ваш)";
    }
}
