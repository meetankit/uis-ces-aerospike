package com.adobe.dx.aep.poc.uis;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * A high level interface specifying operations that must be supported by underlying transactional kv clients.
 * <p>
 * Implementation must ensure thread safety and ACID properties** just like any traditional rdbms database
 * <p>
 * Rollback is implicit in the commit call. No support for partial state as it leads to in-consistent state
 *
 * @param <K> - primary identifier of the record stored in the kv store
 * @param <V> - actual data stored against the key. V can be modelled differently based on the underlying implementation
 */
public interface Transaction<K, V> {

    /**
     * Perform kv put operation
     *
     * @param key       - primary identifier of the record stored in the kv store
     * @param tableName - name of the logical table - differs for each implementation
     * @param v         -
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<Void> put(K key, String tableName, V v) throws TransactionException;

    /**
     * Perform batch kv put operation and if not supported, calls are fired in parallel on separate threads.
     *
     * @param keyVal    - a list of pair of key and value
     * @param tableName - name of the logical table - differs for each implementation
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<List<Pair<K, Void>>> put(Set<Pair<K,V>> keyVal, String tableName) throws TransactionException;

   /* Every transaction is assigned a monotonically increasing byte sequence referred to as the Transaction VersionStamp.
            * Transactions are commit to the database in the order of versionstamp
   *
           *
           * @param key       - only the paths in the key are leveraged
   * @param tableName - name of the logic table - differs for each implementation
   * @param v         - represents value to be stored
   * @return
           * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
   */
    default CompletableFuture<Void> putWithVersionStampedKey(Key key, String tableName, V v) throws TransactionException {
        throw new IllegalStateException("Unsupported operation");
    }

    /**
     * Performs a single get look up operation. If invoked subsequently, then no real kv store call is made but instead already loaded copy is returned
     *
     * @param key       - primary identifier of the record stored in the kv store
     * @param tableName - name of the logical table - differs for each implementation
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<V> get(K key, String tableName) throws TransactionException;

    /**
     * Performs a batch loading of records given a set of keys if supported otherwise fallbacks to single look up.
     * <p>
     * In case of single lookup, calls are fired in parallel on separate threads.
     * <p>
     * If invoked subsequently, then no real kv store call is made but instead already loaded copy is returned.
     *
     * @param key       - primary identifier of the record stored in the kv store
     * @param tableName - name of the logical table - differs for each implementation
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<List<Pair<K, V>>> get(Set<K> key, String tableName) throws TransactionException;


    /**
     * A performs a delete operation on a given key
     *
     * @param k         - primary identifier of the record stored in the kv store
     * @param tableName - name of the logical table - differs for each implementation
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<Void> delete(K k, String tableName) throws TransactionException;

    /**
     * A performs a batch delete operation given set of keys if supported otherwise ok to fall back to single record delete
     *
     * @param keys      -  A set of kv keys
     * @param tableName - name of the logical table - differs for each implementation
     * @return
     * @throws TransactionException - Throws exception in case of conflicts. No support for partial success as it results in consistent state. All the changes must be reverted
     */
    CompletableFuture<Void> delete(Set<K> keys, String tableName) throws TransactionException;

    /**
     * Commits all the in-memory operation performed so far and flushes them to kv store.
     * Any conflicts will result in rolling back or not committing any changes at all
     *
     * @return
     * @throws TransactionException
     */
    CompletableFuture<Void> commit(ThreadContext tc, GraphState s) throws TransactionException;

}