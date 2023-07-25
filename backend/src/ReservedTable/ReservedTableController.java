package ReservedTable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import customer.CustomerService;
import enums.Role;
import exceptions.UnauthorizedRequestException;
import user.UserService;
import utils.APIUtils;
import utils.CustomResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class ReservedTableController implements HttpHandler {
    private final String reservedTablePath;
    private final ReservedTableService reservedTableService;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public ReservedTableController(String reservedTablePath, ReservedTableService reservedTableService, CustomerService customerService, UserService userService) {
        this.reservedTablePath = reservedTablePath;
        this.reservedTableService = reservedTableService;
        this.customerService = customerService;
        this.userService = userService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, gson.toJson(new CustomResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));

        // GET /reserved-table
        if (path.matches(reservedTablePath)) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));

                if (user.getRole() == Role.CUSTOMER) {
                    var customer = customerService.findByUserId(user.getId());
                    response = gson.toJson(reservedTableService.findByCustomer(customer.getId()));
                } else {
                    response = gson.toJson(reservedTableService.findAll());
                }

                statusCode = HttpURLConnection.HTTP_OK;
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
        // GET /reserved-table/{id}
        else if (path.matches(reservedTablePath + "/[0-9]+")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(splittedPath.get()[2]);
                var reservedTable = reservedTableService.findById(id);

                if (user.getRole() == Role.CUSTOMER && !reservedTable.getCustomers().contains(customerService.findByUserId(user.getId()))) {
                    throw new UnauthorizedRequestException();
                }

                response = gson.toJson(reservedTable);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NumberFormatException e) {
                response = gson.toJson(new CustomResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /reserved-table/customer/{id}
        else if (path.matches(reservedTablePath + "/customer/[0-9]+")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.EMPLOYEE, Role.MANAGER));
                int id = Integer.parseInt(splittedPath.get()[3]);

                response = gson.toJson(reservedTableService.findByCustomerAndDatetime(id, LocalDateTime.now()));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NumberFormatException e) {
                response = gson.toJson(new CustomResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
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

        // DELETE /reserved-table/{id}
        if (path.matches(reservedTablePath + "/[0-9]+")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                int id = Integer.parseInt(splittedPath.get()[2]);
                var reservedTable = reservedTableService.findById(id);

                if (user.getRole() == Role.CUSTOMER && !reservedTable.getCustomers().contains(customerService.findByUserId(user.getId()))) {
                    throw new UnauthorizedRequestException();
                }

                reservedTableService.delete(reservedTable);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NumberFormatException e) {
                response = gson.toJson(new CustomResponse("Invalid id"));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse("Invalid endpoint"));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}

