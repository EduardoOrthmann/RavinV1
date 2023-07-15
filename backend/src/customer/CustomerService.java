package customer;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer findById(int id) {
        return customerDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
    }

    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    public void save(Customer entity) {
        customerDAO.save(entity);
    }

    public void update(Customer entity) {
        customerDAO.update(entity);
    }

    public void delete(Customer entity) {
        customerDAO.delete(entity);
    }

    public boolean isBirthday(Customer customer) {
        var today = LocalDate.now();
        return customer.getBirthDate().getMonth() == today.getMonth() && customer.getBirthDate().getDayOfMonth() == today.getDayOfMonth();
    }
}
