package com.adobe.dx.aep.aerospike.poc.uis.tx;

import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashMap;

/**
 * @author agarwalankit
 *
 */
@Data
@AllArgsConstructor
public class RecordUpdate {

    private LinkedHashMap<Key, Bin[]> recordMap ;
    private LinkedHashMap<Key, Integer> generationCheckMap;

    public RecordUpdate() {
        recordMap = new LinkedHashMap<>();
        generationCheckMap = new LinkedHashMap<>();
    }
}
