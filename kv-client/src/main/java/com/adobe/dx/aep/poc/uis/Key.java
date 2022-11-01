package com.adobe.dx.aep.poc.uis;
/**
 * Defines a Key which comprises of actual key and paths
 */
public class Key {

    /**
     * Paths encapsulates directories or subspaces or collections/containers based on kv implementation. If path is null, kv implementation's wouldn't create sub structures.
     *
     * Example in FoundationDB - paths map to subdirectories
     *         in Aerospike    - paths map to key prefix - key(path[0]|path[1]...path[n])
     *         in Cosmos       - paths map to key prefix - key(path[0]|path[1]...path[n])
     */
    private String[] paths;

    /**
     * Actual key to the document or record of  kv
     */
    private String key;
}