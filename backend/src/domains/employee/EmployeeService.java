package domains.employee;

import utils.Constants;
import utils.DateUtils;

import java.util.List;
import java.util.NoSuchElementException;

public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee findById(int id) {
        return employeeRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.EMPLOYEE_NOT_FOUND));
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee save(Employee entity) {
        if (DateUtils.getAge(entity.getBirthDate()) < Constants.MINIMUM_AGE) {
            throw new IllegalArgumentException(Constants.MINIMUM_AGE_NOT_REACHED);
        }

        if (existsByCpf(entity.getCpf())) {
            throw new IllegalArgumentException(Constants.CPF_ALREADY_EXISTS);
        }

        return employeeRepository.save(entity);
    }

    public void update(Employee entity) {
        if (!existsById(entity.getId())) {
            throw new NoSuchElementException(Constants.EMPLOYEE_NOT_FOUND);
        }

        employeeRepository.update(entity);
    }

    public void delete(int entityId) {
        Employee employee = findById(entityId);

        employee.setActive(false);
        employeeRepository.delete(employee);
    }

    public boolean existsById(int id) {
        return employeeRepository.findById(id).isPresent();
    }

    public Employee findByUserId(int userId) {
        return employeeRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
    }

    private boolean existsByCpf(String cpf) {
        return employeeRepository.findByCpf(cpf).isPresent();
    }
}
