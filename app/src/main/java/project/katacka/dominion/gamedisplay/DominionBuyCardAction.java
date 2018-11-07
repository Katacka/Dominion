package project.katacka.dominion.gamedisplay;

import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;

public class DominionBuyCardAction extends GameAction {

    //Which card is being bought
    private int cardIndex;

    //Whether the card is from the base cards. If not, it is from the kingdom cards.
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
