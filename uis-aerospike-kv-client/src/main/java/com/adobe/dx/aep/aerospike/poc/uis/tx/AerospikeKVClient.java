package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.aerospike.AerospikeClientWithTxnSupport;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeConstants;
import com.adobe.dx.aep.poc.uis.Transaction;
import com.adobe.dx.aep.poc.uis.TransactionalKVClient;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.aerospike.client.Host;
import com.aerospike.client.policy.ClientPolicy;

import java.util.Set;

public class AerospikeKVClient implements TransactionalKVClient<RecordKey, RecordValue> {

    private static final ClientPolicy clientPolicy = new ClientPolicy();
    public static volatile AerospikeClientWithTxnSupport AEROSPIKE_CLIENT;

    // only one aerospike client is supported
    // no support for clients for different namespaces/hosts
    public static void initAerospikeClient(String namespace, Set<Host> hosts) {
        if (AEROSPIKE_CLIENT == null) { //double check locking
            synchronized (AerospikeKVClient.class) {
                if (AEROSPIKE_CLIENT == null) {
                    if (hosts == null) {
                        AEROSPIKE_CLIENT =
                                new AerospikeClientWithTxnSupport(clientPolicy, UISAerospikeConstants.AEROSPIKE_SERVER_IP,
                                        UISAerospikeConstants.AEROSPIKE_SERVER_PORT, UISAerospikeConstants.NAMESPACE);
                    } else {
                        AEROSPIKE_CLIENT =
                                new AerospikeClientWithTxnSupport(clientPolicy, namespace, hosts.toArray(new Host[0]));
                    }
                    AEROSPIKE_CLIENT.setEnterprise(true);
                }
            }
        }
    }

    @Override
    public Transaction<RecordKey, RecordValue> createTransaction() {
        return new AerospikeTransaction(AEROSPIKE_CLIENT);
    }
}
