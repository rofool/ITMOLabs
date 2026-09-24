package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Model.Organization;

public class RemoveHeadCommand implements Command {
    private final CollectionManager collectionManager;

    public RemoveHeadCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String execute(String[] args) {
        Organization org = collectionManager.removeHead();
        if (org != null) {
            return "Удалён первый элемент: " + org.toString();
        } else {
            return "Коллекция пуста, нечего удалять.";
        }
    }

    @Override
    public String getDescription() {
        return "Вывести и удалить первый элемент коллекции";
    }
}