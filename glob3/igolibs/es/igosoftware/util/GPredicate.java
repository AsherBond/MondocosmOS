

package es.igosoftware.util;

public abstract class GPredicate<ElementT>
         implements
            IPredicate<ElementT> {

   @Override
   public Boolean apply(final ElementT element) {
      final boolean result = evaluate(element);
      return Boolean.valueOf(result);
   }


}
