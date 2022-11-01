package com.adobe.dx.aep.poc.uis.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * graph object that holds the actual graph.
 * @author ankitagarwal
 */
@Data
@AllArgsConstructor
public class GraphData {

    private String id;
    private Map<String, Set<String>> data;

    public Set<String> getAllNodes(){
        return data == null?new HashSet<>(): data.keySet();
    }

    public boolean containsNode(String nodeId) {
        return getAllNodes().contains(nodeId);
    }

    /**
     * bi-directed graph, add edge both ways
     */
    public void addEdge(Edge edge) {
        String leftNodeId = edge.getLeftNodeId();
        String rightNodeId = edge.getRightNodeId();
        if(!data.containsKey(leftNodeId)) {
            data.put(leftNodeId, new HashSet<>());
        }
        data.get(leftNodeId).add(rightNodeId);

        if(!data.containsKey(rightNodeId)) {
            data.put(rightNodeId, new HashSet<>());
        }
        data.get(rightNodeId).add(leftNodeId);
    }

    /**
     * bi-directed graph, remove edge both ways
     */
    public void removeEdge(Edge edge) {
        String leftNodeId = edge.getLeftNodeId();
        String rightNodeId = edge.getRightNodeId();
        Set<String> eV1 = data.get(leftNodeId);
        if (eV1 != null) {
            eV1.remove(rightNodeId);
            if(eV1.size() == 0) {
                data.remove(leftNodeId);
            }
        }

        Set<String> eV2 = data.get(rightNodeId);
        if (eV2 != null) {
            eV2.remove(leftNodeId);
            if(eV2.size() == 0) {
                data.remove(rightNodeId);
            }
        }
    }

    public boolean containsEdge(Edge edge) {
        if (data == null || !data.containsKey(edge.getLeftNodeId())) {
            return false;
        }
        Set<String> edges = data.get(edge.getLeftNodeId());
        return edges.contains(edge.getRightNodeId());
    }

}
