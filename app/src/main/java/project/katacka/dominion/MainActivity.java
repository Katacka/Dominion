package project.katacka.dominion;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removes the title and notification bars respectively
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Player Tab code
        //TODO: Consider writing this into a multi-purpose tab function
        String[] names = {"Smart AI", "Dumb AI", "Player 1", "Player 2"};
        setNames(names);

        //Populates and displays the opponent cards
        displayCards(findViewById(R.id.Opponent_Cards), 5, new int[]{R.drawable.opponent_card});

        //Populates and displays the player cards
        displayCards(findViewById(R.id.User_Cards), R.layout.player_card, new Cards(4));

        //Populates and displays the shop cards
        displayCards(findViewById(R.id.Shop_Cards), R.layout.shop_card, 5, new Cards(10));

        //Populates the base cards (Treasure and Victory points)
        displayCards(findViewById(R.id.Base_Cards), R.layout.shop_card, 2, new Cards());
    }

    //Currently used to display opponent cards
    //TODO: Wrap this functionality into populateCard/displayCards
    protected void displayCards(TableRow targetLayout, int totalCards, int[] imageID) {
        for (int i = 0; i < totalCards; i++){
            ImageView ivOpponentCard = new ImageView (this);
            ivOpponentCard.setScaleType(ImageView.ScaleType.FIT_XY);
            ivOpponentCard.setImageDrawable(getResources().getDrawable(imageID[0]));
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;
            trParams.gravity = Gravity.TOP;

            ivOpponentCard.setLayoutParams(trParams);
            targetLayout.addView(ivOpponentCard);
        }
    }

    protected View populateCardLayout(View cardLayout, Card card){

        ((TextView) cardLayout.findViewById(R.id.textViewTitle))
                .setText(card.cTitle);

        ((ImageView) cardLayout.findViewById(R.id.imageViewArt))
                .setImageResource(card.cPhotoId);

        ((TextView) cardLayout.findViewById(R.id.textViewText))
                .setText(card.cText);

        ((TextView) cardLayout.findViewById(R.id.textViewCost))
                .setText(String.format(Locale.US, "%d", card.cCost));

        //TODO: Remove randomization used for example GUI
        int randomAmount = (int) (Math.random() * 11);
        ((TextView) cardLayout.findViewById(R.id.textViewAmount))
                .setText(String.format(Locale.US, "%d", randomAmount));

        ((TextView) cardLayout.findViewById(R.id.textViewType))
                .setText(card.cType);

        if (randomAmount == 0) {
            LinearLayout grayOverlay = new LinearLayout(this);
            grayOverlay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            grayOverlay.setBackgroundColor(Color.parseColor("#c8000000"));
            ((ConstraintLayout) cardLayout).addView(grayOverlay);
        }

            return cardLayout;
    }

    protected View populatePlayerCardLayout(View cardLayout, Card card) {
        cardLayout.findViewById(R.id.textViewText)
                .setVisibility(View.VISIBLE);

        cardLayout.findViewById(R.id.frameLayout2)
                .setVisibility(View.INVISIBLE);

        return populateCardLayout(cardLayout, card);
    }

    //Used to generate player cards
    protected void displayCards(ViewGroup cardsLayout, int layoutID, Cards cards) {
        //Declares and defines parameters used to define parent-child relationship attributes
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT);
        trParams.weight = 1.0f;
        trParams.setMargins(5,5,5,5);

        int totalCards = cards.totalCards;

        //Iterates over the TableLayout's TableRows populating each one
        for (int i = 0; i < totalCards; i++) {
            ViewGroup cardLayout = (ViewGroup) getLayoutInflater().inflate(layoutID, null);
            cardsLayout.addView(populatePlayerCardLayout(cardLayout, cards.cardStack.get(i)), trParams);
        }
    }

    //Used to generate shop cards
    protected void displayCards(ViewGroup cardsLayout, int layoutID, int cardsPerRow, Cards cards) {
        int numRows = cardsLayout.getChildCount();
        int indexOffset = 0;

        //Iterates over the TableLayout's TableRows populating each one
        for (int i = 0; i < numRows; i++, indexOffset+=cardsPerRow) {
            TableRow cardRow = (TableRow) cardsLayout.getChildAt(i);

            for (int j = 0; j < cardsPerRow; j++) {
                //cardRow.getChildAt(j).setLayoutParams(trParams);
                populateCardLayout(cardRow.getChildAt(j), cards.cardStack.get(j + indexOffset));
            }
        }
    }

    protected void setNames(String[] names) {
        if (names.length != 4) return;

        ((TextView) findViewById(R.id.playerTab1).findViewById(R.id.playerName)).setText(names[0]);
        ((TextView) findViewById(R.id.playerTab2).findViewById(R.id.playerName)).setText(names[1]);
        ((TextView) findViewById(R.id.playerTab3).findViewById(R.id.playerName)).setText(names[2]);
        ((TextView) findViewById(R.id.playerTab4).findViewById(R.id.playerName)).setText(names[3]);
    }

    protected void setBaseCards(){
        TableLayout baseCards = findViewById(R.id.Base_Cards);

        TableRow top = (TableRow) baseCards.getChildAt(0);

        View cardLayoutCopper = top.getChildAt(0);
        Card cardDataCopper = new Card( "Copper", R.drawable.dominion_copper, "+1 Gold", 0, "TREASURE");
        populateCardLayout(cardLayoutCopper, cardDataCopper);

        View cardLayoutEstate = top.getChildAt(1);
        Card cardDataEstate = new Card("Estate", R.drawable.dominion_estate, "1 Victory Point", 2, "VICTORY");
        populateCardLayout(cardLayoutEstate, cardDataEstate);

        TableRow mid = (TableRow) baseCards.getChildAt(1);

        View cardLayoutSilver = mid.getChildAt(0);
        Card cardDataSilver = new Card("Silver", R.drawable.dominion_silver, "+2 Gold", 3, "TREASURE");
        populateCardLayout(cardLayoutSilver, cardDataSilver);

        View cardLayoutDuchy = mid.getChildAt(1);
        Card cardDataDuchy = new Card("Duchy", R.drawable.dominion_duchy, "3 Victory Points", 5, "VICTORY");
        populateCardLayout(cardLayoutDuchy, cardDataDuchy);

        TableRow bot = (TableRow) baseCards.getChildAt(2);

        View cardLayoutGold = bot.getChildAt(0);
        Card cardDataGold = new Card("Gold", R.drawable.dominion_gold, "+3 Gold", 6, "TREASURE");
        populateCardLayout(cardLayoutGold, cardDataGold);

        View cardLayoutProvince = bot.getChildAt(1);
        Card cardDataProvince = new Card("Province", R.drawable.dominion_province, "6 Victory Points", 8, "VICTORY");
        populateCardLayout(cardLayoutProvince, cardDataProvince);
    }
}
