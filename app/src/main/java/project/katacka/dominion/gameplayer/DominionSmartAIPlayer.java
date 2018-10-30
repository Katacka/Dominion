package project.katacka.dominion.gameplayer;

import android.view.View;

import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionSmartAIPlayer extends project.katacka.dominion.gameplayer.DominionComputerPlayer {
    public DominionSmartAIPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    public DominionSmartAIPlayer(String name, int numCards) {
        super(name);
    }

    public boolean playSmartTurn() {
        updateDeckInfo();
        playSmartActionPhase();
        playAllTreasures();
        playSmartBuyPhase();
        return true;
    }

    public boolean playSmartActionPhase() {
        project.katacka.dominion.gamedisplay.DominionPlayCardAction a;
        return true;
    }


    public boolean playSmartBuyPhase() {
        project.katacka.dominion.gamedisplay.DominionBuyCardAction a;
        return true;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }
}

