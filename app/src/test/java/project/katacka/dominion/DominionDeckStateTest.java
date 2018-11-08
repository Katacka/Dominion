package project.katacka.dominion;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import project.katacka.dominion.GameStateUnitTest;
import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

public class DominionDeckStateTest {

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

    private void setupSpecialDiscard(DominionDeckState deck){
        ArrayList<DominionCardState> discard = deck.getDiscard();
        discard.add(baseCards.get(0).getCard()); //First card copper
        discard.add(baseCards.get(1).getCard()); //Second card Estate
        discard.add(shopCards.get(0).getCard()); //Third card Moat
        discard.add(shopCards.get(8).getCard()); //Forth card Council room
        discard.add(shopCards.get(9).getCard()); //Fifth card Money Lender
        discard.add(shopCards.get(3).getCard()); //Sixth card merchant
        discard.add(baseCards.get(2).getCard()); //Seventh card Silver
    }

    @Test
    public void reveal() {

    }

    @Test
    public void draw() {
    }

    @Test
    public void drawMultiple() {
    }

    @Test
    public void discardByIndex() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();

        assertFalse(deck.discard(deck.getHandSize()));
        assertFalse(deck.discard(-1));

        deck.getHand().clear();
        deck.getDiscard().clear();
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getHand().add(copper);
        deck.discard(0);
        assertEquals(0,deck.getHandSize());
        assertEquals(copper, deck.getDiscard().get(0));
    }

    @Test
    public void discardByCard() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();

        DominionCardState copper = baseCards.get(0).getCard();

        deck.getDiscard().clear();

        setupSpecialHand(deck);

        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        assertFalse(deck.discard(null));
        assertFalse(deck.discard("FOOO"));


        deck.discard("Copper");
        assertFalse(deck.getHand().contains(copper));
        assertTrue(deck.getDiscard().contains(copper));
    }

    @Test
    public void discardNew() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();

        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        assertEquals(0, deck.getDiscardSize());

        deck.discardNew(copper);
        assertEquals(1, deck.getDiscardSize());
        assertEquals(copper, deck.getDiscard().get(0));
    }

    @Test
    public void addManyToDiscard() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();

        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        deck.addManyToDiscard(copper, -1);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 0);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 5);

        boolean onlyCopper = deck.getDiscard().stream().allMatch(cards -> cards == copper);
        assertEquals(true, onlyCopper);

    }

    @Test
    public void discardAll() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);

        assert deck.getHandSize() > 0;
        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        deck.discardAll();

        assertEquals(0, deck.getHandSize());
        assertEquals(true, deck.getDiscard().containsAll(tempHand));
    }

    @Test
    public void discardAllEmpty() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        deck.getHand().clear();

        assert deck.getHandSize() > 0;
        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        deck.discardAll();

        assertEquals(0, deck.getHandSize());
        assertEquals(true, deck.getDiscard().containsAll(tempHand));
    }

    @Test
    public void reshuffle() {
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialDiscard(deck);

        ArrayList<DominionCardState> tempDiscard = new ArrayList<>(deck.getDiscard().size());
        for (DominionCardState card : deck.getDiscard()) {
            tempDiscard.add(card);
        }

        deck.getHand().clear();
        deck.reshuffle();

        assertEquals(0, deck.getDiscardSize());
        assertEquals( true, deck.getDraw().containsAll(tempDiscard));
    }

    @Test
    public void countVictory() {
    }
}