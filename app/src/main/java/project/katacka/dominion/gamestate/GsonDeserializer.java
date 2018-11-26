package project.katacka.dominion.gamestate;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A custom deserializer implementation purposed to convert rawJSON data to an ArrayList of
 * DominionCardState objects
 * @author Julian Donovan
 */
public class GsonDeserializer implements JsonDeserializer<ArrayList<DominionShopPileState>> {

    private final String expansionSet;

    /**
     * Creates a customized deserializer capable of interpreting raw JSON
     * representations of DominionCardState objects
     * @param expansionSet Sets the expansion set, determining the base and shop cards available for generation
     */
    public GsonDeserializer(String expansionSet) {
        this.expansionSet = expansionSet;
    }

    /**
     * Parses the JSON element in question, yielding a DominionCardState object ArrayList
     * @param el Describes the JSON element being streamed
     * @param type The type of object to deserialize to
     * @param jsonContext The surrounding context as to the JSON element
     * @return A DominionCardState ArrayList as populated by JSON data
     * @throws JsonParseException Throws when parsing fails
     */
    @Override
    public ArrayList<DominionShopPileState> deserialize(JsonElement el, Type type, JsonDeserializationContext jsonContext)
        throws JsonParseException {

        //Interprets the JsonElement as a JSON array
        JsonArray jsonCards = ((JsonObject) el).getAsJsonArray(expansionSet);

        //Iterates over the JSON array, extracting card data to populate DominionCardState objects
        ArrayList<DominionShopPileState> cardPiles = new ArrayList<>(10);
        jsonCards.forEach(cards -> {
            JsonObject card = cards.getAsJsonObject();
            cardPiles.add(
                    new DominionShopPileState(
                            new DominionCardState(
                                    card.get("id").getAsInt(),
                                    card.getAsJsonObject().get("title").getAsString(),
                                    card.get("photoStringID").getAsString(),
                                    card.get("text").getAsString(),
                                    card.get("cost").getAsInt(),
                                    card.get("type").getAsString(),
                                    card.get("action").getAsString(),
                                    (card.has("addedTreasure")) ? card.get("addedTreasure").getAsInt() : 0,
                                    (card.has("addedActions")) ? card.get("addedActions").getAsInt() : 0,
                                    (card.has("addedDraw")) ? card.get("addedDraw").getAsInt() : 0,
                                    (card.has("addedBuys")) ? card.get("addedBuys").getAsInt() : 0,
                                    (card.has("victoryPoints")) ? card.get("victoryPoints").getAsInt() : 0
                            ),
                            card.get("amount").getAsInt(),
                            (card.has("isBaseCard")) ? DominionCardPlace.BASE_CARD : DominionCardPlace.SHOP_CARD
                    )
            );
        });

        return cardPiles;
    }

}
