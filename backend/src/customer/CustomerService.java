package customer;

import user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class CustomerService {
    private final CustomerDAO customerDAO;
    private final UserService userService;

    public CustomerService(CustomerDAO customerDAO, UserService userService) {
        this.customerDAO = customerDAO;
        this.userService = userService;
    }

    public Customer findById(int id) {
        return customerDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
    }

    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    public void save(Customer entity) {
        userService.save(entity.getUser());
        customerDAO.save(entity);
    }

    public void update(Customer entity) {
        customerDAO.update(entity);
    }

    public void delete(Customer entity) {
        customerDAO.delete(entity);
        userService.delete(entity.getUser());
    }

    public int findIdByUserId(int userId) {
        return customerDAO.findIdByUserId(userId).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
    }
}
