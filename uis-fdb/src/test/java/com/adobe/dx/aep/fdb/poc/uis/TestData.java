package com.adobe.dx.aep.fdb.poc.uis;

import java.util.HashSet;
import java.util.Set;

public class TestData {

    public static final Set<String> EDGE_0 = new HashSet<>();
    public static final Set<String> EDGE_1 = new HashSet<>();
    public static final Set<String> EDGE_2 = new HashSet<>();
    public static final Set<String> EDGE_3 = new HashSet<>();
    public static final Set<String> EDGE_4_5 = new HashSet<>();
    public static final Set<String> EDGE_0_1 = new HashSet<>();
    public static final Set<String> EDGE_0_1_2 = new HashSet<>();
    public static final Set<String> EDGE_0_1_2_3 = new HashSet<>();
    public static final Set<String> EDGE_1_2_3 = new HashSet<>();
    public static final Set<String> EDGE_1_2 = new HashSet<>();
    public static final Set<String> EDGE_0_3 = new HashSet<>();
    public static final Set<String> EDGE_0_1_3 = new HashSet<>();
    public static final Set<String> EDGE_2_3_N = new HashSet<>();
    public static final Set<String> EDGE_2_3 = new HashSet<>();
    public static final Set<String> EDGE_0_1_2_3_4 = new HashSet<>();
    public static final Set<String> EDGE_1_2_3_4 = new HashSet<>();
    public static final Set<String> EDGE_0_1_N_N = new HashSet<>();
    public static final Set<String> EDGE_0_N = new HashSet<>();

    static {
        EDGE_0.add("INIT-0");

        EDGE_1.add("INIT-1");

        EDGE_2.add("INIT-2");

        EDGE_3.add("INIT-3");

        EDGE_4_5.add("INIT-4");
        EDGE_4_5.add("INIT-5");

        EDGE_0_1.add("INIT-0");
        EDGE_0_1.add("INIT-1");

        EDGE_0_1_2.add("INIT-0");
        EDGE_0_1_2.add("INIT-1");
        EDGE_0_1_2.add("INIT-2");

        EDGE_0_1_2_3.add("INIT-0");
        EDGE_0_1_2_3.add("INIT-1");
        EDGE_0_1_2_3.add("INIT-2");
        EDGE_0_1_2_3.add("INIT-3");

        EDGE_0_1_2_3_4.add("INIT-0");
        EDGE_0_1_2_3_4.add("INIT-1");
        EDGE_0_1_2_3_4.add("INIT-2");
        EDGE_0_1_2_3_4.add("INIT-3");
        EDGE_0_1_2_3_4.add("INIT-4");

        EDGE_1_2_3_4.add("INIT-1");
        EDGE_1_2_3_4.add("INIT-2");
        EDGE_1_2_3_4.add("INIT-3");
        EDGE_1_2_3_4.add("INIT-4");

        EDGE_1_2_3.add("INIT-1");
        EDGE_1_2_3.add("INIT-2");
        EDGE_1_2_3.add("INIT-3");

        EDGE_1_2.add("INIT-1");
        EDGE_1_2.add("INIT-2");

        EDGE_2_3.add("INIT-2");
        EDGE_2_3.add("INIT-3");

        EDGE_0_3.add("INIT-0");
        EDGE_0_3.add("INIT-3");

        EDGE_0_1_3.add("INIT-0");
        EDGE_0_1_3.add("INIT-1");
        EDGE_0_1_3.add("INIT-3");

        EDGE_2_3_N.add("INIT-2");
        EDGE_2_3_N.add("INIT-3");
        EDGE_2_3_N.add("INIT-NN-0");

        EDGE_0_1_N_N.add("INIT-0");
        EDGE_0_1_N_N.add("INIT-1");
        EDGE_0_1_N_N.add("INIT-NN-0");
        EDGE_0_1_N_N.add("INIT-NN-1");

        EDGE_0_N.add("INIT-0");
        EDGE_0_N.add("INIT-NN-0");
    }


}
