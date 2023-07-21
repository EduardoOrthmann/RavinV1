package customer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import enums.Role;
import exceptions.ErrorResponse;
import exceptions.UnauthorizedRequestException;
import user.User;
import user.UserService;
import utils.APIUtils;
import utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class CustomerController implements HttpHandler {
    private final String customerPath;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public CustomerController(String customerPath, CustomerService customerService, UserService userService) {
        this.customerPath = customerPath;
        this.customerService = customerService;
        this.userService = userService;
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
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "GET" -> {
                // GET /customer
                if (path.matches(customerPath)) {
                    try {
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                        var user = userService.findByToken(headerToken);
                        var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                        if (!acceptedRoles.contains(user.getRole())) {
                            throw new UnauthorizedRequestException();
                        }

                        response = gson.toJson(customerService.findAll());
                        statusCode = 200;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (UnauthorizedRequestException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 401;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /customer/{id}
                } else if (path.matches(customerPath + "/[0-9]+")) {
                    try {
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                        int id = Integer.parseInt(splittedPath.get()[2]);

                        var user = userService.findByToken(headerToken);
                        var customer = customerService.findById(id);

                        if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                            throw new UnauthorizedRequestException();
                        }

                        response = gson.toJson(customer);
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (UnauthorizedRequestException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 401;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /customer/{id}/birthday
                } else if (path.matches(customerPath + "/[0-9]+" + "/birthday")) {
                    try {
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                        int id = Integer.parseInt(splittedPath.get()[2]);

                        var user = userService.findByToken(headerToken);
                        var customer = customerService.findById(id);

                        if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                            throw new UnauthorizedRequestException();
                        }

                        response = gson.toJson(Map.of("isBirthday", DateUtils.isBirthday(customer.getBirthDate())));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (UnauthorizedRequestException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 401;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
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
                // POST /customer
                if (path.matches(customerPath)) {
                    Integer createdBy = null;

                    try {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes());

                        if (tokenFromHeaders.isPresent()) {
                            var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.get());
                            createdBy = userService.findByToken(headerToken).getId();
                        }

                        var customer = gson.fromJson(requestBody, Customer.class);

                        customerService.save(
                                new Customer(
                                        customer.getName(),
                                        customer.getPhoneNumber(),
                                        customer.getBirthDate(),
                                        customer.getCpf(),
                                        customer.getAddress(),
                                        new User(customer.getUser().getUsername(), customer.getUser().getPassword(), Role.CUSTOMER),
                                        createdBy,
                                        customer.getAllergies() == null ? new HashSet<>() : customer.getAllergies()
                                )
                        );

                        statusCode = 201;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /customer/{id}
                if (path.matches(customerPath + "/[0-9]+")) {
                    try {
                        String requestBody = new String(exchange.getRequestBody().readAllBytes());
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                        int id = Integer.parseInt(splittedPath.get()[2]);

                        var user = userService.findByToken(headerToken);
                        var customer = customerService.findById(id);

                        if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                            throw new UnauthorizedRequestException();
                        }

                        var updatedCustomer = gson.fromJson(requestBody, Customer.class);
                        var updatedBy = user.getId();

                        updatedCustomer = new Customer(
                                id,
                                updatedCustomer.getName(),
                                updatedCustomer.getPhoneNumber(),
                                updatedCustomer.getBirthDate(),
                                updatedCustomer.getCpf(),
                                updatedCustomer.getAddress(),
                                updatedBy,
                                updatedCustomer.getAllergies()
                        );

                        customer.setName(updatedCustomer.getName());
                        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                        customer.setBirthDate(updatedCustomer.getBirthDate());
                        customer.setCpf(updatedCustomer.getCpf());
                        customer.setAddress(updatedCustomer.getAddress());
                        customer.setUpdatedAt(updatedCustomer.getUpdatedAt());
                        customer.setUpdatedBy(updatedCustomer.getUpdatedBy());
                        customer.setAllergies(updatedCustomer.getAllergies());

                        customerService.update(customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (UnauthorizedRequestException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 401;
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

            case "DELETE" -> {
                // DELETE /customer/{id}
                if (path.matches(customerPath + "/[0-9]+")) {
                    try {
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                        int id = Integer.parseInt(splittedPath.get()[2]);

                        var user = userService.findByToken(headerToken);
                        var customer = customerService.findById(id);

                        if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                            throw new UnauthorizedRequestException();
                        }

                        customerService.delete(customer);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (UnauthorizedRequestException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 401;
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
