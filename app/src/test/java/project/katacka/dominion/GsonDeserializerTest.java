package project.katacka.dominion;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionShopPileState;

import static android.content.ContentValues.TAG;

public class GsonDeserializerTest {
    String expansionSet;

    @BeforeClass
    private void setup() {
        expansionSet = "base";
    }

    @Test //Julian Donovan
    public void testDeserialize() {
        try (InputStream shopStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("shop_cards.json");
             InputStream baseStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("base_cards.json")) {
            //shopCards = reader.generateCards(shopStream, 10);
            //baseCards = reader.generateCards(baseStream, 7);
        }
        catch (IOException e) {
            Log.e(TAG, "Error while generating card pile: " + e);
            //return null;
        }
    }
}
