package main.java.Commands;

import main.java.Storage.CollectionManager;
import main.java.Model.Organization;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Вывести значения поля type всех элементов в порядке убывания.
 */
public class PrintFieldDescendingTypeCommand implements Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingTypeCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        List<Organization> organizations = collectionManager.getAsList();
        if (organizations.isEmpty()) return "Коллекция пуста.";

        return organizations.stream()
                .map(Organization::getType)
                .sorted(Comparator.reverseOrder())
                .map(Enum::name)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getDescription() {
        return "Вывести значения поля type всех элементов в порядке убывания";
    }
}
