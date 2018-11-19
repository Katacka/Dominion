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
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

//TODO: Javadoc
public class GameStateUnitTest {

    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;
    private DominionGameState state;

    private final int COPPER = 0;
    private final int ESTATE = 1;
    private final int MOAT = 0;
    private final int COUNCIL = 8;
    private final int MONEY_LENDER = 9;
    private final int MERCHANT = 3;
    private final int SILVER = 2;

    @BeforeClass
    public static void setupCards(){
        GameStateGenerator.setupCards();
    }

    private void setupState(int players){
        state = GameStateGenerator.getNewState(players);
        baseCards = state.getBaseCards();
        shopCards = state.getShopCards();
    }

    //TODO: fix assumption that player 0 goes first
    @Test
    public void testCopper(){
        setupState(4);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);

        state.playCard(0, 0); //Plays a copper
        assertEquals("Treasure", 1, state.getTreasure());
        assertEquals("Actions", 1, state.getActions());
        assertEquals("Buys", 1, state.getBuys());

        assertEquals(deck.getHand().get(0), baseCards.get(ESTATE).getCard());
        assertEquals(deck.getLastDiscard(), baseCards.get(COPPER).getCard());

        assertEquals(deck.getHandSize(), 6);
    }

    @Test
    //can't play moneylender with no copper
    //new version: can play moneylender with no copper
    public void testMoneyLenderNoCopper(){
        setupState(4);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);

        state.playCard(0, 0); //Plays a copper, you have no copper now

        assertEquals(shopCards.get(MONEY_LENDER).getCard(), deck.getHand().get(3));//money lender is 3rd card

        boolean playedCard = state.playCard(0, 3); //try to player Money Lender

        assertFalse(playedCard); //make sure you haven't played the card

        //make sure you still have moneylender in hand
        assertEquals(shopCards.get(MONEY_LENDER).getCard(), deck.getHand().get(3));

        assertEquals(1, state.getActions()); //still have an action
    }

    @Test
    public void testMoneyLenderWithOneCopper(){
        setupState(4);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);

        assertEquals(shopCards.get(MONEY_LENDER).getCard(), deck.getHand().get(4));//money lender is 4th card
        assertEquals(baseCards.get(COPPER).getCard(), deck.getHand().get(0));//copper is 1st card
        assertEquals(0, state.getTreasure()); //have 0 treasure
        assertEquals(1, state.getActions()); //have 1 action

        boolean playedCard = state.playCard(0, 4); //try to player Money Lender
        assertTrue(playedCard); //make sure you played the card

        //make sure money lender and copper are NOT in hand
        assertFalse(deck.getHand().contains(baseCards.get(COPPER).getCard()));
        assertFalse(deck.getHand().contains(shopCards.get(MONEY_LENDER).getCard()));

        assertEquals(0, state.getActions()); //still have an action
        assertEquals(3, state.getTreasure()); //have 3 treasure now
    }

    @Test
    public void testMoneyLenderWithManyCopper(){
        setupState(4);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);
        ArrayList<DominionCardState> hand = deck.getHand();

        hand.add(baseCards.get(COPPER).getCard()); //Add a copper as 8th card
        hand.add(baseCards.get(COPPER).getCard()); //and 9th card

        assertEquals(baseCards.get(COPPER).getCard(), hand.get(7));//8th card is a copper
        assertEquals(shopCards.get(MONEY_LENDER).getCard(), hand.get(4));//money lender is 4th card
        assertEquals(baseCards.get(COPPER).getCard(), hand.get(0));//copper is 1st card

        assertEquals(0, state.getTreasure()); //have 0 treasure
        assertEquals(1, state.getActions()); //have 1 action

        boolean playedCard = state.playCard(0, 4); //try to player Money Lender
        assertTrue(playedCard); //make sure you played the card

        //make sure money lender is NOT in hand
        assertFalse(hand.contains(shopCards.get(MONEY_LENDER).getCard()));

        //make sure there is still 2 coppers
        assertTrue(hand.contains(baseCards.get(COPPER).getCard()));
        hand.remove(baseCards.get(COPPER).getCard());
        //still 1 copper
        assertTrue(hand.contains(baseCards.get(COPPER).getCard()));

        //remove another one
        hand.remove(baseCards.get(COPPER).getCard());
        assertFalse(hand.contains(baseCards.get(COPPER).getCard()));

        assertEquals(0, state.getActions()); //still have an action
        assertEquals(3, state.getTreasure()); //have 3 treasure now
    }

    @Test
    public void testIsTurn(){
        setupState(4);

        boolean playedCard1 = state.playCard(1, 1);
        assertFalse(playedCard1);

        boolean playedCard2 = state.playCard(2, 2);
        assertFalse(playedCard2);

        boolean playedCard3 = state.playCard(3, 3);
        assertFalse(playedCard3);

        boolean playedCard0 = state.playCard(0, 3);
        assertTrue(playedCard0);
    }

    @Test
    public void testNoActions(){
        setupState(4);
        DominionDeckState deck = state.getDominionPlayers()[0].getDeck();
        setupSpecialHand(deck);
        ArrayList<DominionCardState> hand = deck.getHand();

        assertEquals(1, state.getActions());
        assertTrue(state.playCard(0, 2)); //play moat

        //should have no actions left
        assertEquals(0, state.getActions());

        //make sure card3 is action type
        assertEquals(hand.get(3).getType(), DominionCardType.ACTION);

        //try to play card3
        assertFalse(state.playCard(0, 3)); //play Money Lender

        assertTrue(hand.contains(shopCards.get(MONEY_LENDER).getCard()));
    }

    private void setupSpecialHand(DominionDeckState deck) {
        ArrayList<DominionCardState> hand = deck.getHand();
        hand.set(0, baseCards.get(COPPER).getCard()); //First card copper
        hand.set(1, baseCards.get(ESTATE).getCard()); //Second card Estate
        hand.set(2, shopCards.get(MOAT).getCard()); //Third card Moat
        hand.set(3, shopCards.get(COUNCIL).getCard()); //Forth card Council room
        hand.set(4, shopCards.get(MONEY_LENDER).getCard()); //Fifth card Money Lender
        hand.add(shopCards.get(MERCHANT).getCard()); //Sixth card merchant
        hand.add(baseCards.get(SILVER).getCard()); //Seventh card Silver
    }
}
