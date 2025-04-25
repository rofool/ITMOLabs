package Lab5.Model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Генератор уникальных идентификаторов для объектов.
 * Использует AtomicLong для атомарного увеличения значения.
 */
public class IdGenerator {
    private static final AtomicLong counter = new AtomicLong(1);

    /**
     * Генерирует уникальный идентификатор.
     *
     * @return Уникальный идентификатор.
     */
    public static Long generateId() {
        return counter.getAndIncrement();
    }
}
