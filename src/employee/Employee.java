package employee;

import address.Address;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import interfaces.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employee extends Person {
    private String rg;
    private MaritalStatus maritalStatus;
    private EducationLevel educationLevel;
    private Position position;
    private String workCardNumber;
    private LocalDate admissionDate;
    private LocalDate resignationDate;
    private boolean isAvailable;

    public Employee(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, Employee createdBy, Employee updatedBy, String rg, MaritalStatus maritalStatus, EducationLevel educationLevel, Position position, String workCardNumber, LocalDate admissionDate, LocalDate resignationDate, boolean isAvailable) {
        super(id, name, phoneNumber, birthDate, cpf, address, isActive, createdAt, updatedAt, createdBy, updatedBy);
        this.rg = rg;
        this.maritalStatus = maritalStatus;
        this.educationLevel = educationLevel;
        this.position = position;
        this.workCardNumber = workCardNumber;
        this.admissionDate = admissionDate;
        this.resignationDate = resignationDate;
        this.isAvailable = isAvailable;
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
}
