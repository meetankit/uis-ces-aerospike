package com.adobe.dx.aep.aerospike.poc.uis;

import com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeKVClient;
import com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeTransaction;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph.getGraphIdFromIdVersion;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph.getGraphVersionFromIdVersion;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTable;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTxn;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.services.Reader;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import com.aerospike.client.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author agarwalankit
 *
 */
public class UISAerospikeGraphReader implements Reader {

    //TODO: the client interaction can be abstracted to GraphConcurrentReader
    private final ThreadContext threadCtx;
    private final AerospikeKVClient aerospikeKVClient = new AerospikeKVClient();
    private static final Logger logger = LoggerFactory.getLogger(UISAerospikeGraphReader.class);

    public UISAerospikeGraphReader(ThreadContext tc) {
        AerospikeKVClient.initAerospikeClient(null, null);
        this.threadCtx = tc;
    }

    public UISAerospikeGraphReader() {
        AerospikeKVClient.initAerospikeClient(null, null);
        this.threadCtx = null;
    }

    public UISAerospikeGraphReader(String namespace, Host... hosts) {
        AerospikeKVClient.initAerospikeClient(namespace, Arrays.stream(hosts).collect(Collectors.toSet()));
        this.threadCtx = null;
    }

    @Override
    public AerospikeGraph readNode(String nodeId) throws Exception {

        //get routing record for node
        AerospikeTransaction tx = (AerospikeTransaction) aerospikeKVClient.createTransaction();
        AerospikeRouting nodeRecord = (AerospikeRouting) tx.get(new RecordKey(nodeId), AerospikeTable.ROUTING.toString()).get();
        blockForTurn("Step 1: get routing record for node");

        // record is deleted
        if(nodeRecord == null) {
            //TODO: Should throw RecordNotFoundException, handling it through null for easy handling in ces-app for now
            return null;
        }

        String txnId = nodeRecord.getTxnId();
        String graphIdVersion;

        //check if routing record txn is still incomplete, if so read prev graph version, else acrtive graph version
        if (((AerospikeTxn)tx.get(new RecordKey(txnId), AerospikeTable.TXN.toString()).get()).getTxnIncomplete()) {
            graphIdVersion = nodeRecord.getPrevGraphVersion();
        } else {
            graphIdVersion = nodeRecord.getActiveGraphVersion();
        }
        blockForTurn("Step 2: Check if node is locked for update");

        // record is marked deleted for the given graphIdVersion
        if(graphIdVersion == null) {
            //TODO: Should throw RecordNotFoundException, handling it through null for easy handling in ces-app for now
            return null;
        }

        String graphId = getGraphIdFromIdVersion(graphIdVersion);
        String version = getGraphVersionFromIdVersion(graphIdVersion);
        AerospikeGraph graph = (AerospikeGraph)tx.get(new RecordKey(graphId), AerospikeTable.GRAPH.toString()).get();
        // graph not found, may be some other thread deleted the graph by now
        if(graph == null || !graph.getGraphDataByVersion().containsKey(version)) {
            //TODO: Should throw Retry-able exception,
            // as committed state of graphIdversion is different from committed state of graph (deleted)
            // can also be recordnotfoundexception as deleted graph is still a valid committed state
            // handling it through null for easy handling in ces-app for now
            return null;
        }
        graph.setGraphData(graph.getGraphDataByVersion().get(version));
        graph.setGraphDataByVersion(null);
        return graph;
    }

    private void blockForTurn(String step) {
        if( threadCtx != null) {
            //  logger.info("Entering step: " + step + " runContext: " + threadCtx.getRunContext().getDesc() + " threadNo: "  + threadCtx.getThreadNo());
            threadCtx.blockForTurn(step);
             //  logger.info("Exit step: " + step + " runContext: " + threadCtx.getRunContext().getDesc() + " threadNo: "  + threadCtx.getThreadNo());
        }
    }

}
