package customer;

import address.Address;
import enums.Allergy;
import enums.Role;
import interfaces.Person;

import java.time.LocalDate;
import java.util.Set;

public class Customer extends Person {
    private Set<Allergy> allergies;

    public Customer(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Role createdBy, Set<Allergy> allergies) {
        super(name, phoneNumber, birthDate, cpf, address, Role.USER, createdBy);
        this.allergies = allergies;
    }

    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Role updatedBy, Set<Allergy> allergies) {
        super(id, name, phoneNumber, birthDate, cpf, address, Role.USER, updatedBy);
        this.allergies = allergies;
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
