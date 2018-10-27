package project.katacka.dominion.gameplayer;

import android.view.View;

import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionHumanPlayer extends GameHumanPlayer {
    /**
     * constructor
     *
     * @param name
     * 			the player's name (e.g., "John")
     */
    public DominionHumanPlayer(String name) {
        super(name);
    }

    // sets this player as the GUI player (overrideable)
    public void setAsGui(GameMainActivity activity){

    }

    // sends a message to the player
    public void sendInfo(GameInfo info){

    }

    // start the player
    public void start(){

    }

    /**
     * Returns the GUI's top object; used for flashing.
     *
     * @return the GUI's top object.
     */
    public View getTopView(){
        return null;
    }

    /**
     * Callback-method implemented in the subclass whenever updated
     * state is received.
     *
     * @param info
     * 			the object representing the information from the game
     */
    public void receiveInfo(GameInfo info){

    }




}

