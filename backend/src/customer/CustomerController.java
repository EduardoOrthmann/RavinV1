package customer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CustomerController implements HttpHandler {
    private final String customerPath;
    private final CustomerService customerService;
    private final Gson gson;

    public CustomerController(String customerPath, CustomerService customerService) {
        this.customerPath = customerPath;
        this.customerService = customerService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        final Optional<String> queryParam = Optional.of(path.substring(path.lastIndexOf("/") + 1));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "GET" -> {
                if (path.matches(customerPath)) {
                    response = gson.toJson(customerService.findAll());
                    statusCode = 200;
                } else if (path.matches(customerPath + "/[0-9]+")) {
                    int id = Integer.parseInt(queryParam.get());

                    try {
                        var customer = customerService.findById(id);
                        response = gson.toJson(customer);

                    } catch (NoSuchElementException e) {
                        response = "Customer not found";
                        statusCode = 404;
                        break;
                    }

                    statusCode = 200;
                } else {
                    response = "Invalid endpoint";
                    statusCode = 404;
                }
            }

            case "POST" -> {
                if (path.matches(customerPath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    var customer = gson.fromJson(requestBody, Customer.class);
                    customerService.save(
                            new Customer(
                                    customer.getName(),
                                    customer.getPhoneNumber(),
                                    customer.getBirthDate(),
                                    customer.getCpf(),
                                    customer.getAddress(),
                                    customer.getCreatedBy()
                            )
                    );

                    statusCode = 201;
                } else {
                    response = "Invalid endpoint";
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                if (path.matches(customerPath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());
                    int id = Integer.parseInt(queryParam.get());

                    try {
                        var customer = customerService.findById(id);

                        var updatedCustomer = gson.fromJson(requestBody, Customer.class);
                        updatedCustomer = new Customer(
                                id,
                                updatedCustomer.getName(),
                                updatedCustomer.getPhoneNumber(),
                                updatedCustomer.getBirthDate(),
                                updatedCustomer.getCpf(),
                                updatedCustomer.getAddress(),
                                updatedCustomer.getUpdatedBy()
                        );

                        customer.setId(id);
                        customer.setName(updatedCustomer.getName());
                        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                        customer.setBirthDate(updatedCustomer.getBirthDate());
                        customer.setCpf(updatedCustomer.getCpf());
                        customer.setAddress(updatedCustomer.getAddress());
                        customer.setUpdatedAt(updatedCustomer.getUpdatedAt());
                        customer.setUpdatedBy(updatedCustomer.getUpdatedBy());

                        customerService.update(customer);
                    } catch (NoSuchElementException e) {
                        response = "Customer not found";
                        statusCode = 404;
                        break;
                    }

                    statusCode = 204;
                } else {
                    response = "Invalid endpoint";
                    statusCode = 404;
                }
            }

            case "DELETE" -> {
                if (path.matches(customerPath + "/[0-9]+")) {
                    int id = Integer.parseInt(queryParam.get());
                    var customer = customerService.findById(id);

                    try {
                        customerService.delete(customer);
                    } catch (NoSuchElementException e) {
                        response = "Customer not found";
                        statusCode = 404;
                        break;
                    }

                    statusCode = 204;
                } else {
                    response = "Invalid endpoint";
                    statusCode = 404;
                }
            }

            default -> {
                response = "Method Not Allowed";
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
