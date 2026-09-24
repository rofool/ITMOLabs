package main.java.Commands;

import main.java.Model.Organization;
import main.java.Storage.CollectionManager;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Команда для вывода всех организаций с полной информацией (по id по возрастанию).
 */
public class ShowCommand implements Command {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        if (collectionManager.sizeInt() == 0) {
            return "Коллекция пуста.";
        }
        return collectionManager.getAsList().stream()
                .sorted(Comparator.comparingLong(o -> o.getId()))
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getDescription() {
        return "Вывести всю информацию о всех элементах коллекции";
    }
}
