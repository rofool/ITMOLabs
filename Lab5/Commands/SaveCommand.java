package Lab5.Commands;

import Lab5.Storage.CollectionManager;
import Lab5.Utils.JsonHandler;

/**
 * Команда для сохранения текущего состояния коллекции в файл.
 */
public class SaveCommand implements Command {
    private final CollectionManager collectionManager;
    private final String filePath;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции организаций.
     * @param filePath Путь к файлу для сохранения коллекции.
     */
    public SaveCommand(CollectionManager collectionManager, String filePath) {
        this.collectionManager = collectionManager;
        this.filePath = filePath;
    }

    /**
     * Выполняет сохранение коллекции в указанный файл.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String[] args) {
        try {
            JsonHandler.saveToFile(collectionManager.getAsList(), filePath);
            System.out.println("Коллекция сохранена в файл.");
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Сохранить коллекцию в файл";
    }
}
