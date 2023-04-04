package antifraud.model;

import java.util.HashMap;
import java.util.Map;

public enum Region {
    REGION_EAP("EAP"),
    REGION_ECA("ECA"),
    REGION_HIC("HIC"),
    REGION_LAC("LAC"),
    REGION_MENA("MENA"),
    REGION_SA("SA"),
    REGION_SSA("SSA");
    public final String label;
    private Region(String label) {
        this.label = label;
    }
    private static final Map<String, Region> BY_LABEL = new HashMap<>();

    static {
        for (Region e : values()) {
            BY_LABEL.put(e.label, e);
        }
    }
    public static Boolean valueOfLabel(String label) {
        return BY_LABEL.keySet().contains(label);
//        return BY_LABEL.get(label);
    }
}
