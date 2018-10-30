package project.katacka.dominion.gamedisplay;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import project.katacka.dominion.R;

public class Cards {
    protected ArrayList<CardView> cardViewStack;
    protected int totalCards;
    protected final static int uniqueCards = 10;

    public Cards() {
        this.cardViewStack = new ArrayList<>(6);
        this.totalCards = 6;
        initializeBaseCards();
    }

    public Cards(int totalCards) {
        this.cardViewStack = new ArrayList<>(totalCards);
        this.totalCards = totalCards;
        generateStack(); //Populates cardViewStack
    }

    //Credit: https://stackoverflow.com/questions/4307273/how-can-i-create-and-display-an-arraylist-of-random-numbers-in-java
    private void generateStack(){
        //Gson gson = new Gson(); //ToDo: Finish GSON implementation
        //CardView card = gson.fromJson(R.raw.shop_cards, CardView.class);
        ThreadLocalRandom.current().ints(0, uniqueCards)
                .distinct().limit(totalCards)
                .forEach(randomInt -> initializeRandomCards(randomInt));
    }

    /*private void initializeRandomCard(int randomCard) {

        CardView card
    }*/

    private void initializeRandomCards(int randomCard) {
        switch(randomCard) {
            case(0): //Festival
                cardViewStack.add(new CardView("Festival", R.drawable.dominion_festival, "+2 Actions\n+1 Buy\n+2 Gold", 5, "ACTION", 10));
                break;
            case(1): //Merchant
                cardViewStack.add(new CardView("Merchant", R.drawable.dominion_merchant, "+1 CardView\n+1 Action\nThe first time you play a Silver this turn, +1 Gold", 3, "ACTION", 10));
                break;
            case(2): //Remodel
                cardViewStack.add(new CardView("Remodel", R.drawable.dominion_remodel, "Trash a card from your hand. Gain a card costing up to 2 Gold more than it", 4, "ACTION", 10));
                break;
            case(3): //Throne Room
                cardViewStack.add(new CardView("Throne Room", R.drawable.dominion_throne_room, "You may play an Action card from your hand twice", 4, "ACTION", 10));
                break;
            case(4): //Artisan
                cardViewStack.add(new CardView("Artisan", R.drawable.dominion_artisan, "Gain a card to your hand costing up to 5 Gold. Put a card from your hand onto your deck", 6, "ACTION", 10));
                break;
            case(5): //Witch
                cardViewStack.add(new CardView("Witch", R.drawable.dominion_witch, "+2 shop_cards\nEach other player gains a curse", 5, "ATTACK", 10));
                break;
            case(6): //Library
                cardViewStack.add(new CardView("Library", R.drawable.dominion_library, "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards", 5, "ACTION", 10));
                break;
            case(7): //Laboratory
                cardViewStack.add(new CardView("Laboratory", R.drawable.dominion_laboratory, "+2 shop_cards\n+1 Action", 5, "ACTION", 10));
                break;
            case(8): //Militia
                cardViewStack.add(new CardView("Militia", R.drawable.dominion_militia, "+2 Gold\nEach other player discards down to 3 cards in hand", 4, "ATTACK", 10));
                break;
            case(9): //Harbinger
                cardViewStack.add(new CardView("Harbinger", R.drawable.dominion_harbinger, "+1 CardView\n+1 Action\nLook through your discard pile. You may put a card from it onto your deck", 3, "ACTION", 10));
                break;
            default:
                Log.e("initalizeCards","An exception has occured. No card has been assigned to this value");
        }
    }

    protected void initializeBaseCards() {
        cardViewStack.add(new CardView( "Copper", R.drawable.dominion_copper, "+1 Gold", 0, "TREASURE", 10));
        cardViewStack.add(new CardView("Estate", R.drawable.dominion_estate, "1 Victory Point", 2, "VICTORY", 10));
        cardViewStack.add(new CardView("Silver", R.drawable.dominion_silver, "+2 Gold", 3, "TREASURE", 10));
        cardViewStack.add(new CardView("Duchy", R.drawable.dominion_duchy, "3 Victory Points", 5, "VICTORY", 10));
        cardViewStack.add(new CardView("Gold", R.drawable.dominion_gold, "+3 Gold", 6, "TREASURE", 10));
        cardViewStack.add(new CardView("Province", R.drawable.dominion_province, "6 Victory Points", 8, "VICTORY", 10));
    }
}
