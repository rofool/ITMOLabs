package Lab5.Utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

/**
 * Адаптер для сериализации и десериализации объектов типа ZonedDateTime.
 * Используется для преобразования объектов типа ZonedDateTime в формат ISO 8601 при сериализации
 * и обратно в объект ZonedDateTime при десериализации.
 */
public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    /**
     * Сериализует объект ZonedDateTime в строку в формате ISO 8601.
     *
     * @param src       Объект ZonedDateTime, который нужно сериализовать.
     * @param typeOfSrc Тип исходного объекта (не используется в данном случае).
     * @param context   Контекст сериализации (не используется в данном случае).
     * @return JSON-элемент, представляющий строку в формате ISO 8601.
     */
    @Override
    public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    /**
     * Десериализует строку в формате ISO 8601 в объект ZonedDateTime.
     *
     * @param json    JSON-элемент, представляющий строку с датой в формате ISO 8601.
     * @param typeOfT Тип целевого объекта (не используется в данном случае).
     * @param context Контекст десериализации (не используется в данном случае).
     * @return Объект ZonedDateTime, который был десериализован из строки.
     * @throws JsonParseException Если строка не может быть преобразована в объект ZonedDateTime.
     */
    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(json.getAsString());
    }
}
