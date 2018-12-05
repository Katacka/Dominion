package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;


/**
 * @author Hayden
 *
 * Sent to players when a card is played.
 * Used to toggle between shop and inplay
 */

public class DominionPlayCardInfo extends GameInfo {

    //The index of the card in the player's hand
    private final int cardIndex;

    /**
     * constructor for DominionPlayCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionPlayCardInfo(GamePlayer player, int cardIndex) {
        this.cardIndex = cardIndex;
    }

    /**
     * constructor for DominionPlayCardAction
     */
    public DominionPlayCardInfo() {
        this.cardIndex = 0;
    }


    public int getCardIndex() {
        return cardIndex;
    }
}
