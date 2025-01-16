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
    String random = RandomStringUtils.randomAlphabetic(5);

    @Before
    public void setUp(){
        //RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response = userApi.createUser(user);
    }

    @After
    public void cleanUp(){
        userApi.deleteUser();
    }

    @Test
    public void changeEmailAndNameAuthorizedUserTest(){
        userApi.loginUser(user);
        user1 = new UserData("22" + user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void errorChangeSameEmailAuthorizedUserTest(){
        userApi.loginUser(user);
        user1 = new UserData(user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                //Должен падать
                //В документации: Если передать почту, которая уже используется, вернётся код ответа 403
                .statusCode(HTTP_FORBIDDEN)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantChangeDataUnauthorizedUserTest(){
        user1 = new UserData("22" + user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                //Должен падать
                //В документации: Если выполнить запрос без авторизации, вернётся код ответа 401
                .statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false));
    }
}
