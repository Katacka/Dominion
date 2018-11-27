package project.katacka.dominion.gameplayer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.IntStream;

import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.actionMsg.GameOverAckAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.GameOverInfo;
import project.katacka.dominion.gameframework.infoMsg.GameState;
import project.katacka.dominion.gameframework.infoMsg.IllegalMoveInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionCardType;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static android.content.ContentValues.TAG;

/**
 * allows computer player to play cards, end turn and receive info from DominionLocalGame
 * used as parent class for AI players
 * @author Ryan Regier, Julian Donovan, Ashika Mulagada, Hayden Liao
 */

public abstract class DominionComputerPlayer extends GameComputerPlayer {
    protected DominionGameState gameState;
    protected DominionPlayerState compPlayer;
    protected ArrayList<DominionCardState> draw;
    protected ArrayList<DominionCardState> discard;
    protected ArrayList<DominionCardState> hand;
    protected ArrayList<DominionShopPileState> shopCards;
    protected ArrayList<DominionShopPileState> baseCards;

    protected enum TurnPhases {ACTION, TREASURE, BUY, END, IN_PROGRESS, SETUP, INFINITE, WIN}
    protected TurnPhases currentPhase;

    protected final Random rand;

    /**
     * Invokes the super-class constructor, setting the player's
     * name to <name> along with other default attributes
     *
     * @param name
     * 			the player's name (e.g., "John")
     */
    public DominionComputerPlayer(String name) {
        super(name);
        currentPhase = TurnPhases.END;
        rand = new Random();
    }

    /**
     * Callback-method implemented in the subclass whenever updated
     * state is received.
     *
     * @param info
     * 			the object representing the information from the game
     */
    protected void receiveInfo(GameInfo info){
        if (!gameOver && (info instanceof IllegalMoveInfo || (info instanceof GameState && updateInfo(info)))) {

            Log.d("AI", "Received info");
            if (info instanceof IllegalMoveInfo) {
                Log.e(TAG, "receiveInfo: " + info.toString());
            }

            if (gameState.canMove(playerNum)) {
                playTurnPhase(currentPhase);
            }
        }
    }

    /**
     * updates the correct computer player's hand, draw, discard
     *      and awareness of the community shop and base cards
     *
     * @param info
     *      the game state received from DominionLocalGame
     */
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

    /**
     * Overridden by child classes to implement varied turnPhase behavior
     * @param tempPhase The currently relevant phase
     */
    protected boolean playTurnPhase(TurnPhases tempPhase) {
        return true;
    }

    /**
     * Plays a treasure from the player's hand
     * @return Returns a boolean describing success
     */
    boolean playTreasure() {
        int treasureIdx = IntStream.range(0, hand.size())
                                   .filter(i -> hand.get(i).getType() == DominionCardType.TREASURE)
                                   .findAny()
                                   .orElse(-1);

        if (treasureIdx < 0) {
            return false;
        }

        currentPhase = TurnPhases.TREASURE;
        sleep(100);
        game.sendAction(new DominionPlayCardAction(this, treasureIdx));
        return true;
    }

    /**
     * Ends the player's turn
     * @return Returns a boolean describing success
     */
    boolean endTurn() {
        Log.d("AI", "Ending turn");
        currentPhase = TurnPhases.END;
        sleep(100);
        game.sendAction(new DominionEndTurnAction(this));
        return true;
    }
}
