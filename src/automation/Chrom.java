package automation;

import org.json.simple.JSONObject;
import structures.GeneGroup;

import java.util.ArrayList;

public class Chrom {
    private String chromId;
    private int csbStartIndex;
    private int csbEndIndex;
    private String csbId;
    private ArrayList<GeneGroup> geneSeq;

    private Chrom(String chromId, int csbStartIndex, int csbEndIndex, String csbId) {
        this.chromId = chromId;
        this.csbStartIndex = csbStartIndex;
        this.csbEndIndex = csbEndIndex;
        this.csbId = csbId;
    }

    public void setGeneSeq(ArrayList<GeneGroup> geneSeq) {
        this.geneSeq = geneSeq;
    }

    public String getChromId() {
        return chromId;
    }

    public ArrayList<GeneGroup> getGeneSeq() {
        return geneSeq;
    }

    public int getCsbStartIndex() {
        return csbStartIndex;
    }

    public int getCsbEndIndex() {
        return csbEndIndex;
    }

    public String getCsbId() {
        return csbId;
    }

    public static Chrom generateChromFromJsonObject(JSONObject chromJson, String csbId) {
        String id = (String) chromJson.get("chrom_id");
        int start = Integer.valueOf((String) chromJson.get("start_index"));
        int end = Integer.valueOf((String) chromJson.get("end_index"));
        return new Chrom(id, start, end, csbId);
    }
}
