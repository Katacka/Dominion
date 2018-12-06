package project.katacka.dominion;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

/**
 * Tests methods in DominionGameState
 *
 * @author Ashika
 */
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
    private final int GARDEN = 4;
    private final int PROVINCE = 6;

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

    /**
     * Tests canMove method
     */
    @Test
    public void testCanMove(){
        int turn = state.getCurrentTurn();
        int notTurn;
        if (turn >= 1) notTurn = turn - 1;
        else notTurn = turn + 1;

        assertTrue(state.canMove(turn)); //current player should be able to move
        assertFalse(state.canMove(notTurn)); //any other player should not be able to move

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

    /**
     * Tests getPlayerScores method
     */
    @Test
    public void testGetPlayerScores(){
        int[] playerScores = state.getPlayerScores();
        for(int i = 0; i<state.getDominionPlayers().length; i++){
            assertEquals(3, playerScores[i]);
        }

        int turn = state.getCurrentTurn();

        state.getDominionPlayer(turn).getDeck().getDiscard().add(baseCards.get(ESTATE).getCard());

        int[] playerScoresNew = state.getPlayerScores();
        assertEquals(4, playerScoresNew[turn]);

        state.getDominionPlayer(turn).getDeck().addManyToDiscard(shopCards.get(GARDEN).getCard(), 2);

        playerScoresNew = state.getPlayerScores();
        assertEquals(6, playerScoresNew[turn]);
    }

    /**
     * Tests isLegalBuy method
     */
    @Test
    public void testIsLegalBuy() {
        int turn = state.getCurrentTurn();
        int notTurn;
        if (turn >= 1) notTurn = turn - 1;
        else notTurn = turn + 1;

        state.setTreasure(4);
        int treasures = state.getTreasure();
        assertEquals(4, treasures);

        //shop cards
        assertTrue(state.isLegalBuy(turn, GARDEN, DominionCardPlace.SHOP_CARD)); //valid card index, enough treasures
        assertTrue(state.isLegalBuy(turn, MONEY_LENDER, DominionCardPlace.SHOP_CARD)); //edge card index
        assertTrue(state.isLegalBuy(turn, MOAT, DominionCardPlace.SHOP_CARD)); //edge card index
        assertFalse(state.isLegalBuy(turn, 10, DominionCardPlace.SHOP_CARD)); //out of bounds index
        assertFalse(state.isLegalBuy(turn, 15, DominionCardPlace.SHOP_CARD)); //out of bounds index
        assertFalse(state.isLegalBuy(turn, -2, DominionCardPlace.SHOP_CARD)); //out of bounds index

        //base cards
        assertTrue(state.isLegalBuy(turn, ESTATE, DominionCardPlace.BASE_CARD)); //valid index
        assertFalse(state.isLegalBuy(turn, PROVINCE, DominionCardPlace.BASE_CARD)); //valid index, not enough treasure
        assertFalse(state.isLegalBuy(turn, 8, DominionCardPlace.BASE_CARD)); //invalid index

        assertFalse(state.isLegalBuy(notTurn, 3, DominionCardPlace.SHOP_CARD)); //not correct players turn
    }

    /**
     * Tests isLegalPlay method
     */
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

    /**
     * Tests playCard method
     */
    @Test
    public void testPlayCard(){
        int turn = state.getCurrentTurn();
        int notTurn;
        if(turn>=1) notTurn = turn-1;
        else notTurn = turn+1;

        DominionDeckState deck = state.getDominionPlayers()[turn].getDeck();
        setupSpecialHand(deck);

        assertEquals(1, state.getActions());
        assertTrue(state.playCard(turn, 0)); //valid play, playing first card in hand which is copper
        assertFalse(state.playCard(notTurn, 0)); //not valid play because wrong turn
        assertEquals(1, state.getActions()); //actions were not decremented

        assertTrue(deck.getHand().contains(shopCards.get(MOAT).getCard()));
        boolean playCard = state.playCard(turn, 1); //play moat
        assertTrue(playCard);
        assertFalse(deck.getHand().contains(shopCards.get(MOAT).getCard())); //test that hand no longer contains moat
        assertEquals(0, state.getActions()); //actions decremented

        boolean playAnotherCard = state.playCard(turn, 3); //try to play council room (action)
        assertFalse(playAnotherCard); //should not be able to play action because actions are at 0
    }

    /**
     * Tests playALlCards method
     */
    @Test
    public void testPlayAllCards(){
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

    /**
     * Tests endTurn method
     */
    @Test
    public void testEndTurn(){
        int turn = state.getCurrentTurn();
        boolean endTurn = state.endTurn(turn);
        assertTrue(endTurn);
        assertNotEquals(turn, state.getCurrentTurn());
        assertEquals(state.getCurrentTurn(), (turn + 1) % state.getDominionPlayers().length);
    }

    /**
     * Tests buyCard method
     */
    @Test
    public void testBuyCard(){
        int turn = state.getCurrentTurn();

        assertEquals(0, state.getTreasure());
        state.setTreasure(2);
        int treasures = state.getTreasure();
        assertEquals(2, treasures);

        int discardSize = state.getDominionPlayer(turn).getDeck().getDiscardSize();
        int buys = state.getBuys();
        assertEquals(1, buys);
        boolean buyCard = state.buyCard(turn, MOAT, DominionCardPlace.SHOP_CARD);
        assertTrue(buyCard);

        assertEquals(discardSize+1, state.getDominionPlayer(turn).getDeck().getDiscardSize()); //one was added to discard
        assertEquals(state.getShopCards().get(MOAT).getCard(), state.getDominionPlayer(turn).getDeck().getLastDiscard()); //check that the last discarded is a moat
        assertEquals(buys-1, state.getBuys()); //buys decreased
        assertEquals(treasures-shopCards.get(MOAT).getCard().getCost() , state.getTreasure()); //treasures decreased by the cost of the card

        //should not be able to buy merchant because no more buys left
        boolean buyAnotherCard = state.buyCard(turn, MERCHANT, DominionCardPlace.SHOP_CARD);
        assertFalse(buyAnotherCard);
        assertEquals(state.getShopCards().get(MOAT).getCard(), state.getDominionPlayer(turn).getDeck().getLastDiscard()); //check that the last discarded is still a moat
        assertEquals(buys-1, state.getBuys()); //test that buys have still only decremented 1
    }

    /**
     * Tests getWinner method
     */
    @Test
    public void testGetWinner() {
        int[] playerScores = state.getPlayerScores();
        for(int i = 0; i<state.getDominionPlayers().length; i++){
            assertEquals(3, playerScores[i]); //all players tied
        }

        int winner = state.getWinner();
        assertEquals(-1, winner); //all players tied so should return -1 for no winner

        int turn = state.getCurrentTurn();

        state.getDominionPlayer(turn).getDeck().getDiscard().add(baseCards.get(5).getCard()); //add province to current player

        playerScores = state.getPlayerScores();
        assertEquals(9, playerScores[turn]);

        int newWinner = state.getWinner();
        assertEquals(turn, newWinner); //player who bought another province should win

        int notTurn;
        if(turn>=1) notTurn = turn-1;
        else notTurn = turn+1;

        assertEquals(3, playerScores[notTurn]);
        state.getDominionPlayer(notTurn).getDeck().getDiscard().add(baseCards.get(5).getCard());
        state.getDominionPlayer(notTurn).getDeck().getDiscard().add(baseCards.get(5).getCard()); //add two province to next player

        playerScores = state.getPlayerScores();
        assertEquals(15, playerScores[notTurn]); //score should be 15

        newWinner = state.getWinner();
        assertEquals(notTurn, newWinner); //the new players should now be the winner
    }

    /**
     * Ryan Regier (only one in file not Ashika)
     * Test of getTiedPlayers().
     * Tests it returns null when no tie or when getWinner() has not been called.
     * Tests tiebreaker is handled successfully.
     * Tests function returns correct result
     */
    @Test
    public void testTies(){
        //null if getPlayerScores not called
        int[] tiedPlayers = state.getTiedPlayers();
        assertNull(tiedPlayers);

        //Still null before getWinner called
        int[] scores = state.getPlayerScores();
        tiedPlayers = state.getTiedPlayers();
        assertNull(tiedPlayers);

        DominionPlayerState firstPlayer = state.getDominionPlayer(0);
        DominionPlayerState secondPlayer = state.getDominionPlayer(1);
        DominionDeckState first  = firstPlayer.getDeck();
        DominionDeckState second = secondPlayer.getDeck();
        DominionCardState providence = baseCards.get(5).getCard();

        //Still null if not tie
        first.discardNew(providence);
        int winner = state.getWinner();
        tiedPlayers = state.getTiedPlayers();
        assertNull(tiedPlayers);

        //Tiebreaker: played more turns
        second.discardNew(providence);
        //Make sure player 0 has played more turns
        firstPlayer.startTurn(); //Start turn increments turns played
        firstPlayer.startTurn(); //Called twice in case player 2 started
        winner = state.getWinner();
        assertEquals(1, winner);
        tiedPlayers = state.getTiedPlayers();
        assertNull(tiedPlayers);

        //Two player tie (of 4)
        //Make sure they have played the same number of turns
        while (secondPlayer.getTurnsPlayed() < firstPlayer.getTurnsPlayed()){
            secondPlayer.startTurn();
        }
        winner = state.getWinner();
        assertEquals(-1, winner);
        tiedPlayers = state.getTiedPlayers();
        int[] expected = {0, 1};
        assertArrayEquals(expected, tiedPlayers);
    }

    /**
     * Tests quitGame method
     */
    @Test
    public void testQuitGame(){
        int turn = state.getCurrentTurn();
        boolean quitGame = state.quitGame(turn);
        assertTrue(quitGame); //quit game returns true as long as game is not over

        boolean quitGameAgain = state.quitGame(turn);
        assertFalse(quitGameAgain); //quit game should return false because game is already over
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


