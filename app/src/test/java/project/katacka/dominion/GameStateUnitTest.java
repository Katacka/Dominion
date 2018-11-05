package project.katacka.dominion;

import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class GameStateUnitTest {

    private static ArrayList<DominionShopPileState> baseCards;
    private static ArrayList<DominionShopPileState> shopCards;

    private ArrayList<DominionShopPileState> baseClone;
    private ArrayList<DominionShopPileState> shopClone;

    @BeforeClass
    public static void setupCards(){

        CardReader reader = new CardReader("base");

        baseCards = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            baseCards.add(new DominionShopPileState(DominionCardState.BLANK_CARD, 10));
        }

        shopCards = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            shopCards.add(new DominionShopPileState(DominionCardState.BLANK_CARD, 10));
        }
    }

    @Before
    public void cloneCards(){
        baseClone = new ArrayList<>(baseCards.size());
        for (DominionShopPileState pile : baseCards){
            baseClone.add(new DominionShopPileState(pile));
        }

        shopClone = new ArrayList<>(shopCards.size());
        for (DominionShopPileState pile : shopCards){
            shopClone.add(new DominionShopPileState(pile));
        }
    }

    @Test
    public void testPlayCard(){
        DominionGameState state = new DominionGameState(4, baseClone, shopClone);
        DominionPlayerState player = state.getDominionPlayers()[0];
        state.testSpecialHand();

        DominionCardState copper = baseClone.get(0).getCard();
        System.out.println(copper.toString());

        state.playCard(0, 0); //Plays a copper
        assertEquals(1, state.getTreasure());
        assertEquals(1, state.getActions());
        assertEquals(1, state.getBuys());


    }
}
