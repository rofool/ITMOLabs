package Lab5.Commands;

import Lab5.Model.Organization;
import Lab5.Storage.CollectionManager;

import java.util.stream.Collectors;

/**
 * Команда для вывода всех организаций с полной информацией.
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (Long.parseLong(collectionManager.size()) == 0) {
            return "Коллекция пуста.";
        }
        return collectionManager.getAsList().stream()
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getDescription() {
        return "Вывести всю информацию о всех элементах коллекции";
    }
}
