package menu;

import employee.Employee;
import employee.EmployeeService;
import product.Product;
import product.ProductService;

import javax.swing.*;

public class MenuView {
    public static void view() {
        var menuService = new MenuService();
        var productService = new ProductService();
        var employeeService = new EmployeeService();

        String[] menuOptions = {"findById", "findAll", "save", "update", "delete", "addProduct", "deleteProduct"};

        String selectedMenuOption = (String) JOptionPane.showInputDialog(
                null,
                "Insira um método",
                "Métodos do Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                menuOptions,
                null
        );

        switch (selectedMenuOption) {
            case "findById" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                System.out.println(menuService.findById(id));
            }

            case "findAll" -> System.out.println(menuService.findAll());

            case "save" -> {
                var employees = employeeService.findAll();
                var employeeIds = employees.stream().map(Employee::getId).toArray();

                if (employees.isEmpty()) {
                    System.out.println("Por favor crie um funcionário antes de criar um cliente");
                    break;
                }

                var name = JOptionPane.showInputDialog("Insira o nome do cardápio:");
                var menuCode = JOptionPane.showInputDialog("Insira o código do cardápio:");
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

                menuService.save(
                        new Menu(
                                name,
                                menuCode,
                                createdByEmployee
                        )
                );
            }

            case "delete" -> {
                var id = Integer.parseInt(JOptionPane.showInputDialog("Insira um id:"));
                menuService.delete(menuService.findById(id));
            }

            case "addProduct" -> {
                var menus = menuService.findAll();
                var menuIds = menus.stream().map(Menu::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (menus.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um cardápio e um produto antes de adicionar um produto a um cardápio");
                    break;
                }

                var selectedMenu = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cardápio:",
                        "Cardápios",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        menuIds,
                        null
                );
                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Escolha um produto",
                        "Produtos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );

                var menu = menuService.findById((int) selectedMenu);
                var product = productService.findById((int) selectedProduct);

                menuService.addProduct(menu, product);
            }

            case "deleteProduct" -> {
                var menus = menuService.findAll();
                var menuIds = menus.stream().map(Menu::getId).toArray();
                var products = productService.findAll();
                var productIds = products.stream().map(Product::getId).toArray();

                if (menus.isEmpty() || products.isEmpty()) {
                    System.out.println("Por favor crie um cardápio e um produto antes de adicionar um produto a um cardápio");
                    break;
                }

                var selectedMenu = JOptionPane.showInputDialog(
                        null,
                        "Selecione um cardápio:",
                        "Cardápios",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        menuIds,
                        null
                );
                var selectedProduct = JOptionPane.showInputDialog(
                        null,
                        "Escolha um produto",
                        "Produtos",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        productIds,
                        null
                );

                var menu = menuService.findById((int) selectedMenu);
                var product = productService.findById((int) selectedProduct);

                menuService.deleteProduct(menu, product);
            }
        }
    }
}
