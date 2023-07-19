package person;

import address.Address;
import user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class Person {
    private String name;
    private String phoneNumber;
    private LocalDate birthDate;
    private String cpf;
    private Address address;
    private boolean isActive;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer updatedBy;

    public Person(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, User user, Integer createdBy) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.address = address;
        this.user = user;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public Person(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Integer updatedBy) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Person {" + "\n" +
                "\tname = '" + name + '\'' + ",\n" +
                "\tphoneNumber = '" + phoneNumber + '\'' + ",\n" +
                "\tbirthDate = " + birthDate + ",\n" +
                "\tcpf = '" + cpf + '\'' + ",\n" +
                "\taddress =" + address + ",\n" +
                "\tuser = " + user + ",\n" +
                "\tisActive = " + isActive + ",\n" +
                "\tcreatedAt = " + createdAt + ",\n" +
                "\tupdatedAt = " + updatedAt + ",\n" +
                "\tcreatedBy = " + createdBy + ",\n" +
                "\tupdatedBy = " + updatedBy + "\n" +
                '}';
    }
}
