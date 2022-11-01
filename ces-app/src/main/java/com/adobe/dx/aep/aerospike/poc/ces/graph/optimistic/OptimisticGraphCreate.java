package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.poc.ces.graph.GraphCreate;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphWriter;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 * @author agarwalankit
 *
 */
public abstract class OptimisticGraphCreate extends GraphCreate {

    private final ThreadContext threadCtx;
    private final Edge testEdge;

    public OptimisticGraphCreate(ThreadContext tc, GraphState s, Edge testEdge) {
        super(new UISAerospikeGraphWriter(tc, s), tc, s);
        this.threadCtx = tc;
        this.testEdge = testEdge;
    }

    @Override
    public void run() {
        if(threadCtx != null) threadCtx.start("Create start");
        try {
            createEdge(testEdge);
        } finally {
            if(threadCtx != null) threadCtx.end("Create end");
        }
    }
}
