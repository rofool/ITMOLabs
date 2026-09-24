package Lab5.Commands;

/**
 * Интерфейс, описывающий команду, которая может быть выполнена с набором аргументов.
 */
public interface Command {
    /**
     * Выполнить команду с переданными аргументами.
     *
     * @param args аргументы команды
     */
    String execute(String[] args);

    /**
     * Получить краткое описание команды.
     *
     * @return описание команды
     */
    String getDescription();
}
