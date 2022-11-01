package com.adobe.dx.aep.poc.uis.aerospike.services;

import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import static com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph.nextGraphVersion;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.entities.RuleEngineRecord;
import com.adobe.dx.aep.poc.uis.core.services.GraphRuleEngine;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AerospikeGraphProvider {

    private final GraphRuleEngine ruleEngine = new GraphRuleEngine();

    public Map<Edge, Set<RecordValue>> updateGraph(SetupPhaseOutput setupPhase, LinkedHashMap<Edge, Operation> edgeMap) {
        Map<Edge, Set<RecordValue>> edgeListMap = new LinkedHashMap<>();
        Set<RecordValue> values = new HashSet<>();
        for (Map.Entry<Edge, Operation> entry: edgeMap.entrySet()) {
            GraphData leftGraph = null, rightGraph = null;

            for (Pair<RecordKey, RecordValue> pair: setupPhase.getGraphRecords()) {
                AerospikeGraph graph = (AerospikeGraph) pair.getValue();

                if (graph.getGraphData().containsNode(entry.getKey().getLeftNodeId())) {
                    // strictly one graph value for node
                    if(leftGraph != null)
                        throw new IllegalArgumentException(" Node found in multiple graphs: " + entry.getKey().getLeftNodeId());
                    leftGraph = graph.getGraphData();
                    values.add(graph);
                }
                if (graph.getGraphData().containsNode(entry.getKey().getRightNodeId())) {
                    // strictly one graph value for node
                    if(rightGraph != null)
                        throw new IllegalArgumentException(" Node found in multiple graphs: " + entry.getKey().getRightNodeId());
                    rightGraph = graph.getGraphData();
                    values.add(graph);
                }
            }

            RuleEngineRecord ruleEngineRecord = new RuleEngineRecord(leftGraph, rightGraph, entry.getKey(), entry.getValue());
            boolean isUpdated = ruleEngine.updateGraph(ruleEngineRecord);
            if (isUpdated) {
                postProcess(setupPhase.getGraphRecords(), ruleEngineRecord.getLeftNodeGraph(), ruleEngineRecord.getRightNodeGraph());
                edgeListMap.put(entry.getKey(), values);
            }
        }
        return edgeListMap;
    }

    private void postProcess(Set<Pair<RecordKey, RecordValue>> graphRecords, GraphData left, GraphData right) {
        postProcessGraphData(graphRecords, left);
        postProcessGraphData(graphRecords, right);
    }

    private void postProcessGraphData(Set<Pair<RecordKey, RecordValue>> graphRecords, GraphData graphData) {
        if (graphData == null) return;
        if (graphData.getId() == null) graphData.setId(AerospikeKeyGenerator.generateKey());
        RecordKey key = new RecordKey(graphData.getId());
        boolean found = false;
        for (Pair<RecordKey, RecordValue> pair: graphRecords) {
            if (pair.getKey().getId().equals(graphData.getId())) {
                found= true;
                AerospikeGraph graph = (AerospikeGraph)pair.getValue();
                graph.setVersion(nextGraphVersion(graph.getVersion()));
                graph.setGraphData(graphData);
            }
        }

        // if it is a new graph
        if (!found) {
            AerospikeGraph graph = new AerospikeGraph(graphData);
            Pair<RecordKey, RecordValue> pair = new Pair<>(key, graph);
            graph.setVersion(nextGraphVersion(graph.getVersion()));
            graphRecords.add(pair);
        }
    }

}
