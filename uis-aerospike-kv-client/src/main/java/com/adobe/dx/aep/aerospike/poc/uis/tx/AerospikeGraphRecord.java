package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeConstants;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import static com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils.getGraphDataFromJson;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import org.apache.commons.lang.math.NumberUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AerospikeGraphRecord implements AerospikeRecord {

    @Override
    public Pair<RecordKey, RecordValue> getRecordPair(Record graphRecord) {
        if (graphRecord  ==null) {
            return null;
        }
        String graphId = graphRecord.getString(AerospikeGraph.ID);
        String graphVersion = graphRecord.getString(AerospikeGraph.VERSION);
        Map<String, GraphData> allVersions = new HashMap<>();
        RecordKey key = new RecordKey(graphId);

        for (Map.Entry<String, Object> bin: graphRecord.bins.entrySet()) {
            if (NumberUtils.isNumber(bin.getKey())) { //is version
                allVersions.put(bin.getKey(), new GraphData(graphId, getGraphDataFromJson(String.valueOf(bin.getValue()))));
            }
        }

        AerospikeGraph value = new AerospikeGraph(graphId, allVersions, graphVersion, graphRecord.generation);
        return new Pair<>(key, value);
    }

    @Override
    public RecordUpdate getRecordUpdate(Set<Pair<RecordKey, RecordValue>> records, String txnId) {
        RecordUpdate recordUpdate = new RecordUpdate();
        LinkedHashMap<Key, Bin[]> recordUpdateMap = recordUpdate.getRecordMap();
        LinkedHashMap<Key, Integer> generationCheckMap = recordUpdate.getGenerationCheckMap();
        for (Pair<RecordKey, RecordValue> recordPair:records) {

            AerospikeGraph record = (AerospikeGraph)recordPair.getValue();
            Key graphKey = getKey(record.getGraphData().getId());
            generationCheckMap.put(graphKey, record.getGeneration());

            Bin[] bins = new Bin[3];
            bins[0] = new Bin(record.getVersion(), UISGraphUtils.getJsonFromGraphData(record.getGraphData().getData()));
            bins[1] = new Bin(AerospikeGraph.ID, record.getGraphData().getId());
            bins[2] = new Bin(AerospikeGraph.VERSION, record.getVersion());
            recordUpdateMap.put(graphKey, bins);
        }
        return recordUpdate;
    }

    @Override
    public Key getKey(String id) {
        return new Key(UISAerospikeConstants.NAMESPACE, UISAerospikeConstants.GRAPH_SET_NAME, id);
    }
}
