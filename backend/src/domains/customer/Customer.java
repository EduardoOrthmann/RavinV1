package domains.customer;

import domains.address.Address;
import domains.person.Person;
import domains.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Customer extends Person {
    private int id;
    private Integer tableId;

    public Customer(int id, Integer tableId, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, boolean isActive, User user, LocalDateTime createdAt, LocalDateTime updatedAt, Integer createdBy, Integer updatedBy) {
        super(name, phoneNumber, birthDate, cpf, address, isActive, user, createdAt, updatedAt, createdBy, updatedBy);
        this.id = id;
        this.tableId = tableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
}
