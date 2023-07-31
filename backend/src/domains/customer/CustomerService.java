package domains.customer;

import enums.Role;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer findById(int id) {
        return customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer save(Customer entity) {
        if (existsByCpf(entity.getCpf())) {
            throw new IllegalArgumentException(Constants.CPF_ALREADY_EXISTS);
        }

        entity.getUser().setRole(Role.CUSTOMER);
        return customerRepository.save(entity);
    }

    public void update(Customer entity) {
        if (!existsById(entity.getId())) {
            throw new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND);
        }

        customerRepository.update(entity);
    }

    public void delete(int entityId) {
        customerRepository.delete(findById(entityId));
    }

    public Customer findByUserId(int userId) {
        return customerRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException(Constants.CUSTOMER_NOT_FOUND));
    }

    public boolean existsByCpf(String cpf) {
        return customerRepository.findByCpf(cpf).isPresent();
    }

    public boolean existsById(int id) {
        return customerRepository.findById(id).isPresent();
    }

    public List<Customer> findAllByTableId(int tableId) {
        return customerRepository.findAllByTableId(tableId);
    }
}
