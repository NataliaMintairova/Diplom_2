import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.example.UserApi.CREATE_USER_URI;
import static org.hamcrest.core.IsEqual.equalTo;

public class CreateUserTest extends BaseTest{
    UserApi userApi;
    UserApi userApi2;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);

    @After
    @Step("clean Up")
    public void cleanUp(){
        userApi.deleteUser();
    }

    @Test
    @DisplayName("create Unique User Test")
    @Description("проверка создания уникального пользователя")
    public void createUniqueUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response = userApi.createUser(user);
         response.then().statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("can't Create Same User Test")
    @Description("проверка невозможности создания идентичного пользователя")
    public void cantCreateSameUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response1 = userApi.createUser(user);
        Response response2 = userApi.createUser(user);
        response2.then().statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("can't Create No Email User Test")
    @Description("проверка того, что невозможно создать пользователя без указания почты")
    public void cantCreateNoEmailUserTest(){
        userApi = new UserApi();
        user = new UserData(null, random, random);
        Response response = userApi.createUser(user);
        response.then().statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("can't Create No Password User Test")
    @Description("проверка того, что невозможно создать пользователя без указания пароля")
    public void cantCreateNoPasswordUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", null, random);
        Response response = userApi.createUser(user);
        response.then().statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("can't Create No Name User Test")
    @Description("проверка того, что невозможно создать пользователя без указания имени")
    public void cantCreateNoNameUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, null);
        Response response = userApi.createUser(user);
        response.then().statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
