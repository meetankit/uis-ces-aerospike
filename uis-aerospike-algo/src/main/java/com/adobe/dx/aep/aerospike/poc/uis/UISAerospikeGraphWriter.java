package com.adobe.dx.aep.aerospike.poc.uis;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.aerospike.poc.uis.service.GraphConcurrentWriter;
import com.adobe.dx.aep.aerospike.poc.uis.service.IGraphConcurrentWriter;
import com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeKVClient;
import com.adobe.dx.aep.poc.uis.TransactionException;
import com.adobe.dx.aep.poc.uis.aerospike.services.SetupPhaseOutput;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import static com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils.getEdge;
import com.adobe.dx.aep.poc.uis.core.services.Writer;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import com.aerospike.client.Host;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * @author agarwalankit
 *
 */
public class UISAerospikeGraphWriter implements Writer {

    private final IGraphConcurrentWriter writer;

    public UISAerospikeGraphWriter(ThreadContext tc, GraphState s) {
        //TODO: spring will take care of initialization
        AerospikeKVClient.initAerospikeClient(null, null);
        writer = new GraphConcurrentWriter(tc, s);
    }

    public UISAerospikeGraphWriter(String namespace, Host... hosts) {
        //TODO: spring will take care of initialization
        AerospikeKVClient.initAerospikeClient(namespace, Arrays.stream(hosts).collect(Collectors.toSet()));
        writer = new GraphConcurrentWriter(null, null);
    }

    /**
     * create edge in the graph
     * @param edgeString comma separate nodes
     * @return created graph
     */
    @Override
    public Set<RecordValue> createEdge(String edgeString) throws TransactionException, TxnSupport.GenFailException, TxnSupport.LockAcquireException {
        return writeEdge(edgeString, Operation.CREATE);
    }

    @Override
    public Set<RecordValue> deleteEdge(String edgeString) throws TransactionException, TxnSupport.GenFailException, TxnSupport.LockAcquireException {
        return writeEdge(edgeString, Operation.DELETE);
    }

    @Override
    public Map<Edge, Set<RecordValue>> bulkUpdate(LinkedHashMap<String, Operation> operationMap) throws Exception {
        LinkedHashMap<Edge, Operation> operationEdgeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Operation> entry: operationMap.entrySet()){
            Edge edge = getEdge(entry.getKey());
            operationEdgeMap.put(edge, entry.getValue());
        }
        return bulkWrite(operationEdgeMap);
    }

    private Set<RecordValue> writeEdge(String edgeString, Operation operation) throws TransactionException, TxnSupport.GenFailException, TxnSupport.LockAcquireException {
        Edge edge = getEdge(edgeString);
        LinkedHashMap<Edge, Operation> operationEdgeMap = new LinkedHashMap<>();
        operationEdgeMap.put(edge, operation);
        return bulkWrite(operationEdgeMap).get(edge);
    }

    private Map<Edge, Set<RecordValue>> bulkWrite(LinkedHashMap<Edge, Operation> operationEdgeMap)
            throws TxnSupport.GenFailException, TxnSupport.LockAcquireException, TransactionException {
        try {
            // kept separate phases for logical separation of transactions
            //Setup Phase
            SetupPhaseOutput setupPhaseOutput = writer.setupPhase(operationEdgeMap);
            //Update Phase
            return writer.updatePhase(operationEdgeMap, setupPhaseOutput);
        } catch (TxnSupport.GenFailException | TxnSupport.LockAcquireException e) {
            throw e;
        } catch (Throwable e) {
            throw new TransactionException(e);
        }
    }

}
