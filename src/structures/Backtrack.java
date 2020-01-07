package structures;

public class Backtrack implements Comparable<Backtrack>{
    /**
     * The score of the mapping
     */
    private Double score;
    /**
     * The previous DP Table indices that lead to this mapping
     */
    private TableIndices previousTableIndices;

    public Backtrack(Double score, int treeDeletions, int stringDeletions, int childIndex) {
        this.score = score;
        this.previousTableIndices = new TableIndices(treeDeletions, stringDeletions, childIndex);
    }

    public Backtrack(Double score) {
        this.score = score;
        this.previousTableIndices = null;
    }

    /**
     * @param backtrack1 a Backtrack object
     * @param backtrack2 a Backtrack object
     * @return returns the backtrack with the higher score. If the score is equal, returns {@code backtrack2}
     */
    public static Backtrack max(Backtrack backtrack1, Backtrack backtrack2) {
        return backtrack1.compareTo(backtrack2) > 0 ? backtrack1 : backtrack2;
    }

    /**
     * @param o other Backtrack
     * @return A positive number if {@code this} has a higher score, zero if {@code this} and {@code o} has the same
     * score, and a negative number, otherwise
     */
    @Override
    public int compareTo(Backtrack o) {
        return this.score.compareTo(o.score);
    }

    public Double getScore() {
        return score;
    }

    /**
     * @return {@code true} if this backtrack is the first in the backtrack pate, i.e. it has no previous indices
     */
    public boolean isFirst() {
        return previousTableIndices == null;
    }

    /**
     * @return The previous string deletion index
     */
    public int getPreviousStringDeletions() {
        return this.previousTableIndices.stringDeletions;
    }

    /**
     * @return The previous tree deletion index
     */
    public int getPreviousTreeDeletions() {
        return this.previousTableIndices.treeDeletions;
    }

    /**
     * @return The previous child index (the index not related to deletions)
     */
    public int getPreviousChildIndex() { return this.previousTableIndices.thirdDimension; }

    /**
     * @return The previous children subset index (the index not related to deletions)
     */
    public int getPreviousSubsetIndex() { return this.previousTableIndices.thirdDimension; }

    @Override
    public String toString() {
        return "Backtrack{" +
                "score=" + score +
                ", previousTableIndices=" + previousTableIndices +
                '}';
    }

    /**
     * An inner class for saving the 3 previous DP table indices
     */
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
