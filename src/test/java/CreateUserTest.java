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

public class CreateUserTest {
    UserApi userApi;
    UserApi userApi2;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);

    @Before
public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void cleanUp(){
        userApi.deleteUser();
    }

    @Test
    public void createUniqueUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response = userApi.createUser(user);
         response.then().statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void cantCreateSameUserTest(){
        userApi = new UserApi();
        userApi2 = new UserApi();
        user = new UserData(random + "@yandex.ru", random, random);
        Response response1 = userApi.createUser(user);
        Response response2 = userApi2.createUser(user);
        response2.then().statusCode(403)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantCreateNoEmailUserTest(){
        userApi = new UserApi();
        user = new UserData(null, random, random);
        Response response = userApi.createUser(user);
        response.then().statusCode(403)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantCreateNoPasswordUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", null, random);
        Response response = userApi.createUser(user);
        response.then().statusCode(403)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void cantCreateNoNameUserTest(){
        userApi = new UserApi();
        user = new UserData(random + "@yandex.ru", random, null);
        Response response = userApi.createUser(user);
        response.then().statusCode(403)
                .assertThat()
                .body("success", equalTo(false));
    }
}
