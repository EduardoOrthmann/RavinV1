package domains.customer;

import domains.user.User;
import domains.user.UserService;
import utils.Constants;

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
        return customerDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }

    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    public Customer save(Customer entity) {
        if (userService.existsByUsername(entity.getUser().getUsername())) {
            throw new IllegalArgumentException(Constants.USERNAME_ALREADY_EXISTS);
        }

        var user = userService.save(
                new User(
                        entity.getUser().getUsername(),
                        entity.getUser().getPassword(),
                        entity.getUser().getRole()
                )
        );
        return customerDAO.save(
                new Customer(
                        entity.getName(),
                        entity.getPhoneNumber(),
                        entity.getBirthDate(),
                        entity.getCpf(),
                        entity.getAddress(),
                        user,
                        entity.getCreatedBy()
                )
        );
    }

    public void update(Customer entity) {
        customerDAO.update(entity);
    }

    public void delete(Customer entity) {
        customerDAO.delete(entity);
        userService.delete(entity.getUser());
    }

    public Customer findByUserId(int userId) {
        return customerDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }
}
