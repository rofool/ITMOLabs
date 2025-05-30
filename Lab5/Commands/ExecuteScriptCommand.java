package Lab5.Commands;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Команда для выполнения набора команд из указанного файла-скрипта.
 * Обеспечивает защиту от рекурсивных вызовов скриптов.
 */
public class ExecuteScriptCommand implements Command {
    private final CommandManager commandManager;

    /**
     * Множество для отслеживания активных скриптов, чтобы избежать рекурсии.
     */
    private final Set<String> activeScripts = new HashSet<>();

    /**
     * Конструктор команды.
     *
     * @param commandManager Менеджер команд, через который выполняются команды из скрипта.
     */
    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команды из файла скрипта.
     * Проверяет наличие рекурсивных вызовов и предотвращает их.
     *
     * @param args Первый элемент должен быть именем файла скрипта.
     */
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Не указано имя файла для выполнения скрипта.");
            return;
        }

        String fileName = args[0];

        if (activeScripts.contains(fileName)) {
            System.out.println("Ошибка: рекурсивный вызов скрипта '" + fileName + "' обнаружен!");
            return;
        }

        activeScripts.add(fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                System.out.println("Выполняется команда из скрипта: " + line);

                String[] parts = line.split("\\s+");
                String cmdName = parts[0];
                String[] cmdArgs = parts.length > 1 ? java.util.Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

                if (!commandManager.executeCommand(cmdName, cmdArgs)) {
                    System.out.println("Неизвестная команда в скрипте: " + cmdName);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла скрипта: " + e.getMessage());
        } finally {
            activeScripts.remove(fileName);
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Выполнить скрипт из файла";
    }
}
