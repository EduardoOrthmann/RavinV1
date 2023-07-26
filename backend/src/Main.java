import ReservedTable.ReservedTableController;
import ReservedTable.ReservedTableDAO;
import ReservedTable.ReservedTableService;
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
import payment.PaymentService;
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
        var paymentService = new PaymentService();
        var billService = new BillService(new BillDAO(), customerService, paymentService);
        var orderService = new OrderService(new OrderDAO(), billService);
        var tableService = new TableService(new TableDAO(), billService);
        var reservedTableService = new ReservedTableService(new ReservedTableDAO());

        var builder = new Builder(employeeService, customerService, tableService, productService, menuService);
        builder.employeeBuilder();
        builder.customerBuilder();
        builder.tableBuilder();
        builder.productBuilder();
        builder.menuBuilder();

        final String USER_PATH = "/user";
        final String CUSTOMER_PATH = "/customer";
        final String EMPLOYEE_PATH = "/employee";
        final String PRODUCT_PATH = "/product";
        final String MENU_PATH = "/menu";
        final String TABLE_PATH = "/table";
        final String RESERVED_TABLE_PATH = "/reserved-table";
        final String ORDER_PATH = "/order";
        final String BILL_PATH = "/bill";
        var userController = new UserController(USER_PATH, userService);
        var customerController = new CustomerController(CUSTOMER_PATH, customerService, userService);
        var employeeController = new EmployeeController(EMPLOYEE_PATH, employeeService, userService);
        var productController = new ProductController(PRODUCT_PATH, productService, userService);
        var menuController = new MenuController(MENU_PATH, menuService, productService, userService);
        var tableController = new TableController(TABLE_PATH, tableService, reservedTableService, customerService, userService);
        var reservedTableController = new ReservedTableController(RESERVED_TABLE_PATH, reservedTableService, customerService, userService);
        var orderController = new OrderController(ORDER_PATH, orderService, userService, employeeService, customerService, billService);
        var billController = new BillController(BILL_PATH, billService, orderService, tableService, reservedTableService, productService, customerService, userService);

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext(CUSTOMER_PATH, customerController);
        server.createContext(EMPLOYEE_PATH, employeeController);
        server.createContext(PRODUCT_PATH, productController);
        server.createContext(MENU_PATH, menuController);
        server.createContext(TABLE_PATH, tableController);
        server.createContext(RESERVED_TABLE_PATH, reservedTableController);
        server.createContext(ORDER_PATH, orderController);
        server.createContext(BILL_PATH, billController);
        server.createContext(USER_PATH, userController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running");
    }
}