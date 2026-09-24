package Lab5.Model;

/**
 * Класс, представляющий координаты организации.
 * Координаты содержат два значения: X и Y.
 */
public class Coordinates {
    private Integer x;
    private Long y;

    /**
     * Конструктор для создания объекта Coordinates.
     *
     * @param x Координата X.
     * @param y Координата Y.
     * @throws IllegalArgumentException Если x или y равны null.
     */
    public Coordinates(Integer x, Long y) {
        if (x == null || y == null)
            throw new IllegalArgumentException("Координаты не могут быть null");
        this.x = x;
        this.y = y;
    }

    /**
     * Получает значение координаты X.
     *
     * @return Координата X.
     */
    public Integer getX() {
        return x;
    }

    /**
     * Получает значение координаты Y.
     *
     * @return Координата Y.
     */
    public Long getY() {
        return y;
    }
}
