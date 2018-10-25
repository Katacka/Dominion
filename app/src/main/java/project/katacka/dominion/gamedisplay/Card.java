package project.katacka.dominion.gamedisplay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Card {
    //Graphic Properties
    protected String mTitle;
    protected int mPhotoId;
    protected String mText;
    protected int mCost;
    protected String mType;
    protected int mAmount;
    Method action;

    public Card(Card jsonCard) {
        //this = jsonCard;
    }

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

    public boolean cardAction() {
        try {
            action.invoke(this); //return state after it recognizes boolean nature
            return true;
        }
        catch (IllegalArgumentException e) {

        }
        catch (IllegalAccessException e) {

        }
        catch (InvocationTargetException e) {

        }
        return false;
    }
}
