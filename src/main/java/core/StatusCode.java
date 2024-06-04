package core;

public enum StatusCode {
    SUCCESS(200, "The request succeeded"),
    SUCCESS_OK(200, "OK" ),
    CREATED(201, "The resource was created"),
    NO_CONTENT(204, "No content to send in the response body"),
    BAD_REQUEST_NO_UPPERCASE_IN_PASSWORD(400, "Password must contain at least one uppercase letter."),
    BAD_REQUEST_NO_SPECIAL_CHARACTERS_IN_PASSWORD(400, "Password must contain at least one special character."),
    BAD_REQUEST_ONLY_DIGITS_IN_USERNAME(400, "[username must be an email]"),
    BAD_REQUEST_NO_LOWERCASE_IN_PASSWORD(400, "Password must contain at least one lowercase letter."),
    BAD_REQUEST_PASSWORD_WITHOUT_UPPERCASE(400, "username must be an email"),

    BAD_REQUEST_PASSWORD_LESS_THAN_THREE_CHARACTERS(400, "password must be longer than or equal to 3 characters"),
    BAD_REQUEST_PASSWORD_AS_DIGITS(400, "Password must be at least 8 characters long. Password must contain at least one uppercase letter. Password must contain at least one lowercase letter. Password must contain at least one special character."),

    BAD_REQUEST_FIRSTNAME_LESS_THAN_THREE_CHARACTERS(400, "firstName must be longer than or equal to 3 characters"),
    BAD_REQUEST_LASTNAME_LESS_THAN_THREE_CHARACTERS(400, "lastName must be longer than or equal to 3 characters"),

    BAD_REQUEST_USER_EXIST(400, "User account already exist with same email or phone, please login!"),
    BAD_REQUEST_INVALID_ROLE(400, "Please supply a valid role type"),
    BAD_REQUEST_INVALID_COUNTRY_NAME(400, "Invalid country name!"),

    BAD_REQUEST_INVALID_PHONE_NUMBER(400, "Phone Number supplied isn't valid."),
    UNAUTHORIZED(401, "Invalid access token"),
    NOT_FOUND(404, "Cannot find requested resource");

    public  final int code;

    StatusCode(int code, String message){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
