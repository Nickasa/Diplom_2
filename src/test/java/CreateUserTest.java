import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateUserTest {
    private User user;
    private User userWithoutCredentials;
    private User userWithoutEmail;
    private User userWithoutPassword;
    private User userWithoutName;
    private UserClient userClient;

    private String token = "";

    @Before
    public void setUp() {
        user = UserGenerator.getDefault();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (!token.isEmpty()) {
            userClient.delete(token);
        }
    }

    //Тест на создание пользователя
    @Test
    @DisplayName("Check user can be created and response code/body")
    public void userCanBeCreated () {
        ValidatableResponse response = userClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        boolean userIsCreated = response.extract().path("success");
        assertTrue("User not created", userIsCreated);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");
    }

    //Тест на создание существующего пользователя
    @Test
    @DisplayName("Check user cannot be created twice and response code/body")
    public void createExistingUser() {
        ValidatableResponse response = userClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals("Status code incorrect", SC_OK, statusCode);

        ValidatableResponse existingUserResponse = userClient.create(user);
        int existingUserStatusCode = existingUserResponse.extract().statusCode();
        assertEquals("Wrong code", SC_FORBIDDEN, existingUserStatusCode);

        String existingUserMessage = existingUserResponse.extract().path("message");
        assertEquals("Wrong message", "User already exists", existingUserMessage);

        String rawToken = response.extract().path("accessToken");

        token = rawToken.replaceFirst("Bearer ", "");
    }

    //Тест на невозможность создания пользователя без полей
    @Test
    @DisplayName("Check user cannot be created without fields and response/body")
    public void createUserWithoutRequiredFields() {
        userWithoutCredentials = UserGenerator.getEmptyCredentials();

        ValidatableResponse responseWithoutCredentials = userClient.create(userWithoutCredentials);

        int statusCodeWithoutCredentials = responseWithoutCredentials.extract().statusCode();
        assertEquals("Wrong status code", SC_FORBIDDEN, statusCodeWithoutCredentials);

        String errorMessageWithoutCredentials = responseWithoutCredentials.extract().path("message");
        assertEquals("Wrong message", "Email, password and name are required fields", errorMessageWithoutCredentials);
    }

    //Создание пользователя без почты невозможно
    @Test
    @DisplayName("Check user cannot be created without email field and response/body")
    public void createUserWithoutEmailField() {
        userWithoutEmail = UserGenerator.getGetWithoutEmail();

        ValidatableResponse responseWithoutEmail = userClient.create(userWithoutEmail);

        int statusCodeWithoutEmail = responseWithoutEmail.extract().statusCode();
        assertEquals("Wrong status code", SC_FORBIDDEN, statusCodeWithoutEmail);

        String errorMessageWithoutEmail = responseWithoutEmail.extract().path("message");
        assertEquals("Wrong message", "Email, password and name are required fields", errorMessageWithoutEmail);
    }

    //Создание пользователя без пароля невозможно
    @Test
    @DisplayName("Check user cannot be created without password field and response/body")
    public void createUserWithoutPasswordField() {
        userWithoutPassword = UserGenerator.getWithoutPassword();

        ValidatableResponse responseWithoutPassword = userClient.create(userWithoutPassword);

        int statusCodeWithoutPassword = responseWithoutPassword.extract().statusCode();
        assertEquals("Wrong status code", SC_FORBIDDEN, statusCodeWithoutPassword);

        String errorMessageWithoutPassword = responseWithoutPassword.extract().path("message");
        assertEquals("Wrong message", "Email, password and name are required fields", errorMessageWithoutPassword);
    }

    //Создание пользователя без имени невозможно
    @Test
    @DisplayName("Check user cannot be created without name field and response/body")
    public void createUserWithoutName() {
        userWithoutName = UserGenerator.getWithoutName();

        ValidatableResponse responseWithoutName = userClient.create(userWithoutName);

        int statusCodeWithoutName = responseWithoutName.extract().statusCode();
        assertEquals("Wrong status code", SC_FORBIDDEN, statusCodeWithoutName);

        String errorMessageWithoutName = responseWithoutName.extract().path("message");
        assertEquals("Wrong message", "Email, password and name are required fields", errorMessageWithoutName);
    }

}
