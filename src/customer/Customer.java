package customer;

import address.Address;
import employee.Employee;
import enums.Allergy;
import interfaces.Person;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Customer extends Person {
    private Set<Allergy> allergies;

    public Customer(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Employee createdBy) {
        super(name, phoneNumber, birthDate, cpf, address, createdBy);
        this.allergies = new HashSet<>();
    }

    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Employee updatedBy) {
        super(id, name, phoneNumber, birthDate, cpf, address, updatedBy);
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<Allergy> allergies) {
        this.allergies = allergies;
    }

    @Override
    public String toString() {
        return "Customer {\n" +
                "\tallergies = " + allergies + "\n" +
                "} " + super.toString();
    }
}
