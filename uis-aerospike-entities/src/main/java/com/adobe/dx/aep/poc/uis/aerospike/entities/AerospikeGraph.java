package com.adobe.dx.aep.poc.uis.aerospike.entities;

import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * aerospike graph object.
 * @author agarwalankit
 */
@ToString
@Data
public class AerospikeGraph implements RecordValue {

    public static final String  ID = "id";
    public static final String  VERSION = "version";

    private GraphData graphData;

    // graph data is represented as adjacency list
    //all data by version
    private Map<String, GraphData> graphDataByVersion;
    private Integer generation;
    private String version;

    public AerospikeGraph(String id, Map<String, GraphData> gv, String activeVersion, Integer g) {
        this.graphDataByVersion = gv;
        if (gv.containsKey(activeVersion)) {
            this.graphData = gv.get(activeVersion);
        }
        else this.graphData = new GraphData(id, new HashMap<>());
        this.version = activeVersion;
        this.generation = g;
    }

    public AerospikeGraph(GraphData gd) {
      this.graphData = gd;
      this.generation = 0;
      this.version = "0";
    }

    public String getGraphIdVersion() {
        return graphData == null ? null : graphData.getId() + ":" + getVersion();
    }

    /**
     * graphIdVersion is of format ID:VERSION
     */
    public static String getGraphIdFromIdVersion(String graphIdVersion){
        return graphIdVersion == null ? null :
            graphIdVersion.substring(0, graphIdVersion.indexOf(":"));
    }

    /**
     * graphIdVersion is of format ID:VERSION
     */
    public static String getGraphVersionFromIdVersion(String graphIdVersion) {
        return graphIdVersion.substring(graphIdVersion.indexOf(":") + 1);
    }

    /**
     * increment the current graph version by 1
     */
    public static String nextGraphVersion(String currentVersion) {
        return currentVersion == null ? "0" :
            String.valueOf(Integer.parseInt(currentVersion) + 1);
    }
}
