package project.katacka.dominion;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

//TODO: Javadoc
public class GameStateUnitTest {

    private static ArrayList<DominionShopPileState> baseCards;
    private static ArrayList<DominionShopPileState> shopCards;

    private ArrayList<DominionShopPileState> baseClone;
    private ArrayList<DominionShopPileState> shopClone;

    @BeforeClass
    public static void setupCards(){

        /**
         * External citation
         * Problem: Trying to access resources (card jsons) for unit testing.
         * Resource:
         *  https://stackoverflow.com/questions/29341744/android-studio-unit-testing-read-data-input-file#29488904
         * Solution:
         *  Updated gradle to add res directory for testing. Created this directory, copied in JSONS.
         *  Using class loader to get resources as stream.
         */

        CardReader reader = new CardReader("base");
        try (InputStream shopStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("shop_cards.json");
             InputStream baseStream = GameStateUnitTest.class.getClassLoader().getResourceAsStream("base_cards.json")){
            shopCards = reader.generateCards(shopStream, 10);
            baseCards = reader.generateCards(baseStream, 7);
        } catch (IOException e) {
            Log.e("Testing", "Error while generating card pile: ");
        }
    }

    @Before
    public void cloneCards(){
        baseClone = new ArrayList<>(baseCards.size());
        for (DominionShopPileState pile : baseCards){
            baseClone.add(new DominionShopPileState(pile));
        }

        shopClone = new ArrayList<>(shopCards.size());
        for (DominionShopPileState pile : shopCards){
            shopClone.add(new DominionShopPileState(pile));
        }
    }

    @Test
    public void testPlayCard(){
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        setupSpecialHand(state.getDominionPlayers()[0].getDeck());

        state.playCard(0, 0); //Plays a copper
        assertEquals("Treasure", 1, state.getTreasure());
        assertEquals("Actions", 1, state.getActions());
        assertEquals("Buys", 1, state.getBuys());
    }

    private void setupSpecialHand(DominionDeckState deck){
        ArrayList<DominionCardState> hand = deck.getHand();
        hand.set(0, baseCards.get(0).getCard()); //First card copper
        hand.set(1, baseCards.get(1).getCard()); //Second card Estate
        hand.set(2, shopCards.get(0).getCard()); //Third card Moat
        hand.set(3, shopCards.get(8).getCard()); //Forth card Council room
        hand.set(4, shopCards.get(9).getCard()); //Fifth card Money Lender
        hand.add(shopCards.get(3).getCard()); //Sixth card merchant
        hand.add(baseCards.get(2).getCard()); //Seventh card Silver
    }
}
