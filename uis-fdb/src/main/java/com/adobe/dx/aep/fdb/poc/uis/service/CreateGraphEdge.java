package com.adobe.dx.aep.fdb.poc.uis.service;

import com.adobe.dx.aep.fdb.poc.uis.FDBFactory;
import com.adobe.dx.aep.fdb.poc.uis.entities.FDBGraph;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.GraphRuleEngine;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import com.apple.foundationdb.Database;
import com.apple.foundationdb.Range;
import com.apple.foundationdb.Transaction;
import com.apple.foundationdb.tuple.Tuple;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class CreateGraphEdge {

    private final GraphRuleEngine ruleEngine = new GraphRuleEngine();
    private final Database db;
    private final Range routing;
    private final Range graph;

    public CreateGraphEdge(String apiVersion, String clusterFile){
        db = FDBFactory.getDb(apiVersion, clusterFile);
        routing = FDBFactory.getRoutingRange(apiVersion, clusterFile);
        graph = FDBFactory.getGraphRange(apiVersion, clusterFile);
    }

    public Set<RecordValue> create(Edge edge) {
        return db.run((Transaction tr) -> {
            byte[] leftNodeGraph = tr.get(Tuple.from(edge.getLeftNodeId()).pack(routing.begin)).join();
            byte[] rightNodeGraph = tr.get(Tuple.from(edge.getRightNodeId()).pack(routing.begin)).join();
            FDBGraph graph;
            if(leftNodeGraph == null && rightNodeGraph == null){ // new graph
                graph = new FDBGraph(new GraphData(UUID.randomUUID().toString(), new HashMap<>()));
            } else if(leftNodeGraph == null) {
                graph = (FDBGraph) getGraph(rightNodeGraph, tr).getValue();
            } else if(rightNodeGraph == null) {
                graph = (FDBGraph) getGraph(leftNodeGraph, tr).getValue();
            } else {
                String leftGraphId = Tuple.fromBytes(leftNodeGraph).getString(0);
                String rightGraphId = Tuple.fromBytes(rightNodeGraph).getString(0);
                if(leftGraphId.equals(rightGraphId)) {
                    return null; // both nodes in same graph
                } // else merge graphs

                byte[] leftGraphData = tr.get(Tuple.from(leftGraphId).pack(this.graph.begin)).join();
                byte[] rightGraphData = tr.get(Tuple.from(rightGraphId).pack(this.graph.begin)).join();
                if(leftGraphData == null || rightGraphData == null) {
                    return null; // graph not found
                }
                String leftGraph = Tuple.fromBytes(leftGraphData).getString(0);
                String rightGraph = Tuple.fromBytes(rightGraphData).getString(0);

                GraphData value = new GraphData(rightGraphId, UISGraphUtils.getGraphDataFromJson(leftGraph));
                graph = new FDBGraph(value);

                GraphData mValue = new GraphData(rightGraphId, UISGraphUtils.getGraphDataFromJson(rightGraph));
                FDBGraph mergedGraph = new FDBGraph(mValue);
                ruleEngine.mergeGraphs(graph.getGraphData(), mergedGraph.getGraphData());
                tr.clear(Tuple.from(mergedGraph.getGraphData().getId()).pack());
            }
            if(graph == null) {
                return null;
            }
            graph.getGraphData().addEdge(edge);

            for(String nodeId: graph.getGraphData().getAllNodes()) {
                tr.set(Tuple.from(nodeId).pack(routing.begin), Tuple.from(graph.getGraphData().getId()).pack());
            }
            tr.set(Tuple.from(graph.getGraphData().getId()).pack(this.graph.begin),
                    Tuple.from(UISGraphUtils.getJsonFromGraphData(graph.getGraphData().getData())).pack());
            return Collections.singleton(graph);
        });
    }

    private Pair<RecordKey, RecordValue> getGraph(byte[] graphIdBytes, Transaction tr) {
        String graphId = Tuple.fromBytes(graphIdBytes).getString(0);
        byte[] graphDataByte = tr.get(Tuple.from(graphId).pack(graph.begin)).join();
        if(graphDataByte == null) { // graph not found for nodes graph
            return null;
        }
        String graphData = Tuple.fromBytes(graphDataByte).getString(0);
        RecordKey key = new RecordKey(graphId);
        FDBGraph value = new FDBGraph(new GraphData(graphId, UISGraphUtils.getGraphDataFromJson(graphData)));
        return new Pair<>(key, value);
    }
}
