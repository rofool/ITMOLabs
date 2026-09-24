package Lab5.Commands;

import Lab5.Model.Organization;
import Lab5.Storage.CollectionManager;
import com.google.gson.Gson;

/**
 * Команда для добавления новой организации, если она больше всех в коллекции.
 */
public class AddIfMaxCommand implements Command {
    private final CollectionManager collectionManager;
    private static final Gson gson = new Gson();

    public AddIfMaxCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            return "Не указаны данные для добавления организации.";
        }

        try {
            String json = args[0].trim();
            Organization org = gson.fromJson(json, Organization.class);
            if (org == null) {
                return "Ошибка парсинга JSON.";
            }
            return collectionManager.addIfMax(org);
        } catch (Exception e) {
            return "Ошибка при добавлении организации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Добавить элемент, если он больше всех в коллекции";
    }
}