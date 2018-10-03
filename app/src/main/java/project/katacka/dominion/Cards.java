package project.katacka.dominion;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Cards {
    protected ArrayList<Card> cardStack;
    protected int totalCards;
    protected final static int uniqueCards = 10;

    public Cards() {
        this.cardStack = new ArrayList<>(6);
        this.totalCards = 6;
        initializeBaseCards();
    }

    public Cards(int totalCards) {
        this.cardStack = new ArrayList<>(totalCards);
        this.totalCards = totalCards;
        generateStack(); //Populates cardStack
    }

    public String

    //Credit: https://stackoverflow.com/questions/4307273/how-can-i-create-and-display-an-arraylist-of-random-numbers-in-java
    private void generateStack(){
        //Gson gson = new Gson(); //ToDo: Finish GSON implementation
        //Card card = gson.fromJson(R.raw.shop_cards, Card.class);
        ThreadLocalRandom.current().ints(0, uniqueCards)
                .distinct().limit(totalCards)
                .forEach(randomInt -> initializeRandomCards(randomInt));
    }

    /*private void initializeRandomCard(int randomCard) {

        Card card
    }*/

    private void initializeRandomCards(int randomCard) {
        switch(randomCard) {
            case(0): //Festival
                cardStack.add(new Card("Festival", R.drawable.dominion_festival, "+2 Actions\n+1 Buy\n+2 Gold", 5, "ACTION", 10));
                break;
            case(1): //Merchant
                cardStack.add(new Card("Merchant", R.drawable.dominion_merchant, "+1 Card\n+1 Action\nThe first time you play a Silver this turn, +1 Gold", 3, "ACTION", 10));
                break;
            case(2): //Remodel
                cardStack.add(new Card("Remodel", R.drawable.dominion_remodel, "Trash a card from your hand. Gain a card costing up to 2 Gold more than it", 4, "ACTION", 10));
                break;
            case(3): //Throne Room
                cardStack.add(new Card("Throne Room", R.drawable.dominion_throne_room, "You may play an Action card from your hand twice", 4, "ACTION", 10));
                break;
            case(4): //Artisan
                cardStack.add(new Card("Artisan", R.drawable.dominion_artisan, "Gain a card to your hand costing up to 5 Gold. Put a card from your hand onto your deck", 6, "ACTION", 10));
                break;
            case(5): //Witch
                cardStack.add(new Card("Witch", R.drawable.dominion_witch, "+2 shop_cards\nEach other player gains a curse", 5, "ATTACK", 10));
                break;
            case(6): //Library
                cardStack.add(new Card("Library", R.drawable.dominion_library, "Draw until you have 7 cards in hand, skipping any Action cards you choose to; set those aside, discarding them afterwards", 5, "ACTION", 10));
                break;
            case(7): //Laboratory
                cardStack.add(new Card("Laboratory", R.drawable.dominion_laboratory, "+2 shop_cards\n+1 Action", 5, "ACTION", 10));
                break;
            case(8): //Militia
                cardStack.add(new Card("Militia", R.drawable.dominion_militia, "+2 Gold\nEach other player discards down to 3 cards in hand", 4, "ATTACK", 10));
                break;
            case(9): //Harbinger
                cardStack.add(new Card("Harbinger", R.drawable.dominion_harbinger, "+1 Card\n+1 Action\nLook through your discard pile. You may put a card from it onto your deck", 3, "ACTION", 10));
                break;
            default:
                Log.e("initalizeCards","An exception has occured. No card has been assigned to this value");
        }
    }

    protected void initializeBaseCards() {
        cardStack.add(new Card( "Copper", R.drawable.dominion_copper, "+1 Gold", 0, "TREASURE", 10));
        cardStack.add(new Card("Estate", R.drawable.dominion_estate, "1 Victory Point", 2, "VICTORY", 10));
        cardStack.add(new Card("Silver", R.drawable.dominion_silver, "+2 Gold", 3, "TREASURE", 10));
        cardStack.add(new Card("Duchy", R.drawable.dominion_duchy, "3 Victory Points", 5, "VICTORY", 10));
        cardStack.add(new Card("Gold", R.drawable.dominion_gold, "+3 Gold", 6, "TREASURE", 10));
        cardStack.add(new Card("Province", R.drawable.dominion_province, "6 Victory Points", 8, "VICTORY", 10));
    }
}
