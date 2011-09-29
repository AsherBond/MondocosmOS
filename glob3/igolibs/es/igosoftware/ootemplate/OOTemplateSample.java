/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.ootemplate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;


public class OOTemplateSample {

   public static class Customer {
      private final String   _name;
      private final String[] _addresses;
      private final double   _debt;


      private Customer(final String name,
                       final String[] addresses,
                       final double debt) {
         _name = name;
         _addresses = addresses;
         _debt = debt;
      }


      private final static String[] IMAGES = new String[] {
                                                             "oosample/ciguena.png",
                                                             "oosample/dragon.png",
                                                             "oosample/human.png"
                                           };
      private final static Random   RANDOM = new Random();


      public BufferedImage getImage() {
         try {
            //return ImageIO.read(new File("/home/dgd/Escritorio/IGO-Repository/poda3d/textures/appleleaf.png"));
            return ImageIO.read(new File(Customer.IMAGES[Customer.RANDOM.nextInt(Customer.IMAGES.length)]));
         }
         catch (final IOException e) {
            return null;
         }
      }


      public String getName() {
         return _name;
      }


      public double getDebt() {
         return _debt;
      }


      public List<String> getAddresses() {
         return Collections.unmodifiableList(Arrays.asList(_addresses));
      }
   }


   private static Collection<Customer> createCustomers() {
      final Collection<Customer> customers = new ArrayList<Customer>();
      customers.add(new Customer("El Nobel", new String[] {
         "Acá a la vuelta"
      }, -1000));
      customers.add(new Customer("Top Manta", new String[] {}, 0));
      customers.add(new Customer("El burguer", new String[] {
                        "Dirección 1",
                        "Dirección 2"
      }, -2568769));
      //customers.add(new Customer(null, new String[] {}, 0));
      return customers;
   }


   public static void main(final String[] args) throws IOException, OOSyntaxException {
      System.out.println("OOTemplate 0.1");
      System.out.println("--------------\n");

      // awful hack to avoid warning
      //      final int test = 0 + 0;
      //
      //      if (test == 1) {
      //         testContentXml();
      //      }
      //      else {
      testTestOdt();
      //      }
   }


   //   private static void testContentXml() throws IOException, OOSyntaxException {
   //      final String templateFileName = "/home/dgd/Escritorio/content.xml";
   //      final OOTemplate template = new OOTemplate(templateFileName, true);
   //
   //      final StringBuffer buffer = new StringBuffer();
   //      final StringBufferWriter output = new StringBufferWriter(buffer);
   //      template.evaluate("customers", createCustomers(), output);
   //      output.close();
   //      System.out.println("Result:");
   //      System.out.println("-------");
   //      System.out.println(buffer);
   //   }


   private static void testTestOdt() throws IOException, OOSyntaxException {
      final String templateFileName = "oosample/test.odt";
      final OOTemplate template = new OOTemplate(templateFileName, true);
      template.setFormatNullAsBlank(true);
      //template.setFormatNullPointerExceptionAsBlank(true);

      template.evaluate("customers", createCustomers(), "oosample/result.odt");

      //      final String templateFileName = "/home/dgd/Escritorio/OOTEsting/plantillaSenderos.odt";
      //      final OOTemplate template = new OOTemplate(templateFileName, true);
      //      template.setFormatNullAsBlank(true);
      //      //template.setFormatNullPointerExceptionAsBlank(true);
      //
      //      template.evaluate("r", new R(), "/home/dgd/Escritorio/OOTEsting/result.odt");
   }


   public static class R {
      public List<String> getImagenes() {
         final List<String> result = new ArrayList<String>();
         result.add("/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/ciguena.png");
         result.add("/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/dragon.png");
         result.add("/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/human.png");

         return result;
         //         return new String[] { "/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/ciguena.png",
         //                  "/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/dragon.png",
         //                  "/home/dgd/Escritorio/IGO-Repository/igolibs/oosample/human.png" };
      }
   }


}
