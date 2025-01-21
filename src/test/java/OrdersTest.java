import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
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


public class OrdersTest extends BaseTest{
    UserApi userApi;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);
    public static final String ING_1 = "61c0c5a71d1f82001bdaaa6d";
    public static final String ING_2 = "61c0c5a71d1f82001bdaaa6f";

    @Before
    @Step("set Up")
    public void setUp() {
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
    @DisplayName("create Order Correct Ingredients Authorized User")
    @Description("проверка создания заказа с корректными ингредиентами авторизированным пользователем")
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
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("create Order Correct Ingredients Unauthorized User")
    @Description("проверка создания заказа с корректными ингредиентами неавторизированным пользователем")
    public void createOrderCorrectIngridsUnautorizedUser() {
        OrderApi orderApi = new OrderApi();
        orderApi.accessToken = userApi.accessToken;
        List<String> ingredients = java.util.List.of(ING_1,ING_2);
        OrderData orderData = new OrderData(ingredients);
        Response response1 = orderApi.createOrder(orderData);
        response1.then()
                .log().all()
                //Код 200 по согласованию с наставником в Пачке
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("create Order Without Ingredients Authorized User")
    @Description("проверка создания заказа без ингредиентов авторизированным пользователем")
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
                .statusCode(HTTP_BAD_REQUEST)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("create Order Incorrect Ingredients Authorized User")
    @Description("проверка создания заказа с некорректными ингредиентами авторизированным пользователем")
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
                .statusCode(HTTP_INTERNAL_SERVER_ERROR);
    }
}
