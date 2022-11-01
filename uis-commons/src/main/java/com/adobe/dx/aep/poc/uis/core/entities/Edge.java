package com.adobe.dx.aep.poc.uis.core.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Edge pojo to represent the edge in the graph.
 * @author agarwalankit
 *
 */
@Data
@ToString
@EqualsAndHashCode
public class Edge {
    private String leftNodeId;
    private String rightNodeId;

    public Edge (String leftNodeId, String rightNodeId) {
        this.leftNodeId = leftNodeId;
        this.rightNodeId = rightNodeId;
    }

    public String getEdge() {
        return (rightNodeId == null || rightNodeId.equals(""))?leftNodeId: leftNodeId+","+rightNodeId;
    }

    public String[] getNodes() {
        return new String[] {leftNodeId, rightNodeId};
    }
}