package uk.appinvent.lunchfinder;

import com.google.gson.Gson;

import org.junit.Test;

import uk.appinvent.lunchfinder.data.Dish;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void convert_from_json_to_dish() throws Exception{
        String json_string = " {\n" +
                "      \"Id\":1,\n" +
                "      \"Name\":\"Penne with Sun-Dried Tomato Pesto\",\n" +
                "      \"Description\":\"Pulsing jarred sun-dried tomatoes and their oil in a food processor is all it takes for a tangy, flavorful pesto\",\n" +
                "      \"WaitingTime\":15,\n" +
                "      \"Price\":6.50,\n" +
                "      \"ImageUrl\":\"http://lunchfinder.esy.es/images/454-ei1c12_penne_pesto.jpg.rend.snigalleryslide.jpeg\",\n" +
                "      \"ShortDescription\":\"Pulsing jarred sun-dried tomatoes and their oil in a food processor is all it takes for a tangy, flavorful pesto\",\n" +
                "      \"IsDishOftheDay\":true,\n" +
                "      \"DishOfDayNumber\":1,\n" +
                "      \"Category\":\"Pasta\"\n" +
                "   }";

        Gson gson = new Gson();
        Dish dish = gson.fromJson(json_string, Dish.class);
        assertNotNull(dish);
        System.out.println(dish.getCategory());
    }

    @Test
    public void convert_to_json_from_object(){
        String json_string = " {\n" +
                "      \"Id\":1,\n" +
                "      \"Name\":\"Penne with Sun-Dried Tomato Pesto\",\n" +
                "      \"Description\":\"Pulsing jarred sun-dried tomatoes and their oil in a food processor is all it takes for a tangy, flavorful pesto\",\n" +
                "      \"WaitingTime\":15,\n" +
                "      \"Price\":6.50,\n" +
                "      \"ImageUrl\":\"http://lunchfinder.esy.es/images/454-ei1c12_penne_pesto.jpg.rend.snigalleryslide.jpeg\",\n" +
                "      \"ShortDescription\":\"Pulsing jarred sun-dried tomatoes and their oil in a food processor is all it takes for a tangy, flavorful pesto\",\n" +
                "      \"IsDishOftheDay\":true,\n" +
                "      \"DishOfDayNumber\":1,\n" +
                "      \"Category\":\"Pasta\"\n" +
                "   }";

        Gson gson = new Gson();
        Dish dish = gson.fromJson(json_string, Dish.class);
        assertNotNull(dish);
        System.out.println(dish.getCategory());
        String generatedJson = gson.toJson(dish);
        assertNotNull(generatedJson);
        System.out.println(generatedJson);
    }

}