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
    private DominionGameState state;
    private DominionDeckState deck;

    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;

    private ArrayList<DominionShopPileState> baseClone;
    private ArrayList<DominionShopPileState> shopClone;

    private final int COPPER = 0;
    private final int ESTATE = 1;
    private final int MOAT = 0;
    private final int COUNCIL = 8;
    private final int MONEY_LENDER = 9;
    private final int COUNCIL_ROOM = 8;
    private final int MERCHANT = 3;
    private final int SILVER = 2;
    private final int GARDEN = 4;
    private final int PROVINCE = 6;

    @BeforeClass
    public static void setupCards(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void setup(){ //Sets up state, deck, shopCards and baseCards for future use
        state = GameStateGenerator.getNewState(4);
        baseCards = state.getBaseCards();
        shopCards = state.getShopCards();
        deck = state.getDominionPlayers()[0].getDeck();
    }

    public void cloneCards(){ //Clones baseCards and shopCards for comparison purposes
        baseClone = new ArrayList<>(baseCards.size());
        for (DominionShopPileState pile : baseCards){
            baseClone.add(new DominionShopPileState(pile));
        }

        shopClone = new ArrayList<>(shopCards.size());
        for (DominionShopPileState pile : shopCards){
            shopClone.add(new DominionShopPileState(pile));
        }
    }

    private void setupSpecialCardList(ArrayList<DominionCardState> cardList){
        //Fills the supplied cardList with specifically defined cards
        cardList.clear();
        cardList.add(baseCards.get(COPPER).getCard()); //First card copper
        cardList.add(baseCards.get(ESTATE).getCard()); //Second card Estate
        cardList.add(shopCards.get(MOAT).getCard()); //Third card Moat
        cardList.add(shopCards.get(COUNCIL_ROOM).getCard()); //Forth card Council room
        cardList.add(shopCards.get(MONEY_LENDER).getCard()); //Fifth card Money Lender
        cardList.add(shopCards.get(MERCHANT).getCard()); //Sixth card merchant
        cardList.add(baseCards.get(SILVER).getCard()); //Seventh card Silver
    }

    @Test //Julian Donovan
    public void reveal() {
        deck.getDraw().clear();
        deck.getDiscard().clear();
        assertNull(deck.reveal()); //When both the deck and discard are empty reveal is expected to return NULL

        setupSpecialCardList(deck.getDiscard()); //Populates the discard
        DominionCardState topCard = new DominionCardState(deck.getDraw().get(deck.getDrawSize()));
        assertEquals(topCard, deck.reveal()); //The top card should be a silver as set by setupSpecialCardList

        setupSpecialCardList(deck.getDraw());
        DominionCardState silver = baseCards.get(SILVER).getCard();
        assertEquals(silver, deck.reveal()); //The top card should be a silver as set by setupSpecialCardList
    }

    @Test //Julian Donovan
    public void draw() {
        deck.getDraw().clear();
        deck.getDiscard().clear();
        assertNull(deck.draw()); //When both the deck and discard are empty draw is expected to return NULL (as reveal should return null)

        setupSpecialCardList(deck.getDiscard()); //Populates the discard
        DominionCardState topCard = new DominionCardState(deck.getDraw().get(deck.getDrawSize()));
        assertEquals(topCard, deck.draw()); //The drawn card should be a silver as set by setupSpecialCardList
        assertFalse(deck.getDraw().contains(topCard)); //The drawn card should have been removed from the player's draw
        int lastHandIdx = deck.getHandSize() - 1;
        assertEquals(topCard, deck.getHand().get(lastHandIdx)); //The drawn card hsould be added to the player's hand

        setupSpecialCardList(deck.getDraw());
        DominionCardState silver = baseCards.get(SILVER).getCard();
        assertEquals(silver, deck.draw()); //The drawn card should be a silver as set by setupSpecialCardList
        assertFalse(deck.getDraw().contains(silver)); //The drawn card should have been removed from the player's draw
        lastHandIdx = deck.getHandSize() - 1;
        assertEquals(silver, deck.getHand().get(lastHandIdx)); //The drawn card hsould be added to the player's hand
    }

    @Test
    public void drawMultiple() {
    }

    @Test
    public void putInPlay() {

    }

    @Test
    public void getLastPlayed() {

    }

    @Test //Julian Donovan
    public void discardByIndex() {
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

    @Test //Hayden Liao
    public void discardByCard() {
        DominionCardState copper = baseCards.get(0).getCard();

        deck.getDiscard().clear();

        setupSpecialCardList(deck.getHand());

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

    @Test //Julian Donovan
    public void discardNew() {
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        assertEquals(0, deck.getDiscardSize());

        deck.discardNew(copper);
        assertEquals(1, deck.getDiscardSize());
        assertEquals(copper, deck.getDiscard().get(0));
    }

    @Test //Julian Donovan
    public void addManyToDiscard() {
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        deck.addManyToDiscard(copper, -1);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 0);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 5);

        boolean onlyCopper = deck.getDiscard().stream().allMatch(cards -> cards.getTitle().equals(copper.getTitle()));
        assertTrue(onlyCopper);

    }

    @Test //Hayden Liao
    public void discardAll() {
        setupSpecialCardList(deck.getHand());

        assert deck.getHandSize() > 0;
        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        deck.discardAll();

        assertEquals(0, deck.getHandSize());
        assertEquals(true, deck.getDiscard().containsAll(tempHand));
    }

    @Test //Hayden Liao
    public void discardAllEmpty() {
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

    @Test //Hayden Liao
    public void reshuffle() {
        setupSpecialCardList(deck.getDiscard());

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

    @Test
    public void createCardArray() {

    }

    @Test
    public void equals() {

    }
}