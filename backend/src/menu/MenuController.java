package menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import configuration.LocalDateTimeTypeAdapter;
import configuration.LocalTimeTypeAdapter;
import exceptions.ErrorResponse;
import product.ProductService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MenuController implements HttpHandler {
    private final String menuPath;
    private final MenuService menuService;
    private final ProductService productService;
    private final Gson gson;

    public MenuController(String menuPath, MenuService menuService, ProductService productService) {
        this.menuPath = menuPath;
        this.menuService = menuService;
        this.productService = productService;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
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
                // GET /menu
                if (path.matches(menuPath)) {
                    try {
                        response = gson.toJson(menuService.findAll());
                        statusCode = 200;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // GET /menu/{id}
                } else if (path.matches(menuPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        response = gson.toJson(menuService.findById(id));
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
                // POST /menu
                if (path.matches(menuPath)) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        var menu = gson.fromJson(requestBody, Menu.class);
                        menuService.save(
                                new Menu(
                                        menu.getName(),
                                        menu.getMenuCode(),
                                        menu.getCreatedBy()
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
                // PUT /menu/{id}
                if (path.matches(menuPath + "/[0-9]+")) {
                    String requestBody = new String(exchange.getRequestBody().readAllBytes());

                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var menu = menuService.findById(id);

                        var updatedMenu = gson.fromJson(requestBody, Menu.class);
                        updatedMenu = new Menu(
                                id,
                                updatedMenu.getName(),
                                updatedMenu.getMenuCode(),
                                updatedMenu.getUpdatedBy()
                        );

                        menu.setName(updatedMenu.getName());
                        menu.setMenuCode(updatedMenu.getMenuCode());
                        menu.setUpdatedAt(updatedMenu.getUpdatedAt());
                        menu.setUpdatedBy(updatedMenu.getUpdatedBy());

                        menuService.update(menu);

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
                // DELETE /menu/{id}
                if (path.matches(menuPath + "/[0-9]+")) {
                    try {
                        int id = Integer.parseInt(queryParam.get()[2]);
                        var menu = menuService.findById(id);

                        menuService.delete(menu);

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

            case "PATCH" -> {
                // PATCH /menu/{id}/add-product/{productId}
                if (path.matches(menuPath + "/[0-9]+/add-product/[0-9]+")) {
                    try {
                        int menuId = Integer.parseInt(queryParam.get()[2]);
                        int productId = Integer.parseInt(queryParam.get()[4]);

                        var menu = menuService.findById(menuId);
                        var product = productService.findById(productId);

                        menuService.addProduct(menu, product);

                        statusCode = 204;
                    } catch (NoSuchElementException e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 404;
                    } catch (NumberFormatException e) {
                        response = gson.toJson(new ErrorResponse("Id inválido"));
                        statusCode = 400;
                    } catch (Exception e) {
                        response = gson.toJson(new ErrorResponse(e.getMessage()));
                        statusCode = 500;
                    }
                    // PATCH /menu/{id}/remove-product/{productId}
                } else if (path.matches(menuPath + "/[0-9]+/remove-product/[0-9]+")) {
                    try {
                        int menuId = Integer.parseInt(queryParam.get()[2]);
                        int productId = Integer.parseInt(queryParam.get()[4]);

                        var menu = menuService.findById(menuId);
                        var product = productService.findById(productId);

                        menuService.removeProduct(menu, product);

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
