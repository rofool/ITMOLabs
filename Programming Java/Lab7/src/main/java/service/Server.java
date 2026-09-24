package main.java.service;

import main.java.Commands.CommandManager;
import main.java.Model.Organization;
import main.java.Storage.CollectionManager;
import main.java.client.ClientHandler;
import main.java.db.DataBaseManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private CollectionManager collectionManager;
    private CommandManager commandManager;

    private ExecutorService processPool;
    private ExecutorService sendPool;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {

            DataBaseManager.connect();


            List<Organization> loaded = DataBaseManager.loadAllOrganizations();
            System.out.println("Loaded " + loaded.size() + " organizations from DB");
            collectionManager = new CollectionManager(loaded);


            commandManager = new CommandManager(collectionManager, null);


            processPool = Executors.newCachedThreadPool();
            sendPool = Executors.newFixedThreadPool(4);


            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(port));
            System.out.println("Server started on port " + port);


            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Shutting down pools and DB...");
                    if (processPool != null) processPool.shutdownNow();
                    if (sendPool != null) sendPool.shutdownNow();
                } finally {
                    DataBaseManager.closeQuiet();
                    try {
                        serverChannel.close();
                    } catch (IOException ignore) {
                    }
                }
            }));


            Thread consoleThread = new Thread(this::handleServerConsole);
            consoleThread.setDaemon(true);
            consoleThread.start();


            while (true) {
                SocketChannel clientChannel = serverChannel.accept();
                System.out.println("New client connected: " + clientChannel.socket().getInetAddress());
                ClientHandler handler = new ClientHandler(clientChannel.socket(), commandManager, processPool, sendPool);
                new Thread(handler, "reader-" + clientChannel.socket().getPort()).start();
            }
        } catch (IOException | SQLException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleServerConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Server> ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "exit" -> {
                    System.out.println("Завершение работы сервера...");
                    System.exit(0);
                }
                default -> System.out.println("Неизвестная команда сервера. Доступные: exit");
            }
        }
    }

    public static void main(String[] args) {
        int port = 8082;
        new Server(port).start();
    }
}
