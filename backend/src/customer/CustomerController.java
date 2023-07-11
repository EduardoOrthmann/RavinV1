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

public class CustomerController implements HttpHandler {
    private final CustomerService customerService;
    private final Gson gson;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (path) {
            case "/customer/findAll" -> {
                if (requestMethod.equalsIgnoreCase("GET")) {
                    response = gson.toJson(customerService.findAll());
                    statusCode = 200;
                } else {
                    response = "Method Not Allowed";
                    statusCode = 405;
                }
            }

            case "/customer/save" -> {
                if (requestMethod.equalsIgnoreCase("POST")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    var customer = gson.fromJson(requestBody, Customer.class);
                    customerService.save(customer);

                    response = "Created";
                    statusCode = 201;
                } else {
                    response = "Method Not Allowed";
                    statusCode = 405;
                }
            }

            default -> {
                response = "Invalid endpoint";
                statusCode = 404;
            }
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}
