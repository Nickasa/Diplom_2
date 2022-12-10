import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient{
    private final String ORDERS_PATH = "/api/orders";

    @Step("Create new order {order}")
    public ValidatableResponse createWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .when()
                .body(order)
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Create new order {order} without authorization")
    public ValidatableResponse createWithAuthorization(Order order, String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .when()
                .body(order)
                .post(ORDERS_PATH)
                .then();
    }

    @Step("Get orders with authorization")
    public ValidatableResponse getWithAuthorization(String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    @Step("Get orders without authorization")
    public ValidatableResponse getWithoutAuthorization() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDERS_PATH)
                .then();
    }
}
