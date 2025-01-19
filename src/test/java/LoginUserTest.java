import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.ObjectUtils.notEqual;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class LoginUserTest extends BaseTest{
    UserApi userApi;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);

    @Before
    @Step("set Up")
    public void setUp(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response = userApi.createUser(user);
    }

    @After
    @Step("clean Up")
    public void cleanUp(){
        userApi.deleteUser();
    }

    @Test
    @DisplayName("login Correct Data Test")
    @Description("проверка успешной авторизации пользователя")
    public void loginCorrectDataTest(){
        user = new UserData(user.getEmail(), user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("can't Login Incorrect Email Test")
    @Description("проверка того, что нельзя авторизоваться с неверной почтой")
    public void cantLoginIncorrectEmailTest(){
        user = new UserData("1" +user.getEmail(), user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("can't Login Incorrect Password Test")
    @Description("проверка того, что нельзя авторизоваться с неверным паролем")
    public void cantLoginIncorrectPasswordTest(){
        user = new UserData(user.getEmail(), "1" + user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
