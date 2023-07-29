package domains.order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import domains.ReservedTable.ReservedTableService;
import domains.customer.CustomerService;
import domains.orderItem.OrderItem;
import domains.orderItem.OrderItemService;
import domains.payment.PaymentDTO;
import domains.product.ProductService;
import domains.table.TableService;
import domains.user.UserService;
import enums.PaymentMethodFactory;
import enums.Role;
import exceptions.UnauthorizedRequestException;
import utils.APIUtils;
import utils.Constants;
import utils.CustomResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderController implements HttpHandler {
    private final String orderPath;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final TableService tableService;
    private final ReservedTableService reservedTableService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public OrderController(String orderPath, OrderService orderService, OrderItemService orderItemService, TableService tableService, ReservedTableService reservedTableService, ProductService productService, CustomerService customerService, UserService userService) {
        this.orderPath = orderPath;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.tableService = tableService;
        this.reservedTableService = reservedTableService;
        this.productService = productService;
        this.customerService = customerService;
        this.userService = userService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            case "PATCH" -> patchHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_METHOD)));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // GET /order (all orders)
        if (path.matches(orderPath)) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));

                response = gson.toJson(orderService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /order/{id} (order by id)
        else if (path.matches(orderPath + "/[0-9]+")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var order = orderService.findById(id);
                var assignedCustomer = customerService.findById(order.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(order);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /order/table/{id}?isPaid={isPaid} (order by table id and isPaid)
        else if (path.matches(orderPath + "/table/[0-9]+")) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));
                var queryParams = exchange.getRequestURI().getQuery();
                int tableId = Integer.parseInt(splittedPath.get()[3]);
                var isPaid = Boolean.parseBoolean(APIUtils.getQueryParamValue(queryParams, "isPaid"));

                if (!tableService.existsById(tableId)) {
                    throw new NoSuchElementException(Constants.TABLE_NOT_FOUND);
                }

                response = gson.toJson(orderService.findByTableAndIsPaid(tableId, isPaid));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
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

    private void postHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes());

        // POST /order (create order)
        if (path.matches(orderPath)) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                var order = gson.fromJson(requestBody, Order.class);

                var customer = customerService.findByUserId(user.getId());
                var reservedTable = reservedTableService.findByCustomerAndDatetime(customer.getId(), LocalDateTime.now());

                var createdBy = user.getId();
                var table = reservedTable.getTable();
                var orderItems = order.getOrderItems().stream()
                        .map(orderItem -> {
                            var product = productService.findById(orderItem.getProduct().getId());
                            return orderItemService.save(
                                    new OrderItem(
                                            product,
                                            orderItem.getQuantity(),
                                            orderItem.getNotes() == null ? new ArrayList<>() : orderItem.getNotes(),
                                            createdBy
                                    )
                            );
                        })
                        .collect(Collectors.toSet());

                var createdOrder = orderService.save(
                        new Order(
                                customer.getId(),
                                table,
                                orderItems,
                                createdBy
                        )
                );

                response = gson.toJson(createdOrder);
                statusCode = HttpURLConnection.HTTP_CREATED;
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
        // POST /order/{id}/close-order (close order)
        else if (path.matches(orderPath + "/[0-9]+/close-order")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(path.split("/")[2]);

                var order = orderService.findById(id);
                var assignedCustomer = customerService.findById(order.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                var payment = gson.fromJson(requestBody, PaymentDTO.class);
                var paymentMethod = PaymentMethodFactory.valueOf(payment.paymentMethodFactory().toString()).createPaymentMethod();

                orderService.closeOrder(order, payment.amount(), paymentMethod);

                response = gson.toJson(new CustomResponse(Constants.SUCCESS_MESSAGE));
                statusCode = HttpURLConnection.HTTP_OK;
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

    private void patchHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // PATCH /order/{id}/add-order-item (add order item to order)
        if (path.matches(orderPath + "/[0-9]+/add-order-item")) {
            try {
                var user = userService.authorizeUserByRole(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                int id = Integer.parseInt(splittedPath.get()[2]);

                var order = orderService.findById(id);
                var assignedCustomer = customerService.findById(order.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                var orderItem = gson.fromJson(requestBody, OrderItem.class);
                var product = productService.findById(orderItem.getProduct().getId());
                var createdBy = user.getId();
                var createdOrderItem = orderItemService.save(
                        new OrderItem(
                                product,
                                orderItem.getQuantity(),
                                orderItem.getNotes() == null ? new ArrayList<>() : orderItem.getNotes(),
                                createdBy
                        )
                );

                orderService.addOrderItem(order, createdOrderItem);
                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse("Invalid id"));
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
