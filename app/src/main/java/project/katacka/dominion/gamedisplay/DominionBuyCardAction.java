package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gamestate.DominionCardPlace;

/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to DominionLocalGame to buy a card
 * Handled in makeMove
 */
public class DominionBuyCardAction extends GameAction {

    //card to be bought
    private final int cardIndex;

    //if not a base card, it is a shop card
    private final DominionCardPlace place;

    /**
     * constructor for DominionBuyCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionBuyCardAction(GamePlayer player, int cardIndex, DominionCardPlace place) {
        super(player);
        this.cardIndex = cardIndex;
        this.place = place;
    }

    public int getCardIndex(){
        return cardIndex;
    }

    public DominionCardPlace getCardPlace(){
        return place;
    }
}
