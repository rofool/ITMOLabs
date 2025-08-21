package Lab5.Model;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong counter = new AtomicLong(1);

    public static Long generateId() {
        return counter.getAndIncrement();
    }

    /**
     * Обновляет значение счетчика, если переданное больше текущего.
     * Нужно вызывать после загрузки коллекции, чтобы избежать дублирования ID.
     *
     * @param maxId максимальное существующее значение id
     */
    public static void updateCounter(long maxId) {
        counter.updateAndGet(current -> Math.max(current, maxId + 1));
    }
}
