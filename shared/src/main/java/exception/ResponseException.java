package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }


    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);

        Object statusObject = map.get("status");
        int status = (statusObject instanceof Double) ? ((Double) statusObject).intValue() : 500;

        String message = (map.containsKey("message")) ? map.get("message").toString() : "Unknown error occurred";

        return new ResponseException(status, message);
    }
}