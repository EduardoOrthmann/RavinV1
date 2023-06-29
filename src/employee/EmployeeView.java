package employee;

import address.Address;
import enums.EducationLevel;
import enums.MaritalStatus;
import enums.Position;

import javax.swing.*;
import java.time.LocalDate;

public class EmployeeView {
    public static void view() {
        var employeeService = new EmployeeService();

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
                    System.out.println("Por favor crie um funcionário admin antes de criar um funcionário");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome:");
                var phoneNumber = JOptionPane.showInputDialog("Insira um número de celular:");
                var dayOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o dia do nascimento:"));
                var monthOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o mês do nascimento:"));
                var yearOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o ano do nascimento:"));
                var cpf = JOptionPane.showInputDialog("Insira o cpf:");
                var createdBy = JOptionPane.showInputDialog(
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

                employeeService.save(
                        new Employee(
                                name,
                                phoneNumber,
                                LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
                                cpf,
                                new Address(country, state, city, zipCode, neighborhood, street),
                                employeeService.findById((int) createdBy),
                                rg,
                                maritalStatus,
                                educationLevel,
                                position,
                                workCardNumber
                        )
                );
            }

            case "saveAdmin" -> {
                if (JOptionPane.showConfirmDialog(null, "Se deseja adicionar um admin padrão?") == JOptionPane.YES_OPTION) {
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
                    var dayOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o dia do nascimento:"));
                    var monthOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o mês do nascimento:"));
                    var yearOfBirthDate = Integer.parseInt(JOptionPane.showInputDialog("Insira o ano do nascimento:"));
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
                                    LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
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

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                employeeService.delete(employeeService.findById(id));
            }
        }
    }
}
