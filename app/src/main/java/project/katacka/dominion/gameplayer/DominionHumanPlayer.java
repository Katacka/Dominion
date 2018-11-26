package project.katacka.dominion.gameplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import project.katacka.dominion.R;
import project.katacka.dominion.gamedisplay.DominionBuyCardAction;
import project.katacka.dominion.gamedisplay.DominionEndTurnAction;
import project.katacka.dominion.gamedisplay.DominionPlayAllAction;
import project.katacka.dominion.gamedisplay.DominionPlayCardAction;
import project.katacka.dominion.gameframework.GameHumanPlayer;
import project.katacka.dominion.gameframework.GameMainActivity;
import project.katacka.dominion.gameframework.GamePlayer;
import project.katacka.dominion.gameframework.actionMsg.GameAction;
import project.katacka.dominion.gameframework.infoMsg.GameInfo;
import project.katacka.dominion.gameframework.infoMsg.IllegalMoveInfo;
import project.katacka.dominion.gamestate.DominionCardPlace;
import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gameframework.infoMsg.NotYourTurnInfo;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionPlayerState;
import project.katacka.dominion.gamestate.DominionShopPileState;

/**
 * TODO: Javadoc comment here
 */
public class DominionHumanPlayer extends GameHumanPlayer {

    //TODO: Remove unused variables

    private final int ILLEGAL_TOAST_DURATION = 250;
    private final double CARD_WIDTH_RATIO = 0.66;

    private float tabInactiveVal;
    private float tabActiveVal;

    private DominionGameState state;
    private LinearLayout tab1 = null;
    private LinearLayout tab2 = null;
    private LinearLayout tab3 = null;
    private LinearLayout tab4 = null;
    private ConstraintLayout tabLayout = null;

    private TableLayout shopLayout = null;
    private ArrayList<TableRow> shopRows; //TODO: Not read from
    private ArrayList<ConstraintLayout> shopPiles;

    private TableLayout baseLayout = null;
    private ArrayList<TableRow> baseRows; //TODO: Not read from
    private ArrayList<ConstraintLayout> basePiles;

    private LinearLayout cardRow = null;
    private ArrayList<DominionCardState> hand;

    private int handOffset;

    private int pos;

    private ConstraintLayout mainLayout;

    private Resources res;

    private Button bEndTurn = null;
    private Button bPlayAll = null;

    private GameMainActivity activity = null;

    private TextView tvActions;
    private TextView tvBuys;
    private TextView tvTreasure;

    private TextView tvOppDraw;
    private ImageView oppDraw;
    private TextView tvOppDiscard;
    private ConstraintLayout oppDiscardLayout;
    private ImageView oppEmptyDiscard;

    private TextView tvDrawCount;
    private TextView tvDiscardCount;

    private TextView bMenu;

    private ImageView drawPile;
    private ConstraintLayout discardPile;
    private ImageView emptyDiscardPile;
    private ImageView emptyDrawPile;

    private DominionPlayerState playerState;

    //TODO: Delete this
    private final GamePlayer thisPlayer = this;

    private final Handler myHandler;
    private Drawable background;
    private Toast illegalMoveToast;

    public DominionHumanPlayer(String name) {
        this(name, 5); //Default starting hand size is 5
    }

    //TODO: Merge constructors
    public DominionHumanPlayer(String name, int numCards) {
        super(name);
        myHandler = new Handler();
    }

    //TODO: Reference all actions properly
    //TODO: Remove these methods
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
        tab1 = (LinearLayout) tabLayout.getChildAt(0);
        tab2 = (LinearLayout) tabLayout.getChildAt(1);
        tab3 = (LinearLayout) tabLayout.getChildAt(2);
        tab4 = (LinearLayout) tabLayout.getChildAt(3);

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
        shopRows = new ArrayList<>();
        for(int i = 0, j = shopLayout.getChildCount(); i < j; i++){
            shopRows.add((TableRow) shopLayout.getChildAt(i));
        }

        baseLayout = activity.findViewById(R.id.Base_Cards);
        baseRows = new ArrayList<>();
        for(int i = 0, j = baseLayout.getChildCount(); i < j; i++){
            baseRows.add((TableRow) baseLayout.getChildAt(i));
        }

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
        oppDraw = activity.findViewById(R.id.ivOppDrawCard);
        tvOppDiscard = activity.findViewById(R.id.textViewOppDiscard);
        oppDiscardLayout = activity.findViewById(R.id.oppDiscardCard);
        oppDiscardLayout.setRotation(180);
        oppEmptyDiscard = activity.findViewById(R.id.oppDiscardEmpty);
        tvOppDraw.setText("5");
        tvOppDiscard.setText("0");

        drawPile = activity.findViewById(R.id.ivDrawCard);
        discardPile = activity.findViewById(R.id.imageViewDiscard);
        emptyDiscardPile = activity.findViewById(R.id.imageViewDiscardEmpty);
        emptyDrawPile = activity.findViewById(R.id.imageViewDrawEmpty);

        res = activity.getResources();

        mainLayout = activity.findViewById(R.id.constraintMain);
        background = mainLayout.getBackground();

        //set listeners
        bMenu = activity.findViewById(R.id.bMenu);
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

    //TODO: Set correctly
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

        for(int i = 0; i < state.getDominionPlayers().length; i++){
            if(i == activePlayer){
                c.constrainPercentWidth(playerTabs[i], tabActiveVal);
            } else {
                c.constrainPercentWidth(playerTabs[i], tabInactiveVal);
            }
        }
        c.applyTo(tabLayout);
        c.clone((ConstraintLayout) activity.findViewById(R.id.Player_Tabs));
        c.constrainPercentWidth(R.id.playerTab1, R.dimen.tabActive);
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
        DominionCardState card = deck.getLastDiscard(); //TODO: Remove this line or use variable

        tvDrawCount.setText(Integer.toString(drawSize));
        tvDiscardCount.setText(Integer.toString(discardSize));

        if(drawSize == 0){
            drawPile.setVisibility(View.INVISIBLE);
            emptyDrawPile.setVisibility(View.VISIBLE);

        } else {
            drawPile.setVisibility(View.VISIBLE);
            emptyDrawPile.setVisibility(View.INVISIBLE);
        }
        if(discardSize == 0){
            discardPile.setVisibility(View.INVISIBLE);
            emptyDiscardPile.setVisibility(View.VISIBLE);
        } else {
            discardPile.setVisibility(View.VISIBLE);
            emptyDiscardPile.setVisibility(View.INVISIBLE);
            updateCardView(discardPile, playerState.getDeck().getLastDiscard(), -1);
        }
    }

    /**
     * Updates player's hand
     */
    private void updatePlayerHand(){
        hand = state.getDominionPlayer(playerNum).getDeck().getHand();
        cardRow = activity.findViewById(R.id.User_Cards);

        ConstraintLayout layout;
        DominionCardState card;

        int childCount = cardRow.getChildCount();
        for(int i = 0; i < childCount; i++){
            layout = (ConstraintLayout) cardRow.getChildAt(i);
            if(i < hand.size()){
                layout.setOnClickListener(handClickListener);
                card = hand.get(i);
                updateCardView(layout, card, -1);
                layout.setVisibility(View.VISIBLE);
            } else {
                layout.setVisibility(View.GONE);
            }
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
     * Updates the shop piles
     */
    private void updateShopPiles(){
        boolean isTurn = playerNum == state.getCurrentTurn();

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
                    setBuyable(shopCard, isTurn && cardState.getCost() <= state.getTreasure() && amount > 0);
                    if (amount == 0) setGrayedOut(shopCard);
                    m++;
                }
            }
        }
    }

    private void setGrayedOut(ConstraintLayout shopCard) {
        /*
         * External Citation
         * Date: 11/18/18
         * Problem: Trying to use PorterDuffColorFilter
         * Source: https://developer.android.com/reference/android/graphics/PorterDuff.Mode
         * Solution: Used PorterDuff Multiply mode to make color filter
         */
        ColorFilter grayFilter = new PorterDuffColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        ((ImageView) shopCard.findViewById(R.id.imageViewArt)).setColorFilter(grayFilter);
        shopCard.getBackground().setColorFilter(grayFilter);
        ((ImageView) shopCard.findViewById(R.id.imageViewCost)).setColorFilter(grayFilter);
        ((ImageView) shopCard.findViewById(R.id.imageViewAmount)).setColorFilter(grayFilter);
    }

    private void setBuyable(ConstraintLayout shopCard, boolean canBuy){
        if (canBuy && state.getBuys() >= 1){
            shopCard.setBackgroundResource(R.drawable.dominion_card_border_green);
        } else {
            shopCard.setBackgroundResource(R.drawable.dominion_card_border_squared);
        }
    }

    /**
     * Updates the base piles
     */
    private void updateBasePiles(){
        /*
         * External Citation
         * Date: 11/5/18
         * Problem: setting imageview using string
         * Source: https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
         * Solution: shows how to convert string to resource id to use to set image view
         */
        basePiles = new ArrayList<>();
        boolean isTurn = playerNum == state.getCurrentTurn();

        //TODO: Clean up. (Just comments maybe?)
        int c = 0, start = 0, end = 2;
        for(int a = 0; a < baseLayout.getChildCount(); a++){
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
                    setBuyable(baseCard, isTurn && cardState.getCost() <= state.getTreasure() && amount > 0);
                    if (amount == 0) setGrayedOut(baseCard);
                    c++;
                }
                start = start+2;
                end = end+2;
            }
        }
    }

    private void updateOppDrawDiscard(int player){
        if (player == playerNum) return;
        DominionDeckState currPlayerDeck = state.getDominionPlayer(player).getDeck();
        int drawSize = currPlayerDeck.getDrawSize();
        tvOppDraw.setText(Integer.toString(drawSize));
        if (drawSize > 0){
            oppDraw.setImageResource(R.drawable.dominion_opponent_card_back);
        } else {
            oppDraw.setImageResource(R.drawable.dominion_draw);
        }
        int discardSize = currPlayerDeck.getDiscardSize();
        tvOppDiscard.setText(Integer.toString(discardSize));
        if (discardSize > 0) {
            updateCardView(oppDiscardLayout, currPlayerDeck.getLastDiscard(), -1);
            oppDiscardLayout.setVisibility(View.VISIBLE);
            oppEmptyDiscard.setVisibility(View.INVISIBLE);
        }
        else {
            oppDiscardLayout.setVisibility(View.INVISIBLE);
            oppEmptyDiscard.setVisibility(View.VISIBLE);
        }
    }

    private void updateOppHand(int player){
        //Finds how many cards to display
        int handSize;
        if (player == playerNum){
            handSize = 5;
        } else {
            handSize = state.getDominionPlayer(player).getDeck().getHandSize();
        }

        ConstraintLayout oppCardsLayout = activity.findViewById(R.id.Opponent_Cards);
        oppCardsLayout.removeAllViews();

        //Creates new image views and puts them in layout
        ImageView[] cards = new ImageView[handSize];
        for (int i = 0; i < handSize; i++){
            cards[i] = new ImageView(activity);
            cards[i].setScaleType(ImageView.ScaleType.FIT_START);
            cards[i].setImageResource(R.drawable.dominion_opponent_card_back);
            cards[i].setId(View.generateViewId()); //Needed to allow constraints
            oppCardsLayout.addView(cards[i]);
        }

        ConstraintSet set = new ConstraintSet();
        set.clone(oppCardsLayout);
        float biasMultiplier = Math.min(0.2f, 1/(float)handSize); //How far apart the cards should be, as a percentage
        @IdRes int layoutID = oppCardsLayout.getId();

        //Add constraints to every card image
        for (int i = 0; i < handSize; i++){
            ImageView card = cards[i];
            @IdRes int id = card.getId();

            //Constrain to all four edges of the layout
            set.connect(id, ConstraintSet.LEFT, layoutID, ConstraintSet.LEFT);
            set.connect(id, ConstraintSet.RIGHT, layoutID, ConstraintSet.RIGHT);
            set.connect(id, ConstraintSet.TOP, layoutID, ConstraintSet.TOP);
            set.connect(id, ConstraintSet.BOTTOM, layoutID, ConstraintSet.BOTTOM);

            //Have it fill the height it can
            set.constrainHeight(id, ConstraintSet.MATCH_CONSTRAINT);
            //Have it be wide enough to maintain aspect ration
            set.constrainWidth(id, ConstraintSet.WRAP_CONTENT);

            //Position the card in the correct position
            //This is the entire reason we use a constraint layout
            set.setHorizontalBias(id, i*biasMultiplier);
        }
        set.applyTo(oppCardsLayout);
    }

    //TODO: fix to update tabs more accurately for attack turns
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
            } else {
                updateTabs(state.getCurrentTurn());
            }

            updateTurnInfo(state.getActions(), state.getBuys(), state.getTreasure());
            updateDrawDiscard();
            updateShopPiles();
            updateBasePiles();
            updatePlayerHand();

            updateOppDrawDiscard(state.getCurrentTurn());
            updateOppHand(state.getCurrentTurn());

            //set listeners
            bEndTurn.setOnClickListener(handClickListener);
            bPlayAll.setOnClickListener(handClickListener);

            bMenu.setOnClickListener(menuClickListener);

            /*
            External Citation
            setting imageview using string
            https://stackoverflow.com/questions/5254100/how-to-set-an-imageviews-image-from-a-string
            shows how to convert string to resource id to use to set image view
            */ //TODO: Move citation to correct place

        } else if(info instanceof NotYourTurnInfo) {
            //TODO: actually do something if not player turn
            Log.i("DominionHumanPlayer: receiveInfo", "Not your turn.");

        } else if (info instanceof IllegalMoveInfo){
            flash(Color.RED, ILLEGAL_TOAST_DURATION);
            Log.i("HumanPlayer", "Illegal move");
            if (illegalMoveToast != null){
                illegalMoveToast.cancel();
            }
            illegalMoveToast = Toast.makeText(activity, "Illegal move", Toast.LENGTH_SHORT);
            illegalMoveToast.show();
        }

        //TODO: Move citation to correct place
        /* External Citation:
        Date: Nov 4, 2018
        Source: https://stackoverflow.com/questions/44749481/how-to-change-constraint-layouts-child-views-constraints-programatically#44750506
        Problem: wanted to set tab widths programatically
        Solution: Use ConstraintSet to clone ConstraintLayout width and set tabs relative to that ConstraintLayout
         */
    }//updateTabs

    private final View.OnClickListener handClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            if(v == null) { return; }

            GameAction action;
            if(v == bPlayAll){
               Log.i("DomHumPlayer: HandClickListener onClick: ", "Play all button clicked");

               action = new DominionPlayAllAction(thisPlayer);
            } else if(v == bEndTurn) {
                Log.i("TAG: ", "" + state.getCurrentTurn());
                Log.i("DomHumPlayer: onClick", "End turn button clicked.");

                action = new DominionEndTurnAction(thisPlayer);
            } else if(v instanceof ConstraintLayout){ //v is one of the playerCards
                Log.i("DomHumPlayer: onClick", "Player's card button clicked.");

                int index = cardRow.indexOfChild(v);
                action = new DominionPlayCardAction(thisPlayer, index);
            } else {
                Log.i("DomHumPlayer: onClick", "Player card button clicked.");

                int toPlayIdx = ((LinearLayout)v.getParent()).indexOfChild(v);
                action = new DominionPlayCardAction(thisPlayer, toPlayIdx);
            }
            game.sendAction(action);
        }
    };

    private final View.OnClickListener shopClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //GameAction action = null;
            //boolean isBaseCard = basePiles.contains(v); TODO: This was commented, below was added.
            DominionCardPlace place;
            if (basePiles.contains(v)) place = DominionCardPlace.BASE_CARD;
            else place = DominionCardPlace.SHOP_CARD;

            TableRow parentView = (TableRow) v.getParent();
            //is the table row the top row or bottom row
            TableLayout parentLayout = (TableLayout) parentView.getParent();
            int offSet = parentLayout.indexOfChild(parentView) * parentView.getVirtualChildCount();
            int desiredIndex = parentView.indexOfChild(v) + offSet;

            game.sendAction(new DominionBuyCardAction(thisPlayer, desiredIndex, place));
        }
    };

    private final View.OnClickListener menuClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ArrayList<Integer> imageList = new ArrayList<Integer>();

            imageList.add(R.drawable.rules_manual);
            imageList.add(R.drawable.rules_play_card);
            imageList.add(R.drawable.rules_buy_card);
            imageList.add(R.drawable.rules_longpress);
            imageList.add(R.drawable.rules_end_turn);

            Log.i("DomHumPlayer: onClick", "Menu clicked.");
            pos = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setPositiveButton("Next", null);
            builder.setNegativeButton("Previous", null);

            final AlertDialog dialog = builder.create();
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.dialog_help, null);

            dialog.setView(dialogLayout);

            ImageView image = dialogLayout.findViewById(R.id.image_help);

            try{image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Log.i("DomHumPlayer: onClick: Try catch", "Position is" + pos);
                image.setImageResource(imageList.get(pos));}catch(OutOfMemoryError e){
                image.setImageBitmap(null);
            }

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("DomHumPlayer: onClick", "Position is" + pos);
                            Log.i("DomHumPlayer: onClick", "Next clicked.");
                            if(pos< (imageList.size()-1)){
                                pos++;
                            }
                            Log.i("DomHumPlayer: onClick", "Position is" + pos);
                            try{image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                Log.i("DomHumPlayer: onClick: Try catch", "Position is" + pos);
                                image.setImageResource(imageList.get(pos));}catch(OutOfMemoryError e){
                                image.setImageBitmap(null);
                            }
                        }
                    });
                    Button prevButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    prevButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("DomHumPlayer: onClick", "Position is" + pos);
                            Log.i("DomHumPlayer: onClick", "prev clicked.");
                            if(pos > 0){
                                pos--;
                            }
                            Log.i("DomHumPlayer: onClick", "Position is" + pos);
                            try{image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                Log.i("DomHumPlayer: onClick: Try catch", "Position is" + pos);
                                image.setImageResource(imageList.get(pos));}catch(OutOfMemoryError e){
                                image.setImageBitmap(null);
                            }
                        }
                    });
                }
            });

            dialog.show();

            Window window = dialog.getWindow();
            double width = mainLayout.getWidth() * 0.75;
            window.setLayout((int) (width), (int) (width * 0.71));
        }
    };

    private final View.OnLongClickListener shopLongClickListener = new View.OnLongClickListener(){
      @Override
      public boolean onLongClick(View v) {
          TableRow parentView = (TableRow) v.getParent();
          //is the table row the top row or bottom row
          TableLayout parentLayout = (TableLayout) parentView.getParent();
          int offSet = parentLayout.indexOfChild(parentView) * parentView.getVirtualChildCount();
          int desiredIndex = parentView.indexOfChild(v) + offSet;

          //get dominion shop pile state
          DominionShopPileState pileState = state.getShopCards().get(desiredIndex);

          final Dialog dialog = new Dialog(activity);
          dialog.setContentView(populateCardLayout(pileState));
          dialog.show();
          Window window = dialog.getWindow();
          double height = mainLayout.getHeight() * 0.50;
          window.setLayout((int) (height * CARD_WIDTH_RATIO), (int) height);

          return true;
      }
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

    @Override
    protected void flash(int color, int duration) {
        View top = this.getTopView();
        if (top == null) return;

        //This part is different
        //Background is not saved - this is done when GUI is set
        //This prevents a race condition where the "flashed" background gets saved
        top.setBackgroundColor(color);
        Log.i("Human", "Starting flash");

        myHandler.postDelayed(new Unflasher(), duration);
    }

    /**
    * helper-class to finish a "flash".
     * Making our own so that a image background can be supported
    *
    */
    private class Unflasher implements Runnable {

        // constructor
        public Unflasher() {

        }

        // method to run at the appropriate time: sets background color
        // back to the original
        public void run() {
            View top = getTopView();
            if (top == null) return;
            top.setBackground(background);
            Log.i("Human", "Ending flash");
        }
    }
}