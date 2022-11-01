package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.*;
import static com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext.INIT_RUN_DESC;

public class CesUisUtils {

    private static final Logger logger = LoggerFactory.getLogger(CesUisUtils.class);

    //TODO: generalize this for pessimistic
    private static final String PREFIX = "spec/optimistic/output/";
    private static final Map<String, Map<String, Map<String, Set<String>>>> EXPECTED_MAP = new ConcurrentHashMap<>();

    static {
        loadExpectedData();
    }

    /**
     * preload all the output files in a map
     */
    private static void loadExpectedData() {
        for (String op : writeOps) {
            for (String state : states) {
                Map<String, Map<String, Set<String>>> data =
                        new HashMap<>(GraphFileUtils.readGraphOutputFromFile(getFileName(op, state)));
                EXPECTED_MAP.put(op + "-" + state, data);
            }
        }
    }

    /**
     * store initial state
     */
    public static Map<String, Map<String, Set<String>>> getInitState(String runDesc) {
        return convertInitMapToDescMap(INIT_STATE, runDesc);
    }

    /**
     * get the expected output stored in file for the ops and status
     * replace INIT run desc with the current rundesc
     *
     */
    public static Map<String, Map<String, Set<String>>> getExpectedWriteOutput(String execOp, String status, String runDesc) {
        if(status.equals("")) {
            return getInitState(runDesc);
        }

        //remove all read ops, TODO: better way?
        for(String op: readOps)
            execOp = execOp.replaceAll(op, "");

        if(!EXPECTED_MAP.containsKey(execOp + "-" + status)) {
            logger.info("map not found: e:" + execOp  + " s:" + status + " full: " + EXPECTED_MAP);
            return new HashMap<>();
        }

        return convertInitMapToDescMap(EXPECTED_MAP.get(execOp + "-" + status), runDesc);
    }

    /**
     * replace INIT run desc with the current rundesc
     *
     */
    public static Map<String, Map<String, Set<String>>> convertInitMapToDescMap(Map<String, Map<String, Set<String>>> map, String runDesc) {
        Map<String, Map<String, Set<String>>> output = new HashMap<>();
        for(Map.Entry<String, Map<String, Set<String>>> initEntry : map.entrySet()) {
            String nodeKey = initEntry.getKey().replaceFirst(INIT_RUN_DESC, runDesc);
            if(initEntry.getValue() == null) {
                output.put(nodeKey, null);
            } else {
                Map<String, Set<String>> data = initEntry.getValue();
                Map<String, Set<String>> graphData = new HashMap<>();
                for(Map.Entry<String, Set<String>> entry: data.entrySet()) {
                    Set<String> nodeValue = entry.getValue().stream()
                            .map(node -> node = node.replaceFirst(INIT_RUN_DESC, runDesc)).collect(Collectors.toSet());
                    graphData.put(entry.getKey().replaceFirst(INIT_RUN_DESC, runDesc), nodeValue);
                }
                output.put(nodeKey, graphData);
            }
        }
        return output;
    }

    /**
     * replace current rundesc with the INIT run desc
     *
     */
    public static Map<String, Set<String>> convertDescMapToInitMap(Map<String, Set<String>> map, String runDesc) {
        Map<String, Set<String>> output = new HashMap<>();
        for(Map.Entry<String, Set<String>> initEntry : map.entrySet()) {
            String nodeKey = initEntry.getKey().replaceFirst(runDesc, INIT_RUN_DESC);
            Set<String> nodeValue = initEntry.getValue().stream()
                    .map(node -> node = node.replaceFirst(runDesc, INIT_RUN_DESC)).collect(Collectors.toSet());
            output.put(nodeKey, nodeValue);
        }
        return output;
    }

    public static String getFileName(String execOp, String state) {
        return PREFIX + execOp + "-" + state + ".json";
    }

    public static String getExecOps(RunContext runContext) {
        return Arrays.stream(runContext.getOperationNamesByThread()).reduce((a, b) -> a+=b).get();
    }

    /**
     * validate graph against the expected output
     * @param expectedMap expected Map
     * @param graph actual graph
     * @param nodeId node id of the graph
     */
    public static boolean assertEqualGraphs(Map<String, Map<String, Set<String>>> expectedMap, AerospikeGraph graph, String nodeId) {
        if(graph == null) { // if graph is null, expected should be empty too
            return expectedMap == null || expectedMap.get(nodeId) == null || expectedMap.get(nodeId).size() == 0;
        }

        Map<String, Set<String>> expected = expectedMap.get(nodeId);
        Map<String, Set<String>> actual = graph.getGraphData().getData();
        if (expected.size() != actual.size()) {
            return false;
        }
        return expected.entrySet().stream()
                .allMatch(e -> e.getValue().equals(actual.get(e.getKey())));

    }

    /**
     * read can happen from all successfully committed states till now
     * if current status is 1.0 (both thread complete in this order):
     *      read can be initial state or 1 (thread 1 completed write) or 1.0 (both thread completed write in this order)
     */
    public static Set<String> getAllCommittedStates(String status) {
        Set<String> states = new HashSet<>();
        String state = "";
        for(char ch: status.toCharArray()) {
            state += ch;
            if(ch != '.') states.add(state);
        }
        return states;
    }
}
