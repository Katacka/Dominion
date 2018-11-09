package project.katacka.dominion.gameplayer;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;
import java.util.Comparator;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
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
    public boolean playTurnPhase(turnPhases tempPhase) {
        Log.d("SimpleAI", "Playing turn");
        currentPhase = turnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case ACTION:
                if (playSmartActionPhase()) break;
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

        //if(currentPhase == turnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    //TODO: Reference all actions properly
    public boolean playSmartActionPhase() {
        if (gameState.getActions() > 0) {
            //Get all action cards in hand
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
            int handIdx = hand.indexOf(orderedActionArray[0]);

            if (!handleMoneylender(orderedActionArray[0])) {
                return false; //Informs the AI that not all actions could be used
            }

            currentPhase = turnPhases.ACTION;
            sleep(100);
            game.sendAction(new DominionPlayCardAction(this, handIdx)); //TODO: PlayCardAction needs index
            return true;
        }

        return false;
    }

    private boolean handleMoneylender(DominionCardState randCard) {
        boolean isCopper = hand.stream().anyMatch(card -> card.getTitle().equals("Copper"));
        return !randCard.getTitle().equals("Moneylender") || isCopper;
    }

    public boolean playSimpleBuyPhase() {
        if (gameState.getBuys() > 0) {
            Log.i("a" + gameState.getBuys(), "gameTreasure: " + gameState.getTreasure());

            Stream<DominionShopPileState> buyOptionsStream = Stream.of(shopCards.stream(), baseCards.stream())
                                                            .flatMap(piles -> piles)
                                                            .filter(pile -> pile.getAmount() > 0 &&
                                                                    pile.getCard().getCost() <= gameState.getTreasure() &&
                                                                    pile.getCard().getType() != DominionCardType.BLANK);

            DominionShopPileState[] orderedActionArray;
            if (predictTreasureDraw() < 8) {
                orderedActionArray = buyOptionsStream.map(DominionShopPileState::getCard)
                        .sorted(Comparator.comparing(DominionCardState::getAddedTreasure)
                                .thenComparing(DominionCardState::getAddedActions)
                                .thenComparing(DominionCardState::getAddedDraw)
                                .thenComparing(DominionCardState::getCost))
                        .toArray(DominionShopPileState[]::new);
            }
            else {
                //TODO: Fix implementation
                /*orderedActionArray = buyOptionsStream.map(DominionShopPileState::getCard)
                        .sorted(Comparator.comparing((a,b) -> a.getVictoryPoints(compPlayer.getDeck().getTotalCards()) > b.getVictoryPoints(compPlayer.getDeck().getTotalCards()) ? a : b)
                                .thenComparing(DominionCardState::getAddedActions)
                                .thenComparing(DominionCardState::getAddedDraw)
                                .thenComparing(DominionCardState::getCost))
                        .toArray(DominionShopPileState[]::new);*/
            }

            if(orderedActionArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionShopPileState selectedPile = orderedActionArray[0];
            boolean isBaseCard = selectedPile.isBaseCard();
            int pileIdx = (isBaseCard) ? baseCards.indexOf(selectedPile) : shopCards.indexOf(selectedPile);

            currentPhase = turnPhases.BUY;
            sleep(100);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard)); //TODO: BuyCardAction needs proper params
            return true;
        }

        //currentPhase = turnPhases.END;
        return false;
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

