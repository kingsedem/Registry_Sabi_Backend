package pojo;

import java.util.List;

public record createUserWithNoTenantIdPostBody(String username, String password, String role, String firstName, String lastName, countryRequest country, String phone) {}



