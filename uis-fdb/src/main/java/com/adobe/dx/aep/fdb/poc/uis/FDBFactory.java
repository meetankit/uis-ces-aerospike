package com.adobe.dx.aep.fdb.poc.uis;

import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.Range;
import com.apple.foundationdb.directory.DirectoryLayer;

import java.util.Collections;

public abstract class FDBFactory {

    private static volatile FDB FOUNDATION_FB;
    private static Database DB;
    private static Range ROUTING_RANGE;
    private static Range GRAPH_RANGE;
    public static final String FDB_SPECIFIC_JVM_ARG_1 = "FDB_LIBRARY_PATH_FDB_C";
    public static final String FDB_SPECIFIC_JVM_VAL_1 = "/usr/local/lib/libfdb_c.dylib";

    public static void init(String apiVersion, String clusterFile) {
        if(FOUNDATION_FB == null) {
            synchronized (FDBFactory.class) {
                if(FOUNDATION_FB == null) { // DCL
                    System.getProperties().setProperty(FDB_SPECIFIC_JVM_ARG_1, FDB_SPECIFIC_JVM_VAL_1);
                    FOUNDATION_FB = FDB.selectAPIVersion(Integer.parseInt(apiVersion.trim()));
                    if (clusterFile != null) DB = FOUNDATION_FB.open(clusterFile);
                    else DB = FOUNDATION_FB.open();
                    ROUTING_RANGE = DirectoryLayer.getDefault().createOrOpen(DB, Collections.singletonList("routing")).join().range();
                    GRAPH_RANGE = DirectoryLayer.getDefault().createOrOpen(DB, Collections.singletonList("graph")).join().range();
                }
            }
        }
    }

    public static FDB getFoundationFb(String apiVersion, String clusterFile) {
        init(apiVersion, clusterFile);
        return FOUNDATION_FB;
    }

    public static Database getDb(String apiVersion, String clusterFile) {
        init(apiVersion, clusterFile);
        return DB;
    }

    public static Range getGraphRange(String apiVersion, String clusterFile) {
        init(apiVersion, clusterFile);
        return GRAPH_RANGE;
    }

    public static Range getRoutingRange(String apiVersion, String clusterFile) {
        init(apiVersion, clusterFile);
        return ROUTING_RANGE;
    }
}
