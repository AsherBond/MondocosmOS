

package es.igosoftware.euclid.scripting.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class GAllTests
         extends
            TestCase {

   public static Test suite() {
      final TestSuite suite = new TestSuite("Script tests");
      suite.addTestSuite(GScriptTest.class);
      return suite;
   }
}
