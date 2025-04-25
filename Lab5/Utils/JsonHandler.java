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
 * Класс для работы с сериализацией и десериализацией данных в формате JSON.
 * Использует библиотеку Gson для преобразования объектов в JSON и обратно.
 * Также использует адаптер для сериализации/десериализации объектов типа ZonedDateTime.
 */
public class JsonHandler {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();

    /**
     * Загружает список организаций из файла в формате JSON.
     *
     * @param filePath Путь к файлу, из которого будут загружены данные.
     * @return Список организаций, загруженных из файла.
     * @throws IOException Если возникает ошибка при чтении файла.
     */
    public static List<Organization> loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Type listType = new TypeToken<List<Organization>>() {
            }.getType();
            return gson.fromJson(reader, listType); // Десериализация JSON в список организаций
        }
    }

    /**
     * Сохраняет список организаций в файл в формате JSON.
     *
     * @param organizations Список организаций, который нужно сохранить в файл.
     * @param filePath      Путь к файлу, в который будут сохранены данные.
     * @throws IOException Если возникает ошибка при записи в файл.
     */
    public static void saveToFile(List<Organization> organizations, String filePath) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath))) {
            gson.toJson(organizations, writer);
        }
    }
}
