package Lab5.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start(String[] args) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             Scanner scanner = new Scanner(System.in)) {
            System.out.println("Подключено к серверу. Введите команду (введите 'help' для списка):");
            String[] commands = (args.length > 0) ? args : new String[0];
            boolean useArgs = commands.length > 0;

            while (true) {
                String command;
                if (useArgs && commands.length > 0) {
                    command = commands[0];
                    String[] newCommands = new String[commands.length - 1];
                    System.arraycopy(commands, 1, newCommands, 0, newCommands.length);
                    commands = newCommands;
                    if (commands.length == 0) useArgs = false;
                } else {
                    System.out.print("> ");
                    command = scanner.nextLine().trim();
                }
                if (command.isEmpty()) continue;

                String[] parts = command.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String argsStr = parts.length > 1 ? parts[1].trim() : "";

                if ((commandName.equals("add") || commandName.equals("add_if_max") || commandName.equals("add_if_min") || commandName.equals("update")) && argsStr.isEmpty() && !useArgs) {
                    argsStr = promptInteractiveInput(commandName, scanner);
                } else if (commandName.equals("execute_script") && !argsStr.isEmpty()) {
                    executeScript(argsStr, out, in);
                    continue; // После выполнения скрипта ждем следующую команду
                } else if (!argsStr.isEmpty() && !commandName.equals("remove_by_id") && !commandName.equals("filter_by_annual_turnover") && !commandName.equals("remove_any_by_employees_count")) {
                    argsStr = convertToJson(commandName, argsStr.split("\\s+"));
                }

                String fullCommand = commandName + (argsStr.isEmpty() ? "" : " " + argsStr);
                System.out.println("Отправка команды: " + fullCommand); // Отладка
                out.println(fullCommand);

                StringBuilder response = new StringBuilder();
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.isEmpty()) break;
                        response.append(line).append("\n");
                    }
                    if (response.length() > 0) {
                        System.out.println(response.toString().trim());
                    } else {
                        System.out.println("Нет ответа от сервера для команды: " + command);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка чтения ответа: " + e.getMessage());
                    // Сбрасываем буферы
                    out.flush();
                    continue; // Продолжаем цикл
                }

                if (useArgs && commands.length == 0) break;
            }
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
        }
    }

    private String promptInteractiveInput(String commandName, Scanner scanner) {
        if ("add".equals(commandName) || "add_if_max".equals(commandName) || "add_if_min".equals(commandName)) {
            System.out.println("Введите название организации:");
            String name = scanner.nextLine().trim();
            System.out.println("Введите координату X (int):");
            String x = scanner.nextLine().trim();
            System.out.println("Введите координату Y (long):");
            String y = scanner.nextLine().trim();
            System.out.println("Введите годовой оборот (можно оставить пустым):");
            String turnover = scanner.nextLine().trim();
            System.out.println("Введите количество сотрудников (int):");
            String employees = scanner.nextLine().trim();
            System.out.println("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY):");
            String type = scanner.nextLine().trim().toUpperCase();
            System.out.println("Введите улицу (можно оставить пустой):");
            String street = scanner.nextLine().trim();
            System.out.println("Введите ZIP-код:");
            String zip = scanner.nextLine().trim();
            return String.format("{\"name\":\"%s\",\"coordinates\":{\"x\":%s,\"y\":%s},\"annualTurnover\":%s,\"employeesCount\":%s,\"type\":\"%s\",\"officialAddress\":{\"street\":\"%s\",\"zipCode\":\"%s\"}}",
                    name, x, y, turnover.isEmpty() ? "null" : turnover, employees, type, street.isEmpty() ? "null" : street, zip);
        } else if ("update".equals(commandName)) {
            System.out.println("Введите ID организации:");
            String id = scanner.nextLine().trim();
            System.out.println("Введите название организации:");
            String name = scanner.nextLine().trim();
            System.out.println("Введите координату X (int):");
            String x = scanner.nextLine().trim();
            System.out.println("Введите координату Y (long):");
            String y = scanner.nextLine().trim();
            System.out.println("Введите годовой оборот (можно оставить пустым):");
            String turnover = scanner.nextLine().trim();
            System.out.println("Введите количество сотрудников (int):");
            String employees = scanner.nextLine().trim();
            System.out.println("Введите тип организации (COMMERCIAL, PUBLIC, PRIVATE_LIMITED_COMPANY):");
            String type = scanner.nextLine().trim().toUpperCase();
            System.out.println("Введите улицу (можно оставить пустой):");
            String street = scanner.nextLine().trim();
            System.out.println("Введите ZIP-код:");
            String zip = scanner.nextLine().trim();
            return String.format("{\"id\":%s,\"name\":\"%s\",\"coordinates\":{\"x\":%s,\"y\":%s},\"annualTurnover\":%s,\"employeesCount\":%s,\"type\":\"%s\",\"officialAddress\":{\"street\":\"%s\",\"zipCode\":\"%s\"}}",
                    id, name, x, y, turnover.isEmpty() ? "null" : turnover, employees, type, street.isEmpty() ? "null" : street, zip);
        }
        return "";
    }

    private String convertToJson(String commandName, String[] args) {
        StringBuilder json = new StringBuilder("{");
        if ("add".equals(commandName) || "add_if_max".equals(commandName) || "add_if_min".equals(commandName)) {
            if (args.length >= 8) {
                json.append("\"name\":\"").append(args[0]).append("\",")
                        .append("\"coordinates\":{\"x\":").append(args[1]).append(",\"y\":").append(args[2]).append("},")
                        .append("\"annualTurnover\":").append(args[3].isEmpty() ? "null" : args[3]).append(",")
                        .append("\"employeesCount\":").append(args[4]).append(",")
                        .append("\"type\":\"").append(args[5]).append("\",")
                        .append("\"officialAddress\":{\"street\":\"").append(args[6].isEmpty() ? "null" : args[6]).append("\",\"zipCode\":\"").append(args[7]).append("\"}");
            }
        } else if ("update".equals(commandName)) {
            if (args.length >= 9) {
                json.append("\"id\":").append(args[0]).append(",")
                        .append("\"name\":\"").append(args[1]).append("\",")
                        .append("\"coordinates\":{\"x\":").append(args[2]).append(",\"y\":").append(args[3]).append("},")
                        .append("\"annualTurnover\":").append(args[4].isEmpty() ? "null" : args[4]).append(",")
                        .append("\"employeesCount\":").append(args[5]).append(",")
                        .append("\"type\":\"").append(args[6]).append("\",")
                        .append("\"officialAddress\":{\"street\":\"").append(args[7].isEmpty() ? "null" : args[7]).append("\",\"zipCode\":\"").append(args[8]).append("\"}");
            }
        }
        json.append("}");
        return json.toString();
    }

    private String readScriptContent(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString().trim();
        } catch (IOException e) {
            return "Ошибка чтения файла скрипта: " + e.getMessage();
        }
    }

    private void executeScript(String fileName, PrintWriter out, BufferedReader in) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // Пропуск пустых и комментариев
                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String argsStr = parts.length > 1 ? parts[1].trim() : "";
                if (!argsStr.isEmpty() && !commandName.equals("remove_by_id") && !commandName.equals("filter_by_annual_turnover") && !commandName.equals("remove_any_by_employees_count")) {
                    argsStr = convertToJson(commandName, argsStr.split("\\s+"));
                }
                String fullCommand = commandName + (argsStr.isEmpty() ? "" : " " + argsStr);
                System.out.println("Выполняется команда из скрипта: " + fullCommand);
                out.println(fullCommand);

                StringBuilder response = new StringBuilder();
                String respLine;
                while ((respLine = in.readLine()) != null) {
                    if (respLine.isEmpty()) break;
                    response.append(respLine).append("\n");
                }
                if (response.length() > 0) {
                    System.out.println(response.toString().trim());
                }
            }
            System.out.println("Скрипт выполнился успешно.");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла скрипта: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8082;
        Client client = new Client(host, port);
        client.start(args);
    }
}