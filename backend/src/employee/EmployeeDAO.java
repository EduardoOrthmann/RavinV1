package employee;

import customer.Customer;
import interfaces.Crud;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EmployeeDAO implements Crud<Employee> {
    private final List<Employee> employeeList;

    public EmployeeDAO() {
        this.employeeList = new ArrayList<>();
    }

    @Override
    public Optional<Employee> findById(int id) {
        return employeeList.stream()
                .filter(employee -> employee.getId() == id)
                .findFirst();
    }

    @Override
    public List<Employee> findAll() {
        return this.employeeList;
    }

    @Override
    public void save(Employee entity) {
        this.employeeList.add(entity);
    }

    @Override
    public void update(Employee entity) {
        var employee = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
        this.employeeList.set(employeeList.indexOf(employee), entity);
    }

    @Override
    public void delete(Employee entity) {
        var employee = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
        this.employeeList.remove(employee);
    }

    public Optional<Integer> findIdByUserId(int userId) {
        return employeeList.stream()
                .filter(employee -> employee.getUser().getId() == userId)
                .map(Employee::getId)
                .findFirst();
    }
}
