package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

/**
 * @author Ashika Hayden Julian Ryan
 *
 * Sent to DominionLocalGame to buy a card
 * Handled in makeMove
 */
public class DominionBuyCardAction extends GameAction {

    //card to be bought
    private int cardIndex;

    //if not a base card, it is a shop card
    private boolean isBaseCard;

    /**
     * constructor for DominionBuyCardAction
     *
     * @param player
     * 		the player who created the action
     */
    public DominionBuyCardAction(GamePlayer player, int cardIndex, boolean isBaseCard) {
        super(player);
        this.cardIndex = cardIndex;
        this.isBaseCard = isBaseCard;
    }

    public int getCardIndex(){
        return cardIndex;
    }

    public boolean getIsBaseCard(){
        return isBaseCard;
    }
}
