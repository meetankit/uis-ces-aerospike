package com.adobe.dx.aep.poc.uis;

/**
 * Transactional KV client interface defines methods to create transaction(s) which are independent of the underlying kv client implementations.
 *
 * Calling createTransaction everytime in the same thread must always issue new transaction object
 *
 * @param <K> - Defines key of kv
 * @param <V> - Defines value of the kv
 */
public interface TransactionalKVClient<K, V> {
    Transaction<K, V> createTransaction();
}