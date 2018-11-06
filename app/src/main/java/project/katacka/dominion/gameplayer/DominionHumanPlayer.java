package project.katacka.dominion.gameplayer;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import project.katacka.dominion.MainActivity;
import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionGameState;

public class DominionHumanPlayer extends GameHumanPlayer implements View.OnClickListener{
    private int startPlayer = 3;
    private float tabInactiveVal;
    private float tabActiveVal;

    private DominionGameState state;

    private LinearLayout tab1 = null;
    private LinearLayout tab2 = null;
    private LinearLayout tab3 = null;
    private LinearLayout tab4 = null;
    ConstraintLayout tabLayout = null;
    private Button bEndTurn = null;

    private GameMainActivity activity = null;

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
        //remember activity
        this.activity = activity;

        //set display based XML resource
        activity.setContentView(R.layout.activity_main);

        //init all the things
        tabLayout = activity.findViewById(R.id.Player_Tabs);
        tab1 = (LinearLayout) tabLayout.getChildAt(0);
        tab2 = (LinearLayout) tabLayout.getChildAt(1);
        tab3 = (LinearLayout) tabLayout.getChildAt(2);
        tab4 = (LinearLayout) tabLayout.getChildAt(3);
        bEndTurn = activity.findViewById(R.id.buttonEndTurn);

        //tab set up stuff
        TypedValue outValueInactive = new TypedValue();
        TypedValue outValueActive = new TypedValue();
        //true means follow the resource if it references another resource
        activity.getResources().getValue(R.dimen.tabInactive, outValueInactive, true);
        activity.getResources().getValue(R.dimen.tabActive, outValueActive, true);
        tabInactiveVal = outValueInactive.getFloat();
        tabActiveVal = outValueActive.getFloat();

        //init tabs to starting config
        updateTabs(startPlayer);

        //set listeners
        bEndTurn.setOnClickListener(this);
    }


    //TODO: fix to update tabs more accurately for attack turns
    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState) {
            state = (DominionGameState) info;

            if (state.getIsAttackTurn()) {
                updateTabs(state.getAttackTurn());
            } else {
                updateTabs(state.getCurrentTurn());
            }
        }
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        // by default, we do nothing

        //Sets tab names
        for(int i = 0; i < allPlayerNames.length; i++) {
            //((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(allPlayerNames[i]);\
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(allPlayerNames[i]);
        }
    }

    @Override
    public View getTopView() {
        return null;
    }

    /**
     * sets tabs so the tab of the current player is 100% of the constraint width
     * other players are 85% of constraint width
     * @param activePlayer player to set active tab for
     *
     */
    public void updateTabs(int activePlayer){
        ConstraintSet c = new ConstraintSet();
        //clone Player_tabs (tabs wrapper)constraints
        c.clone(tabLayout);
        //set default individual tab widths as percentages of the parents constraints
        //by default, tab1 is active

        switch(activePlayer){
            case 0:
                c.constrainPercentWidth(R.id.playerTab1, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 1:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 2:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
            case 3:
                c.constrainPercentWidth(R.id.playerTab1, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabActiveVal);
                break;
            default:
                c.constrainPercentWidth(R.id.playerTab1, tabActiveVal);
                c.constrainPercentWidth(R.id.playerTab2, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab3, tabInactiveVal);
                c.constrainPercentWidth(R.id.playerTab4, tabInactiveVal);
                break;
        }
        c.applyTo(tabLayout);

        /* External Citation:
        Date: Nov 4, 2018
        Source: https://stackoverflow.com/questions/44749481/how-to-change-constraint-layouts-child-views-constraints-programatically#44750506
        Problem: wanted to set tab widths programatically
        Solution: Use ConstraintSet to clone ConstraintLayout width and set tabs relative to that ConstraintLayout
         */
    }//updateTabs




    /**
     * this method gets called when the user clicks the die or hold button. It
     * creates a new PigRollAction or PigHoldAction and sends it to the game.
     *
     * @param button
     * 		the button that was clicked
     */
    //TODO: FIX TAB UPDATES, currently only work on the first time user clicks end turn button
    public void onClick(View button) {
        Log.i("DomHumPlayer: onClick", "End turn button clicked.");
        GameAction action = null;
        if(button == null){ return; }
        else if(button == bEndTurn){
          action = new DominionEndTurnAction(this);
            Log.i("TAG: ", "" + state.getCurrentTurn());
            /*
            state.endTurn(state.getCurrentTurn());
            updateTabs(state.getCurrentTurn());
             */
        //} else if(){
        }
        game.sendAction(action);
    }// onClick'

    public boolean draw(){

        return true;
    }

}

//TODO: Hayden
/*
    use recieveInfo to update tab indentation
       need to know whose turn it is XXX
    manually call receiveInfo from gameState until written by Ryan
 */