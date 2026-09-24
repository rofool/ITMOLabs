package main.java.client;

import main.java.Commands.CommandManager;
import main.java.db.DataBaseManager;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final CommandManager commandManager;
    private final ExecutorService processPool; // обработка
    private final ExecutorService sendPool;    // отправка

    public ClientHandler(Socket socket, CommandManager commandManager,
                         ExecutorService processPool, ExecutorService sendPool) {
        this.socket = socket;
        this.commandManager = commandManager;
        this.processPool = processPool;
        this.sendPool = sendPool;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {

            final Object sendLock = new Object();

            String wire;
            while ((wire = in.readLine()) != null) {
                final String line = wire;
                if (line.trim().isEmpty()) {
                    sendPool.submit(() -> {
                        synchronized (sendLock) { out.println(); out.flush(); }
                    });
                    continue;
                }

                processPool.submit(() -> {
                    String response;
                    try {
                        int bar = line.indexOf('|');
                        if (bar <= 0) {
                            response = "Ошибка протокола: используйте 'login:password|команда'.";
                            send(sendPool, sendLock, out, response);
                            return;
                        }
                        String lp = line.substring(0, bar);
                        String commandPart = line.substring(bar + 1);

                        int colon = lp.indexOf(':');
                        if (colon <= 0) {
                            response = "Ошибка протокола: отсутствует ':' между логином и паролем.";
                            send(sendPool, sendLock, out, response);
                            return;
                        }
                        String login = lp.substring(0, colon);
                        String pass  = lp.substring(colon + 1);

                        String[] parts = commandPart.split("\\s+", 2);
                        String commandName = parts[0].toLowerCase();
                        String[] commandArgs = parts.length > 1 ? new String[]{parts[1]} : new String[0];

                        if (commandName.equals("register")) {
                            try {
                                long id = DataBaseManager.register(login, pass);
                                response = "201 Пользователь создан, id=" + id;
                            } catch (Exception e) {
                                response = "Ошибка регистрации: " + e.getMessage();
                            }
                            send(sendPool, sendLock, out, response);
                            return;
                        }

                        if (commandName.equals("help")) {
                            response = commandManager.executeCommand("help", new String[0]) +
                                    "\nПодсказка: если видите '401 Неавторизовано', выполните 'register'.";
                            send(sendPool, sendLock, out, response);
                            return;
                        }

                        Optional<Long> ownerOpt;
                        try {
                            ownerOpt = DataBaseManager.authenticate(login, pass);
                        } catch (Exception e) {
                            response = "Ошибка БД: " + e.getMessage();
                            send(sendPool, sendLock, out, response);
                            return;
                        }
                        if (ownerOpt.isEmpty()) {
                            response = "401 Неавторизовано. Подсказка: выполните 'register' и повторите команду.";
                            send(sendPool, sendLock, out, response);
                            return;
                        }
                        long ownerId = ownerOpt.get();

                        if (commandName.equals("exit")) {
                            response = "Сервер: завершение работы по команде exit.";
                            send(sendPool, sendLock, out, response);
                            System.exit(0);
                            return;
                        }

                        response = commandManager.executeCommand(commandName, commandArgs, ownerId);
                        if (response == null || response.isEmpty()) response = "Команда выполнена, нет вывода.";
                    } catch (Exception e) {
                        response = "Внутренняя ошибка обработки: " + e.getMessage();
                    }
                    send(sendPool, sendLock, out, response);
                });
            }
        } catch (IOException e) {
            System.err.println("Ошибка обработки клиента: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) { System.err.println("Ошибка закрытия сокета: " + e.getMessage()); }
        }
    }

    private static void send(ExecutorService sendPool, Object lock, PrintWriter out, String payload) {
        sendPool.submit(() -> {
            synchronized (lock) {
                out.println(payload);
                out.println(); // разделитель ответа
                out.flush();
            }
        });
    }
}
