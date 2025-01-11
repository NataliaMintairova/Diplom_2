import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;

public class ChangeUserDataTest {
    UserApi userApi;
    UserData user;
    UserData user1;
    String random = RandomStringUtils.randomAlphabetic(5);

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
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
                .statusCode(200)
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
                .statusCode(403)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantChangeDataUnauthorizedUserTest(){
        user1 = new UserData("22" + user.getEmail(), user.getPassword(), "22" + user.getName());
        Response response = userApi.changeEmailUser(user1);
        response.then()
                .log().all()
                .statusCode(401)
                .assertThat()
                .body("success", equalTo(false));
    }
}
