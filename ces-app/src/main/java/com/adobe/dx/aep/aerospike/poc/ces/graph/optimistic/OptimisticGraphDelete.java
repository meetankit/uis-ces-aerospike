package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.poc.ces.graph.GraphDelete;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphWriter;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphDelete extends GraphDelete {

    private final ThreadContext threadCtx;
    private final Edge testEdge;

    public OptimisticGraphDelete(ThreadContext tc, GraphState s) {
        super(new UISAerospikeGraphWriter(tc, s), tc, s);
        this.threadCtx = tc;
        String runDesc = tc.getRunContext().getDesc();
        this.testEdge =  new Edge(runDesc+"-0", runDesc+"-" + Math.addExact(tc.getThreadNo(),1));
    }

    public OptimisticGraphDelete(RunContext rc, Integer tn) {
        super(new UISAerospikeGraphWriter((ThreadContext) null, null), null, null);
        this.threadCtx = null;
        this.testEdge =  new Edge(rc.getDesc()+"-0", rc.getDesc()+"-" + Math.addExact(tn,1));
    }

    @Override
    public void run() {
        if(threadCtx != null) threadCtx.start("Delete start");
        try {
            deleteEdge(testEdge);
        } finally {
            if(threadCtx != null) threadCtx.end("Delete end");
        }
    }

}
