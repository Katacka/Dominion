package project.katacka.dominion.gameplayer;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayAllAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * Class controls gui and represent information for current human player
 * @author Ryan Regier, Julian Donovan, Ashika Mulagada, Hayden Liao
 */
public class DominionHumanPlayer extends GameHumanPlayer{
    private Integer grayOverlayIdx;

    private int handOffset;

    private float tabInactiveVal;
    private float tabActiveVal;

    private DominionGameState state;
    private ConstraintLayout tabLayout = null;

    private TableLayout shopLayout = null;
    private ArrayList<ConstraintLayout> shopPiles;

    private TableLayout baseLayout = null;
    private ArrayList<ConstraintLayout> basePiles;

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
    private GameHumanPlayer thisPlayer;

    public DominionHumanPlayer(String name) {
        super(name);
        grayOverlayIdx = Integer.MAX_VALUE;
        handOffset = 0;
        thisPlayer = this;
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

        shopLayout = activity.findViewById(R.id.Shop_Cards);
        baseLayout = activity.findViewById(R.id.Base_Cards);

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

        res = activity.getResources();

        bEndTurn.setOnClickListener(endTurnClickListener);
        bPlayAll.setOnClickListener(playAllClickListener);
    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        //Sets tab names
        for(int i = 0; i < tabLayout.getChildCount(); i++) {
            if (i < allPlayerNames.length) {
                ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(allPlayerNames[i]);
            }
            else tabLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }

    @Override
    public View getTopView() {
        return mainLayout;
    }

    /**
     * sets tabs so the tab of the current player is 100% of the constraint width
     * other players are 85% of constraint width
     *
     */
    private void updateTabs(){
        /**
         * External Citation:
         * Date: Nov 4, 2018
         * Source: https://stackoverflow.com/questions/44749481/how-to-change-constraint-layouts-child-views-constraints-programatically#44750506
         * Problem: wanted to set tab widths programatically
         * Solution: Use ConstraintSet to clone ConstraintLayout width and set tabs relative to that ConstraintLayout
         */
        ConstraintSet c = new ConstraintSet();
        c.clone(tabLayout);
        //set default individual tab widths as percentages of the parents constraints
        //by default, tab1 is active

        int[] playerTabs = {R.id.playerTab1, R.id.playerTab2, R.id.playerTab3, R.id.playerTab4};
        for(int i = 0; i < state.getDominionPlayers().length; i++) {
            if (state.canMove(i)) c.constrainPercentWidth(playerTabs[i], tabActiveVal);
            else c.constrainPercentWidth(playerTabs[i], tabInactiveVal);
        }

        c.applyTo(tabLayout);
    }

    /**
     * Updates textViews that provide player info
     * @param actions The current number of player's actions
     * @param buys The current number of player's buys
     * @param treasure The current number of player's treasures
     */
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

    /**
     * Updates player's draw and discard piles
     */
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

    /**
     * Updates opponent's draw and discard pile
     * @param player The current player who's draw and discard should be drawn.
     */
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

    /**
     * Updates opponent's hand pile
     * @param player The current player who's hand should be drawn.
     */
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
            /**
             * External citation
             * Date: 11/08/2018
             * Problem: Everything needed an ID to create a constraint set
             * Resource:
             *  https://stackoverflow.com/questions/50526880/constraint-layout-layout-crashing-all-children-of-constraint-layout-should-hav#50870367
             * Solution: Generate View ID function used.
             */
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

    /**
     * Updates player's hand
     */
    private void updatePlayerHand(){
        hand = state.getDominionPlayer(playerNum).getDeck().getHand();
        LinearLayout cardRow = activity.findViewById(R.id.User_Cards);

        ConstraintLayout layout;
        //for every item in hand up to five,
        int childCount = cardRow.getChildCount();
        for(int i = 0; i < childCount; i++) {
            layout = (ConstraintLayout) cardRow.getChildAt(i);
            if (i + handOffset < hand.size()) {
                layout.setOnClickListener(handClickListener);
                layout.setOnTouchListener(handSwipeListener);
                DominionCardState card = hand.get(i + handOffset);

                //if the card exists
                //read xml and update corresponding textViews and such
                updateCardView(layout, card, -1);
                layout.setVisibility(View.VISIBLE);
            }
            else { //card does not exist
                layout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Updates the base piles
     */
    private void updateBasePiles(){
        /**
         * External Citation
         * setting imageview using string
         * https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
         * shows how to convert string to resource id to use to set image view
         */
        basePiles = new ArrayList<>();
        int c = 0, start = 0, end = 2;
        for(int a = 0, b = baseLayout.getChildCount(); a < b; a++){
            View baseRow = baseLayout.getChildAt(a);

            if(baseRow instanceof TableRow){
                for (int k = 0; k < 2; k++) {
                    basePiles.add((ConstraintLayout) ((TableRow) baseRow).getVirtualChildAt(k));
                }

                for (int r=start; r<end; r++) {
                    ConstraintLayout baseCard = basePiles.get(r);
                    baseCard.setOnClickListener(shopClickListener);
                    DominionCardState cardState = state.getBaseCards().get(c).getCard();
                    int amount = state.getBaseCards().get(c).getAmount();
                    updateCardView(baseCard, cardState, amount);
                    if (amount == 0) displayEmptyStack(basePiles.get(r));
                    c++;
                }
                start = start+2;
                end = end+2;
            }
        }

    }

    /**
     * Updates the shop piles
     */
    private void updateShopPiles(){
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
                    shopCard.setOnClickListener(shopClickListener);
                    shopCard.setOnLongClickListener(shopLongClickListener);
                    DominionCardState cardState = state.getShopCards().get(m).getCard();
                    int amount = state.getShopCards().get(m).getAmount();
                    updateCardView(shopCard, cardState, amount);
                    if (amount == 0) displayEmptyStack(shopCard);
                    m++;
                }
            }
        }
    }

    /**
     * Generates and adds a gray filter on top of some pre-existing layout
     * @param cardLayout Describes the card being grayed out
     */
    private void displayEmptyStack(View cardLayout) {
        //Defines constraints and coloring for the gray overlay
        LinearLayout grayOverlay = new LinearLayout(activity);
        grayOverlay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        grayOverlay.setBackgroundColor(Color.parseColor("#c8000000"));
        if (grayOverlayIdx < ((ConstraintLayout) cardLayout).getChildCount()) ((ConstraintLayout) cardLayout).removeViewAt(grayOverlayIdx);
        grayOverlayIdx = ((ConstraintLayout) cardLayout).getChildCount();
        ((ConstraintLayout) cardLayout).addView(grayOverlay, grayOverlayIdx);
    }

    //TODO: fix to update tabs more accurately for attack turns
    //TODO: Break into more functions

    /**
     * Receives game state info and calls update methods to reflect state
     * @param info Game information
     */
    @Override
    public void receiveInfo(GameInfo info) {
        //get updated info
        if(info instanceof DominionGameState){
            state = (DominionGameState) info;
            playerState = state.getDominionPlayer(playerNum);

            //update tabs to reflect turn
            if (state.getIsAttackTurn()) {
                updateTabs();
                updateOppDrawDiscard(state.getAttackTurn());
                updateOppHand(state.getAttackTurn());
            } else {
                updateTabs();
                updateOppDrawDiscard(state.getCurrentTurn());
                updateOppHand(state.getCurrentTurn());
            }

            updateTurnInfo(state.getActions(), state.getBuys(), state.getTreasure());
            updateDrawDiscard();
            updateShopPiles();
            updateBasePiles();
            updatePlayerHand();

        } else if(info instanceof NotYourTurnInfo) {
            //TODO: actually do something if not player turn
            Log.i("DominionHumanPlayer: recieveInfo", "Not your turn.");
        }
    }

    /**
     * Plays all treasures in hand for current player
     */
    private View.OnClickListener playAllClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "PlayAll button clicked.");
        game.sendAction(new DominionPlayAllAction(this));
    };

    /**
     * Ends turn for current player
     */
    private View.OnClickListener endTurnClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "End turn button clicked.");
        game.sendAction(new DominionEndTurnAction(this));
    };

    /**
     * Plays the card tapped on
     */
    private View.OnClickListener handClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "Player card button clicked.");
        int targetIdx = ((LinearLayout) v.getParent()).indexOfChild(v);
        int handOffsetTemp = handOffset;
        handOffset = (hand.size() - handOffset > 5) ? handOffset : Math.max(handOffset - 1, 0);
        game.sendAction(new DominionPlayCardAction(thisPlayer, targetIdx + handOffsetTemp));
    };

    /**
     * Handles navigation of the player hand
     */
    private OnSwipeTouchListener handSwipeListener = new OnSwipeTouchListener(activity) {
        @Override
        public void onSwipeRight(float distX) {
            handOffset = Math.max(handOffset - 1, 0);
            Log.e("a", "onSwipeLeft: " + handOffset);
            updatePlayerHand();
        }

        @Override
        public void onSwipeLeft(float distX) {
            handOffset = Math.min(handOffset + 1, Math.max(hand.size() - 5, 0));
            Log.e("a", "onSwipeRight: " + handOffset);
            updatePlayerHand();
        }
    };

    /**
     * Buys the card in shop tapped on
     */
    private View.OnClickListener shopClickListener = (View v) -> {
        Log.i("DomHumPlayer: onClick", "Shop card button clicked.");
        boolean isBaseCard = basePiles.contains(v);
        TableRow parentView = ((TableRow) v.getParent());
        int colOffset = ((TableLayout) parentView.getParent()).indexOfChild(parentView) * parentView.getChildCount();
        int targetIdx =  parentView.indexOfChild(v) + colOffset;
        game.sendAction(new DominionBuyCardAction(this, targetIdx, isBaseCard));
    };

    /**
     * Displays a dialog with card and description for the card in shop that is long pressed
     */
    private View.OnLongClickListener shopLongClickListener = (View v) -> { //Not applied to base cards
        Log.i("DomHumPlayer: onClick", "Shop card button long-clicked.");
        TableRow parentView = ((TableRow) v.getParent());

        int colOffset = ((TableLayout) parentView.getParent()).indexOfChild(parentView) * parentView.getChildCount();
        int targetIdx =  parentView.indexOfChild(v) + colOffset;

        DominionShopPileState pile = state.getShopCards().get(targetIdx);

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(populateCardLayout(pile));
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(500, 750); //TODO: put width, height in dimen (or aspect ratio?)

        return true;
    };

    /**
     * Creates new cardView and populates it (for card description dialogue)
     * @param pile Shop pile that will be used for card description dialogue
     * @return cardView to be displayed
     */
    protected View populateCardLayout(DominionShopPileState pile){
        ConstraintLayout cardView = (ConstraintLayout) LayoutInflater.from(activity).inflate(R.layout.player_card, mainLayout, false);
        DominionCardState card = pile.getCard();
        updateCardView(cardView, card, pile.getAmount());
        return cardView;
    }
}