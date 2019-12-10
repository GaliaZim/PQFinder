package structures;

public class Backtrack implements Comparable<Backtrack>{
    private Double score;
    private TableIndices previousTableIndices;

    public Backtrack(Double score, int treeDeletions, int stringDeletions, int childIndex) {
        this.score = score;
        this.previousTableIndices = new TableIndices(treeDeletions, stringDeletions, childIndex);
    }

    public Backtrack(Double score) {
        this.score = score;
        this.previousTableIndices = null;
    }

    public static Backtrack max(Backtrack backtrack1, Backtrack backtrack2) {
        return backtrack1.compareTo(backtrack2) > 0 ? backtrack1 : backtrack2;
    }

    @Override
    public int compareTo(Backtrack o) {
        return this.score.compareTo(o.score);
    }

    public Double getScore() {
        return score;
    }

    public boolean isFirst() {
        return previousTableIndices == null;
    }

    public int getPreviousStringDeletions() {
        return this.previousTableIndices.stringDeletions;
    }

    public int getPreviousTreeDeletions() {
        return this.previousTableIndices.treeDeletions;
    }

    public int getPreviousChildIndex() { return this.previousTableIndices.thirdDimension; }

    public int getSubsetIndex() { return this.previousTableIndices.thirdDimension; }

    @Override
    public String toString() {
        return "Backtrack{" +
                "score=" + score +
                ", previousTableIndices=" + previousTableIndices +
                '}';
    }

    private class TableIndices {
        int treeDeletions;
        int stringDeletions;
        int thirdDimension;

        TableIndices(int treeDeletions, int stringDeletions, int thirdDimension) {
            this.treeDeletions = treeDeletions;
            this.stringDeletions = stringDeletions;
            this.thirdDimension = thirdDimension;
        }

        @Override
        public String toString() {
            return "TableIndices{" +
                    "treeDeletions=" + treeDeletions +
                    ", stringDeletions=" + stringDeletions +
                    ", thirdDimension=" + thirdDimension +
                    '}';
        }
    }
}
