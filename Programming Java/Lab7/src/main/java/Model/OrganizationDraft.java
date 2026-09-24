package main.java.Model;

public class OrganizationDraft {
    public String name;
    public Coordinates coordinates;
    public Integer annualTurnover;
    public int employeesCount;
    public OrganizationType type;
    public Address officialAddress;

    public OrganizationDraft() {}

    public OrganizationDraft(String name, Coordinates coordinates, Integer annualTurnover,
                             int employeesCount, OrganizationType type, Address officialAddress) {
        this.name = name;
        this.coordinates = coordinates;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.type = type;
        this.officialAddress = officialAddress;
    }
}
