package order;

import employee.Employee;
import employee.EmployeeService;
import enums.ProductStatus;
import product.Product;
import product.ProductService;

import javax.swing.*;

public class OrderView {
    public static void view() {
        var orderService = new OrderService();
        var productService = new ProductService();
        var employeeService = new EmployeeService();

        String[] orderOptions = {"findById", "findAll", "save", "update", "delete", "addNote", "updateNote", "deleteNote"};

        String selectedOrderOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do pedido",
                JOptionPane.QUESTION_MESSAGE,
                null,
                orderOptions,
                null
        );

        switch (selectedOrderOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(orderService.findById(id));
            }

            case "findAll" -> System.out.println(orderService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (employees.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um funcionário e um produto antes de criar um pedido");
                    break;
                }

                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Selecione um produto:",
                        "Produto",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );
                var selectedEmployee = JOptionPane.showInputDialog(
                        null,
                        "Selecione um funcionário:",
                        "Funcionário",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var product = productService.findById((int) selectedProduct);
                var employee = employeeService.findById((int) selectedEmployee);
                var quantity = Integer.parseInt(JOptionPane.showInputDialog("Insira a quantidade:"));
                var status = (ProductStatus) JOptionPane.showInputDialog(
                        null,
                        "Selecione um status:",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ProductStatus.values(),
                        null
                );
                var createdBy = JOptionPane.showInputDialog(
                        null,
                        "Quem esta criando esse cardápio?",
                        "Criado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );
                var createdByEmployee = employeeService.findById((int) createdBy);

                orderService.save(
                        new Order(
                                product,
                                employee,
                                quantity,
                                status,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                orderService.delete(orderService.findById(id));
            }

            case "addNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de adicionar uma observação a um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );
                var note = JOptionPane.showInputDialog("Insira uma observação ao pedido");

                var order = orderService.findById((int) selectedOrder);

                orderService.addNote(order, note);
            }

            case "updateNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de atualizar uma observação a um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var order = orderService.findById((int) selectedOrder);

                var note = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecione uma observação para atualizar:",
                        "Observações",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        order.getNotes().toArray(),
                        null
                );

                var updatedNote = JOptionPane.showInputDialog("Insira a atualização desejada");

                orderService.updateNote(order, note, updatedNote);
            }

            case "deleteNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    System.out.println("Por favor crie um pedido antes de remover uma observação de um pedido");
                    break;
                }

                var selectedOrder = JOptionPane.showInputDialog(
                        null,
                        "Selecione um pedido:",
                        "Pedidos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var order = orderService.findById((int) selectedOrder);

                var note = (String) JOptionPane.showInputDialog(
                        null,
                        "Selecione uma observação para deletar:",
                        "Observações",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        order.getNotes().toArray(),
                        null
                );

                orderService.deleteNote(order, note);
            }
        }
    }
}
