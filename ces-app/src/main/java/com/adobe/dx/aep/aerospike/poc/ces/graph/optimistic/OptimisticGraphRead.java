package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.poc.ces.graph.GraphRead;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphReader;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import lombok.SneakyThrows;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphRead extends GraphRead implements Runnable {

    private final ThreadContext threadCtx;

    public OptimisticGraphRead(ThreadContext tc, GraphState s) {
        super(new UISAerospikeGraphReader(tc), tc, s);
        this.threadCtx = tc;
    }

    @SneakyThrows
    @Override
    public void run() {
        threadCtx.start("Reader Start");
        try {
            String nodeId = threadCtx.getRunContext().getDesc()+"-0";
            AerospikeGraph graph = readNode(nodeId);
            assertGraph(graph, nodeId);
        } finally {
            threadCtx.end("Reader End");
        }
    }

}
