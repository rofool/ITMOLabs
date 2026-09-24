package Lab5.Commands;

import Lab5.Model.IdGenerator;
import Lab5.Model.Organization;
import Lab5.Storage.CollectionManager;
import Lab5.Utils.JsonHandler;
import com.google.gson.Gson;

import java.time.ZonedDateTime;

/**
 * Команда для добавления новой организации.
 */
public class AddCommand implements Command {
    private final CollectionManager collectionManager;
    private static final Gson gson = JsonHandler.gson;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            return "Не указаны данные для добавления организации.";
        }

        try {
            String json = args[0].trim();
            // Десериализуем без id и creationDate
            Organization tempOrg = gson.fromJson(json, Organization.class);
            if (tempOrg == null) {
                return "Ошибка парсинга JSON.";
            }

            // Генерируем новый id и creationDate на сервере
            Long newId = IdGenerator.generateId();
            ZonedDateTime creationDate = ZonedDateTime.now();
            Organization org = new Organization(newId, creationDate,
                    tempOrg.getName(), tempOrg.getCoordinates(), tempOrg.getAnnualTurnover(),
                    tempOrg.getEmployeesCount(), tempOrg.getType(), tempOrg.getOfficialAddress());

            collectionManager.add(org);
            return "Организация добавлена: " + org.getName() + " (ID: " + newId + ")";
        } catch (Exception e) {
            return "Ошибка при добавлении организации: " + e.getMessage();
        }
    }

    @Override
    public String getDescription() {
        return "Добавить новую организацию";
    }
}