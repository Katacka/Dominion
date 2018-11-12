package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionPlayAllAction extends GameAction {

    //The index of the card in the player's hand
    private int cardIndex;

    /**
     * constructor for DominionPlayCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionPlayAllAction(GamePlayer player) {
        super(player);
    }
}
