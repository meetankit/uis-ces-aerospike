/**
 * TearDown.java
 */
package com.adobe.dx.aep.aerospike.poc.ces;

import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.services.Reader;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.adobe.dx.aep.aerospike.poc.ces.CesUisUtils.*;
import static com.adobe.dx.aep.aerospike.poc.ces.GraphFileUtils.writeGraphOutputToFile;
import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.ALL_NODES;
import static com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext.INIT_RUN_DESC;

/**
 * @author agarwalankit
 *
 */
public abstract class WriteTearDown implements Runnable
{
  @Getter
  private final RunContext runContext;
  private final GraphState state;
  private final Reader reader;

  public WriteTearDown(Reader reader, RunContext rc, Object appCtx) {
    this.runContext = rc;
    this.state = (GraphState) appCtx;
    this.reader = reader;
  }

  @SneakyThrows
  @Override
  public void run() {
    //ignore init run
    if(runContext.getDesc().equalsIgnoreCase(INIT_RUN_DESC)) return;

    //write output if thread order execution or else validate
    if(runContext.isThreadOrder()) writeOutput();
    else validate();
  }

  /**
   * files are stored as json of:
   * nodeId -> graphData of the graph nodeId belongs to
   * graphData -> adjacencyList of nodes (nodeId-> all connected node)
   * so all the nodes of the same graph will have same graph data
   */
  private void writeOutput() throws Exception {
    Map<String, Map<String, Set<String>>> nodeData = new HashMap<>();
    String desc = runContext.getDesc();
    Set<String> nodes = new HashSet<>();

    for(String nodeKey: ALL_NODES) {
      String nodeId = nodeKey.replaceFirst(INIT_RUN_DESC, desc); //node of current run context
      if(nodes.contains(nodeId)){
        continue; // skip
      }
      AerospikeGraph readGraph = (AerospikeGraph)reader.readNode(nodeId);
      if(readGraph != null) {
        Map<String, Set<String>> graphData =
                new HashMap<>(convertDescMapToInitMap(readGraph.getGraphData().getData(), desc));
        for(String node: readGraph.getGraphData().getAllNodes()) {
          nodeData.put(node.replaceFirst(desc, INIT_RUN_DESC), graphData);
        }
        nodes.addAll(readGraph.getGraphData().getAllNodes());
      } else {
        nodeData.put(nodeKey, null);
      }
    }
    writeGraphOutputToFile(nodeData, getFileName(getExecOps(runContext), runContext.getDesc()));
  }

  private void validate() throws Exception {
    String status = state.getStatus();
    String execOps = getExecOps(runContext);
    Map<String, Map<String, Set<String>>> expected = getExpectedWriteOutput(execOps, status, runContext.getDesc());

    for(Map.Entry<String, Map<String, Set<String>>> nodeData: expected.entrySet()) {
      AerospikeGraph graph = (AerospikeGraph)reader.readNode(nodeData.getKey());
      if(!assertEqualGraphs(expected, graph, nodeData.getKey())) {
        runContext.recordThreadFailure(null,
                "Tear down of state for run: " + runContext.getDesc() + " status: " + state.getStatus() + " graph: "
                        + graph + " expected: " + nodeData + " node: " + nodeData.getKey());
      }
    }
  }

}
