package core;

public enum StatusCode {
    SUCCESS(200), CREATED(201), O_CONTENT(204), BAD_REQUEST(400), UNAUTHORIZED(401), NOT_FOUND(404);

    public  final int code;

    StatusCode(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
