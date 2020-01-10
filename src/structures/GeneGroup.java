package structures;

public class GeneGroup {
    private String cog;
    private Strand strand;

    public GeneGroup(String cog, String strand) {
        this.cog = cog;
        this.strand = Strand.getStrand(strand);
    }

    public String getCog() {
        return cog;
    }

    public Strand getStrand() {
        return strand;
    }

    @Override
    public String toString() {
        return getCog()+getStrand().getStrandSymbol();
    }

    enum Strand {
        PLUS ("+"),
        MINUS ("-");

        private final String strandSymbol;

        Strand(String s) {
            this.strandSymbol = s;
        }

        public String getStrandSymbol() {
            return strandSymbol;
        }

        static Strand getStrand(String strandSymbol) {
            switch(strandSymbol){
                case "+": return Strand.PLUS;
                case "-": return Strand.MINUS;
                default: return null;
            }
        }

    }
}
