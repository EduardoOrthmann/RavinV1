package customer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import enums.Role;
import utils.CustomResponse;
import exceptions.UnauthorizedRequestException;
import user.User;
import user.UserService;
import utils.APIUtils;
import utils.DateUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            case "PUT" -> putHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // GET /customer
        if (path.matches(customerPath)) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                response = gson.toJson(customerService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /customer/{id}
        else if (path.matches(customerPath + "/[0-9]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var customer = customerService.findById(id);

                if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(customer);
                statusCode = HttpURLConnection.HTTP_OK;
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
        // GET /customer/{id}/birthday
        else if (path.matches(customerPath + "/[0-9]+" + "/birthday")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var customer = customerService.findById(id);

                if (user.getRole() == Role.CUSTOMER && user.getId() != customer.getUser().getId()) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(Map.of("isBirthday", DateUtils.isBirthday(customer.getBirthDate())));
                statusCode = HttpURLConnection.HTTP_OK;
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

    private void postHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes());

        // POST /customer
        if (path.matches(customerPath)) {
            Integer createdBy = null;

            try {
                if (tokenFromHeaders.isPresent()) {
                    var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.get());
                    createdBy = userService.findByToken(headerToken).getId();
                }

                var customer = gson.fromJson(requestBody, Customer.class);

                if (userService.existsByUsername(customer.getUser().getUsername())) {
                    throw new IllegalArgumentException("Username já cadastrado");
                }

                customerService.save(
                        new Customer(
                                customer.getName(),
                                customer.getPhoneNumber(),
                                customer.getBirthDate(),
                                customer.getCpf(),
                                customer.getAddress(),
                                new User(customer.getUser().getUsername(), customer.getUser().getPassword(), Role.CUSTOMER),
                                createdBy
                        )
                );

                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
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

    private void putHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

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
                        updatedBy
                );

                customer.setName(updatedCustomer.getName());
                customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
                customer.setBirthDate(updatedCustomer.getBirthDate());
                customer.setCpf(updatedCustomer.getCpf());
                customer.setAddress(updatedCustomer.getAddress());
                customer.setUpdatedAt(updatedCustomer.getUpdatedAt());
                customer.setUpdatedBy(updatedCustomer.getUpdatedBy());

                customerService.update(customer);

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

    private void deleteHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

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
