package project.katacka.dominion;

public class Card {
    //Graphic Properties
    protected String cTitle;
    protected int cPhotoId;
    protected String cText;
    protected int cCost;
    protected String cType;
    protected int cAmount;

    public Card (String name, int photoId, String text, int cost, String type){
        cTitle = name;
        cPhotoId = photoId;
        cText = text;
        cCost = cost;
        cType = type;
        cAmount = 10;
    }

    public void setcAmount(int cAmount) {
        this.cAmount = cAmount;
    }
}
