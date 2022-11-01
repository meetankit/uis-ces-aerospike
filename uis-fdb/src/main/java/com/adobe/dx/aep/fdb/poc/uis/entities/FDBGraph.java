package com.adobe.dx.aep.fdb.poc.uis.entities;

import com.adobe.dx.aep.poc.uis.core.entities.GraphData;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FDBGraph implements RecordValue {

    private final GraphData graphData;

}
