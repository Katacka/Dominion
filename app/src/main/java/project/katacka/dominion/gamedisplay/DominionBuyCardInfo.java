package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionCardPlace;

/**
 * Sent to players when a card is bought.
 * Used to perform buy card animations.
 */
public class DominionBuyCardInfo extends GameInfo {
    private final int cardIndex;
    private final DominionCardPlace place;

    public DominionBuyCardInfo(int index, DominionCardPlace place){
        cardIndex = index;
        this.place = place;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public DominionCardPlace getPlace() {
        return place;
    }
}
