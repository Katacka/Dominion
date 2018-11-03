package project.katacka.dominion;


import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;

import static org.junit.Assert.*;

public class testDraw {

    @Test
    public void testDraw(){
        /*
    !isGameOver
	game is started
	isPlayerTurn || special card
    hasDraw
         */

        //make a card
        DominionCardState card = new DominionCardState("Moat", "photoID", "card text", 0, "REACTION", "moatAction",
                0, 0, 0, 0, 0);

        DominionDeckState deck = new DominionDeckState();


        //make a deck
        //make a player
        //make a state


/*
        int numPlayers = 4;
        DominionGameState state = new DominionGameState(numPlayers, null, null);
        //Create the players
        state.setDominionPlayers() = new DominionPlayerState[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            state.dominionPlayers[i] = new DominionPlayerState("Player " + i,
                    baseCards.get(PILE_COPPER), //The copper pile
                    baseCards.get(PILE_ESTATE).getCard()); //The estate card

        }


        assertFalse();



*/




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
