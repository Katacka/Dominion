package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.Comparator;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Complex AI behavior intended to provide an adaptive and competitive computer opponent
 * @author Julian Donovan, Ryan Regier, Ashika Mulagada, Hayden Liao
 */
public class DominionSmartAIPlayer extends DominionComputerPlayer {
    private int remCopper;
    private int remSilver;
    private int remGold;
    private double avgDraw;

    /**
     * Constructs a DominionSmartAIPlayer intended to be both adaptable and competitive
     * @param name Describes the name of the AI player
     */
    public DominionSmartAIPlayer(String name) {
        super(name);
        remCopper = 7; //Player starts with 7 cards in their hand
    }

    /**
     * Overrides DominionComputerPlayer's playTurnPhase method, introducing adaptive behaviors
     * @param tempPhase Describes the selected phase
     * @return A boolean value describing phase success
     */
    @Override
    public boolean playTurnPhase(TurnPhases tempPhase) {
        Log.d("SmartAI", "Using Big Money behavior");
        currentPhase = TurnPhases.IN_PROGRESS;

        if(tempPhase == TurnPhases.END) tempPhase = TurnPhases.ACTION;

        switch (tempPhase) {
            case ACTION:
                if (playBigMoneyActionPhase()) break;
            case TREASURE:
                if (playTreasure()) break;
            case BUY:
                if (playBigMoneyBuyPhase()) break;
            case END:
                endTurn();
            case IN_PROGRESS:
            default:
                break;
        }

        return true;
    }

    /**
     * Handles the action phase of the BigMoney behavior. Focuses on handling edge cases where action
     * cards were picked up, as BigMoney primarily fixates on Gold and Provinces
     * @return A boolean value describing the success of this phase
     */
    private boolean playBigMoneyActionPhase() {
        if (gameState.getActions() > 0) {
            //Get all action cards in the AI's hand
            Stream<DominionCardState> actionStream = hand.stream()
                                                         .filter(card -> card.getType() == DominionCardType.ACTION ||
                                                         card.getType() == DominionCardType.REACTION ||
                                                         card.getType() == DominionCardType.ATTACK);

            //Sorted by addedActions, then inter-sorted by addedDraw, then inter-sorted by cost
            DominionCardState[] orderedActionArray = actionStream.sorted(Comparator.comparing(DominionCardState::getAddedActions)
                                                                                   .thenComparing(DominionCardState::getAddedDraw)
                                                                                   .thenComparing(DominionCardState::getCost))
                                                                 .toArray(DominionCardState[]::new);

            if (orderedActionArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionCardState targetCard = orderedActionArray[0];
            int handIdx = hand.indexOf(targetCard);

            if (!handleMoneylender(orderedActionArray[0])) {
                return false; //Informs the AI that the Moneylender card could not be played
            }

            currentPhase = TurnPhases.ACTION;
            sleep(500);
            game.sendAction(new DominionPlayCardAction(this, handIdx));
            return true;
        }

        return false;
    }

    /**
     * Handles the Moneylender card's special discard behaviors
     * @param targetCard Describes the card in question
     * @return A boolean value describing the success of the Moneylender card
     */
    private boolean handleMoneylender(DominionCardState targetCard) {
        boolean isCopper = hand.stream().anyMatch(card -> card.getTitle().equals("Copper"));
        boolean isMoneylender = targetCard.getTitle().equals("Moneylender");

        if (isMoneylender && isCopper) remCopper--; //Updates known number of copper cards
        return !isMoneylender || isCopper;
    }

    /**
     * Handles the buy phase of the BigMoney behavior. Focuses on buying gold until enough is acquired
     * to statistically purchase a Province each turn
     * @return A boolean value describing the success of the phase
     */
    private boolean playBigMoneyBuyPhase() {
        if (gameState.getBuys() > 0) {
            Log.i("a" + gameState.getBuys(), "gameTreasure: " + gameState.getTreasure());

            Stream<DominionShopPileState> buyOptionsStream = Stream.of(shopCards.stream(), baseCards.stream())
                                                                   .flatMap(piles -> piles)
                                                                   .filter(pile -> pile.getAmount() > 0 &&
                                                                                   pile.getCard().getCost() <= gameState.getTreasure() &&
                                                                                   pile.getCard().getType() != DominionCardType.BLANK &&
                                                                                   !pile.getCard().getTitle().equals("Copper"));

            DominionShopPileState[] orderedActionArray;
            if (gameState.getTreasure() < 8) {
                orderedActionArray = buyOptionsStream.sorted(Comparator.comparing(DominionShopPileState::getAddedTreasure)
                                                                       .thenComparing(DominionShopPileState::getAddedActions)
                                                                       .thenComparing(DominionShopPileState::getAddedDraw)
                                                                       .thenComparing(DominionShopPileState::getCost).reversed())
                                                     .toArray(DominionShopPileState[]::new);
            }
            else {
                orderedActionArray = buyOptionsStream.sorted(Comparator.comparing(DominionShopPileState::getSimpleVictoryPoints)
                                                                       .thenComparing(DominionShopPileState::getAddedActions)
                                                                       .thenComparing(DominionShopPileState::getAddedDraw)
                                                                       .thenComparing(DominionShopPileState::getCost).reversed())
                                                     .toArray(DominionShopPileState[]::new);
            }

            if(orderedActionArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionShopPileState selectedPile = orderedActionArray[0];
            trackTreasure(selectedPile.getCard());
            DominionCardPlace place = selectedPile.getPlace();
            int pileIdx = (place == DominionCardPlace.BASE_CARD) ? baseCards.indexOf(selectedPile) : shopCards.indexOf(selectedPile);

            currentPhase = TurnPhases.BUY;
            sleep(500);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, place));
            return true;
        }

        return false;
    }

    /**
     * Keeps track of current treasure quantities after purchase
     * @param card Describes the card being tracked
     */
    private void trackTreasure(DominionCardState card) {
        switch (card.getTitle()) {
            case "Copper":
                remCopper++;
                break;
            case "Silver":
                remSilver++;
                break;
            case "Gold":
                remGold++;
                break;
            default:
                Log.e("trackTreasure: ", "Unexpected case encountered");
        }
    }

    /**
     * Receives info from DominionLocalGame regarding DominionGameState
     * @param info Describes the DominionGameState
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        super.receiveInfo(info);

        if(!gameOver && info instanceof GameState && ((DominionGameState) info).canMove(this.playerNum)) {
            if (draw != null && discard != null && hand != null) {
                //Draw parameters
                avgDraw = 5;
            }
        }
    }


    /**
     * TODO: Use method
     * Used to predict the average treasure value the AI will draw from its
     * @return A double describing the predicted treasure value of a hand
     */
    private double predictTreasureDraw() {
        double expectedTreasure = 0;
        double statModifier = 1;

        for(double i = avgDraw; i > 0; i--) {
            if(i < 1) statModifier = i;
            expectedTreasure += calcExpectedTreasure() * statModifier;
        }
        Log.e("Expected treasure", " " + expectedTreasure);
        return  expectedTreasure;
    }

    /**
     * Calculates total treasure base off known treasure distribution
     * @return A double describing the predicted treasure value of a single draw
     */
    private double calcExpectedTreasure() {
        double remTreasure = (remGold * 3) + (remSilver * 2) + remCopper;
        return remTreasure / compPlayer.getDeck().getTotalCards();
    }


    public String toString(){
        return "CardView Name: " + super.name;

    }
}

