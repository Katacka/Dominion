package project.katacka.dominion;

import android.widget.ArrayAdapter;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Array;
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
    private final int MOAT = 0;
    private final int COUNCIL = 8;
    private final int MONEY_LENDER = 9;
    private final int MERCHANT = 3;
    private final int SILVER = 2;

    DominionGameState state;
    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;
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
        shopCards = state.getShopCards();
        currTurn = state.getCurrentTurn();
    }


    @Test
    public void testConstructor(){
        int copperCount = 0;
        int estateCount = 0;
        DominionShopPileState copperPile = baseCards.get(COPPER);
        DominionShopPileState estatePile = baseCards.get(ESTATE);

        DominionPlayerState player = new DominionPlayerState("name", copperPile, estatePile.getCard());

        //test name
        assertEquals("name", player.getName());

        //test that there is 7 copper and 3 estates
        ArrayList<DominionCardState> draw = player.getDeck().getDraw();
        ArrayList<DominionCardState> hand = player.getDeck().getHand();

        for(DominionCardState card : hand){
            if(card.getTitle().equals(estatePile.getCard().getTitle())){
                estateCount++;
            } else if(card.getTitle().equals(copperPile.getCard().getTitle())){
                copperCount++;
            }
        }

        for(DominionCardState card : draw){
            if(card.getTitle().equals(estatePile.getCard().getTitle())){
                estateCount++;
            } else if(card.getTitle().equals(copperPile.getCard().getTitle())){
                copperCount++;
            }
        }
        assertEquals("copper count", 7, copperCount);
        assertEquals("estate count", 3, estateCount);

        //test turns played
        assertEquals(0, player.getTurnsPlayed());
    }

    @Test
    public void testPopulateStartingDeck(){


    }

    @Test
    public void testEndTurn(){


    }


}
