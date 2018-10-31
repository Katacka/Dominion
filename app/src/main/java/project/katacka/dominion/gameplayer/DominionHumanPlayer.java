package project.katacka.dominion.gameplayer;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.TextView;

import project.katacka.dominion.MainActivity;
import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionGameState;

public class DominionHumanPlayer extends GameHumanPlayer{
    //textviews
    //buttons
    DominionGameState state;

    public DominionHumanPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    public DominionHumanPlayer(String name, int numCards) {
        super(name);
    }

    //TODO: Reference all actions properly
    public boolean playSimpleActionPhase() {
        return true;
    }

    public boolean playAllTreasures() {
        return true;
    }

    public boolean playSimpleBuyPhase() {
        return true;
    }

    public boolean quitGame() {
        return true;
    }

    public boolean endTurn() {
        return true;
    }


    public String toString(){
        String string = "CardView Name: " + super.name;
        return string;
    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        //draw things

        ConstraintLayout tabLayout = activity.findViewById(R.id.Player_Tabs);

        TextView tab1 = activity.findViewById(R.id.playerTab1);
        TextView tab2 = activity.findViewById(R.id.playerTab2);
        TextView tab3 = activity.findViewById(R.id.playerTab3);
        TextView tab4 = activity.findViewById(R.id.playerTab4);


        for(int i = 0; i< allPlayerNames.length; i++){
            if(i == state.getCurrentTurn()){

                switch(i) {
                    case 1:
                        //set width of active player to tab active, set all others to inactive
                        tab1.setWidth(R.dimen.tabActive);
                        tab2.setWidth(R.dimen.tabInactive);
                        tab3.setWidth(R.dimen.tabInactive);
                        tab4.setWidth(R.dimen.tabInactive);
                        break;
                    case 2:
                        tab1.setWidth(R.dimen.tabInactive);
                        tab2.setWidth(R.dimen.tabActive);
                        tab3.setWidth(R.dimen.tabInactive);
                        tab4.setWidth(R.dimen.tabInactive);
                        break;
                    case 3:
                        tab1.setWidth(R.dimen.tabInactive);
                        tab2.setWidth(R.dimen.tabInactive);
                        tab3.setWidth(R.dimen.tabActive);
                        tab4.setWidth(R.dimen.tabInactive);
                        break;
                    case 4:
                        tab1.setWidth(R.dimen.tabInactive);
                        tab2.setWidth(R.dimen.tabInactive);
                        tab3.setWidth(R.dimen.tabInactive);
                        tab4.setWidth(R.dimen.tabActive);
                        break;
                    default:
                        tab1.setWidth(R.dimen.tabActive);
                        tab2.setWidth(R.dimen.tabInactive);
                        tab3.setWidth(R.dimen.tabInactive);
                        tab4.setWidth(R.dimen.tabInactive);
                        break;
                }
            }

        }

        /*
             //Sets tab names
    protected void setNames(String[] names) {
        ConstraintLayout tabLayout = findViewById(R.id.Player_Tabs);
        for(int i = 0; i < names.length; i++) {
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(names[i]);
        }
    }
             */
    }

    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState){
            state = (DominionGameState) info;

            //((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(names[i]);



        }
    }

    @Override
    public View getTopView() {
        return null;
    }
}

//TODO: Hayden
/*
    use recieveInfo to update tab indentation
       need to know whose turn it is XXX
    manually call receiveInfo from gameState until written by Ryan
 */