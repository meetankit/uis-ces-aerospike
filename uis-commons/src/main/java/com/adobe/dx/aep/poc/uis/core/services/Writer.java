package com.adobe.dx.aep.poc.uis.core.services;

import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface Writer {

    /**
     * create an eddge
     * @param edgeString comma separated nodes
     * @return created graph, null if no graph is created/updated
     */
    Set<RecordValue> createEdge(String edgeString) throws Exception;

    /**
     * delete an eddge
     * @param edgeString comma separated nodes
     * @return created graph, null if no graph is deleted
     * */
    Set<RecordValue> deleteEdge(String edgeString) throws Exception;

    /**
     * bulk update the edges in the order
     * @param edges edge map with the operation
     * @return edge and the corresponding updated graph
     * @throws Exception
     */
    Map<Edge, Set<RecordValue>> bulkUpdate(LinkedHashMap<String, Operation> edges) throws Exception;
}
