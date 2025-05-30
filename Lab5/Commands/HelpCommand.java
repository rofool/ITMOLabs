package Lab5.Commands;

import java.util.Map;

/**
 * Команда для вывода списка всех доступных команд с их описаниями.
 */
public class HelpCommand implements Command {
    private final Map<String, Command> commands;

    /**
     * Конструктор команды.
     *
     * @param commands Карта всех зарегистрированных команд.
     */
    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    /**
     * Выводит список всех доступных команд и их описания.
     *
     * @param args Аргументы команды (не используются).
     */
    @Override
    public void execute(String[] args) {
        System.out.println("Список доступных команд:");
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            System.out.printf("  %s — %s%n", entry.getKey(), entry.getValue().getDescription());
        }
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Вывести справку по доступным командам";
    }
}
