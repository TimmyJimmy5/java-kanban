package service.http;

public enum HttpMethods {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private final String method;

        private HttpMethods(String method) {
            this.method = method;
        }
    }