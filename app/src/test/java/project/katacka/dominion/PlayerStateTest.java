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
 *
 * TODO: Javadoc methods
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

    /**
     * Tests that player initializes with 0 turns played
     */
    @Test
    public void testConstructor(){
        DominionShopPileState copperPile = baseCards.get(COPPER);
        DominionShopPileState estatePile = baseCards.get(ESTATE);

        DominionPlayerState player = new DominionPlayerState(copperPile, estatePile.getCard());

        //test turns played
        assertEquals(0, player.getTurnsPlayed());
    }

    //NOTE TO GRADER:
    // The copy constructor is tested in a different class called CopyConstructorTest

    /**
     * Tests that populateStartingDeck puts 7 copper and 3 estates in player's discard
     * Tests that 7 copper is deducted from copper pile with 4 players
     */
    @Test
    public void testPopulateStartingDeck(){
        int copperCount = 0;
        int estateCount = 0;

        DominionPlayerState p = state.getDominionPlayer(currTurn);
        DominionDeckState deck = p.getDeck();

        //test for starting deck (filled with blank cards)
        assertEquals("discard", 0, deck.getDiscard().size());
        assertEquals("draw", 5, deck.getDraw().size());
        assertEquals("hand", 5, deck.getHand().size());
        assertEquals("inplay", 0, deck.getInPlay().size());

        //remove cards from deck b/c we're testing populateStartingDeck()
        deck.getDraw().clear();
        deck.getHand().clear();

        //make sure clear worked
        assertEquals("draw", 0, deck.getDraw().size());
        assertEquals("hand", 0, deck.getHand().size());

        //test amount in copper pile
            //default num players = 4
        assertEquals("num copper in pile", 60-4*7, copperPile.getAmount());

        //call populateStartingDeck
        p.populateStartingDeck(copperPile, estatePile.getCard());

        //test deck again
        assertEquals("discard", 10, deck.getDiscard().size());
        assertEquals("draw", 0, deck.getDraw().size());
        assertEquals("hand", 0, deck.getHand().size());
        assertEquals("inplay", 0, deck.getInPlay().size());

        //test that there is 7 copper and 3 estates
        ArrayList<DominionCardState> discard = p.getDeck().getDiscard();

        for(DominionCardState card : discard){
            if(card.getTitle().equals(estatePile.getCard().getTitle())){
                estateCount++;
            } else if(card.getTitle().equals(copperPile.getCard().getTitle())){
                copperCount++;
            }
        }

        assertEquals("copper count", 7, copperCount);
        assertEquals("estate count", 3, estateCount);

        //test amount in copper pile
        assertEquals("num copper in pile", 32-7, copperPile.getAmount());
    }

    /**
     * Tests that 7 copper is deducted from copper pile with 3 players
     */
    @Test
    public void testPopulateStartingDeck3Player(){
        makeState(3); //resets state w/ 3 players
        DominionPlayerState p = state.getDominionPlayer(currTurn);
        assertEquals("num copper in pile", 39, copperPile.getAmount());
        p.populateStartingDeck(copperPile, estatePile.getCard());
        assertEquals("num copper in pile", 39-7, copperPile.getAmount());
    }

    /**
     * Tests that 7 copper is deducted from copper pile with 2 players
     */
    @Test
    public void testPopulateStartingDeck2Player(){
        makeState(2); //resets state w/ 3 players
        DominionPlayerState p = state.getDominionPlayer(currTurn);
        assertEquals("num copper in pile", 46, copperPile.getAmount());
        p.populateStartingDeck(copperPile, estatePile.getCard());
        assertEquals("num copper in pile", 46-7, copperPile.getAmount());
    }

    @Test
    public void testEndTurn(){
        //tested at deck level
    }
}
