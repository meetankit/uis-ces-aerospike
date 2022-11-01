package com.adobe.dx.aep.poc.uis.aerospike.entities;

import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * aerospike routing object.
 * @author ankitagarwal
 */
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Data
public class AerospikeRouting implements RecordValue {

    public static final String  ID = "id";
    public static final String  TXN_ID = "txnId";
    public static final String  ACTIVE_GRAPH = "activeGraph";
    public static final String  PREV_GRAPH = "prevGraph";

    private String nodeId;
    private String activeGraphVersion;
    private String prevGraphVersion;
    private String txnId;
    private Integer generation;

}
