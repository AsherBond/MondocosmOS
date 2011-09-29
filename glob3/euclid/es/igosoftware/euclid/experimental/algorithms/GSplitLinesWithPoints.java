

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.features.IGlobeFeatureCollection.IFeatureVisitor;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTLeafNode;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.IPolygonalChain;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.IFunction;


public class GSplitLinesWithPoints<

VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

SegmentT extends GSegment<VectorT, SegmentT, ? extends IFiniteBounds<VectorT, ?>>,

BoundsT extends GAxisAlignedOrthotope<VectorT, BoundsT>,

FeatureLineT extends IPolygonalChain<VectorT, SegmentT, ? extends IFiniteBounds<VectorT, ?>>>
         implements
            IAlgorithm<

            VectorT, GSplitLinesWithPoints.Parameters<VectorT, FeatureLineT>,

            VectorT, GSplitLinesWithPoints.Result<VectorT, FeatureLineT>

            > {

   public static class Parameters<VectorT extends IVector<VectorT, ? extends IFiniteBounds<VectorT, ?>>,

   FeatureLineT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   >
            implements
               IAlgorithmParameters<VectorT> {
      private final IGlobeFeatureCollection<VectorT, FeatureLineT> _source;
      private final IGlobeFeatureCollection<VectorT, VectorT>      _splitting;
      private final double                                         _tolerance;


      public Parameters(final IGlobeFeatureCollection<VectorT, FeatureLineT> source,
                        final IGlobeFeatureCollection<VectorT, VectorT> splitting,
                        final double tolerance) {
         super();
         _source = source;
         _splitting = splitting;
         _tolerance = tolerance;
      }

   }

   public static class Result<VectorT extends IVector<VectorT, ?>,

   FeatureLineT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   >
            implements
               IAlgorithmResult<VectorT> {

      private final List<CutLine<VectorT, FeatureLineT>> _cutLines;


      public Result(final List<CutLine<VectorT, FeatureLineT>> cutLines) {
         _cutLines = cutLines;
      }


      public List<CutLine<VectorT, FeatureLineT>> getCutLines() {
         return _cutLines;
      }

   }

   public static class CutLine<VectorT extends IVector<VectorT, ?>,

   FeatureLineT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > {
      private final FeatureLineT _line;
      private final long         _lineId;
      private final long         _sourcePointId;
      private final long         _targetPointId;


      public CutLine(final FeatureLineT line,
                     final long lineId,
                     final long sourcePointId,
                     final long targetPointId) {
         super();
         _line = line;
         _lineId = lineId;
         _sourcePointId = sourcePointId;
         _targetPointId = targetPointId;
      }


      public FeatureLineT getLine() {
         return _line;
      }


      public long getLineId() {
         return _lineId;
      }


      public long getSourcePointId() {
         return _sourcePointId;
      }


      public long getTargetPointId() {
         return _targetPointId;
      }


   }


   @Override
   public Result<VectorT, FeatureLineT> apply(final Parameters<VectorT, FeatureLineT> params) {
      final IGlobeFeatureCollection<VectorT, VectorT> points = params._splitting;
      final IGlobeFeatureCollection<VectorT, FeatureLineT> lines = params._source;
      final double tolerance = params._tolerance;

      final GGeometryNTree<VectorT, UsablePoint, VectorT> index = createIndex(points);

      final ArrayList<CutLine<VectorT, FeatureLineT>> features = new ArrayList<CutLine<VectorT, FeatureLineT>>();

      final IFeatureVisitor<VectorT, FeatureLineT> visitor = new IFeatureVisitor<VectorT, FeatureLineT>() {

         @Override
         public void visit(final IGlobeFeature<VectorT, FeatureLineT> feature,
                           final long i) {
            final FeatureLineT geometry = feature.getDefaultGeometry();
            final List<CutLine<VectorT, FeatureLineT>> splitLines = splitLine(geometry, i, index, tolerance);
            features.addAll(splitLines);
         }
      };
      lines.acceptVisitor(visitor);

      return new Result<VectorT, FeatureLineT>(features);
   }


   private List<CutLine<VectorT, FeatureLineT>> splitLine(final FeatureLineT geometry,
                                                          final long lineId,
                                                          final GGeometryNTree<VectorT, UsablePoint, VectorT> index,
                                                          final double tolerance) {

      final ArrayList<CutLine<VectorT, FeatureLineT>> ret = new ArrayList<CutLine<VectorT, FeatureLineT>>();

      final List<SegmentT> edges = geometry.getEdges();
      ArrayList<VectorT> path = new ArrayList<VectorT>();
      if (!edges.isEmpty()) {
         path.add(edges.get(0)._from);
      }
      long lastPointId = -1;

      for (final SegmentT edge : edges) {
         final ClosestPointsVisitor visitor = new ClosestPointsVisitor(edge, tolerance);
         index.breadthFirstAcceptVisitor(visitor);

         final SortedSet<ComparableVectorT> closePoints = visitor._closestPoint;

         for (final ComparableVectorT closePoint : closePoints) {
            path.add(closePoint._point);
            final long currentPointId = closePoint._id;
            ret.add(addCurrentGeometryValues(path, lineId, lastPointId, currentPointId));
            path = new ArrayList<VectorT>();
            if (!closePoint._point.closeTo(edge._to)) {
               path.add(closePoint._point);
            }
            lastPointId = currentPointId;
         }

         path.add(edge._to);
      }

      final CutLine<VectorT, FeatureLineT> newLine = addCurrentGeometryValues(path, lineId, lastPointId, -1);
      if (newLine != null) {
         ret.add(newLine);
      }

      return ret;
   }


   private CutLine<VectorT, FeatureLineT> addCurrentGeometryValues(final List<VectorT> path,
                                                                   final long lineId,
                                                                   final long sourcePointId,
                                                                   final long targetPointId) {
      if (path.size() > 1) {
         @SuppressWarnings("unchecked")
         final VectorT[] points = path.toArray((VectorT[]) new IVector[0]);
         final IPolygonalChain<VectorT, SegmentT, BoundsT> geom = GShapeUtils.createPolygonalChain(false, points);
         @SuppressWarnings("unchecked")
         final CutLine<VectorT, FeatureLineT> ret = new CutLine<VectorT, FeatureLineT>((FeatureLineT) geom, lineId,
                  sourcePointId, targetPointId);
         return ret;
      }
      return null;
   }


   private GGeometryNTree<VectorT, UsablePoint, VectorT> createIndex(final IGlobeFeatureCollection<VectorT, VectorT> snapTo) {
      final IFunction<UsablePoint, Collection<? extends VectorT>> function = new IFunction<UsablePoint, Collection<? extends VectorT>>() {

         @Override
         public Collection<? extends VectorT> apply(final UsablePoint element) {
            return Collections.singleton(element._point);
         }

      };

      final ArrayList<UsablePoint> points = new ArrayList<UsablePoint>();
      long index = 0;
      for (final IGlobeFeature<VectorT, VectorT> iGlobeFeature : snapTo) {
         points.add(new UsablePoint(iGlobeFeature.getDefaultGeometry(), index));
         index++;
      }

      return new SnapIndex<VectorT, UsablePoint, VectorT>(points, function);
   }

   private class UsablePoint {
      private final VectorT _point;
      private boolean       _used = false;
      private final long    _id;


      private UsablePoint(final VectorT point,
                          final long id) {
         _point = point;
         _id = id;
      }


      public long getId() {
         return _id;
      }
   }


   @Override
   public String getName() {
      return "Split lines with points";
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

      private static final String INDEX_NAME = "SplitLinesWithPoints index";


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

   private final class ClosestPointsVisitor
            implements
               IGTBreadFirstVisitor<VectorT, UsablePoint, VectorT> {
      private final SegmentT                          _segment;
      private final SortedSet<ComparableVectorT>      _closestPoint = new TreeSet<ComparableVectorT>();
      private final double                            _tolerance;
      private final GAxisAlignedOrthotope<VectorT, ?> _segmentBounds;


      private ClosestPointsVisitor(final SegmentT segment,
                                   final double tolerance) {
         _segment = segment;
         _segmentBounds = _segment.getBounds().asAxisAlignedOrthotope();
         _tolerance = tolerance * tolerance;
      }


      @Override
      public void visitOctree(final GGeometryNTree<VectorT, UsablePoint, VectorT> octree) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = octree.getRoot().getBounds();
         if ((bounds != null) && (bounds.expandedByDistance(_tolerance).touches(_segmentBounds))) {
            processElements(octree.getRoot().getElements());
         }
      }


      @Override
      public void visitInnerNode(final GGTInnerNode<VectorT, UsablePoint, VectorT> inner) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = inner.getBounds();
         if ((bounds != null) && bounds.expandedByDistance(_tolerance).touches(_segmentBounds)) {
            processElements(inner.getElements());
         }
      }


      private void processElements(final Collection<GElementGeometryPair<VectorT, UsablePoint, VectorT>> elements) {
         for (final GElementGeometryPair<VectorT, UsablePoint, VectorT> elementGeometryPair : elements) {
            final VectorT point = elementGeometryPair.getGeometry();
            if (!elementGeometryPair.getElement()._used) {
               final VectorT closestPointCandidate = _segment.closestPoint(point);
               final double distance = closestPointCandidate.squaredDistance(point);
               if (distance < _tolerance) {
                  _closestPoint.add(new ComparableVectorT(closestPointCandidate, distance,
                           elementGeometryPair.getElement().getId()));
                  elementGeometryPair.getElement()._used = true;
               }
            }
         }
      }


      @Override
      public void visitLeafNode(final GGTLeafNode<VectorT, UsablePoint, VectorT> leaf) {
         final GAxisAlignedOrthotope<VectorT, ?> bounds = leaf.getBounds();
         if ((bounds != null) && bounds.expandedByDistance(_tolerance).touches(_segmentBounds)) {
            processElements(leaf.getAllElements());
         }
      }
   }

   private class ComparableVectorT
            implements
               Comparable<ComparableVectorT> {
      private final VectorT _point;
      private final double  _distance;
      private final long    _id;


      public ComparableVectorT(final VectorT point,
                               final double distance,
                               final long id) {
         this._point = point;
         this._distance = distance;
         this._id = id;
      }


      @Override
      public int compareTo(final ComparableVectorT that) {
         return (int) Math.signum(this._distance - that._distance);
      }
   }

}
