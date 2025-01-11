package org.example;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    public String accessToken;
    public static final String CREATE_ORDER_URI = "/api/orders";

    @Step("create Order")
    public Response createOrder(OrderData order){
        return
                given()
                        .header("Authorization", accessToken)
                        .header("Content-type", "application/json")
                        .body(order)
                        .when()
                        .post(CREATE_ORDER_URI);
    }

    @Step("get Orders List")
    public  Response getOrdersList(){
        return
                given()
                        .header("Authorization", accessToken)
                        .get(CREATE_ORDER_URI);
    }
}
