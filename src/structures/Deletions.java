package structures;

public class Deletions implements Comparable<Deletions>{
    private int stringDeletions;
    private int treeDeletions;

    public Deletions(int stringDeletions, int treeDeletions) {
        this.stringDeletions = stringDeletions;
        this.treeDeletions = treeDeletions;
    }

    public int getStringDeletions() {
        return stringDeletions;
    }

    public int getTreeDeletions() {
        return treeDeletions;
    }

    @Override
    public int compareTo(Deletions o) {
        if(o == null)
            return 1;
        return (stringDeletions - treeDeletions) - (o.stringDeletions - o.treeDeletions);
    }

    public String toString() {
        return String.format("(%d,%d)", treeDeletions, stringDeletions);
    }
}
