package employee;

import user.UserService;

import java.util.List;
import java.util.NoSuchElementException;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;
    private final UserService userService;

    public EmployeeService(EmployeeDAO employeeDAO, UserService userService) {
        this.employeeDAO = employeeDAO;
        this.userService = userService;
    }

    public Employee findById(int id) {
        return employeeDAO.findById(id).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
    }

    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }

    public Employee save(Employee entity) {
        userService.save(entity.getUser());
        return employeeDAO.save(entity);
    }

    public void update(Employee entity) {
        employeeDAO.update(entity);
    }

    public void delete(Employee entity) {
        employeeDAO.delete(entity);
        userService.delete(entity.getUser());
    }

    public Employee findByUserId(int userId) {
        return employeeDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
    }
}
