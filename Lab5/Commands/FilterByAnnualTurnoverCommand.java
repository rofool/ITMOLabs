package Lab5.Commands;

import Lab5.Storage.CollectionManager;

/**
 * Команда для фильтрации и вывода организаций по заданному значению annualTurnover.
 */
public class FilterByAnnualTurnoverCommand implements Command {
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды.
     *
     * @param collectionManager Менеджер коллекции, в которой происходит фильтрация.
     */
    public FilterByAnnualTurnoverCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет фильтрацию организаций по заданному annualTurnover.
     * Ожидает один аргумент — значение annualTurnover для фильтрации.
     *
     * @param args Аргументы команды, первый элемент — значение annualTurnover.
     */
    @Override
    public void execute(String[] args) {
        try {
            int turnover = Integer.parseInt(args[0]);
            collectionManager.filterByAnnualTurnover(turnover);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Вывести элементы с заданным annualTurnover";
    }
}
