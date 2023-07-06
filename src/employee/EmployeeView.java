package employee;

import address.Address;
import enums.Role;
import templates.AddressForm;
import templates.BooleanField;
import templates.LocalDateField;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;
import utils.ObjectUtils;

import javax.swing.*;
import java.time.LocalDate;

public class EmployeeView {
    public final EmployeeService employeeService;

    public EmployeeView(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public void view() {
        String[] employeeOptions = {"findById", "findAll", "save", "saveAdmin", "update", "delete"};

        String selectedEmployeeOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Funcionário",
                JOptionPane.QUESTION_MESSAGE,
                null,
                employeeOptions,
                null
        );

        switch (selectedEmployeeOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(employeeService.findById(id));
            }

            case "findAll" -> System.out.println(employeeService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um funcionário admin antes de criar um funcionário");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome:");
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                var birthDate = LocalDateField.showInputLocalDateDialog("Insira a data do nascimento:");
                var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                var selectedCreatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse funcionário?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var address = AddressForm.showInputLocalDateDialog("Insira o endereço");
                var role = (Role) JOptionPane.showInputDialog(
                        null,
                        "Insira um nível de acesso:",
                        "Nível de acesso",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Role.values(),
                        null
                );
                var rg = JOptionPane.showInputDialog("Insira o rg:");
                var maritalStatus = (MaritalStatus) JOptionPane.showInputDialog(
                        null,
                        "Insira um estado civil:",
                        "Estado civil",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        MaritalStatus.values(),
                        null
                );
                var educationLevel = (EducationLevel) JOptionPane.showInputDialog(
                        null,
                        "Insira um nível de educação:",
                        "Nível de educação",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        EducationLevel.values(),
                        null
                );
                var position = (Position) JOptionPane.showInputDialog(
                        null,
                        "Insira um cargo:",
                        "Cargo",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Position.values(),
                        null
                );
                var workCardNumber = JOptionPane.showInputDialog("Insira o número da carteira de trabalho:");

                var createdBy = employeeService.findById((int) selectedCreatedBy).getRole();

                employeeService.save(
                        new Employee(
                                name,
                                phoneNumber,
                                birthDate,
                                cpf,
                                address,
                                role,
                                createdBy,
                                rg,
                                maritalStatus,
                                educationLevel,
                                position,
                                workCardNumber
                        )
                );
            }

            case "saveAdmin" -> {
                if (JOptionPane.showConfirmDialog(null, "Deseja adicionar um admin padrão?") == JOptionPane.YES_OPTION) {
                    employeeService.save(
                            new Employee(
                                    "Eduardo",
                                    "47999197929",
                                    LocalDate.of(2003, 11, 27),
                                    "11111111111",
                                    new Address("Brasil", "SC", "Blumenau", "89022110", "Velha", "Rua dos caçadores"),
                                    Role.ADMIN,
                                    Role.SYSTEM,
                                    "123123123",
                                    MaritalStatus.SINGLE,
                                    EducationLevel.HIGHER_EDUCATION,
                                    Position.MANAGER,
                                    "123123123"
                            )
                    );
                } else {
                    var name = JOptionPane.showInputDialog("Insira um nome:");
                    var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                    var birthDate = LocalDateField.showInputLocalDateDialog("Insira a data do nascimento:");
                    var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                    var address = AddressForm.showInputLocalDateDialog("Insira o endereço");
                    var rg = JOptionPane.showInputDialog("Insira o rg:");
                    var maritalStatus = (MaritalStatus) JOptionPane.showInputDialog(
                            null,
                            "Insira um estado civil:",
                            "Estado civil",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            MaritalStatus.values(),
                            null
                    );
                    var educationLevel = (EducationLevel) JOptionPane.showInputDialog(
                            null,
                            "Insira um nível de educação:",
                            "Nível de educação",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            EducationLevel.values(),
                            null
                    );
                    var position = (Position) JOptionPane.showInputDialog(
                            null,
                            "Insira um cargo:",
                            "Cargo",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            Position.values(),
                            null
                    );
                    var workCardNumber = JOptionPane.showInputDialog("Insira o número da carteira de trabalho:");

                    employeeService.save(
                            new Employee(
                                    name,
                                    phoneNumber,
                                    birthDate,
                                    cpf,
                                    address,
                                    Role.ADMIN,
                                    Role.SYSTEM,
                                    rg,
                                    maritalStatus,
                                    educationLevel,
                                    position,
                                    workCardNumber
                            )
                    );
                }
            }

            case "update" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um funcionário antes de atualizar um funcionário");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Que funcionário você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var employee = employeeService.findById(id);

                var name = JOptionPane.showInputDialog("Insira um nome:", employee.getName());
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:", employee.getPhoneNumber());
                var birthDate = LocalDateField.showInputLocalDateDialog("Insira a data de nascimento:", employee.getBirthDate());
                var cpf = JOptionPane.showInputDialog("Insira o cpf:", employee.getCpf());
                var address = AddressForm.showInputLocalDateDialog("Insira o endereço", employee.getAddress());
                var role = (Role) JOptionPane.showInputDialog(
                        null,
                        "Insira um nível de acesso:",
                        "Nível de acesso",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Role.values(),
                        employee.getRole()
                );
                var rg = JOptionPane.showInputDialog("Insira o rg:", employee.getRg());
                var maritalStatus = (MaritalStatus) JOptionPane.showInputDialog(
                        null,
                        "Insira um estado civil:",
                        "Estado civil",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        MaritalStatus.values(),
                        employee.getMaritalStatus()
                );
                var educationLevel = (EducationLevel) JOptionPane.showInputDialog(
                        null,
                        "Insira um nível de educação:",
                        "Nível de educação",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        EducationLevel.values(),
                        employee.getEducationLevel()
                );
                var position = (Position) JOptionPane.showInputDialog(
                        null,
                        "Insira um cargo:",
                        "Cargo",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Position.values(),
                        employee.getPosition()
                );
                var workCardNumber = JOptionPane.showInputDialog("Insira o número da carteira de trabalho:", employee.getWorkCardNumber());
                var admissionDate = LocalDateField.showInputLocalDateDialog("Insira a data de admissão:", employee.getAdmissionDate());

                LocalDate resignationDate = null;
                if (employee.getResignationDate() != null) {
                    resignationDate = LocalDateField.showInputLocalDateDialog("Insira a data de demissão:", employee.getResignationDate());
                }

                var isAvailable = BooleanField.showInputBooleanDialog("O usuário está disponível?", employee.isAvailable());
                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está atualizando esse funcionário?",
                        "Atualizado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                var updatedEmployee = new Employee(
                  id,
                  name,
                  phoneNumber,
                  birthDate,
                  cpf,
                  address,
                  role,
                  updatedBy,
                  rg,
                  maritalStatus,
                  educationLevel,
                  position,
                  workCardNumber,
                  admissionDate,
                  resignationDate,
                  isAvailable
                );

                ObjectUtils.copyNonNullFields(updatedEmployee, employee);

                employeeService.update(employee);
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                employeeService.delete(employeeService.findById(id));
            }
        }
    }
}
