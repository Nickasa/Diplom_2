import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class OrderTest {
    private User user;
    private UserClient userClient;
    private IngredientsClient ingredientsClient;
    private OrderClient orderClient;
    private String token = "";

    @Before
    public void setUp() {
        ingredientsClient = new IngredientsClient();
        user = UserGenerator.getDefault();
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (!token.isEmpty()) {
            userClient.delete(token);
        }
    }

    //Тест на создание заказа без авторизации
    @Test
    @DisplayName("Check order can be created without authorization and response code/body")
    public void createOrderWithoutAuthorization() {
        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(0));
        orderIngredients.add(ingredientsIDs.get(6));

        Order order = new Order(orderIngredients);

        ValidatableResponse createOrderResponse = orderClient.createWithoutAuthorization(order);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        boolean orderCreated = createOrderResponse.extract().path("success");
        assertTrue("Order is not created", orderCreated);
    }

    //В данном тесте проверяется что можно создать заказ с авторизацией и ингридиентами (отдельно тест на проверку создания заказа с ингридиентами но без авторизации не стал делать)
    @Test
    @DisplayName("Check order can be created with authorization and ingredients and response code/body")
    public void createOrderWithAuthorizationAndIngredients() {
        ValidatableResponse response = userClient.create(user);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");

        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(0));
        orderIngredients.add(ingredientsIDs.get(6));

        Order order = new Order(orderIngredients);

        ValidatableResponse createOrderResponse = orderClient.createWithAuthorization(order, token);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        boolean orderCreated = createOrderResponse.extract().path("success");
        assertTrue("Order is not created", orderCreated);

    }

    //Тест на создание заказа без ингредиентов
    @Test
    @DisplayName("Check order cannot be created without ingredients and response code/body")
    public void createOrderWithoutIngredients() {
        List<String> orderIngredients = new ArrayList<>();

        Order order = new Order(orderIngredients);

        ValidatableResponse createOrderResponse = orderClient.createWithoutAuthorization(order);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_BAD_REQUEST, statusCode);

        boolean orderCreated = createOrderResponse.extract().path("success");
        assertTrue("Wrong response", !orderCreated);

        String responseMessage = createOrderResponse.extract().path("message");
        assertEquals("Status code incorrect", "Ingredient ids must be provided", responseMessage);
    }

    //Тест на создания заказа с неправильным хешем ингредиентов
    @Test
    @DisplayName("Check order cannot be created with wrong hash of ingredients and response code/body")
    public void createOrderWithWrongHashOfIngredients() {
        List<String> orderIngredients = new ArrayList<>();

        Order order = new Order(orderIngredients);

        orderIngredients.add("123");
        orderIngredients.add("456");

        ValidatableResponse createOrderResponse = orderClient.createWithoutAuthorization(order);

        int statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_INTERNAL_SERVER_ERROR, statusCode);

        String orderResponseBody = createOrderResponse.toString();
        assertNotNull("Body is null", orderResponseBody);
    }

    //Тест на получение заказа авторизованного пользователя
    @Test
    @DisplayName("Get orders from authorized user")
    public void getOrdersFromAuthorizedUser() {
        ValidatableResponse response = userClient.create(user);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");

        List<String> ingredientsIDs = ingredientsClient.getIngredientsIDs();

        List<String> orderIngredients = new ArrayList<>();

        orderIngredients.add(ingredientsIDs.get(0));
        orderIngredients.add(ingredientsIDs.get(6));

        Order order = new Order(orderIngredients);

        orderClient.createWithAuthorization(order, token);

        ValidatableResponse userOrdersResponse = orderClient.getWithAuthorization(token);

        int statusCode = userOrdersResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        List<String> ordersCount = userOrdersResponse.extract().path("orders");
        assertNotNull("Orders is empty", ordersCount);

    }

    //Тест на получение заказа неавторизованного пользователя
    @Test
    @DisplayName("Get orders without authorization")
    public void getOrdersWithoutAuthorization() {
        ValidatableResponse userOrdersResponse = orderClient.getWithoutAuthorization();

        int statusCode = userOrdersResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_UNAUTHORIZED, statusCode);

        boolean successStatus = userOrdersResponse.extract().path("success");
        assertTrue("Success in not false", !successStatus);

        //Непонятно, нужно было ли проверять тело ответа, сделал потому что написано в задании
        String userOrdersResponseMessage = userOrdersResponse.extract().path("message");
        assertEquals("Wrong message", "You should be authorised", userOrdersResponseMessage);
    }
}
