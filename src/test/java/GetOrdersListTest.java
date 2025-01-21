import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.example.OrderApi;
import org.example.UserApi;
import org.example.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;

public class GetOrdersListTest extends BaseTest {
    UserApi userApi;
    UserData user;
    String random = RandomStringUtils.randomAlphabetic(5);

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
    @DisplayName("get Orders List Authorized User Test")
    @Description("проверка получения списка заказов авторизированным пользователем")
    public void getOrdersListAuthorizedUserTest(){
        OrderApi orderApi = new OrderApi();
        user = new UserData(user.getEmail(), user.getPassword());
        userApi.loginUser(user);
        orderApi.accessToken = userApi.accessToken;
        Response response = orderApi.getOrdersList();
        response.then()
                .log().all()
                .statusCode(HTTP_OK)
                .assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("get Orders List Unauthorized User Test")
    @Description("проверка того, что невозможно получить список заказов без авторизации")
    public void getOrdersListUnauthorizedUserTest(){
        OrderApi orderApi = new OrderApi();
        orderApi.accessToken = userApi.accessToken;
        Response response = orderApi.getOrdersListUnauthorized();
        response.then()
                .log().all()
                .statusCode(HTTP_UNAUTHORIZED)
                .assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
