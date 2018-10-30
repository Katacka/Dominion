package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionActionPlayed extends GameAction {
    /**
     * constructor for DominionPlayCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionActionPlayed(GamePlayer player) {
        super(player);
    }

}
