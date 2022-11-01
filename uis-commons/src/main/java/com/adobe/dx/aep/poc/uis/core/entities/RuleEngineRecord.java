package com.adobe.dx.aep.poc.uis.core.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * pojo to represent the edge object for executing rule engine.
 * @author ankitagarwal
 */
@Data
@AllArgsConstructor
public class RuleEngineRecord {

    private GraphData leftNodeGraph;
    private GraphData rightNodeGraph;
    private Edge edge;
    private Operation operation;

}
