import io.qameta.allure.Step;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends RestClient{
    private final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Get ingredients IDs")
    public List<String> getIngredientsIDs() {
            Ingredients ingredientsIDs =  given()
                    .spec(getBaseSpec())
                    .get(INGREDIENTS_PATH).body().as(Ingredients.class);
            return getIngredientId(ingredientsIDs);
        }

    public List<String> getIngredientId(Ingredients ingredients) {
        return ingredients.getIngredientData().stream().
                map(data -> data.get_id()).collect(Collectors.toList());
    }
}
