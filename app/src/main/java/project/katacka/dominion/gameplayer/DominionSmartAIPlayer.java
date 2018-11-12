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

/**
 * Complex AI behavior intended to provide an adaptive and competitive computer opponent
 * @author Julian Donovan, Ryan Regier, Ashika Mulagada, Hayden Liao
 */
public class DominionSmartAIPlayer extends DominionComputerPlayer {
    //General use variables (shared by all behaviors)
    private int remCopper; //Important for OTK determination of infinite state
    private int remSilver;
    private int remGold;
    private double avgDraw;
    private int pilesEmpty;

    //OTK Behavior variables
    enum BehaviorTypes {BIGMONEY, OTK}
    private BehaviorTypes compBehavior;
    private TurnPhases currentOTKSetupPhase;

    private boolean hasNeededMoneylenders;
    private boolean hasNeededCouncilRooms;
    private boolean hasNeededVillages;
    private int villageCouncilNum;
    private boolean hasNeededSilvers;
    private boolean canGoInfinite;

    private ArrayList<DominionShopPileState> availableVPPiles;
    private int totalVPCards;
    private int totalVPCardsCost;

    /**
     * Constructs a DominionSmartAIPlayer intended to be both adaptable and competitive
     * @param name Describes the name of the AI player
     * @return A DominionSmartAIPlayer object
     */
    public DominionSmartAIPlayer(String name) {
        super(name);
        remCopper = 7; //Player starts with 7 cards in their hand
        compBehavior = BehaviorTypes.OTK;
        currentOTKSetupPhase = TurnPhases.ACTION;
        hasNeededMoneylenders = false;
        hasNeededCouncilRooms = false;
        hasNeededVillages = false;
        hasNeededSilvers = false;
        canGoInfinite = false;
        villageCouncilNum = 0;
    }

    /**
     * Overrides DominionComputerPlayer's playTurnPhase method, introducing adaptive behaviors
     * @param tempPhase Describes the selected phase
     * @return A boolean value describing phase success
     */
    @Override
    public boolean playTurnPhase(TurnPhases tempPhase) {
        Log.d("SmartAI", "Playing turn");
        switch (compBehavior) {
            case OTK:
                if(tempPhase == TurnPhases.END) tempPhase = TurnPhases.SETUP;
                playOTKTurnPhase(tempPhase);
                break;
            case BIGMONEY:
                if(tempPhase == TurnPhases.END) tempPhase = TurnPhases.ACTION;
                playBigMoneyTurnPhase(tempPhase);
                break;
            default:
                endTurn();
                return false;
        }

        return true;
    }

    /**
     * Defines the general structure of the OTK behavior. Cascading switch behavior is used to implement
     * phase transition on failure
     * @param tempPhase Describes the selected phase
     */
    private void playOTKTurnPhase(TurnPhases tempPhase) {
        Log.d("SmartAI", "Using OTK behavior");
        currentPhase = TurnPhases.IN_PROGRESS; //Used to prevent possible race conditions

        switch (tempPhase) {
            case SETUP:
                if (hasNeededCouncilRooms && hasNeededVillages && hasNeededMoneylenders && hasNeededSilvers && remCopper <= 1) {
                    canGoInfinite = true;
                }
                else if (playOTKSetupPhase(currentOTKSetupPhase)) break;
            case INFINITE:
                if (playOTKInfinitePhase()) break;
            case WIN:
                if (playOTKWinPhase()) break;
            case END:
                endTurn();
            case IN_PROGRESS:
            default:
                break;
        }
    }

    /**
     * Elaborates on the specific structure of the OTK behavior's nested setup phases
     * @param tempPhase Describes the selected phase
     * @return A boolean value describing phase success
     */
    private boolean playOTKSetupPhase(TurnPhases tempPhase) {
        Log.d("SmartAI", "Using OTK behavior setup");
        currentOTKSetupPhase = TurnPhases.IN_PROGRESS; //Used to prevent possible race conditions

        switch (tempPhase) {
            case ACTION:
                if (playOTKSetupPlayPhase()) break;
            case TREASURE:
                if (playOTKSetupTreasurePhase()) break;
            case BUY:
                if (playOTKSetupBuyPhase()) break;
            case END:
                currentOTKSetupPhase = TurnPhases.ACTION;
                return false;
            case IN_PROGRESS:
                break;
            default:
                return false;
        }

        currentPhase = TurnPhases.SETUP;
        return true;
    }

    /**
     * Defines the card playing portion of the OTK behavior setup. Typically this is mainly concerned
     * with thinning the hand by act of the Moneylender card's trashing effect
     * @return A boolean value describing phase success
     */
    private boolean playOTKSetupPlayPhase() {
        if (gameState.getActions() > 0) {
            DominionPlayCardAction action;

            //Note to the grader: The entirety of my AI classes are based around streams. Apologies if this proves confusing, it was
            //very convenient, particularly for complex inter-sorting (which was frequently necessary)

            //This example below in particular creates a stream from the hand array list, finds only the cards with a title property of
            //"Moneylender" and returns the first match. If none are found, null is returned instead
            DominionCardState moneylenderCard = hand.stream()
                                                    .filter(card -> card.getTitle().equals("Moneylender"))
                                                    .findFirst()
                                                    .orElse(null);

            DominionCardState villageCard = hand.stream()
                                                .filter(card -> card.getTitle().equals("Village"))
                                                .findFirst()
                                                .orElse(null);

            DominionCardState councilRoomCard = hand.stream()
                                                    .filter(card -> card.getTitle().equals("Council Room"))
                                                    .findFirst()
                                                    .orElse(null);

            if (moneylenderCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(moneylenderCard));
                remCopper--; //Keeps track of total copper
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
            return true;
        }

        return false;
    }

    /**
     * Defines the treasure playing portion of the OTK behavior setup. All treasures are played
     * @return A boolean value describing phase success
     */
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

    /**
     * Defines the buying portion of the OTK behavior setup. A single Moneylender, Silver, Village and
     * Council Room are all that is needed to OTK
     * @return A boolean value describing phase success
     */
    private boolean playOTKSetupBuyPhase() {
        if (gameState.getBuys() > 0 && gameState.getTreasure() > 2) { //2 treasure is not enough to buy any of the necessary cards
            DominionBuyCardAction action;

            DominionShopPileState moneylenderCard = shopCards.stream()
                                                             .filter(pile -> !pile.isEmpty() && pile.getCard().getTitle().equals("Moneylender"))
                                                             .findFirst()
                                                             .orElse(null);

            DominionShopPileState silverCard = baseCards.stream()
                                                        .filter(pile -> !pile.isEmpty() && pile.getCard().getTitle().equals("Silver"))
                                                        .findFirst()
                                                        .orElse(null);

            DominionShopPileState villageCard = shopCards.stream()
                                                         .filter(pile -> !pile.isEmpty() && pile.getCard().getTitle().equals("Village"))
                                                         .findFirst()
                                                         .orElse(null);

            DominionShopPileState councilRoomCard = shopCards.stream()
                                                             .filter(pile -> !pile.isEmpty() && pile.getCard().getTitle().equals("Council Room"))
                                                             .findFirst()
                                                             .orElse(null);

            if (gameState.getTreasure() >= 4 && moneylenderCard != null && !hasNeededMoneylenders) {
                action = new DominionBuyCardAction(this, shopCards.indexOf(moneylenderCard), false);
                hasNeededMoneylenders = true;
            }
            else if (gameState.getTreasure() >= 3 && silverCard != null && !hasNeededSilvers) {
                action = new DominionBuyCardAction(this, baseCards.indexOf(silverCard), true);
                hasNeededSilvers = true;
            }
            else if (gameState.getTreasure() >= 3 && villageCard != null && (!hasNeededVillages || hasNeededCouncilRooms) && villageCouncilNum == 1) {
                action = new DominionBuyCardAction(this, shopCards.indexOf(villageCard), false);
                hasNeededVillages = true;
                villageCouncilNum = 2;
            }
            else if (gameState.getTreasure() >= 5 && councilRoomCard != null && (!hasNeededCouncilRooms || villageCouncilNum == 2)) {
                action = new DominionBuyCardAction(this, shopCards.indexOf(councilRoomCard), false);
                hasNeededCouncilRooms = true;
                villageCouncilNum = 1;
            }
            else return false;

            currentOTKSetupPhase = TurnPhases.BUY;
            sleep(100);
            game.sendAction(action);
            return true;
        }

        return false;
    }

    /**
     * If the AI completes it is able to enter the infinite phase, where if proper conditions have been met
     * the AI will be able to accrue enough buys and treasure to buy every victory point left in the game
     * @return A boolean value describing phase success
     */
    private boolean playOTKInfinitePhase() {
        if (gameState.getActions() > 0 && canGoInfinite) {
            DominionPlayCardAction action;

            DominionCardState silverCard = hand.stream()
                                               .filter(card -> card.getTitle().equals("Silver"))
                                               .findFirst()
                                               .orElse(null);

            DominionCardState villageCard = hand.stream()
                                                .filter(card -> card.getTitle().equals("Village"))
                                                .findFirst()
                                                .orElse(null);

            DominionCardState councilRoomCard = hand.stream()
                                                    .filter(card -> card.getTitle().equals("Council Room"))
                                                    .findFirst()
                                                    .orElse(null);

            if(canBuyAllVP()) return false;
            else if (silverCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(silverCard));
            }
            else if (villageCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(villageCard));
            }
            else if (councilRoomCard != null) {
                action = new DominionPlayCardAction(this, hand.indexOf(councilRoomCard));
            }
            else return false;

            currentPhase = TurnPhases.INFINITE;
            sleep(100);
            game.sendAction(action);
            return true;
        }

        return false;
    }

    /**
     * Checks to see whether infinite has accrued enough treasure and buys to buy every victory point
     * card left
     * @return A boolean value describing the viability of buying every victory point
     */
    private boolean canBuyAllVP() {
        //Ordered list of viable victory points piles from least value to greatest
        availableVPPiles = Stream.of(baseCards.stream(), shopCards.stream())
                .flatMap(pile -> pile)
                .filter(pile -> pile.getCard().getType() == DominionCardType.VICTORY &&
                        (pile.getAmount() > 1 ||
                                (pile.getAmount() > 0 && pile.getCard().getTitle().equals("Province"))))
                .sorted(Comparator.comparing(DominionShopPileState::getSimpleVictoryPoints))
                .collect(Collectors.toCollection(ArrayList::new));

        //Total number of victory point cards
        totalVPCards = availableVPPiles.stream()
                .mapToInt(DominionShopPileState::getAmount)
                .sum();

        //Total cost of victory point cards
        totalVPCardsCost = availableVPPiles.stream()
                .mapToInt(card -> card.getAmount() * card.getCost())
                .sum();

        return gameState.getBuys() >= totalVPCards && gameState.getTreasure() > totalVPCardsCost;
    }

    /**
     * If the infinite phase has properly met the buyAllVp requirements, the AI buys all victory points.
     * This wins all winnable games, effectively completing the OTK
     * @return A boolean value describing the success of the phase
     */
    private boolean playOTKWinPhase() {
        if (canBuyAllVP() && availableVPPiles.size() > 0) {
            DominionShopPileState selectedPile = availableVPPiles.get(0);
            boolean isBaseCard = selectedPile.isBaseCard();
            int pileIdx = (isBaseCard) ? baseCards.indexOf(selectedPile) : shopCards.indexOf(selectedPile);

            currentPhase = TurnPhases.WIN;
            sleep(100);
            game.sendAction(new DominionBuyCardAction(this, pileIdx, isBaseCard));
            return true;
        }

        return false;
    }

    /**
     * An alternate, more conservative behavior set used when OTK is no longer viable
     * @param tempPhase Describes the current phase
     */
    private void playBigMoneyTurnPhase(TurnPhases tempPhase) {
        Log.d("SmartAI", "Using Big Money behavior");
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
            case IN_PROGRESS:
            default:
                break;
        }
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
            sleep(100);
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

    @Override
    protected void receiveInfo(GameInfo info) {
        super.receiveInfo(info);

        if(info instanceof GameState && ((DominionGameState) info).canMove(this.playerNum)) {
            compBehavior = determineBehavior();

            if (draw != null && discard != null && hand != null) {
                //Draw parameters
                avgDraw = 5;

                //Shop parameters
                pilesEmpty = (int) Stream.of(shopCards.stream(), baseCards.stream())
                                         .flatMap(a -> a)
                                         .filter(DominionShopPileState::isEmpty)
                                         .count();
            }
        }
    }

    /**
     * Determines whether the AI should employ OTK or BigMoney behavior
     * @return A BehaviorTypes enum value
     */
    private BehaviorTypes determineBehavior() {
        if (compBehavior == BehaviorTypes.OTK) {
            boolean moneylenderExists = shopCards.stream()
                                                 .filter(pile -> !pile.isEmpty())
                                                 .map(DominionShopPileState::getCard)
                                                 .anyMatch(card -> card.getTitle().equals("Moneylender"));

            boolean villageExists = shopCards.stream()
                                             .filter(pile -> !pile.isEmpty())
                                             .map(DominionShopPileState::getCard)
                                             .anyMatch(card -> card.getTitle()
                                             .equals("Village"));

            boolean councilRoomExists = shopCards.stream()
                                                 .filter(pile -> !pile.isEmpty())
                                                 .map(DominionShopPileState::getCard)
                                                 .anyMatch(card -> card.getTitle()
                                                 .equals("Council Room"));

            boolean silverExists = baseCards.stream()
                                            .filter(pile -> !pile.isEmpty())
                                            .map(DominionShopPileState::getCard)
                                            .anyMatch(card -> card.getTitle()
                                            .equals("Silver"));

            //To the grader: Apologies for the awful format here, this was the cleanest format I could think of
            if(canGoInfinite || ((hasNeededMoneylenders || moneylenderExists) &&
                                 (hasNeededCouncilRooms || councilRoomExists) &&
                                 (hasNeededVillages || villageExists) &&
                                 (hasNeededSilvers || silverExists))) {
                return BehaviorTypes.OTK;
            }
        }

        return BehaviorTypes.BIGMONEY;
    }

    /**
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
        String string = "CardView Name: " + super.name;
        return string;
    }
}

