package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gameframework.infoMsg.IllegalMoveInfo;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gameframework.util.GameTimer;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;
import project.katacka.dominion.localgame.DominionGameInfo;

import static android.content.ContentValues.TAG;

public class DominionComputerPlayer extends GameComputerPlayer {
    protected DominionGameState gameState;
    protected DominionPlayerState compPlayer;
    protected ArrayList<DominionCardState> draw;
    protected ArrayList<DominionCardState> discard;
    protected ArrayList<DominionCardState> hand;
    protected ArrayList<DominionShopPileState> shopCards;
    protected ArrayList<DominionShopPileState> baseCards;

    protected boolean turnStarted;
    protected enum turnPhases {ACTION, TREASURE, BUY, END, IN_PROGRESS}
    protected turnPhases currentPhase;

    protected Random rand;

    /**
     * Invokes the super-class constructor, setting the player's
     * name to <name> along with other default attributes
     *
     * @param name
     * 			the player's name (e.g., "John")
     */
    public DominionComputerPlayer(String name) {
        super(name);
        turnStarted = false;
        currentPhase = turnPhases.END;
        rand = new Random();
    }

    /*@Override
    protected void timerTicked() {
        if(turnStarted == false) {
            turnStarted = true;
            playTurn();
        }
    }*/

    /*protected void initAfterReady() {
        //Used to initialize dynamic data structs if necessary
    }*/

    /**
     * Callback-method implemented in the subclass whenever updated
     * state is received.
     *
     * @param info
     * 			the object representing the information from the game
     */
    protected void receiveInfo(GameInfo info){
        if(info instanceof IllegalMoveInfo || (info instanceof GameState && updateInfo(info))) {

            Log.d("AI", "Received info");
            if(info instanceof IllegalMoveInfo) {
                Log.e(TAG, "receiveInfo: " + info.toString());
            }

            if(currentPhase == turnPhases.END) currentPhase = turnPhases.ACTION;
            playTurnPhase(currentPhase);
        }
    }

    private boolean updateInfo(GameInfo info) {
        if(((DominionGameState) info).canMove(this.playerNum)) {
            gameState = (DominionGameState) info;
            compPlayer = gameState.getDominionPlayers()[playerNum];
            draw = compPlayer.getDeck().getDraw();
            hand = compPlayer.getDeck().getHand();
            discard = compPlayer.getDeck().getDiscard();
            shopCards = gameState.getShopCards();
            baseCards = gameState.getBaseCards();
            return true;
        }

        return false;
    }

    protected boolean playTurnPhase(turnPhases tempPhase) {
        return true;
    }

    /*protected boolean genericCardCheck(DominionCardState card) {
        return (card != null && card.getType() != DominionCardType.BLANK && card.getType() != null);
    }*/ //TODO: Move to testing

    protected boolean playTreasure() { //TODO: Do we need a callback here..?
        //for(int i = 0; i < hand.size(); i++) {
            //Log.i("c: " + hand.toString(), "t: " + gameState.getTreasure());

        int treasureIdx = IntStream.range(0, hand.size())
                                   .filter(i -> hand.get(i).getType() == DominionCardType.TREASURE)
                                   .findAny()
                                   .orElse(-1);
        if (treasureIdx < 0) {
            //currentPhase = turnPhases.BUY;
            return false;
        }

        currentPhase = turnPhases.TREASURE;
        sleep(100);
        game.sendAction(new DominionPlayCardAction(this, treasureIdx));
        return true;
    }

    protected boolean endTurn() {
        Log.d("AI", "Ending turn");

        currentPhase = turnPhases.END;
        sleep(100);
        game.sendAction(new DominionEndTurnAction(this));

        return true;
    }
}
