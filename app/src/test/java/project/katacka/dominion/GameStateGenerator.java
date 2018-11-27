package project.katacka.dominion;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Generates new game states for testing purposes.
 *
 * @author Ryan Regier, Hayden Liao, Julian Donovan, Ashika Mulagada
 */
public class GameStateGenerator {

    //The base and shop cards, generated from the resource stream
    private static ArrayList<DominionShopPileState> shopCards, baseCards;

    /**
     * Reads the JSON files to load the cards.
     * Must be called before getting new game states.
     */
    public static void setupCards(){

        /*
         * External citation
         * Problem: Trying to access resources (card jsons) for unit testing.
         * Resource:
         *  https://stackoverflow.com/questions/29341744/android-studio-unit-testing-read-data-input-file#29488904
         * Solution:
         *  Updated gradle to add res directory for testing. Created this directory, copied in JSONS.
         *  Using class loader to get resources as stream.
         */

        CardReader reader = new CardReader("base");
        try (InputStream shopStream = GameStateGenerator.class.getClassLoader().getResourceAsStream("shop_cards.json");
             InputStream baseStream = GameStateGenerator.class.getClassLoader().getResourceAsStream("base_cards.json")){
            shopCards = reader.generateCards(shopStream, 10);
            baseCards = reader.generateCards(baseStream, 7);
        } catch (IOException e) {
            Log.e("Testing", "Error while generating card pile: ");
        }
    }

    /**
     * Creates and returns a new game state.
     * Must be called after calling setupCards, or it will fail.
     *
     * @param players The number of players in the game
     * @return A new game state
     */
    public static DominionGameState getNewState(int players){
        //Copy the card arrays, as they are passed by reference into the game state constructor
        ArrayList<DominionShopPileState> baseClone = new ArrayList<>(baseCards.size());
        for (DominionShopPileState pile : baseCards){
            baseClone.add(new DominionShopPileState(pile));
        }

        ArrayList<DominionShopPileState> shopClone = new ArrayList<>(shopCards.size());
        for (DominionShopPileState pile : shopCards){
            shopClone.add(new DominionShopPileState(pile));
        }

        return new DominionGameState(players, baseClone, shopClone);
    }
}
