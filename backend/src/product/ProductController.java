package product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import enums.Role;
import exceptions.ErrorResponse;
import exceptions.UnauthorizedRequestException;
import user.UserService;
import utils.APIUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class ProductController implements HttpHandler {
    private final String productPath;
    private final ProductService productService;
    private final UserService userService;
    private final Gson gson;

    public ProductController(String productPath, ProductService productService, UserService userService) {
        this.productPath = productPath;
        this.productService = productService;
        this.userService = userService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> getHandler(exchange);
            case "POST" -> postHandler(exchange);
            case "PUT" -> putHandler(exchange);
            case "DELETE" -> deleteHandler(exchange);
            default -> APIUtils.sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, gson.toJson(new ErrorResponse("Invalid request method")));
        }
    }

    private void getHandler(HttpExchange exchange) throws IOException {
        String response;
        int statusCode;

        String path = exchange.getRequestURI().getPath();
        final Optional<String[]> splittedPath = Optional.of(path.split("/"));

        // GET /product
        if (path.matches(productPath)) {
            try {
                response = gson.toJson(productService.findAll());
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        }
        // GET /product/{id}
        else if (path.matches(productPath + "/[0-9]+")) {
            try {
                int id = Integer.parseInt(splittedPath.get()[2]);
                response = gson.toJson(productService.findById(id));
                statusCode = HttpURLConnection.HTTP_OK;
            } catch (NoSuchElementException e) {
                response = gson.toJson(new ErrorResponse(e.getMessage()));
                statusCode = HttpURLConnection.HTTP_NOT_FOUND;
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

        // POST /product
        if (path.matches(productPath)) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));

                var user = userService.findByToken(headerToken);
                var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                if (!acceptedRoles.contains(user.getRole())) {
                    throw new UnauthorizedRequestException();
                }

                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                var product = gson.fromJson(requestBody, Product.class);
                var createdBy = user.getId();

                productService.save(
                        new Product(
                                product.getName(),
                                product.getDescription(),
                                product.getProductCode(),
                                product.getCostPrice(),
                                product.getSalePrice(),
                                product.getPreparationTime(),
                                createdBy
                        )
                );

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

        // PUT /product/{id}
        if (path.matches(productPath + "/[0-9]+")) {
            try {
                String requestBody = new String(exchange.getRequestBody().readAllBytes());
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var product = productService.findById(id);
                var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                if (!acceptedRoles.contains(user.getRole())) {
                    throw new UnauthorizedRequestException();
                }

                var updatedProduct = gson.fromJson(requestBody, Product.class);
                var updatedBy = user.getId();

                updatedProduct = new Product(
                        id,
                        updatedProduct.getName(),
                        updatedProduct.getDescription(),
                        updatedProduct.getProductCode(),
                        updatedProduct.getCostPrice(),
                        updatedProduct.getSalePrice(),
                        updatedProduct.getPreparationTime(),
                        updatedProduct.isAvailable(),
                        updatedBy
                );

                product.setName(updatedProduct.getName());
                product.setDescription(updatedProduct.getDescription());
                product.setProductCode(updatedProduct.getProductCode());
                product.setCostPrice(updatedProduct.getCostPrice());
                product.setSalePrice(updatedProduct.getSalePrice());
                product.setPreparationTime(updatedProduct.getPreparationTime());
                product.setAvailable(updatedProduct.isAvailable());
                product.setUpdatedAt(updatedProduct.getUpdatedAt());
                product.setUpdatedBy(updatedProduct.getUpdatedBy());

                productService.update(product);

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

        // DELETE /product/{id}
        if (path.matches(productPath + "/[0-9]+")) {
            try {
                var headerToken = APIUtils.extractTokenFromAuthorizationHeader(tokenFromHeaders.orElse(null));
                int id = Integer.parseInt(splittedPath.get()[2]);

                var user = userService.findByToken(headerToken);
                var product = productService.findById(id);
                var acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

                if (!acceptedRoles.contains(user.getRole())) {
                    throw new UnauthorizedRequestException();
                }

                productService.delete(product);

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
