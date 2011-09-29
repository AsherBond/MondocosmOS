

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.GVector2F;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.graph.GGraph;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


public class GVectorial2DSymbolsRenderer
         implements
            IVectorial2DSymbolsRenderer {

   private static final Comparator<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> PRIORITY_COMPARATOR;

   static {
      PRIORITY_COMPARATOR = new Comparator<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>() {
         @Override
         public int compare(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> o1,
                            final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> o2) {
            final int priority1 = o1.getPriority();
            final int priority2 = o2.getPriority();
            if (priority1 > priority2) {
               return 1;
            }
            else if (priority1 < priority2) {
               return -1;
            }
            else {
               //               final int position1 = o1.getPosition();
               //               final int position2 = o2.getPosition();
               //               if (position1 > position2) {
               //                  return 1;
               //               }
               //               else if (position1 < position2) {
               //                  return -1;
               //               }
               return 0;
            }
         }
      };
   }


   private final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>              _nonGroupableSymbols;
   private final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>              _groupableSymbols;

   private final boolean                                                                                  _clusterSymbols;
   private final double                                                                                   _lodMinSize;
   private final boolean                                                                                  _debugRendering;
   private final boolean                                                                                  _renderLODIgnores;

   private final IVectorial2DDrawer                                                                       _drawer;
   private final boolean                                                                                  _verbose;


   public GVectorial2DSymbolsRenderer(final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> nonGroupableSymbols,
                                      final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> groupableSymbols,
                                      final ISymbolizer2D symbolizer,
                                      final IVectorial2DDrawer drawer,
                                      final boolean verbose) {
      _nonGroupableSymbols = nonGroupableSymbols;
      _groupableSymbols = groupableSymbols;

      _clusterSymbols = symbolizer.isClusterSymbols();
      _lodMinSize = symbolizer.getLODMinSize();
      _debugRendering = symbolizer.isDebugRendering();
      _renderLODIgnores = symbolizer.isRenderLODIgnores();

      _drawer = drawer;
      _verbose = verbose;
   }


   @Override
   public void draw() {
      if (_clusterSymbols && !_groupableSymbols.isEmpty()) {
         drawSymbolsInClusters();
      }
      else {
         final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> allSymbols = new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
                  _nonGroupableSymbols.size() + _groupableSymbols.size());

         allSymbols.addAll(_groupableSymbols);
         _groupableSymbols.clear(); // release some memory

         allSymbols.addAll(_nonGroupableSymbols);
         _nonGroupableSymbols.clear(); // release some memory

         drawSymbolsIndividually(allSymbols);
      }
   }


   private void drawSymbolsIndividually(final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols) {
      Collections.sort(symbols, PRIORITY_COMPARATOR);

      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols) {
         drawSymbol(symbol);
      }

      if (_verbose) {
         System.out.println("  - Rendered " + symbols.size() + " symbols");
      }
   }


   private void drawSymbol(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol) {
      if (symbol != null) {
         symbol.draw(_drawer, _lodMinSize, _debugRendering, _renderLODIgnores);
      }
   }


   private void drawSymbolsInClusters() {

      final GSymbol2DList allSymbols = new GSymbol2DList(_groupableSymbols.size() + _nonGroupableSymbols.size());

      final Collection<Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clusters = createClusters(
               _groupableSymbols, false);

      final int groupableSymbolsCount = _groupableSymbols.size();
      _groupableSymbols.clear(); // release some memory

      int symbolsInClustersCount = 0;
      for (final Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster : clusters) {
         final int clusterSize = cluster.size();
         symbolsInClustersCount += clusterSize;

         if (clusterSize == 0) {
            continue;
         }
         else if (clusterSize == 1) {
            allSymbols.add(GCollections.theOnlyOne(cluster));
         }
         else {
            allSymbols.addAll(createClusterSymbols(cluster));
         }
      }

      GAssert.isTrue(symbolsInClustersCount == groupableSymbolsCount, "clustered");

      if (_verbose) {
         System.out.println("  - Clustered " + groupableSymbolsCount + " symbols in " + clusters.size() + " clusters");
      }


      allSymbols.addAll(_nonGroupableSymbols);
      _nonGroupableSymbols.clear(); // release some memory


      drawSymbolsIndividually(allSymbols.getSymbols());
   }


   private GSymbol2DList createClusterSymbols(final Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster) {


      if (isHomogenous(cluster)) {
         final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = cluster.iterator().next();
         return exemplar.createGroupSymbols(cluster);
      }


      final Collection<Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clustersByGroups = createClusters(
               cluster, true);

      final GSymbol2DList result = new GSymbol2DList();

      int symbolsInClustersCount = 0;
      for (final Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> clusterByGroup : clustersByGroups) {
         final int clusterSize = clusterByGroup.size();
         symbolsInClustersCount += clusterSize;

         if (clusterSize == 0) {
            continue;
         }
         else if (clusterSize == 1) {
            result.add(GCollections.theOnlyOne(clusterByGroup));
         }
         else {
            final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> exemplar = clusterByGroup.iterator().next();

            result.addAll(exemplar.createGroupSymbols(clusterByGroup));
         }
      }

      GAssert.isTrue(symbolsInClustersCount == cluster.size(), "clustered");

      return result;

   }


   private static boolean isHomogenous(final Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster) {
      final Iterator<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> iterator = cluster.iterator();

      final Class<? extends GSymbol2D> klass = iterator.next().getClass();
      while (iterator.hasNext()) {
         if (klass != iterator.next().getClass()) {
            return false;
         }
      }

      return true;
   }


   private static Collection<Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> createClusters(final Collection<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> symbols,
                                                                                                                        final boolean considerIsGroupableWith) {
      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(false, 20, 10,
               GGeometryNTreeParameters.BoundsPolicy.MINIMUM, false);

      final GGeometryQuadtree<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbolsQuadtree = new GGeometryQuadtree<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>(
               "Symbols Quadtree",
               null,
               symbols,
               new IFunction<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>() {
                  @Override
                  public Collection<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> apply(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> element) {
                     return Collections.singleton(element.getGeometry());
                  }
               }, parameters);


      final GGraph<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> graph = new GGraph<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               symbols);

      for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol : symbols) {
         final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> neighborhood = calculateNeighborhood(
                  symbolsQuadtree, symbol, considerIsGroupableWith);
         for (final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> neighbor : neighborhood) {
            graph.addBidirectionalEdge(symbol, neighbor);
         }
      }

      final Collection<Set<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> clusters = graph.getConnectedGroupsOfNodes();

      return clusters;
   }


   private static List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> calculateNeighborhood(final GGeometryQuadtree<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbolsQuadtree,
                                                                                                                    final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> symbol,
                                                                                                                    final boolean considerIsGroupableWith) {

      if (!symbol.isGroupable()) {
         return Collections.emptyList();
      }

      final GAxisAlignedRectangle symbolBounds = toRoundedInt(symbol.getBounds());
      if (symbolBounds == null) {
         return Collections.emptyList();
      }
      final int symbolPriority = symbol.getPriority();
      final double minOverlapArea = symbolBounds.area() * 0.25;

      final List<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> neighborhood = new LinkedList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

      symbolsQuadtree.breadthFirstAcceptVisitor(
               symbolBounds,
               new IGTBreadFirstVisitor<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>() {
                  @Override
                  public void visitOctree(final GGeometryNTree<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> octree) {
                  }


                  @Override
                  public void visitInnerNode(final GGTInnerNode<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> inner) {
                     processNode(inner);
                  }


                  @Override
                  public void visitLeafNode(final GGTLeafNode<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> leaf) {
                     processNode(leaf);
                  }


                  private void processNode(final GGTNode<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node) {

                     for (final GElementGeometryPair<IVector2, GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> elementAndGeometry : node.getElements()) {
                        final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> element = elementAndGeometry.getElement();
                        if (element != symbol) {
                           if (element.isGroupable()) {
                              if (element.getPriority() == symbolPriority) {
                                 if (!considerIsGroupableWith || symbol.isGroupableWith(element)) {
                                    final GAxisAlignedRectangle geometryBounds = toRoundedInt(element.getBounds());
                                    if (symbolBounds.touchesBounds(geometryBounds)) {
                                       if (symbolBounds.intersection(geometryBounds).area() >= minOverlapArea) {
                                          neighborhood.add(element);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               });

      return neighborhood;
   }


   private static GAxisAlignedRectangle toRoundedInt(final GAxisAlignedRectangle rectangle) {
      if (rectangle == null) {
         return null;
      }

      return new GAxisAlignedRectangle(toRoundedInt(rectangle._lower), toRoundedInt(rectangle._upper));
   }


   private static IVector2 toRoundedInt(final IVector2 vector) {
      return new GVector2F(GMath.toRoundedInt(vector.x()), GMath.toRoundedInt(vector.y()));
   }


}
