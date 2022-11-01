package com.adobe.dx.aep.aerospike.poc.ces.graph;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 *  @author agarwalankit
 */
public abstract class GraphOperation implements Runnable {

    private final ThreadContext threadCtx;
    private final GraphState state;

    public enum Ops {
        NEW, MERGE, DELETE, UPDATE, BULK
    }

    public GraphOperation(ThreadContext threadCtx, GraphState state){
        this.threadCtx = threadCtx;
        this.state = state;
    }

    public void updateStatus(){
        GraphState.updateStatus(threadCtx, state);
    }

}
