package project.katacka.dominion;

public class Card {
    //Graphic Properties
    protected String mTitle;
    protected int mPhotoId;
    protected String mText;
    protected int mCost;
    protected String mType;
    protected int mAmount;

    public Card (String name, int photoId, String text, int cost, String type, int amount){
        mTitle = name;
        mPhotoId = photoId;
        mText = text;
        mCost = cost;
        mType = type;
        mAmount = amount;
    }

    public void setmAmount(int mAmount) {
        this.mAmount = mAmount;
    }
}
