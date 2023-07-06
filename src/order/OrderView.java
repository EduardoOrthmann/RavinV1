package order;

import employee.Employee;
import employee.EmployeeService;
import enums.ProductStatus;
import product.Product;
import product.ProductService;

import javax.swing.*;

public class OrderView {
    private final OrderService orderService;
    private final ProductService productService;
    private final EmployeeService employeeService;

    public OrderView(OrderService orderService, ProductService productService, EmployeeService employeeService) {
        this.orderService = orderService;
        this.productService = productService;
        this.employeeService = employeeService;
    }

    public void view() {
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
                    JOptionPane.showMessageDialog(null, "Por favor crie um funcionário e um produto antes de criar um pedido");
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
                var createdByEmployee = employeeService.findById((int) createdBy).getRole();

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

            case "update" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (orders.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um pedido antes de atualizar um pedido");
                    break;
                }

                var id = (int) JOptionPane.showInputDialog(
                        null,
                        "Qual pedido você deseja alterar?",
                        "Alterar",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        orderIds,
                        null
                );

                var order = orderService.findById(id);

                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Selecione um produto:",
                        "Produto",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        order.getProduct()
                );

                var product = productService.findById((int) selectedProduct);

                var selectedEmployee = JOptionPane.showInputDialog(
                        null,
                        "Quem atendeu este pedido:",
                        "Funcionário",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        order.getEmployee()
                );

                var employee = employeeService.findById((int) selectedEmployee);

                var quantity = Integer.parseInt(JOptionPane.showInputDialog("Insira a quantidade:", order.getQuantity()));

                var status = (ProductStatus) JOptionPane.showInputDialog(
                        null,
                        "Qual é o status desse pedido?",
                        "Status",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ProductStatus.values(),
                        order.getStatus()
                );

                var selectedUpdatedBy = JOptionPane.showInputDialog(
                        null,
                        "Quem está alterando esse pedido?",
                        "Alterado por",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        employeeIds,
                        null
                );

                var updatedBy = employeeService.findById((int) selectedUpdatedBy).getRole();

                order.setId(id);
                order.setProduct(product);
                order.setEmployee(employee);
                order.setQuantity(quantity);
                order.setStatus(status);
                order.setUpdatedBy(updatedBy);

                orderService.update(order);
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                orderService.delete(orderService.findById(id));
            }

            case "addNote" -> {
                var orders = orderService.findAll();
                var orderIds = orders.stream().map(Order::getId).toArray();

                if (orders.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor crie um pedido antes de adicionar uma observação a um pedido");
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
                    JOptionPane.showMessageDialog(null, "Por favor crie um pedido antes de atualizar uma observação a um pedido");
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
                    JOptionPane.showMessageDialog(null, "Por favor crie um pedido antes de remover uma observação de um pedido");
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
