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

        //Player tab code
        //TODO: Consider writing this into a multi-purpose tab function
        String[] names = {"Smart AI", "Dumb AI", "Player 1", "Player 2"};
        setNames(names);

        //Populates and displays the opponent cards
        displayImages(findViewById(R.id.Opponent_Cards), 5, R.drawable.opponent_card);

        //Populates the base cards (Treasure and Victory points)
        displayCards(findViewById(R.id.Base_Cards), R.layout.shop_card, 2, new Cards());

        //Populates and displays the shop cards
        displayCards(findViewById(R.id.Shop_Cards), R.layout.shop_card, 5, new Cards(10));

        //Populates and displays the player cards
        displayCards(findViewById(R.id.User_Cards), R.layout.player_card, new Cards(4));
    }

    //Generates and displays a row of images within a TableRow
    protected void displayImages(TableRow targetLayout, int totalCards, int imageID) {
        for (int i = 0; i < totalCards; i++){
            //Create, define and size image
            ImageView ivOpponentCard = new ImageView (this);
            ivOpponentCard.setScaleType(ImageView.ScaleType.FIT_XY);
            ivOpponentCard.setImageResource(imageID);

            //Modify image parameters relating to sizing within the parent layout
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;

            //Effectively renders the image
            targetLayout.addView(ivOpponentCard, trParams);
        }
    }

    //Generates and adds a gray filter on top of some pre-existing layout
    protected void displayEmptyStack(View cardLayout) {
        //Defines constraints and coloring for the gray overlay
        LinearLayout grayOverlay = new LinearLayout(this);
        grayOverlay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        grayOverlay.setBackgroundColor(Color.parseColor("#c8000000"));

        ((ConstraintLayout) cardLayout).addView(grayOverlay);
    }

    //
    protected View populateCardLayout(View cardLayout, Card card){
        //Sets the card title
        ((TextView) cardLayout.findViewById(R.id.textViewTitle))
                .setText(card.cTitle);

        //Sets the card image
        ((ImageView) cardLayout.findViewById(R.id.imageViewArt))
                .setImageResource(card.cPhotoId);

        //Sets the card text
        ((TextView) cardLayout.findViewById(R.id.textViewText))
                .setText(card.cText);

        //Sets the card cost text
        ((TextView) cardLayout.findViewById(R.id.textViewCost))
                .setText(String.format(Locale.US, "%d", card.cCost));

        //Sets the card amount text
        //TODO: Remove randomization used for example GUI
        int randomAmount = (int) (Math.random() * 11);
        ((TextView) cardLayout.findViewById(R.id.textViewAmount))
                .setText(String.format(Locale.US, "%d", randomAmount));

        //Sets the card type text
        ((TextView) cardLayout.findViewById(R.id.textViewType))
                .setText(card.cType);

        //Grays out the card layout if empty
        if (randomAmount == 0) {
            displayEmptyStack(cardLayout);
        }

        return cardLayout;
    }

    protected View populatePlayerCardLayout(View cardLayout, Card card) {
        //Reveals text for player cards, as room allows
        cardLayout.findViewById(R.id.textViewText)
                .setVisibility(View.VISIBLE);

        //Hides the card amount, as not relevant
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

    //Sets tab names
    protected void setNames(String[] names) {
        ConstraintLayout tabLayout = findViewById(R.id.Player_Tabs);
        for(int i = 0; i < names.length; i++) {
            ((TextView) tabLayout.getChildAt(i).findViewById(R.id.playerName)).setText(names[i]);
        }
    }


}
