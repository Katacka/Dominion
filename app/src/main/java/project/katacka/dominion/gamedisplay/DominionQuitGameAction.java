package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionQuitGameAction extends GameAction {

    /**
     * constructor for DominionEndTurnAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionQuitGameAction(GamePlayer player) {
        super(player);
    }

}
