package domains.user;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.UnauthorizedRequestException;
import utils.APIUtils;
import utils.Constants;
import utils.CustomResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
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
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_METHOD)));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // GET /user (findAll)
        if (path.matches(userPath)) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null));

                var users = userService.findAll();

                response = gson.toJson(users);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /user/{id} (findById)
        else if (path.matches(userPath + "/[0-9]+")) {
            try {
                userService.authorizeUserByRole(tokenFromHeaders.orElse(null));

                var id = Integer.parseInt(path.split("/")[2]);
                var user = userService.findById(id);

                response = gson.toJson(user);
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // invalid endpoint
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_NOT_FOUND;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }

    private void postHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        var tokenFromHeaders = Optional.ofNullable(exchange.getRequestHeaders().getFirst("Authorization"));

        // POST /user/login (login)
        if (path.matches(userPath + "/login")) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());

            try {
                var userCredentials = gson.fromJson(requestBody, User.class);
                var token = userService.login(userCredentials.getUsername(), userCredentials.getPassword());

                exchange.getResponseHeaders().add("Authorization", token);

                response = gson.toJson(new CustomResponse(Constants.SUCCESS_MESSAGE));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // POST /user/logout?token={token} (logout)
        else if (path.matches(userPath + "/logout")) {
            try {
                var queryParams = exchange.getRequestURI().getQuery();
                var token = APIUtils.getQueryParamValue(queryParams, "token");
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                if (!headerToken.equals(token)) {
                    throw new UnauthorizedRequestException("Token diferente do informado no header");
                }

                userService.logout(token);

                response = gson.toJson(new CustomResponse(Constants.SUCCESS_MESSAGE));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
            } catch (UnauthorizedRequestException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_UNAUTHORIZED;
            } catch (IllegalArgumentException e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
            } catch (Exception e) {
                response = gson.toJson(new CustomResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // Invalid request path
        else {
            response = gson.toJson(new CustomResponse(Constants.INVALID_REQUEST_ENDPOINT));
            statusCode = HttpURLConnection.HTTP_BAD_REQUEST;
        }

        APIUtils.sendResponse(exchange, statusCode, response);
    }
}
