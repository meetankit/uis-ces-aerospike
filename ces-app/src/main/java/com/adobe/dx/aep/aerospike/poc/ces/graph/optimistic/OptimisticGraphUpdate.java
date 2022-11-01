package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphUpdate extends OptimisticGraphCreate {

    public OptimisticGraphUpdate(ThreadContext tc, GraphState s) {
        super(tc, s, new Edge(tc.getRunContext().getDesc()+"-0", tc.getRunContext().getDesc()+"-NN-"+ tc.getThreadNo()));
    }

    public OptimisticGraphUpdate(RunContext rc, Integer tn) {
        super(null, null, new Edge(rc.getDesc()+"-0", rc.getDesc()+"-NN-"+ tn));
    }
}
