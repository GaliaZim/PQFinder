package tests;

import structures.GeneGroup;

import java.util.ArrayList;

class GeneGroupsProvider {
    private static GeneGroupsProvider singletonInstance = null;
    private static ArrayList<GeneGroup> geneGroupsPlus;
    private static ArrayList<GeneGroup> geneGroupsMinus;

    private GeneGroupsProvider() {
        String cog;
        geneGroupsPlus = new ArrayList<>();
        geneGroupsMinus = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
            cog = "cog" + i;
            geneGroupsPlus.add(i, new GeneGroup(cog, "+"));
            geneGroupsMinus.add(i, new GeneGroup(cog, "-"));
        }
    }

    static GeneGroupsProvider getInstance() {
        if(singletonInstance == null)
            singletonInstance = new GeneGroupsProvider();
        return singletonInstance;
    }

    ArrayList<GeneGroup> convertToGeneGroups(String str) {
        ArrayList<GeneGroup> string = new ArrayList<>();
        int index;
        for(char c : str.toCharArray()) {
            index = Character.getNumericValue(c);
            string.add(geneGroupsPlus.get(index));
        }
        return string;
    }

}
