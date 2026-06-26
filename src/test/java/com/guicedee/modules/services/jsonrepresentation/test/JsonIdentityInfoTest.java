package com.guicedee.modules.services.jsonrepresentation.test;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.guicedee.modules.services.jsonrepresentation.IJsonRepresentation;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that Jackson object-identity and reference annotations behave correctly
 * under the GuicedEE Jackson 3 mapper configuration (field visibility ANY,
 * getter/setter NONE, NON_NULL inclusion).
 * <p>
 * Covers:
 * <ul>
 *     <li>{@code @JsonIdentityInfo} with {@code PropertyGenerator} (bidirectional cycle)</li>
 *     <li>{@code @JsonIdentityInfo} with {@code IntSequenceGenerator} (self cycle)</li>
 *     <li>{@code @JsonIdentityReference(alwaysAsId = true)}</li>
 *     <li>{@code @JsonManagedReference} / {@code @JsonBackReference}</li>
 *     <li>{@code @JsonIgnoreProperties} for breaking back-references</li>
 * </ul>
 */
class JsonIdentityInfoTest
{
    private final ObjectMapper mapper = IJsonRepresentation.getObjectMapper();

    //
    // @JsonIdentityInfo with PropertyGenerator — bidirectional parent/child cycle
    //

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Node
    {
        public int id;
        public String name;
        public Node parent;
        public List<Node> children = new ArrayList<>();

        public Node() {}

        public Node(int id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void identityInfoSerializesCycleWithoutInfiniteRecursion() throws Exception
    {
        Node root = new Node(1, "root");
        Node child = new Node(2, "child");
        root.children.add(child);
        child.parent = root;

        // Without @JsonIdentityInfo this would StackOverflow; with it, it succeeds.
        String json = mapper.writeValueAsString(root);
        assertNotNull(json);
        assertTrue(json.contains("\"id\""), json);
        // The back-reference to the parent is rendered as the parent's id (1), not a full object.
        assertTrue(json.contains("\"parent\":1"), () -> "Expected parent rendered as id reference: " + json);
    }

    @Test
    void identityInfoRoundTripReconstructsObjectGraph() throws Exception
    {
        Node root = new Node(1, "root");
        Node child = new Node(2, "child");
        root.children.add(child);
        child.parent = root;

        String json = mapper.writeValueAsString(root);
        Node back = mapper.readValue(json, Node.class);

        assertEquals(1, back.id);
        assertEquals(1, back.children.size());
        Node backChild = back.children.get(0);
        assertEquals(2, backChild.id);
        // The identity reference must resolve to the SAME parent instance (cycle preserved).
        assertSame(back, backChild.parent);
    }

    //
    // @JsonIdentityInfo with IntSequenceGenerator — self-referential cycle
    //

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
    public static class GraphNode
    {
        public String label;
        public GraphNode next;

        public GraphNode() {}

        public GraphNode(String label)
        {
            this.label = label;
        }
    }

    @Test
    void intSequenceGeneratorHandlesTwoNodeCycle() throws Exception
    {
        GraphNode a = new GraphNode("a");
        GraphNode b = new GraphNode("b");
        a.next = b;
        b.next = a;

        String json = mapper.writeValueAsString(a);
        assertTrue(json.contains("\"@id\""), () -> "Expected @id markers: " + json);

        GraphNode back = mapper.readValue(json, GraphNode.class);
        assertEquals("a", back.label);
        assertEquals("b", back.next.label);
        assertSame(back, back.next.next, "Cycle must be reconstructed to the same instance");
    }

    //
    // @JsonIdentityReference(alwaysAsId = true)
    //

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    public static class Employee
    {
        public int id;
        public String name;

        @JsonIdentityReference(alwaysAsId = true)
        public Employee manager;

        public Employee() {}

        public Employee(int id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void identityReferenceAlwaysAsIdSerializesManagerAsId() throws Exception
    {
        Employee manager = new Employee(10, "Manager");
        Employee report = new Employee(20, "Report");
        report.manager = manager;

        String json = mapper.writeValueAsString(report);
        // manager must appear purely as an id reference (10), never as a nested object.
        assertTrue(json.contains("\"manager\":10"), () -> "Expected manager as id: " + json);
        assertFalse(json.contains("\"Manager\""), () -> "Manager object should not be inlined: " + json);
    }

    //
    // @JsonManagedReference / @JsonBackReference
    //

    public static class Department
    {
        public String name;

        @JsonManagedReference
        public List<Worker> workers = new ArrayList<>();

        public Department() {}

        public Department(String name)
        {
            this.name = name;
        }
    }

    public static class Worker
    {
        public String name;

        @JsonBackReference
        public Department department;

        public Worker() {}

        public Worker(String name)
        {
            this.name = name;
        }
    }

    @Test
    void managedAndBackReferenceOmitBackLinkAndRestoreOnRead() throws Exception
    {
        Department dept = new Department("Engineering");
        Worker w1 = new Worker("Ada");
        Worker w2 = new Worker("Linus");
        w1.department = dept;
        w2.department = dept;
        dept.workers.add(w1);
        dept.workers.add(w2);

        String json = mapper.writeValueAsString(dept);
        assertTrue(json.contains("Ada"), json);
        // The back reference (worker -> department) must be omitted to avoid the cycle.
        assertFalse(json.contains("\"department\""), () -> "Back reference should be omitted: " + json);

        Department back = mapper.readValue(json, Department.class);
        assertEquals(2, back.workers.size());
        // Deserialization must re-link the back reference.
        assertSame(back, back.workers.get(0).department);
        assertSame(back, back.workers.get(1).department);
    }

    //
    // @JsonIgnoreProperties for breaking a back-reference cycle
    //

    public static class Order
    {
        public String ref;
        public List<Line> lines = new ArrayList<>();

        public Order() {}

        public Order(String ref)
        {
            this.ref = ref;
        }
    }

    @JsonIgnoreProperties("order")
    public static class Line
    {
        public String sku;
        public Order order;

        public Line() {}

        public Line(String sku)
        {
            this.sku = sku;
        }
    }

    @Test
    void jsonIgnorePropertiesBreaksCycle() throws Exception
    {
        Order order = new Order("ORD-1");
        Line line = new Line("SKU-1");
        line.order = order;
        order.lines.add(line);

        String json = mapper.writeValueAsString(order);
        assertTrue(json.contains("SKU-1"), json);
        assertFalse(json.contains("\"order\""), () -> "order property should be ignored on Line: " + json);
    }
}


