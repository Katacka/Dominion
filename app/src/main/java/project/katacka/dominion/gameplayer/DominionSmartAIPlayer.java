package project.katacka.dominion.gameplayer;

import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;

import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class DominionSmartAIPlayer extends DominionComputerPlayer {
    private int remCopper;
    private int remSilver;
    private int remGold;
    private int remCards;
    private double avgDraw;
    private int handTreasure;
    private int pilesEmpty;

    enum behaviorTypes {BigMoney, OTK}
    private behaviorTypes compBehavior;

    //OTK Behavior variables
    boolean hasNeededMoneylenders;
    boolean hasNeededCouncilRooms;
    boolean hasNeededVillages;
    boolean hasNeededSilvers;
    boolean canGoInfinite;

    public DominionSmartAIPlayer(String name) {
        super(name);
        remCopper = 7; //Player starts with 7 cards in their hand
        compBehavior = behaviorTypes.OTK;
    }

    @Override
    public boolean playTurnPhase(turnPhases tempPhase) {
        Log.d("SimpleAI", "Playing turn");
        switch (compBehavior) {
            case OTK:
                /*if(currentPhase == turnPhases.END) currentPhase = turnPhases.SETUP;
                playOTKTurnPhase(tempPhase);
                break;*/
            case BigMoney:
                if(currentPhase == turnPhases.END) currentPhase = turnPhases.ACTION;
                playBigMoneyTurnPhase(tempPhase);
                break;
            default:
                endTurn();
                return false;
        }

        //if(currentPhase == turnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    //TODO: Determine if boolean is useful
    private boolean playOTKTurnPhase(turnPhases tempPhase) {
        Log.d("SimpleAI", "Using Big Money behavior");
        currentPhase = turnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case SETUP:
                if (playOTKSetupPhase()) break;
            case INFINITE:
                if (playOTKInfinitePhase()) break;
            case WIN:
                if (playOTKWinPhase()) break;
            case END:
                endTurn();
                break;
            case IN_PROGRESS:
                break;
            default:
                return false;
        }

        //if(currentPhase == turnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    private boolean playOTKSetupPhase() {
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

            DominionCardState targetCard = orderedActionArray[0];
            int handIdx = hand.indexOf(targetCard);

            if (!handleMoneylender(orderedActionArray[0])) {
                return false; //Informs the AI that not all actions could be used
            }

            currentPhase = turnPhases.ACTION;
            sleep(100);
            game.sendAction(new DominionPlayCardAction(this, handIdx));
            return true;
        }

        return false;
    }

    private boolean playOTKInfinitePhase() {
        /*ArrayList<DominionCardState> cardOptionArray = hand.stream().sorted((a, b) -> {
            String cardName_A = a.getTitle();
            String cardName_B = b.getTitle();
            return Objects.equals(cardName_A, cardName_B) ? 0 :
                    cardName_A.equals("Silver") ? 1 :
                    cardName_B.equals("Silver") ? -1 :
                            cardName_A.equals("Village") ? 1 :
                                    cardName_B.equals("Village") ? -1 :
                                            cardName_A.equals("Council Room") ? 1 :
                                                    cardName_B.equals("Council Room") ? -1 :
                                                            cardName_A.equals("Silver") ? 1 :
                                                                    cardName_B.equals("Silver") ? -1 :
        })*/
        return false;
    }

    private boolean playOTKWinPhase() {
        //Ordered list of viable victory points piles from least value to greatest
        ArrayList<DominionShopPileState> availableVPPiles = Stream.of(baseCards.stream(), shopCards.stream())
                                                            .flatMap(pile -> pile)
                                                            .filter(pile -> pile.getCard().getType() == DominionCardType.TREASURE &&
                                                                            (pile.getAmount() > 1 ||
                                                                            (pile.getAmount() > 0 && pile.getCard().getTitle().equals("Province"))))
                                                            .sorted(Comparator.comparing(DominionShopPileState::getSimpleVictoryPoints))
                                                            .collect(Collectors.toCollection(ArrayList::new));
        int totalVPCards = availableVPPiles.stream()
                           .mapToInt(DominionShopPileState::getAmount)
                           .sum();

        if (gameState.getBuys() >= totalVPCards && availableVPPiles.size() > 0) {
            DominionShopPileState selectedPile = availableVPPiles.get(0);
            trackTreasure(selectedPile.getCard()); //Should do nothing
            boolean isBaseCard = selectedPile.isBaseCard();
            int pileIdx = (isBaseCard) ? baseCards.indexOf(selectedPile) : shopCards.indexOf(selectedPile);

            currentPhase = turnPhases.WIN;
            //TODO: See how fast this runs to determine an ideal sleep time
            sleep(30);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard));
            return true;
        }

        return false;
    }

    private boolean playBigMoneyTurnPhase(turnPhases tempPhase) {
        Log.d("SimpleAI", "Using Big Money behavior");
        currentPhase = turnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case ACTION:
                if (playBigMoneyActionPhase()) break;
            case TREASURE:
                if (playTreasure()) break;
            case BUY:
                if (playBigMoneyBuyPhase()) break;
            case END:
                endTurn();
                break;
            case IN_PROGRESS:
                break;
            default:
                return false;
        }

        //if(currentPhase == turnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    public boolean playBigMoneyActionPhase() {
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

            DominionCardState targetCard = orderedActionArray[0];
            int handIdx = hand.indexOf(targetCard);

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

    private boolean handleMoneylender(DominionCardState targetCard) {
        boolean isCopper = hand.stream().anyMatch(card -> card.getTitle().equals("Copper"));
        boolean isMoneylender = targetCard.getTitle().equals("Moneylender");

        if (isMoneylender && isCopper) remCopper--; //Updates known number of copper cards
        return !isMoneylender || isCopper;
    }

    public boolean playBigMoneyBuyPhase() {
        if (gameState.getBuys() > 0) {
            Log.i("a" + gameState.getBuys(), "gameTreasure: " + gameState.getTreasure());

            Stream<DominionShopPileState> buyOptionsStream = Stream.of(shopCards.stream(), baseCards.stream())
                                                            .flatMap(piles -> piles)
                                                            .filter(pile -> pile.getAmount() > 0 &&
                                                                    pile.getCard().getCost() <= gameState.getTreasure() &&
                                                                    pile.getCard().getType() != DominionCardType.BLANK &&
                                                                    !pile.getCard().getTitle().equals("Copper"));

            DominionShopPileState[] orderedActionArray;
            if (predictTreasureDraw() < 8) {
                orderedActionArray = buyOptionsStream
                        .sorted(Comparator.comparing(DominionShopPileState::getAddedTreasure)
                                .thenComparing(DominionShopPileState::getAddedActions)
                                .thenComparing(DominionShopPileState::getAddedDraw)
                                .thenComparing(DominionShopPileState::getCost).reversed())
                        .toArray(DominionShopPileState[]::new);
            }
            else {
                //TODO: Fix implementation
                orderedActionArray = buyOptionsStream
                        .sorted(Comparator.comparing(DominionShopPileState::getSimpleVictoryPoints)
                                .thenComparing(DominionShopPileState::getAddedActions)
                                .thenComparing(DominionShopPileState::getAddedDraw)
                                .thenComparing(DominionShopPileState::getCost).reversed())
                                .peek(pile -> Log.e("AI Hand", "Card: " + pile.getCard().getTitle())) //TODO: Remove if not debugging smart AI BigMoney buy behavior
                        .toArray(DominionShopPileState[]::new);
            }

            if(orderedActionArray.length < 1) {
                return false; //Informs the AI that not all actions could be used
            }

            DominionShopPileState selectedPile = orderedActionArray[0];
            trackTreasure(selectedPile.getCard());
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

    protected int getVictoryPoints(DominionCardState card) {
        return card.getVictoryPoints(compPlayer.getDeck().getTotalCards());
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        super.receiveInfo(info);

        if(info instanceof GameState && ((DominionGameState) info).canMove(this.playerNum)) {
            compBehavior = determineBehavior();

            if (draw != null && discard != null && hand != null) { //TODO: Ensure draw is being populated with shuffled discard
                //Draw parameters
                remCards = draw.size();
                //Stream<DominionCardState> allTreasures = Stream.of(draw.stream(), discard.stream(), hand.stream())
                /*DominionCardState[] remainingTreasures = draw.stream()
                                                               .filter(card -> card.getType() == DominionCardType.TREASURE)
                                                               .toArray(DominionCardState[]::new);
                remCopper = (int) Arrays.stream(remainingTreasures).filter(card -> card.getTitle().equals("Copper")).count();
                remSilver = (int) Arrays.stream(remainingTreasures).filter(card -> card.getTitle().equals("Silver")).count();
                remGold = (int) Arrays.stream(remainingTreasures).filter(card -> card.getTitle().equals("Gold")).count();
                avgDraw = 5;*/

                //Hand parameters
                handTreasure = (int) hand.stream().filter(card -> card.getType() == DominionCardType.TREASURE).count();

                //Shop parameters
                pilesEmpty = (int) Stream.of(shopCards.stream(), baseCards.stream())
                        .flatMap(a -> a)
                        .filter(DominionShopPileState::isEmpty)
                        .count();
            }
        }
    }

    private behaviorTypes determineBehavior() {
        if (compBehavior == behaviorTypes.OTK) {
            boolean moneylenderExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Moneylender"));
            boolean villageExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Village"));
            boolean councilRoomExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Council Room"));
            boolean silverExists = baseCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Silver"));

            if(canGoInfinite || ((hasNeededMoneylenders || moneylenderExists) &&
                                 (hasNeededCouncilRooms || councilRoomExists) &&
                                 (hasNeededVillages || villageExists) &&
                                 (hasNeededSilvers || silverExists))) {
                return behaviorTypes.OTK;
            }
        }

        return behaviorTypes.BigMoney;
    }


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

    private int calcExpectedTreasure() { //TODO: Render more intelligent accounting for card distribution
        int remTreasure = (remGold * 3) + (remSilver * 2) + remCopper;
        return remTreasure / remCards;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

