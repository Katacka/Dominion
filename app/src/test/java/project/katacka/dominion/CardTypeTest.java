package project.katacka.dominion;

import org.junit.Test;

import project.katacka.dominion.gamestate.DominionCardType;

import static org.junit.Assert.*;

public class CardTypeTest {

    @Test
    public void testCardType(){
        assertEquals("VICTORY", DominionCardType.VICTORY, DominionCardType.getTypeFromString("VICTORY"));
        assertEquals("victory", DominionCardType.VICTORY, DominionCardType.getTypeFromString("victory"));
        assertNotEquals("Victory not treasure", DominionCardType.VICTORY, DominionCardType.getTypeFromString("TREASURE"));
        assertNull("Bad input", DominionCardType.getTypeFromString("bad"));
        assertNull("Null input", DominionCardType.getTypeFromString(null));
    }
}
