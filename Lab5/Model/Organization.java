package Lab5.Model;

import java.time.ZonedDateTime;

/**
 * Класс, представляющий организацию.
 * Хранит информацию о названии организации, ее координатах, обороте, количестве сотрудников,
 * типе организации и официальном адресе.
 * Организация также имеет уникальный идентификатор, который генерируется автоматически,
 * а также дату создания, которая также генерируется автоматически.
 */
public class Organization implements Comparable<Organization> {
    private final Long id;
    private final ZonedDateTime creationDate;

    private String name;
    private Coordinates coordinates;
    private Integer annualTurnover;
    private int employeesCount;
    private OrganizationType type;
    private Address officialAddress;

    /**
     * Конструктор для создания новой организации с автоматической генерацией ID и даты создания.
     *
     * @param name            Имя организации. Не может быть null или пустым.
     * @param coordinates     Координаты организации. Не могут быть null.
     * @param annualTurnover  Годовой оборот организации. Может быть null, но если задан, должен быть больше 0.
     * @param employeesCount  Количество сотрудников. Должно быть больше 0.
     * @param type            Тип организации. Не может быть null.
     * @param officialAddress Официальный адрес организации. Может быть null.
     */
    public Organization(String name, Coordinates coordinates, Integer annualTurnover, int employeesCount,
                        OrganizationType type, Address officialAddress) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Имя не может быть пустым");
        if (coordinates == null)
            throw new IllegalArgumentException("Координаты не могут быть null");
        if (employeesCount <= 0)
            throw new IllegalArgumentException("Количество сотрудников должно быть больше 0");
        if (type == null)
            throw new IllegalArgumentException("Тип организации не может быть null");
        if (annualTurnover != null && annualTurnover <= 0)
            throw new IllegalArgumentException("Оборот, если задан, должен быть больше 0");

        this.id = IdGenerator.generateId();
        this.creationDate = ZonedDateTime.now();
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    /**
     * Конструктор для создания организации с заданными ID и датой создания.
     * Этот конструктор может использоваться для обновления существующей организации.
     *
     * @param id              Идентификатор организации. Не может быть null или отрицательным.
     * @param creationDate    Дата создания организации. Не может быть null.
     * @param name            Имя организации. Не может быть null или пустым.
     * @param coordinates     Координаты организации. Не могут быть null.
     * @param annualTurnover  Годовой оборот организации. Может быть null, но если задан, должен быть больше 0.
     * @param employeesCount  Количество сотрудников. Должно быть больше 0.
     * @param type            Тип организации. Не может быть null.
     * @param officialAddress Официальный адрес организации. Может быть null.
     */
    public Organization(Long id, ZonedDateTime creationDate,
                        String name, Coordinates coordinates, Integer annualTurnover,
                        int employeesCount, OrganizationType type, Address officialAddress) {

        if (id == null || id <= 0)
            throw new IllegalArgumentException("ID должен быть положительным и не null");
        if (creationDate == null)
            throw new IllegalArgumentException("Дата создания не может быть null");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Имя не может быть пустым");
        if (coordinates == null)
            throw new IllegalArgumentException("Координаты не могут быть null");
        if (employeesCount <= 0)
            throw new IllegalArgumentException("Количество сотрудников должно быть больше 0");
        if (type == null)
            throw new IllegalArgumentException("Тип организации не может быть null");
        if (annualTurnover != null && annualTurnover <= 0)
            throw new IllegalArgumentException("Оборот, если задан, должен быть больше 0");

        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    /**
     * Возвращает идентификатор организации.
     *
     * @return Идентификатор организации.
     */
    public Long getId() {
        return id;
    }

    /**
     * Возвращает дату создания организации.
     *
     * @return Дата создания организации.
     */
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Возвращает имя организации.
     *
     * @return Имя организации.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает координаты организации.
     *
     * @return Координаты организации.
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Возвращает годовой оборот организации.
     *
     * @return Годовой оборот организации.
     */
    public Integer getAnnualTurnover() {
        return annualTurnover;
    }

    /**
     * Возвращает количество сотрудников организации.
     *
     * @return Количество сотрудников организации.
     */
    public int getEmployeesCount() {
        return employeesCount;
    }

    /**
     * Возвращает тип организации.
     *
     * @return Тип организации.
     */
    public OrganizationType getType() {
        return type;
    }

    /**
     * Возвращает официальный адрес организации.
     *
     * @return Официальный адрес организации.
     */
    public Address getOfficialAddress() {
        return officialAddress;
    }

    /**
     * Сравнивает текущую организацию с другой организацией по количеству сотрудников.
     *
     * @param other Организация для сравнения.
     * @return Результат сравнения по числу сотрудников: отрицательное значение, если меньше,
     * положительное, если больше, 0 если равны.
     */
    @Override
    public int compareTo(Organization other) {
        return Integer.compare(this.employeesCount, other.employeesCount);
    }

    /**
     * Возвращает строковое представление организации.
     *
     * @return Строковое представление организации.
     */
    @Override
    public String toString() {
        return String.format("Organization{id=%d, name='%s', employees=%d}", id, name, employeesCount);
    }
}
