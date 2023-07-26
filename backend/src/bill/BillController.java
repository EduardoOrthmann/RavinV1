package bill;

import ReservedTable.ReservedTableService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import customer.CustomerService;
import enums.PaymentMethodFactory;
import enums.Role;
import exceptions.UnauthorizedRequestException;
import order.Order;
import order.OrderService;
import payment.PaymentDTO;
import product.ProductService;
import table.TableService;
import user.UserService;
import utils.APIUtils;
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

public class BillController implements HttpHandler {
    private final String billPath;
    private final BillService billService;
    private final OrderService orderService;
    private final TableService tableService;
    private final ReservedTableService reservedTableService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public BillController(String billPath, BillService billService, OrderService orderService, TableService tableService, ReservedTableService reservedTableService, ProductService productService, CustomerService customerService, UserService userService) {
        this.billPath = billPath;
        this.billService = billService;
        this.orderService = orderService;
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
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // GET /bill (all bills)
        if (path.matches(billPath)) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));

                response = gson.toJson(billService.findAll());
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
        // GET /bill/{id} (bill by id)
        else if (path.matches(billPath + "/[0-9]+")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var bill = billService.findById(id);
                var assignedCustomer = customerService.findById(bill.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(bill);
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
        // GET /bill/table/{id} (bill by table id and isPaid)
        else if (path.matches(billPath + "/table/[0-9]+")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));
                var queryParams = exchange.getRequestURI().getQuery();
                int tableId = Integer.parseInt(splittedPath.get()[3]);
                var isPaid = Boolean.parseBoolean(APIUtils.getQueryParamValue(queryParams, "isPaid"));

                if (!tableService.existsById(tableId)) {
                    throw new NoSuchElementException("Mesa não encontrada");
                }

                response = gson.toJson(billService.findByTableAndIsPaid(tableId, isPaid));
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
            response = gson.toJson(new CustomResponse("Invalid endpoint"));
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

        // POST /bill
        if (path.matches(billPath)) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                var bill = gson.fromJson(requestBody, Bill.class);

                var customer = customerService.findByUserId(user.getId());
                var reservedTable = reservedTableService.findByCustomerAndDatetime(customer.getId(), LocalDateTime.now());

                var createdBy = user.getId();
                var table = reservedTable.getTable();
                var orders = bill.getOrders().stream()
                        .map(order -> {
                            var product = productService.findById(order.getProduct().getId());
                            return orderService.save(
                                    new Order(
                                            product,
                                            order.getQuantity(),
                                            order.getNotes() == null ? new ArrayList<>() : order.getNotes(),
                                            createdBy
                                    )
                            );
                        })
                        .collect(Collectors.toSet());

                var createdBill = billService.save(
                        new Bill(
                                customer.getId(),
                                table,
                                orders,
                                createdBy
                        )
                );

                response = gson.toJson(createdBill);
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
        // POST /bill/{id}/close-bill
        else if (path.matches(billPath + "/[0-9]+/close-bill")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(path.split("/")[2]);

                var bill = billService.findById(id);
                var assignedCustomer = customerService.findById(bill.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                var payment = gson.fromJson(requestBody, PaymentDTO.class);
                var paymentMethod = PaymentMethodFactory.valueOf(payment.paymentMethodFactory().toString()).createPaymentMethod();

                billService.closeBill(bill, payment.amount(), paymentMethod);

                response = gson.toJson(new CustomResponse("Comanda paga com sucesso"));
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
            response = gson.toJson(new CustomResponse("Invalid endpoint"));
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

        // PATCH /bill/{id}/add-order
        if (path.matches(billPath + "/[0-9]+/add-order")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                int id = Integer.parseInt(splittedPath.get()[2]);

                var bill = billService.findById(id);
                var assignedCustomer = customerService.findById(bill.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                var order = gson.fromJson(requestBody, Order.class);
                var product = productService.findById(order.getProduct().getId());
                var createdBy = user.getId();
                var createdOrder = orderService.save(
                        new Order(
                                product,
                                order.getQuantity(),
                                order.getNotes() == null ? new ArrayList<>() : order.getNotes(),
                                createdBy
                        )
                );

                billService.addOrder(bill, createdOrder);
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
            response = gson.toJson(new CustomResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
