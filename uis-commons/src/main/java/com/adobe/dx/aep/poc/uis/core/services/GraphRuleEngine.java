package com.adobe.dx.aep.poc.uis.core.services;

import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RuleEngineRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * performs the graph operations.
 * @author ankitagarwal
 */
public class GraphRuleEngine {

    /**
     * merge the two graphs.
     * @param graphDocument first graph
     * @param mergedGraphDocument graph to be merged.
     */
    public void mergeGraphs(GraphData graphDocument, GraphData mergedGraphDocument) {
        // merge graphs
        Map<String, Set<String>> mergedGraphData = mergedGraphDocument.getData();
        for(Map.Entry<String, Set<String>> entry: mergedGraphData.entrySet()) {
            for(String nodeId: entry.getValue()) {
                graphDocument.addEdge(new Edge(entry.getKey(), nodeId));
            }
        }
    }

    /**
     * split the graph into two graphs.
     * @param graphData graph to be splitted
     * @param splitGraphData output split graph
     * @param splitNode pivot node for split
     */
    public void splitGraph(GraphData graphData, GraphData splitGraphData, String splitNode) {
        createSplitGraph(graphData, splitGraphData, splitNode, 0);
        updateActiveGraph(graphData, splitGraphData, splitNode, 0);
    }

    /**
     * check if thr graph is a connected graph.
     * @param graphData graph
     * @param edge connecting edge
     * @return true if graph is conencted
     */
    public boolean isConnected(GraphData graphData, Edge edge) {
        // small graph only, DFS
        return pathExists(graphData, edge.getRightNodeId(), edge.getLeftNodeId(), new ArrayList<>());
    }

    private boolean pathExists(GraphData activeGraphData, String fromNode, String toNode, List<String> visitedNodes) {
        if(visitedNodes.contains(fromNode) || !activeGraphData.getData().containsKey(fromNode)) {
            return false;
        }
        boolean pathExist = false;
        for(String nodeId: activeGraphData.getData().get(fromNode)) {
            if(nodeId.equals(toNode)) {
                return true;
            }
            visitedNodes.add(fromNode);
            pathExist = pathExists(activeGraphData, nodeId, toNode, visitedNodes);
        }
        return pathExist;
    }

    /**
     * add disconnected graph edges (starting from splitNode) to the split graph
     */
    private void createSplitGraph(GraphData activeGraphData, GraphData splitGraphData, String splitNode, int depth){
        // small graph only, DFS
        if(depth > 200) { //  too much depth -- some bug
            throw new RuntimeException("too much graph depth " + splitGraphData );
        }
        if(!activeGraphData.getData().containsKey(splitNode)) {
            return;
        }
        for(String nodeId: activeGraphData.getData().get(splitNode)) {
            Edge splitEdge = new Edge(splitNode, nodeId);
            if(!splitGraphData.containsEdge(splitEdge)) {
                splitGraphData.addEdge(splitEdge);
                createSplitGraph(activeGraphData, splitGraphData, nodeId, depth+1);
            }
        }
    }

    /**
     *  delete edges from active graph which are created in split graph.
     */
    private void updateActiveGraph(GraphData activeGraphData,
                                          GraphData splitGraphData, String splitNode, int depth){
        if(depth > 200) { // too much depth -- some bug
            throw new RuntimeException("too much graph depth " + splitGraphData);
        }
        if(!activeGraphData.getData().containsKey(splitNode)) {
            return;
        }
        for(String nodeId: splitGraphData.getData().get(splitNode)) {
            Edge splitEdge = new Edge(splitNode, nodeId);
            if(activeGraphData.containsEdge(splitEdge)) {
                activeGraphData.removeEdge(splitEdge);
                updateActiveGraph(activeGraphData, splitGraphData, nodeId, depth+1);
            }
        }
    }

    public boolean updateGraph(RuleEngineRecord record){
        Edge edge = record.getEdge();
        GraphData leftGraph = record.getLeftNodeGraph() == null? record.getRightNodeGraph():record.getLeftNodeGraph();
        GraphData rightGraph = record.getLeftNodeGraph() == null? record.getLeftNodeGraph():record.getRightNodeGraph();

        if (record.getOperation() == Operation.CREATE) {
            if(leftGraph == null) { // new graph
                leftGraph = new GraphData(null, new HashMap<>());
                record.setLeftNodeGraph(leftGraph);
            }

            //null edge or edge already exists
            if (edge == null || leftGraph.containsEdge(edge)) {
                return false;
            }

            leftGraph.addEdge(edge);
            //merging of graph
            if (rightGraph != null) {
                if(leftGraph.getId().equals(rightGraph.getId())) {
                    // both nodes in same graph
                    record.setRightNodeGraph(null);
                } else {
                    mergeGraphs(leftGraph, rightGraph);
                    rightGraph.setData(new HashMap<>());
                }
            }
        } else if (record.getOperation() == Operation.DELETE) {

            //graph doesnt exist or both nodes not in same graph
            if(leftGraph == null || !(leftGraph.containsNode(edge.getLeftNodeId()) &&
                    leftGraph.containsNode(edge.getRightNodeId()))) {
                return false;
            }

            leftGraph.removeEdge(edge);
            if (!isConnected(leftGraph, edge)) { // if graph is no more connected, split graph
                GraphData splitGraph = new GraphData(null, new HashMap<>());
                splitGraph(leftGraph, splitGraph, edge.getRightNodeId());
                record.setRightNodeGraph(splitGraph);
            }
        }
        return true;
    }

}
