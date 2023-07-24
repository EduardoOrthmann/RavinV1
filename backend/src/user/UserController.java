package user;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.CustomResponse;
import utils.APIUtils;

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
        int statusCode;
        String response = "";
        var path = exchange.getRequestURI().getPath();
        var requestMethod = exchange.getRequestMethod();
        var queryParams = exchange.getRequestURI().getQuery();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        switch (requestMethod) {
            case "POST" -> {
                // POST /user/login
                if (path.matches(userPath + "/login")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var userCredentials = gson.fromJson(requestBody, User.class);
                        var token = userService.login(userCredentials.getUsername(), userCredentials.getPassword());

                        response = gson.toJson(Map.of("token", token));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // POST /user/logout
                } else if (path.matches(userPath + "/logout")) {
                    try {
                        var token = APIUtils.getQueryParamValue(queryParams, "token");
                        var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                        if (!headerToken.equals(token)) {
                            throw new IllegalArgumentException("Token diferente do informado no header");
                        }

                        userService.logout(token);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (IllegalArgumentException e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new CustomResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new CustomResponse("Invalid request path"));
                    statusCode = 404;
                }
            }

            default -> {
                response = gson.toJson(new CustomResponse("Invalid request method"));
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
