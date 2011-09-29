

package es.igosoftware.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.IFunction;


public class GGraph<NodeT> {


   public static class Edge<NodeT> {
      public final NodeT _from;
      public final NodeT _to;


      private Edge(final NodeT from,
                   final NodeT to) {
         GAssert.notNull(from, "from");
         GAssert.notNull(to, "to");

         _from = from;
         _to = to;
      }


      public NodeT either() {
         return _from;
      }


      public NodeT other(final NodeT node) {
         GAssert.isTrue((node == _from) || (node == _to), "invalid node");

         return (node == _from) ? _to : _from;
      }


      @Override
      public String toString() {
         return "Edge [" + _from + " -> " + _to + "]";
      }


      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + _from.hashCode();
         result = prime * result + _to.hashCode();
         return result;
      }


      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         @SuppressWarnings("unchecked")
         final Edge<NodeT> other = (Edge<NodeT>) obj;
         if (_from == null) {
            if (other._from != null) {
               return false;
            }
         }
         else if (!_from.equals(other._from)) {
            return false;
         }
         if (_to == null) {
            if (other._to != null) {
               return false;
            }
         }
         else if (!_to.equals(other._to)) {
            return false;
         }
         return true;
      }
   }


   private final Set<NodeT>                   _nodes;
   private final Map<NodeT, Set<Edge<NodeT>>> _nodeEdges = new HashMap<NodeT, Set<Edge<NodeT>>>();


   public GGraph(final Collection<NodeT> nodes) {
      GAssert.notEmpty(nodes, "nodes");

      _nodes = new HashSet<NodeT>(nodes);
   }


   public GGraph(final NodeT... nodes) {
      GAssert.notEmpty(nodes, "nodes");

      _nodes = new HashSet<NodeT>();
      for (final NodeT node : nodes) {
         _nodes.add(node);
      }
   }


   public void addNode(final NodeT node) {
      GAssert.notNull(node, "node");

      _nodes.add(node);
   }


   public void addBidirectionalEdge(final NodeT node1,
                                    final NodeT node2) {
      checkNodeExists(node1);
      checkNodeExists(node2);

      getOrCreateNeighbors(node1).add(new Edge<NodeT>(node1, node2));

      getOrCreateNeighbors(node2).add(new Edge<NodeT>(node2, node1));
   }


   protected Set<Edge<NodeT>> getOrCreateNeighbors(final NodeT node) {
      Set<Edge<NodeT>> neighbors = _nodeEdges.get(node);
      if (neighbors == null) {
         neighbors = new HashSet<Edge<NodeT>>();
         _nodeEdges.put(node, neighbors);
      }
      return neighbors;
   }


   private void checkNodeExists(final NodeT node) {
      if (!_nodes.contains(node)) {
         throw new RuntimeException("Node " + node + " not present in the graph");
      }
   }


   @Override
   public String toString() {
      return "GGraph [edges=" + getEdgesCount() + ", nodes=" + _nodes.size() + "]";
   }


   public void printStructure(final PrintStream out) {
      final Set<NodeT> visited = new HashSet<NodeT>();

      System.out.println("-------------------------------------------------------------------");
      out.println(this);
      out.println("Edges:");
      for (final Set<Edge<NodeT>> edges : _nodeEdges.values()) {
         for (final Edge<NodeT> edge : edges) {
            out.println("  " + edge._from + " -> " + edge._to);
            visited.add(edge._from);
            visited.add(edge._to);
         }
      }

      if (visited.size() != _nodes.size()) {
         out.println("Unconnected nodes:");
         for (final NodeT node : _nodes) {
            if (!visited.contains(node)) {
               out.println("  " + node);
            }
         }
      }
      System.out.println("-------------------------------------------------------------------");

   }


   public Set<Edge<NodeT>> getEdges(final NodeT node) {
      final Set<Edge<NodeT>> edges = _nodeEdges.get(node);
      if (edges == null) {
         return Collections.emptySet();
      }
      return edges;
   }


   public Set<NodeT> getNeighbors(final NodeT node) {
      final Set<NodeT> neighbors = GCollections.collect(getEdges(node), new IFunction<Edge<NodeT>, NodeT>() {
         @Override
         public NodeT apply(final Edge<NodeT> edge) {
            return edge._to;
         }
      });
      return Collections.unmodifiableSet(neighbors);
   }


   public int getNodesCount() {
      return _nodes.size();
   }


   public long getEdgesCount() {
      long count = 0;
      for (final Set<Edge<NodeT>> nodeAndEdges : _nodeEdges.values()) {
         count += nodeAndEdges.size();
      }
      return count;
   }


   public boolean isAdjacent(final NodeT node1,
                             final NodeT node2) {
      final Set<Edge<NodeT>> neighbors = _nodeEdges.get(node1);
      if (neighbors == null) {
         return false;
      }

      return GCollections.anySatisfy(neighbors, new GPredicate<Edge<NodeT>>() {
         @Override
         public boolean evaluate(final Edge<NodeT> element) {
            return element._to.equals(node2);
         }
      });
   }


   public Collection<Set<NodeT>> getConnectedGroupsOfNodes() {
      final Collection<Set<NodeT>> result = new ArrayList<Set<NodeT>>();

      final LinkedList<NodeT> toProcess = new LinkedList<NodeT>(_nodes);

      while (!toProcess.isEmpty()) {
         final NodeT current = toProcess.removeFirst();

         final Set<NodeT> group = new HashSet<NodeT>();
         result.add(group);

         depthFirstAcceptVisitor(current, true, false, new IGraphVisitor<NodeT>() {
            @Override
            public void visitNode(final NodeT node) {
               group.add(node);
               toProcess.remove(node);
            }
         });
      }

      return result;
   }


   public Collection<GGraph<NodeT>> getConnectedGraphs() {
      final Collection<Set<NodeT>> groupsOfNodes = getConnectedGroupsOfNodes();
      final List<GGraph<NodeT>> result = new ArrayList<GGraph<NodeT>>(groupsOfNodes.size());

      for (final Set<NodeT> groupOfNodes : groupsOfNodes) {
         final GGraph<NodeT> graph = new GGraph<NodeT>(groupOfNodes);
         result.add(graph);

         for (final NodeT node : groupOfNodes) {
            for (final Entry<NodeT, Set<Edge<NodeT>>> nodeAndEdges : _nodeEdges.entrySet()) {
               if (node == nodeAndEdges.getKey()) {
                  for (final Edge<NodeT> edge : nodeAndEdges.getValue()) {
                     graph.addBidirectionalEdge(edge._from, edge._to);
                  }
               }
            }
         }
      }

      return result;
   }


   public void depthFirstAcceptVisitor(final NodeT node,
                                       final boolean preVisit,
                                       final boolean postVisit,
                                       final IGraphVisitor<NodeT> visitor) {
      final Set<NodeT> visited = new HashSet<NodeT>(_nodes.size());
      depthFirstAcceptVisitor(visited, node, preVisit, postVisit, visitor);
   }


   private void depthFirstAcceptVisitor(final Set<NodeT> visited,
                                        final NodeT node,
                                        final boolean preVisit,
                                        final boolean postVisit,
                                        final IGraphVisitor<NodeT> visitor) {
      visited.add(node);

      if (preVisit) {
         visitor.visitNode(node);
      }


      //      for (final NodeT neighbor : getNeighbors(node)) {
      //         if (!visited.contains(neighbor)) {
      //            depthFirstAcceptVisitor(visited, neighbor, preVisit, postVisit, visitor);
      //         }
      //      }
      final Set<Edge<NodeT>> edges = _nodeEdges.get(node);
      if (edges != null) {
         for (final Edge<NodeT> edge : edges) {
            final NodeT neighbor = edge._to;
            if (!visited.contains(neighbor)) {
               depthFirstAcceptVisitor(visited, neighbor, preVisit, postVisit, visitor);
            }
         }
      }


      if (postVisit) {
         visitor.visitNode(node);
      }
   }


   public Set<NodeT> getNodes() {
      return Collections.unmodifiableSet(_nodes);
   }


   public static void main(final String[] args) {
      System.out.println("GGraph 0.1");
      System.out.println("----------\n");

      final GGraph<String> graph = new GGraph<String>("Apple", "Orange", "Lemon", "Watermelon", "A", "B", "C", "Alone");
      graph.addBidirectionalEdge("Apple", "Orange");
      graph.addBidirectionalEdge("Lemon", "Orange");
      graph.addBidirectionalEdge("Watermelon", "Orange");

      graph.addBidirectionalEdge("A", "B");
      graph.addBidirectionalEdge("A", "C");


      System.out.println(graph);

      System.out.println();
      graph.printStructure(System.out);
      System.out.println();


      graph.depthFirstAcceptVisitor("Apple", true, false, new IGraphVisitor<String>() {
         @Override
         public void visitNode(final String node) {
            System.out.println(node);
         }
      });

      System.out.println();

      final Collection<Set<String>> connectedGroups = graph.getConnectedGroupsOfNodes();
      System.out.println(connectedGroups);

      final Collection<GGraph<String>> connectedGraphs = graph.getConnectedGraphs();
      for (final GGraph<String> subgraph : connectedGraphs) {
         System.out.println();
         subgraph.printStructure(System.out);
      }

   }

}
