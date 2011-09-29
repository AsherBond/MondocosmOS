

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.GGlobeFeature;
import es.igosoftware.euclid.features.GListFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeatureCollection.IFeatureVisitor;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.IFunction;


public class GSnapPoints<

VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         implements
            IAlgorithm<

            VectorT, GSnapPoints.Parameters<VectorT, FeatureGeometryT>,

            VectorT, GSnapPoints.Result<VectorT>

            > {

   public static class Parameters<VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

   FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>>
            implements
               IAlgorithmParameters<VectorT> {
      private final IGlobeFeatureCollection<VectorT, VectorT>          _source;
      private final IGlobeFeatureCollection<VectorT, FeatureGeometryT> _snapping;
      private final double                                             _tolerance;


      public Parameters(final IGlobeFeatureCollection<VectorT, VectorT> source,
                        final IGlobeFeatureCollection<VectorT, FeatureGeometryT> snapping,
                        final double tolerance) {
         _source = source;
         _snapping = snapping;
         _tolerance = tolerance;
      }
   }

   public static class Result<VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>>
            implements
               IAlgorithmResult<VectorT> {
      private final IGlobeFeatureCollection<VectorT, VectorT> _features;


      private Result(final IGlobeFeatureCollection<VectorT, VectorT> features) {
         _features = features;
      }


      public IGlobeFeatureCollection<VectorT, VectorT> getFeatures() {
         return _features;
      }


   }


   @Override
   public Result<VectorT> apply(final Parameters<VectorT, FeatureGeometryT> parameters) {
      final IGlobeFeatureCollection<VectorT, VectorT> source = parameters._source;
      final IGlobeFeatureCollection<VectorT, FeatureGeometryT> snapping = parameters._snapping;
      final double tolerance = parameters._tolerance;

      final GGeometryNTree<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> index = createIndex(snapping);

      final ArrayList<IGlobeFeature<VectorT, VectorT>> features = new ArrayList<IGlobeFeature<VectorT, VectorT>>();

      final IFeatureVisitor<VectorT, VectorT> visitor = new IFeatureVisitor<VectorT, VectorT>() {

         @Override
         public void visit(final IGlobeFeature<VectorT, VectorT> feature,
                           final long i) {
            VectorT point = feature.getDefaultGeometry().getCentroid();
            point = snap(point, index, tolerance);
            features.add(new GGlobeFeature<VectorT, VectorT>(point, feature.getAttributes()));
         }
      };
      source.acceptVisitor(visitor);

      final GListFeatureCollection<VectorT, VectorT> ret = new GListFeatureCollection<VectorT, VectorT>(GProjection.EPSG_23030,
               new ArrayList<GField>(), features);
      return new Result<VectorT>(ret);
   }


   private VectorT snap(final VectorT point,
                        final GGeometryNTree<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> index,
                        final double tolerance) {

      final ClosestPointVisitor visitor = new ClosestPointVisitor(point, tolerance);
      index.breadthFirstAcceptVisitor(visitor);

      final VectorT closestPoint = visitor.closestPoint;
      return closestPoint != null ? closestPoint : point;
   }


   private GGeometryNTree<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> createIndex(final IGlobeFeatureCollection<VectorT, FeatureGeometryT> snapTo) {
      final IFunction<IGlobeFeature<VectorT, FeatureGeometryT>, Collection<? extends FeatureGeometryT>> function = new IFunction<IGlobeFeature<VectorT, FeatureGeometryT>, Collection<? extends FeatureGeometryT>>() {

         @Override
         public Collection<? extends FeatureGeometryT> apply(final IGlobeFeature<VectorT, FeatureGeometryT> element) {
            return Collections.singleton(element.getDefaultGeometry());
         }
      };
      return new SnapIndex<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT>(snapTo, function);
   }


   @Override
   public String getName() {
      return "Snap points";
   }


   @Override
   public String getDescription() {
      // TODO Auto-generated method stub
      return null;
   }

   public static class SnapIndex<

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   >
            extends
               GGeometryNTree<VectorT, ElementT, GeometryT> {

      private static final String INDEX_NAME = "Snap index";


      protected SnapIndex(final Iterable<? extends ElementT> elements,
                          final IFunction<ElementT, Collection<? extends GeometryT>> transformer) {
         super(INDEX_NAME, null, elements, transformer, new GGeometryNTreeParameters(false, 20, 10,
                  GGeometryNTreeParameters.BoundsPolicy.MINIMUM, false));
      }


      @Override
      protected String getTreeName() {
         return INDEX_NAME;
      }
   }

   private final class ClosestPointVisitor
            implements
               IGTBreadFirstVisitor<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> {
      private final VectorT _point;
      private VectorT       closestPoint = null;
      private double        minDistance;


      private ClosestPointVisitor(final VectorT point,
                                  final double tolerance) {
         _point = point;
         minDistance = tolerance * tolerance;
      }


      @Override
      public void visitOctree(final GGeometryNTree<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> octree) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = octree.getRoot().getBounds();
         if ((bounds != null) && (bounds.expandedByDistance(minDistance).distance(_point) < minDistance)) {
            processElements(octree.getRoot().getElements());
         }
      }


      @Override
      public void visitInnerNode(final GGTInnerNode<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> inner) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = inner.getBounds();
         if ((bounds != null) && bounds.expandedByDistance(minDistance).contains(_point)) {
            processElements(inner.getElements());
         }
      }


      private void processElements(final Collection<GElementGeometryPair<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT>> elements) {
         for (final GElementGeometryPair<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> elementGeometryPair : elements) {
            final VectorT closestPointCandidate = elementGeometryPair.getGeometry().closestPoint(_point);
            final double distance = closestPointCandidate.squaredDistance(_point);
            if (distance < minDistance) {
               closestPoint = closestPointCandidate;
               minDistance = distance;
            }
         }
      }


      @Override
      public void visitLeafNode(final GGTLeafNode<VectorT, IGlobeFeature<VectorT, FeatureGeometryT>, FeatureGeometryT> leaf) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = leaf.getBounds();
         if ((bounds != null) && bounds.expandedByDistance(minDistance).contains(_point)) {
            processElements(leaf.getAllElements());
         }
      }
   }

}
