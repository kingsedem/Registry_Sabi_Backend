package userManagement;

import core.ApiError;
import core.ApiResponse;
import core.StatusCode;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojo.countryRequest;
import pojo.createUserWithNoTenantIdPostBody;
import utils.JsonReader;
import utils.PropertyReader;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.testng.AssertJUnit.assertEquals;


public class createUsers {

    @Test(description = "Create Tenant Owner Without tenantID")
    public void validate_Create_Tenant_Super_Admin() {

        countryRequest countryRequestData = new countryRequest("Nigeria", "NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                "cicat65637@agaseo.com",
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
                countryRequestData,
                "08061994102");

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId).log().all()
                .when()
                .post(baseUrl + createUserEndpoint)
                .then()
                .extract().response();

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.CREATED.code);
        System.out.println(response.body().asString());

    }

    @Test(description = "Creating user an existing email")
    public void validate_Creating_User_with_an_existing_email() {

        countryRequest countryRequestData = new countryRequest("Nigeria","NG", "NGN");
        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody(
                "cicat65637@agaseo.com",
                "@Sabisabi123",
                "TENANT_SUPER_ADMIN",
                "Kelly",
                "Jack",
                countryRequestData,
                "08061994102");

        String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
        String createUserEndpoint = JsonReader.getTestData("createUser");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(createUserPostRequestWithNoTenantId).log().all()
                .when()
                .post(baseUrl + createUserEndpoint);

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.BAD_REQUEST.code);
        ApiError error = new ApiError(StatusCode.BAD_REQUEST, "User account already exist with same email or phone, please login!");
        //return new ApiResponse(error.getCode(), error.getMessage());
        assertEquals(error.getMessage(), "User account already exist with same email or phone, please login!" );
        System.out.println(error.getMessage());
    }
}
