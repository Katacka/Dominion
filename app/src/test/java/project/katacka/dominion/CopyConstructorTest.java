package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Tests the copy constructors for every class in the game state.
 *
 * @author Ryan Regier
 */
public class CopyConstructorTest {

    private DominionGameState state;

    @BeforeClass
    public static void setup(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void getState(){
        state = GameStateGenerator.getNewState(4);
    }

    /**
     * Tests DominionGameState
     */
    @Test
    public void testState(){

        /*
         * External Citation
         * Date: 11/19/18
         * Problem: Unmocked methods
         * Resource:
         *  http://tools.android.com/tech-docs/unit-testing-support#TOC-Method-...-not-mocked.- (linked in error message)
         * Solution: From first article: "We are aware that the default behavior is problematic when using classes like Log or TextUtils and will evaluate possible solutions in future releases."
         *              Since TextUtils is being used in toString, we cannot compare state strings.
         *              Instead, equals() and hashCode() methods were auto-generated, so .equals() works and we can compare easily.
         */

        //Make some moves in state, to change it from initial state
        int currPlayer = state.getCurrentTurn();
        state.playAllCards(currPlayer);
        state.buyCard(currPlayer, 0, DominionCardPlace.BASE_CARD); //Buy some copper

        //Create a copy
        DominionGameState other = new DominionGameState(state, currPlayer);

        //Draw, discard, and hands are obfuscated.
        //We clear all obfuscated lists then compare, which should then be accurate.
        //Tests to ensure the obfuscation is correct are handled in the testing of the deck copy constructor
        clearHiddenContent(state);
        clearHiddenContent(other);
        assertEquals("Game State Copy", state, other);

        //Test state that is not a copy
        DominionGameState newState = GameStateGenerator.getNewState(4);
        clearHiddenContent(newState);
        assertNotEquals("Game State Not Copy", state, newState);
    }

    /**
     * Tests DominionCardState
     */
    @Test
    public void testCard(){
        DominionCardState copper, moat, blank;
        copper = state.getBaseCards().get(0).getCard();
        moat = state.getShopCards().get(0).getCard();
        blank = DominionCardState.BLANK_CARD;

        assertNotEquals("Different cards", copper, moat);
        assertNotEquals("Not blank card", blank, moat);

        DominionCardState copyCopper = new DominionCardState(copper);
        DominionCardState copyBlank = new DominionCardState(blank);

        assertEquals("Copper copy", copper, copyCopper);
        assertEquals("Blank copy", blank, copyBlank);
    }

    /**
     * Tests DominionDeckState
     */
    @Test
    public void testDeck(){
        DominionCardState copper = state.getShopCards().get(0).getCard();
        DominionDeckState deck, shown, hidden, unequal;
        deck = state.getDominionPlayer(0).getDeck();

        //Make changes to  deck so that arrays are not empty
        deck.getDiscard().add(copper);
        deck.getDiscard().add(copper);
        deck.getInPlay().add(copper);
        shown = new DominionDeckState(deck, true);
        hidden = new DominionDeckState(deck, false);

        unequal = state.getDominionPlayer(1).getDeck();

        //Ensure decks are not the same - otherwise obfuscation did not occur
        assertNotEquals("Shown deck not same", deck, shown);
        assertNotEquals("Hidden deck not same", deck, hidden);

        //Check hands. This is the only difference between shown and hidden
        assertEquals("Shown hands same", deck.getHand(), shown.getHand());
        assertNotEquals("Hidden hands different", deck.getHand(), hidden.getHand());

        //Check in play
        assertEquals("Shown in play", deck.getInPlay(), shown.getInPlay());

        //Check sizes
        assertEquals("Shown draw size", deck.getDrawSize(), shown.getDrawSize());
        assertEquals("Shown discard size", deck.getDiscardSize(), shown.getDiscardSize());
        assertEquals("Hidden hand size", deck.getHandSize(), hidden.getHandSize());

        //Confirm the draw, discards, and hands are blank
        assertEquals("Shown blank draw", DominionCardState.BLANK_CARD, shown.getDraw().get(0));
        assertEquals("Shown blank discard", DominionCardState.BLANK_CARD, shown.getDiscard().get(0));
        assertEquals("Hidden blank hand", DominionCardState.BLANK_CARD, hidden.getHand().get(0));

        //Confirm the top card of the discard pile is the same
        assertEquals("Shown top discard", deck.getLastDiscard(), shown.getLastDiscard());
        assertEquals("Hidden top discard", deck.getLastDiscard(), hidden.getLastDiscard());

        //Make sure that shown and hidden are otherwise the same
        //This is why the above asserts are not repeated for hidden.
        shown.getHand().clear();
        hidden.getHand().clear();
        assertEquals("Shown and hidden", shown, hidden);

        //Makes sure different decks are not seen as equal
        assertNotEquals("Unequal", deck, unequal);
    }

    /**
     * Tests DominionPlayerState
     */
    @Test
    public void testPlayer(){
        DominionPlayerState actual, shown, hidden, unequal;
        actual = state.getDominionPlayer(0);
        shown = new DominionPlayerState(actual, true);
        hidden = new DominionPlayerState(actual, false);
        unequal = state.getDominionPlayer(1);

        //Check that obfuscation worked
        assertNotEquals("Shown not equal", actual, shown);
        assertNotEquals("Hidden not equal", actual, hidden);
        assertNotEquals("Shown not hidden", shown, hidden);

        //Helper variables
        DominionDeckState actualDeck, shownDeck, hiddenDeck;
        actualDeck = actual.getDeck();
        shownDeck = shown.getDeck();
        hiddenDeck = hidden.getDeck();

        //Check shown is equal when draw/discard is cleared.
        //Because hand is obfucated for hidden, it should still be unequal
        //The deck copying is checked in the test of the deck copy constructor
        actualDeck.getDraw().clear();
        actualDeck.getDiscard().clear();
        shownDeck.getDraw().clear();
        shownDeck.getDiscard().clear();
        hiddenDeck.getDraw().clear();
        hiddenDeck.getDiscard().clear();
        assertEquals("Shown equal", actual, shown);
        assertNotEquals("Hidden hides hand", actual, hidden);

        //Makes sure hidden is otherwise the same after clearing hand
        actualDeck.getHand().clear();
        hiddenDeck.getHand().clear();
        assertEquals("Hidden equal", actual, hidden);

        //Makes sure different players are not the same
        assertNotEquals("Unequal", actual, unequal);
    }

    /**
     * Tests DominionShopPileState
     */
    @Test
    public void testShopPile(){
        DominionShopPileState actual, copy, unequal;
        actual = state.getShopCards().get(0);
        copy = new DominionShopPileState(actual);
        unequal = state.getShopCards().get(1);

        assertEquals("Equal", actual, copy);
        assertNotEquals("Unequal", actual, unequal);
    }

    /**
     * Clears the arrays that have their cards obfuscated so equality can be checked.
     * Used for comparing copied game states
     * @param state The state to clear from. Modified in place.
     */
    private void clearHiddenContent(DominionGameState state){
        int currPlayer = state.getCurrentTurn();
        DominionPlayerState[] players = state.getDominionPlayers();
        for (int i = 0; i < players.length; i++){
            DominionDeckState deck = players[i].getDeck();
            if (currPlayer != i){
                deck.getHand().clear();
            }
            deck.getDiscard().clear();
            deck.getDraw().clear();
        }
    }

}
