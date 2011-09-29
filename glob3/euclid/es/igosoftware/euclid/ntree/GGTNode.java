

package es.igosoftware.euclid.ntree;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.logging.GLoggerObject;


public abstract class GGTNode<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GLoggerObject {


   protected final GGTInnerNode<VectorT, ElementT, GeometryT>                     _parent;
   protected final GAxisAlignedOrthotope<VectorT, ?>                              _bounds;
   protected final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> _elements;


   //   private GAxisAlignedOrthotope<VectorT, ?>                                      _minimumBounds;


   protected GGTNode(final GGTInnerNode<VectorT, ElementT, GeometryT> parent,
                     final GAxisAlignedOrthotope<VectorT, ?> bounds,
                     final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements) {
      _parent = parent;
      _bounds = bounds;
      _elements = elements;
   }


   public final GGTInnerNode<VectorT, ElementT, GeometryT> getParent() {
      return _parent;
   }


   public final GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return _bounds;
   }


   //   public final GAxisAlignedOrthotope<VectorT, ?> getMinimumBounds() {
   //      if (_minimumBounds == null) {
   //         _minimumBounds = calculateMinimumBounds().clamp(_bounds);
   //      }
   //      return _minimumBounds;
   //   }


   //   private GAxisAlignedOrthotope<VectorT, ?> calculateMinimumBounds() {
   //      VectorT lower = null;
   //      VectorT upper = null;
   //
   //      for (final GElementGeometryPair<VectorT, ElementT, GeometryT> pair : getAllElements()) {
   //         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = pair.getGeometry().getBounds().asAxisAlignedOrthotope();
   //         final VectorT geometryLower = geometryBounds._lower;
   //         final VectorT geometryUpper = geometryBounds._upper;
   //         lower = (lower == null) ? geometryLower : lower.min(geometryLower);
   //         upper = (upper == null) ? geometryUpper : upper.max(geometryUpper);
   //      }
   //
   //      return GAxisAlignedOrthotope.create(lower, upper);
   //   }


   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final GGTInnerNode<VectorT, ElementT, GeometryT> getRoot() {
      if (_parent == null) {
         return (GGTInnerNode<VectorT, ElementT, GeometryT>) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final boolean logVerbose() {
      return getNTree().logVerbose();
   }


   public GGeometryNTree<VectorT, ElementT, GeometryT> getNTree() {
      return _parent.getNTree();
   }


   public final String getId() {
      if (_parent == null) {
         return "";
      }

      final byte myId = _parent.getChildIndex(this);

      final String parentId = _parent.getId();
      if ((parentId == null) || parentId.isEmpty()) {
         return Byte.toString(myId);
      }

      return parentId + "-" + myId;
   }


   public abstract void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT, GeometryT> visitor)
                                                                                                                 throws IGTBreadFirstVisitor.AbortVisiting;


   public abstract int getLeafNodesCount();


   public abstract int getInnerNodesCount();


   public final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getElements() {
      if (_elements == null) {
         return Collections.emptyList();
      }

      return Collections.unmodifiableCollection(_elements);
   }


   public final int getElementsCount() {
      return (_elements == null) ? 0 : _elements.size();
   }


   public abstract Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getAllElements();


   public abstract int getAllElementsCount();


   protected abstract void validate();


   @Override
   public abstract String toString();

}
