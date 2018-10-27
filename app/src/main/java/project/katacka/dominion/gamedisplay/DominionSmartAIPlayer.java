package project.katacka.dominion.gamedisplay;

import android.view.View;

import project.katacka.dominion.gameframework.GameComputerPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;

public class DominionSmartAIPlayer extends GameComputerPlayer {
    protected Cards mDeckPile;
    protected Cards mDiscardPile;
    protected Cards mHand;
    protected int mActions;
    protected int mBuys;
    protected int mGold;
    protected int mVP;

    protected DominionSmartAIPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    protected DominionSmartAIPlayer(String name, int numCards) {
        super(name);

        this.mDeckPile = new Cards(numCards);
        this.mDiscardPile = new Cards(0);
        this.mHand = new Cards(0);

        this.mActions = 0;
        this.mBuys = 0;
        this.mGold = 0;
        this.mVP = 0;
    }

    public boolean playSmartTurn() {
        updateDeckInfo();
        playSmartActionPhase();
        playAllTreasures();
        playSmartBuyPhase();
        return true;
    }

    public boolean updateDeckInfo() {
        return true;
    }

    public boolean playSmartActionPhase() {
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean playSmartBuyPhase() {
        return true;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }

    @Override
    public void setAsGui(GameMainActivity activity) {

    }

    @Override
    public void receiveInfo(GameInfo info) {

    }
}

