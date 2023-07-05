package interfaces;

import address.Address;
import enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Person {
    private static int lastId = 0;
    private int id;
    private String name;
    private String phoneNumber;
    private LocalDate birthDate;
    private String cpf;
    private Address address;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;
    private Role createdBy;
    private Role updatedBy;

    public Person(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Role role, Role createdBy) {
        this.role = role;
        this.id = ++lastId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.address = address;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.createdBy = createdBy;
        this.updatedBy = createdBy;
    }

    public Person() {
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        this.updatedAt = LocalDateTime.now();
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Role getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Role createdBy) {
        this.createdBy = createdBy;
    }

    public Role getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Role updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Person {" + "\n" +
                "\tid = " + id + ",\n" +
                "\tname = '" + name + '\'' + ",\n" +
                "\tphoneNumber = '" + phoneNumber + '\'' + ",\n" +
                "\tbirthDate = " + birthDate + ",\n" +
                "\tcpf = '" + cpf + '\'' + ",\n" +
                "\taddress =" + address + ",\n" +
                "\tisActive = " + isActive + ",\n" +
                "\tcreatedAt = " + createdAt + ",\n" +
                "\tupdatedAt = " + updatedAt + ",\n" +
                "\tcreatedBy = " + createdBy + ",\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
