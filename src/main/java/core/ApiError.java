package core;

public class ApiError {
    private StatusCode code;
    private String message;

    public ApiError(StatusCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public StatusCode getCode() {
        return code;
    }

    public void setCode(StatusCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
