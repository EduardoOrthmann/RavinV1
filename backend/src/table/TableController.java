package table;

import ReservedTable.ReservedTable;
import ReservedTable.ReservedTableService;
import bill.BillService;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TableController implements HttpHandler {
    private final String tablePath;
    private final TableService tableService;
    private final ReservedTableService reservedTableService;
    private final BillService billService;
    private final CustomerService customerService;
    private final UserService userService;
    private final Gson gson;

    public TableController(String tablePath, TableService tableService, ReservedTableService reservedTableService, BillService billService, CustomerService customerService, UserService userService) {
        this.tablePath = tablePath;
        this.tableService = tableService;
        this.reservedTableService = reservedTableService;
        this.billService = billService;
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
            case "POST" -> postHandler(exchange);
            case "PUT" -> putHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, gson.toJson(new CustomResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));

        // GET /table
        if (path.matches(tablePath)) {
            try {
                response = gson.toJson(tableService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /table/{id}
        else if (path.matches(tablePath + "/[0-9]+")) {
            try {
                int id = Integer.parseInt(splittedPath.get()[2]);
                response = gson.toJson(tableService.findById(id));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (NumberFormatException e) {
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
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        String requestBody = new String(exchange.getRequestBody().readAllBytes());

        // POST /table
        if (path.matches(tablePath)) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                var table = gson.fromJson(requestBody, Table.class);
                var createdBy = user.getId();

                response = gson.toJson(
                        tableService.save(
                                new Table(
                                        table.getName(),
                                        table.getTableNumber(),
                                        table.getMaxCapacity(),
                                        createdBy
                                )
                        )
                );

                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // POST /table/{id}/reserve
        else if (path.matches(tablePath + "/[0-9]+/reserve")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.values()));
                var id = Integer.parseInt(splittedPath.get()[2]);

                var reservedTable = gson.fromJson(requestBody, ReservedTable.class);
                var table = tableService.findById(id);
                var createdBy = user.getId();
                var customers = reservedTable.getCustomers().stream()
                        .map(customer -> customerService.findById(customer.getId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                reservedTable.setCustomers(customers);
                reservedTable.setCreatedBy(createdBy);

                response = gson.toJson(reservedTableService.reserveTable(table, reservedTable));
                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // POST /table/reserved-table/{id}/occupy
        else if (path.matches(tablePath + "/reserved-table/[0-9]+/occupy")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));

                int id = Integer.parseInt(path.split("/")[3]);
                var reservedTable = reservedTableService.findById(id);

                tableService.occupyTable(reservedTable.getTable(), reservedTable.getCustomers());

                response = gson.toJson(new CustomResponse("Mesa ocupada com sucesso"));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (IllegalStateException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_CONFLICT;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // POST /table/{id}/free
        else if (path.matches(tablePath + "/[0-9]+/free")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null), Set.of(Role.ADMIN, Role.MANAGER, Role.EMPLOYEE));

                int id = Integer.parseInt(path.split("/")[2]);
                var table = tableService.findById(id);

                if (billService.existsByTableAndIsPaid(id, false)) {
                    throw new IllegalStateException("Não é possível liberar uma mesa com contas em aberto");
                }

                tableService.freeTable(table);

                response = gson.toJson(new CustomResponse("Mesa liberada com sucesso"));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (IllegalStateException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_CONFLICT;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
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

    private void putHandler(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // PUT /table/{id}
        if (path.matches(tablePath + "/[0-9]+")) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                int id = Integer.parseInt(splittedPath.get()[2]);
                var table = tableService.findById(id);
                var updatedTable = gson.fromJson(requestBody, Table.class);
                var updatedBy = user.getId();

                updatedTable = new Table(
                        id,
                        updatedTable.getName(),
                        updatedTable.getTableNumber(),
                        updatedTable.getMaxCapacity(),
                        updatedTable.getStatus(),
                        updatedBy
                );

                table.setName(updatedTable.getName());
                table.setTableNumber(updatedTable.getTableNumber());
                table.setMaxCapacity(updatedTable.getMaxCapacity());
                table.setStatus(updatedTable.getStatus());
                table.setUpdatedAt(updatedTable.getUpdatedAt());
                table.setUpdatedBy(updatedTable.getUpdatedBy());

                tableService.update(table);

                statusCode = HttpURLConnection.HTTP_NO_CONTENT;
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

        // DELETE /table/{id}
        if (path.matches(tablePath + "/[0-9]+")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                int id = Integer.parseInt(splittedPath.get()[2]);
                var table = tableService.findById(id);

                tableService.delete(table);

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
