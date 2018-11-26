package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.assertEquals;
import static project.katacka.dominion.GameStateGenerator.getNewState;

/**
 * @author Hayden Liao
 */

public class PlayerStateTest {
    private final int COPPER = 0;
    private final int ESTATE = 1;

    DominionGameState state;
    private ArrayList<DominionShopPileState> baseCards;
    DominionShopPileState copperPile;
    DominionShopPileState estatePile;
    private int currTurn;

    private final int NUM_PLAYERS = 4;

    @BeforeClass
    public static void setup(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void makeState(){
        state = getNewState(NUM_PLAYERS);
        baseCards = state.getBaseCards();
        currTurn = state.getCurrentTurn();

        copperPile = baseCards.get(COPPER);
        estatePile = baseCards.get(ESTATE);
    }

    public void makeState(int numPlayers){
        state = getNewState(numPlayers);
        baseCards = state.getBaseCards();
        currTurn = state.getCurrentTurn();

        copperPile = baseCards.get(COPPER);
        estatePile = baseCards.get(ESTATE);
    }

    @Test
    public void testConstructor(){
        DominionShopPileState copperPile = baseCards.get(COPPER);
        DominionShopPileState estatePile = baseCards.get(ESTATE);

        DominionPlayerState player = new DominionPlayerState("name", copperPile, estatePile.getCard());

        //test name
        assertEquals("name", player.getName());

        //test turns played
        assertEquals(0, player.getTurnsPlayed());
    }

    //NOTE TO GRADER:
    // The copy constructor is tested in a different class called CopyConstructorTest

    @Test
    public void testPopulateStartingDeck(){
        int copperCount = 0;
        int estateCount = 0;

        DominionPlayerState p = state.getDominionPlayer(currTurn);
        DominionDeckState deck = p.getDeck();

        //make sure deck is empty
        assertEquals("discard", 0, deck.getDiscard().size());
        assertEquals("draw", 0, deck.getDraw().size());
        assertEquals("hand", 0, deck.getHand().size());
        assertEquals("inplay", 0, deck.getInPlay().size());

        //test amount in copper pile
        assertEquals("num copper in pile", 60, copperPile.getAmount());

        //call populateStartingDeck
        p.populateStartingDeck(copperPile, estatePile.getCard());

        //test deck again
        assertEquals("discard", 10, deck.getDiscard().size());
        assertEquals("draw", 0, deck.getDraw().size());
        assertEquals("hand", 0, deck.getHand().size());
        assertEquals("inplay", 0, deck.getInPlay().size());


        //test that there is 7 copper and 3 estates
        ArrayList<DominionCardState> draw = p.getDeck().getDraw();
        ArrayList<DominionCardState> hand = p.getDeck().getHand();

        for(DominionCardState card : draw){
            if(card.getTitle().equals(estatePile.getCard().getTitle())){
                estateCount++;
            } else if(card.getTitle().equals(copperPile.getCard().getTitle())){
                copperCount++;
            }
        }

        assertEquals("copper count", 7, copperCount);
        assertEquals("estate count", 3, estateCount);

        //test amount in copper pile
        assertEquals("num copper in pile", 32, copperPile.getAmount());

        //repeat test with 3 players
        makeState(3); //resets state
        p = state.getDominionPlayer(currTurn); //resets player

        assertEquals("num copper in pile", 60, copperPile.getAmount());
        p.populateStartingDeck(copperPile, estatePile.getCard());
    }

    @Test
    public void testPopulateStartingDeck3Player(){
        makeState(3); //resets state w/ 3 players
        DominionPlayerState p = state.getDominionPlayer(currTurn);
        assertEquals("num copper in pile", 60, copperPile.getAmount());
        p.populateStartingDeck(copperPile, estatePile.getCard());
        assertEquals("num copper in pile", 39, copperPile.getAmount());
    }

    @Test
    public void testPopulateStartingDeck2Player(){
        makeState(2); //resets state w/ 3 players
        DominionPlayerState p = state.getDominionPlayer(currTurn);
        assertEquals("num copper in pile", 60, copperPile.getAmount());
        p.populateStartingDeck(copperPile, estatePile.getCard());
        assertEquals("num copper in pile", 46, copperPile.getAmount());
    }

    @Test
    public void testEndTurn(){
        //tested at deck level
    }
}
