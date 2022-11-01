package com.adobe.dx.aep.fdb.poc.uis;

import com.adobe.dx.aep.fdb.poc.uis.entities.FDBGraph;
import com.apple.foundationdb.Transaction;
import junit.framework.TestCase;

import java.util.Set;

public class UISFDBGraphWriterTest extends TestCase {

    private final UISFDBGraphWriter writer = new UISFDBGraphWriter();
    private final UISFDBGraphReader reader = new UISFDBGraphReader();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        reader.getDb().run((Transaction tr) -> {
            tr.clear(reader.getGraph());
            tr.clear(reader.getRouting());
            return null;
        });
    }

    private void assertEquals(Set<String> expected, Set<String> actual)  {
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
    }

    public void testCreateGraph() {
        writer.createEdge("INIT-0,INIT-1");
        FDBGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());
    }

    public void testUpdateGraph() {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-2");

        FDBGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2, graph.getGraphData().getAllNodes());
    }

    public void testDeleteEdge() {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-2");
        writer.deleteEdge("INIT-0,INIT-2");

        FDBGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-2");
        assertNull(graph);
    }

    public void testSpitGraph() {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-0,INIT-4");
        writer.createEdge("INIT-4,INIT-5");
        writer.createEdge("INIT-1,INIT-2");
        writer.deleteEdge("INIT-0,INIT-4");

        FDBGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2, graph.getGraphData().getAllNodes());

        graph = reader.readNode("INIT-4");
        assertEquals(TestData.EDGE_4_5, graph.getGraphData().getAllNodes());
    }

    public void testMergeGraph() {
        writer.createEdge("INIT-0,INIT-1");
        writer.createEdge("INIT-2,INIT-3");
        writer.createEdge("INIT-1,INIT-2");

        FDBGraph graph = reader.readNode("INIT-0");
        assertEquals(TestData.EDGE_0_1_2_3, graph.getGraphData().getAllNodes());
    }
}
