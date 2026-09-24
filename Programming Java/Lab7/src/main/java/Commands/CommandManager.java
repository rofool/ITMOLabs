package main.java.Commands;

import main.java.Storage.CollectionManager;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager(CollectionManager collectionManager, String filePath) {

        commands.put("help", new HelpCommand(commands));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("remove_first", new RemoveFirstCommand(collectionManager));
        commands.put("remove_head", new RemoveHeadCommand(collectionManager));
        commands.put("add_if_min", new AddIfMinCommand(collectionManager));
        commands.put("remove_any_by_employees_count", new RemoveAnyByEmployeesCountCommand(collectionManager));
        commands.put("filter_by_annual_turnover", new FilterByAnnualTurnoverCommand(collectionManager));
        commands.put("print_field_descending_type", new PrintFieldDescendingTypeCommand(collectionManager));
        commands.put("execute_script", new ExecuteScriptCommand(this));
        commands.put("exit", new ExitCommand());
    }

    /**
     * Вызов без авторизации (help, show, info и т.п.)
     */
    public String executeCommand(String commandName, String[] args) {
        Command command = commands.get(commandName.toLowerCase());
        if (command == null) return "Неизвестная команда: " + commandName;
        return command.execute(args);
    }

    /**
     * Вызов с ownerId после успешной аутентификации
     */
    public String executeCommand(String commandName, String[] args, long ownerId) {
        Command command = commands.get(commandName.toLowerCase());
        if (command == null) return "Неизвестная команда: " + commandName;

        if (command instanceof AuthenticatedCommand ac) {
            return ac.execute(args, ownerId);
        } else {

            return command.execute(args);
        }
    }
}
