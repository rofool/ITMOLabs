package main.java.Commands;

import com.google.gson.Gson;
import main.java.Model.Organization;
import main.java.Model.OrganizationDraft;
import main.java.Storage.CollectionManager;
import main.java.Utils.JsonHandler;
import main.java.db.DataBaseManager;

import java.time.ZonedDateTime;

public class AddIfMinCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;
    private static final Gson gson = JsonHandler.gson;

    public AddIfMinCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            return "Не указаны данные для добавления организации.";
        }
        try {
            OrganizationDraft d = gson.fromJson(args[0].trim(), OrganizationDraft.class);
            if (d == null) return "Ошибка парсинга JSON.";


            Organization probe = new Organization(
                    1L, ZonedDateTime.now(),
                    d.name, d.coordinates, d.annualTurnover, d.employeesCount, d.type, d.officialAddress
            );

            if (!collectionManager.isMin(probe)) {
                return "Организация не добавлена — она не меньше минимальной.";
            }

            // Сначала пишем в БД, затем добавляем в память
            Organization saved = DataBaseManager.insertOrganization(d, ownerId);
            collectionManager.add(saved);
            return "Организация добавлена, она меньше всех (ID: " + saved.getId() + ").";
        } catch (Exception e) {
            return "Ошибка при добавлении организации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Добавить элемент, если он меньше всех в коллекции";
    }
}
