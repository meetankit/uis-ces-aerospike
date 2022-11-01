/**
 * TearDownFactory.java
 */
package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.skaluskar.poc.ces.types.AbstractTearDownFactory;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

/**
 * @author agarwalankit
 *
 */
public class TearDownFactory extends AbstractTearDownFactory
{
  private static final String OPERATION_NAME = "teardown";

  public TearDownFactory()
  {
    super(OPERATION_NAME);
  }

  @Override
  public Runnable newTearDownRuntime(RunContext runContext, Object appContext)
  {
    return new TearDown(runContext, appContext);
  }
}
