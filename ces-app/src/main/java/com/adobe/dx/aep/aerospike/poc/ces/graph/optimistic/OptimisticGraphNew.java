package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphNew extends OptimisticGraphCreate {

    public OptimisticGraphNew(ThreadContext tc, GraphState s) {
        super(tc, s, new Edge(tc.getRunContext().getDesc()+"-N",
                tc.getRunContext().getDesc()+"-NN-"+ tc.getThreadNo()));
    }

    public OptimisticGraphNew(RunContext rc, Integer tn) {
        super(null, null, new Edge(rc.getDesc()+"-N",
                rc.getDesc()+"-NN-"+ tn));
    }
}
