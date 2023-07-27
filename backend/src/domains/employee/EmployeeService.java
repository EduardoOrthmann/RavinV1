package domains.employee;

import domains.user.User;
import domains.user.UserService;
import utils.Constants;
import utils.DateUtils;

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
        return employeeDAO.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.EMPLOYEE_NOT_FOUND));
    }

    public List<Employee> findAll() {
        return employeeDAO.findAll();
    }

    public Employee save(Employee entity) {
        if (userService.existsByUsername(entity.getUser().getUsername())) {
            throw new IllegalArgumentException(Constants.USERNAME_ALREADY_EXISTS);
        }

        if (DateUtils.getAge(entity.getBirthDate()) < Constants.MINIMUM_AGE) {
            throw new IllegalArgumentException("Funcionário deve ser maior de idade");
        }

        var user = userService.save(
                new User(
                        entity.getUser().getUsername(),
                        entity.getUser().getPassword(),
                        entity.getUser().getRole()
                )
        );
        return employeeDAO.save(
                new Employee(
                        entity.getName(),
                        entity.getPhoneNumber(),
                        entity.getBirthDate(),
                        entity.getCpf(),
                        entity.getAddress(),
                        user,
                        entity.getCreatedBy(),
                        entity.getRg(),
                        entity.getMaritalStatus(),
                        entity.getEducationLevel(),
                        entity.getPosition(),
                        entity.getWorkCardNumber()
                )
        );
    }

    public void update(Employee entity) {
        employeeDAO.update(entity);
    }

    public void delete(Employee entity) {
        employeeDAO.delete(entity);
    }

    public Employee findByUserId(int userId) {
        return employeeDAO.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("Funcionário não encontrado"));
    }
}
