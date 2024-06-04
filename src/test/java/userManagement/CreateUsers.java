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

@Epic("USER MANAGEMENT - Create Tenant Owner")
@Feature("Tenant-Admin-User creation")

public class CreateUsers {
    @Test(priority = 1, description = "Check that user can be created successfully")
    @Description("This test ensures that a user can be created successfully without a tenantID.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Create_User(ITestContext context) {

        countryRequest countryRequestData = new countryRequest("Nigeria", "NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Testing",
                "Account",
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
        //System.out.println(phone);
    }


    @Test(priority = 2, description = "Check that user cannot be created with an existing email successfully")
    @Description("This test ensures that a user cannot be created with an existing email or username.")
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
    @Description("This test verifies that a user cannot be created with an existing phone number.")
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
    @Description("This test verifies that the user's authorization token can be generated.")
    @Severity(SeverityLevel.BLOCKER)
    public void generateAuthToken(ITestContext context) {

        String username = context.getSuite().getAttribute("tenantUsername").toString();

        loginUserPostBody userLogin = new loginUserPostBody(username, "@Sabisabi123", "TENANT_SUPER_ADMIN");
        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
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
    @Description("This test verifies that users can be deleted successfully.")
    @Severity(SeverityLevel.BLOCKER)
    public void deleteUserId(ITestContext context) {

        String authToken = context.getSuite().getAttribute("authorizationToken").toString();

        System.out.println("Authorization Token: " + authToken);
        String userId = context.getSuite().getAttribute("tenantUserId").toString();
        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
      //String deleteUserEndpoint = "/api/v1/admins/users/{id}"; // Ensure this matches your actual endpoint
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
    @Test(priority = 6, description = "Check that user cannot be created with a Phone number not matching the country name")
    @Description("This test verifies that the user's country name matches the phone number successfully.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_An_Invalid_Phone_Number() {
        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "John",
                "Jack",
                 countryRequestData,
                "0902300303"); //Invalid Phone number not matching Country (Nigeria)

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given().filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId)
                .when()
                .post(baseUrl + createUserEndpoint);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_PHONE_NUMBER.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Phone Number supplied isn't valid.");
        System.out.println("Message: " + message);
    }


    @Test(priority = 7, description = "Check that user cannot be created with username as digits successfully")
    @Description("This test verifies that the users cannot be created with username as only digits.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_Username_As_Digits() {
        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                "123.com", //Username as digit
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_ONLY_DIGITS_IN_USERNAME.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "[username must be an email]");
        System.out.println("Message: " + message);
    }


    @Test(priority = 8, description = "Check that user cannot be created  with password less than three characters")
    @Description("This test verifies that the users cannot be created with username as only digits.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_Password_Less_Than_Three_Characters() {
        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "12", //Password  less than three characters
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_LESS_THAN_THREE_CHARACTERS.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "[password must be longer than or equal to 3 characters]"); //This error message comes from validator
        System.out.println("Message: " + message);
    }

    @Test(priority = 9, description = "Check that user cannot be created with password as digits only")
    @Description("This test verifies that the users cannot be created with password as only digits.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_Password_As_Digits_Only() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "1245678", //Password as only digits
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Password must be at least 8 characters long. Password must contain at least one uppercase letter. Password must contain at least one lowercase letter. Password must contain at least one special character.");
        System.out.println("Message: " + message);
    }

    @Test(priority = 10, description = "Check that user cannot be created with a password lacking an uppercase letter.")
    @Description("This test verifies that users cannot be created with a password that does not contain an uppercase letter.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_WithOut_Uppercase_Letter_In_Password() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@string111", //Password without an uppercase letter
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_NO_UPPERCASE_IN_PASSWORD.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Password must contain at least one uppercase letter.");
        System.out.println("Message: " + message);
    }

    @Test(priority = 11, description = "Check that user cannot be created with a password lacking a lowercase letter.")
    @Description("This test verifies that users cannot be created with a password that does not contain a lowercase letter.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_WithOut_Lowercase_Letter_In_Password() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@SABISABI111", //Password without lowercase letter
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_NO_LOWERCASE_IN_PASSWORD.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Password must contain at least one lowercase letter.");
        System.out.println("Message: " + message);
    }

    @Test(priority = 12, description = "Check that user cannot be created without including a number in the password.")
    @Description("This test verifies that a user cannot be created without including a number in the password.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_WithOut_Numbers_In_Password() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi", //Password without numbers
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_PASSWORD_AS_DIGITS.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Password must contain at least one number.");
        System.out.println("Message: " + message);
    }

    @Test(priority = 13, description = "Check that user cannot be created without including a Special Character in the password.")
    @Description("This test verifies that a user cannot be created without including a Special Character in the password.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_WithOut_Special_Characters_In_Password() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "Sabisabi123", //Password with no special characters
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_NO_SPECIAL_CHARACTERS_IN_PASSWORD.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Password must contain at least one special character.");
        System.out.println("Message: " + message);
    }

    @Test(priority = 14,description = "Check that user cannot be created with an invalid role")
    @Description("This test verifies that a user cannot be created without including a Special Character in the password.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_An_Invalid_role() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "SUPER", //Invalid role type
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_ROLE.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Please supply a valid role type");
        System.out.println("Message: " + message);
    }
    @Test(priority = 15, description = "Check that user cannot be created with first name less than 3 characters")
    @Description("This test verifies that a user cannot be created with first name less than 3 characters ")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_FirstName_Less_Than_3_Characters() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Ke", //First name less than three characters
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_FIRSTNAME_LESS_THAN_THREE_CHARACTERS.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "[firstName must be longer than or equal to 3 characters]");
        System.out.println("Message: " + message);
    }

    @Test( priority = 16, description = "Check that user cannot be created with last name less than 3 characters")
    @Description("This test verifies that a user cannot be created with last name less than 3 characters ")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_LastName_Less_Than_3_Characters() {
        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Ja", //last name less than three characters
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_LASTNAME_LESS_THAN_THREE_CHARACTERS.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "[lastName must be longer than or equal to 3 characters]");
        System.out.println("Message: " + message);
    }


    @Test(priority = 17, description = "Check that user cannot be created with an invalid Country name")
    @Description("This test ensures that a user cannot be created with an invalid Country name")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Creating_User_With_An_Invalid_Country_Name() {
                                                                      //Invalid CountryName
        countryRequest countryRequestData = new countryRequest("Nigeriana","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                userRandomEmail(),
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
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
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST_INVALID_COUNTRY_NAME.code);
        String message = response.jsonPath().getString("message");
        Assert.assertEquals(message, "Invalid country name!");
        System.out.println("Message: " + message);
    }
}
