package domains.employee;

import interfaces.Crud;
import utils.Constants;

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
    public Employee save(Employee entity) {
        this.employeeList.add(entity);
        return entity;
    }

    @Override
    public void update(Employee entity) {
        var employee = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.EMPLOYEE_NOT_FOUND));
        this.employeeList.set(employeeList.indexOf(employee), entity);
    }

    @Override
    public void delete(Employee entity) {
        var employee = findById(entity.getId()).orElseThrow(() -> new NoSuchElementException(Constants.EMPLOYEE_NOT_FOUND));
        employee.setActive(false);
    }

    public Optional<Employee> findByUserId(int userId) {
        return employeeList.stream()
                .filter(employee -> employee.getUser().getId() == userId)
                .findFirst();
    }
}
