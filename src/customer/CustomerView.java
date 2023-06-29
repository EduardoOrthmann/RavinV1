package customer;

import address.Address;
import employee.Employee;
import employee.EmployeeService;
import enums.Allergy;

import javax.swing.*;
import java.time.LocalDate;

public class CustomerView {
    public static void view() {
        var customerService = new CustomerService();
        var employeeService = new EmployeeService();

        String[] customerOptions = {"findById", "findAll", "save", "update", "delete", "addAllergy", "deleteAllergy"};

        String selectedCustomerOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                customerOptions,
                null
        );

        switch (selectedCustomerOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(customerService.findById(id));
            }

            case "findAll" -> System.out.println(customerService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

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
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está criando esse cliente?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                customerService.save(
                        new Customer(
                                name,
                                phoneNumber,
                                LocalDate.of(yearOfBirthDate, monthOfBirthDate, dayOfBirthDate),
                                cpf,
                                new Address(country, state, city, zipCode, neighborhood, street),
                                employeeService.findById((int) createdBy)
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                customerService.delete(customerService.findById(id));
            }

            case "addAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    System.out.println("Por favor crie um cliente antes de adicionar uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.addAllergy(customer, allergy);
            }

            case "deleteAllergy" -> {
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (customers.isEmpty()) {
                    System.out.println("Por favor crie um cliente antes de remover uma alergia a um cliente");
                    break;
                }

                var allergy = (Allergy) JOptionPane.showInputDialog(
                        null,
                        "Escolha uma alergia",
                        "Alergias",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        Allergy.values(),
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cliente:",
                        "Cliente",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var customer = customerService.findById((int) selectedCustomer);

                customerService.deleteAllergy(customer, allergy);
            }
        }
    }
}
