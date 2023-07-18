package product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import exceptions.ErrorResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ProductController implements HttpHandler {
    private final String productPath;
    private final ProductService productService;
    private final Gson gson;

    public ProductController(String productPath, ProductService productService) {
        this.productPath = productPath;
        this.productService = productService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
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
            case "GET" -> {
                // GET /product
                if (path.matches(productPath)) {
                    try {
                        response = gson.toJson(productService.findAll());
                        statusCode = 200;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /product/{id}
                } else if (path.matches(productPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(productService.findById(id));
                        statusCode = 200;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "POST" -> {
                // POST /product
                if (path.matches(productPath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var product = gson.fromJson(requestBody, Product.class);
                        productService.save(
                                new Product(
                                        product.getName(),
                                        product.getDescription(),
                                        product.getProductCode(),
                                        product.getCostPrice(),
                                        product.getSalePrice(),
                                        product.getPreparationTime(),
                                        product.getCreatedBy()
                                )
                        );

                        statusCode = 201;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "PUT" -> {
                // PUT /product/{id}
                if (path.matches(productPath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var product = productService.findById(id);

                        var updatedProduct = gson.fromJson(requestBody, Product.class);
                        updatedProduct = new Product(
                                id,
                                updatedProduct.getName(),
                                updatedProduct.getDescription(),
                                updatedProduct.getProductCode(),
                                updatedProduct.getCostPrice(),
                                updatedProduct.getSalePrice(),
                                updatedProduct.getPreparationTime(),
                                updatedProduct.isAvailable(),
                                updatedProduct.getUpdatedBy()
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

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                } else {
                    response = gson.toJson(new ErrorResponse("Invalid endpoint"));
                    statusCode = 404;
                }
            }

            case "DELETE" -> {
                // DELETE /product/{id}
                if (path.matches(productPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var product = productService.findById(id);

                        productService.delete(product);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Invalid id"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
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
