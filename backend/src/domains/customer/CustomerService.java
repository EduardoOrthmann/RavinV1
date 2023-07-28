package domains.customer;

import enums.Role;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public Customer findById(int id) {
        return customerDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }

    public List<Customer> findAll() {
        return customerDAO.findAll();
    }

    public Customer save(Customer entity) {
        if (existsByCpf(entity.getCpf())) {
            throw new IllegalArgumentException(Constants.CPF_ALREADY_EXISTS);
        }

        entity.getUser().setRole(Role.CUSTOMER);
        return customerDAO.save(entity);
    }

    public void update(Customer entity) {
        if (!existsById(entity.getId())) {
            throw new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND);
        }

        customerDAO.update(entity);
    }

    public void delete(int entityId) {
        Customer customer = findById(entityId);

        customer.setActive(false);
        customerDAO.delete(customer);
    }

    public Customer findByUserId(int userId) {
        return customerDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }

    public boolean existsByCpf(String cpf) {
        return customerDAO.findByCpf(cpf).isPresent();
    }

    public boolean existsById(int id) {
        return customerDAO.findById(id).isPresent();
    }
}
