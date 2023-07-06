package table;

import customer.Customer;
import customer.CustomerService;
import employee.Employee;
import employee.EmployeeService;
import enums.TableStatus;

import javax.swing.*;

public class TableView {
    private final TableService tableService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    public TableView(TableService tableService, CustomerService customerService, EmployeeService employeeService) {
        this.tableService = tableService;
        this.customerService = customerService;
        this.employeeService = employeeService;
    }

    public void view() {
        String[] tableOptions = {"findById", "findAll", "save", "update", "delete", "addCustomer", "deleteCustomer"};

        String selectedTableOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos da mesa",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tableOptions,
                null
        );

        switch (selectedTableOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(tableService.findById(id));
            }

            case "findAll" -> System.out.println(tableService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira um nome para a mesa:");
                var tableNumber = Short.parseShort(JOptionPane.showInputDialog("Insira um número para a mesa:"));
                var maxCapacity = Short.parseShort(JOptionPane.showInputDialog("Insira a capacidade máxima da mesa:"));
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando esse cardápio?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy).getRole();

                tableService.save(
                        new Table(
                                name,
                                tableNumber,
                                maxCapacity,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                tableService.delete(tableService.findById(id));
            }

            case "update" -> {
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (tables.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie uma mesa antes de atualizar uma mesa");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Qual mesa você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );

                var table = tableService.findById(id);

                var name = JOptionPane.showInputDialog("Insira um nome:", table.getName());
                var tableNumber = Short.parseShort(JOptionPane.showInputDialog("Insira o número da mesa:", table.getTableNumber()));
                var maxCapacity = Short.parseShort(JOptionPane.showInputDialog("Insira a capacidade máxima:", table.getMaxCapacity()));
                var selectedStatus = JOptionPane.showInputDialog(
                        null,
                        "Qual é o status dessa mesa?",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        TableStatus.values(),
                        table.getStatus()
                );

                var status = (TableStatus) selectedStatus;
                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está alterando essa mesa?",
                        "Alterado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                table.setId(id);
                table.setName(name);
                table.setTableNumber(tableNumber);
                table.setMaxCapacity(maxCapacity);
                table.setStatus(status);
                table.setUpdatedBy(updatedBy);

                tableService.update(table);
            }

            case "addCustomer" -> {
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (tables.isEmpty() || customers.isEmpty()) {
                    System.out.println("Por favor crie uma mesa e um cliente antes de adicionar um cliente a uma mesa");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Escolha um cliente",
                        "Clientes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                tableService.addCustomer(table, customer);
            }

            case "deleteCustomer" -> {
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();

                if (tables.isEmpty() || customers.isEmpty()) {
                    System.out.println("Por favor crie uma mesa e um cliente antes de deletar um cliente de uma mesa");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
                        null
                );
                var selectedCustomer = JOptionPane.showInputDialog(
                        null,
                        "Escolha um cliente",
                        "Clientes",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        customerIds,
                        null
                );

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                tableService.deleteCustomer(table, customer);
            }
        }
    }
}
