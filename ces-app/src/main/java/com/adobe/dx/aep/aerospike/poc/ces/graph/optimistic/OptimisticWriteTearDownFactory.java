/**
 * TearDownFactory.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.skaluskar.poc.ces.types.AbstractTearDownFactory;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

/**
 * @author agarwalankit
 *
 */
public class OptimisticWriteTearDownFactory extends AbstractTearDownFactory
{
  private static final String OPERATION_NAME = "w-teardown";

  public OptimisticWriteTearDownFactory()
  {
    super(OPERATION_NAME);
  }

  @Override
  public Runnable newTearDownRuntime(RunContext runContext, Object appContext)
  {
    return new OptimisticWriteTearDown(runContext, appContext);
  }
}
