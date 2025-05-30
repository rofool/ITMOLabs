package Lab5.Utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

/**
 * Адаптер для сериализации и десериализации объекта ZonedDateTime
 * в/из JSON формата ISO 8601 при помощи библиотеки Gson.
 */
public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    /**
     * Сериализует объект ZonedDateTime в JSON строку в формате ISO 8601.
     *
     * @param src исходный объект ZonedDateTime.
     * @param typeOfSrc тип исходного объекта.
     * @param context контекст сериализации.
     * @return JSON элемент с текстовым представлением даты и времени.
     */
    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    /**
     * Десериализует JSON элемент в объект ZonedDateTime.
     *
     * @param json JSON элемент с датой и временем в формате ISO 8601.
     * @param typeOfT тип результата.
     * @param context контекст десериализации.
     * @return объект ZonedDateTime.
     * @throws JsonParseException если строка не может быть распарсена в ZonedDateTime.
     */
    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(json.getAsString());
    }
}
