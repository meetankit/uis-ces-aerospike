package com.adobe.dx.aep.fdb.poc.uis.service;

import com.adobe.dx.aep.fdb.poc.uis.FDBFactory;
import com.adobe.dx.aep.fdb.poc.uis.entities.FDBGraph;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
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

public class DeleteGraphEdge {

    private final GraphRuleEngine ruleEngine = new GraphRuleEngine();
    private final Database db;
    private final Range routing;
    private final Range graph;

    public DeleteGraphEdge(String apiVersion, String clusterFile){
        db = FDBFactory.getDb(apiVersion, clusterFile);
        routing = FDBFactory.getRoutingRange(apiVersion, clusterFile);
        graph = FDBFactory.getGraphRange(apiVersion, clusterFile);
    }

    public Set<RecordValue> delete(Edge edge) {
        return db.run((Transaction tr) -> {
            byte[] leftNodeGraph = tr.get(Tuple.from(edge.getLeftNodeId()).pack(routing.begin)).join();
            byte[] rightNodeGraph = tr.get(Tuple.from(edge.getRightNodeId()).pack(routing.begin)).join();
            FDBGraph graph;
            if(leftNodeGraph == null || rightNodeGraph == null){ // nothing to delete
                return null;
            } else {
                String leftGraphId = Tuple.fromBytes(leftNodeGraph).getString(0);
                String rightGraphId = Tuple.fromBytes(rightNodeGraph).getString(0);
                if(!leftGraphId.equals(rightGraphId)) { // edge not exists
                    return null;
                }

                byte[] graphDataBytes = tr.get(Tuple.from(leftGraphId).pack(this.graph.begin)).join();
                if(graphDataBytes == null) {
                    return null; // graph not found
                }
                String graphData = Tuple.fromBytes(graphDataBytes).getString(0);
                graph = new FDBGraph(new GraphData(leftGraphId, UISGraphUtils.getGraphDataFromJson(graphData)));
            }

            graph.getGraphData().removeEdge(edge);
            FDBGraph splitGraph = new FDBGraph(new GraphData(null, new HashMap<>()));
            if (!ruleEngine.isConnected(graph.getGraphData(), edge)) { // if graph is no more connected, split graph
                ruleEngine.splitGraph(graph.getGraphData(), splitGraph.getGraphData(), edge.getRightNodeId());
                if (splitGraph.getGraphData().getAllNodes().size() > 0) { // store split graph
                    for(String nodeId: splitGraph.getGraphData().getAllNodes()) {
                        tr.set(Tuple.from(nodeId).pack(routing.begin), Tuple.from(splitGraph.getGraphData().getId()).pack());
                    }
                    tr.set(Tuple.from(splitGraph.getGraphData().getId()).pack(this.graph.begin),
                            Tuple.from(UISGraphUtils.getJsonFromGraphData(splitGraph.getGraphData().getData())).pack());
                }
            }
            if(!graph.getGraphData().containsNode(edge.getLeftNodeId()) && !splitGraph.getGraphData().containsNode(edge.getLeftNodeId())) { // node deleted
                tr.clear(Tuple.from(edge.getLeftNodeId()).pack(routing.begin));
            }
            if(!graph.getGraphData().containsNode(edge.getRightNodeId()) && !splitGraph.getGraphData().containsNode(edge.getRightNodeId())) { // node deleted
                tr.clear(Tuple.from(edge.getRightNodeId()).pack(routing.begin));
            }
            if(graph.getGraphData().getAllNodes().size() == 0) { // graph is empty now
                tr.clear(Tuple.from(graph.getGraphData().getId()).pack(this.graph.begin));
            } else {
                tr.set(Tuple.from(graph.getGraphData().getId()).pack(this.graph.begin),
                        Tuple.from(UISGraphUtils.getJsonFromGraphData(graph.getGraphData().getData())).pack());
            }
            return Collections.singleton(graph);
        });
    }
}
