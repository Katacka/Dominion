package project.katacka.dominion.gamestate;

import android.content.Context;
import android.support.annotation.RawRes;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * A wrapper class for GsonDeserializer purposed to provide card generation abstraction.
 * Reads JSON files and returns ArrayLists with CardPile instances
 *
 * @author Julian Donovan, Ryan Regier, Ashika Mulagada, Hayden Liao
 */
public class CardReader{

    private GsonBuilder gsonBuilder;
    private final Type arrayType;
    private final Gson gsonParser;

    /**
     * Constructs a CardReader object serving as a wrapper to interface with the GsonDeserializer class
     * @param expansionSet Application context allowing for global information regarding environment/resources
     */
    public CardReader(String expansionSet) {
        gsonBuilder = new GsonBuilder();
        arrayType = new TypeToken<ArrayList<DominionShopPileState>>(){}.getType();
        gsonBuilder.registerTypeAdapter(arrayType, new GsonDeserializer(expansionSet));
        gsonParser = gsonBuilder.create();
    }

    //Known bug: Images have different sizes

    /**
     * An overloaded generateCards method intended to simplify cases where all possible
     * DominionShopPileState objects should be extracted from the JSON file in question
     * @param context Application context allowing for global information regarding environment/resources
     * @param resourceID Application-specific resource file reference
     * @return A DominionShopPileState ArrayList as populated by GsonDeserializer
     */
    public ArrayList<DominionShopPileState> generateCards(Context context, @RawRes int resourceID){
        return generateCards(context, -1, resourceID);
    }

    /**
     * Intended to return the specified number of DominionShopPileState objects as extracted and
     * randomly selected from the JSON file in question
     * @param context Application context allowing for global information regarding environment/resources
     * @param uniqueCardPiles Describes the number of DominionShopPileState objects to put in the ArrayList
     * @param resourceID Application-specific resource file reference
     * @return A DominionShopPileState ArrayList as populated by GsonDeserializer
     */
    public ArrayList<DominionShopPileState> generateCards(Context context, int uniqueCardPiles, @RawRes int resourceID) {
        try (InputStream ins = context.getResources().openRawResource(resourceID)) {
            ArrayList<DominionShopPileState> cardPiles = gsonParser.fromJson(new InputStreamReader(ins, "UTF-8"), arrayType);
            return (uniqueCardPiles > 0) ? selectCards(cardPiles, uniqueCardPiles) : cardPiles;
        }
        catch (IOException e) {
            Log.e(TAG, "Error while generating card pile: " + e);
            return null;
        }
    }

    /**
     * Returns a selection of the DominionShopPileState ArrayList as parsed by GsonDeserializer
     * @param cardPiles ArrayList of DominionShopPileState objects as parsed by GsonDeserializer
     * @param uniqueCardPiles Number of unique, requested card piles
     * @return An ArrayList of DominionShopPileState objects equal to or less than uniqueCardPiles
     *         (considering that uniqueCardPiles could be greater than the number of unique cards
     *         stored in JSON)
     */
    private ArrayList<DominionShopPileState> selectCards(ArrayList<DominionShopPileState> cardPiles, int uniqueCardPiles) {
        if (cardPiles.size() > uniqueCardPiles) {
            Collections.shuffle(cardPiles);
            return (ArrayList<DominionShopPileState>) cardPiles.subList(0, uniqueCardPiles);
        }
        return cardPiles;
    }
}
