import java.util.List;

public class Ingredients {
    private boolean success;
    private List<Data> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Data> getIngredientData() {
        return data;
    }

    public void setIngredientData(List<Data> ingredientData) {
        this.data = ingredientData;
    }
}
