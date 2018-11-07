package project.katacka.dominion;

import org.junit.Test;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class testShuffleDiscard {

    @Test
    public void testShuffleDiscard(){
        DominionGameState state = new DominionGameState(4, null, null);
        DominionCardState copperState = new DominionCardState ("copper", "dominion_copper", "this is copper", 3, "base", "money",
        4, 1, 0, 0, 0);
        DominionCardState estateState = new DominionCardState ("Estate", "dominion_estate", "this is estate", 3, "province", "money",
                4, 1, 0, 0, 2);
        DominionShopPileState copperPileState = new DominionShopPileState(copperState, 3);
        //DominionPlayerState playerState = new DominionPlayerState("Player", shopPileState, cardState);
        DominionPlayerState player = new DominionPlayerState("player2", copperPileState, estateState);
        //if player tries to draw from empty draw pile, it will reshuffle
        //assertequals(0, player.getDeck().getDrawSize());
        //assert
        player.getDeck().draw();


        //state. = new DominionPlayerState[4];
        for (int i = 0; i < 4; i++) {
        //    state.setDominionPlayers(DominionPlayerState[] dominion) = new DominionPlayerState("Player " + i,
        //            baseCards.get(PILE_COPPER), //The copper pile
        //            baseCards.get(PILE_ESTATE).getCard()); //The estate card

        }
        //number of cards in pile is zero should do nothing
        //assert(discard.getsize(0), 0);
    }

}
