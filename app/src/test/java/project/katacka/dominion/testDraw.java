package project.katacka.dominion;


import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class testDraw {

    @Test
    public void testDrawEndTurn() {
        /*
    !isGameOver
	game is started
	isPlayerTurn || special card
    hasDraw
         */

        //make a card
        //////////DON'T NEED THIS BC HUMAN PLAYER AND AI CONSTRUCTORS

        DominionCardState copper = new DominionCardState(0, "Copper", "dominion_copper", "+1 Gold", 0, "TREASURE", "baseAction",
                1, 0, 0, 0, 0);
        DominionCardState estate = new DominionCardState(1, "Estate", "dominion_estate", "1 Victory Point", 2, "VICTORY", "baseAction",
                0, 0, 0, 0, 1);

        /*
        DominionCardState moat = new DominionCardState("Moat", "dominion_moat", "moat card text", 0, "REACTION", "baseAction", 0, 0, 0, 0, 0);
         */


    DominionShopPileState copperPile = new DominionShopPileState(copper, 10, DominionCardPlace.BASE_CARD);

        ArrayList<DominionShopPileState> shopPileArray = new ArrayList<>(1);
        shopPileArray.add(copperPile);

        //make a state

        int numPlayers = 4;
        DominionGameState state = new DominionGameState(numPlayers, shopPileArray, null); //TODO: Use Generator
        Log.i(this + "", "Current turn: " + state.getCurrentTurn());
        state.endTurn(state.getCurrentTurn());

        //remember old hand
        //assert different from old hand
        //will fail occasionally

        //assertFalse();

    }

    /**
     *
     * For testing purposes. Replaces cards in hand with specific set of cards to allow testing of actions

    public void testMoat(DominionCardState gold, DominionCardState moat){
        ArrayList<DominionCardState> hand = deck.getHand();
        hand.set(0, moat);
        hand.set(1, gold);
        hand.set(2, gold);
    }
     */

}
