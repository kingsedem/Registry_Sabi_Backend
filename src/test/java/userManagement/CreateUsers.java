package userManagement;


import core.StatusCode;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import pojo.countryRequest;
import pojo.createUserWithNoTenantIdPostBody;
import pojo.loginUserPostBody;
import utils.JsonReader;
import utils.PropertyReader;

import static helper.RandomData.userRandomPhoneNumber;
import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;
import static helper.RandomData.userRandomEmail;

@Epic("User MANAGEMENT - Create Tenant Owner")
@Feature("Tenant-Admin-User creation")

public class CreateUsers {
    @Test(priority = 1, description = "Check that user can be created successfully")
    @Description("his ensures user is created without a tenantID")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Create_User(ITestContext context) {

        countryRequest countryRequestData = new countryRequest("Nigeria", "NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "TestTenantAdmin",
                "User",
                countryRequestData,
                userRandomPhoneNumber());

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId).log().all()
                .when()
                .post(baseUrl + createUserEndpoint);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.CREATED.code);
        String username = response.jsonPath().getString("username");  //This gets the username response that is returned
        String phone = response.jsonPath().getString("phone");
        String userId = response.jsonPath().getString("_id");
        System.out.println(response.body().asString());
        context.getSuite().setAttribute("tenantUsername", username);
        context.getSuite().setAttribute("tenantPhoneNumber", phone);
        context.getSuite().setAttribute("tenantUserId", userId);
        System.out.println("UserId is " + userId);
    }


    @Test(priority = 2, description = "Check that user cannot be created with an existing email successfully")
    @Description("This ensures user cannot be created  with an existing email/username")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_An_Existing_Email(ITestContext context) {

        String username = context.getSuite().getAttribute("tenantUsername").toString();

        countryRequest countryRequestData = new countryRequest("Nigeria", "NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                username,  //Existing email
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "TestTenantAdmin",
                "User",
                countryRequestData,
                userRandomPhoneNumber());

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId)
                .when()
                .post(baseUrl + createUserEndpoint);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_USER_EXIST.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User account already exist with same email or phone, please login!");
        System.out.println("Message: " + message);
        System.out.println("Existing email is: " + username);
    }

    @Test(priority = 3, description = "Check that user cannot be created with an existing Phone Number successfully")
    @Description("This ensures user cannot be created with an existing Phone Number")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_An_Existing_PhoneNumber(ITestContext context) {

        String phone = context.getSuite().getAttribute("tenantPhoneNumber").toString();

        countryRequest countryRequestData = new countryRequest("Nigeria", "NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "TestTenantAdmin",
                "User",
                countryRequestData,
                phone);  //Existing Phone number

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId)
                .when()
                .post(baseUrl + createUserEndpoint);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_USER_EXIST.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "User account already exist with same email or phone, please login!");
        System.out.println("Message: " + message);
        System.out.println("Existing Phone Number is: " + phone);
    }

    // GENERATE TOKEN

    @Test(priority = 4, description = "Check that authorization token can be generated successfully")
    @Description("This ensures Authorization token of user can be generated")
    @Severity(SeverityLevel.BLOCKER)
    public void generateAuthToken(ITestContext context) {

        String username = context.getSuite().getAttribute("tenantUsername").toString();

        loginUserPostBody userLogin = new loginUserPostBody(username, "@Sabisabi123", "TENANT_SUPER_ADMIN");
        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        //String loginUserEndpoint = "/api/v1/users/login/public"; // Adjust the endpoint as needed
        String loginUserEndpoint = JsonReader.getTestData("login");

        Response response = given().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(userLogin)
                .when()
                .post(baseUrl + loginUserEndpoint);

        assertEquals(response.statusCode(), StatusCode.CREATED.code);
        String authToken = response.jsonPath().getString("accessToken");
        System.out.println("Token is: " + authToken);

// Store the token in the suite context for later use
        context.getSuite().setAttribute("authorizationToken", authToken);
    }

    //DELETE USER_ID HERE
    @Test(priority = 5, description = "Check that user can be deleted successfully")
    @Description("This ensures users can be deleted successfully")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteUserId(ITestContext context) {

        String authToken = context.getSuite().getAttribute("authorizationToken").toString();

        System.out.println("Authorization Token: " + authToken);
        String userId = context.getSuite().getAttribute("tenantUserId").toString();
        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
      //  String deleteUserEndpoint = "/api/v1/admins/users/{id}"; // Ensure this matches your actual endpoint
         String deleteUserEndpoint = JsonReader.getTestData("deleteUser");
         String fullUrl = baseUrl + deleteUserEndpoint.replace(":id", userId);

        Response response = given().filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .when()
                .delete(fullUrl);

        System.out.println("Response: " + response.asString());
        assertEquals(response.statusCode(), StatusCode.SUCCESS.code);
    }



    //OTHER NEGATIVE SCENARIOS
//    @Test(priority = 6, description = "Creating Tenant Owner with an invalid Phone number not matching country name")
//    public void validate_Creating_User_With_An_Invalid_Phone_Number() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi123",
//                "TENANT_SUPER_ADMIN",
//                "John",
//                "Jack",
//                 countryRequestData,
//                "0902300303"); //Invalid Phone number not matching Country
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_PHONE_NUMBER.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Phone Number supplied isn't valid.");
//        System.out.println("Message: " + message);
//    }
//
//
//    @Test(priority = 7, description = "Creating Tenant Owner with username as digits")
//    public void validate_Creating_User_With_Username_As_Digits() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                "123.com", //Username as digit
//                "@Sabisabi123",
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_DIGITS_AS_USERNAME.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "[username must be an email]");
//        System.out.println("Message: " + message);
//    }
//
//
//    @Test(priority = 8, description = "Creating Tenant Owner with password less than three characters")
//    public void validate_Creating_User_With_Password_Less_Than_Three_Characters() {
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "12", //Password  less than three characters
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_LESS_THAN_THREE_CHARACTERS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "[password must be longer than or equal to 3 characters]");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 9, description = "Creating Tenant Owner with password as digits only")
//    public void validate_Creating_User_With_Password_As_Digits_Only() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "1245678", //Password as only digits
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Password must contain a number, special character, alphabet both upper and lower case, and must be at least of 6 letters.");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 10, description = "Password must contain uppercase letter")
//    public void validate_Creating_User_WithOut_Uppercase_Letter_In_Password() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@string111", //Password as only digits
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Password must contain a number, special character, alphabet both upper and lower case, and must be at least of 6 letters.");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 11, description = "Password must contain lowercase letter")
//    public void validate_Creating_User_WithOut_Lowercase_Letter_In_Password() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@SABISABI111", //Password as only digits
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Password must contain a number, special character, alphabet both upper and lower case, and must be at least of 6 letters.");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 12, description = "Password must contain Numbers")
//    public void validate_Creating_User_WithOut_Numbers_In_Password() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi", //Password as only digits
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Password must contain a number, special character, alphabet both upper and lower case, and must be at least of 6 letters.");
//        System.out.println("Message: " + message);
//    }
//
//
//    @Test(priority = 13, description = "Password must contain Special Characters")
//    public void validate_Creating_User_WithOut_Special_Characters_In_Password() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "Sabisabi123", //Password as only digits
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Password must contain a number, special character, alphabet both upper and lower case, and must be at least of 6 letters.");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 14,description = "Creating Tenant Owner with an invalid role")
//    public void validate_Creating_User_With_An_Invalid_role() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi123",
//                "SUPER", //Invalid role type
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_ROLE.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Please supply a valid role type");
//        System.out.println("Message: " + message);
//    }
//    @Test(priority = 15, description = "Creating Tenant Owner with first name less than 3 characters")
//    public void validate_Creating_User_With_FirstName_Less_Than_3_Characters() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi123",
//                "TENANT_SUPER_ADMIN",
//                "Ke", //First name less than three characters
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_FIRSTNAME_LESS_THAN_THREE_CHARACTERS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "[firstName must be longer than or equal to 3 characters]");
//        System.out.println("Message: " + message);
//    }
//
//    @Test( priority = 16, description = "Creating Tenant Owner with last name less than 3 characters")
//    public void validate_Creating_User_With_LastName_Less_Than_3_Characters() {
//
//        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi123",
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Ja", //last name less than three characters
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_LASTNAME_LESS_THAN_THREE_CHARACTERS.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "[lastName must be longer than or equal to 3 characters]");
//        System.out.println("Message: " + message);
//    }
//
//    @Test(priority = 17, description = "Creating Tenant Owner with an invalid Country name")
//    public void validate_Creating_User_With_An_Invalid_Country_Name() {
//
//                                                                      //Invalid CountryName
//        countryRequest countryRequestData = new countryRequest("Nigeriana","NG", "NGN");
//        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
//                userRandomEmail(),
//                "@Sabisabi123",
//                "TENANT_SUPER_ADMIN",
//                "Kelly",
//                "Jack",
//                countryRequestData,
//                userRandomPhoneNumber());
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String createUserEndpoint = JsonReader.getTestData("createUser");
//
//        Response response = given()
//                .header("Content-Type", "application/json")
//                .body(createUserPostRequestWithNoTenantId)
//                .when()
//                .post(baseUrl + createUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_COUNTRY_NAME.code);
//        String message = response.jsonPath().getString("message");
//        Assert.assertEquals(message, "Invalid country name!");
//        System.out.println("Message: " + message);
//    }
}
