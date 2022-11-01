package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.aerospike.AerospikeClientWithTxnSupport;
import com.adobe.dx.aep.aerospike.TxnSupport;
import static com.adobe.dx.aep.aerospike.poc.uis.tx.UISAerospikeGraphUtils.getAerospikeKey;
import static com.adobe.dx.aep.aerospike.poc.uis.tx.UISAerospikeGraphUtils.getRecordPairs;
import static com.adobe.dx.aep.aerospike.poc.uis.tx.UISAerospikeGraphUtils.getRecordUpdateForTable;
import com.adobe.dx.aep.poc.uis.Transaction;
import com.adobe.dx.aep.poc.uis.TransactionException;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeTable;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.BatchPolicy;
import com.aerospike.client.policy.WritePolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AerospikeTransaction implements Transaction<RecordKey, RecordValue> {

    private final AerospikeClientWithTxnSupport aerospikeClientWithTxnSupport;
    private final String txnId;
    private final RecordUpdate recordUpdate = new RecordUpdate();

    public AerospikeTransaction(AerospikeClientWithTxnSupport client) {
        aerospikeClientWithTxnSupport = client;
        txnId = UUID.randomUUID().toString();
    }

    @Override
    public CompletableFuture<Void> put(RecordKey key, String tableName, RecordValue recordValue) throws TransactionException {
        Set<Pair<RecordKey, RecordValue>> keyVal = new HashSet<>();
        keyVal.add(new Pair<>(key, recordValue));
        putRecords(keyVal, tableName);
        return new CompletableFuture<>();
    }

    private void putRecords(Set<Pair<RecordKey, RecordValue>> keyVal, String tableName) {
        RecordUpdate ru = getRecordUpdateForTable(keyVal, txnId, tableName);
        recordUpdate.getRecordMap().putAll(ru.getRecordMap());
        recordUpdate.getGenerationCheckMap().putAll(ru.getGenerationCheckMap());
    }

    @Override
    public CompletableFuture<List<Pair<RecordKey, Void>>> put(Set<Pair<RecordKey, RecordValue>> keyVal, String tableName)
            throws TransactionException {
        putRecords(keyVal, tableName);
        return new CompletableFuture<>();
    }
    
    @Override
    public CompletableFuture<RecordValue> get(RecordKey key, String tableName) throws TransactionException {
        CompletableFuture<RecordValue>  completableFuture = new CompletableFuture<>();
        Key aerospikeKey = getAerospikeKey(key, tableName);
        Record[] recordArray = aerospikeClientWithTxnSupport.get(new BatchPolicy(), new Key[]{aerospikeKey});
        List<Pair<RecordKey, RecordValue>> records = getRecordPairs(recordArray, AerospikeTable.valueOf(tableName));
        completableFuture.complete(records.size() == 0 ? null : records.get(0).getValue());
        return completableFuture;
    }

    @Override
    public CompletableFuture<List<Pair<RecordKey, RecordValue>>> get(Set<RecordKey> keys, String tableName) throws TransactionException {
        CompletableFuture<List<Pair<RecordKey, RecordValue>>> completableFuture = new CompletableFuture<>();
        List<Key> aerospikeKeys = keys.stream().map(key -> getAerospikeKey(key, tableName)).collect(Collectors.toList());
        Record[] recordArray = aerospikeClientWithTxnSupport.get(new BatchPolicy(), aerospikeKeys.toArray((new Key[0])));
        completableFuture.complete(getRecordPairs(recordArray, AerospikeTable.valueOf(tableName)));
        return completableFuture;
    }

    @Override
    public CompletableFuture<Void> delete(RecordKey recordKey, String tableName) throws TransactionException {
        //multi record library do not have separate support for delete, use put instead
        // send value as null for the key in put call to delete record
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override
    public CompletableFuture<Void> delete(Set<RecordKey> keys, String tableName) throws TransactionException {
        //multi record library do not have separate support for delete, use put instead
        // send value as null for the key in put call to delete record
        throw new UnsupportedOperationException("Delete not supported");
    }

    @Override
    public CompletableFuture<Void> commit(ThreadContext tc, GraphState s) throws TransactionException {
        try {
            // aerospike multi record library puts all the records in single transaction atomically
            // providing write isolation (either all are written or none)
            // We use the multi record library put call to perform commit to write all the records at once
            aerospikeClientWithTxnSupport.put(new WritePolicy(),
                    recordUpdate.getRecordMap(), recordUpdate.getGenerationCheckMap(), txnId, tc, s);
        } catch (TxnSupport.LockAcquireException | TxnSupport.GenFailException e) {
            throw new TransactionException(e);
        }
        return new CompletableFuture<>();
    }
}
