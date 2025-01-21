package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserApi {
    public Response handleCreate;
    public Response handleLogin;
    public String accessToken;
    public static final String CREATE_USER_URI = "/api/auth/register";
    public static final String USER_URI = "/api/auth/user";
    public static final String LOGIN_USER_URI = "/api/auth/login";

    @Step("create User")
    public Response createUser(UserData user) {
        handleCreate =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(user)
                        .when()
                        .post(CREATE_USER_URI);
        accessToken = handleCreate.then()
                .extract().path("accessToken");
        return handleCreate;
    }

    @Step("delete User")
    public void deleteUser() {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .when()
                    .delete(USER_URI)
                    .then()
                    .log().all();
        }
    }

    @Step("login User")
    public Response loginUser(UserData user){
        handleLogin =
        given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_URI);
        accessToken = handleLogin.then()
                .extract().path("accessToken");
        return handleLogin;
    }

    @Step("change Email User")
    public Response changeEmailUser(UserData user1){
        return
        given()
                .header("Authorization", accessToken)
                .header("Content-type", "application/json")
                .body(user1)
                .when()
                .patch(USER_URI);
    }

    @Step("change Email Unauthorized User")
    public Response changeEmailUnauthorizedUser(UserData user1){
        return
                given()
                        .header("Content-type", "application/json")
                        .body(user1)
                        .when()
                        .patch(USER_URI);
    }


}
