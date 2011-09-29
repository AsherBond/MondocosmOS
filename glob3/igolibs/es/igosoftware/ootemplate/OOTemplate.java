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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import bsh.EvalError;
import bsh.Interpreter;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.StringBufferWriter;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.ootemplate.OOToken.XMLAttribute;


public class OOTemplate {
   private static final ILogger _logger = GLogger.instance();

   //   private static final String  OPEN_TAG  = "{{";
   //   private static final String  CLOSE_TAG = "}}";

   private final String         _templateFileName;
   //private final OOTagOLD       _oldRootTag;
   private final OOTag.Document _documentTag;
   private final boolean        _debug;


   public OOTemplate(final String templateFileName) throws IOException, OOSyntaxException {
      this(templateFileName, false);
   }


   public OOTemplate(final String templateFileName,
                     final boolean debug) throws IOException, OOSyntaxException {
      _debug = debug;
      _templateFileName = templateFileName;

      if (_debug) {
         OOTemplate._logger.logInfo("Reading " + new File(_templateFileName).getAbsolutePath() + "...");
      }

      //      _oldRootTag = readTemplateFileOLD();
      //      _oldRootTag.setTemplate(this);

      _documentTag = parseTemplateFile();
      _documentTag.setTemplate(this);


      if (_debug) {
         OOTemplate._logger.logInfo("Read " + new File(_templateFileName).getAbsolutePath());
      }
   }


   private OOTag.Document parseTemplateFile() throws IOException, OOSyntaxException {
      if (_templateFileName.toLowerCase().endsWith(".xml")) {
         FileReader reader = null;
         try {
            reader = new FileReader(_templateFileName);
            return parseSAX(reader);
         }
         finally {
            GIOUtils.gentlyClose(reader);
         }
      }

      final ZipFile templateZip = new ZipFile(_templateFileName);

      final ZipEntry contentEntry = templateZip.getEntry("content.xml");

      final OOTag.Document result = parseSAX(new InputStreamReader(templateZip.getInputStream(contentEntry)));


      templateZip.close();

      return result;
   }


   private OOTag.Document parseSAX(final Reader reader) throws IOException, OOSyntaxException {
      final OOToken.Document documentToken = new OOToken.Document();

      try {
         final XMLReader xmlReader = XMLReaderFactory.createXMLReader();

         final List<OOToken.XMLAttribute> prefixMapping = new ArrayList<XMLAttribute>();

         final LinkedList<OOToken> stack = new LinkedList<OOToken>();
         stack.push(documentToken);

         xmlReader.setContentHandler(new ContentHandler() {
            @Override
            public void startElement(final String uri,
                                     final String localName,
                                     final String name,
                                     final Attributes atts) {
               final OOToken newElement = stack.getLast().processElement(name, atts, prefixMapping);
               stack.addLast(newElement);
            }


            @Override
            public void endElement(final String uri,
                                   final String localName,
                                   final String name) {
               stack.removeLast();
            }


            @Override
            public void characters(final char[] ch,
                                   final int start,
                                   final int length) {
               final String string = new String(ch, start, length);
               stack.getLast().processCharacters(string);
            }


            @Override
            public void endDocument() {
            }


            @Override
            public void endPrefixMapping(final String prefix) {
            }


            @Override
            public void ignorableWhitespace(final char[] ch,
                                            final int start,
                                            final int length) {
            }


            @Override
            public void processingInstruction(final String target,
                                              final String data) {
            }


            @Override
            public void setDocumentLocator(final Locator locator) {
            }


            @Override
            public void skippedEntity(final String name) {
            }


            @Override
            public void startDocument() {
            }


            @Override
            public void startPrefixMapping(final String prefix,
                                           final String uri) {
               prefixMapping.add(new OOToken.XMLAttribute("xmlns:" + prefix, uri, false));
            }
         });

         xmlReader.parse(new InputSource(reader));


      }
      catch (final SAXException e) {
         e.printStackTrace();
      }


      if (_debug) {
         final StringBuffer tokensBuffer = new StringBuffer();
         final Writer tokensOutput = new StringBufferWriter(tokensBuffer);
         documentToken.printStructure(tokensOutput);
         OOTemplate._logger.logInfo("\nTokens Structure:");
         OOTemplate._logger.logInfo("-----------------");
         OOTemplate._logger.logInfo(tokensBuffer.toString());
      }

      final OOTag.Document documentTag = OOTag.createDocumentTag(documentToken);

      if (_debug) {
         final StringBuffer tagsBuffer = new StringBuffer();
         final Writer tagsOutput = new StringBufferWriter(tagsBuffer);
         documentTag.printStructure(tagsOutput);
         OOTemplate._logger.logInfo("\nTags Structure:");
         OOTemplate._logger.logInfo("---------------");
         OOTemplate._logger.logInfo(tagsBuffer.toString());
      }

      return documentTag;
   }


   //   private OOTagOLD readReader(final Reader reader) throws IOException, OOSyntaxException {
   //      final BufferedReader content = new BufferedReader(reader);
   //
   //      final LinkedList<OOTokenOLD> ds = new LinkedList<OOTokenOLD>();
   //
   //      String line;
   //      while ((line = content.readLine()) != null) {
   //         processLine(line, ds);
   //      }
   //      if (_currentTag != null) {
   //         ds.add(new OOTokenOLD.LiteralOLD(OOTemplate.OPEN_TAG + _currentTag.toString(), true));
   //         _currentTag = null;
   //      }
   //
   //      content.close();
   //
   //      return OOTagOLD.createMainTag(ds);
   //   }


   //private StringBuffer _currentTag;
   private boolean _formatNullAsBlank                 = false;
   private boolean _formatNullPointerExceptionAsBlank = false;


   //   private void processLine(final String line,
   //                            final LinkedList<OOTokenOLD> ds) {
   //
   //      if (line.trim().startsWith("#")) {
   //         return;
   //      }
   //
   //      if (_currentTag == null) {
   //
   //         final int openPos = line.indexOf(OOTemplate.OPEN_TAG);
   //         if (openPos == -1) {
   //            ds.add(new OOTokenOLD.LiteralOLD(line, true));
   //         }
   //         else {
   //            final String literalPart = line.substring(0, openPos);
   //            if (!literalPart.isEmpty()) {
   //               ds.add(new OOTokenOLD.LiteralOLD(literalPart, false));
   //            }
   //
   //            if (_currentTag == null) {
   //               _currentTag = new StringBuffer();
   //            }
   //
   //            processLine(line.substring(openPos + OOTemplate.OPEN_TAG.length()), ds);
   //         }
   //
   //      }
   //      else {
   //
   //         final int closePos = line.indexOf(OOTemplate.CLOSE_TAG);
   //         if (closePos == -1) {
   //            _currentTag.append(line);
   //         }
   //         else {
   //            _currentTag.append(line.substring(0, closePos));
   //            ds.add(new OOTokenOLD.TagOLD(_currentTag.toString()));
   //            _currentTag = null;
   //
   //            processLine(line.substring(closePos + OOTemplate.CLOSE_TAG.length()), ds);
   //         }
   //
   //      }
   //
   //   }


   //   void evaluate(final String name,
   //                 final Object value,
   //                 final Writer writer) throws IOException {
   //      final HashMap<String, Object> context = new HashMap<String, Object>();
   //      context.put(name, value);
   //      evaluate(context, writer, null);
   //   }


   private Interpreter createBSHInterpreter(final Map<String, Object> context) {
      final Interpreter bsh = new Interpreter();

      try {
         for (final Entry<String, Object> contextEntry : context.entrySet()) {
            bsh.set(contextEntry.getKey(), contextEntry.getValue());
         }
      }
      catch (final EvalError e) {
         e.printStackTrace();
      }

      return bsh;
   }


   private void evaluate(final Map<String, Object> context,
                         final Writer writer,
                         final ZipOutputStream outputZip,
                         final Set<String> imagesToRemove,
                         final List<String> imagesToAdd) throws IOException {
      //_oldRootTag.evaluate(createBSHInterpreter(context), writer);

      _documentTag.evaluate(createBSHInterpreter(context), writer, outputZip, imagesToRemove, imagesToAdd);
   }


   public void evaluate(final String name,
                        final Object value,
                        final String outputFileName) throws IOException {
      final HashMap<String, Object> context = new HashMap<String, Object>();
      context.put(name, value);
      evaluate(context, outputFileName);
   }


   public void evaluate(final Map<String, Object> context,
                        final String outputFileName) throws IOException {
      if (outputFileName.toLowerCase().endsWith(".xml")) {
         final StringBuffer outputBuffer = new StringBuffer();

         final Set<String> imagesToRemove = new HashSet<String>();
         final List<String> imagesToAdd = new ArrayList<String>();
         evaluate(context, new StringBufferWriter(outputBuffer), null, imagesToRemove, imagesToAdd);
         final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
         writer.append(outputBuffer);
         writer.close();
      }
      else {
         if (_debug) {
            OOTemplate._logger.logInfo("Saving " + new File(outputFileName).getAbsolutePath() + "...");
         }

         OutputStream output = null;
         try {
            output = new FileOutputStream(outputFileName);

            evaluate(context, output);

            output.flush();
         }
         finally {
            GIOUtils.gentlyClose(output);
         }

         if (_debug) {
            OOTemplate._logger.logInfo("Saved " + new File(outputFileName).getAbsolutePath());
         }
      }
   }


   public void evaluate(final String name,
                        final Object value,
                        final OutputStream output) throws IOException {
      final HashMap<String, Object> context = new HashMap<String, Object>();
      context.put(name, value);
      evaluate(context, output);
   }


   public void evaluate(final Map<String, Object> context,
                        final OutputStream output) throws IOException {

      final ZipOutputStream outputZip = new ZipOutputStream(new BufferedOutputStream(output));
      final Set<String> imagesToRemove = new HashSet<String>();
      final List<String> imagesToAdd = new ArrayList<String>();
      final StringBuffer outputBuffer = new StringBuffer();
      evaluate(context, new StringBufferWriter(outputBuffer), outputZip, imagesToRemove, imagesToAdd);

      final ZipFile templateZip = new ZipFile(_templateFileName);
      final Enumeration<? extends ZipEntry> entries = templateZip.entries();
      while (entries.hasMoreElements()) {
         final ZipEntry entry = entries.nextElement();
         if (entry.getName().equals("content.xml")) {
            final byte[] outputBytes = outputBuffer.toString().getBytes();

            final ZipEntry contentEntry = new ZipEntry("content.xml");
            contentEntry.setSize(outputBytes.length);
            outputZip.putNextEntry(contentEntry);

            outputZip.write(outputBytes);
         }
         else if (entry.getName().equals("META-INF/manifest.xml")) {
            final byte[] outputBytes = evaluateManifiest(imagesToRemove, imagesToAdd);
            final ZipEntry contentEntry = new ZipEntry("META-INF/manifest.xml");
            contentEntry.setSize(outputBytes.length);
            outputZip.putNextEntry(contentEntry);
            outputZip.write(outputBytes);
         }
         else {
            if (imagesToRemove.contains(entry.getName())) {
               continue;
            }
            final ZipEntry newEntry = new ZipEntry(entry);
            outputZip.putNextEntry(newEntry);
            GIOUtils.copy(templateZip.getInputStream(entry), outputZip);
         }
         outputZip.closeEntry();
      }

      outputZip.flush();
      outputZip.close();
   }


   private byte[] evaluateManifiest(final Set<String> imagesToRemove,
                                    final List<String> imagesToAdd) {
      try {
         final ZipFile templateZip = new ZipFile(_templateFileName);
         final ZipEntry entry = templateZip.getEntry("META-INF/manifest.xml");
         final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
         final org.w3c.dom.Document doc = documentBuilder.parse(templateZip.getInputStream(entry));
         final Element root = doc.getDocumentElement();
         final NodeList childNodes = root.getChildNodes();
         for (int i = 0; i < childNodes.getLength(); i++) {
            final Node children = childNodes.item(i);
            if (children instanceof Element) {
               final String mediaType = children.getAttributes().getNamedItem("manifest:media-type").getTextContent();
               if (mediaType.equals("image/png")) {
                  final String fullPath = children.getAttributes().getNamedItem("manifest:full-path").getTextContent();
                  boolean removed = false;
                  for (final String imageToRemove : imagesToRemove) {
                     //TODO:Vidal: He introducido este código porque no tiene sentido que siga buscando un Node tras haberlo borrado.
                     if (!removed && fullPath.equals(imageToRemove)) {
                        try {
                           if (children.equals(root.removeChild(children))) {
                              removed = true;
                           }
                        }
                        catch (final DOMException DOMEx) {
                           DOMEx.printStackTrace();
                        }
                     }
                  }

               }
            }
         }
         for (final String imageToAdd : imagesToAdd) {
            final Element node = doc.createElement("manifest:file-entry");
            node.setAttribute("manifest:media-type", "img/png");
            node.setAttribute("manifest:full-path", imageToAdd);
            root.appendChild(node);
         }
         final Source source = new DOMSource(root);
         final ByteArrayOutputStream out = new ByteArrayOutputStream();
         final Result result = new StreamResult(out);
         final TransformerFactory factory = TransformerFactory.newInstance();
         final Transformer transformer = factory.newTransformer();
         transformer.transform(source, result);
         return out.toByteArray();
      }
      catch (final IOException e) {
         e.printStackTrace();
      }
      catch (final ParserConfigurationException e) {
         e.printStackTrace();
      }
      catch (final SAXException e) {
         e.printStackTrace();
      }
      catch (final TransformerConfigurationException e) {
         e.printStackTrace();
      }
      catch (final TransformerException e) {
         e.printStackTrace();
      }
      return null;
   }


   public boolean isFormatNullAsBlank() {
      return _formatNullAsBlank;
   }


   public void setFormatNullAsBlank(final boolean formatNullAsBlank) {
      _formatNullAsBlank = formatNullAsBlank;
   }


   public boolean isFormatNullPointerExceptionAsBlank() {
      return _formatNullPointerExceptionAsBlank;
   }


   public void setFormatNullPointerExceptionAsBlank(final boolean formatNullPointerExceptionAsBlank) {
      _formatNullPointerExceptionAsBlank = formatNullPointerExceptionAsBlank;
   }


}
