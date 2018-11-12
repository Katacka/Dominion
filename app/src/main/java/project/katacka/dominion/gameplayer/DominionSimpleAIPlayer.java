package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.stream.Stream;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionShopPileState;


public class DominionSimpleAIPlayer extends DominionComputerPlayer {


    public DominionSimpleAIPlayer(String name) {
        super(name);
    }

    @Override
    public boolean playTurnPhase(TurnPhases tempPhase) {
        Log.d("SimpleAI", "Playing turn");
        if(tempPhase == TurnPhases.END) tempPhase = TurnPhases.ACTION;
        currentPhase = TurnPhases.IN_PROGRESS;

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
            case IN_PROGRESS:
                break;
            default:
                endTurn();
                return false;
        }

        //if(currentPhase == TurnPhases.IN_PROGRESS) currentPhase = tempPhase;
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

            if (actionArray.length < 1) {
                //currentPhase = TurnPhases.TREASURE;
                return false; //Informs the AI that not all actions could be used
            }
            DominionCardState randCard = actionArray[rand.nextInt(actionArray.length)];
            int handIdx = hand.indexOf(randCard);

            if (!handleMoneylender(randCard)) {
                //currentPhase = TurnPhases.TREASURE;
                return false; //Informs the AI that not all actions could be used
            }

            currentPhase = TurnPhases.ACTION;
            sleep(100);
            game.sendAction(new DominionPlayCardAction(this, handIdx)); //TODO: PlayCardAction needs index
            return true;
        }
            //}

        //currentPhase = TurnPhases.TREASURE;
        return false;
    }

    private boolean handleMoneylender(DominionCardState randCard) {
        boolean isCopper = hand.stream().anyMatch(card -> card.getTitle().equals("Copper"));
        return !randCard.getTitle().equals("Moneylender") || isCopper;
    }

    public boolean playSimpleBuyPhase() {
        if (gameState.getBuys() > 0) {
            Log.i("a" + gameState.getBuys(), "gameTreasure: " + gameState.getTreasure());

            DominionShopPileState[] buyOptionsArray = Stream.of(shopCards.stream(), baseCards.stream())
                                                  .flatMap(piles -> piles)
                                                  .filter(pile -> pile.getAmount() > 0 &&
                                                                  pile.getCard().getCost() <= gameState.getTreasure() &&
                                                                  pile.getCard().getType() != DominionCardType.BLANK)
                                                  .toArray(DominionShopPileState[]::new);

            if(buyOptionsArray.length < 1) {
                //currentPhase = TurnPhases.END;
                return false; //Informs the AI that not all actions could be used
            }
            DominionShopPileState randPile = buyOptionsArray[rand.nextInt(buyOptionsArray.length)];
            boolean isBaseCard = randPile.isBaseCard();
            int pileIdx = (isBaseCard) ? baseCards.indexOf(randPile) : shopCards.indexOf(randPile);

            currentPhase = TurnPhases.BUY;
            sleep(100);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard)); //TODO: BuyCardAction needs proper params
            return true;
        }

        //currentPhase = TurnPhases.END;
        return false;
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

