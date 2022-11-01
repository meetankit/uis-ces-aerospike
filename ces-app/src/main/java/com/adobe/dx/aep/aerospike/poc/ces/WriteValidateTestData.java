package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WriteValidateTestData {

    public static final int TOTAL_NODES = 9;
    public static final int NODES_PER_GRAPH = 3;
    public static final Set<String> ALL_NODES = new HashSet<>();

    public static final Map<String, Map<String, Set<String>>> INIT_STATE = new HashMap<>();
    public static final List<String> writeOps = new ArrayList<>();
    public static final List<String> readOps = new ArrayList<>();
    public static final List<String> states = new ArrayList<>();
    public static final Map<String, Set<String>> validatedStates = new ConcurrentHashMap<>();

    static {
        prepareAllNodes();
        prepareInitialGraph();
        prepareOps();
    }

    private static void prepareAllNodes() {
        for(int i = 0; i< TOTAL_NODES; i++) {
            ALL_NODES.add(RunContext.INIT_RUN_DESC+"-"+i);
        }
        ALL_NODES.add(RunContext.INIT_RUN_DESC+"-NN-0");
        ALL_NODES.add(RunContext.INIT_RUN_DESC+"-NN-1");
        ALL_NODES.add(RunContext.INIT_RUN_DESC+"-NN-2");
        ALL_NODES.add(RunContext.INIT_RUN_DESC+"-N");
    }

    private static void prepareInitialGraph() {
        Map<String, Set<String>> map = new HashMap<>();
        Set<String> INIT_0 = new HashSet<>();
        INIT_0.add("INIT-1");
        map.put("INIT-0", INIT_0);
        Set<String> INIT_1 = new HashSet<>();
        INIT_1.add("INIT-0");
        INIT_1.add("INIT-2");
        map.put("INIT-1", INIT_1);
        Set<String> INIT_2 = new HashSet<>();
        INIT_2.add("INIT-1");
        map.put("INIT-2", INIT_2);
        INIT_STATE.put("INIT-0", map);
        INIT_STATE.put("INIT-1", map);
        INIT_STATE.put("INIT-2", map);
    }

    private static void prepareOps() {
        writeOps.add("deletedelete");
        writeOps.add("deletenew");
        writeOps.add("deleteupdate");
        writeOps.add("mergedelete");
        writeOps.add("mergeupdate");
        writeOps.add("mergemerge");
        writeOps.add("mergenew");
        writeOps.add("newnew");
        writeOps.add("newnew");
        writeOps.add("updatenew");
        writeOps.add("updateupdate");
        writeOps.add("bulkupdate");
        writeOps.add("bulkbulk");
        writeOps.add("bulkdelete");
        writeOps.add("bulknew");
        writeOps.add("bulkmerge");

        readOps.add("read");

        states.add("1.0");
        states.add("0");
        states.add("1");
        states.add("0.1");
    }

}
