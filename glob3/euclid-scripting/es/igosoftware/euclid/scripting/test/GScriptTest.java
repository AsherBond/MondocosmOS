

package es.igosoftware.euclid.scripting.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import junit.framework.TestCase;
import es.igosoftware.euclid.experimental.algorithms.IAlgorithm;
import es.igosoftware.euclid.scripting.GIllegalScriptException;
import es.igosoftware.euclid.scripting.GScriptManager;
import es.igosoftware.io.GFileName;


public class GScriptTest
         extends
            TestCase {

   @Override
   protected void setUp() throws Exception {
      super.setUp();
      if (!GScriptManager.isInitialized()) {
         GScriptManager.initialize();
      }
   }


   public void testJythonNormalCase() throws Exception {
      final IAlgorithm alg = GScriptManager.getPythonAlgorithm(getScriptStream(GFileName.relative("normalCase.py")));
      assertTrue(alg.getName().equals("name"));
      assertTrue(alg.getDescription().equals("description"));
      @SuppressWarnings("unchecked")
      final Object result = alg.apply(new Integer(2));
      assertTrue(result.equals(3.14159));
   }


   public void testJythonNoAlgorithmVarSet() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream(GFileName.relative("noAlgorithmVarSet.py")));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   public void testJythonNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getPythonAlgorithm(getScriptStream(GFileName.relative("noIAlgorithmInstance.py")));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   public void testBeanshellNormalCase() throws Exception {
      final IAlgorithm alg = GScriptManager.getBeanshellAlgorithm(getScriptStream(GFileName.relative("normalCase.bsh")));
      assertTrue(alg.getName().equals("name"));
      assertTrue(alg.getDescription().equals("description"));
      @SuppressWarnings("unchecked")
      final Object result = alg.apply(new Integer(2));
      assertTrue(result.equals(3.14159));
   }


   public void testBeanshellNoAlgorithmVarSet() throws Exception {
      try {
         GScriptManager.getBeanshellAlgorithm(getScriptStream(GFileName.relative("noAlgorithmVarSet.bsh")));
         fail();
      }
      catch (final GIllegalScriptException e) {
         System.out.println(e.getMessage());
      }
   }


   public void testBeanshellNoIAlgorithmInstance() throws Exception {
      try {
         GScriptManager.getBeanshellAlgorithm(getScriptStream(GFileName.relative("noIAlgorithmInstance.bsh")));
         fail();
      }
      catch (final GIllegalScriptException e) {
      }
   }


   public void testSum() throws Exception {
      //      final IAlgorithm<?, ?, ?, ?> script = GScriptManager.getBeanshellAlgorithm(getScriptStream(GFileName.relative("realCase.bsh")));
      //      ?script.apply(null);
      fail();
   }


   private InputStream getScriptStream(final GFileName fileName) throws FileNotFoundException {
      final File file = GFileName.fromParts(GFileName.relative("es", "igosoftware", "euclid", "scripting", "test"), fileName).asFile();
      return new BufferedInputStream(new FileInputStream(file));
   }
}
