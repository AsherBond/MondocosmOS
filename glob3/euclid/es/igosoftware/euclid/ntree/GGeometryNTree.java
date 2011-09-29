

package es.igosoftware.euclid.ntree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.GGTInnerNode.GeometriesDistribution;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.IFunction;


public abstract class GGeometryNTree<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GLoggerObject {


   private final String                                     _name;
   private final long                                       _elementsCount;

   private final GGeometryNTreeParameters                   _parameters;

   //   private final byte                                       _dimensions;
   private final GAxisAlignedOrthotope<VectorT, ?>          _geometriesBounds;
   private final GAxisAlignedOrthotope<VectorT, ?>          _bounds;

   private final GGTInnerNode<VectorT, ElementT, GeometryT> _root;


   protected GGeometryNTree(final String name,
                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                            final Iterable<? extends ElementT> elements,
                            final IFunction<ElementT, Collection<? extends GeometryT>> transformer,
                            final GGeometryNTreeParameters parameters) {
      _name = name;
      _parameters = parameters;

      final String nameMsg = (_name == null) ? "" : "\"" + _name + "\" ";
      logInfo("Creating " + getTreeName() + " " + nameMsg);


      VectorT geometriesLower = null;
      VectorT geometriesUpper = null;
      final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> pairs = new ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>>();
      for (final ElementT element : elements) {
         final Collection<? extends GeometryT> geometries = transformer.apply(element);
         for (final GeometryT geometry : geometries) {
            pairs.add(new GElementGeometryPair<VectorT, ElementT, GeometryT>(element, geometry));

            final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = geometry.getBounds().asAxisAlignedOrthotope();
            geometriesLower = (geometriesLower == null) ? geometryBounds._lower : geometriesLower.min(geometryBounds._lower);
            geometriesUpper = (geometriesUpper == null) ? geometryBounds._upper : geometriesUpper.max(geometryBounds._upper);
         }
      }
      pairs.trimToSize();


      _elementsCount = pairs.size();

      if (_elementsCount == 0) {
         _geometriesBounds = null;
         _bounds = null;


         final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> empty = Collections.emptyList();

         _root = new GGTInnerNode<VectorT, ElementT, GeometryT>(null, _bounds, empty, empty, 0, parameters, null) {
            @Override
            public GGeometryNTree<VectorT, ElementT, GeometryT> getNTree() {
               return GGeometryNTree.this;
            }


            @Override
            public boolean isRoot() {
               return true;
            }
         };
      }
      else {
         final GProgress progress = new GProgress(_elementsCount) {
            @Override
            public void informProgress(final long stepsDone,
                                       final double percent,
                                       final long elapsed,
                                       final long estimatedMsToFinish) {
               if (_parameters._verbose) {
                  logInfo("  Creating " + getTreeName() + " " + nameMsg
                          + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
               }
            }
         };

         _geometriesBounds = GAxisAlignedOrthotope.create(geometriesLower, geometriesUpper);

         _bounds = initializeBounds(bounds);


         final GeometriesDistribution<VectorT, ElementT, GeometryT> distribution = GGTInnerNode.distributeGeometries(_bounds,
                  pairs);

         _root = new GGTInnerNode<VectorT, ElementT, GeometryT>(null, _bounds, distribution.getOwnGeometries(),
                  distribution.getGeometriesToDistribute(), 0, parameters, progress) {
            @Override
            public GGeometryNTree<VectorT, ElementT, GeometryT> getNTree() {
               return GGeometryNTree.this;
            }


            @Override
            public boolean isRoot() {
               return true;
            }
         };
      }

      validate();

      if (_parameters._verbose) {
         showStatistics();
      }
   }


   private void validate() {
      _root.validate();
   }


   private GAxisAlignedOrthotope<VectorT, ?> initializeBounds(final GAxisAlignedOrthotope<VectorT, ?> givenBounds) {

      if ((givenBounds != null) && (_parameters._boundsPolicy != GGeometryNTreeParameters.BoundsPolicy.GIVEN)) {
         throw new IllegalArgumentException("Can't provide a bounds with a policy other that GIVEN");
      }


      switch (_parameters._boundsPolicy) {
         case GIVEN:
            return returnGivenBounds(givenBounds);

         case MINIMUM:
            return _geometriesBounds;

         case DIMENSIONS_MULTIPLE_OF_SMALLEST:
            return multipleOfSmallestDimention(_geometriesBounds);
         case DIMENSIONS_MULTIPLE_OF_SMALLEST_AND_CENTERED:
            return centerBounds(multipleOfSmallestDimention(_geometriesBounds));

         case REGULAR:
            return calculateRegularBounds(_geometriesBounds);
         case REGULAR_AND_CENTERED:
            return centerBounds(calculateRegularBounds(_geometriesBounds));
      }

      throw new IllegalArgumentException("Must not reach here");
   }


   private GAxisAlignedOrthotope<VectorT, ?> centerBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT delta = bounds.getCenter().sub(_geometriesBounds.getCenter());
      return bounds.translatedBy(delta.negated());
   }


   private GAxisAlignedOrthotope<VectorT, ?> calculateRegularBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds._extent;

      final byte dimensions = bounds.dimensions();

      double biggestExtension = Double.NEGATIVE_INFINITY;
      for (byte i = 0; i < dimensions; i++) {
         final double ext = extent.get(i);
         if (ext > biggestExtension) {
            biggestExtension = ext;
         }
      }

      final VectorT newUpper = bounds._lower.add(biggestExtension);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   private GAxisAlignedOrthotope<VectorT, ?> multipleOfSmallestDimention(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds._extent;
      final byte dimensions = bounds.dimensions();

      double smallestExtension = Double.POSITIVE_INFINITY;
      for (byte i = 0; i < dimensions; i++) {
         final double ext = extent.get(i);
         if (ext < smallestExtension) {
            smallestExtension = ext;
         }
      }

      final VectorT newExtent = smallestBiggerMultipleOf(extent, smallestExtension);
      final VectorT newUpper = bounds._lower.add(newExtent);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   @SuppressWarnings("unchecked")
   private static <VectorT extends IVector<VectorT, ?>> VectorT smallestBiggerMultipleOf(final VectorT lower,
                                                                                         final double smallestExtension) {

      final byte dimensionsCount = lower.dimensions();

      final double[] dimensionsValues = new double[dimensionsCount];
      for (byte i = 0; i < dimensionsCount; i++) {
         dimensionsValues[i] = smallestBiggerMultipleOf(lower.get(i), smallestExtension);
      }

      return (VectorT) GVectorUtils.createD(dimensionsValues);
   }


   private static double smallestBiggerMultipleOf(final double value,
                                                  final double multiple) {
      if (GMath.closeTo(value, multiple)) {
         return multiple;
      }

      final int times = (int) (value / multiple);

      double result = times * multiple;
      if (value < 0) {
         if (result > value) {
            result -= multiple;
         }
      }
      else {
         if (result < value) {
            result += multiple;
         }
      }

      return result;
   }


   private GAxisAlignedOrthotope<VectorT, ?> returnGivenBounds(final GAxisAlignedOrthotope<VectorT, ?> givenBounds) {
      if (givenBounds == null) {
         throw new IllegalArgumentException("Can't use policy GIVEN without providing a bounds");
      }

      if (!_geometriesBounds.isFullInside(givenBounds)) {
         throw new IllegalArgumentException("The given bounds is not big enough to hold all the elements");
      }

      return givenBounds;
   }


   private void showStatistics() {

      return;
      /*logInfo("---------------------------------------------------------------");

      if (_name != null) {
         logInfo(" " + getTreeName() + " \"" + _name + "\":");
      }

      logInfo(" ");
      logInfo("  Elements Bounds: " + _geometriesBounds);
      logInfo("  Elements Extent: " + _geometriesBounds._extent);

      logInfo(" ");
      logInfo("  Bounds: " + _bounds);
      logInfo("  Extent: " + _bounds._extent);

      final GLongHolder innerNodesCounter = new GLongHolder(0);
      final GLongHolder leafNodesCounter = new GLongHolder(0);
      final GLongHolder elementsInLeafNodesCounter = new GLongHolder(0);
      final GLongHolder maxElementsCountInLeafNodes = new GLongHolder(0);
      final GLongHolder minElementsCountInLeafNodes = new GLongHolder(Integer.MAX_VALUE);

      final GLongHolder elementsInInnerNodesCounter = new GLongHolder(0);
      final GLongHolder maxElementsCountInInnerNodes = new GLongHolder(0);
      final GLongHolder minElementsCountInInnerNodes = new GLongHolder(Integer.MAX_VALUE);

      final GLongHolder totalDepth = new GLongHolder(0);
      final GLongHolder maxDepth = new GLongHolder(0);
      final GLongHolder minDepth = new GLongHolder(Integer.MAX_VALUE);

      final GHolder<VectorT> totalLeafExtentHolder = new GHolder<VectorT>(null);

      breadthFirstAcceptVisitor(new IGTBreadFirstVisitor<VectorT, ElementT, GeometryT>() {

         @Override
         public void visitOctree(final GGeometryNTree<VectorT, ElementT, GeometryT> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<VectorT, ElementT, GeometryT> inner) {
            innerNodesCounter.increment();

            final int elementsCount = inner.getElementsCount();
            elementsInInnerNodesCounter.increment(elementsCount);

            maxElementsCountInInnerNodes.max(elementsCount);
            minElementsCountInInnerNodes.min(elementsCount);


         }


         @Override
         public void visitLeafNode(final GGTLeafNode<VectorT, ElementT, GeometryT> leaf) {
            leafNodesCounter.increment();

            final int elementsCount = leaf.getElementsCount();
            elementsInLeafNodesCounter.increment(elementsCount);

            maxElementsCountInLeafNodes.max(elementsCount);
            minElementsCountInLeafNodes.min(elementsCount);

            final int depth = leaf.getDepth();
            totalDepth.increment(depth);
            minDepth.min(depth);
            maxDepth.max(depth);


            final VectorT leafExtent = leaf.getBounds().getExtent();
            final VectorT totalLeafExtent = totalLeafExtentHolder.get();
            if (totalLeafExtent == null) {
               totalLeafExtentHolder.set(leafExtent);
            }
            else {
               totalLeafExtentHolder.set(totalLeafExtentHolder.get().add(leafExtent));
            }
         }
      });


      logInfo(" ");
      final long totalNodes = innerNodesCounter.get() + leafNodesCounter.get();
      logInfo("  Nodes: " + totalNodes);
      logInfo("    Inner: " + innerNodesCounter.get() + " (" + GStringUtils.formatPercent(innerNodesCounter.get(), totalNodes)
              + ")");
      logInfo("    Leaf : " + leafNodesCounter.get() + " (" + GStringUtils.formatPercent(leafNodesCounter.get(), totalNodes)
              + ")");


      logInfo(" ");
      final long totalElements = elementsInInnerNodesCounter.get() + elementsInLeafNodesCounter.get();
      final long duplicates = totalElements - _elementsCount;
      logInfo(" Distributed Elements: " + totalElements + "  (duplicates: " + duplicates + " ("
              + GStringUtils.formatPercent(duplicates, totalElements) + "))");


      logInfo(" ");
      logInfo("  Elements in Inners: " + elementsInInnerNodesCounter.get() + " ("
              + GStringUtils.formatPercent(elementsInInnerNodesCounter.get(), totalElements) + ")");
      logInfo("  Elements per Inner: min=" + minElementsCountInInnerNodes.get() + ", max=" + maxElementsCountInInnerNodes.get()
              + ", average=" + ((float) elementsInInnerNodesCounter.get() / innerNodesCounter.get()));

      logInfo(" ");
      logInfo("  Elements in Leafs: " + elementsInLeafNodesCounter.get() + " ("
              + GStringUtils.formatPercent(elementsInLeafNodesCounter.get(), totalElements) + ")");
      logInfo("  Elements per Leaf: min=" + minElementsCountInLeafNodes.get() + ", max=" + maxElementsCountInLeafNodes.get()
              + ", average=" + ((float) elementsInLeafNodesCounter.get() / leafNodesCounter.get()));

      logInfo("  Average leaf extent: " + totalLeafExtentHolder.get().div(leafNodesCounter.get()));


      logInfo(" ");
      logInfo("  Depth: Max=" + maxDepth.get() + ", Min=" + minDepth.get() + ", Average="
              + ((double) totalDepth.get() / leafNodesCounter.get()));

      logInfo("---------------------------------------------------------------");*/
   }


   protected abstract String getTreeName();


   @Override
   public boolean logVerbose() {
      return _parameters._verbose;
   }


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, ElementT, GeometryT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, ElementT, GeometryT> visitor) {
      if (!_bounds.touchesBounds(region)) {
         return;
      }

      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(region, visitor);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT, GeometryT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.depthFirstAcceptVisitor(visitor);

         visitor.finishedOctree(this);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   //   public byte getDimensions() {
   //      return _dimensions;
   //   }


   public int getLeafNodesCount() {
      return _root.getLeafNodesCount();
   }


   public int getInnerNodesCount() {
      return _root.getInnerNodesCount();
   }


   public GGTInnerNode<VectorT, ElementT, GeometryT> getRoot() {
      return _root;
   }


}
