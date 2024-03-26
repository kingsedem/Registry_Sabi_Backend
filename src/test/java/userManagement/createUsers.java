package userManagement;

import core.StatusCode;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import pojo.countryRequest;
import pojo.createUserWithNoTenantIdPostBody;
import utils.JsonReader;
import utils.PropertyReader;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;



public class createUsers {

    @Test (description = "Create Tenant Owner- Without tenantID")
    public void validate_Create_Tenant_Super_Admin() {

        countryRequest countryRequestData = new countryRequest();
        countryRequestData.setName("Nigeria");
        countryRequestData.setCode("NG");
        countryRequestData.setCurrency("NGN");

        List<countryRequest> countryRequests = new ArrayList<>();
        countryRequests.add(countryRequestData);

        createUserWithNoTenantIdPostBody createUserPostRequestWithNoTenantId = new createUserWithNoTenantIdPostBody();
        createUserPostRequestWithNoTenantId.setUsername("weniyi4303@sentrau.com");
        createUserPostRequestWithNoTenantId.setPassword("@Sabisabi123");
        createUserPostRequestWithNoTenantId.setRole("TENANT_SUPER_ADMIN");
        createUserPostRequestWithNoTenantId.setFirstName("Kelly");
        createUserPostRequestWithNoTenantId.setLastName("Jack");
        createUserPostRequestWithNoTenantId.setCountryRequestBody(countryRequests);
        createUserPostRequestWithNoTenantId.setPhone("08062994000");


       String baseUrl = PropertyReader.propertyReader("config.properties", "devBaseUrl");
       String createUserEndpoint = JsonReader.getTestData("createUser");
       String url =  baseUrl + createUserEndpoint;

        Response response = given()
                 .header("Content-Type", "application/json")
                 .body(createUserPostRequestWithNoTenantId)
                 .when()
                 .post(url)
                 .then()
                 .extract().response();

        int actualStatusCode = response.statusCode();
        assertEquals(actualStatusCode, StatusCode.CREATED.code);
        System.out.println(response.body().asString());
    }
}


