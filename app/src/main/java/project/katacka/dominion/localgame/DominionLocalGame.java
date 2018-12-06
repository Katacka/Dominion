package project.katacka.dominion.localgame;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Locale;

import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionBuyCardInfo;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayAllAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardInfo;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.LocalGame;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gamestate.CardReader;
import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Implementation of Local Game for Dominion.
 *
 * @author Ryan Regier, Julian Donovan, Ashika Mulagada, Hayden Liao
 */
public class DominionLocalGame extends LocalGame {

    //The official copy of the game state
    private DominionGameState state;

    //The cards used for the GameState
    private final ArrayList<DominionShopPileState> baseCards;
    private final ArrayList<DominionShopPileState> shopCards;

    /**
     * Constructor. Reads cards from XML.
     * @param context The context in which the game is created. Used to read the XML.
     */
    public DominionLocalGame(Context context){
        super();
        CardReader reader = new CardReader("base");
        baseCards = reader.generateCards(context, R.raw.base_cards);
        shopCards = reader.generateCards(context, R.raw.shop_cards);
    }

    /**
     * Copies and sends game state. Hides cards that cannot be seen.
     *
     * @param p The player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p){
        DominionGameState copy = new DominionGameState(state, getPlayerIdx(p));
        p.sendInfo(copy);
    }

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     * 		true iff the player is allowed to move
     */
    @Override
    protected boolean canMove(int playerIdx){
        return state.canMove(playerIdx);
    }

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return
     * 			a message that tells who has won the game, or null if the
     * 			game is not over
     */
    @Override
    protected String checkIfGameOver(){
        if (!state.getGameOver()){
            return null; //Game is not over
        }

        int[] scores = state.getPlayerScores();
        int winner = state.getWinner();
        String result;
        if (winner != -1){ //No tie
            result = String.format(Locale.US, "%s won.\nScores:\n", playerNames[winner]);
        } else { //In case of tie
            int[] tiedPlayers = state.getTiedPlayers();
            int numTied = tiedPlayers.length;
            result = String.format(Locale.US, "There was a %d-way tie between ", numTied);
            for (int i = 0; i < numTied; i++){
                if (i == numTied-1){ //Last player in tied list.
                    result = result.concat(String.format(Locale.US, "%s.\nScores:\n", playerNames[tiedPlayers[i]]));
                } else if ( i == numTied - 2){ //Second to last player in tied list.
                    result = result.concat(String.format(Locale.US, "%s and ", playerNames[tiedPlayers[i]]));
                } else { //All other players
                    result = result.concat(String.format(Locale.US, "%s, ", playerNames[tiedPlayers[i]]));
                }
            }
        }

        //Prints everyone's scores out
        for(int i = 0; i < playerNames.length; i++){
            result = result.concat(String.format(Locale.US, "%s: %d\n", playerNames[i], scores[i]));
        }

        return result.concat("Thanks for playing!");
    }

    /**
     * Makes a move on behalf of a player.
     *
     * @param gameAction
     * 			The move that the player has sent to the game
     * @return
     * 			Tells whether the move was a legal one.
     */
    @Override
    protected boolean makeMove(GameAction gameAction){
        if (gameAction instanceof DominionPlayCardAction){
            DominionPlayCardAction action = (DominionPlayCardAction) gameAction;
            GamePlayer player = action.getPlayer();

            int playerID = getPlayerIdx(player);
            int cardIndex = action.getCardIndex();

            boolean success = state.playCard(playerID, cardIndex);

            if (success){
                DominionPlayCardInfo info = new DominionPlayCardInfo();
                for (GamePlayer gamePlayer : players){
                    gamePlayer.sendInfo(info);
                    Log.i("TAG", "sending players playCard info");
                }
            }

            return success;

        } else if (gameAction instanceof DominionPlayAllAction){
            DominionPlayAllAction action = (DominionPlayAllAction) gameAction;
            GamePlayer player = action.getPlayer();

            int playerID = getPlayerIdx(player);

            return state.playAllCards(playerID);
        } else if (gameAction instanceof DominionBuyCardAction){

            DominionBuyCardAction action = (DominionBuyCardAction) gameAction;
            GamePlayer player = action.getPlayer();

            int playerIndex = getPlayerIdx(player);
            int cardIndex = action.getCardIndex();
            DominionCardPlace place = action.getCardPlace();

            boolean success =  state.buyCard(playerIndex, cardIndex, place);

            if (success){
                DominionBuyCardInfo info = new DominionBuyCardInfo(cardIndex, place);
                for (GamePlayer gamePlayer : players){
                    gamePlayer.sendInfo(info);
                    Log.i("TAG", "sending players buyCard info");
                }
            }

            if(!(player instanceof GameHumanPlayer)){
                try{
                    Thread.sleep(900);}
                catch(InterruptedException e){
                    Log.i("LocalGame makeMove(): ", "Sleep error: " + e);
                }
            }

            return success;

        } else if (gameAction instanceof DominionEndTurnAction){

            DominionEndTurnAction action = (DominionEndTurnAction) gameAction;
            int playerIndex = getPlayerIdx(action.getPlayer());

            return state.endTurn(playerIndex);
        } else {
            return false;
        }
    }

    /**
     * Starts the game. Creates initial game state.
     * @param players The list of players in the game.
     */
    @Override
    public void start(GamePlayer[] players) {
        super.start(players);
        state = new DominionGameState(players.length, baseCards, shopCards);
    }


}

