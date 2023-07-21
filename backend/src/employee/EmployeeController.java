package employee;

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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class EmployeeController implements HttpHandler {
    private final String employeePath;
    private final EmployeeService employeeService;
    private final UserService userService;
    private final Gson gson;

    public EmployeeController(String employeePath, EmployeeService employeeService, UserService userService) {
        this.employeePath = employeePath;
        this.employeeService = employeeService;
        this.userService = userService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            case "PUT" -> putHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, String.valueOf(new ErrorResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // GET /employee
        if (path.matches(employeePath)) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                var user = userService.findByToken(headerToken);
                var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                if (!acceptedRoles.contains(user.getRole())) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(employeeService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /employee/{id}
        else if (path.matches(employeePath + "/[0-9]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var employee = employeeService.findById(id);

                if ((user.getRole() == Role.EMPLOYEE && user.getId() != employee.getUser().getId()) || user.getRole() == Role.CUSTOMER) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(employee);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            response = gson.toJson(new ErrorResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // POST /employee
        if (path.matches(employeePath)) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                var user = userService.findByToken(headerToken);
                var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                if (!acceptedRoles.contains(user.getRole())) {
                    throw new UnauthorizedRequestException();
                }

                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                var employee = gson.fromJson(requestBody, Employee.class);
                var createdBy = user.getId();

                employeeService.save(
                        new Employee(
                                employee.getName(),
                                employee.getPhoneNumber(),
                                employee.getBirthDate(),
                                employee.getCpf(),
                                employee.getAddress(),
                                new User(employee.getUser().getUsername(), employee.getUser().getPassword(), employee.getUser().getRole()),
                                createdBy,
                                employee.getRg(),
                                employee.getMaritalStatus(),
                                employee.getEducationLevel(),
                                employee.getPosition(),
                                employee.getWorkCardNumber()
                        )
                );

                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            response = gson.toJson(new ErrorResponse("Invalid endpoint"));
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

        // PUT /employee/{id}
        if (path.matches(employeePath + "/[0-9]+")) {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var employee = employeeService.findById(id);

                if ((user.getRole() == Role.EMPLOYEE && user.getId() != employee.getUser().getId()) || user.getRole() == Role.CUSTOMER) {
                    throw new UnauthorizedRequestException();
                }

                var updatedEmployee = gson.fromJson(requestBody, Employee.class);
                var updatedBy = user.getId();

                updatedEmployee = new Employee(
                        id,
                        updatedEmployee.getName(),
                        updatedEmployee.getPhoneNumber(),
                        updatedEmployee.getBirthDate(),
                        updatedEmployee.getCpf(),
                        updatedEmployee.getAddress(),
                        updatedBy,
                        updatedEmployee.getRg(),
                        updatedEmployee.getMaritalStatus(),
                        updatedEmployee.getEducationLevel(),
                        updatedEmployee.getPosition(),
                        updatedEmployee.getWorkCardNumber(),
                        updatedEmployee.getAdmissionDate()
                );

                employee.setId(id);
                employee.setName(updatedEmployee.getName());
                employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
                employee.setBirthDate(updatedEmployee.getBirthDate());
                employee.setCpf(updatedEmployee.getCpf());
                employee.setAddress(updatedEmployee.getAddress());
                employee.setUpdatedBy(updatedEmployee.getUpdatedBy());
                employee.setUpdatedAt(updatedEmployee.getUpdatedAt());
                employee.setRg(updatedEmployee.getRg());
                employee.setMaritalStatus(updatedEmployee.getMaritalStatus());
                employee.setEducationLevel(updatedEmployee.getEducationLevel());
                employee.setPosition(updatedEmployee.getPosition());
                employee.setWorkCardNumber(updatedEmployee.getWorkCardNumber());
                employee.setAdmissionDate(updatedEmployee.getAdmissionDate());

                employeeService.update(employee);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            response = gson.toJson(new ErrorResponse("Invalid endpoint"));
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

        // DELETE /employee/{id}
        if (path.matches(employeePath + "/[0-9]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var employee = employeeService.findById(id);

                if ((user.getRole() == Role.EMPLOYEE && user.getId() != employee.getUser().getId()) || user.getRole() == Role.CUSTOMER) {
                    throw new UnauthorizedRequestException();
                }

                employeeService.delete(employee);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } else {
            response = gson.toJson(new ErrorResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
