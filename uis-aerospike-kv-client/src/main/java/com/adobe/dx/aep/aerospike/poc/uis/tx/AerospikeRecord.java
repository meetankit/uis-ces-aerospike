package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.aerospike.client.Key;
import com.aerospike.client.Record;

import java.util.Set;

public interface AerospikeRecord {

    Pair<RecordKey, RecordValue> getRecordPair(Record routingRecord);

    RecordUpdate getRecordUpdate(Set<Pair<RecordKey, RecordValue>> records, String txnId);

    Key getKey(String id);
}
