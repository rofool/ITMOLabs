package Lab5.Model;

/**
 * Класс, представляющий адрес организации.
 * Адрес содержит информацию об улице и почтовом индексе.
 */
public class Address {
    private String street;
    private String zipCode;

    /**
     * Конструктор для создания объекта Address.
     *
     * @param street Улица. Может быть null.
     * @param zipCode Почтовый индекс. Не может быть null, длина не более 18 символов.
     * @throws IllegalArgumentException Если zipCode равен null или его длина превышает 18 символов,
     * или если street задана и пустая.
     */
    public Address(String street, String zipCode) {
        if (zipCode == null || zipCode.length() > 18)
            throw new IllegalArgumentException("ZipCode не может быть null и должен быть <= 18 символов");
        if (street != null && street.trim().isEmpty())
            throw new IllegalArgumentException("Улица, если задана, не может быть пустой");

        this.street = street;
        this.zipCode = zipCode;
    }

    /**
     * Получает значение поля street (улица).
     *
     * @return Значение улицы или null, если оно не задано.
     */
    public String getStreet() {
        return street;
    }

    /**
     * Получает значение поля zipCode (почтовый индекс).
     *
     * @return Почтовый индекс.
     */
    public String getZipCode() {
        return zipCode;
    }
}
