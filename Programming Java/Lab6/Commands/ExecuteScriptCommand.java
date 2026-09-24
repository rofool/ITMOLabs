package Lab5.Commands;

import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements Command {
    private final CommandManager commandManager;
    private final Set<String> activeScripts = new HashSet<>();

    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            System.out.println("Ошибка: Не указано содержимое скрипта.");
            return "Не указано содержимое скрипта.";
        }

        String scriptContent = args[0].trim();
        String scriptId = Integer.toString(scriptContent.hashCode());
        System.out.println("Получено содержимое скрипта от клиента: " + scriptId.substring(0, 8) + "...");

        if (activeScripts.contains(scriptId)) {
            System.out.println("Ошибка: Рекурсивный вызов скрипта обнаружен!");
            return "Ошибка: рекурсивный вызов скрипта обнаружен!";
        }

        activeScripts.add(scriptId);

        try {
            String[] lines = scriptContent.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                System.out.println("Выполняется команда из скрипта: " + line);

                String[] parts = line.split("\\s+", 2);
                String cmdName = parts[0].toLowerCase();
                String[] cmdArgs = parts.length > 1 ? parts[1].trim().split("\\s+") : new String[0];

                // Преобразуем аргументы в JSON для команд, требующих данные
                if (cmdName.equals("add") || cmdName.equals("add_if_max") || cmdName.equals("add_if_min")) {
                    if (cmdArgs.length >= 8) {
                        String json = convertToJson(cmdName, cmdArgs);
                        System.out.println("Преобразовано в JSON: " + json); // Отладка
                        cmdArgs = new String[]{json};
                    } else {
                        System.out.println("Ошибка: Недостаточно аргументов для команды " + cmdName);
                        return "Ошибка: Недостаточно аргументов для команды " + cmdName;
                    }
                } else if (cmdName.equals("update")) {
                    if (cmdArgs.length >= 9) {
                        String json = convertToJson(cmdName, cmdArgs);
                        System.out.println("Преобразовано в JSON: " + json); // Отладка
                        cmdArgs = new String[]{json};
                    } else {
                        System.out.println("Ошибка: Недостаточно аргументов для команды " + cmdName);
                        return "Ошибка: Недостаточно аргументов для команды " + cmdName;
                    }
                } else if (cmdName.equals("filter_by_annual_turnover") || cmdName.equals("remove_any_by_employees_count") || cmdName.equals("remove_by_id")) {
                    cmdArgs = cmdArgs.length > 0 ? new String[]{cmdArgs[0]} : new String[]{};
                }

                System.out.println("Команда: " + cmdName + ", Аргументы: " + (cmdArgs.length > 0 ? String.join(",", cmdArgs) : "Нет"));
                String response = commandManager.executeCommand(cmdName, cmdArgs);
                if (response != null && !response.equals("false")) {
                    System.out.println("Ответ сервера: " + response);
                } else {
                    System.out.println("Ошибка: Неизвестная команда или ошибка выполнения: " + cmdName);
                    return "Неизвестная команда в скрипте: " + cmdName;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка выполнения скрипта: " + e.getMessage());
            return "Ошибка выполнения скрипта: " + e.getMessage();
        } finally {
            activeScripts.remove(scriptId);
        }
        return "Скрипт выполнился успешно.";
    }

    private String convertToJson(String commandName, String[] args) {
        StringBuilder json = new StringBuilder("{");
        if ("add".equals(commandName) || "add_if_max".equals(commandName) || "add_if_min".equals(commandName)) {
            json.append("\"name\":\"").append(args[0]).append("\",")
                    .append("\"coordinates\":{\"x\":").append(args[1]).append(",\"y\":").append(args[2]).append("},")
                    .append("\"annualTurnover\":").append(args[3].isEmpty() ? "null" : args[3]).append(",")
                    .append("\"employeesCount\":").append(args[4]).append(",")
                    .append("\"type\":\"").append(args[5]).append("\",")
                    .append("\"officialAddress\":{\"street\":\"").append(args[6].isEmpty() ? "null" : args[6]).append("\",\"zipCode\":\"").append(args[7]).append("\"}");
        } else if ("update".equals(commandName)) {
            json.append("\"id\":").append(args[0]).append(",")
                    .append("\"name\":\"").append(args[1]).append("\",")
                    .append("\"coordinates\":{\"x\":").append(args[2]).append(",\"y\":").append(args[3]).append("},")
                    .append("\"annualTurnover\":").append(args[4].isEmpty() ? "null" : args[4]).append(",")
                    .append("\"employeesCount\":").append(args[5]).append(",")
                    .append("\"type\":\"").append(args[6]).append("\",")
                    .append("\"officialAddress\":{\"street\":\"").append(args[7].isEmpty() ? "null" : args[7]).append("\",\"zipCode\":\"").append(args[8]).append("\"}");
        }
        json.append("}");
        return json.toString();
    }

    @Override
    public String getDescription() {
        return "Выполнить скрипт из переданного содержимого";
    }
}