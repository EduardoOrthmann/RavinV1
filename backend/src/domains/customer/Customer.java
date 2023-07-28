package domains.customer;

import domains.address.Address;
import domains.person.Person;
import domains.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Customer extends Person {
    private int id;

    // all args
    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, boolean isActive, User user, LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        super(name, phoneNumber, birthDate, cpf, address, isActive, user, createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
