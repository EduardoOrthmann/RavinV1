package employee;

import address.Address;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import person.Person;
import user.User;

import java.time.LocalDate;

public class Employee extends Person {
    private static int lastId = 0;
    private int id;
    private String rg;
    private MaritalStatus maritalStatus;
    private EducationLevel educationLevel;
    private Position position;
    private String workCardNumber;
    private LocalDate admissionDate;
    private LocalDate resignationDate;
    private boolean isAvailable;

    public Employee(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, User user, Integer createdBy, String rg, MaritalStatus maritalStatus, EducationLevel educationLevel, Position position, String workCardNumber) {
        super(name, phoneNumber, birthDate, cpf, address, user, createdBy);
        this.id = ++lastId;
        this.rg = rg;
        this.maritalStatus = maritalStatus;
        this.educationLevel = educationLevel;
        this.position = position;
        this.workCardNumber = workCardNumber;
        this.admissionDate = LocalDate.now();
        this.isAvailable = true;
    }

    public Employee(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, User user, Integer updatedBy, String rg, MaritalStatus maritalStatus, EducationLevel educationLevel, Position position, String workCardNumber, LocalDate admissionDate, boolean isAvailable) {
        super(id, name, phoneNumber, birthDate, cpf, address, user, updatedBy);
        this.id = id;
        this.rg = rg;
        this.maritalStatus = maritalStatus;
        this.educationLevel = educationLevel;
        this.position = position;
        this.workCardNumber = workCardNumber;
        this.admissionDate = admissionDate;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getWorkCardNumber() {
        return workCardNumber;
    }

    public void setWorkCardNumber(String workCardNumber) {
        this.workCardNumber = workCardNumber;
    }

    public LocalDate getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDate admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Employee {\n" +
                "\tid = " + id + "\n" +
                "\trg = '" + rg + '\'' + "\n" +
                "\tmaritalStatus = " + maritalStatus + "\n" +
                "\teducationLevel = " + educationLevel + "\n" +
                "\tposition = " + position + "\n" +
                "\tworkCardNumber = '" + workCardNumber + '\'' + "\n" +
                "\tadmissionDate = " + admissionDate + "\n" +
                "\tresignationDate = " + resignationDate + "\n" +
                "\tisAvailable = " + isAvailable + "\n" +
                "} " + super.toString();
    }
}
