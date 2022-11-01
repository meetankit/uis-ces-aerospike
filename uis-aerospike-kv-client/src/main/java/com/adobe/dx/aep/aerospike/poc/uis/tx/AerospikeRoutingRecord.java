package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeConstants;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting.ACTIVE_GRAPH;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting.ID;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting.PREV_GRAPH;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting.TXN_ID;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;

import java.util.LinkedHashMap;
import java.util.Set;

public class AerospikeRoutingRecord implements AerospikeRecord {

    @Override
    public Pair<RecordKey, RecordValue> getRecordPair(Record routingRecord) {
        if(routingRecord == null) {
            return null;
        }
        String graphIdVersion = routingRecord.getString(ACTIVE_GRAPH);
        String prevGraph = routingRecord.getString(PREV_GRAPH);
        String txnId = routingRecord.getString(TXN_ID);
        String nodeId = routingRecord.getString(ID);

        RecordKey key = new RecordKey(nodeId);
        AerospikeRouting value = new AerospikeRouting(nodeId, graphIdVersion, prevGraph, txnId, routingRecord.generation);
        return new Pair<>(key, value);
    }

    @Override
    public RecordUpdate getRecordUpdate(Set<Pair<RecordKey, RecordValue>> records, String txnId) {
        RecordUpdate recordUpdate = new RecordUpdate();
        LinkedHashMap<Key, Bin[]> recordUpdateMap = recordUpdate.getRecordMap();
        LinkedHashMap<Key, Integer> generationCheckMap = recordUpdate.getGenerationCheckMap();
        for (Pair<RecordKey, RecordValue> recordPair:records) {

            AerospikeRouting record = (AerospikeRouting)recordPair.getValue();
            Key routingKey = getKey(record.getNodeId());
            generationCheckMap.put(routingKey, record.getGeneration());
            
            Bin[] nodeBin = new Bin[4];
            nodeBin[0] = new Bin(ACTIVE_GRAPH, record.getActiveGraphVersion());
            nodeBin[1] = new Bin(PREV_GRAPH, record.getPrevGraphVersion());
            nodeBin[2] = new Bin(TXN_ID, txnId);
            nodeBin[3] = new Bin(ID, record.getNodeId());
            recordUpdateMap.put(routingKey, nodeBin);
        }
        return recordUpdate;
    }

    @Override
    public Key getKey(String id) {
        return new Key(UISAerospikeConstants.NAMESPACE, UISAerospikeConstants.ROUTING_SET_NAME, id);
    }
}
