package Lab5.Model;

import com.google.gson.annotations.SerializedName;
import java.time.ZonedDateTime;

/**
 * Класс, представляющий организацию.
 */
public class Organization implements Comparable<Organization> {
    private final Long id;
    @SerializedName("creationDate")
    private final ZonedDateTime creationDate;

    private String name;
    private Coordinates coordinates;
    private Integer annualTurnover;
    private int employeesCount;
    private OrganizationType type;
    private Address officialAddress;

    // Конструкторы остаются без изменений
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

    // Геттеры
    public Long getId() { return id; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public Integer getAnnualTurnover() { return annualTurnover; }
    public int getEmployeesCount() { return employeesCount; }
    public OrganizationType getType() { return type; }
    public Address getOfficialAddress() { return officialAddress; }

    // Сеттеры
    public void setName(String name) { this.name = name; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public void setAnnualTurnover(Integer annualTurnover) { this.annualTurnover = annualTurnover; }
    public void setEmployeesCount(int employeesCount) { this.employeesCount = employeesCount; }
    public void setType(OrganizationType type) { this.type = type; }
    public void setOfficialAddress(Address officialAddress) { this.officialAddress = officialAddress; }

    @Override
    public int compareTo(Organization other) {
        return Integer.compare(this.employeesCount, other.employeesCount);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=(" + coordinates.getX() + ", " + coordinates.getY() + ")" +
                ", annualTurnover=" + (annualTurnover != null ? annualTurnover : "null") +
                ", employeesCount=" + employeesCount +
                ", type=" + type +
                ", street='" + (officialAddress != null ? officialAddress.getStreet() : "null") + '\'' +
                ", zipCode='" + (officialAddress != null ? officialAddress.getZipCode() : "null") + '\'' +
                '}';
    }
}