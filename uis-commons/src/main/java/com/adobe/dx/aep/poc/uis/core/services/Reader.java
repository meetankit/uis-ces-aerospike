package com.adobe.dx.aep.poc.uis.core.services;

import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;

public interface Reader {

    RecordValue readNode(String nodeId) throws Exception;
}
