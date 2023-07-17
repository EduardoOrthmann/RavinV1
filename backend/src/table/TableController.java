package table;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import customer.CustomerService;
import exceptions.ErrorResponse;
import order.OrderService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TableController implements HttpHandler {
    private final String tablePath;
    private final TableService tableService;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final Gson gson;

    public TableController(String tablePath, TableService tableService, CustomerService customerService, OrderService orderService) {
        this.tablePath = tablePath;
        this.tableService = tableService;
        this.customerService = customerService;
        this.orderService = orderService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
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
                // GET /table
                if (path.matches(tablePath)) {
                    response = gson.toJson(tableService.findAll());
                    statusCode = 200;

                    // GET /table/{id}
                } else if (path.matches(tablePath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(tableService.findById(id));
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
                // POST /table
                if (path.matches(tablePath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    var table = gson.fromJson(requestBody, Table.class);
                    tableService.save(
                            new Table(
                                    table.getName(),
                                    table.getTableNumber(),
                                    table.getMaxCapacity(),
                                    table.getCreatedBy()
                            )
                    );

                    statusCode = 201;
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /table/{id}
                if (path.matches(tablePath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var table = tableService.findById(id);

                        var updatedTable = gson.fromJson(requestBody, Table.class);
                        updatedTable = new Table(
                                id,
                                updatedTable.getName(),
                                updatedTable.getTableNumber(),
                                updatedTable.getMaxCapacity(),
                                updatedTable.getStatus(),
                                updatedTable.getUpdatedBy()
                        );

                        table.setName(updatedTable.getName());
                        table.setTableNumber(updatedTable.getTableNumber());
                        table.setMaxCapacity(updatedTable.getMaxCapacity());
                        table.setStatus(updatedTable.getStatus());
                        table.setUpdatedAt(updatedTable.getUpdatedAt());
                        table.setUpdatedBy(updatedTable.getUpdatedBy());

                        tableService.update(table);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse("Table not found"));
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

            case "DELETE" -> {
                // DELETE /table/{id}
                if (path.matches(tablePath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var table = tableService.findById(id);

                        tableService.delete(table);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse("Table not found"));
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
                // PATCH /table/{id}/add-customer/{customerId}
                if (path.matches(tablePath + "/[0-9]+/add-customer/[0-9]+")) {
                    try {
                        int tableId = Integer.parseInt(queryParam.get()[2]);
                        int customerId = Integer.parseInt(queryParam.get()[4]);

                        var table = tableService.findById(tableId);
                        var customer = customerService.findById(customerId);

                        tableService.addCustomer(table, customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    }
                    // PATCH /table/{id}/remove-customer/{customerId}
                } else if (path.matches(tablePath + "/[0-9]+/remove-customer/[0-9]+")) {
                    try {
                        int tableId = Integer.parseInt(queryParam.get()[2]);
                        int customerId = Integer.parseInt(queryParam.get()[4]);

                        var table = tableService.findById(tableId);
                        var customer = customerService.findById(customerId);

                        tableService.removeCustomer(table, customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    }
                    // PATCH /table/{id}/add-order/{orderId}
                } else if (path.matches(tablePath + "/[0-9]+/add-order/[0-9]+")) {
                    try {
                        int tableId = Integer.parseInt(queryParam.get()[2]);
                        int orderId = Integer.parseInt(queryParam.get()[4]);

                        var table = tableService.findById(tableId);
                        var order = orderService.findById(orderId);

                        tableService.addOrder(table, order);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    }
                    // PATCH /table/{id}/remove-order/{orderId}
                } else if (path.matches(tablePath + "/[0-9]+/remove-order/[0-9]+")) {
                    try {
                        int tableId = Integer.parseInt(queryParam.get()[2]);
                        int orderId = Integer.parseInt(queryParam.get()[4]);

                        var table = tableService.findById(tableId);
                        var order = orderService.findById(orderId);

                        tableService.removeOrder(table, order);

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
