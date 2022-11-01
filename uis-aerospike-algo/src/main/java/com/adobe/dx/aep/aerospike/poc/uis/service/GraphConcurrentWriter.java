package com.adobe.dx.aep.aerospike.poc.uis.service;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeKVClient;
import static com.adobe.dx.aep.aerospike.poc.uis.tx.UISAerospikeGraphUtils.getAerospikeKey;
import com.adobe.dx.aep.poc.uis.Transaction;
import com.adobe.dx.aep.poc.uis.TransactionException;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph.getGraphIdFromIdVersion;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTable;
import com.adobe.dx.aep.poc.uis.aerospike.services.AerospikeGraphProvider;
import com.adobe.dx.aep.poc.uis.aerospike.services.AerospikeRoutingRecordProvider;
import com.adobe.dx.aep.poc.uis.aerospike.services.SetupPhaseOutput;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GraphConcurrentWriter implements IGraphConcurrentWriter {

    private final ThreadContext threadCtx;
    private final GraphState state;
    private final AerospikeKVClient aerospikeKVClient = new AerospikeKVClient();
    private final AerospikeGraphProvider graphProvider = new AerospikeGraphProvider();
    private final AerospikeRoutingRecordProvider routingRecordProvider = new AerospikeRoutingRecordProvider();

    private static final Logger logger = LoggerFactory.getLogger(GraphConcurrentWriter.class);

    public GraphConcurrentWriter(ThreadContext tc, GraphState s) {
        this.threadCtx = tc;
        this.state = s;
    }

    @Override
    public SetupPhaseOutput setupPhase(LinkedHashMap<Edge, Operation> edgeMap) throws Throwable {
        Transaction<RecordKey, RecordValue> tx = aerospikeKVClient.createTransaction();
        SetupPhaseOutput setupPhase = new SetupPhaseOutput();

        Set<String> edgeKeys = new HashSet<>();
        for(Edge edge: edgeMap.keySet()) {
            edgeKeys.add(edge.getRightNodeId());
            edgeKeys.add(edge.getLeftNodeId());
        }

        //read routing records for edge nodes
        List<Pair<RecordKey, RecordValue>> records = readRoutingRecords(edgeKeys, tx);
        setupPhase.getRoutingRecords().addAll(records);

        //read edge nodes graph records
        List<String> graphKeys = setupPhase.getRoutingRecords().stream()
                .map(node -> getGraphIdFromIdVersion(((AerospikeRouting)node.getValue()).getActiveGraphVersion()))
                .distinct().collect(Collectors.toList());
        List<Pair<RecordKey, RecordValue>> graphs = readGraph(graphKeys, tx);
        setupPhase.getGraphRecords().addAll(graphs);

        //read all nodes routing records of graphs
        Set<String> nodeKeys = new HashSet<>();
        for(Pair<RecordKey, RecordValue> graph: setupPhase.getGraphRecords()) {
            nodeKeys.addAll(((AerospikeGraph)graph.getValue()).getGraphData().getAllNodes().stream()
                    .filter(node -> !edgeKeys.contains(node)).collect(Collectors.toSet()));
        }
        List<Pair<RecordKey, RecordValue>> graphNodes = readRoutingRecords(nodeKeys, tx);

        // some node in graph is not found in routing (may be deleted by some other thread) TODO: find which nodes
        if(graphNodes.size() != nodeKeys.size()) {
            throw new TxnSupport.GenFailException(
                    getAerospikeKey(new RecordKey(nodeKeys.stream().findAny().get()),
                        AerospikeTable.ROUTING.name()), "Record is deleted");
        }
        setupPhase.getRoutingRecords().addAll(graphNodes);

        //update prev version in routing records to be same as active version
        routingRecordProvider.updateRoutingPrevGraphVersion(setupPhase);
        tx.put(setupPhase.getRoutingRecords(), AerospikeTable.ROUTING.toString());

        //apply updates
        try {
            tx.commit(null, null);
        } catch (TransactionException e) {
            throw e.getCause();
        }
        blockForTurn("Step 7: Update Routing records prev graph version");

        return setupPhase;
    }

    @Override
    public Map<Edge, Set<RecordValue>> updatePhase(LinkedHashMap<Edge, Operation> edgeMap, SetupPhaseOutput setupPhase) throws Throwable {
        Transaction<RecordKey, RecordValue> tx = aerospikeKVClient.createTransaction();

        //execute update graph operation (rule engine)
        Map<Edge, Set<RecordValue>> output = graphProvider.updateGraph(setupPhase, edgeMap);
        if(output.size() > 0) {
            tx.put(setupPhase.getGraphRecords(), AerospikeTable.GRAPH.toString());

            //create/update routing records with the new graph versions
            routingRecordProvider.updateRouting(setupPhase, edgeMap);
            tx.put(setupPhase.getRoutingRecords(), AerospikeTable.ROUTING.toString());
        }

        //apply updates
        try {
            tx.commit(threadCtx, state);
        } catch (TransactionException e) {
            throw e.getCause();
        }
        return output;
    }

    private List<Pair<RecordKey, RecordValue>> readRoutingRecords(Set<String> nodeKeys,
                                                              Transaction<RecordKey, RecordValue> transaction)
            throws TransactionException, ExecutionException, InterruptedException {
        Set<RecordKey> recordKeys = nodeKeys.stream().map(RecordKey::new).collect(Collectors.toSet());
        List<Pair<RecordKey, RecordValue>> routingRecords = transaction.get(recordKeys, AerospikeTable.ROUTING.toString()).get();
        blockForTurn("Step: Read Routing records for all nodes in existing graph");
        return routingRecords;
    }

    private List<Pair<RecordKey, RecordValue>>  readGraph(List<String> graphKeys, Transaction<RecordKey, RecordValue> transaction)
            throws TransactionException, ExecutionException, InterruptedException {
        Set<RecordKey> recordKeys = graphKeys.stream().map(RecordKey::new).collect(Collectors.toSet());
        List<Pair<RecordKey, RecordValue>> routingRecords = transaction.get(recordKeys, AerospikeTable.GRAPH.toString()).get();
        blockForTurn("Step 4: Read existing graph for new edge");

        return routingRecords.stream()
                .map(pair -> new Pair<>(pair.getKey(), pair.getValue())).collect(Collectors.toList());
    }

    private void blockForTurn(String step) {
        if( threadCtx != null) {
            //logger.info("Entering step: " + step + " runContext: " + threadCtx.getRunContext().getDesc() + " threadNo: "  + threadCtx.getThreadNo());
            threadCtx.blockForTurn(step);
            //logger.info("Exit step: " + step + " runContext: " + threadCtx.getRunContext().getDesc() + " threadNo: "  + threadCtx.getThreadNo());
        }
    }
}
