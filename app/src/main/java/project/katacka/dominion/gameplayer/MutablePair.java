package project.katacka.dominion.gameplayer;

public class MutablePair<First, Second> {
    private First first;
    private Second second;

    public MutablePair(First first, Second second) {
        this.first = first;
        this.second = second;
    }

    public void setPair(First first, Second second) {
        setFirst(first);
        setSecond(second);
    }

    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }
}
