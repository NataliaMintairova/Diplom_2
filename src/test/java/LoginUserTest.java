import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;

public class LoginUserTest {
    UserApi userApi;
    UserData user;
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
    public void loginCorrectDataTest(){
        user = new UserData(user.getEmail(), user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void cantLoginIncorrectEmailTest(){
        user = new UserData("1" +user.getEmail(), user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(401)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantLoginIncorrectPasswordTest(){
        user = new UserData(user.getEmail(), "1" + user.getPassword());
        Response response = userApi.loginUser(user);
        response.then().statusCode(401)
                .assertThat()
                .body("success", equalTo(false));
    }

}
