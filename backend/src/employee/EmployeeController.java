package employee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import exceptions.ErrorResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EmployeeController implements HttpHandler {
    private final String employeePath;
    private final EmployeeService employeeService;
    private final Gson gson;

    public EmployeeController(String employeePath, EmployeeService employeeService) {
        this.employeePath = employeePath;
        this.employeeService = employeeService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;
        String path = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        final Optional<String[]> queryParam = Optional.of(path.split("/"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "GET" -> {
                // GET /employee
                if (path.matches(employeePath)) {
                    response = gson.toJson(employeeService.findAll());
                    statusCode = 200;

                    // GET /employee/{id}
                } else if (path.matches(employeePath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(employeeService.findById(id));
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
                // POST /employee
                if (path.matches(employeePath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var employee = gson.fromJson(requestBody, Employee.class);
                        employeeService.save(
                                new Employee(
                                        employee.getName(),
                                        employee.getPhoneNumber(),
                                        employee.getBirthDate(),
                                        employee.getCpf(),
                                        employee.getAddress(),
                                        employee.getUser(),
                                        employee.getCreatedBy(),
                                        employee.getRg(),
                                        employee.getMaritalStatus(),
                                        employee.getEducationLevel(),
                                        employee.getPosition(),
                                        employee.getWorkCardNumber()
                                )
                        );

                        statusCode = 201;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /employee/{id}
                if (path.matches(employeePath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var employee = employeeService.findById(id);

                        var updatedEmployee = gson.fromJson(requestBody, Employee.class);
                        updatedEmployee = new Employee(
                                id,
                                updatedEmployee.getName(),
                                updatedEmployee.getPhoneNumber(),
                                updatedEmployee.getBirthDate(),
                                updatedEmployee.getCpf(),
                                updatedEmployee.getAddress(),
                                updatedEmployee.getUser(),
                                updatedEmployee.getUpdatedBy(),
                                updatedEmployee.getRg(),
                                updatedEmployee.getMaritalStatus(),
                                updatedEmployee.getEducationLevel(),
                                updatedEmployee.getPosition(),
                                updatedEmployee.getWorkCardNumber(),
                                updatedEmployee.getAdmissionDate(),
                                updatedEmployee.isAvailable()
                        );

                        employee.setId(id);
                        employee.setName(updatedEmployee.getName());
                        employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
                        employee.setBirthDate(updatedEmployee.getBirthDate());
                        employee.setCpf(updatedEmployee.getCpf());
                        employee.setAddress(updatedEmployee.getAddress());
                        employee.setUser(updatedEmployee.getUser());
                        employee.setUpdatedBy(updatedEmployee.getUpdatedBy());
                        employee.setUpdatedAt(updatedEmployee.getUpdatedAt());
                        employee.setRg(updatedEmployee.getRg());
                        employee.setMaritalStatus(updatedEmployee.getMaritalStatus());
                        employee.setEducationLevel(updatedEmployee.getEducationLevel());
                        employee.setPosition(updatedEmployee.getPosition());
                        employee.setWorkCardNumber(updatedEmployee.getWorkCardNumber());
                        employee.setAdmissionDate(updatedEmployee.getAdmissionDate());
                        employee.setAvailable(updatedEmployee.isAvailable());

                        employeeService.update(employee);

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

            case "DELETE" -> {
                // DELETE /employee/{id}
                if (path.matches(employeePath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var employee = employeeService.findById(id);

                        employeeService.delete(employee);

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
