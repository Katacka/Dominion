package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.Random;
import java.util.stream.Stream;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionShopPileState;


public class DominionSimpleAIPlayer extends DominionComputerPlayer {
    protected Random rand;

    public DominionSimpleAIPlayer(String name) {
        super(name);
        rand = new Random();
    }

    @Override
    public boolean playTurnPhase(turnPhases tempPhase) {
        Log.d("SimpleAI", "Playing turn");
        currentPhase = turnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case ACTION:
                if (playSimpleActionPhase()) break;
            case TREASURE:
                if (playTreasure()) break;
            case BUY:
                if (playSimpleBuyPhase()) break;
            case END:
                endTurn();
                break;
            default:
                return false;
        }

        return true;
    }

    //TODO: Reference all actions properly
    public boolean playSimpleActionPhase() {
        //while (gameState.getActions() > 0) {
        if (gameState.getActions() > 0) {
            DominionCardState[] actionArray = hand.stream()
                    .filter(card -> card.getType() == DominionCardType.ACTION ||
                            card.getType() == DominionCardType.REACTION ||
                            card.getType() == DominionCardType.ATTACK).toArray(DominionCardState[]::new);

            if (actionArray.length < 1) return false; //Informs the AI that not all actions could be used
            int randPick = rand.nextInt(actionArray.length);

            currentPhase = turnPhases.ACTION;
            sleep(500);
            game.sendAction(new DominionPlayCardAction(this, randPick)); //TODO: PlayCardAction needs index
            return true;
        }
            //}

        return false;
    }

    public boolean playSimpleBuyPhase() {
        if (gameState.getBuys() > 0) {
            Log.i("a" + gameState.getBuys(), "gameTreasure: " + gameState.getTreasure());

            DominionShopPileState[] buyOptionsArray = Stream.of(shopCards.stream(), baseCards.stream())
                                                  .flatMap(cards -> cards)
                                                  .filter(card -> card.getCard().getCost() <= gameState.getTreasure())
                                                  .toArray(DominionShopPileState[]::new);

            if(buyOptionsArray.length < 1) return false; //Informs the AI that not all actions could be used
            DominionShopPileState randPile = buyOptionsArray[rand.nextInt(buyOptionsArray.length)];
            boolean isBaseCard = randPile.isBaseCard();
            int pileIdx = (isBaseCard) ? baseCards.indexOf(randPile) : shopCards.indexOf(randPile);

            currentPhase = turnPhases.BUY;
            sleep(500);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard)); //TODO: BuyCardAction needs proper params
            return true;
        }

        return false;
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

