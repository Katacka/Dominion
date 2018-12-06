package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.infoMsg.GameInfo;

/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to players when a card is played.
 * Used to toggle between shop and inplay
 */

public class DominionPlayCardInfo extends GameInfo {

    //The index of the card in the player's hand
    private final int cardIndex;

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
