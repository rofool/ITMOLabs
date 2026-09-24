package Lab5.client;

import Lab5.Commands.CommandManager;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CommandManager commandManager;

    public ClientHandler(Socket socket, CommandManager commandManager) {
        this.socket = socket;
        this.commandManager = commandManager;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {
            String command;
            while ((command = in.readLine()) != null) {
                System.out.println("Получено от клиента: '" + command + "'");
                if (command.trim().isEmpty()) {
                    out.println("");
                    continue;
                }
                String[] parts = command.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                if (commandName.equals("exit")) {
                    out.println("Сервер завершил работу.");
                    System.exit(0);
                }
                String[] commandArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0]; // Изменено
                String response = commandManager.executeCommand(commandName, commandArgs);
                if (response != null && !response.isEmpty()) {
                    out.println(response);
                } else {
                    out.println("Команда выполнена, нет вывода.");
                }
                out.println();
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки клиента: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Ошибка закрытия сокета: " + e.getMessage());
            }
        }
    }
}