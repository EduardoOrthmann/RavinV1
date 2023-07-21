package table;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalDateTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import exceptions.ErrorResponse;
import exceptions.UnauthorizedRequestException;
import user.UserService;
import utils.APIUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TableController implements HttpHandler {
    private final String tablePath;
    private final TableService tableService;
    private final UserService userService;
    private final Gson gson;

    public TableController(String tablePath, TableService tableService, UserService userService) {
        this.tablePath = tablePath;
        this.tableService = tableService;
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
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, gson.toJson(new ErrorResponse("Invalid request method")));
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
                response = gson.toJson(new ErrorResponse(e.getMessage()));
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
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (NumberFormatException e) {
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

        // POST /table
        if (path.matches(tablePath)) {
            try {
                var user = userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                var table = gson.fromJson(requestBody, Table.class);
                var createdBy = user.getId();

                tableService.save(new Table(
                        table.getName(),
                        table.getTableNumber(),
                        table.getMaxCapacity(),
                        createdBy
                ));

                statusCode = HttpURLConnection.HTTP_CREATED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
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

        // DELETE /table/{id}
        if (path.matches(tablePath + "/[0-9]+")) {
            try {
                userService.checkUserRoleAndAuthorize(tokenFromHeaders.orElse(null));

                int id = Integer.parseInt(splittedPath.get()[2]);
                var table = tableService.findById(id);

                tableService.delete(table);

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
