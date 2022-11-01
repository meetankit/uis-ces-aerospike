package com.adobe.dx.aep.poc.uis.aerospike.services;

import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeRouting;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * routing record operations
 * @author agarwalankit
 */
public class AerospikeRoutingRecordProvider {

    /**
     * create/update routing records with the new graph versions.
     */
    public void updateRouting(SetupPhaseOutput setupPhase, LinkedHashMap<Edge, Operation> edgeMap) {
        Set<Pair<RecordKey, RecordValue>> routingRecords = setupPhase.getRoutingRecords();
        for (Pair<RecordKey, RecordValue> routingRecord : routingRecords) {
            updateRoutingValue(setupPhase, routingRecord);
        }
        for (Map.Entry<Edge, Operation> entry: edgeMap.entrySet()) {
            if (entry.getValue() == Operation.CREATE) {
                createRoutingForNewEdge(setupPhase, entry.getKey());
            }
        }
    }

    private void createRoutingForNewEdge(SetupPhaseOutput setupPhase, Edge edge) {
        Set<Pair<RecordKey, RecordValue>> routingRecords = setupPhase.getRoutingRecords();
        // add new routing records for new edge nodes
        for (String node : edge.getNodes()) {
            RecordKey key = new RecordKey(node.trim());
            if (routingRecords.stream()
                    .filter(record -> ((AerospikeRouting)record.getValue()).getNodeId().equals(node.trim()))
                    .collect(Collectors.toSet()).size()==0) {
                AerospikeRouting value =
                        new AerospikeRouting(node.trim(), null, null, null, -1);
                Pair<RecordKey, RecordValue> routing = new Pair<>(key, value);
                updateRoutingValue(setupPhase, routing);
                setupPhase.getRoutingRecords().add(routing);
            }
        }
    }

    private void updateRoutingValue(SetupPhaseOutput setupPhase, Pair<RecordKey, RecordValue> routingRecord) {
        AerospikeRouting routing = ((AerospikeRouting)routingRecord.getValue());
        routing.setActiveGraphVersion(getGraphIdVersionForNode(setupPhase, routing.getNodeId()));
        // generation number got updated in setup phase,
        // hence incrementing by 1 for another update on same record
        // this will ensure no update between setup phase and update phase
        routing.setGeneration(routing.getGeneration() + 1);
    }

    /**
     * update routing records prev versions to be same as active version
     */
    public void updateRoutingPrevGraphVersion(SetupPhaseOutput setupPhase) {
        for (Pair<RecordKey, RecordValue> routingRecord : setupPhase.getRoutingRecords()) {
            AerospikeRouting value = ((AerospikeRouting)routingRecord.getValue());
            value.setPrevGraphVersion(value.getActiveGraphVersion());
        }
    }

    /**
     * get graph id/version for the given node.
     */
    public String getGraphIdVersionForNode(SetupPhaseOutput setupPhase, String nodeId) {
        String graphVersion = null;
        for (Pair<RecordKey, RecordValue> graph: setupPhase.getGraphRecords()) {
            AerospikeGraph aerospikeGraph = (AerospikeGraph)graph.getValue();
            if (aerospikeGraph.getGraphData().containsNode(nodeId)) {
                // node should be strictly in one graph
                if (graphVersion != null)
                    throw new IllegalArgumentException(" Node found in multiple graphs: " + nodeId);
                graphVersion = aerospikeGraph.getGraphIdVersion();
            }
        }
        return graphVersion;
    }

}
