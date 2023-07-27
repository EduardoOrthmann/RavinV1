package domains.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import domains.bill.BillService;
import domains.customer.CustomerService;
import domains.employee.EmployeeService;
import domains.user.UserService;
import enums.OrderStatus;
import enums.Role;
import exceptions.UnauthorizedRequestException;
import utils.APIUtils;
import utils.Constants;
import utils.CustomResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class OrderController implements HttpHandler {
    private final String orderPath;
    private final OrderService orderService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final BillService billService;
    private final Gson gson;

    public OrderController(String orderPath, OrderService orderService, UserService userService, EmployeeService employeeService, CustomerService customerService, BillService billService) {
        this.orderPath = orderPath;
        this.orderService = orderService;
        this.userService = userService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.billService = billService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "PATCH" -> patchHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_METHOD)));
        }
    }

    private void patchHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // PATCH /order/{id}/update-status/{status} (update order status)
        if (path.matches(orderPath + "/[0-9]+/update-status/[a-zA-Z]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                var orderId = Integer.parseInt(splittedPath.get()[2]);
                var orderStatus = OrderStatus.valueOf(splittedPath.get()[4]);

                var user = userService.findByToken(headerToken);
                var order = orderService.findById(orderId);

                var assignedEmployeeUserId = employeeService.findById(order.getEmployeeId()).getUser().getId();
                if (assignedEmployeeUserId != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderService.updateStatus(order, orderStatus, user.getId());

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // PATCH /order/{id}/take-order (take order)
        else if (path.matches(orderPath + "/[0-9]+/take-order")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.EMPLOYEE, Role.MANAGER, Role.ADMIN));

                var orderId = Integer.parseInt(splittedPath.get()[2]);
                var order = orderService.findById(orderId);
                var employee = employeeService.findByUserId(user.getId());

                orderService.takeOrder(order, employee);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // // PATCH /order/{id}/cancel-order (cancel order)
        else if (path.matches(orderPath + "/[0-9]+/cancel-order")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                var id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var order = orderService.findById(id);
                var associatedBill = billService.findByOrderId(order.getId());
                var associatedCustomer = customerService.findById(associatedBill.getCustomerId());

                if (associatedCustomer.getUser().getId() != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderService.cancelOrder(order, user.getId());

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
