package project.katacka.dominion;

import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Removes the title and notification bars respectively
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Ryan's Player Tab code
        //TODO: Consider writing this into a multi-purpose tab function
        String[] names = {"Smart AI", "Dumb AI", "Player 1", "Player 2"};
        setNames(names);

        //Populates and displays the opponent cards
        displayCards(findViewById(R.id.Opponent_Cards), 5, new int[]{R.drawable.opponent_card});

        //Populates and displays the player cards
        displayCards(findViewById(R.id.User_Cards), R.layout.player_card, new Cards(3), false, true);

        //Populates and displays the Shop cards
        displayCards(findViewById(R.id.Shop_Cards), R.layout.shop_card, new Cards(10), true, false);

    }

    //Currently used to display opponent cards
    //TODO: Wrap this functionality into populateCard/displayCards
    protected void displayCards(TableRow targetLayout, int totalCards, int[] imageID) {
        for (int i = 0; i < totalCards; i++){
            ImageView ivOpponentCard = new ImageView (this);
            //ivOpponentCard.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
            ivOpponentCard.setImageDrawable(getResources().getDrawable(imageID[0]));
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;
            trParams.gravity = Gravity.TOP;
            //ivOpponentCard.layout
            ivOpponentCard.setLayoutParams(trParams);
            targetLayout.addView(ivOpponentCard);
        }
    }

    protected ViewGroup populateCard(int layoutID, Card cardData, Boolean isVisible){
        //Inflates and stores a reference to the XML card layout
        ViewGroup cardLayout = (ViewGroup) getLayoutInflater().inflate(layoutID, null);

        //Setting the card's title
        ((TextView) cardLayout.findViewById(R.id.textViewTitle)).setText(cardData.cTitle);

        //Setting the card's image
        ((ImageView) cardLayout.findViewById(R.id.imageViewArt)).setImageResource(cardData.cPhotoId);

        //Setting the card's text and text visibility
        TextView cardText = cardLayout.findViewById(R.id.textViewText);
        cardText.setText(cardData.cText);
        if (isVisible) cardText.setVisibility(View.VISIBLE);

        //Setting the card's cost
        //((TextView) cardLayout.findViewById(R.id.constraintChild).findViewById(R.id.frameLayout).findViewById(R.id.textViewCost)).setText(cardData.cCost);

        //Setting the card's type
        ((TextView) cardLayout.findViewById(R.id.textViewType)).setText(cardData.cType);

        return cardLayout;
    }

    protected void displayCards(ViewGroup cardLayout, int layoutID, Cards cards, Boolean isTableLayout, Boolean isVisible) {
        //Declares and defines parameters used to define parent-child relationship attributes
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        trParams.weight = 1.0f;
        trParams.setMargins(5,5,5,5);

        //Renders the function more extensuble allowing for cardLayout to be a TableLayout or TableRow
        int numRows = (isTableLayout) ? cardLayout.getChildCount() : 1;

        //Iterates over the TableLayout's TableRows populating each one
        for (int i = 0; i < numRows; i++) {
            for (int j = (i * 5); j < (cards.totalCards/numRows) + (i * 5); j++) {
                if (isTableLayout) ((TableRow) cardLayout.getChildAt(i)).addView(populateCard(layoutID, cards.cardStack.get(j), isVisible), trParams);
                else cardLayout.addView(populateCard(layoutID, cards.cardStack.get(j), isVisible), trParams);
            }
        }
    }

    /*protected void displayCards(TableRow cardRow, int layoutID, Cards cards) {
        TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        trParams.weight = 1.0f;

        for (int j = 0; j < cards.totalCards; j++) {
            cardRow.addView(populateCard(layoutID, cards.cardStack.get(j)), trParams);
        }
    }*/

    /*protected void displayCards(TableRow targetLayout, int totalCards, Cards cards) {
        for (int i = 0; i < totalCards; i++){
            final RelativeLayout rlCard = new RelativeLayout(this);
            TableRow.LayoutParams trParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            trParams.weight = 1.0f;
            trParams.setMargins(10, 10, 10, 10);
            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFADB7C1); //white background
            border.setStroke(3, 0xFF000000); //black border with full opacity
            rlCard.setBackground(border);
            rlCard.setLayoutParams(trParams);

            RelativeLayout.LayoutParams rlParam0 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam0.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            rlParam0.setMargins(3, 3, 3, 0);

            //Card Title
            final TextView tvCardName = new TextView(this);
            tvCardName.setText(cards.cardStack.get(i).cTitle);
            tvCardName.setTypeface(null, Typeface.BOLD);
            tvCardName.setTextSize(18);
            tvCardName.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            tvCardName.setGravity(Gravity.CENTER_HORIZONTAL);
            tvCardName.setId(1);
            rlCard.addView(tvCardName, rlParam0);

            RelativeLayout.LayoutParams rlParam1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam1.addRule(RelativeLayout.BELOW, tvCardName.getId());
            rlParam1.setMargins(3, 0, 3, 0);


            //Card Image
            final ImageView ivCard = new ImageView (this);
            ivCard.setImageDrawable(getResources().getDrawable(cards.cardStack.get(i).cPhotoId));
            ivCard.setId(4);
            ivCard.setAdjustViewBounds(true);
            //ivCard.setScaleType(ImageView.ScaleType.FIT_XY);
            rlCard.addView(ivCard, rlParam1);

            RelativeLayout.LayoutParams rlParam2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam2.addRule(RelativeLayout.BELOW, ivCard.getId());
            rlParam2.setMargins(3, 0, 3, 0);


            //Card Text
            final TextView tvCardText = new TextView(this);
            tvCardText.setText(cards.cardStack.get(i).cText);
            tvCardText.setId(2);

            rlCard.addView(tvCardText, rlParam2);

            RelativeLayout.LayoutParams rlParam3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlParam3.addRule(rlCard.ALIGN_PARENT_BOTTOM, tvCardText.getId());
            rlParam3.setMargins(3, 0, 3, 3);


            //Card Type
            final TextView tvCardType = new TextView(this);
            tvCardType.setText(cards.cardStack.get(i).cType);
            tvCardType.setId(3);
            tvCardType.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            tvCardType.setGravity(Gravity.CENTER_HORIZONTAL);
            rlCard.addView(tvCardType, rlParam3);

            //rlParams.addRule(RelativeLayout.BELOW, ivCard.getId());

            targetLayout.addView(rlCard);
        }
    }*/

    protected void setNames(String[] names) {
        if (names.length != 4) return;

        ((TextView) (findViewById(R.id.playerTab1).findViewById(R.id.playerName))).setText(names[0]);
        ((TextView) (findViewById(R.id.playerTab2).findViewById(R.id.playerName))).setText(names[1]);
        ((TextView) (findViewById(R.id.playerTab3).findViewById(R.id.playerName))).setText(names[2]);
        ((TextView) (findViewById(R.id.playerTab4).findViewById(R.id.playerName))).setText(names[3]);
    }
}
