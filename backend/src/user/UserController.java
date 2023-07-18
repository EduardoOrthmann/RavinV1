package user;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ErrorResponse;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class UserController implements HttpHandler {
    private final String userPath;
    private final UserService userService;
    private final Gson gson;

    public UserController(String userPath, UserService userService) {
        this.userPath = userPath;
        this.userService = userService;
        this.gson = new Gson();
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
            case "POST" -> {
                // POST /user/login
                if (path.matches(userPath + "/login")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var userCredentials = gson.fromJson(requestBody, User.class);
                        var token = userService.authenticate(userCredentials.getUsername(), userCredentials.getPassword());

                        response = gson.toJson(Map.of("token", token));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // POST /user/logout
                } else if (path.matches(userPath + "/[0-9]+" + "/logout")) {
                    try {
                        var token = queryParam.get()[2];
                        userService.logout(token);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid request path"));
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
