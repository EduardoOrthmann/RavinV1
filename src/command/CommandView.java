package command;

import customer.Customer;
import customer.CustomerService;
import employee.Employee;
import employee.EmployeeService;
import enums.OrderStatus;
import order.Order;
import order.OrderService;
import table.Table;
import table.TableService;

import javax.swing.*;

public class CommandView {
    public static void view() {
        var commandService = new CommandService();
        var tableService = new TableService();
        var customerService = new CustomerService();
        var orderService = new OrderService();
        var employeeService = new EmployeeService();

        String[] orderOptions = {"findById", "findAll", "save", "update", "delete", "addOrder", "deleteOrder", "closeCommand"};

        String selectedOrderOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos da comanda",
                JOptionPane.QUESTION_MESSAGE,
                null,
                orderOptions,
                null
        );

        switch (selectedOrderOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(commandService.findById(id));
            }

            case "findAll" -> System.out.println(commandService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();
                var customers = customerService.findAll();
                var customerIds = customers.stream().map(Customer::getId).toArray();
                var tables = tableService.findAll();
                var tableIds = tables.stream().map(Table::getId).toArray();

                if (employees.isEmpty() || customers.isEmpty() || tables.isEmpty()) {
                    System.out.println("Por favor crie um funcionário e um cliente e uma mesa antes de criar um pedido");
                    break;
                }

                var selectedTable = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma mesa:",
                        "Mesa",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        tableIds,
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

                var table = tableService.findById((int) selectedTable);
                var customer = customerService.findById((int) selectedCustomer);

                var status = (OrderStatus) JOptionPane.showInputDialog(
                        null,
                        "Selecione um status:",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        OrderStatus.values(),
                        null
                );

                var totalPrice = Double.parseDouble(JOptionPane.showInputDialog("Insira o preço total:"));
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando essa comanda?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                commandService.save(
                        new Command(
                                table,
                                customer,
                                status,
                                totalPrice,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                commandService.delete(commandService.findById(id));
            }

            case "addOrder" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (commands.isEmpty() || orders.isEmpty()) {
                    System.out.println("Por favor crie uma comanda e um pedido antes de adicionar um pedido a uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var command = commandService.findById((int) selectedCommand);
                var order = orderService.findById((int) selectedOrder);

                commandService.addOrder(command, order);
            }

            case "deleteOrder" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (commands.isEmpty() || orders.isEmpty()) {
                    System.out.println("Por favor crie uma comanda e um pedido antes de deletar um pedido de uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var command = commandService.findById((int) selectedCommand);
                var order = orderService.findById((int) selectedOrder);

                commandService.deleteOrder(command, order);
            }

            case "closeCommand" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();

                if (commands.isEmpty()) {
                    System.out.println("Por favor crie uma comanda antes de fechar uma comanda");
                    break;
                }

                var selectedCommand = JOptionPane.showInputDialog(
                        null,
                        "Selecione uma comanda:",
                        "Comandas",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );
                var cash = Double.parseDouble(JOptionPane.showInputDialog("Insira um valor"));

                var command = commandService.findById((int) selectedCommand);

                commandService.closeCommand(command, cash);
            }
        }
    }
}
