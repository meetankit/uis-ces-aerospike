package com.adobe.dx.aep.aerospike.poc.ces.graph;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.Writer;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import lombok.SneakyThrows;

import java.util.Set;

/**
 *  @author agarwalankit
 */
public abstract class GraphCreate extends GraphOperation {

    private final Writer writer;

    public GraphCreate(Writer writer, ThreadContext threadCtx, GraphState state) {
        super(threadCtx, state);
        this.writer = writer;
    }

    @SneakyThrows
    public Set<RecordValue> createEdge(Edge testEdge) {
        Set<RecordValue> graph = null;
        try {
            graph = writer.createEdge(testEdge.getEdge());

            // edge created
            if(graph != null && graph.size() > 0) updateStatus();
        } catch (TxnSupport.GenFailException | TxnSupport.LockAcquireException  e) {
            //do nothing
        }
        return graph;
    }

}
