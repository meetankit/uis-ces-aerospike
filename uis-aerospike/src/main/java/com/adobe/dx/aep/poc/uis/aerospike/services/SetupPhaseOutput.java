package com.adobe.dx.aep.poc.uis.aerospike.services;

import com.adobe.dx.aep.poc.uis.core.entities.Pair;
import com.adobe.dx.aep.poc.uis.core.entities.RecordKey;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * pojo for setupphase output
 * @author ankitagarwal
 */
@Data
@ToString
public class SetupPhaseOutput {

    private Set<Pair<RecordKey, RecordValue>> routingRecords = new HashSet<>();
    private Set<Pair<RecordKey, RecordValue>> graphRecords = new HashSet<>();
}
