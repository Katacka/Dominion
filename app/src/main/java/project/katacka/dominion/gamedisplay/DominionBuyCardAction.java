package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionBuyCardAction extends GameAction {
    /**
     * constructor for DominionBuyCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionBuyCardAction(GamePlayer player) {
        super(player);
    }
}
