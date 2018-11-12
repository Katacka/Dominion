package project.katacka.dominion.gameplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import project.katacka.dominion.MainActivity;
import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.Cards;
import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayAllAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * TODO: Javadoc comment here
 */
public class DominionHumanPlayer extends GameHumanPlayer implements View.OnClickListener{

    private float tabInactiveVal;
    private float tabActiveVal;

    private DominionGameState state;
    private ConstraintLayout tabLayout = null;

    private TableLayout shopLayout = null;
    //private ArrayList<TableRow> shopRows;
    private ArrayList<ConstraintLayout> shopPiles;

    private TableLayout baseLayout = null;
    //private ArrayList<TableRow> baseRows;
    private ArrayList<ConstraintLayout> basePiles;

    //private TableRow cardRow = null;
    private ArrayList<DominionCardState> hand;

    private Resources res;

    private Button bEndTurn = null;
    private Button bPlayAll = null;

    private GameMainActivity activity = null;

    private TextView tvActions;
    private TextView tvBuys;
    private TextView tvTreasure;

    private TextView tvDrawCount;
    private TextView tvDiscardCount;

    private TextView tvOppDiscard;
    private TextView tvOppDraw;
    private ConstraintLayout oppDiscardLayout;

    private ImageView drawPile;
    private ConstraintLayout discardPile;

    private ConstraintLayout mainLayout;

    private DominionPlayerState playerState;

    public DominionHumanPlayer(String name) {
        super(name);
    }

    public String toString(){
        return "CardView Name: " + super.name;
    }

    @Override
    public void setAsGui(GameMainActivity activity) {
        //remember activity
        this.activity = activity;

        //set display based XML resource
        activity.setContentView(R.layout.activity_main);

        //TODO figure out if we need this
        //handler = new ShopPileHandler(state);
        //detector = new GestureDetector(activity, handler);

        //init all the things
        tabLayout = activity.findViewById(R.id.Player_Tabs);
        bEndTurn = activity.findViewById(R.id.buttonEndTurn);
        bPlayAll = activity.findViewById(R.id.buttonPlayAll);

        //tab set up stuff
        TypedValue outValueInactive = new TypedValue();
        TypedValue outValueActive = new TypedValue();

        //true means follow the resource if it references another resource
        activity.getResources().getValue(R.dimen.tabInactive, outValueInactive, true);
        activity.getResources().getValue(R.dimen.tabActive, outValueActive, true);
        tabInactiveVal = outValueInactive.getFloat();
        tabActiveVal = outValueActive.getFloat();

        //making array list of tablerows for shop and base cards
        shopLayout = activity.findViewById(R.id.Shop_Cards);
        baseLayout = activity.findViewById(R.id.Base_Cards);
        /*shopRows = new ArrayList<TableRow>();
        for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
            shopRows.add((TableRow) shopLayout.getChildAt(i));
        }

        baseRows = new ArrayList<TableRow>();
        for(int i = 0, j = baseLayout.getChildCount(); i < j; i++){
            baseRows.add((TableRow) baseLayout.getChildAt(i));
        }*///TODO: Is this code needed? (Uncomment var declarations above as well)

        /*
        External Citation
        iterating through table layout
        https://stackoverflow.com/questions/3327599/get-all-tablerows-in-a-tablelayout
         */

        tvActions = activity.findViewById(R.id.tvActions);
        tvBuys = activity.findViewById(R.id.tvBuys);
        tvTreasure = activity.findViewById(R.id.tvTreasures);
        updateTurnInfo(0, 0, 0);

        tvDrawCount = activity.findViewById(R.id.textViewDrawCount);
        tvDiscardCount = activity.findViewById(R.id.textViewDiscardCount);
        tvDrawCount.setText("0");
        tvDiscardCount.setText("0");

        tvOppDraw = activity.findViewById(R.id.textViewOppDraw);
        tvOppDiscard = activity.findViewById(R.id.textViewOppDiscard);
        oppDiscardLayout = activity.findViewById((R.id.oppDiscardCard));
        oppDiscardLayout.setRotation(180);
        tvOppDraw.setText("0");
        tvOppDiscard.setText("0");

        drawPile = activity.findViewById(R.id.ivDrawCard);
        discardPile = activity.findViewById(R.id.imageViewDiscard);

        mainLayout = activity.findViewById(R.id.constraintMain);

        for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
            View shopRow = shopLayout.getChildAt(i);
            //should always be true
            if(shopRow instanceof TableRow){
                //cards are ConstraintLayouts in XML
                shopPiles = new ArrayList<ConstraintLayout>();
                for (int k = 0; k < ((TableRow) shopRow).getChildCount(); k++) {
                    shopPiles.add((ConstraintLayout) ((TableRow) shopRow).getVirtualChildAt(k));
                }
                for (ConstraintLayout shopCard: shopPiles) {
                    shopCard.setOnClickListener(shopClickListener);
                    shopCard.setOnLongClickListener(shopLongClickListener);
                }
            }
        }

        for(int i = 0, j = baseLayout.getChildCount(); i < j; i++){
            View baseRow = baseLayout.getChildAt(i);
            //should always be true
            if(baseRow instanceof TableRow){
                //cards are ConstraintLayouts in XML
                basePiles = new ArrayList<ConstraintLayout>();
                for (int k = 0; k < ((TableRow) baseRow).getChildCount(); k++) {
                    basePiles.add((ConstraintLayout) ((TableRow) baseRow).getVirtualChildAt(k));
                }
                for (ConstraintLayout baseCard: basePiles) {
                    baseCard.setOnClickListener(shopClickListener);
                }
            }
        }

        res = activity.getResources();

        //set listeners
        bEndTurn.setOnClickListener(endTurnClickListener);
        bPlayAll.setOnClickListener(playAllClickListener);
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        //Sets tab names
        for(int i = 0; i < allPlayerNames.length; i++) {
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(allPlayerNames[i]);
        }
    }

    @Override
    public View getTopView() {
        return mainLayout;
    }

    /**
     * sets tabs so the tab of the current player is 100% of the constraint width
     * other players are 85% of constraint width
     * @param activePlayer player to set active tab for
     *
     */
    private void updateTabs(int activePlayer){
        ConstraintSet c = new ConstraintSet();
        //clone Player_tabs (tabs wrapper)constraints
        c.clone(tabLayout);
        //set default individual tab widths as percentages of the parents constraints
        //by default, tab1 is active

        int[] playerTabs = {R.id.playerTab1, R.id.playerTab2, R.id.playerTab3, R.id.playerTab4};
        for(int i = 0; i < state.getDominionPlayers().length; i++) {
            if (playerNum == i) c.constrainPercentWidth(playerTabs[i], tabActiveVal);
            else c.constrainPercentWidth(playerTabs[i], tabInactiveVal);
        }

        c.applyTo(tabLayout);
        //c.clone((ConstraintLayout) activity.findViewById(R.id.Player_Tabs));
        //c.constrainPercentWidth(R.id.playerTab1, R.dimen.tabActive);
    }

    private void updateTurnInfo(int actions, int buys, int treasure){
        /**
         * External Citation
         * Date: 11/6/18
         * Problem: Needed to combine text and number in text view
         * Resource:
         *  https://developer.android.com/guide/topics/resources/string-resource#java
         * Solution: Set up XML strings properly to allow format parameters when read
         */
        tvActions.setText(activity.getString(R.string.actions, actions));
        tvBuys.setText(activity.getString(R.string.buys, buys));
        tvTreasure.setText(activity.getString(R.string.treasure, treasure));
    }

    private void updateDrawDiscard(){
        DominionDeckState deck = playerState.getDeck();
        int drawSize = deck.getDrawSize();
        int discardSize = deck.getDiscardSize();
        DominionCardState card = deck.getLastDiscard();

        tvDrawCount.setText(Integer.toString(drawSize));
        tvDiscardCount.setText(Integer.toString(discardSize));

        if(drawSize == 0){
            drawPile.setVisibility(View.INVISIBLE);
        } else {
            drawPile.setVisibility(View.VISIBLE);
        }
        if(discardSize == 0){
            discardPile.setVisibility(View.INVISIBLE);
        } else {
            discardPile.setVisibility(View.VISIBLE);
            updateCardView(discardPile, card, -1);
        }
    }

    /**
     * Draws the card at the given view
     * @param cardView The view to draw the card
     * @param card The card to display
     * @param num The amount of cards. If -1, amount is hidden.
     */
    private void updateCardView(ConstraintLayout cardView, DominionCardState card, int num){
        TextView cost = cardView.findViewById(R.id.textViewCost);
        cost.setText(Integer.toString(card.getCost()));

        TextView title = cardView.findViewById(R.id.textViewTitle);
        title.setText(card.getTitle());

        FrameLayout layout = cardView.findViewById(R.id.frameLayoutAmount);
        if (num == -1){
            layout.setVisibility(View.INVISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            TextView amount = cardView.findViewById(R.id.textViewAmount);
            amount.setText(Integer.toString(num));
        }

        TextView description = cardView.findViewById(R.id.tvDescription);
        description.setText(card.getFormattedText());

        TextView type = cardView.findViewById(R.id.textViewType);
        type.setText(card.getType().toString());

        ImageView image = cardView.findViewById(R.id.imageViewArt);

        String name = card.getPhotoId();
        int resID = res.getIdentifier(name, "drawable", "project.katacka.dominion_card_back");
        image.setImageResource(resID);
    }

    private void updateOppDrawDiscard(int player){
        if (player == playerNum) return;
        DominionDeckState currPlayerDeck = state.getDominionPlayer(player).getDeck();
        tvOppDraw.setText(Integer.toString(currPlayerDeck.getDrawSize()));
        int discardSize = currPlayerDeck.getDiscardSize();
        tvOppDiscard.setText(Integer.toString(discardSize));
        if (discardSize > 0) {
            updateCardView(oppDiscardLayout, currPlayerDeck.getLastDiscard(), -1);
            oppDiscardLayout.setVisibility(View.VISIBLE);
        }
        else {
            oppDiscardLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void updateOppHand(int player){
        int handSize;
        if (player == playerNum){
            handSize = 5;
        } else {
            handSize = state.getDominionPlayer(player).getDeck().getHandSize();
        }
        ConstraintLayout oppCardsLayout = activity.findViewById(R.id.Opponent_Cards);
        oppCardsLayout.removeAllViews();
        ImageView[] cards = new ImageView[handSize];
        for (int i = 0; i < handSize; i++){
            cards[i] = new ImageView(activity);
            cards[i].setScaleType(ImageView.ScaleType.FIT_XY);
            cards[i].setImageResource(R.drawable.dominion_opponent_card_back);
            cards[i].setId(View.generateViewId());
            oppCardsLayout.addView(cards[i]);
        }
        ConstraintSet set = new ConstraintSet();
        set.clone(oppCardsLayout);
        float biasMultiplier = Math.min(1/5.0f, 1/(float)handSize);
        @IdRes int layoutID = oppCardsLayout.getId();
        for (int i = 0; i < handSize; i++){
            ImageView card = cards[i];
            @IdRes int id = card.getId();

            set.connect(id, ConstraintSet.LEFT, layoutID, ConstraintSet.LEFT);
            set.connect(id, ConstraintSet.RIGHT, layoutID, ConstraintSet.RIGHT);
            set.connect(id, ConstraintSet.TOP, layoutID, ConstraintSet.TOP);
            set.connect(id, ConstraintSet.BOTTOM, layoutID, ConstraintSet.BOTTOM);


            set.constrainHeight(id, ConstraintSet.MATCH_CONSTRAINT);
            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);

            set.setHorizontalBias(id, i*biasMultiplier);
        }
        set.applyTo(oppCardsLayout);
    }

    //TODO: fix to update tabs more accurately for attack turns
    //TODO: Break into more functions
    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState){
            state = (DominionGameState) info;
            playerState = state.getDominionPlayer(playerNum);

            //update tabs to reflect turn
            //updateTabs(state.getCurrentTurn());
            if (state.getIsAttackTurn()) {
                updateTabs(state.getAttackTurn());
                updateOppDrawDiscard(state.getAttackTurn());
                updateOppHand(state.getAttackTurn());
            } else {
                updateTabs(state.getCurrentTurn());
                updateOppDrawDiscard(state.getCurrentTurn());
                updateOppHand(state.getCurrentTurn());
            }

            updateTurnInfo(state.getActions(), state.getBuys(), state.getTreasure());
            updateDrawDiscard();

            //Display shop
            int m = 0;
            for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
                View shopRow = shopLayout.getChildAt(i);

                //should always be true
                if(shopRow instanceof TableRow){

                    //cards are ConstraintLayouts in XML
                    shopPiles = new ArrayList<>();
                    for (int k = 0; k < 5; k++) {
                        shopPiles.add((ConstraintLayout) ((TableRow) shopRow).getVirtualChildAt(k));
                    }

                    for (ConstraintLayout shopCard: shopPiles) {
                        DominionCardState cardState = state.getShopCards().get(m).getCard();
                        int amount = state.getShopCards().get(m).getAmount();
                        updateCardView(shopCard, cardState, amount);
                        m++;
                        //TODO: Change display with empty piles
                    }
                }
            }

            //display base cards
            basePiles = new ArrayList<ConstraintLayout>();
            int c = 0, start = 0, end = 2;
            for(int a = 0, b = baseLayout.getChildCount(); a < b; a++){
                View baseRow = baseLayout.getChildAt(a);
                if(baseRow instanceof TableRow){
                    for (int k = 0; k < 2; k++) {
                        basePiles.add((ConstraintLayout) ((TableRow) baseRow).getVirtualChildAt(k));
                    }
                    for (int r=start; r<end; r++) {
                        ConstraintLayout baseCard = basePiles.get(r);
                        DominionCardState cardState = state.getBaseCards().get(c).getCard();
                        int amount = state.getBaseCards().get(c).getAmount();
                        updateCardView(baseCard, cardState, amount);
                        c++;
                    }
                    start = start+2;
                    end = end+2;
                }
            }
            /*
            External Citation
            setting imageview using string
            https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
            shows how to convert string to resource id to use to set image view
            */ //TODO: Move to correct place

            ////////display player hand////////////
            //get hand
            hand = state.getDominionPlayer(playerNum).getDeck().getHand();
            LinearLayout cardRow = activity.findViewById(R.id.User_Cards);

            /*
            ArrayList<DominionCardState> testhand = new ArrayList<DominionCardState>();

            testhand.add(0, state.getBaseCards().get(1).getCard());
            testhand.add(1, state.getBaseCards().get(2).getCard());
            testhand.add(2, state.getShopCards().get(8).getCard());
            testhand.add(3, state.getShopCards().get(9).getCard());
            testhand.add(4, state.getBaseCards().get(3).getCard());
            */

            ConstraintLayout layout;
            //for every item in hand up to five,
            int childCount = cardRow.getChildCount();
            for(int i = 0; i < childCount; i++) {
                layout = (ConstraintLayout) cardRow.getChildAt(i);
                if (i < hand.size()) {
                    layout.setOnClickListener(handClickListener);
                    DominionCardState card = hand.get(i);

                    //if the card exists
                    //read xml and update corresponding textviews and such
                    updateCardView(layout, card, -1);
                    layout.setVisibility(View.VISIBLE);
                }
                else { //card does not exist
                    layout.setVisibility(View.GONE);
                }
            }

            //Update treasure, actions, and buys


        } else if(info instanceof NotYourTurnInfo) {
            //TODO: actually do something if not player turn
            Log.i("DominionHumanPlayer: recieveInfo", "Not your turn.");
        }

        //TODO: Move citation to correct place
        /* External Citation:
        Date: Nov 4, 2018
        Source: https://stackoverflow.com/questions/44749481/how-to-change-constraint-layouts-child-views-constraints-programatically#44750506
        Problem: wanted to set tab widths programatically
        Solution: Use ConstraintSet to clone ConstraintLayout width and set tabs relative to that ConstraintLayout
         */
    }//updateTabs

    /**
     *
     * @param v
     * 		the button that was clicked
     */
    public void onClick(View v) { //TODO remove
        /*GameAction action = null;
        int index = 0;
        if(v == null){ return; }
        else if(v instanceof Button){
            if(v == bEndTurn){
                action = new DominionEndTurnAction(this);
                Log.i("TAG: ", "" + state.getCurrentTurn());
                Log.i("DomHumPlayer: onClick", "End turn button clicked.");
            }
        }
        else if(v instanceof ConstraintLayout){
            Log.i("DomHumPlayer: onClick", "Player's card button clicked.");
            TextView title = v.findViewById(R.id.textViewTitle);
            String titleString = title.getText().toString();

            for (int i = 0; i<hand.size(); i++) {
                if(hand.get(i).getTitle().equals(titleString)){
                    index = i;
                }
            }
            action = new DominionPlayCardAction(this, index);
        }

        game.sendAction(action);*/
    }// onClick'

    public boolean draw(){

        return true;
    }

    private View.OnClickListener playAllClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "PlayAll button clicked.");
        game.sendAction(new DominionPlayAllAction(this));
    };

    private View.OnClickListener endTurnClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "End turn button clicked.");
        game.sendAction(new DominionEndTurnAction(this));
    };

    private View.OnClickListener handClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "Player card button clicked.");
        int targetIdx = ((LinearLayout) v.getParent()).indexOfChild(v);
        game.sendAction(new DominionPlayCardAction(this, targetIdx));
    };

    private View.OnClickListener shopClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "Shop card button clicked.");
        boolean isBaseCard = basePiles.contains(v); //TODO: Fix, since this boolean is always false
        TableRow parentView = ((TableRow) v.getParent());
        int colOffset = ((TableLayout) parentView.getParent()).indexOfChild(parentView) * parentView.getChildCount();
        int targetIdx =  parentView.indexOfChild(v) + colOffset;
        game.sendAction(new DominionBuyCardAction(this, targetIdx, isBaseCard));
    };

    private View.OnLongClickListener shopLongClickListener = (View v) -> { //Not applied to base cards
        Log.i("DomHumPlayer: onClick", "Shop card button long-clicked.");
        TableRow parentView = ((TableRow) v.getParent());
        //parentView.removeView(v);

        int colOffset = ((TableLayout) parentView.getParent()).indexOfChild(parentView) * parentView.getChildCount();
        int targetIdx =  parentView.indexOfChild(v) + colOffset;

        DominionShopPileState pile = state.getShopCards().get(targetIdx);

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(populateCardLayout(pile));
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout(500, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setLayout(500, 750); //TODO: put width, height in dimen (or aspect ratio?)

        return true;
    };

    protected View populateCardLayout(DominionShopPileState pile){
        ConstraintLayout cardView = (ConstraintLayout) LayoutInflater.from(activity).inflate(R.layout.player_card, mainLayout, false);
        DominionCardState card = pile.getCard();
        updateCardView(cardView, card, pile.getAmount());
        return cardView;
    }
}