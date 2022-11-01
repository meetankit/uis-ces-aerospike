package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTable;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.aerospike.client.Key;
import com.aerospike.client.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class UISAerospikeGraphUtils {

    private static final Map<String, AerospikeRecord> recordFactory = new HashMap<>();
    static {
        recordFactory.put(AerospikeTable.GRAPH.name(), new AerospikeGraphRecord());
        recordFactory.put(AerospikeTable.ROUTING.name(), new AerospikeRoutingRecord());
        recordFactory.put(AerospikeTable.TXN.name(), new TxnAerospikeRecord());
    }

    public static List<Pair<RecordKey, RecordValue>> getRecordPairs(Record[] records, AerospikeTable table) {
        List<Pair<RecordKey, RecordValue>> recordList = new ArrayList<>();
        for(Record record : records) {
            Pair<RecordKey, RecordValue> pair = recordFactory.get(table.name()).getRecordPair(record);
            if(pair != null) recordList.add(pair);
        }
        return recordList;
    }

    public static RecordUpdate getRecordUpdateForTable(Set<Pair<RecordKey, RecordValue>> records, String txnId, String table) {
        RecordUpdate recordUpdate = new RecordUpdate();
        if(!recordFactory.containsKey(table)) {
            return recordUpdate; // ignore those records, TODO: throw exception
        }
        RecordUpdate tableRecordUpdate = recordFactory.get(table).getRecordUpdate(records, txnId);
        recordUpdate.getRecordMap().putAll(tableRecordUpdate.getRecordMap());
        recordUpdate.getGenerationCheckMap().putAll(tableRecordUpdate.getGenerationCheckMap());
        return recordUpdate;
    }

    public static Key getAerospikeKey(RecordKey key, String tableName) {
        return recordFactory.get(tableName).getKey(key.getId());
    }

}
