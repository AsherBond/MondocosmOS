

package es.unex.s3xtante.modules.sextante.bindings;

public interface WWVectorLayerFactory {

   public abstract WWVectorLayer create(String sName,
                                        int iShapeType,
                                        Class<?>[] sFields,
                                        String[] fields,
                                        String filename,
                                        Object crs);

}
