/**
 * SetUp.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.poc.ces.UISGraphFactory;
import com.adobe.dx.aep.aerospike.poc.ces.graph.SetUp;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

/**
 * @author ankitagarwal
 *
 */
public class OptimisticSetUp extends SetUp
{

  public OptimisticSetUp(RunContext rc)
  {
    super(UISGraphFactory.getWriter(), rc);
  }
}
