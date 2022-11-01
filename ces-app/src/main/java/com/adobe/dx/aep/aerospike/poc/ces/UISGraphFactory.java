package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphReader;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphWriter;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

public class UISGraphFactory {

    private static final UISAerospikeGraphReader READER = new UISAerospikeGraphReader();
    private static final UISAerospikeGraphWriter WRITER = new UISAerospikeGraphWriter((ThreadContext) null, null);

    public static UISAerospikeGraphReader getReader() {
        return READER;
    }

    public static UISAerospikeGraphWriter getWriter() {
        return WRITER;
    }
}
