package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gameframework.util.GameTimer;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class DominionComputerPlayer extends GameComputerPlayer {
    protected DominionGameState gameState;
    protected DominionPlayerState compPlayer;
    protected ArrayList<DominionCardState> draw;
    protected ArrayList<DominionCardState> discard;
    protected ArrayList<DominionCardState> hand;
    protected ArrayList<DominionShopPileState> shopCards;
    protected ArrayList<DominionShopPileState> baseCards;
    protected Random rand;
    protected boolean turnStarted = false;

    /**
     * Invokes the super-class constructor, setting the player's
     * name to <name> along with other default attributes
     *
     * @param name
     * 			the player's name (e.g., "John")
     */
    public DominionComputerPlayer(String name) {
        super(name);
    }

    @Override
    protected void timerTicked() {
        if(turnStarted == false) {
            turnStarted = true;
            playTurn();
        }
    }

    /*protected void initAfterReady() {
        //Used to initialize dynamic data structs if necessary
    }*/

    protected boolean playTurn() { return true; }

    /**
     * Callback-method implemented in the subclass whenever updated
     * state is received.
     *
     * @param info
     * 			the object representing the information from the game
     */
    protected void receiveInfo(GameInfo info){
        if(info == null) return;
        if(!(info instanceof DominionGameState)) return;
        gameState = (DominionGameState) info;
        if(!gameState.canMove(playerNum)) return; //Ignore non-applicable info
        compPlayer = gameState.getDominionPlayers()[playerNum];
        draw = compPlayer.getDeck().getDraw();
        hand = compPlayer.getDeck().getHand();
        discard = compPlayer.getDeck().getDiscard();
        shopCards = gameState.getShopCards();
        baseCards = gameState.getBaseCards();

        Log.d("AI", "Recieved info");

        if(!turnStarted) {
            turnStarted = true;
            playTurn();
        }
    }

    /*protected boolean genericCardCheck(DominionCardState card) {
        return (card != null && card.getType() != DominionCardType.BLANK && card.getType() != null);
    }*/ //TODO: Move to testing

    protected boolean playAllTreasures() { //TODO: Do we need a callback here..?
        for(int i = 0; i < draw.size(); i++) {
            DominionCardState card = draw.get(i);

            /*if (!genericCardCheck(card)) { //TODO: Move to testing
                Log.e("Invalid card observed: ", card.toString());
                return false;
            }*/
            if (card.getType() == DominionCardType.TREASURE) {
                //game.sendAction(new DominionPlayCardAction(this, i)); TODO: PlayCardAction needs index
                //sleep(100);
            }
        }
        return true;
    }

    protected boolean endTurn() {
        Log.d("AI", "Ending turn");
        game.sendAction(new DominionEndTurnAction(this));
        turnStarted = false;
        return true;
    }
}
