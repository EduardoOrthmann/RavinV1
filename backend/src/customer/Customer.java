package customer;

import address.Address;
import enums.Allergy;
import person.Person;
import user.User;

import java.time.LocalDate;
import java.util.Set;

public class Customer extends Person {
    private static int lastId = 0;
    private int id;
    private Set<Allergy> allergies;

    public Customer(String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, User user, Integer createdBy, Set<Allergy> allergies) {
        super(name, phoneNumber, birthDate, cpf, address, user, createdBy);
        this.id = ++lastId;
        this.allergies = allergies;
    }

    public Customer(int id, String name, String phoneNumber, LocalDate birthDate, String cpf, Address address, Integer updatedBy, Set<Allergy> allergies) {
        super(id, name, phoneNumber, birthDate, cpf, address, updatedBy);
        this.id = id;
        this.allergies = allergies;
    }

    public Set<Allergy> getAllergies() {
        return allergies;
    }

    public void setAllergies(Set<Allergy> allergies) {
        this.allergies = allergies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Customer {\n" +
                "\tid = " + id + "\n" +
                "\tallergies = " + allergies + "\n" +
                "} " + super.toString();
    }
}
