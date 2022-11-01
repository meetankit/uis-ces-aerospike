package com.adobe.dx.aep.aerospike.poc.uis;

import com.adobe.dx.aep.aerospike.AerospikeClientWithTxnSupport;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_0_1;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_0_1_2;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_0_1_2_3;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_0_1_2_3_4;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_0_N;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_1_2;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_1_2_3;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_1_2_3_4;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_2_3;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_2_3_N;
import static com.adobe.dx.aep.aerospike.poc.uis.TestData.EDGE_2_4_5;
import com.adobe.dx.aep.aerospike.poc.uis.tx.AerospikeKVClient;
import com.adobe.dx.aep.poc.uis.aerospike.entities.AerospikeGraph;
import com.adobe.dx.aep.poc.uis.core.entities.Edge;
import com.adobe.dx.aep.poc.uis.core.entities.Operation;
import com.adobe.dx.aep.poc.uis.core.entities.RecordValue;
import com.adobe.dx.aep.poc.uis.core.services.UISGraphUtils;
import com.adobe.dx.aep.skaluskar.poc.ces.types.ThreadContext;
import com.aerospike.client.Host;
import junit.framework.TestCase;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class UISAerospikeGraphWriterTest extends TestCase {

    private static final UISAerospikeGraphReader reader = new UISAerospikeGraphReader();
    private static final UISAerospikeGraphWriter writer = new UISAerospikeGraphWriter((ThreadContext)null, null);
    private static final AerospikeClientWithTxnSupport AEROSPIKE_CLIENT_WITH_TXN_SUPPORT;

    static {
        AerospikeKVClient.initAerospikeClient(null, null);
        AEROSPIKE_CLIENT_WITH_TXN_SUPPORT = AerospikeKVClient.AEROSPIKE_CLIENT;
    }

    @Override
    protected void setUp() throws Exception {
        AEROSPIKE_CLIENT_WITH_TXN_SUPPORT.truncate(null, UISAerospikeConstants.NAMESPACE, null, null);
        super.setUp();
    }

    public void testConstructor() throws Exception {
        Host host = new Host(UISAerospikeConstants.AEROSPIKE_SERVER_IP, UISAerospikeConstants.AEROSPIKE_SERVER_PORT);
        UISAerospikeGraphWriter uisWriter =  new UISAerospikeGraphWriter(UISAerospikeConstants.NAMESPACE, host);

        UISAerospikeGraphReader uisReader =  new UISAerospikeGraphReader(UISAerospikeConstants.NAMESPACE, host);
        uisWriter.createEdge("INIT-0,INIT-1");
        AerospikeGraph graph = uisReader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = uisReader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());
    }
    
    public void testCreate() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());
    }

    public void testCreateReverseEdge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-0");
        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());
        assertTrue(graph.getGraphData().containsEdge(new Edge("INIT-0","INIT-1")));
        assertTrue(graph.getGraphData().containsEdge(new Edge("INIT-1","INIT-0")));
    }

    public void testCreateDuplicateEdge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-1");
        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());
        assertTrue(graph.getGraphData().containsEdge(new Edge("INIT-0","INIT-1")));
        assertTrue(graph.getGraphData().containsEdge(new Edge("INIT-1","INIT-0")));
    }

    private void assertEquals(Set<String> expected, Set<String> actual)  {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    public void testCreateGraph() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());
    }

    public void testUpdateGraph() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2, graph.getGraphData().getAllNodes());
    }

    public void testDeleteEdge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-2");
        writer.deleteEdge("INIT-0,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());
    }

    public void testBulkUpdate() throws Exception {
        String edge1 = "INIT-0,INIT-1";
        String edge2 = "INIT-1,INIT-2";
        String edge3 = "INIT-2,INIT-3";
        String edge4 = "INIT-2,INIT-4";
        String edge5 = "INIT-4,INIT-5";
        String edge6 = "INIT-5,INIT-2";

        LinkedHashMap<String, Operation> operationMap = new LinkedHashMap<>();
        operationMap.put(edge1, Operation.CREATE);
        operationMap.put(edge2, Operation.CREATE);
        operationMap.put(edge3, Operation.CREATE);
        writer.bulkUpdate(operationMap);
        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        operationMap = new LinkedHashMap<>();
        operationMap.put(edge2, Operation.DELETE);
        operationMap.put(edge4, Operation.CREATE);
        operationMap.put(edge5, Operation.CREATE);
        operationMap.put(edge3, Operation.DELETE);
        writer.bulkUpdate(operationMap);

        graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());
        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_4_5, graph.getGraphData().getAllNodes());

        operationMap = new LinkedHashMap<>();
        operationMap.put(edge1, Operation.CREATE);
        operationMap.put(edge3, Operation.DELETE);
        Map<Edge, Set<RecordValue>> updates = writer.bulkUpdate(operationMap);
        assertNull(updates.get(UISGraphUtils.getEdge(edge1)));
        assertNull(updates.get(UISGraphUtils.getEdge(edge3)));

        graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());
        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_4_5, graph.getGraphData().getAllNodes());

        operationMap = new LinkedHashMap<>();
        operationMap.put(edge6, Operation.CREATE);
        operationMap.put(edge3, Operation.DELETE);
        updates = writer.bulkUpdate(operationMap);
        assertNotNull(updates.get(UISGraphUtils.getEdge(edge6)));

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_4_5, graph.getGraphData().getAllNodes());
    }

    public void testDeleteGraph() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.deleteEdge("INIT-0,INIT-1");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertNull(graph);
    }

    public void testSplitGraph() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-4");
        writer.createEdge("INIT-4,INIT-5");
        writer.createEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-0,INIT-4");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-4");
        assertEquals(TestData.EDGE_4_5, graph.getGraphData().getAllNodes());
    }

    public void testMergeGraph() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");
        writer.createEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2_3, graph.getGraphData().getAllNodes());
    }

    public void testUpdate() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_0_1_2, graph.getGraphData().getAllNodes());
    }

    public void testUpdateDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");

        //update
        writer.createEdge("INIT-0,INIT-NN-0");
        //delete
        writer.deleteEdge("INIT-0,INIT-1");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_N, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-NN-0");
        assertEquals(EDGE_0_N, graph.getGraphData().getAllNodes());
    }

    
    public void testMerge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");

        //merge
        writer.createEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testDuplicateMerge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");

        //merge
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_0_1_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testMergeDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");

        //merge
        writer.createEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testMergeSplit() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");

        //merge
        writer.createEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testSplitDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-0,INIT-2"); //split
        writer.deleteEdge("INIT-0,INIT-1");//delete

        AerospikeGraph graph = reader.readNode("INIT-0");
      //  assertEquals(EDGE_0_1_2_3, graph.getRecordValue().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_1_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_1_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testNoMerge() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testInvalidDelete() throws Exception {

        writer.deleteEdge("INIT-12,INIT-22");

        AerospikeGraph graph = reader.readNode("INIT-12");
        assertNull(graph);

        graph = reader.readNode("INIT-22");
        assertNull(graph);
    }

    
    public void testInvalidDelete2() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-12,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testDuplicateDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testSplit() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testDuplicateSplit() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-1,INIT-2");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testInvalidSplit() throws Exception {

        writer.deleteEdge("INIT-12,INIT-22");

        AerospikeGraph graph = reader.readNode("INIT-12");
        assertNull(graph);

        graph = reader.readNode("INIT-22");
        assertNull(graph);
    }

    
    public void testInvalidSplit2() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-12,INIT-0");

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());
    }

    
    public void testSplitUpdate() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");

        writer.deleteEdge("INIT-1,INIT-2"); //split
        writer.createEdge("INIT-3,INIT-NN-0");//update

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3_N, graph.getGraphData().getAllNodes()); // still pointing to older version

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3_N, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-NN-0");
        assertEquals(EDGE_2_3_N, graph.getGraphData().getAllNodes());
    }

    
    public void testSplitSplit() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");
        writer.createEdge("INIT-3,INIT-4");

        writer.deleteEdge("INIT-1,INIT-2"); //split
        writer.deleteEdge("INIT-3,INIT-4"); //split

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_2_3, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-4");
        //assertNull(graph);
    }

    public void testCycleGraphNoSplit() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");
        writer.createEdge("INIT-3,INIT-4");
        writer.createEdge("INIT-4,INIT-1");

        writer.deleteEdge("INIT-1,INIT-2"); //no split

        AerospikeGraph graph = reader.readNode("INIT-0");
        assertEquals(EDGE_0_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_0_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_0_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_0_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-4");
        assertEquals(EDGE_0_1_2_3_4, graph.getGraphData().getAllNodes());
    }

    public void testDeleteDelete() throws Exception {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-1,INIT-2");
        writer.createEdge("INIT-2,INIT-3");
        writer.createEdge("INIT-2,INIT-4");

        writer.deleteEdge("INIT-3,INIT-4"); //delete
        writer.deleteEdge("INIT-0,INIT-1");//delete

        AerospikeGraph graph = reader.readNode("INIT-0");
     //   assertEquals(EDGE_0_1_2_3_4, graph.getRecordValue().getAllNodes());

        graph = reader.readNode("INIT-1");
        assertEquals(EDGE_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertEquals(EDGE_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-3");
        assertEquals(EDGE_1_2_3_4, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-4");
        assertEquals(EDGE_1_2_3_4, graph.getGraphData().getAllNodes());
    }


    @Override
    protected void tearDown() throws Exception {
        AEROSPIKE_CLIENT_WITH_TXN_SUPPORT.truncate(null, UISAerospikeConstants.NAMESPACE, null, null);
        super.tearDown();
    }
}