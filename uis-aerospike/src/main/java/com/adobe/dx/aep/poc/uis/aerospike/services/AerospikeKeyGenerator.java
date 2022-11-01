package com.adobe.dx.aep.poc.uis.aerospike.services;

import java.util.UUID;

/**
 * generate key for aerospike tables.
 * @author ankitagarwal
 */
public class AerospikeKeyGenerator {

    public static String generateKey() {
        return UUID.randomUUID().toString();
    }

}
