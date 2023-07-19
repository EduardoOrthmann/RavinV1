import bill.BillController;
import com.sun.net.httpserver.HttpServer;
import bill.BillDAO;
import bill.BillService;
import customer.CustomerController;
import customer.CustomerDAO;
import customer.CustomerService;
import employee.EmployeeController;
import employee.EmployeeDAO;
import employee.EmployeeService;
import menu.MenuController;
import menu.MenuDAO;
import menu.MenuService;
import objectBuilder.Builder;
import order.OrderController;
import order.OrderDAO;
import order.OrderService;
import product.ProductController;
import product.ProductDAO;
import product.ProductService;
import table.TableController;
import table.TableDAO;
import table.TableService;
import user.UserController;
import user.UserRepository;
import user.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        var userService = new UserService(new UserRepository());
        var employeeService = new EmployeeService(new EmployeeDAO(), userService);
        var productService = new ProductService(new ProductDAO());
        var menuService = new MenuService(new MenuDAO());
        var customerService = new  CustomerService(new CustomerDAO(), userService);
        var tableService = new TableService(new TableDAO());
        var orderService = new OrderService(new OrderDAO());
        var commandService = new BillService(new BillDAO());

        var builder = new Builder(employeeService, customerService, tableService, productService, menuService);
        builder.employeeBuilder();
        builder.customerBuilder();
        builder.tableBuilder();
        builder.productBuilder();
        builder.menuBuilder();

        final String CUSTOMER_PATH = "/customer";
        final String EMPLOYEE_PATH = "/employee";
        final String PRODUCT_PATH = "/product";
        final String MENU_PATH = "/menu";
        final String TABLE_PATH = "/table";
        final String ORDER_PATH = "/order";
        final String BILL_PATH = "/bill";
        final String USER_PATH = "/user";
        var customerController = new CustomerController(CUSTOMER_PATH, customerService, userService);
        var employeeController = new EmployeeController(EMPLOYEE_PATH, employeeService);
        var productController = new ProductController(PRODUCT_PATH, productService);
        var menuController = new MenuController(MENU_PATH, menuService, productService);
        var tableController = new TableController(TABLE_PATH, tableService, customerService, orderService);
        var orderController = new OrderController(ORDER_PATH, orderService, productService, employeeService);
        var billController = new BillController(BILL_PATH, commandService, orderService);
        var userController = new UserController(USER_PATH, userService);

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext(CUSTOMER_PATH, customerController);
        server.createContext(EMPLOYEE_PATH, employeeController);
        server.createContext(PRODUCT_PATH, productController);
        server.createContext(MENU_PATH, menuController);
        server.createContext(TABLE_PATH, tableController);
        server.createContext(ORDER_PATH, orderController);
        server.createContext(BILL_PATH, billController);
        server.createContext(USER_PATH, userController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running");
    }
}