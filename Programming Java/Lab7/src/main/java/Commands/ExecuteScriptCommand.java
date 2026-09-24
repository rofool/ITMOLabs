package main.java.Commands;

import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements AuthenticatedCommand {
    private final CommandManager commandManager;
    private final Set<String> activeScripts = new HashSet<>();

    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public String execute(String[] args) { return "Требуется авторизация."; }

    @Override
    public String execute(String[] args, long ownerId) {
        if (args.length == 0 || args[0].trim().isEmpty()) {
            return "Не указано содержимое скрипта.";
        }

        String scriptContent = args[0].trim();
        String scriptId = Integer.toString(scriptContent.hashCode());

        if (activeScripts.contains(scriptId)) {
            return "Ошибка: рекурсивный вызов скрипта обнаружен!";
        }

        activeScripts.add(scriptId);
        try {
            String[] lines = scriptContent.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+", 2);
                String cmdName = parts[0].toLowerCase();
                String[] cmdArgs = parts.length > 1 ? new String[]{parts[1].trim()} : new String[0];


                if (cmdName.equals("add") || cmdName.equals("add_if_max") || cmdName.equals("add_if_min")) {
                    if (cmdArgs.length == 1 && cmdArgs[0].startsWith("{")) {

                    } else {

                        String[] a = parts.length > 1 ? parts[1].trim().split("\\s+") : new String[0];
                        if (a.length < 8) return "Ошибка: Недостаточно аргументов для команды " + cmdName;
                        cmdArgs = new String[]{convertToJson(cmdName, a)};
                    }
                } else if (cmdName.equals("update")) {
                    if (cmdArgs.length == 1 && cmdArgs[0].startsWith("{")) {

                    } else {
                        String[] a = parts.length > 1 ? parts[1].trim().split("\\s+") : new String[0];
                        if (a.length < 9) return "Ошибка: Недостаточно аргументов для команды " + cmdName;
                        cmdArgs = new String[]{convertToJson(cmdName, a)};
                    }
                } else if (cmdName.equals("filter_by_annual_turnover")
                        || cmdName.equals("remove_any_by_employees_count")
                        || cmdName.equals("remove_by_id")) {

                    cmdArgs = parts.length > 1 ? new String[]{parts[1].trim()} : new String[0];
                }

                String r = commandManager.executeCommand(cmdName, cmdArgs, ownerId);
                if (r == null) r = "";
                if (r.startsWith("Неизвестная команда")) return r;
            }
            return "Скрипт выполнился успешно.";
        } catch (Exception e) {
            return "Ошибка выполнения скрипта: " + e.getMessage();
        } finally {
            activeScripts.remove(scriptId);
        }
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
    public String getDescription() { return "Выполнить скрипт из переданного содержимого"; }
}
