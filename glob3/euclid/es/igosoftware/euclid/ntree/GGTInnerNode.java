

package es.igosoftware.euclid.ntree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GProgress;


public class GGTInnerNode<

VectorT extends IVector<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, ElementT, GeometryT> {


   private final GGTNode<VectorT, ElementT, GeometryT>[] _children;


   static class GeometriesDistribution<

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   >
            extends
               GPair<Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>>, Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>>> {

      private static final long serialVersionUID = 1L;


      private GeometriesDistribution(final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> ownElements,
                                     final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elementsToDistribute) {
         super(ownElements, elementsToDistribute);
      }


      Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getOwnGeometries() {
         return _first;
      }


      Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getGeometriesToDistribute() {
         return _second;
      }
   }


   static final <

   VectorT extends IVector<VectorT, ?>,

   BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > GeometriesDistribution<VectorT, ElementT, GeometryT> distributeGeometries(final BoundsT bounds,
                                                                               final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> pairs) {

      if (pairs.size() == 1) {
         final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> empty = Collections.emptyList();
         return new GeometriesDistribution<VectorT, ElementT, GeometryT>(pairs, empty);
      }


      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = bounds.subdividedAtCenter();

      final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> ownElements = new ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>>();
      final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> elementsToDistribute = new ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>>();


      for (final GElementGeometryPair<VectorT, ElementT, GeometryT> pair : pairs) {
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = pair.getBounds();
         int geometryInChildrenCounter = 0;
         for (final GAxisAlignedOrthotope<VectorT, ?> childBounds : childrenBounds) {
            if (childBounds.touches(geometryBounds)) {
               geometryInChildrenCounter++;
            }
         }

         if (geometryInChildrenCounter == 0) {
            throw new RuntimeException("WARNING >> element don't added: " + pair);
         }
         else if (geometryInChildrenCounter == 1) {
            elementsToDistribute.add(pair);
         }
         else {
            ownElements.add(pair);
         }
      }

      if ((ownElements.size() + elementsToDistribute.size()) != pairs.size()) {
         throw new RuntimeException("Invalid Distribution: ownElements=" + ownElements.size() + ", elementsToDistribute="
                                    + elementsToDistribute.size() + ", totalGeometries=" + pairs.size());
      }

      ownElements.trimToSize();
      elementsToDistribute.trimToSize();

      return new GeometriesDistribution<VectorT, ElementT, GeometryT>(ownElements, elementsToDistribute);
   }


   GGTInnerNode(final GGTInnerNode<VectorT, ElementT, GeometryT> parent,
                final GAxisAlignedOrthotope<VectorT, ?> bounds,
                final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> ownGeometries,
                final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> geometriesToDistribute,
                final int depth,
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds, ownGeometries.isEmpty() ? null : ownGeometries);

      _children = initializeChildren(geometriesToDistribute, depth, parameters, progress);
   }


   private static class Division<

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > {

      private final GAxisAlignedOrthotope<VectorT, ?>                             _bounds;
      private final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> _elements = new ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>>();


      private Division(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
         _bounds = bounds;
      }


      private void addElement(final GElementGeometryPair<VectorT, ElementT, GeometryT> element) {
         _elements.add(element);
      }

   }


   private Division<VectorT, ElementT, GeometryT>[] createDivisionsByBounds(final GAxisAlignedOrthotope<VectorT, ?>[] bounds) {
      @SuppressWarnings({
         "unchecked"
      })
      final Division<VectorT, ElementT, GeometryT>[] result = (Division<VectorT, ElementT, GeometryT>[]) new Division<?, ?, ?>[bounds.length];

      for (int i = 0; i < bounds.length; i++) {
         result[i] = new Division<VectorT, ElementT, GeometryT>(bounds[i]);
      }

      return result;
   }


   private GGTNode<VectorT, ElementT, GeometryT>[] initializeChildren(final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements,
                                                                      final int depth,
                                                                      final GGeometryNTreeParameters parameters,
                                                                      final GProgress progress) {
      if (elements.isEmpty()) {
         @SuppressWarnings("unchecked")
         final GGTNode<VectorT, ElementT, GeometryT>[] empty = (GGTNode<VectorT, ElementT, GeometryT>[]) new GGTNode<?, ?, ?>[0];
         return empty;
      }

      final Division<VectorT, ElementT, GeometryT>[] divisions = createDivisionsByBounds(_bounds.subdividedAtCenter());

      for (final GElementGeometryPair<VectorT, ElementT, GeometryT> pair : elements) {
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = pair.getBounds();

         int geometryAddedCounter = 0;
         for (final Division<VectorT, ElementT, GeometryT> division : divisions) {
            if (division._bounds.touches(geometryBounds)) {
               division.addElement(pair);
               geometryAddedCounter++;
            }
         }

         if (geometryAddedCounter == 0) {
            throw new RuntimeException("WARNING >> element don't added: " + pair);
         }
         else if (geometryAddedCounter > 1) {
            throw new RuntimeException("WARNING >> element added " + geometryAddedCounter + " times: " + pair);
            //            progress.incrementSteps(geometryAddedCounter - 1);
         }
      }

      // clear some memory: the geometries at this point was split into divisions and it safe to clear the given geometries collection
      elements.clear();


      final GGTNode<VectorT, ElementT, GeometryT>[] result;
      if (parameters._multiThread) {
         result = multiThreadChildrenCreation(depth, parameters, progress, divisions);
      }
      else {
         result = singleThreadChildrenCreation(depth, parameters, progress, divisions);
      }

      return GCollections.rtrim(result);
   }


   private GGTNode<VectorT, ElementT, GeometryT>[] singleThreadChildrenCreation(final int depth,
                                                                                final GGeometryNTreeParameters parameters,
                                                                                final GProgress progress,
                                                                                final Division<VectorT, ElementT, GeometryT>[] divisions) {
      @SuppressWarnings({
         "unchecked"
      })
      final GGTNode<VectorT, ElementT, GeometryT>[] result = (GGTNode<VectorT, ElementT, GeometryT>[]) new GGTNode<?, ?, ?>[divisions.length];

      for (int i = 0; i < divisions.length; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = divisions[i]._bounds;
         final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> childElements = divisions[i]._elements;
         childElements.trimToSize();

         result[i] = createChildNode(childBounds, childElements, depth + 1, parameters, progress);
      }

      return result;
   }


   private GGTNode<VectorT, ElementT, GeometryT>[] multiThreadChildrenCreation(final int depth,
                                                                               final GGeometryNTreeParameters parameters,
                                                                               final GProgress progress,
                                                                               final Division<VectorT, ElementT, GeometryT>[] divisions) {
      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      @SuppressWarnings("unchecked")
      final Future<GGTNode<VectorT, ElementT, GeometryT>>[] futures = (Future<GGTNode<VectorT, ElementT, GeometryT>>[]) new Future<?>[divisions.length];

      for (int i = 0; i < divisions.length; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = divisions[i]._bounds;
         final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> childElements = divisions[i]._elements;
         childElements.trimToSize();

         futures[i] = executor.submit(new Callable<GGTNode<VectorT, ElementT, GeometryT>>() {
            @Override
            public GGTNode<VectorT, ElementT, GeometryT> call() {
               return createChildNode(childBounds, childElements, depth + 1, parameters, progress);
            }
         });
      }

      @SuppressWarnings({
         "unchecked"
      })
      final GGTNode<VectorT, ElementT, GeometryT>[] result = (GGTNode<VectorT, ElementT, GeometryT>[]) new GGTNode<?, ?, ?>[futures.length];
      for (int i = 0; i < futures.length; i++) {
         try {
            result[i] = futures[i].get();
         }
         catch (final InterruptedException e) {
            e.printStackTrace();
         }
         catch (final ExecutionException e) {
            e.printStackTrace();
         }
      }

      return result;
   }


   private GGTNode<VectorT, ElementT, GeometryT> createChildNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                 final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements,
                                                                 final int depth,
                                                                 final GGeometryNTreeParameters parameters,
                                                                 final GProgress progress) {

      if (elements.isEmpty()) {
         return null;
      }


      if (acceptLeafNodeCreation(bounds, elements, depth, parameters)) {
         return createLeafNode(bounds, elements, progress);
      }

      final GeometriesDistribution<VectorT, ElementT, GeometryT> distribution = distributeGeometries(bounds, elements);

      if (distribution.getGeometriesToDistribute().isEmpty()) {
         return createLeafNode(bounds, elements, progress);
      }

      return new GGTInnerNode<VectorT, ElementT, GeometryT>(this, bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), depth, parameters, progress);

   }


   private GGTNode<VectorT, ElementT, GeometryT> createLeafNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements,
                                                                final GProgress progress) {
      progress.stepsDone(elements.size());
      return new GGTLeafNode<VectorT, ElementT, GeometryT>(this, bounds, elements);
   }


   @SuppressWarnings("unchecked")
   private boolean acceptLeafNodeCreation(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                          final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> elements,
                                          final int depth,
                                          final GGeometryNTreeParameters parameters) {
      // if the bounds extent is too small, force a leaf creation to avoid problems trying to subdivide the bounds
      final VectorT boundsExtent = bounds._extent;
      for (byte i = 0; i < boundsExtent.dimensions(); i++) {
         if (boundsExtent.get(i) <= 0.00000001) {
            return true;
         }
      }

      return parameters._acceptLeafNodeCreationPolicy.acceptLeafNodeCreation(depth, bounds, elements);
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, ElementT, GeometryT> visitor)
                                                                                                          throws IGTBreadFirstVisitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, ElementT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, ElementT, GeometryT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode<VectorT, ElementT, GeometryT> current = queue.removeFirst();

         if ((region != null) && !current.getBounds().touchesBounds(region)) {
            continue;
         }

         if (current instanceof GGTInnerNode) {
            final GGTInnerNode<VectorT, ElementT, GeometryT> currentInner = (GGTInnerNode<VectorT, ElementT, GeometryT>) current;

            visitor.visitInnerNode(currentInner);

            for (final GGTNode<VectorT, ElementT, GeometryT> child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GGTLeafNode) {
            final GGTLeafNode<VectorT, ElementT, GeometryT> currentLeaf = (GGTLeafNode<VectorT, ElementT, GeometryT>) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, ElementT, GeometryT> visitor)
                                                                                                          throws IGTBreadFirstVisitor.AbortVisiting {

      breadthFirstAcceptVisitor(null, visitor);

   }


   final byte getChildIndex(final GGTNode<VectorT, ElementT, GeometryT> node) {
      for (byte i = 0; i < _children.length; i++) {
         if (node == _children[i]) {
            return i;
         }
      }
      return -1;
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT, GeometryT> visitor)
                                                                                                        throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);
   }


   @Override
   public final int getLeafNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            counter += child.getLeafNodesCount();
         }
      }
      return counter;
   }


   @Override
   public final int getInnerNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            counter += child.getInnerNodesCount();
         }
      }
      return counter + 1;
   }


   @Override
   public final int getAllElementsCount() {
      int result = 0;
      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            result += child.getAllElementsCount();
         }
      }
      return result + getElementsCount();
   }


   @Override
   public final Collection<GElementGeometryPair<VectorT, ElementT, GeometryT>> getAllElements() {
      final ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>> result = new ArrayList<GElementGeometryPair<VectorT, ElementT, GeometryT>>();
      result.addAll(getElements());

      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            result.addAll(child.getAllElements());
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public List<GGTNode<VectorT, ElementT, GeometryT>> getChildren() {
      final ArrayList<GGTNode<VectorT, ElementT, GeometryT>> result = new ArrayList<GGTNode<VectorT, ElementT, GeometryT>>(
               _children.length);
      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child != null) {
            result.add(child);
         }
      }
      return Collections.unmodifiableList(result);
   }


   @Override
   protected void validate() {
      if (isRoot()) {
         if (_parent != null) {
            System.err.println("The root inner node has a parent");
         }
      }
      else {
         if (_parent == null) {
            System.err.println("A non-root inner node has not a parent");
         }
      }

      for (final GGTNode<VectorT, ElementT, GeometryT> child : _children) {
         if (child == null) {
            continue;
         }

         if (child.getParent() != this) {
            System.err.println("INVALID PARENT");
         }
         child.validate();
      }
   }


   public boolean isRoot() {
      return false;
   }


   @Override
   public String toString() {
      return "GGTInnerNode [id=" + getId() + ", depth=" + getDepth() + ", bounds=" + getBounds() + ", elements="
             + getElementsCount() + "]";
   }
}
