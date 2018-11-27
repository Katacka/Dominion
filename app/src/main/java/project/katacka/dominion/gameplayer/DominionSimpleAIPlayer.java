package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.stream.Stream;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Simple AI behavior intended to provide a random, casual computer opponent
 * @author Julian Donovan, Ryan Regier, Ashika Mulagada, Hayden Liao
 */
public class DominionSimpleAIPlayer extends DominionComputerPlayer {

    /**
     * Constructs a DominionSimpleAIPlayer intended to be both adaptable and competitive
     * @param name Describes the name of the AI player
     */
    public DominionSimpleAIPlayer(String name) {
        super(name);
    }

    /**
     * Overrides DominionComputerPlayer's playTurnPhase method, introducing mostly randomized behaviors
     * @param tempPhase Describes the selected phase
     * @return A boolean value describing phase success
     */
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
            case END: //TODO: Not reachable case (according to linter)
                endTurn();
                break;
            case IN_PROGRESS:
            default:
                endTurn();
                return false;
        }

        return true;
    }

    /**
     * Handles the simpleAI's action phase, playing a valid action at random
     * @return A boolean value describing phase success
     */
    private boolean playSimpleActionPhase() {
        if (gameState.getActions() > 0) {
            DominionCardState[] actionArray = hand.stream()
                                                  .filter(card -> card.getType() == DominionCardType.ACTION ||
                                                                  card.getType() == DominionCardType.REACTION ||
                                                                  card.getType() == DominionCardType.ATTACK).toArray(DominionCardState[]::new);

            if (actionArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionCardState randCard = actionArray[rand.nextInt(actionArray.length)];
            int handIdx = hand.indexOf(randCard);

            if (!handleMoneylender(randCard)) {
                return false; //Informs the AI that Moneylender could not be used
            }

            currentPhase = TurnPhases.ACTION;
            sleep(400);
            game.sendAction(new DominionPlayCardAction(this, handIdx));
            return true;
        }

        return false;
    }

    /**
     * Handles the Moneylender card's special discard behaviors
     * @param randCard Describes the card in question
     * @return A boolean value describing the success of the Moneylender card
     */
    private boolean handleMoneylender(DominionCardState randCard) {
        boolean isCopper = hand.stream().anyMatch(card -> card.getTitle().equals("Copper"));
        return !randCard.getTitle().equals("Moneylender") || isCopper;
    }

    /**
     * Handles the simpleAI's action phase, buying a valid card at random
     * @return A boolean value describing phase success
     */
    private boolean playSimpleBuyPhase() {
        if (gameState.getBuys() > 0) {
            DominionShopPileState[] buyOptionsArray = Stream.of(shopCards.stream(), baseCards.stream())
                                                            .flatMap(piles -> piles)
                                                            .filter(pile -> pile.getAmount() > 0 &&
                                                                            pile.getCard().getCost() <= gameState.getTreasure() &&
                                                                            pile.getCard().getType() != DominionCardType.BLANK)
                                                            .toArray(DominionShopPileState[]::new);

            if(buyOptionsArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionShopPileState randPile = buyOptionsArray[rand.nextInt(buyOptionsArray.length)];
            DominionCardPlace place = randPile.getPlace();
            int pileIdx = (place == DominionCardPlace.BASE_CARD) ? baseCards.indexOf(randPile) : shopCards.indexOf(randPile);

            currentPhase = TurnPhases.BUY;
            sleep(500);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, place));
            return true;
        }

        return false;
    }

    public String toString(){
        return "CardView Name: " + super.name;
    }
}

