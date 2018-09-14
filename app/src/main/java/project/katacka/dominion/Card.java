package project.katacka.dominion;

public class Card {
    //Graphic Properties
    protected String cName;
    protected int cPhotoId;
    protected String cText;
    protected int cCost;
    protected String cType;

    public Card (String name, int photoId, String text, int cost, String type){
        cName = name;
        cPhotoId = photoId;
        cText = text;
        cCost = cost;
        cType = type;
    }
}
