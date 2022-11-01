package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.NODES_PER_GRAPH;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphMerge extends OptimisticGraphCreate {

    public OptimisticGraphMerge(ThreadContext tc, GraphState s) {
        super(tc, s, new Edge(tc.getRunContext().getDesc()+"-0",
                tc.getRunContext().getDesc()+"-" +  Math.multiplyExact(Math.addExact(tc.getThreadNo(), 1), NODES_PER_GRAPH)));
    }

    public OptimisticGraphMerge(RunContext rc, Integer tn) {
        super(null, null, new Edge(rc.getDesc()+"-0",
                rc.getDesc()+"-" +  Math.multiplyExact(Math.addExact(tn, 1), NODES_PER_GRAPH)));
    }
}
