/**
 * SetUp.java
 */
package com.adobe.dx.aep.aerospike.poc.ces.graph;

import com.adobe.dx.aep.aerospike.TxnSupport;
import com.adobe.dx.aep.poc.uis.ces.GraphState;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.services.Writer;
import com.adobe.dx.aep.skaluskar.poc.ces.types.RunContext;
import com.aerospike.client.AerospikeException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.NODES_PER_GRAPH;
import static com.adobe.dx.aep.aerospike.poc.ces.WriteValidateTestData.TOTAL_NODES;

/**
 * @author agarwalankit
 *
 */
public abstract class SetUp implements Callable<Object> {

  private static final Logger logger = LoggerFactory.getLogger(SetUp.class);

  @Getter
  private final RunContext          runContext;

  @Getter
  private final GraphState state;

  private final Writer writer;

  /**
   * setup the test data as follows:
   * create 3 graphs with 9 nodes: INIT-0, INIT-1.... INIT-8
   * each graph has two edges (3 nodes): INIT-0 to INIT-1 & INIT-1 to INIT-2
   */
  @SneakyThrows
  public SetUp(Writer writer, RunContext rc) {
    this.writer = writer;
    runContext = rc;
    state = new GraphState();
    Map<String, Edge> edges = generateEdges();
    for(int i = 0; i < TOTAL_NODES; i+= NODES_PER_GRAPH) {
      createGraph(edges, i);
    }
  }

  private void createGraph(Map<String, Edge> edges, int start) throws Exception {
    for(int i = start; i < start+NODES_PER_GRAPH-1; i++) {
      try {
        Edge edge = edges.get(runContext.getDesc()+"-"+i);
        writer.createEdge(edge.getEdge());
      } catch (TxnSupport.GenFailException | TxnSupport.LockAcquireException e) {
        throw new AerospikeException("Failed in setup: " + this.runContext.getDesc(), e);
      }
    }
  }

  private Map<String, Edge> generateEdges() {
    Map<String, Edge> edgeMap = new HashMap<>();
    for(int i = 0; i< TOTAL_NODES; i++) {
      String runDesc = runContext.getDesc();
      String leftNode = runDesc+"-"+i;
      String rightNode = runDesc+"-"+Math.addExact(i, 1);
      Edge edge = new Edge(leftNode, rightNode);
      edgeMap.put(leftNode, edge);
      edgeMap.put(rightNode, edge);
    }
    return edgeMap;
  }

  @Override
  public Object call() {
    return state;
  }

}
