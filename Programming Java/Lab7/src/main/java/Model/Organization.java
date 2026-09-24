package main.java.Model;

import java.time.ZonedDateTime;

/**
 * Доменная модель организации.
 * id и creationDate приходят из БД (INSERT ... RETURNING).
 */
public class Organization implements Comparable<Organization> {
    private final Long id;
    private final ZonedDateTime creationDate;

    private String name;
    private Coordinates coordinates;
    private Integer annualTurnover;     // может быть null
    private int employeesCount;         // > 0
    private OrganizationType type;      // != null
    private Address officialAddress;    // может быть null

    public Organization(Long id,
                        ZonedDateTime creationDate,
                        String name,
                        Coordinates coordinates,
                        Integer annualTurnover,
                        int employeesCount,
                        OrganizationType type,
                        Address officialAddress) {

        if (id == null || id <= 0) throw new IllegalArgumentException("ID должен быть положительным");
        if (creationDate == null)      throw new IllegalArgumentException("creationDate не может быть null");
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Имя не может быть пустым");
        if (coordinates == null)       throw new IllegalArgumentException("Координаты не могут быть null");
        if (employeesCount <= 0)       throw new IllegalArgumentException("employeesCount должен быть > 0");
        if (type == null)              throw new IllegalArgumentException("Тип организации не может быть null");
        if (annualTurnover != null && annualTurnover <= 0)
            throw new IllegalArgumentException("annualTurnover, если задан, должен быть > 0");

        this.id = id;
        this.creationDate = creationDate;
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }

    public Long getId() { return id; }
    public ZonedDateTime getCreationDate() { return creationDate; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public Integer getAnnualTurnover() { return annualTurnover; }
    public int getEmployeesCount() { return employeesCount; }
    public OrganizationType getType() { return type; }
    public Address getOfficialAddress() { return officialAddress; }


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
        String street = (officialAddress != null ? officialAddress.getStreet() : "null");
        String zip    = (officialAddress != null ? officialAddress.getZipCode() : "null");


        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        return "Organization{" +
                "id=" + id +
                ", creationDate=" + creationDate.format(fmt) +
                ", name='" + name + '\'' +
                ", coordinates=(" + coordinates.getX() + ", " + coordinates.getY() + ")" +
                ", annualTurnover=" + (annualTurnover == null ? "null" : annualTurnover) +
                ", employeesCount=" + employeesCount +
                ", type=" + type +
                ", street='" + street + '\'' +
                ", zipCode='" + zip + '\'' +
                '}';
    }

}
