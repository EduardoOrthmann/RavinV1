import command.CommandDAO;
import command.CommandService;
import command.CommandView;
import customer.CustomerDAO;
import customer.CustomerService;
import customer.CustomerView;
import employee.EmployeeDAO;
import employee.EmployeeService;
import employee.EmployeeView;
import menu.MenuDAO;
import menu.MenuService;
import menu.MenuView;
import order.OrderDAO;
import order.OrderService;
import order.OrderView;
import product.ProductDAO;
import product.ProductService;
import product.ProductView;
import table.TableDAO;
import table.TableService;
import table.TableView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        var employeeService = new EmployeeService(new EmployeeDAO());
        var productService = new ProductService(new ProductDAO());
        var menuService = new MenuService(new MenuDAO());
        var customerService = new  CustomerService(new CustomerDAO());
        var tableService = new TableService(new TableDAO());
        var orderService = new OrderService(new OrderDAO());
        var commandService = new CommandService(new CommandDAO());

        var employeeView = new EmployeeView(employeeService);
        var productView = new ProductView(productService, employeeService);
        var menuView = new MenuView(menuService, productService, employeeService);
        var customerView = new CustomerView(customerService, employeeService);
        var tableView = new TableView(tableService, customerService, employeeService);
        var orderView = new OrderView(orderService, productService, employeeService);
        var commandView = new CommandView(commandService, tableService, customerService, orderService, employeeService);

        do {
            String[] entityOptions = {"Cliente", "Funcionário", "Produto", "Menu", "Mesa", "Pedido", "Comanda"};

            String selectedEntityOption = (String) JOptionPane.showInputDialog(
                    null,
                    "Insira uma classe",
                    "Menu do Restaurante",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    entityOptions,
                    null
            );

            switch (selectedEntityOption) {
                case "Cliente" -> customerView.view();
                case "Funcionário" -> employeeView.view();
                case "Produto" -> productView.view();
                case "Menu" -> menuView.view();
                case "Mesa" -> tableView.view();
                case "Pedido" -> orderView.view();
                case "Comanda" -> commandView.view();
            }
        } while (JOptionPane.showConfirmDialog(null, "Deseja continuar?") == JOptionPane.YES_OPTION);
    }
}