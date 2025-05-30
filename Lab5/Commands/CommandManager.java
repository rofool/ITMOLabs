package Lab5.Commands;

import Lab5.Storage.CollectionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер команд, который хранит набор доступных команд
 * и выполняет их по имени.
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    /**
     * Конструктор менеджера команд.
     * Регистрирует все доступные команды с передачей зависимостей.
     *
     * @param collectionManager Менеджер коллекции организаций.
     * @param filePath Путь к файлу для сохранения/загрузки коллекции.
     */
    public CommandManager(CollectionManager collectionManager, String filePath) {
        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("save", new SaveCommand(collectionManager, filePath));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("exit", new ExitCommand());
        commands.put("remove_first", new RemoveFirstCommand(collectionManager));
        commands.put("remove_head", new RemoveHeadCommand(collectionManager));
        commands.put("add_if_min", new AddIfMinCommand(collectionManager));
        commands.put("remove_any_by_employees_count", new RemoveAnyByEmployeesCountCommand(collectionManager));
        commands.put("filter_by_annual_turnover", new FilterByAnnualTurnoverCommand(collectionManager));
        commands.put("print_field_descending_type", new PrintFieldDescendingTypeCommand(collectionManager));
        commands.put("add_if_max", new AddIfMaxCommand(collectionManager));
    }

    /**
     * Выполнить команду по её имени с аргументами.
     *
     * @param commandName Имя команды для выполнения.
     * @param args Аргументы, передаваемые команде.
     * @return true, если команда найдена и успешно выполнена; false, если команда неизвестна.
     */
    public boolean executeCommand(String commandName, String[] args) {
        Command command = commands.get(commandName.toLowerCase());
        if (command == null) {
            System.out.println("Неизвестная команда: " + commandName);
            return false;
        }
        command.execute(args);
        return true;
    }
}
