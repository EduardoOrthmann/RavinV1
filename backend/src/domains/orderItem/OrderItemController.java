package domains.orderItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import domains.order.OrderService;
import domains.customer.CustomerService;
import domains.employee.EmployeeService;
import domains.user.UserService;
import enums.OrderItemStatus;
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

public class OrderItemController implements HttpHandler {
    private final String orderItemPath;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final Gson gson;

    public OrderItemController(String orderItemPath, OrderItemService orderItemService, UserService userService, EmployeeService employeeService, CustomerService customerService, OrderService orderService) {
        this.orderItemPath = orderItemPath;
        this.orderItemService = orderItemService;
        this.userService = userService;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.orderService = orderService;
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

        // PATCH /order-item/{id}/update-status/{status} (update order item status)
        if (path.matches(orderItemPath + "/[0-9]+/update-status/[a-zA-Z]+")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.EMPLOYEE, Role.MANAGER, Role.ADMIN));
                var orderId = Integer.parseInt(splittedPath.get()[2]);
                var orderStatus = OrderItemStatus.valueOf(splittedPath.get()[4]);

                var orderItem = orderItemService.findById(orderId);

                var assignedEmployeeUserId = employeeService.findById(orderItem.getEmployeeId()).getUser().getId();
                if (assignedEmployeeUserId != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderItemService.updateStatus(orderItem, orderStatus, user.getId());

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
        // PATCH /order-item/{id}/take-order (take order item)
        else if (path.matches(orderItemPath + "/[0-9]+/take-order")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.EMPLOYEE, Role.MANAGER, Role.ADMIN));
                var orderId = Integer.parseInt(splittedPath.get()[2]);

                var orderItem = orderItemService.findById(orderId);
                var employee = employeeService.findByUserId(user.getId());

                orderItemService.takeOrderItem(orderItem, employee);

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
        // // PATCH /order-item/{id}/cancel-order (cancel order item)
        else if (path.matches(orderItemPath + "/[0-9]+/cancel-order")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                var id = Integer.parseInt(splittedPath.get()[2]);

                var orderItem = orderItemService.findById(id);
                var associatedOrder = orderService.findById(orderItem.getOrderId());
                var associatedCustomer = customerService.findById(associatedOrder.getCustomerId());

                if (associatedCustomer.getUser().getId() != user.getId()) {
                    throw new UnauthorizedRequestException();
                }

                orderItemService.cancelOrder(orderItem, user.getId());

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
