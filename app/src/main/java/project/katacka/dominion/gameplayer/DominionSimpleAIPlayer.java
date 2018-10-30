package project.katacka.dominion.gameplayer;

import android.view.View;

import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionSimpleAIPlayer extends DominionComputerPlayer {

    public DominionSimpleAIPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    public DominionSimpleAIPlayer(String name, int numCards) {
        super(name);
    }

    public boolean playSimpleTurn() {
        updateDeckInfo();
        playSimpleActionPhase();
        playAllTreasures();
        playSimpleBuyPhase();
        endTurn();
        return true;
    }


    //TODO: Reference all actions properly
    public boolean playSimpleActionPhase() {
        return true;
    }

    public boolean playSimpleBuyPhase() {
        return true;
    }

    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

