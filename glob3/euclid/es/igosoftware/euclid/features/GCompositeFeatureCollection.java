

package es.igosoftware.euclid.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GCompositeIterator;
import es.igosoftware.util.GLazyValue;
import es.igosoftware.util.GPredicate;
import es.igosoftware.util.IFunction;


public class GCompositeFeatureCollection<

VectorT extends IVector<VectorT, ?>,

FeatureGeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

>
         implements
            IGlobeFeatureCollection<VectorT, FeatureGeometryT> {


   private final List<IGlobeFeatureCollection<VectorT, FeatureGeometryT>> _children;

   private final GLazyValue<Long>                                         _lazySize         = new GLazyValue<Long>() {
                                                                                               @Override
                                                                                               protected Long calculateValue() {
                                                                                                  return calculateSize();
                                                                                               }
                                                                                            };
   private final GLazyValue<GAxisAlignedOrthotope<VectorT, ?>>            _lazyBounds       = new GLazyValue<GAxisAlignedOrthotope<VectorT, ?>>() {
                                                                                               @Override
                                                                                               protected GAxisAlignedOrthotope<VectorT, ?> calculateValue() {
                                                                                                  return calculateBounds();
                                                                                               }
                                                                                            };
   private final GLazyValue<EnumSet<GGeometryType>>                       _lazyGeometryType = new GLazyValue<EnumSet<GGeometryType>>() {
                                                                                               @Override
                                                                                               protected EnumSet<GGeometryType> calculateValue() {
                                                                                                  return calculateGeometryType();
                                                                                               }
                                                                                            };
   private final GLazyValue<List<GField>>                                 _lazyFields       = new GLazyValue<List<GField>>() {
                                                                                               @Override
                                                                                               protected List<GField> calculateValue() {
                                                                                                  return calculateFields();
                                                                                               }
                                                                                            };


   public GCompositeFeatureCollection(final IGlobeFeatureCollection<VectorT, FeatureGeometryT>... children) {
      GAssert.notEmpty(children, "children");

      _children = Arrays.asList(children);
      validateChildren();
   }


   public GCompositeFeatureCollection(final List<IGlobeFeatureCollection<VectorT, FeatureGeometryT>> children) {
      GAssert.notEmpty(children, "children");

      // copy to protect from external modifications
      _children = new ArrayList<IGlobeFeatureCollection<VectorT, FeatureGeometryT>>(children);
      validateChildren();
   }


   private void validateChildren() {
      final GProjection projection = _children.get(0).getProjection();
      for (int i = 1; i < _children.size(); i++) {
         final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child = _children.get(i);
         if (child.getProjection() != projection) {
            throw new RuntimeException("The children has not the same projection");
         }
      }
   }


   @Override
   public Iterator<IGlobeFeature<VectorT, FeatureGeometryT>> iterator() {
      final Iterable<Iterator<IGlobeFeature<VectorT, FeatureGeometryT>>> childrenIterators = GCollections.collect(
               _children,
               new IFunction<IGlobeFeatureCollection<VectorT, FeatureGeometryT>, Iterator<IGlobeFeature<VectorT, FeatureGeometryT>>>() {
                  @Override
                  public Iterator<IGlobeFeature<VectorT, FeatureGeometryT>> apply(final IGlobeFeatureCollection<VectorT, FeatureGeometryT> element) {
                     return element.iterator();
                  }
               });

      return new GCompositeIterator<IGlobeFeature<VectorT, FeatureGeometryT>>(childrenIterators);
   }


   @Override
   public void acceptVisitor(final IGlobeFeatureCollection.IFeatureVisitor<VectorT, FeatureGeometryT> visitor) {
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         child.acceptVisitor(visitor);
      }
   }


   @Override
   public boolean isEmpty() {
      return GCollections.allSatisfy(_children, new GPredicate<IGlobeFeatureCollection<VectorT, FeatureGeometryT>>() {
         @Override
         public boolean evaluate(final IGlobeFeatureCollection<VectorT, FeatureGeometryT> element) {
            return element.isEmpty();
         }
      });
   }


   @Override
   public long size() {
      return _lazySize.get().longValue();
   }


   private long calculateSize() {
      long size = 0;
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         size += child.size();
      }
      return size;
   }


   @Override
   public GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return _lazyBounds.get();
   }


   private GAxisAlignedOrthotope<VectorT, ?> calculateBounds() {
      final List<GAxisAlignedOrthotope<VectorT, ?>> childrenBounds = GCollections.collect(_children,
               new IFunction<IGlobeFeatureCollection<VectorT, FeatureGeometryT>, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> apply(final IGlobeFeatureCollection<VectorT, FeatureGeometryT> element) {
                     return element.getBounds();
                  }
               });

      return GAxisAlignedOrthotope.merge(childrenBounds);
   }


   @Override
   public EnumSet<GGeometryType> getGeometryType() {
      return _lazyGeometryType.get();
   }


   private EnumSet<GGeometryType> calculateGeometryType() {
      final EnumSet<GGeometryType> result = EnumSet.noneOf(GGeometryType.class);
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         result.addAll(child.getGeometryType());
      }
      return result;
   }


   @Override
   public GProjection getProjection() {
      return _children.get(0).getProjection();
   }


   @Override
   public IGlobeFeature<VectorT, FeatureGeometryT> get(final long index) {
      long acumIndex = 0;
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         final long childIndex = index - acumIndex;
         final long childSize = child.size();
         if (childIndex < childSize) {
            return child.get(childIndex);
         }
         acumIndex += childSize;
      }
      throw new IndexOutOfBoundsException("#" + index);
   }


   @Override
   public int getFieldsCount() {
      int fieldsCount = 0;
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         fieldsCount += child.getFieldsCount();
      }
      return fieldsCount;
   }


   @Override
   public int getFieldIndex(final String fieldName) {
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         final int childFieldIndex = child.getFieldIndex(fieldName);
         if (childFieldIndex >= 0) {
            return childFieldIndex;
         }
      }
      return -1;
   }


   @Override
   public List<GField> getFields() {
      return _lazyFields.get();
   }


   private List<GField> calculateFields() {
      final List<GField> fields = new ArrayList<GField>();
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         fields.addAll(child.getFields());
      }
      return fields;
   }


   @Override
   public boolean hasField(final String fieldName) {
      for (final IGlobeFeatureCollection<VectorT, FeatureGeometryT> child : _children) {
         if (child.hasField(fieldName)) {
            return true;
         }
      }
      return false;
   }


   @Override
   public String toString() {
      return "GCompositeFeatureCollection [children=" + _children.size() + ", size=" + size() + ", bounds=" + getBounds()
             + ", geometryType=" + getGeometryType() + "]";
   }


}
