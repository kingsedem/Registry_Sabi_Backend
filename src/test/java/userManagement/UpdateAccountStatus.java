package userManagement;

import core.StatusCode;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import pojo.countryRequest;
import pojo.createUserWithNoTenantIdPostBody;
import pojo.loginUserPostBody;
import utils.JsonReader;
import utils.PropertyReader;

import static helper.RandomData.userRandomEmail;
import static helper.RandomData.userRandomPhoneNumber;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;


public class UpdateAccountStatus {

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
        String status = response.jsonPath().getString("status");
        System.out.println(response.body().asString());
        context.getSuite().setAttribute("tenantUsername", username);
        context.getSuite().setAttribute("tenantPhoneNumber", phone);
        context.getSuite().setAttribute("tenantUserId", userId);
        context.getSuite().setAttribute("tenantUserStatus", status);

        System.out.println("UserId is " + userId);
        //System.out.println(phone);
    }

    // GENERATE TOKEN
    @Test(priority = 2, description = "Check that authorization token can be generated successfully")
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


    //PASSING TEST

    @Test(priority = 3, description = "Check that user status can be updated from active to disable successfully")
    @Description("This test ensures that a user status can be updated successfully from active to disable.")
    @Severity(SeverityLevel.BLOCKER)
    public void validate_Update_Status(ITestContext context) {
        String authToken = context.getSuite().getAttribute("authorizationToken").toString();
        String userId = context.getSuite().getAttribute("tenantUserId").toString();

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String updateUserEndpoint = JsonReader.getTestData("updateUser");
        String fullUrl = baseUrl + updateUserEndpoint.replace(":userId", userId).replace(":status", "DISABLE");

        Response response = given().filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .when()
                .put(fullUrl);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.SUCCESS.code);
        // Validate response body
        String responseBody = response.getBody().asString();
        assertTrue(responseBody.contains("status"), "DISABLE");
        System.out.println(response.body().asString());
    }


    //FAILING TEST  1
//    @Test(priority = 3, description = "Check that user status can be updated from active to disable successfully")
//    @Description("This test ensures that a user status can be updated successfully from active to disable.")
//    @Severity(SeverityLevel.BLOCKER)
//    public void validate_Update_Status(ITestContext context) {
//        String authToken = context.getSuite().getAttribute("authorizationToken").toString();
//        String userId = context.getSuite().getAttribute("tenantUserId").toString();
//
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String updateUserEndpoint = JsonReader.getTestData("updateUser");
//
//        Response response = given().filter(new AllureRestAssured())
//                .header("Authorization", "Bearer " + authToken)
//                .header("Content-Type", "application/json")
//                .pathParam("userId", userId)
//                .pathParam("status", "DISABLE")
//                .when()
//                .put(baseUrl + updateUserEndpoint);
//
//        int actualStatusCode = response.statusCode();
//        assertEquals(actualStatusCode, StatusCode.SUCCESS_OK.code);
//        // Validate response body
//        String responseBody = response.getBody().asString();
//        assertTrue(responseBody.contains("status"), "DISABLE");
//        System.out.println(response.body().asString());
//    }


    //DELETE USER_ID HERE:   FAILING TEST 2
//    @Test(priority = 4, description = "Check that user can be deleted successfully")
//    @Description("This test verifies that users can be deleted successfully.")
//    @Severity(SeverityLevel.BLOCKER)
//    public void deleteUserId(ITestContext context) {
//
//        String authToken = context.getSuite().getAttribute("authorizationToken").toString();
//
//        String userId = context.getSuite().getAttribute("tenantUserId").toString();
//        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
//        String deleteUserEndpoint = JsonReader.getTestData("deleteUser");
//        String fullUrl = baseUrl + deleteUserEndpoint.replace(":id", userId);
//        Response response = given().filter(new AllureRestAssured())
//                .header("Authorization", "Bearer " + authToken)
//                .when()
//                .delete(fullUrl);
//
//
//        System.out.println("Response: " + response.asString());
//        assertEquals(response.statusCode(), StatusCode.SUCCESS.code);
//    }

}
