package com.adobe.dx.aep.poc.uis.aerospike.entities;

import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * aerospike txn object.
 * @author ankitagarwal
 */
@AllArgsConstructor
@Data
public class AerospikeTxn implements RecordValue {

    private Boolean txnIncomplete;

}
