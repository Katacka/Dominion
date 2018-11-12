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
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class DominionSmartAIPlayer extends DominionComputerPlayer {
    private int remCopper; //Important for OTK determination of infinite state
    private int remSilver;
    private int remGold;
    private int remCards;
    private double avgDraw;
    private int handTreasure;
    private int pilesEmpty;

    enum BehaviorTypes {BIGMONEY, OTK}
    private BehaviorTypes compBehavior;

    TurnPhases currentOTKSetupPhase;

    //OTK Behavior variables
    private boolean hasNeededMoneylenders;
    private boolean hasNeededCouncilRooms;
    private boolean hasNeededVillages;
    private boolean hasNeededSilvers;
    private boolean canGoInfinite;

    public DominionSmartAIPlayer(String name) {
        super(name);
        remCopper = 7; //Player starts with 7 cards in their hand
        compBehavior = BehaviorTypes.OTK;
        currentOTKSetupPhase = TurnPhases.ACTION;
    }

    //TODO: Add phase switch from OTK to BigMoney
    @Override
    public boolean playTurnPhase(TurnPhases tempPhase) {
        Log.d("SimpleAI", "Playing turn");
        switch (compBehavior) {
            case OTK:
                /*if(currentPhase == TurnPhases.END) currentPhase = TurnPhases.SETUP;
                playOTKTurnPhase(tempPhase);
                break;*/
            case BIGMONEY:
                if(tempPhase == TurnPhases.END) tempPhase = TurnPhases.ACTION;
                playBigMoneyTurnPhase(tempPhase);
                break;
            default:
                endTurn();
                return false;
        }

        //if(currentPhase == TurnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    //TODO: Determine if boolean is useful
    private boolean playOTKTurnPhase(TurnPhases tempPhase) {
        Log.d("SimpleAI", "Using Big Money behavior");
        currentPhase = TurnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case SETUP:
                if (playOTKSetupPhase(tempPhase)) break;
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

        //if(currentPhase == TurnPhases.IN_PROGRESS) currentPhase = tempPhase;
        return true;
    }

    private boolean playOTKSetupPhase(TurnPhases tempPhase) {
        Log.d("SimpleAI", "Using Big Money behavior setup");
        currentOTKSetupPhase = TurnPhases.IN_PROGRESS;

        switch (tempPhase) {
            case ACTION:
                if (playOTKSetupPlayPhase()) break;
            case TREASURE:
                if (playOTKSetupTreasurePhase()) break;
            case BUY:
                if (playOTKSetupBuyPhase()) break;
            case END:
                return false;
            case IN_PROGRESS:
                break;
            default:
                return false;
        }

        currentPhase = TurnPhases.SETUP;
        return true;
    }

    //TODO: Finish these phases
    private boolean playOTKSetupPlayPhase() {
        if (gameState.getActions() > 0) {
            DominionPlayCardAction action;

            DominionCardState moneylenderCard = hand.stream().filter(card -> card.getTitle().equals("Moneylender")).findFirst().orElse(null);
            DominionCardState villageCard = hand.stream().filter(card -> card.getTitle().equals("Village")).findFirst().orElse(null);
            DominionCardState councilRoomCard = hand.stream().filter(card -> card.getTitle().equals("Council Room")).findFirst().orElse(null);

            if (moneylenderCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(moneylenderCard));
                remCopper--;
            }
            else if (villageCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(villageCard));
            }
            else if (councilRoomCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(councilRoomCard));
            }
            else return false;

            currentOTKSetupPhase = TurnPhases.ACTION;
            sleep(100);
            game.sendAction(action);
        }

        return false;
    }

    private boolean playOTKSetupTreasurePhase() {
        int treasureIdx = IntStream.range(0, hand.size())
                .filter(i -> hand.get(i).getType() == DominionCardType.TREASURE)
                .findAny()
                .orElse(-1);
        if (treasureIdx < 0) {
            return false;
        }

        currentOTKSetupPhase = TurnPhases.TREASURE;
        sleep(100);
        game.sendAction(new DominionPlayCardAction(this, treasureIdx));
        return true;
    }

    private boolean playOTKSetupBuyPhase() {
        if (gameState.getBuys() > 0 && handTreasure) {
            DominionBuyCardAction action;


            DominionCardState moneylenderCard = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).filter(card -> card.getTitle().equals("Moneylender")).findFirst().orElse(null);
            DominionCardState silverCard = baseCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).filter(card -> card.getTitle().equals("Silver")).findFirst().orElse(null);
            DominionCardState villageCard = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).filter(card -> card.getTitle().equals("Village")).findFirst().orElse(null);
            DominionCardState councilRoomCard = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).filter(card -> card.getTitle().equals("Council Room")).findFirst().orElse(null);

            if (moneylenderCard != null && hasNeededMoneylenders) {
                action = new DominionBuyCardAction(this, hand.indexOf(moneylenderCard), false);
                hasNeededSilvers = true;
            }
            else if (silverCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(silverCard), true);
                remCopper--;
            }
            else if (villageCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(villageCard), false);
            }
            else if (councilRoomCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(councilRoomCard), false);
            }
            else return false;

            currentOTKSetupPhase = TurnPhases.ACTION;
            sleep(100);
            game.sendAction(action);
        }

        return false;
    }

    private boolean playOTKInfinitePhase() {
        if (gameState.getActions() > 0) {
            DominionPlayCardAction action;

            DominionCardState silverCard = hand.stream().filter(card -> card.getTitle().equals("Silver")).findFirst().orElse(null);
            DominionCardState villageCard = hand.stream().filter(card -> card.getTitle().equals("Village")).findFirst().orElse(null);
            DominionCardState councilRoomCard = hand.stream().filter(card -> card.getTitle().equals("Council Room")).findFirst().orElse(null);
            //DominionCardState otherCard = hand.stream().filter(card -> !card.getTitle().equals("Silver") && !card.getTitle().equals("Village") && !card.getTitle().equals("Council Room")).findFirst().orElse(null);

            if (silverCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(silverCard));
            }
            else if (villageCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(villageCard));
            }
            else if (councilRoomCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(councilRoomCard));
            }
            /*else if (otherCard != null && ) {

            }*/
            else return false;

            currentPhase = TurnPhases.INFINITE;
            sleep(100);
            game.sendAction(action);
        }

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

            currentPhase = TurnPhases.WIN;
            //TODO: See how fast this runs to determine an ideal sleep time
            sleep(30);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard));
            return true;
        }

        return false;
    }

    private boolean playBigMoneyTurnPhase(TurnPhases tempPhase) {
        Log.d("SimpleAI", "Using Big Money behavior");
        currentPhase = TurnPhases.IN_PROGRESS;

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

        //if(currentPhase == TurnPhases.IN_PROGRESS) currentPhase = tempPhase;
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

            currentPhase = TurnPhases.ACTION;
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

            currentPhase = TurnPhases.BUY;
            sleep(100);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard)); //TODO: BuyCardAction needs proper params
            return true;
        }

        //currentPhase = TurnPhases.END;
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
                //handTreasure = (int) hand.stream().filter(card -> card.getType() == DominionCardType.TREASURE).count();

                //Shop parameters
                pilesEmpty = (int) Stream.of(shopCards.stream(), baseCards.stream())
                        .flatMap(a -> a)
                        .filter(DominionShopPileState::isEmpty)
                        .count();
            }
        }
    }

    private BehaviorTypes determineBehavior() {
        if (compBehavior == BehaviorTypes.OTK) {
            boolean moneylenderExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Moneylender"));
            boolean villageExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Village"));
            boolean councilRoomExists = shopCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Council Room"));
            boolean silverExists = baseCards.stream().filter(pile -> !pile.isEmpty()).map(DominionShopPileState::getCard).anyMatch(card -> card.getTitle().equals("Silver"));

            if(canGoInfinite || ((hasNeededMoneylenders || moneylenderExists) &&
                                 (hasNeededCouncilRooms || councilRoomExists) &&
                                 (hasNeededVillages || villageExists) &&
                                 (hasNeededSilvers || silverExists))) {
                return BehaviorTypes.OTK;
            }
        }

        return BehaviorTypes.BIGMONEY;
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

