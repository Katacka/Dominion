package project.katacka.dominion;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class GameStateUnitTest {

    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;

    @Before
    public void setupCards(){
        baseCards = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            baseCards.add(new DominionShopPileState(DominionCardState.BLANK_CARD, 10));
        }

        shopCards = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            shopCards.add(new DominionShopPileState(DominionCardState.BLANK_CARD, 10));
        }
    }

    @Test
    public void testPlayCard(){
        DominionGameState state = new DominionGameState(4, baseCards, shopCards);

    }
}
