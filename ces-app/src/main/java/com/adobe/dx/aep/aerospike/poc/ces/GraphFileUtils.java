package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.skaluskar.poc.cutils.basics.serde.SerDeUtils;
import com.aerospike.client.AerospikeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils.getGraphDataFromJson;
import static com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils.getJsonFromGraphData;

public class GraphFileUtils {

    private static final Logger logger = LoggerFactory.getLogger(GraphFileUtils.class);

    /**
     * write graph to a file named as ops-status.json (e.g. deleteupdate-0.1.json)
     */
    public static void writeGraphOutputToFile(Map<String, Map<String, Set<String>>> data, String fileName) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            Map<String, String> map = new HashMap<>();
            for(Map.Entry<String, Map<String, Set<String>>> entry: data.entrySet()) {
                map.put(entry.getKey(), getJsonFromGraphData(entry.getValue()));
            }
            out.write(SerDeUtils.serializeToJson(map));
        } catch (IOException e) {
            throw new AerospikeException("Write file failed", e);
        }
    }

    /**
     * read graph data from files
     */
    public static Map<String, Map<String, Set<String>>> readGraphOutputFromFile(String fileName) {
        Map<String, Map<String, Set<String>>> data = new HashMap<>();
        try (BufferedReader out = new BufferedReader(new FileReader(fileName))) {
            String line;
            StringBuilder output = new StringBuilder();
            while((line = out.readLine()) != null) {
                output.append(line);
            }
            Map<String, Object> deMap = SerDeUtils.deserializeMapFromJson(output.toString(), String.class);
            for(Map.Entry<String, Object> entry:deMap.entrySet()) {
                data.put(entry.getKey(), getGraphDataFromJson((String)entry.getValue()));
            }
        } catch (IOException e) {
            throw new AerospikeException("Read file failed", e);
        }
        return data;
    }

}
