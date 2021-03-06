package project.katacka.dominion;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import project.katacka.dominion.gamestate.DominionCardState;
import project.katacka.dominion.gamestate.DominionDeckState;
import project.katacka.dominion.gamestate.DominionGameState;
import project.katacka.dominion.gamestate.DominionShopPileState;

import static org.junit.Assert.*;

/**
 * Tests DominionDeckState methods
 * @author Julian Donovan
 */
public class DominionDeckStateTest {
    private DominionGameState state;
    private DominionDeckState deck;

    private ArrayList<DominionShopPileState> baseCards;
    private ArrayList<DominionShopPileState> shopCards;

    private final int COPPER = 0;
    private final int ESTATE = 1;
    private final int MOAT = 0;
    private final int MONEY_LENDER = 9;
    private final int COUNCIL_ROOM = 8;
    private final int MERCHANT = 3;
    private final int SILVER = 2;

    @BeforeClass
    public static void setupCards(){
        GameStateGenerator.setupCards();
    }

    @Before
    public void setup(){ //Sets up state, deck, shopCards and baseCards for future use
        state = GameStateGenerator.getNewState(4);
        baseCards = state.getBaseCards();
        shopCards = state.getShopCards();
        deck = state.getDominionPlayers()[0].getDeck();
    }

    /**
     * Clones a card list, allowing for one to make before and after comparisons
     * @param copyList The list to be copied
     * @param tmpList The list copied to
     *
     * @author Julian Donovan
     */
    public void cloneCardList(ArrayList<DominionCardState> copyList, ArrayList<DominionCardState> tmpList) {
        for (DominionCardState card : copyList){
            tmpList.add(new DominionCardState(card));
        }
    }

    /**
     * Sets up a card arrayList with specific cards in known positions, this allows for removal, search and insertion testing
     * @param cardList The list of cards to be modified
     *
     * @author Julian Donovan
     */
    private void setupSpecialCardList(ArrayList<DominionCardState> cardList){
        //Fills the supplied cardList with specifically defined cards
        cardList.clear();
        cardList.add(baseCards.get(COPPER).getCard()); //First card copper
        cardList.add(baseCards.get(ESTATE).getCard()); //Second card Estate
        cardList.add(shopCards.get(MOAT).getCard()); //Third card Moat
        cardList.add(shopCards.get(COUNCIL_ROOM).getCard()); //Forth card Council room
        cardList.add(shopCards.get(MONEY_LENDER).getCard()); //Fifth card Money Lender
        cardList.add(shopCards.get(MERCHANT).getCard()); //Sixth card merchant
        cardList.add(baseCards.get(SILVER).getCard()); //Seventh card Silver
    }

    /**
     * Tests the reveal method, ensuring a card is properly peeked from the discard
     * @author Julian Donovan
     */
    @Test
    public void testReveal() {
        deck.getDraw().clear();
        deck.getDiscard().clear();
        assertNull(deck.reveal()); //When both the deck and discard are empty reveal is expected to return NULL

        setupSpecialCardList(deck.getDiscard()); //Populates the discard
        assertNotNull(deck.reveal()); //Since the discard is shuffled into the deck, the top card cannot be known. However, it should certainly not be NULL

        setupSpecialCardList(deck.getDraw());
        DominionCardState silver = baseCards.get(SILVER).getCard();
        assertEquals(silver, deck.reveal()); //The top card should be a silver as set by setupSpecialCardList
    }

    /**
     * Tests the draw method, ensuring a card is moved from draw to hand
     * @author Julian Donovan
     */
    @Test
    public void testDraw() {
        deck.getDraw().clear();
        deck.getDiscard().clear();
        deck.getHand().clear();
        assertNull(deck.draw()); //When both the deck and discard are empty draw is expected to return NULL (as reveal should return null)

        setupSpecialCardList(deck.getDiscard()); //Populates the discard
        int drawSize =  deck.getDiscardSize(); //The discard will be shuffled into the draw
        assertNotNull(deck.draw()); //Since the discard is shuffled into the deck, the top card cannot be known. However, it should certainly not be NULL
        assertEquals(drawSize - 1, deck.getDrawSize()); //A card should have been taken from the draw
        assertEquals(1, deck.getHand().size()); //The drawn card should be added to the player's hand

        setupSpecialCardList(deck.getDraw());
        DominionCardState silver = baseCards.get(SILVER).getCard();
        assertEquals(silver, deck.draw()); //The drawn card should be a silver as set by setupSpecialCardList
        assertFalse(deck.getDraw().contains(silver)); //The drawn card should have been removed from the player's draw
        int lastHandIdx = deck.getHandSize() - 1;
        assertEquals(silver, deck.getHand().get(lastHandIdx)); //The drawn card should be added to the player's hand
    }

    /**
     * Tests the multi-draw method, ensuring multiple cards (as specified) are moved from draw to hand
     * @author Julian Donovan
     */
    @Test
    public void testDrawMultiple() {
        deck.getDraw().clear();
        deck.getDiscard().clear();
        deck.getHand().clear();
        assertFalse(deck.drawMultiple(1)); //When both the deck and discard are empty draw is expected to return false as no cards exist to draw
        assertFalse(deck.drawMultiple(5)); //The size of the draw request fails to matter if the draw pile and discard are empty
        assertFalse(deck.drawMultiple(-1)); //Returns false as negative cards cannot be drawn
        assertTrue(deck.drawMultiple(0)); //Returns true as zero cards may be drawn

        setupSpecialCardList(deck.getDiscard()); //Populates the discard
        ArrayList<DominionCardState> tmpDiscard = new ArrayList<>(deck.getDiscardSize());
        cloneCardList(deck.getDiscard(), tmpDiscard);

        assertTrue(deck.drawMultiple(tmpDiscard.size())); //Since the discard is shuffled into the deck, up to getDiscardSize cards may be drawn
        assertEquals(0, deck.getDrawSize()); //The draw should have been emptied
        assertTrue(tmpDiscard.containsAll(deck.getHand())); //The drawn cards should have been added to the player's hand
        assertFalse(deck.drawMultiple(1)); //Having drawn the entire discard and draw, attempting to draw more will fail
    }

    /**
     * Tests the putInPlay method, ensuring cards are moved from hand to the inPlay arrayList
     * @author Julian Donovan
     */
    @Test
    public void testPutInPlay() {
        deck.getHand().clear();
        assertFalse(deck.putInPlay(-1)); //Negative 1 is an impossible index
        assertFalse(deck.putInPlay(deck.getHandSize())); //Accessing the size of an arrayList as an index is impossible as arrays are 0 indexed

        setupSpecialCardList(deck.getHand());
        assertTrue(deck.putInPlay(0)); //Should put estate in play
        assertTrue(deck.putInPlay(0)); //Should put a copper in play

        DominionCardState estate = baseCards.get(ESTATE).getCard();
        assertFalse(deck.getHand().contains(estate));
        assertEquals(estate, deck.getInPlay().get(1)); //Ensures an estate has been removed form the hand and put in play

        DominionCardState copper = baseCards.get(COPPER).getCard();
        assertFalse(deck.getHand().contains(copper));
        assertEquals(copper, deck.getInPlay().get(0)); //Ensures an estate has been removed form the hand and put in play
    }

    /**
     * Tests the getLastPlayed method, allowing one to view the top of the inPlay arrayList
     * @author Julian Donovan
     */
    @Test
    public void testGetLastPlayed() {
        deck.getInPlay().clear();
        assertNull(deck.getLastPlayed()); //The inPlay array is empty, as such nothing exists to display

        setupSpecialCardList(deck.getInPlay());
        DominionCardState silver = baseCards.get(SILVER).getCard();
        assertEquals(silver, deck.getLastPlayed()); //The last card put in the inPlay array was a silver
    }

    /**
     * Tests the discard method by index, ensuring the specified card is discarded
     * @author Julian Donovan
     */
    @Test
    public void testDiscardByIndex() {
        assertFalse(deck.discard(deck.getHandSize()));
        assertFalse(deck.discard(-1));

        deck.getHand().clear();
        deck.getDiscard().clear();
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getHand().add(copper);
        deck.discard(0);
        assertEquals(0,deck.getHandSize());
        assertEquals(copper, deck.getDiscard().get(0));
    }

    /**
     * Tests that discard moves card from hand to discard
     * @author Hayden Liao
     */
    @Test
    public void testDiscardByCard() {
        DominionCardState copper = baseCards.get(0).getCard();

        deck.getDiscard().clear();

        setupSpecialCardList(deck.getHand());

        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        assertFalse(deck.discard(null));
        assertFalse(deck.discard("FOOO"));


        deck.discard("Copper");
        assertFalse(deck.getHand().contains(copper));
        assertTrue(deck.getDiscard().contains(copper));
    }

    /**
     * Tests the discard new method, adding a card to the discard (after buy)
     * @author Julian Donovan
     */
    @Test
    public void testDiscardNew() {
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        assertEquals(0, deck.getDiscardSize());

        deck.discardNew(copper);
        assertEquals(1, deck.getDiscardSize());
        assertEquals(copper, deck.getDiscard().get(0));
    }

    /**
     * Tests the discard method when adding many cards (as specified)
     * @author Julian Donovan
     */
    @Test
    public void testAddManyToDiscard() {
        DominionCardState copper = baseCards.get(0).getCard();
        deck.getDiscard().clear();

        deck.addManyToDiscard(copper, -1);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 0);
        assertEquals(0, deck.getDiscardSize());
        deck.addManyToDiscard(copper, 5);

        boolean onlyCopper = deck.getDiscard().stream().allMatch(cards -> cards.getTitle().equals(copper.getTitle()));
        assertTrue(onlyCopper);

    }

    /**
     * Tests that discard all moves all cards from hand into discard
     * @author Hayden Liao
     */
    @Test
    public void testDiscardAll() {
        deck.getHand().clear();
        ArrayList<DominionCardState> tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }
        deck.discardAll();
        assertEquals(0, deck.getHandSize());
        assertTrue(deck.getDiscard().containsAll(tempHand));

        setupSpecialCardList(deck.getHand());
        tempHand = new ArrayList<>(deck.getHand().size());
        for (DominionCardState card : deck.getHand()) {
            tempHand.add(card);
        }

        deck.discardAll();

        assertEquals(0, deck.getHandSize());
        assertTrue(deck.getDiscard().containsAll(tempHand));
    }

    /**
     * Tests that reshuffle shuffles cards from discard into draw
     * @author Hayden Liao
     */
    @Test
    public void testReshuffle() {
        setupSpecialCardList(deck.getDiscard());

        ArrayList<DominionCardState> tempDiscard = new ArrayList<>(deck.getDiscard().size());
        for (DominionCardState card : deck.getDiscard()) {
            tempDiscard.add(card);
        }

        deck.getHand().clear();
        deck.reshuffle();

        assertEquals(0, deck.getDiscardSize());
        assertEquals( true, deck.getDraw().containsAll(tempDiscard));
    }

    /**
     * Tests the countVictory method, ensuring victory cards have been correctly counted
     * @author Julian Donovan
     */
    @Test
    public void testCountVictory() {
        deck.getDiscard().clear();
        deck.getDraw().clear();
        deck.getHand().clear();
        assertEquals(0, deck.countVictory()); //With no victory point cards, the total should be 0

        setupSpecialCardList(deck.getDiscard());
        setupSpecialCardList(deck.getDraw());
        setupSpecialCardList(deck.getHand()); //Each setup special includes 1 estate and by effect 1 VP
        assertEquals(3, deck.countVictory());

    }
}