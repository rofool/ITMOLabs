package main.java.Commands;

import main.java.Model.Organization;
import main.java.Model.OrganizationDraft;
import main.java.Storage.CollectionManager;
import main.java.Utils.JsonHandler;
import main.java.db.DataBaseManager;

/** Добавление организации ТОЛЬКО для авторизованного пользователя */
public class AddCommand implements AuthenticatedCommand {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /** Вызов без ownerId не допускается */
    @Override public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            return "Не указаны данные для добавления организации.";
        }
        try {
            String json = args[0].trim();


            OrganizationDraft draft = JsonHandler.gson.fromJson(json, OrganizationDraft.class);
            if (draft == null) return "Ошибка парсинга JSON.";


            Organization saved = DataBaseManager.insertOrganization(draft, ownerId);


            collectionManager.add(saved);

            return "Организация добавлена: " + saved.getName() + " (ID: " + saved.getId() + ")";
        } catch (Exception e) {
            return "Ошибка при добавлении организации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Добавить новую организацию (требуется авторизация)";
    }
}
