package project.katacka.dominion;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

public class GameStateTest {

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

    @Before
    public void setUpState(){
        state = GameStateGenerator.getNewState(4);
        baseCards = state.getBaseCards();
        shopCards = state.getShopCards();
    }

    @Test
    public void testCanMove(){
        int turn = state.getCurrentTurn();
        if(turn == 0){
            assertFalse(state.canMove(1));
            assertFalse(state.canMove(3));
            assertTrue(state.canMove(0));
        }
        else if(turn == 1){
            assertFalse(state.canMove(0));
            assertTrue(state.canMove(1));
        }
        else if(turn == 2){
            assertFalse(state.canMove(0));
            assertTrue(state.canMove(2));
        }
        else if(turn == 3){
            assertFalse(state.canMove(1));
            assertTrue(state.canMove(3));
        }
    }

    @Test
    public void testGetPlayerScores(){
        int[] playerScores = state.getPlayerScores();
        for(int i = 0; i<state.getDominionPlayers().length; i++){
            assertEquals(3, playerScores[i]);
        }

        //TODO: This is a redundant test, as this player is already checked in the for loop above.
        int turn = state.getCurrentTurn();
        assertEquals(3, playerScores[turn]);

        //TODO: Just add VP cards directly to the deck, since that's a bit cleaner. (deck.getDraw.add(baseCards.get(1).getCard() or something like that)
        state.playAllCards(turn);
        boolean boughtCard = state.buyCard(turn, 1, true); //TODO: If this fails after merging, replace true with DominionCardPlace.BASE_CARD
        assertTrue(boughtCard);

        int[] playerScoresNew = state.getPlayerScores();
        assertNotEquals(3, playerScoresNew[turn]); //TODO: Calculate the new value and test equality instead. Should be 4

        //TODO: Add a gardens and make sure it works correctly (might want a few tests depending on the number of cards)
    }

    @Test
    public void testIsLegalBuy() {
        int turn = state.getCurrentTurn();
        int notTurn;
        if (turn >= 1) notTurn = turn - 1;
        else notTurn = turn + 1;

        //TODO: This is inconsistent and random, should modify
        //Options: Add and use a setter for the number of treasures in state (recommended)
        //          Hack the player's hand to ensure set number of treasures
        //          Have a treasure card activate itself by calling cardAction()
        boolean playAll = state.playAllCards(turn);
        assertTrue(playAll);
        int treasures = state.getTreasure();
        assertNotEquals(0, treasures);

        //TODO: Again, these will fail after merge, because I replaced baseCard with place. Generally, replace
        //      false with DominionCardPlace.SHOP_CARD and true with DominionCardPlace.BASE_CARD.
        //TODO: Give card indexies names so its more obvious what cards you are trying to buy.
        if(treasures>=4){
            assertTrue(state.isLegalBuy(turn, 4, false)); //valid card index
            assertTrue(state.isLegalBuy(turn, 9, false)); //edge card index
        }
        assertTrue(state.isLegalBuy(turn, 0, false)); //edge card index
        assertFalse(state.isLegalBuy(turn, 10, false)); //out of bounds index
        assertFalse(state.isLegalBuy(turn, 15, false)); //out of bounds index
        //TODO: Did you want to test negatives?

        //TODO: Why is one true and one false? What are you testing here?
        assertTrue(state.isLegalBuy(turn, 1, true));
        assertFalse(state.isLegalBuy(turn, 8, true));

        assertFalse(state.isLegalBuy(notTurn, 3, false));
    }

    @Test
    public void testIsLegalPlay(){
        int turn = state.getCurrentTurn();
        int notTurn;
        if(turn>=1) notTurn = turn-1;
        else notTurn = turn+1;

        assertEquals(1, state.getActions());

        DominionDeckState deck = state.getDominionPlayer(state.getCurrentTurn()).getDeck();
        int deckSize = deck.getHandSize();

        assertTrue(state.isLegalPlay(turn, 0)); //valid card index edge
        assertTrue(state.isLegalPlay(turn, deckSize-2)); //valid card index
        assertTrue(state.isLegalPlay(turn, deckSize-1)); //valid card index edge
        assertFalse(state.isLegalPlay(turn, deckSize + 2)); //invalid card index
        assertFalse(state.isLegalPlay(notTurn, 0)); //not right player
    }

    @Test
    public void testPlayCard(){
        int turn = state.getCurrentTurn();
        int notTurn;
        if(turn>=1) notTurn = turn-1;
        else notTurn = turn+1;

        DominionDeckState deck = state.getDominionPlayers()[turn].getDeck();
        setupSpecialHand(deck);

        //TODO: What card did you play here? Clear up with comments or constants
        assertTrue(state.playCard(turn, 0)); //valid play
        assertFalse(state.playCard(notTurn, 0)); //not valid play
        assertEquals(1, state.getActions());

        assertTrue(deck.getHand().contains(shopCards.get(MOAT).getCard()));
        boolean playCard = state.playCard(turn, 2); //play moat
        assertTrue(playCard);
        //TODO: Why is the below test commented out?
        //assertFalse(deck.getHand().contains(shopCards.get(MOAT).getCard())); //test that hand no longer contains council room
        //TODO: Might be worth testing if card played is now in play (see deck class).
        //TODO: Test illegal play
        //TODO: Test actions decremented for action card, not for non-action card (can add to other tests you have)
    }

    @Test
    public void testPlayAllCards(){
       /*setupCards();
       setUpState();

       int turn = state.getCurrentTurn();

       assertEquals(5, state.getDominionPlayers()[turn].getDeck().getHandSize());

       assertFalse(state.playAllCards(notTurn));
       boolean playAll = state.playAllCards(turn);
       assertTrue(playAll);
       assertEquals(0, state.getDominionPlayers()[turn].getDeck().getHandSize());*/

        int turn = state.getCurrentTurn();

        DominionDeckState deck = state.getDominionPlayers()[turn].getDeck();
        setupSpecialHand(deck);

        assertEquals(7, deck.getHandSize());
        boolean playAll = state.playAllCards(turn);
        assertTrue(playAll);
        assertEquals(4, deck.getHandSize());
        assertFalse(deck.getHand().contains(baseCards.get(COPPER).getCard()));
        assertTrue(deck.getHand().contains(shopCards.get(MOAT).getCard()));
    }

    @Test
    public void testEndTurn(){
        int turn = state.getCurrentTurn();
        boolean endTurn = state.endTurn(turn);
        assertTrue(endTurn);
        assertNotEquals(turn, state.getCurrentTurn());
        assertEquals(state.getCurrentTurn(), (turn + 1) % state.getDominionPlayers().length);
    }

    @Test
    public void testBuyCard(){
        int turn = state.getCurrentTurn();

        assertEquals(0, state.getTreasure());
        //TODO: Again, find way to gain treasure more consistently
        boolean playAll = state.playAllCards(turn);
        assertTrue(playAll);
        int treasures = state.getTreasure();
        assertNotEquals(0, treasures);

        if(state.getTreasure() >= 2){
            int discardSize = state.getDominionPlayer(turn).getDeck().getDiscardSize();
            int buys = state.getBuys();
            assertEquals(1, buys);
            boolean buyCard = state.buyCard(turn, MOAT, false);
            assertTrue(buyCard);

            assertEquals(discardSize+1, state.getDominionPlayer(turn).getDeck().getDiscardSize());
            assertTrue(state.getDominionPlayer(turn).getDeck().getDiscard().contains(shopCards.get(MOAT).getCard())); //TODO: Use get last discard function
            assertEquals(buys-1, state.getBuys());
            assertEquals(treasures-shopCards.get(MOAT).getCard().getCost() , state.getTreasure());

            //TODO: Make sure none of that happens when illegal buy attempted
        }

    }

    @Test
    public void testGetWinner() {
        setupCards();
        setUpState();

        int[] playerScores = state.getPlayerScores();
        for(int i = 0; i<state.getDominionPlayers().length; i++){
            assertEquals(3, playerScores[i]); //all players tied
        }

        int winner = state.getWinner();
        assertEquals(-1, winner); //all players tied so should return -1 for no winner

        int turn = state.getCurrentTurn();
        assertEquals(3, playerScores[turn]);

        state.playAllCards(turn);
        state.buyCard(turn, ESTATE, true);
        state.endTurn(turn); //one player gets more victory points

        playerScores = state.getPlayerScores();
        assertNotEquals(3, playerScores[turn]);

        int newWinner = state.getWinner();
        assertEquals(turn, newWinner); //player who bought another estate should win

        int notTurn = state.getCurrentTurn();
        if(notTurn == turn) state.endTurn(notTurn);
        notTurn = state.getCurrentTurn();

        assertEquals(3, playerScores[notTurn]);
        state.playAllCards(notTurn);
        state.buyCard(notTurn, ESTATE, true);
        state.endTurn(notTurn); //another player gets more victory points

        playerScores = state.getPlayerScores();
        assertNotEquals(3, playerScores[notTurn]);

        newWinner = state.getWinner();
        assertEquals(-1, newWinner); //the two players should now be tied with equal amount of turns
    }

    /**
     * quitGame
     */

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


