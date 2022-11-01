package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.aerospike.AerospikeClientWithTxnSupport;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeConstants;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTxn;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.aerospike.client.Key;
import com.aerospike.client.Record;

import java.util.Set;

public class TxnAerospikeRecord implements com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeRecord {

    @Override
    public Pair<RecordKey, RecordValue> getRecordPair(Record txnRecord) {
        RecordKey key = new RecordKey("");
        if(txnRecord == null ) {
            return new Pair<>(key, new AerospikeTxn(false));
        }
        return new Pair<>(key, new AerospikeTxn(true));
    }

    @Override
    public RecordUpdate getRecordUpdate(Set<Pair<RecordKey, RecordValue>> records, String txnId) {
        return new RecordUpdate();
    }

    @Override
    public Key getKey(String id) {
        return new Key(UISAerospikeConstants.NAMESPACE, AerospikeClientWithTxnSupport.TRANSACTION_SET, id);
    }
}
