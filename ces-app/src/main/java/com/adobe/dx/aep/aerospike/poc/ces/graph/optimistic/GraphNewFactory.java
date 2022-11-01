/**
 * WriterFactory.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic;

import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.skaluskar.poc.ces.types.AbstractOperationFactory;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;

/**
 * @author agarwalankit
 *
 */
public class GraphNewFactory extends AbstractOperationFactory
{
  private static final String OPERATION_NAME = "new";

  public GraphNewFactory()
  {
    super(OPERATION_NAME);
  }

  @Override
  public Runnable newRuntime(ThreadContext threadCtx, Object appCtx)
  {
    return new OptimisticGraphNew(threadCtx, (GraphState) appCtx);
  }
}
