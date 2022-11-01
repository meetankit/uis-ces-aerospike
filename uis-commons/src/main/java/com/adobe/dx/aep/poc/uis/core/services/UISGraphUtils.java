package com.adobe.dx.aep.poc.uis.core.services;

import com.adobe.dx.aep.poc.uis.core.entities.Edge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class UISGraphUtils {

    /**
     * convert json to graph data
     */
    public static Map<String, Set<String>> getGraphDataFromJson(String graphDataJson) {
        if (graphDataJson == null || graphDataJson.equals("")) {
            return new HashMap<>();
        }
        Map<String, Set<String>> data = new HashMap<>();
        Map<String, Object> deMap = SerDeUtils.deserializeMapFromJson(graphDataJson, Set.class);
        for(Map.Entry<String, Object> entry:deMap.entrySet()) {
            data.put(entry.getKey(), (Set<String>)entry.getValue());
        }
        return data;
    }

    /**
     * convert graph data to json
     */
    public static String getJsonFromGraphData(Map<String, Set<String>> graphData) {
        return SerDeUtils.serializeToJson(graphData);
    }

    /**
     * edge string is comma separated two nodes
     */
    public static Edge getEdge(String edgeString) {
        return new Edge(edgeString.split(",")[0].trim(), edgeString.split(",")[1].trim());
    }

}
