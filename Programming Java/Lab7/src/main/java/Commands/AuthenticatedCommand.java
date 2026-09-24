package main.java.Commands;

public interface AuthenticatedCommand extends Command {

    @Override
    default String execute(String[] args) {
        return "Требуется авторизация.";
    }

    String execute(String[] args, long ownerId);
}
