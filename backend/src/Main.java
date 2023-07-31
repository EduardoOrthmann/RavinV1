import com.sun.net.httpserver.HttpServer;
import database.PostgresConnector;
import domains.ReservedTable.ReservedTableController;
import domains.ReservedTable.ReservedTableRepository;
import domains.ReservedTable.ReservedTableService;
import domains.order.OrderController;
import domains.order.OrderRepository;
import domains.order.OrderService;
import domains.customer.CustomerController;
import domains.customer.CustomerRepository;
import domains.customer.CustomerService;
import domains.employee.EmployeeController;
import domains.employee.EmployeeRepository;
import domains.employee.EmployeeService;
import domains.menu.MenuController;
import domains.menu.MenuRepository;
import domains.menu.MenuService;
import domains.orderItem.OrderItemController;
import domains.orderItem.OrderItemRepository;
import domains.orderItem.OrderItemService;
import domains.orderItemComment.OrderItemCommentRepository;
import domains.payment.PaymentService;
import domains.product.ProductController;
import domains.product.ProductRepository;
import domains.product.ProductService;
import domains.reservedTableCustomer.ReservedTableCustomerRepository;
import domains.table.TableController;
import domains.table.TableRepository;
import domains.table.TableService;
import domains.user.UserController;
import domains.user.UserRepository;
import domains.user.UserService;
import utils.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        var databaseConnector = new PostgresConnector();

        var userService = new UserService(new UserRepository(databaseConnector));
        var customerService = new CustomerService(new CustomerRepository(databaseConnector, userService));
        var employeeService = new EmployeeService(new EmployeeRepository(databaseConnector, userService));
        var productService = new ProductService(new ProductRepository(databaseConnector));
        var menuService = new MenuService(new MenuRepository(databaseConnector, productService), productService);
        var paymentService = new PaymentService();
        var orderItemCommentRepository = new OrderItemCommentRepository(databaseConnector);
        var orderItemRepository = new OrderItemRepository(databaseConnector, productService, orderItemCommentRepository);
        var orderService = new OrderService(new OrderRepository(databaseConnector, orderItemRepository), orderItemRepository, customerService, paymentService);
        var reservedTableCustomerRepository = new ReservedTableCustomerRepository(databaseConnector, customerService);
        var orderItemService = new OrderItemService(orderItemRepository, orderService, orderItemCommentRepository);
        var tableService = new TableService(new TableRepository(databaseConnector, customerService), orderService, customerService);
        var reservedTableService = new ReservedTableService(new ReservedTableRepository(databaseConnector, tableService, reservedTableCustomerRepository), reservedTableCustomerRepository);

        var userController = new UserController(Constants.USER_PATH, userService);
        var customerController = new CustomerController(Constants.CUSTOMER_PATH, customerService, userService);
        var employeeController = new EmployeeController(Constants.EMPLOYEE_PATH, employeeService, userService);
        var productController = new ProductController(Constants.PRODUCT_PATH, productService, userService);
        var menuController = new MenuController(Constants.MENU_PATH, menuService, productService, userService);
        var tableController = new TableController(Constants.TABLE_PATH, tableService, reservedTableService, customerService, userService);
        var reservedTableController = new ReservedTableController(Constants.RESERVED_TABLE_PATH, reservedTableService, customerService, userService);
        var orderItemController = new OrderItemController(Constants.ORDER_ITEM_PATH, orderItemService, userService, employeeService, customerService, orderService);
        var orderController = new OrderController(Constants.ORDER_PATH, orderService, tableService, reservedTableService, productService, customerService, userService);

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.create(address, 0);

        server.createContext(Constants.CUSTOMER_PATH, customerController);
        server.createContext(Constants.EMPLOYEE_PATH, employeeController);
        server.createContext(Constants.PRODUCT_PATH, productController);
        server.createContext(Constants.MENU_PATH, menuController);
        server.createContext(Constants.TABLE_PATH, tableController);
        server.createContext(Constants.RESERVED_TABLE_PATH, reservedTableController);
        server.createContext(Constants.ORDER_ITEM_PATH, orderItemController);
        server.createContext(Constants.ORDER_PATH, orderController);
        server.createContext(Constants.USER_PATH, userController);

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running");
    }
}