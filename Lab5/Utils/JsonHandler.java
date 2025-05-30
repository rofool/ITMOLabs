package Lab5.Utils;

import Lab5.Model.Organization;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Утилитарный класс для работы с JSON-файлами, содержащими коллекции организаций.
 * Использует библиотеку Gson для сериализации и десериализации.
 */
public class JsonHandler {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Загружает список организаций из JSON-файла.
     *
     * @param filePath путь к JSON-файлу.
     * @return список организаций.
     * @throws IOException в случае ошибки чтения файла.
     */
    public static List<Organization> loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Type listType = new TypeToken<List<Organization>>() {}.getType();
            return gson.fromJson(reader, listType);
        }
    }

    /**
     * Сохраняет список организаций в JSON-файл.
     *
     * @param organizations список организаций для сохранения.
     * @param filePath путь к JSON-файлу.
     * @throws IOException в случае ошибки записи файла.
     */
    public static void saveToFile(List<Organization> organizations, String filePath) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath))) {
            gson.toJson(organizations, writer);
        }
    }
}
