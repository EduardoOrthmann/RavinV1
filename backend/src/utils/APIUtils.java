package utils;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class APIUtils {
    public static String getQueryParamValue(String queryParams, String key) {
        if (queryParams == null) {
            throw new IllegalArgumentException("Invalid query params");
        }

        var queryParam = queryParams.split("&");

        var queryParamMap = Arrays.stream(queryParam)
                .collect(Collectors.toMap(
                        queryParamEntry -> queryParamEntry.split("=")[0],
                        queryParamEntry -> queryParamEntry.split("=")[1]
                ));

        return queryParamMap.get(key);
    }

    public static String extractTokenFromAuthorizationHeader(String authHeaderValue) {
        if (authHeaderValue == null) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        return authHeaderValue.split(" ")[1];
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        if (responseBody.getBytes().length > 0) {
            exchange.sendResponseHeaders(statusCode, responseBody.getBytes().length);
            exchange.getResponseBody().write(responseBody.getBytes());
        } else {
            exchange.sendResponseHeaders(statusCode, -1);
        }

        exchange.close();
    }
}
