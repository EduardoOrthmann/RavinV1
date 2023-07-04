package employee;

import address.Address;
import components.BooleanField;
import components.LocalDateField;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;

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
                var country = JOptionPane.showInputDialog("Insira o país:");
                var state = JOptionPane.showInputDialog("Insira o estado:");
                var city = JOptionPane.showInputDialog("Insira a cidade:");
                var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
                var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
                var street = JOptionPane.showInputDialog("Insira a rua:");
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

                var createdBy = employeeService.findById((int) selectedCreatedBy);

                employeeService.save(
                        new Employee(
                                name,
                                phoneNumber,
                                birthDate,
                                cpf,
                                new Address(country, state, city, zipCode, neighborhood, street),
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
                                    null,
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
                    var country = JOptionPane.showInputDialog("Insira o país:");
                    var state = JOptionPane.showInputDialog("Insira o estado:");
                    var city = JOptionPane.showInputDialog("Insira a cidade:");
                    var zipCode = JOptionPane.showInputDialog("Insira o CEP:");
                    var neighborhood = JOptionPane.showInputDialog("Insira o bairro:");
                    var street = JOptionPane.showInputDialog("Insira a rua:");
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
                                    new Address(country, state, city, zipCode, neighborhood, street),
                                    null,
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
                var country = JOptionPane.showInputDialog("Insira o país:", employee.getAddress().getCountry());
                var state = JOptionPane.showInputDialog("Insira o estado:", employee.getAddress().getState());
                var city = JOptionPane.showInputDialog("Insira a cidade:", employee.getAddress().getCity());
                var zipCode = JOptionPane.showInputDialog("Insira o CEP:", employee.getAddress().getZipCode());
                var neighborhood = JOptionPane.showInputDialog("Insira o bairro:", employee.getAddress().getNeighborhood());
                var street = JOptionPane.showInputDialog("Insira a rua:", employee.getAddress().getStreet());
                var isActive = BooleanField.showInputBooleanDialog("É um usuário ativo?", employee.getIsActive());
                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está atualizando esse funcionário?",
                        "Atualizado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
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
                var resignationDate = LocalDateField.showInputLocalDateDialog("Insira a data de demissão:", employee.getResignationDate());
                var isAvailable = BooleanField.showInputBooleanDialog("O usuário está disponível?", employee.getIsAvailable());

                var updatedBy = employeeService.findById((int) selectedUpdatedBy);

                employeeService.update(
                    new Employee(
                            id,
                            name,
                            phoneNumber,
                            birthDate,
                            cpf,
                            new Address(country, state, city, zipCode, neighborhood, street),
                            isActive,
                            updatedBy,
                            rg,
                            maritalStatus,
                            educationLevel,
                            position,
                            workCardNumber,
                            admissionDate,
                            resignationDate,
                            isAvailable
                    )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                employeeService.delete(employeeService.findById(id));
            }
        }
    }
}
