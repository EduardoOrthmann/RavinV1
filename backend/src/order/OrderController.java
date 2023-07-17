package order;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import employee.EmployeeService;
import enums.OrderStatus;
import exceptions.ErrorResponse;
import product.ProductService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class OrderController implements HttpHandler {
    private final String orderPath;
    private final OrderService orderService;
    private final ProductService productService;
    private final EmployeeService employeeService;
    private final Gson gson;

    public OrderController(String orderPath, OrderService orderService, ProductService productService, EmployeeService employeeService) {
        this.orderPath = orderPath;
        this.orderService = orderService;
        this.productService = productService;
        this.employeeService = employeeService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        final Optional<String[]> queryParam = Optional.of(path.split("/"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "GET" -> {
                // GET /order
                if (path.matches(orderPath)) {
                    response = gson.toJson(orderService.findAll());
                    statusCode = 200;

                    // GET /order/{id}
                } else if (path.matches(orderPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(orderService.findById(id));
                        statusCode = 200;

                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "POST" -> {
                // POST /order
                if (path.matches(orderPath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    var order = gson.fromJson(requestBody, Order.class);

                    var product = productService.findById(order.getProduct().getId());

                    orderService.save(
                            new Order(
                                    product,
                                    order.getQuantity(),
                                    order.getCustomerId(),
                                    order.getNotes() == null ? new ArrayList<>() : order.getNotes()
                            )
                    );

                    statusCode = 201;
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /order/{id}
                if (path.matches(orderPath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var order = orderService.findById(id);

                        var updatedOrder = gson.fromJson(requestBody, Order.class);
                        updatedOrder = new Order(
                                id,
                                updatedOrder.getProduct(),
                                updatedOrder.getQuantity(),
                                updatedOrder.getCustomerId(),
                                updatedOrder.getNotes()
                        );

                        order.setProduct(updatedOrder.getProduct());
                        order.setQuantity(updatedOrder.getQuantity());
                        order.setCustomerId(updatedOrder.getCustomerId());
                        order.setNotes(updatedOrder.getNotes());
                        order.setUpdatedBy(updatedOrder.getUpdatedBy());

                        orderService.update(order);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "DELETE" -> {
                // DELETE /order/{id}
                if (path.matches(orderPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var order = orderService.findById(id);

                        orderService.delete(order);
                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PATCH" -> {
                // PATCH /order/{id}/update-status/{status}/employee/{employeeId}
                if (path.matches(orderPath + "/[0-9]+/update-status/[a-zA-Z]+/employee/[0-9]+")) {
                    try {
                        int orderId = Integer.parseInt(queryParam.get()[2]);
                        String status = queryParam.get()[4];
                        int employeeId = Integer.parseInt(queryParam.get()[6]);

                        var order = orderService.findById(orderId);
                        var employee = employeeService.findById(employeeId);

                        orderService.updateStatus(order, OrderStatus.valueOf(status), employee.getId());

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            default -> {
                response = gson.toJson(new ErrorResponse("Invalid request method"));
                statusCode = 405;
            }
        }

        if (response.getBytes().length > 0) {
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
        } else {
            exchange.sendResponseHeaders(statusCode, -1);
        }

        exchange.close();
    }
}
