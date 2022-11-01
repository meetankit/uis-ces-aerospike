package com.adobe.dx.aep.fdb.poc.uis;

import com.adobe.dx.aep.fdb.poc.uis.service.CreateGraphEdge;
import com.adobe.dx.aep.fdb.poc.uis.service.DeleteGraphEdge;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import com.adobe.dx.aep.poc.uis.core.services.Writer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author agarwalankit
 *
 */
public class UISFDBGraphWriter implements Writer {

    private final CreateGraphEdge createGraphEdge;
    private final DeleteGraphEdge deleteGraphEdge;

    public UISFDBGraphWriter(String apiVersion, String clusterFile) {
        createGraphEdge = new CreateGraphEdge(apiVersion, clusterFile);
        deleteGraphEdge = new DeleteGraphEdge(apiVersion, clusterFile);
    }

    public UISFDBGraphWriter() {
        createGraphEdge = new CreateGraphEdge("630", null);
        deleteGraphEdge = new DeleteGraphEdge("630", null);
    }

    /**
     * create edge in the graph
     * @param edgeString comma separate nodes
     * @return created graph
     */
    @Override
    public Set<RecordValue> createEdge(String edgeString) {
        return writeEdge(edgeString, Operation.CREATE);
    }

    @Override
    public Set<RecordValue> deleteEdge(String edgeString)  {
        return writeEdge(edgeString, Operation.DELETE);
    }

    @Override
    public Map<Edge, Set<RecordValue>> bulkUpdate(LinkedHashMap<String, Operation> edges) throws Exception {
        return null;
    }

    private Set<RecordValue> writeEdge(String edgeString, Operation operation)  {
        Edge edge = UISGraphUtils.getEdge(edgeString);
        switch (operation){
            case CREATE: return createGraphEdge.create(edge);
            case DELETE: return deleteGraphEdge.delete(edge);
            default: return null;
        }
    }


}
