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

import static org.hamcrest.core.IsEqual.equalTo;

public class ChangeUserDataTest extends BaseTest{
    UserApi userApi;
    UserData user;
    UserData user1;
    UserData user2;
    String random = RandomStringUtils.randomAlphabetic(5);
    String random2 = "1" + random;

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
    @DisplayName("change Email Authorized User Test")
    @Description("проверка обновления почты пользователя при авторизации")
    public void changeEmailAuthorizedUserTest(){
        userApi.loginUser(user);
        user1 = new UserData("22" + user.getEmail(), user.getPassword(), user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo("22" + user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("change Name Authorized User Test")
    @Description("проверка обновления имени пользователя при авторизации")
    public void changeNameAuthorizedUserTest(){
        userApi.loginUser(user);
        user1 = new UserData(user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo("22" + user.getName()));
    }

    @Test
    @DisplayName("error Change Same Email Authorized User Test")
    @Description("проверка получения ошибки при передаче почты, которая уже используется")
    public void errorChangeSameEmailAuthorizedUserTest(){
        UserApi userApi2 = new UserApi();
        user2 = new UserData(random2 + "@yandex.ru", random2, random2);
       userApi2.createUser(user2);
        userApi.loginUser(user);
        user1 = new UserData(user2.getEmail(), user.getPassword(), user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
        userApi2.deleteUser();
    }

    @Test
    @DisplayName("can't Change Email Unauthorized User Test")
    @Description("проверка того, что нельзя изменить почту пользователя без авторизации")
    public void cantChangeEmailUnauthorizedUserTest(){
        user1 = new UserData("22" + user.getEmail(), user.getPassword(), user.getName());
        Response response = userApi.changeEmailUnauthorizedUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("can't Change Name Unauthorized User Test")
    @Description("проверка того, что нельзя изменить имя пользователя без авторизации")
    public void cantChangeNameUnauthorizedUserTest(){
        user1 = new UserData(user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUnauthorizedUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
