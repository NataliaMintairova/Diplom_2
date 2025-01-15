import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.OrderApi;
import org.example.OrderData;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;


public class OrdersTest {
    UserApi userApi;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);
    public static final String ING_1 = "61c0c5a71d1f82001bdaaa6d";
    public static final String ING_2 = "61c0c5a71d1f82001bdaaa6f";

    @Before
    public void setUp() {
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
    public void createOrderCorrectIngridsAutorizedUser() {
        OrderApi orderApi = new OrderApi();
        user = new UserData(user.getEmail(), user.getPassword());
        userApi.loginUser(user);
        orderApi.accessToken = userApi.accessToken;
        List<String> ingredients = java.util.List.of(ING_1,ING_2);
        OrderData orderData = new OrderData(ingredients);
        Response response1 = orderApi.createOrder(orderData);
        response1.then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void createOrderCorrectIngridsUnautorizedUser() {
        OrderApi orderApi = new OrderApi();
        orderApi.accessToken = userApi.accessToken;
        List<String> ingredients = java.util.List.of(ING_1,ING_2);
        OrderData orderData = new OrderData(ingredients);
        Response response1 = orderApi.createOrder(orderData);
        response1.then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void createOrderWithoutIngridsAutorizedUser() {
        OrderApi orderApi = new OrderApi();
        user = new UserData(user.getEmail(), user.getPassword());
        userApi.loginUser(user);
        orderApi.accessToken = userApi.accessToken;
        List<String> ingredients = java.util.List.of();
        OrderData orderData = new OrderData(ingredients);
        Response response1 = orderApi.createOrder(orderData);
        response1.then()
                .log().all()
                .statusCode(400)
                .assertThat()
                .body("success", equalTo(false));
    }

    @Test
    public void createOrderIncorrectIngridsAutorizedUser() {
        OrderApi orderApi = new OrderApi();
        user = new UserData(user.getEmail(), user.getPassword());
        userApi.loginUser(user);
        orderApi.accessToken = userApi.accessToken;
        List<String> ingredients = java.util.List.of(ING_1 + "1",ING_2);
        OrderData orderData = new OrderData(ingredients);
        Response response1 = orderApi.createOrder(orderData);
        response1.then()
                .log().all()
                .statusCode(500);
    }

    @Test
    public void getOrdersListAuthorizedUserTest(){
        OrderApi orderApi = new OrderApi();
        user = new UserData(user.getEmail(), user.getPassword());
        userApi.loginUser(user);
        orderApi.accessToken = userApi.accessToken;
        Response response = orderApi.getOrdersList();
        response.then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    public void getOrdersListUnauthorizedUserTest(){
        OrderApi orderApi = new OrderApi();
        orderApi.accessToken = userApi.accessToken;
        Response response = orderApi.getOrdersList();
        response.then()
                .log().all()
                .statusCode(401)
                .assertThat()
                .body("success", equalTo(false));
    }
}
