/**
 * TearDown.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.aerospike.poc.ces.UISGraphFactory;
import com.adobe.dx.aep.aerospike.poc.ces.WriteTearDown;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

/**
 * @author agarwalankit
 *
 */
public class OptimisticWriteTearDown extends WriteTearDown
{

  public OptimisticWriteTearDown(RunContext rc, Object appCtx)
  {
    super(UISGraphFactory.getReader(), rc, appCtx);
  }
}
