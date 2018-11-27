import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.GameStateGenerator;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

public class DominionShopPileTest {
    private ArrayList<DominionShopPileState> baseCards;
    private DominionShopPileState copperPile;
    private DominionGameState state;

    @BeforeClass
    public static void setupCards(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void setUpState(){
        state = GameStateGenerator.getNewState(4);
        baseCards = state.getBaseCards();
        copperPile = baseCards.get(0);
    }

    @Test //Julian
    public void testSetAmount() {
        assertEquals(0, copperPile.setAmount(-1)); //Negative values should default to 0
        assertEquals(0, copperPile.setAmount(0)); //0 is an acceptable amount
        assertEquals(Integer.MAX_VALUE, copperPile.setAmount(Integer.MAX_VALUE)); //As is the max int value

        assertNotEquals(2, copperPile.setAmount(3));
    }

    @Test //Julian
    public void testRemoveCard() {
        int amount = copperPile.getAmount();
        assertEquals(--amount, copperPile.removeCard()); //Removing a card reduces the amount by one

        copperPile.setAmount(0);
        assertEquals(0, copperPile.removeCard()); //Provided amount will not be lowered to a non-zero number, in which case it defaults to 0
    }

    @Test //Julian
    public void removeAmount() {
        int amount = copperPile.getAmount();
        assertEquals(amount - 5, copperPile.removeAmount(5)); //Removing an amount of five reduces the amount by five
        assertEquals(0, copperPile.removeAmount(Integer.MAX_VALUE));  //Provided amount will not be lowered to a non-zero number, in which case it defaults to 0
    }
}
