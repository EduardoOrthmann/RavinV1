package bill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import customer.CustomerService;
import enums.Role;
import interfaces.Payment;
import payment.CashPayment;
import payment.CreditCardPayment;
import payment.DebitCardPayment;
import payment.PaymentDTO;
import utils.CustomResponse;
import exceptions.UnauthorizedRequestException;
import order.Order;
import order.OrderService;
import product.ProductService;
import user.UserService;
import utils.APIUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class BillController implements HttpHandler {
    private final String billPath;
    private final BillService billService;
    private final OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public BillController(String billPath, BillService billService, OrderService orderService, ProductService productService, CustomerService customerService, UserService userService) {
        this.billPath = billPath;
        this.billService = billService;
        this.orderService = orderService;
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

        // GET /bill
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
        // GET /bill/{id}
        else if (path.matches(billPath + "/[0-9]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
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
        } else {
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
                var createdBy = user.getId();
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
                                bill.getCustomerId(),
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
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(path.split("/")[2]);

                var user = userService.findByToken(headerToken);
                var bill = billService.findById(id);
                var assignedCustomer = customerService.findById(bill.getCustomerId());

                if (user.getRole() == Role.CUSTOMER && user.getId() != assignedCustomer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                var payment = gson.fromJson(requestBody, PaymentDTO.class);
                Payment paymentMethod;

                switch (payment.paymentMethod()) {
                    case CASH -> paymentMethod = new CashPayment();
                    case CREDIT_CARD -> paymentMethod = new CreditCardPayment();
                    case DEBIT_CARD -> paymentMethod = new DebitCardPayment();
                    default -> throw new IllegalArgumentException("Método de pagamento inválido");
                }

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
        } else {
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
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
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
        } else {
            response = gson.toJson(new CustomResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
