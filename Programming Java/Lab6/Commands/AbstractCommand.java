package Lab5.Commands;

import Lab5.Storage.CollectionManager;

public abstract class AbstractCommand implements Command {
    protected static CollectionManager collectionManager = new CollectionManager();

    public AbstractCommand(CollectionManager collectionManager) {
        AbstractCommand.collectionManager = collectionManager;
    }

    public AbstractCommand() {
    }
}
