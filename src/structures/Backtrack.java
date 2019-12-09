package structures;

public class Backtrack implements Comparable<Backtrack>{
    private Double score;
    private TableIndices previourTableIndices;

    public Backtrack(Double score, int treeDeletions, int stringDeletions, int childIndex) {
        this.score = score;
        this.previourTableIndices = new TableIndices(treeDeletions, stringDeletions, childIndex);
    }

    public Backtrack(Double score) {
        this.score = score;
        this.previourTableIndices = null;
    }

    @Override
    public int compareTo(Backtrack o) {
        return this.score.compareTo(o.score);
    }

    public Double getScore() {
        return score;
    }

    public boolean isFirst() {
        return previourTableIndices == null;
    }

    public int getPreviousStringDeletions() {
        return this.previourTableIndices.stringDeletions;
    }

    public int getPreviousTreeDeletions() {
        return this.previourTableIndices.treeDeletions;
    }

    public int getPreviousChildIndex() { return this.previourTableIndices.childIndex; }

    @Override
    public String toString() {
        return "Backtrack{" +
                "score=" + score +
                ", previourTableIndices=" + previourTableIndices +
                '}';
    }

    private class TableIndices {
        int treeDeletions;
        int stringDeletions;
        int childIndex;

        TableIndices(int treeDeletions, int stringDeletions, int childIndex) {
            this.treeDeletions = treeDeletions;
            this.stringDeletions = stringDeletions;
            this.childIndex = childIndex;
        }

        @Override
        public String toString() {
            return "TableIndices{" +
                    "treeDeletions=" + treeDeletions +
                    ", stringDeletions=" + stringDeletions +
                    ", childIndex=" + childIndex +
                    '}';
        }
    }
}
