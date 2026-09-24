package main.java.Commands;

import main.java.Model.*;
import main.java.Storage.CollectionManager;
import main.java.Utils.JsonHandler;
import main.java.db.DataBaseManager;

public class UpdateCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) { this.collectionManager = collectionManager; }

    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        if (args.length == 0 || args[0].trim().isEmpty()) return "Не указан JSON с данными для обновления.";
        try {
            String json = args[0].trim();
            var obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
            if (!obj.has("id")) return "В JSON отсутствует поле id";
            long id = obj.get("id").getAsLong();

            Organization old = collectionManager.getById(id);
            if (old == null) return "Организация с таким ID не найдена.";

            OrganizationDraft d = JsonHandler.gson.fromJson(json, OrganizationDraft.class);
            Organization updated = new Organization(
                    id, old.getCreationDate(),
                    d.name, new Coordinates(d.coordinates.getX(), d.coordinates.getY()),
                    d.annualTurnover, d.employeesCount, d.type,
                    (d.officialAddress == null ? null : new Address(d.officialAddress.getStreet(), d.officialAddress.getZipCode()))
            );

            boolean ok = DataBaseManager.updateOrganization(updated, ownerId);
            if (!ok) return "Не удалось обновить: либо нет прав, либо ID не существует.";

            if (collectionManager.updateByIdBool(id, updated)) return "Организация обновлена.";
            return "Ошибка при обновлении в памяти.";
        } catch (Exception e) {
            return "Ошибка при обновлении: " + e.getMessage();
        }
    }

    @Override public String getDescription() { return "Обновить организацию по ID (ожидает JSON с id)"; }
}
