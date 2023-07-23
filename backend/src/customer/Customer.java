package customer;

import address.Address;
import person.Person;
import user.User;

import java.time.LocalDate;

public class Customer extends Person {
    private static int lastId = 0;
    private int id;

    public Customer(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, User user, Integer createdBy) {
        super(name, phoneNumber, birthDate, cpf, address, user, createdBy);
        this.id = ++lastId;
    }

    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Integer updatedBy) {
        super(id, name, phoneNumber, birthDate, cpf, address, updatedBy);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
