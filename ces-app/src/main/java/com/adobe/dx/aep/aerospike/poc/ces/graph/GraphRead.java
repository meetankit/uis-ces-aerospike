package com.adobe.dx.aep.aerospike.poc.ces.graph;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.services.Reader;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import com.aerospike.client.AerospikeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.adobe.dx.aep.aerospike.poc.ces.CesUisUtils.*;
import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.validatedStates;

/**
 * @author agarwalankit
 *
 */
public abstract class GraphRead {

    private final ThreadContext threadCtx;
    private final GraphState state;
    private final Reader reader;
    private static final Logger logger = LoggerFactory.getLogger(GraphRead.class);

    public GraphRead(Reader reader, ThreadContext tc, GraphState s) {
        this.reader = reader;
        this.threadCtx = tc;
        this.state = s;
    }

    public AerospikeGraph readNode(String nodeId) throws Exception {
        try {
            return (AerospikeGraph)reader.readNode(nodeId);
        } catch (AerospikeException |TxnSupport.LockAcquireException e) {
            logger.info("Error read: ", e);
            threadCtx.recordFailure("Failed readData:" +threadCtx.getRunContext().getDesc()+" nodeId: "+ nodeId + " state:" + state);
        }
        return null;
    }

    public void assertGraph(AerospikeGraph graph, String nodeId) {
        Set<String> expectedStates = getAllCommittedStates(state.getStatus());
        String execOps = getExecOps(threadCtx.getRunContext());
        String runDesc = threadCtx.getRunContext().getDesc();


        List<Map<String, Map<String, Set<String>>>> expectedList = new ArrayList<>();
        //expected can be initial state
        expectedList.add(getInitState(runDesc));
        for(String status: expectedStates) {
            // for each past committed state, get the expected graph data
            expectedList.add(getExpectedWriteOutput(execOps, status, runDesc));
            if(!validatedStates.containsKey(execOps)) {
                validatedStates.put(execOps, new HashSet<>());
            }
            validatedStates.get(execOps).add(status);
        }

        // compare current read against each expected graph data
        boolean isExpected = false;
        for(Map<String, Map<String, Set<String>>> expected: expectedList) {
            if(assertEqualGraphs(expected, graph, nodeId)) {
                isExpected = true;
                break;
            }
        }
        if(!isExpected) {
           threadCtx.recordFailure("Failed readData: " + graph + " Expected: " + expectedList +
                    " states:" + expectedStates + " nodeId: "+ nodeId + " state:" + state);
        }
    }
}
