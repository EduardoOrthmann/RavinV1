package bill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import exceptions.ErrorResponse;
import order.OrderService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class BillController implements HttpHandler {
    private final String billPath;
    private final BillService billService;
    private final OrderService orderService;
    private final Gson gson;

    public BillController(String billPath, BillService billService, OrderService orderService) {
        this.billPath = billPath;
        this.billService = billService;
        this.orderService = orderService;
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
                // GET /bill
                if (path.matches(billPath)) {
                    try {
                        response = gson.toJson(billService.findAll());
                        statusCode = 200;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /bill/{id}
                } else if (path.matches(billPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(billService.findById(id));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "POST" -> {
                // POST /bill
                if (path.matches(billPath)) {
                    try {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes());

                        var bill = gson.fromJson(requestBody, Bill.class);
                        billService.save(
                                new Bill(
                                        bill.getCreatedBy()
                                )
                        );

                        statusCode = 201;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PATCH" -> {
                // PATCH /bill/{id}/add-order/{order_id}
                if (path.matches(billPath + "/[0-9]+/add-order/[0-9]+")) {
                    try {
                        int billId = Integer.parseInt(queryParam.get()[2]);
                        int orderId = Integer.parseInt(queryParam.get()[4]);

                        var bill = billService.findById(billId);
                        var order = orderService.findById(orderId);

                        billService.addOrder(bill, order);
                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // PATCH /bill/{id}/remove-order/{order_id}
                } else if (path.matches(billPath + "/[0-9]+/remove-order/[0-9]+")) {
                    try {
                        int billId = Integer.parseInt(queryParam.get()[2]);
                        int orderId = Integer.parseInt(queryParam.get()[4]);

                        var bill = billService.findById(billId);
                        var order = orderService.findById(orderId);

                        billService.removeOrder(bill, order);
                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
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
