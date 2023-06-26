package customer;

import address.Address;
import employee.Employee;
import enums.Allergy;
import interfaces.Person;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class Customer extends Person {
    private Set<Allergy> allergies;

    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt, Employee createdBy, Employee updatedBy, Set<Allergy> allergies) {
        super(id, name, phoneNumber, birthDate, cpf, address, isActive, createdAt, updatedAt, createdBy, updatedBy);
        this.allergies = allergies;
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<Allergy> allergies) {
        this.allergies = allergies;
    }
}
