package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to DominionLocalGame to play a card
 * Handled in makeMove
 */

public class DominionPlayCardAction extends GameAction {

    //The index of the card in the player's hand
    private final int cardIndex;

    /**
     * constructor for DominionPlayCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionPlayCardAction(GamePlayer player, int cardIndex) {
        super(player);
        this.cardIndex = cardIndex;
    }

    public int getCardIndex() {
        return cardIndex;
    }
}
