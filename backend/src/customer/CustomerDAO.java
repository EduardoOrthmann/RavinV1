package customer;

import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CustomerDAO implements Crud<Customer> {
    private final List<Customer> customerList;

    public CustomerDAO() {
        this.customerList = new ArrayList<>();
    }

    @Override
    public Optional<Customer> findById(int id) {
        return customerList.stream()
                .filter(customer -> customer.getId() == id)
                .findFirst();
    }

    @Override
    public List<Customer> findAll() {
        return this.customerList;
    }

    @Override
    public void save(Customer entity) {
        this.customerList.add(entity);
    }

    @Override
    public void update(Customer entity) {
        var customer = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
        this.customerList.set(customerList.indexOf(customer), entity);
    }

    @Override
    public void delete(Customer entity) {
        var customer = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Cliente não encontrado"));
        this.customerList.remove(customer);
    }

    public Optional<Integer> findIdByUserId(int userId) {
        return customerList.stream()
                .filter(customer -> customer.getUser().getId() == userId)
                .map(Customer::getId)
                .findFirst();
    }
}
