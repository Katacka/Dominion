package project.katacka.dominion.gameplayer;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.stream.Stream;

import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class DominionSmartAIPlayer extends DominionComputerPlayer {
    private int remCopper;
    private int remSilver;
    private int remGold;
    private int remCards;
    private double avgDraw;
    private int handTreasure;
    private int pilesEmpty;

    enum TargetMax {Actions, Draw, Cost}

    public DominionSmartAIPlayer(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        super.receiveInfo(info);
        if(!turnStarted) return; //Only updates on AI's turn

        if(draw != null && discard != null && hand != null) { //TODO: Ensure draw is being populated with shuffled discard
            //Draw parameters
            remCards = draw.size();
            //Stream<DominionCardState> allTreasures = Stream.of(draw.stream(), discard.stream(), hand.stream())
            Stream<DominionCardState> remainingTreasures = draw.stream()
                                //                         .flatMap(card -> card)
                                                           .filter(card -> card.getType() == DominionCardType.TREASURE);
            remCopper = (int) remainingTreasures.filter(card -> card.getTitle() == "Copper").count();
            remSilver = (int) remainingTreasures.filter(card -> card.getTitle() == "Silver").count();
            remGold = (int) remainingTreasures.filter(card -> card.getTitle() == "Gold").count();
            avgDraw = 5;

            //Hand parameters
            handTreasure = (int) hand.stream().filter(card -> card.getType() == DominionCardType.TREASURE).count();

            //Shop parameters
            pilesEmpty = (int) Stream.of(shopCards.stream(), baseCards.stream())
                               .flatMap(a -> a)
                               .filter(DominionShopPileState::isEmpty)
                               .count();
        }
    }

    @Override
    public boolean playTurnPhase(turnPhases tempPhase) {
        avgDraw = 5;
        playSmartActionPhase();
        playTreasure();
        playSmartBuyPhase();
        endTurn();
        return true;
    }


    //TODO: Reference all actions properly
    public boolean playSmartActionPhase() {
        while (gameState.getActions() > 0) {
            DominionCardState[] actionArray = hand.stream()
                                              .filter(card -> card.getType() == DominionCardType.ACTION ||
                                                              card.getType() == DominionCardType.REACTION ||
                                                              card.getType() == DominionCardType.ATTACK)
                                              .toArray(DominionCardState[]::new);

            if(actionArray.length < 1) return false; //Informs the AI that not all available actions could be used

            //TODO: Consider the impact of cards that generate money in hand, in respect to one's treasure in hand
            //TODO: This involves considering one's expected draw value compared to treasure cards in hand
            //Attempts to play a card that generates more actions
            int maxActionsIdx = findMax(actionArray, TargetMax.Actions);
            if(actionArray[maxActionsIdx].getAddedDraw() > 0) {
                //game.sendAction(new DominionPlayCardAction(this, maxDrawIdx)); //TODO: PlayCardAction needs index
                avgDraw = (avgDraw + actionArray[maxActionsIdx].getAddedDraw()) / 2;
                maxActionsIdx = findMax(actionArray, TargetMax.Actions);
                sleep(250);
            }

            //Attempts to play a card that draws more cards
            int maxDrawIdx = findMax(actionArray, TargetMax.Draw);
            if (actionArray[maxDrawIdx].getAddedDraw() > 0) {
                //game.sendAction(new DominionPlayCardAction(this, maxDrawIdx)); //TODO: PlayCardAction needs index
                avgDraw = (avgDraw + actionArray[maxDrawIdx].getAddedDraw()) / 2;
                maxDrawIdx = findMax(actionArray, TargetMax.Draw);
                sleep(250);
            }

            //Defaults to playing the highest cost card
            int maxCostIdx = findMax(actionArray, TargetMax.Cost);
            if (actionArray[maxCostIdx].getAddedDraw() > 0) {
                //game.sendAction(new DominionPlayCardAction(this, maxDrawIdx)); //TODO: PlayCardAction needs index
                maxCostIdx = findMax(actionArray, TargetMax.Cost);
                sleep(250);
            }
        }

        //if (!genericCardCheck(card)) return false; //TODO: Move to testing
        return true;
    }

    private int findMax(DominionCardState[] cardArray, TargetMax target) {
        int maxIdx = 0;

        switch(target) {
            case Actions:
                for (int i = 1; i < cardArray.length; i++) {
                    if (cardArray[i].getAddedActions() > cardArray[maxIdx].getAddedActions()) {
                        maxIdx = i;
                    }
                }
                break;
            case Draw:
                for (int i = 1; i < cardArray.length; i++) {
                    if (cardArray[i].getAddedDraw() > cardArray[maxIdx].getAddedDraw()) {
                        maxIdx = i;
                    }
                }
                break;
            case Cost:
                for (int i = 1; i < cardArray.length; i++) {
                    if (cardArray[i].getCost() > cardArray[maxIdx].getCost()) {
                        maxIdx = i;
                    }
                }
                break;
            default:
        }

        return maxIdx;
    }

    //TODO: Finish implementation
    public boolean playSmartBuyPhase() {
        double expectedTreasure = predictTreasureDraw();

        while (gameState.getBuys() > 0) {
            DominionCardState[] shopBaseArray = Stream.of(shopCards.stream(), baseCards.stream())
                                                .flatMap(a -> a)
                                                .filter(pile -> !pile.isEmpty() &&
                                                                pile.getCard().getType() != null &&
                                                                pile.getCard().getType() != DominionCardType.BLANK)
                                                .toArray(DominionCardState[]::new);

            if(shopBaseArray.length < 1) {
                Log.e("All shop or base piles are empty. ", "The game should have ended before now");
                return false; //Informs the AI that all piles are empty
            }

            while(gameState.getBuys() > 0) {
                /*if (pilesEmpty > 2 &&  ) { //Wins if possible

                }*/
                if (handTreasure > 8 && expectedTreasure > 8) { //Province targeting behavior

                }
                else if(handTreasure > 6) { //Gold targeting behavior

                }
                else { //Default behavior

                }

                //if (!genericCardCheck(card)) return false; //TODO: Move to testing
                //game.sendAction(new DominionBuyCardAction(this, randPick)); TODO: BuyCardAction needs proper params
                sleep(250);
            }
        }
        return true;
    }

    private double predictTreasureDraw() {
        double expectedTreasure = 0;
        double statModifier = 1;
        for(double i = avgDraw; i > 0; i--) {
            if(i < 1) statModifier = i;
                expectedTreasure += calcExpectedTreasure() * statModifier;
        }
        return  expectedTreasure;
    }

    private int calcExpectedTreasure() { //TODO: Render more intelligent accounting for card distribution
        int remTreasure = (remGold * 3) + (remSilver * 2) + remCopper;
        return remTreasure / remCards;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

