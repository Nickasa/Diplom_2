import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeUserInformationTest {
    private String newEmail = "burger-test12@yandex.ru";
    private String newName = "burger-test1";
    private UserEmailAndName userEmailAndName = new UserEmailAndName(newEmail, newName);
    private User user;
    private UserClient userClient;
    private String token;

    @Before
    public void setUp() {
        user = UserGenerator.getDefault();
        userClient = new UserClient();

        ValidatableResponse response = userClient.create(user);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");
    }

    @After
    public void tearDown() {
        userClient.delete(token);
    }

    //Тест на изменение информации авторизованного пользователя
    @Test
    @DisplayName("Change user with authorization and response code/body")
    public void changeUserInformationWithAuthorization() {
        ValidatableResponse changeInformationResponse = userClient.patch(userEmailAndName, token);

        int statusCode = changeInformationResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        boolean informationIsChanged = changeInformationResponse.extract().path("success");
        assertTrue("User information is not changed", informationIsChanged);

        String changedEmail = changeInformationResponse.extract().path("user.email");
        assertEquals("Email is incorrect", newEmail, changedEmail);

        String changedName = changeInformationResponse.extract().path("user.name");
        assertEquals("Name is incorrect", newName, changedName);
    }

    //Тест на изменение информации неавторизованного пользователя
    @Test
    @DisplayName("Change user without authorization and response code/body")
    public void changeUserInformationWithoutAuthorization() {
        ValidatableResponse changeInformationResponse = userClient.patchWithoutAuth(userEmailAndName);

        int statusCode = changeInformationResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_UNAUTHORIZED, statusCode);

        boolean informationIsNotChanged = changeInformationResponse.extract().path("success");
        assertTrue("Success in not false", !informationIsNotChanged);

        String errorMessage = changeInformationResponse.extract().path("message");
        assertEquals("Wrong message", "You should be authorised", errorMessage);
    }
}
