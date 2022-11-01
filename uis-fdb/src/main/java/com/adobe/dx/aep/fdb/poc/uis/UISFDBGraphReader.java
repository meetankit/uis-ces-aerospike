package com.adobe.dx.aep.fdb.poc.uis;

import com.adobe.dx.aep.fdb.poc.uis.entities.FDBGraph;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.services.Reader;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import com.apple.foundationdb.Database;
import com.apple.foundationdb.FDB;
import com.apple.foundationdb.Range;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author agarwalankit
 *
 */
public class UISFDBGraphReader implements Reader {

    private static final Logger logger = LoggerFactory.getLogger(UISFDBGraphReader.class);
    private FDB fdb;
    private final Database db;
    private final Range routing;
    private final Range graph;

    public UISFDBGraphReader(String apiVersion, String clusterFile) {
        fdb = FDBFactory.getFoundationFb(apiVersion, clusterFile);
        db = FDBFactory.getDb(apiVersion, clusterFile);
        routing = FDBFactory.getRoutingRange(apiVersion, clusterFile);
        graph = FDBFactory.getGraphRange(apiVersion, clusterFile);
    }

    public UISFDBGraphReader() {
        db = FDBFactory.getDb("630", null);
        routing = FDBFactory.getRoutingRange("630", null);
        graph = FDBFactory.getGraphRange("630", null);
    }

    public FDB getFdb() {
        return fdb;
    }

    public Database getDb() {
        return db;
    }

    public Range getRouting() {
        return routing;
    }

    public Range getGraph() {
        return graph;
    }

    @Override
    public FDBGraph readNode(String nodeId) {

        return db.run((Transaction tr) -> {
            byte[] graphBytes = tr.get(Tuple.from(nodeId).pack(routing.begin)).join();
            if(graphBytes == null ){
                return null;
            }
            String graphId = Tuple.fromBytes(graphBytes).getString(0);

            byte[] result = tr.get(Tuple.from(graphId).pack(graph.begin)).join();
            if(result == null ){
                return null;
            }
            String graphDataJson = Tuple.fromBytes(result).getString(0);
            return new FDBGraph(new GraphData(graphId, UISGraphUtils.getGraphDataFromJson(graphDataJson)));
        });
    }

}
