import com.sun.net.httpserver.HttpServer;
import domains.ReservedTable.ReservedTableController;
import domains.ReservedTable.ReservedTableDAO;
import domains.ReservedTable.ReservedTableService;
import domains.bill.BillController;
import domains.bill.BillDAO;
import domains.bill.BillService;
import domains.customer.CustomerController;
import domains.customer.CustomerDAO;
import domains.customer.CustomerService;
import domains.employee.EmployeeController;
import domains.employee.EmployeeDAO;
import domains.employee.EmployeeService;
import domains.menu.MenuController;
import domains.menu.MenuDAO;
import domains.menu.MenuService;
import domains.order.OrderController;
import domains.order.OrderDAO;
import domains.order.OrderService;
import domains.payment.PaymentService;
import domains.product.ProductController;
import domains.product.ProductDAO;
import domains.product.ProductService;
import domains.table.TableController;
import domains.table.TableDAO;
import domains.table.TableService;
import domains.user.UserController;
import domains.user.UserRepository;
import domains.user.UserService;
import utils.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        var userService = new UserService(new UserRepository());
        var employeeService = new EmployeeService(new EmployeeDAO(), userService);
        var productService = new ProductService(new ProductDAO());
        var menuService = new MenuService(new MenuDAO());
        var customerService = new CustomerService(new CustomerDAO(), userService);
        var paymentService = new PaymentService();
        var billService = new BillService(new BillDAO(), customerService, paymentService);
        var orderService = new OrderService(new OrderDAO(), billService);
        var tableService = new TableService(new TableDAO(), billService);
        var reservedTableService = new ReservedTableService(new ReservedTableDAO());

        var userController = new UserController(Constants.USER_PATH, userService);
        var customerController = new CustomerController(Constants.CUSTOMER_PATH, customerService, userService);
        var employeeController = new EmployeeController(Constants.EMPLOYEE_PATH, employeeService, userService);
        var productController = new ProductController(Constants.PRODUCT_PATH, productService, userService);
        var menuController = new MenuController(Constants.MENU_PATH, menuService, productService, userService);
        var tableController = new TableController(Constants.TABLE_PATH, tableService, reservedTableService, customerService, userService);
        var reservedTableController = new ReservedTableController(Constants.RESERVED_TABLE_PATH, reservedTableService, customerService, userService);
        var orderController = new OrderController(Constants.ORDER_PATH, orderService, userService, employeeService, customerService, billService);
        var billController = new BillController(Constants.BILL_PATH, billService, orderService, tableService, reservedTableService, productService, customerService, userService);

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext(Constants.CUSTOMER_PATH, customerController);
        server.createContext(Constants.EMPLOYEE_PATH, employeeController);
        server.createContext(Constants.PRODUCT_PATH, productController);
        server.createContext(Constants.MENU_PATH, menuController);
        server.createContext(Constants.TABLE_PATH, tableController);
        server.createContext(Constants.RESERVED_TABLE_PATH, reservedTableController);
        server.createContext(Constants.ORDER_PATH, orderController);
        server.createContext(Constants.BILL_PATH, billController);
        server.createContext(Constants.USER_PATH, userController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running");
    }
}