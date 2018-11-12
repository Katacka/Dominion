package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;


/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to DominionLocalGame to play all treasure cards
 * Handled in makeMove
 */

public class DominionPlayAllAction extends GameAction {

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
