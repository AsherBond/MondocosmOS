

package es.igosoftware.euclid.scripting;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.UtilEvalError;
import es.igosoftware.euclid.experimental.algorithms.IAlgorithm;


public class GScriptManager {

   private static final String      PYTHON_VAR_NAME = "ALGORITHM";
   private static PythonInterpreter jythonInterpreter;
   private static Interpreter       beanshellInterpreter;


   public static void initialize() throws GScriptInitializationException {
      jythonInterpreter = new PythonInterpreter();
      beanshellInterpreter = new Interpreter();
      try {
         beanshellInterpreter.getNameSpace().doSuperImport();
      }
      catch (final UtilEvalError e) {
         beanshellInterpreter = null;
         jythonInterpreter = null;
         throw new GScriptInitializationException("Cannot initialize beanshell interpreter");
      }
   }


   /**
    * Returns an instance of the IAlgorithm instance. The instance is passed in python in the InputStream parameter. The python
    * code must have a statement that sets a {@value #PYTHON_VAR_NAME} local variable to the python instance.
    * 
    * @param is
    * @return
    * @throws GIllegalScriptException
    *            If the instance does not implement the {@link IAlgorithm} instance or the {@value #PYTHON_VAR_NAME} is not set
    */
   public static IAlgorithm<?, ?, ?, ?> getPythonAlgorithm(final InputStream is) throws GIllegalScriptException {
      jythonInterpreter.set(PYTHON_VAR_NAME, null);
      jythonInterpreter.execfile(is);
      final PyObject pyObject = jythonInterpreter.get(PYTHON_VAR_NAME);
      if (pyObject == null) {
         throw new GIllegalScriptException("There is no \"" + PYTHON_VAR_NAME + " = "
                                           + "[algorithm instance]\" statement in the script");
      }
      final Object javaObject = pyObject.__tojava__(IAlgorithm.class);
      return cast(javaObject, "python");
   }


   private static IAlgorithm<?, ?, ?, ?> cast(final Object javaObject,
                                              final String lang) throws GIllegalScriptException {
      try {
         return (IAlgorithm<?, ?, ?, ?>) javaObject;
      }
      catch (final ClassCastException e) {
         throw new GIllegalScriptException("The returned " + lang + " object does not implement the IAlgorithm interface");
      }
   }


   /**
    * Returns an instance of the IAlgorithm instance. The instance is passed in a beanshell script in the InputStream parameter.
    * The code must have a statement that returns the beanshell instance that implements IAlgorithm
    * 
    * @param is
    * @return
    * @throws GIllegalScriptException
    *            If the returned object does not implement the {@link IAlgorithm} instance or no object is returned
    */
   public static IAlgorithm<?, ?, ?, ?> getBeanshellAlgorithm(final InputStream is) throws GIllegalScriptException {
      Object instance;
      try {
         instance = beanshellInterpreter.eval(new InputStreamReader(is));
      }
      catch (final EvalError e) {
         throw new GIllegalScriptException("The beanshell code contains errors", e);
      }
      if (instance == null) {
         throw new GIllegalScriptException("No IAlgorithm instance returned in the script");
      }
      return cast(instance, "beanshell");
   }


   public static boolean isInitialized() {
      return (beanshellInterpreter != null) && (jythonInterpreter != null);
   }
}
