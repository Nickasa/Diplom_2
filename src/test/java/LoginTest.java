import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginTest {

    private User user;
    private User userWithoutEmail;
    private User userWithoutPassword;
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

    //Тест на логин пользователя
    @Test
    @DisplayName("Check user login and response code/body")
    public void userCanLogin() {
        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));

        int statusCode = loginResponse.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        boolean userLoggedIn = loginResponse.extract().path("success");
        assertTrue("User not logged in", userLoggedIn);

    }

    //Тест с разными вариациями логина пользователя с отсутствующими данными
    @Test
    @DisplayName("Check login attempt with missing credentials and response code/body")
    public void userWithMissingCredentials() {
        userWithoutEmail = UserGenerator.getGetWithoutEmail();
        userWithoutPassword = UserGenerator.getWithoutPassword();

        //Логин пользователя без почты невозможен
        ValidatableResponse responseWithoutEmail = userClient.login(UserCredentials.from(userWithoutEmail));

        int statusCodeWithoutEmail = responseWithoutEmail.extract().statusCode();
        assertEquals("Wrong status code", SC_UNAUTHORIZED, statusCodeWithoutEmail);

        String errorMessageWithoutEmail = responseWithoutEmail.extract().path("message");
        assertEquals("Wrong message", "email or password are incorrect", errorMessageWithoutEmail);

        //Логин пользователя без пароля невозможен
        ValidatableResponse responseWithoutPassword = userClient.login(UserCredentials.from(userWithoutPassword));

        int statusCodeWithoutPassword = responseWithoutPassword.extract().statusCode();
        assertEquals("Wrong status code", SC_UNAUTHORIZED, statusCodeWithoutPassword);

        String errorMessageWithoutPassword = responseWithoutPassword.extract().path("message");
        assertEquals("Wrong message", "email or password are incorrect", errorMessageWithoutPassword);

    }

}
