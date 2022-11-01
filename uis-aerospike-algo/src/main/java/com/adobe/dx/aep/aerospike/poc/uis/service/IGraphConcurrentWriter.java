package com.adobe.dx.aep.aerospike.poc.uis.service;

import com.adobe.dx.aep.poc.uis.aerospike.services.SetupPhaseOutput;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface IGraphConcurrentWriter {

    /**
     * Setup Phase: 1) get edge records 2) get graph 3) get all nodes routing records 4) update prev version
     */
    SetupPhaseOutput setupPhase(LinkedHashMap<Edge, Operation> edge) throws Throwable;

    /**
     * Update Phase: 1) update graph and routing records
     */
    Map<Edge, Set<RecordValue>> updatePhase(LinkedHashMap<Edge, Operation>  edge, SetupPhaseOutput setupPhaseOutput) throws Throwable;

}
