package command;

import employee.Employee;
import employee.EmployeeService;
import order.Order;
import order.OrderService;
import utils.ObjectUtils;

import javax.swing.*;

public class CommandView {
    private final CommandService commandService;
    private final OrderService orderService;
    private final EmployeeService employeeService;

    public CommandView(CommandService commandService, OrderService orderService, EmployeeService employeeService) {
        this.commandService = commandService;
        this.orderService = orderService;
        this.employeeService = employeeService;
    }

    public void view() {
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
                var orders = orderService.findAll();

                if (employees.isEmpty() || orders.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um pedido e um funcionário de criar uma comanda");
                    break;
                }

                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando essa comanda?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy).getRole();

                commandService.save(new Command(createdByEmployee));
            }

            case "update" -> {
                var commands = commandService.findAll();
                var commandIds = commands.stream().map(Command::getId).toArray();
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (commands.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie uma comanda antes de atualizar um comando");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Qual comando você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        commandIds,
                        null
                );

                var command = commandService.findById(id);

                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está alterando esse comando?",
                        "Alterado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                var updatedCommand = new Command(
                        id,
                        updatedBy
                );

                ObjectUtils.copyNonNullFields(updatedCommand, command);

                commandService.update(command);
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
                    JOptionPane.showMessageDialog(null, "Por favor crie uma comanda e um pedido antes de adicionar um pedido a uma comanda");
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
                    JOptionPane.showMessageDialog(null, "Por favor crie uma comanda e um pedido antes de deletar um pedido de uma comanda");
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
                    JOptionPane.showMessageDialog(null, "Por favor crie uma comanda antes de fechar uma comanda");
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
