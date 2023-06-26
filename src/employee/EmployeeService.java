package employee;

import java.util.List;
import java.util.NoSuchElementException;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public Employee findById(int id) {
        return employeeDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
    }

    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }

    public void save(Employee entity) {
        employeeDAO.save(entity);
    }

    public void update(Employee entity) {
        employeeDAO.update(entity);
    }

    public void delete(Employee entity) {
        employeeDAO.delete(entity);
    }
}
