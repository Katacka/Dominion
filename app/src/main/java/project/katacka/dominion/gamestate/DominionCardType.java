package project.katacka.dominion.gamestate;

import java.io.Serializable;

/**
 * Different types cards can be.
 *
 * Note: Advanced cards have multiple types
 *
 * @author Ryan Regier, Hayden Liao, Ashika Mulagada, Julian Donovan
 */
public enum DominionCardType implements Serializable{
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
        if (typeName == null) return null;
        String lower = typeName.toUpperCase();
        for (DominionCardType type : values()){
            if (type.name().equals(lower)){
                return type;
            }
        }
        return null;
    }
}
