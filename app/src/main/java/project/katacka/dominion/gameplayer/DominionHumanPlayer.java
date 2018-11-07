package project.katacka.dominion.gameplayer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import project.katacka.dominion.MainActivity;
import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

public class DominionHumanPlayer extends GameHumanPlayer implements View.OnClickListener{
    int startPlayer = 3;
    //textviews
    //buttons
    private DominionGameState state;
    private LinearLayout tab1 = null;
    private LinearLayout tab2 = null;
    private LinearLayout tab3 = null;
    private LinearLayout tab4 = null;
    ConstraintLayout tabLayout = null;

    private TableLayout shopLayout = null;
    private ArrayList<TableRow> shopRows;
    private ArrayList<ConstraintLayout> shopPiles;

    private TableLayout baseLayout = null;
    private ArrayList<TableRow> baseRows;
    private ArrayList<ConstraintLayout> basePiles;

    private Resources res;

    private TextView tvPlayerCard = null;
    private Button bEndTurn = null;
    float tabInactiveVal;
    float tabActiveVal;
    GameMainActivity activity = null;


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
        this.activity = activity;
        activity.setContentView(R.layout.activity_main);
        tabLayout = activity.findViewById(R.id.Player_Tabs);
        tab1 = (LinearLayout) tabLayout.getChildAt(0);
        tab2 = (LinearLayout) tabLayout.getChildAt(1);
        tab3 = (LinearLayout) tabLayout.getChildAt(2);
        tab4 = (LinearLayout) tabLayout.getChildAt(3);
        //set default tab widths
        //set width of active player to tab active, set all others to inactive

        TypedValue outValueInactive = new TypedValue();
        //true means follow the resource if it references another resource
        activity.getResources().getValue(R.dimen.tabInactive, outValueInactive, true);
        tabInactiveVal = outValueInactive.getFloat();

        TypedValue outValueActive = new TypedValue();
        //true means follow the resource if it references another resource
        activity.getResources().getValue(R.dimen.tabActive, outValueActive, true);
        tabActiveVal = outValueActive.getFloat();

        updateTabs(startPlayer);
    }

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
        c.clone((ConstraintLayout) activity.findViewById(R.id.Player_Tabs));
        c.constrainPercentWidth(R.id.playerTab1, R.dimen.tabActive);


        shopLayout = activity.findViewById(R.id.Shop_Cards);
        shopRows = new ArrayList<TableRow>();
        for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
            shopRows.add((TableRow) shopLayout.getChildAt(i));
        }

        baseLayout = activity.findViewById(R.id.Base_Cards);
        baseRows = new ArrayList<TableRow>();
        for(int i = 0, j = baseLayout.getChildCount(); i < j; i++){
            baseRows.add((TableRow) baseLayout.getChildAt(i));
        }

        /*
        External Citation
        iterating through table layout
        https://stackoverflow.com/questions/3327599/get-all-tablerows-in-a-tablelayout
         */

        res = activity.getResources();
    }

    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState){
            state = (DominionGameState) info;

            /*ConstraintSet c = new ConstraintSet();
            //clone Player_tabs (tabs wrapper)constraints
            c.clone(tabLayout);

            c.constrainPercentWidth(R.id.playerTab1, R.dimen.tabInactive);
            c.constrainPercentWidth(R.id.playerTab2, R.dimen.tabInactive);
            c.constrainPercentWidth(R.id.playerTab3, R.dimen.tabInactive);
            c.constrainPercentWidth(R.id.playerTab4, R.dimen.tabInactive);
           */
            for(int i = 0; i< allPlayerNames.length; i++) {
                if (state.canMove(i)) {
                    /*
                    switch (i) {
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
                    */
                }
            }

            int m = 0;
            for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
                View shopRow = shopLayout.getChildAt(i);
                if(shopRow instanceof TableRow){
                    shopPiles = new ArrayList<ConstraintLayout>();
                    for (int k = 0; k < 5; k++) {
                        shopPiles.add((ConstraintLayout) ((TableRow) shopRow).getVirtualChildAt(k));
                    }
                    for (ConstraintLayout shopCard: shopPiles) {
                        DominionCardState cardState = state.getShopCards().get(m).getCard();
                        TextView cost = shopCard.findViewById(R.id.textViewCost);
                        cost.setText("" + cardState.getCost());
                        TextView title = shopCard.findViewById(R.id.textViewTitle);
                        title.setText(cardState.getTitle());
                        TextView amount = shopCard.findViewById(R.id.textViewAmount);
                        amount.setText("" + state.getShopCards().get(m).getAmount());
                        TextView type = shopCard.findViewById(R.id.textViewType);
                        type.setText(cardState.getType().toString());
                        ImageView image = shopCard.findViewById(R.id.imageViewArt);
                        String name = cardState.getPhotoId();
                        int resID = res.getIdentifier(name, "drawable", "project.katacka.dominion_card_back");
                        image.setImageResource(resID);
                        m++;
                    }
                }
            }

            int c = 0;
            for(int a = 0, b = baseLayout.getChildCount(); a < b; a++){
                View baseRow = baseLayout.getChildAt(a);
                if(baseRow instanceof TableRow){
                    basePiles = new ArrayList<ConstraintLayout>();
                    for (int k = 0; k < 2; k++) {
                        basePiles.add((ConstraintLayout) ((TableRow) baseRow).getVirtualChildAt(k));
                    }
                    for (ConstraintLayout shopCard: basePiles) {
                        DominionCardState cardState = state.getBaseCards().get(c).getCard();
                        TextView cost = shopCard.findViewById(R.id.textViewCost);
                        cost.setText("" + cardState.getCost());
                        TextView title = shopCard.findViewById(R.id.textViewTitle);
                        title.setText(cardState.getTitle());
                        TextView amount = shopCard.findViewById(R.id.textViewAmount);
                        amount.setText("" + state.getBaseCards().get(c).getAmount());
                        TextView type = shopCard.findViewById(R.id.textViewType);
                        type.setText(cardState.getType().toString());
                        ImageView image = shopCard.findViewById(R.id.imageViewArt);
                        String name = cardState.getPhotoId();
                        int resID = res.getIdentifier(name, "drawable", "project.katacka.dominion_card_back");
                        image.setImageResource(resID);
                        c++;
                    }
                }
            }
            /*
            External Citation
            setting imageview using string
            https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
            shows how to convert string to resource id to use to set image view
            */
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
     * this method gets called when the user clicks the die or hold button. It
     * creates a new PigRollAction or PigHoldAction and sends it to the game.
     *
     * @param button
     * 		the button that was clicked
     */
    public void onClick(View button) {
        if(button == bEndTurn){
            state.endTurn(state.getCurrentTurn());
        }
    }// onClick

}

//TODO: Hayden
/*
    use recieveInfo to update tab indentation
       need to know whose turn it is XXX
    manually call receiveInfo from gameState until written by Ryan
 */