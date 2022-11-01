package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.aerospike.poc.ces.UISGraphFactory;
import com.adobe.dx.aep.aerospike.poc.ces.graph.GraphOperation;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphWriter;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import com.adobe.dx.aep.poc.uis.core.services.Writer;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.NODES_PER_GRAPH;

/**
 * @author agarwalankit
 *
 */
public class OptimisticGraphBulk extends GraphOperation {

    private ThreadContext threadCtx;
    private final Writer writer;
    private final LinkedHashMap<String, Operation> testOps = new LinkedHashMap<>();
    private final String update, merge, delete;

    private static final Logger logger = LoggerFactory.getLogger(OptimisticGraphBulk.class);

    public OptimisticGraphBulk(ThreadContext tc, GraphState s) {
        super(tc, s);
        this.writer = new UISAerospikeGraphWriter(tc, s);
        this.threadCtx = tc;
        String desc = threadCtx.getRunContext().getDesc();
        update = desc+"-0," + desc + "-NN-" + tc.getThreadNo();
        merge = desc+"-0," + desc + "-" + Math.multiplyExact(Math.addExact(tc.getThreadNo(), 1), NODES_PER_GRAPH);
        delete = desc+"-0," + desc + "-" + Math.addExact(tc.getThreadNo(),1);
        testOps.put(update, Operation.CREATE);
        testOps.put(merge, Operation.CREATE);
        testOps.put(delete, Operation.DELETE);
    }

    public OptimisticGraphBulk(RunContext rc, Integer tn) {
        super(null, null);
        this.writer = UISGraphFactory.getWriter();
        String desc = rc.getDesc();
        update = desc+"-0,"+ desc+"-NN-"+ tn;
        merge = desc+"-0,"+ desc+ "-" +  Math.multiplyExact(Math.addExact(tn, 1), NODES_PER_GRAPH);
        delete = desc+"-0,"+ desc+ "-" + Math.addExact(tn,1);
        testOps.put(update, Operation.CREATE);
        testOps.put(merge, Operation.CREATE);
        testOps.put(delete, Operation.DELETE);
    }

    @Override
    @SneakyThrows
    public void run() {
        if(threadCtx != null) threadCtx.start("Create start");
        try {
            try {

                Map<Edge, Set<RecordValue>> graph = writer.bulkUpdate(testOps);
                // edge created
                if(graph != null
                        && graph.get(UISGraphUtils.getEdge(update)) != null
                        && graph.get(UISGraphUtils.getEdge(merge)) != null
                        && graph.get(UISGraphUtils.getEdge(delete)) != null) updateStatus();
            } catch (TxnSupport.GenFailException | TxnSupport.LockAcquireException  e) {
                //do nothing
            }
        } finally {
            if(threadCtx != null) threadCtx.end("Create end");
        }
    }
}
