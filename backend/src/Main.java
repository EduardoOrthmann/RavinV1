import com.sun.net.httpserver.HttpServer;
import command.CommandDAO;
import command.CommandService;
import customer.CustomerController;
import customer.CustomerDAO;
import customer.CustomerService;
import employee.EmployeeController;
import employee.EmployeeDAO;
import employee.EmployeeService;
import menu.MenuDAO;
import menu.MenuService;
import objectBuilder.Builder;
import order.OrderDAO;
import order.OrderService;
import product.ProductController;
import product.ProductDAO;
import product.ProductService;
import table.TableDAO;
import table.TableService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        var employeeService = new EmployeeService(new EmployeeDAO());
        var productService = new ProductService(new ProductDAO());
        var menuService = new MenuService(new MenuDAO());
        var customerService = new  CustomerService(new CustomerDAO());
        var tableService = new TableService(new TableDAO());
        var orderService = new OrderService(new OrderDAO());
        var commandService = new CommandService(new CommandDAO());

        final String CUSTOMER_PATH = "/customer";
        final String EMPLOYEE_PATH = "/employee";
        final String PRODUCT_PATH = "/product";
        var customerController = new CustomerController(CUSTOMER_PATH, customerService);
        var employeeController = new EmployeeController(EMPLOYEE_PATH, employeeService);
        var productController = new ProductController(PRODUCT_PATH, productService);

        var builder = new Builder(employeeService, customerService, tableService, productService, menuService);
        builder.employeeBuilder();
        builder.customerBuilder();
        builder.tableBuilder();
        builder.productBuilder();
        builder.menuBuilder();

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext(CUSTOMER_PATH, customerController);
        server.createContext(EMPLOYEE_PATH, employeeController);
        server.createContext(PRODUCT_PATH, productController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running");
    }
}