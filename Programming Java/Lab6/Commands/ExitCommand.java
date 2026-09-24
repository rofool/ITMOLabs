package Lab5.Commands;

/**
 * Команда для завершения работы программы.
 * При выполнении выводит сообщение и завершает JVM с кодом 0.
 */
public class ExitCommand implements Command {

    /**
     * Выполняет команду завершения программы.
     * Игнорирует переданные аргументы.
     *
     * @param args аргументы команды (не используются)
     */
    @Override
    public String execute(String[] args) {
        System.exit(0);
        return "Завершение программы...";
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    @Override
    public String getDescription() {
        return "Завершить программу";
    }
}
