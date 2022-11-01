/**
 * SetUpFactory.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import java.util.concurrent.Callable;

import com.adobe.dx.aep.skaluskar.poc.ces.types.AbstractSetUpFactory;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;

/**
 * @author agarwalankit
 *
 */
public class SetUpFactory extends AbstractSetUpFactory
{
  private static final String OPERATION_NAME = "setup";

  public SetUpFactory()
  {
    super(OPERATION_NAME);
  }

  @Override
  public Callable<Object> newSetUpRuntime(RunContext runContext)
  {
    return new OptimisticSetUp(runContext);
  }
}
