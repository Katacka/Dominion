package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import static org.junit.Assert.*;

import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;

//import static org.junit.Assert.assertEquals;

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

    @Test
    public void testState() throws IOException{

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

        //Make some moves in state
        int currPlayer = state.getCurrentTurn();
        state.playAllCards(currPlayer);
        state.buyCard(currPlayer, 0, true); //Buy some copper

        //Create a copy
        DominionGameState other = new DominionGameState(state, currPlayer);

        //This data is obfuscated, so we only test state.
        //Strictly speaking, we need to do this for every player, but one seems sufficient
        DominionDeckState stateDeck = state.getDominionPlayer(currPlayer).getDeck();
        DominionDeckState otherDeck = other.getDominionPlayer(currPlayer).getDeck();
        assertEquals("Draw size", stateDeck.getDrawSize(), otherDeck.getDrawSize());
        assertEquals("Discard size", stateDeck.getDiscardSize(), otherDeck.getDiscardSize());

        //Draw and discard are obfuscated, so they must be cleared before comparing
        clearHiddenContent(state);
        clearHiddenContent(other);
        assertEquals("Game State Copy", state, other);

        //Test state that is not a copy
        DominionGameState newState = GameStateGenerator.getNewState(4);
        clearHiddenContent(newState);
        assertNotEquals("Game State Not Copy", state, newState);
    }

    public void clearHiddenContent(DominionGameState state){
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
