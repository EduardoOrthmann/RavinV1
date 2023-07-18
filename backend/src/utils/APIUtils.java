package utils;

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
}
