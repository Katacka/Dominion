package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;


/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to DominionLocalGame to end current player's turn
 * Handled in makeMove
 */

public class DominionEndTurnAction extends GameAction {
    /**
     * constructor for DominionEndTurnAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionEndTurnAction(GamePlayer player) {
        super(player);
    }

}
