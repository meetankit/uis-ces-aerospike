/**
 * TearDown.java
 */
package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.aerospike.poc.ces.graph.GraphOperation;
import com.adobe.dx.aep.aerospike.poc.ces.graph.optimistic.*;
import com.adobe.dx.aep.aerospike.poc.uis.UISAerospikeGraphReader;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.adobe.dx.aep.aerospike.poc.ces.CesUisUtils.*;

/**
 * @author agarwalankit
 *
 */
public class TearDown implements Runnable
{
  private static final Logger logger = LoggerFactory.getLogger(TearDown.class);

  @Getter
  private final RunContext          runContext;
  private final GraphState state;
  private final UISAerospikeGraphReader reader = UISGraphFactory.getReader();

  public TearDown(RunContext rc, Object appCtx)
  {
    runContext = rc;
    this.state = (GraphState) appCtx;
  }

  private GraphOperation getGraphOperation (String opName, RunContext runContext, Integer tn) {
    GraphOperation.Ops operation = GraphOperation.Ops.valueOf(opName.toUpperCase(Locale.ROOT));
    switch (operation) {
      case NEW: return new OptimisticGraphNew(runContext, tn);
      case UPDATE: return new OptimisticGraphUpdate(runContext, tn);
      case DELETE: return new OptimisticGraphDelete(runContext, tn);
      case MERGE: return new OptimisticGraphMerge(runContext, tn);
      case BULK: return new OptimisticGraphBulk(runContext, tn);
    }
    return null;
  }

  @SneakyThrows
  @Override
  public void run() {

    String status = state.getStatus();
    boolean partial = false;
    if(!status.contains("0")) {
      String opName = runContext.getOperationNamesByThread()[0];
      getGraphOperation(opName, runContext, 0).run();
      if(!StringUtils.isEmpty(status)) status += ".";
      status += "0";
      partial = true;
    }
    if(!state.getStatus().contains("1")) {
      String opName = runContext.getOperationNamesByThread()[1];
      getGraphOperation(opName, runContext, 1).run();
      if(!StringUtils.isEmpty(status)) status += ".";
      status += "1";
      partial = true;
    }
    if(partial) validate(status);
  }

  private void validate(String status) throws Exception {
    String execOps = getExecOps(runContext);
    Map<String, Map<String, Set<String>>> expected = getExpectedWriteOutput(execOps, status, runContext.getDesc());

    for(Map.Entry<String, Map<String, Set<String>>> nodeData: expected.entrySet()) {
      AerospikeGraph graph = reader.readNode(nodeData.getKey());
      if(!assertEqualGraphs(expected, graph, nodeData.getKey())) {
        runContext.recordThreadFailure(null,
                "Tear down of state for run: " + runContext.getDesc() + " status: " + state.getStatus() + " graph: "
                        + graph + " expected: " + nodeData + " node: " + nodeData.getKey());
      }
    }
  }
}
