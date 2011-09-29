

package es.igosoftware.graph;

import java.util.Set;

import junit.framework.TestCase;
import es.igosoftware.util.GCollections;


public class GGraphTest
         extends
            TestCase {

   public void testGraph() {
      final GGraph<String> graph = new GGraph<String>("Apple", "Orange", "Lemon", "Watermelon");

      assertEquals(4, graph.getNodesCount());
      assertEquals(0, graph.getEdgesCount());

      graph.addBidirectionalEdge("Apple", "Orange");
      graph.addBidirectionalEdge("Lemon", "Orange");
      graph.addBidirectionalEdge("Watermelon", "Orange");
      assertEquals(6, graph.getEdgesCount());


      assertTrue(graph.isAdjacent("Apple", "Orange"));
      assertTrue(graph.isAdjacent("Orange", "Apple"));

      assertTrue(graph.isAdjacent("Lemon", "Orange"));
      assertTrue(graph.isAdjacent("Orange", "Lemon"));

      assertTrue(graph.isAdjacent("Watermelon", "Orange"));
      assertTrue(graph.isAdjacent("Orange", "Watermelon"));


      final Set<String> lemonNeighbors = graph.getNeighbors("Lemon");
      assertEquals(1, lemonNeighbors.size());
      assertEquals("Orange", GCollections.theOnlyOne(lemonNeighbors));


      final Set<String> orangeNeighbors = graph.getNeighbors("Orange");
      assertEquals(3, orangeNeighbors.size());
      assertTrue(orangeNeighbors.contains("Apple"));
      assertTrue(orangeNeighbors.contains("Lemon"));
      assertTrue(orangeNeighbors.contains("Watermelon"));

   }


}
