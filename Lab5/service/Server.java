package Lab5.service;

import Lab5.Commands.CommandManager;
import Lab5.Model.Organization;
import Lab5.Storage.CollectionManager;
import Lab5.Utils.JsonHandler;
import Lab5.client.ClientHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * Server class that listens for client connections using ServerSocketChannel and manages the organization collection.
 * Handles server-side console commands in a separate thread.
 */
public class Server {
    private final int port;
    private final String filePath;
    private CollectionManager collectionManager;
    private CommandManager commandManager;

    public Server(int port, String filePath) {
        this.port = port;
        this.filePath = filePath;
    }

    public void start() {
        try {
            List<Organization> loadedOrganizations = JsonHandler.loadFromFile(filePath);
            collectionManager = new CollectionManager(loadedOrganizations);
            commandManager = new CommandManager(collectionManager, filePath);

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(port));
            System.out.println("Server started on port " + port);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("Сохранение коллекции перед завершением работы...");
                    JsonHandler.saveToFile(collectionManager.getAsList(), filePath);
                    System.out.println("Коллекция успешно сохранена в " + filePath);
                    serverChannel.close();
                } catch (IOException e) {
                    System.err.println("Ошибка при сохранении коллекции: " + e.getMessage());
                }
            }));

            Thread consoleThread = new Thread(this::handleServerConsole);
            consoleThread.setDaemon(true);
            consoleThread.start();

            while (true) {
                SocketChannel clientChannel = serverChannel.accept();
                System.out.println("New client connected: " + clientChannel.socket().getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientChannel.socket(), commandManager);
                clientHandler.run();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleServerConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Server> ");
            String command = scanner.nextLine().trim().toLowerCase();
            switch (command) {
                case "save_collection":
                    try {
                        JsonHandler.saveToFile(collectionManager.getAsList(), filePath);
                        System.out.println("Коллекция сохранена в " + filePath);
                    } catch (IOException e) {
                        System.err.println("Ошибка при сохранении: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Неизвестная команда сервера. Доступные команды: save_collection");
            }
        }
    }

    public static void main(String[] args) {
        int port = 8082;
        String filePath = Paths.get(System.getProperty("user.dir"), "orgs.json").toString();
        Server server = new Server(port, filePath);
        server.start();
    }
}