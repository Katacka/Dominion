package project.katacka.dominion.gamestate;

/**
 * Enum class purposed to handle DominionCardState types
 * @author Ryan Regier, Hayden Liao
 */
public enum DominionCardType {
    TREASURE,
    VICTORY,
    ACTION,
    ATTACK,
    REACTION,
    BLANK;

    /**
     * Parses a given string, converting its character value to a DominionCardType enum
     * @param typeName A String representing a DominionCardType enum
     * @return A DominionCardType enum, describing card type, or null if type does not exist
     */
    public static DominionCardType getTypeFromString(String typeName){
        for (DominionCardType type : values()){
            if (type.name().equals(typeName)){
                return type;
            }
        }
        return null;
    }
}
