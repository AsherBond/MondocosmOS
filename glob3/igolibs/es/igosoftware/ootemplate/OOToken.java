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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;

import es.igosoftware.util.GStringUtils;


public abstract class OOToken {
   public static final String FOR_IN_REGEXP = " *for (.*) in (.*)";


   private static void printIdentation(final Writer output,
                                       final int level) throws IOException {
      output.write(GStringUtils.spaces(level * 2));
   }


   public interface Visitor {
      void visit(final OOToken token);
   }


   private Compound _parent = null;


   @Override
   public abstract String toString();


   boolean isForEnd() {
      return false;
   }


   protected void acceptVisitor(final OOToken.Visitor visitor) {
      visitor.visit(this);
   }


   void printStructure(final Writer output) throws IOException {
      printStructure(output, 0);
   }


   protected abstract void printStructure(final Writer output,
                                          final int level) throws IOException;


   abstract OOToken processElement(final String name,
                                   final Attributes atts,
                                   final List<XMLAttribute> prefixMapping);


   abstract void processCharacters(final String string);


   private void setParent(final OOToken.Compound parent) {
      if (_parent != null) {
         throw new IllegalArgumentException(this + " already has a parent (" + _parent + ")");
      }
      _parent = parent;
   }


   protected int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   protected OOToken.Document getRoot() {
      if (_parent == null) {
         // the only token without a parent must be a Document
         return (OOToken.Document) this;
      }
      return _parent.getRoot();
   }


   protected List<OOToken> getTokensInTheSameGeneration() {
      final List<OOToken> result = new ArrayList<OOToken>();
      final int ownDepth = getDepth();

      getRoot().acceptVisitor(new OOToken.Visitor() {
         @Override
         public void visit(final OOToken token) {
            if (token.getDepth() == ownDepth) {
               result.add(token);
            }
         }
      });

      return result;
   }


   //   protected List<OOToken> getSiblings() {
   //      if (_parent == null) {
   //         return Collections.emptyList();
   //      }
   //      return _parent.getChildren();
   //   }


   protected abstract OOTag createTag(final Set<OOToken> usedTokens) throws OOSyntaxException;


   abstract static class Compound
            extends
               OOToken {
      protected List<OOToken> _children = new LinkedList<OOToken>();


      @Override
      OOToken processElement(final String name,
                             final Attributes atts,
                             final List<XMLAttribute> prefixMapping) {

         final List<XMLAttribute> attributes = new ArrayList<XMLAttribute>(atts.getLength());

         if (name.equals("office:document-content")) {
            // insert the prefixesMapping as attributes in document-content element
            attributes.addAll(prefixMapping);
         }

         for (int i = 0; i < atts.getLength(); i++) {
            attributes.add(new XMLAttribute(atts.getQName(i), atts.getValue(i), true));
         }

         final XMLElement child = new XMLElement(name, attributes);
         addChild(child);
         return child;
      }


      private void addChild(final OOToken child) {
         child.setParent(this);
         _children.add(child);
      }


      @Override
      void processCharacters(final String string) {
         for (final Characters each : OOToken.Characters.getTokens(string)) {
            addChild(each);
         }
         //_children.addAll(OOToken.Characters.getTokens(string));
      }


      protected String childrenToString() {
         if (_children.isEmpty()) {
            return "";
         }

         return _children.toString();
      }


      protected void printChildrenStructure(final Writer output,
                                            final int level) throws IOException {
         for (final OOToken child : _children) {
            child.printStructure(output, level + 1);
         }
      }


      List<OOToken> getChildren() {
         return Collections.unmodifiableList(_children);
      }


      @Override
      protected void acceptVisitor(final OOToken.Visitor visitor) {
         super.acceptVisitor(visitor);

         for (final OOToken child : _children) {
            child.acceptVisitor(visitor);
         }
      }


      public boolean containsOnly(final Collection<OOToken> candidates) {
         for (final OOToken child : _children) {
            if (!candidates.contains(child)) {
               return false;
            }
         }
         return true;
      }
   }


   final static class Document
            extends
               Compound {


      @Override
      public String toString() {
         return "Document " + childrenToString();
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         printIdentation(output, level);
         output.write("<document>");
         output.write("\n");
         printChildrenStructure(output, level);
      }


      @Override
      protected OOTag.Document createTag(final Set<OOToken> usedTokens) throws OOSyntaxException {
         return OOTag.Document.createTag(this, usedTokens);
      }

   }


   final static class XMLAttribute {
      private final String  _name;
      private String        _value;
      private final boolean _escape;


      XMLAttribute(final String name,
                   final String value,
                   final boolean escape) {
         _name = name;
         _value = value;
         _escape = escape;
      }


      void printXML(final Writer out) throws IOException {
         out.write(" ");
         out.write(_name);
         out.write("=\"");
         if (_escape) {
            out.write(OOUtils.escape(_value));
         }
         else {
            out.write(_value);
         }
         out.write("\"");
      }


      @Override
      public String toString() {
         return _name + "=" + _value;
      }


      public String getName() {
         return _name;
      }


      public String getValue() {
         return _value;
      }
   }


   final static class XMLElement
            extends
               Compound {
      final private String             _name;
      final private List<XMLAttribute> _attributes;


      private XMLElement(final String name,
                         final List<XMLAttribute> attributes) {
         _name = name;
         _attributes = new ArrayList<XMLAttribute>(attributes);
      }


      @Override
      public String toString() {
         final String attributesString;
         if (_attributes.isEmpty()) {
            attributesString = "";
         }
         else {
            attributesString = _attributes.toString();
         }
         return "Element: " + _name + " " + attributesString + " " + childrenToString();
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         printIdentation(output, level);
         output.write('<' + _name + '>');
         output.write("\n");
         printChildrenStructure(output, level);
      }


      @Override
      protected OOTag createTag(final Set<OOToken> usedTokens) throws OOSyntaxException {
         return OOTag.XMLElement.createTag(this, usedTokens);
      }


      String getName() {
         return _name;
      }


      List<XMLAttribute> getAttributes() {
         return Collections.unmodifiableList(_attributes);
      }


      String getAttributeValue(final String name,
                               final String defaultValue) {
         for (final XMLAttribute attribute : _attributes) {
            if (attribute._name.equals(name)) {
               return attribute._value;
            }
         }
         return defaultValue;
      }


      void setAttribute(final String name,
                        final String value) {
         for (final XMLAttribute attribute : _attributes) {
            if (attribute._name.equals(name)) {
               attribute._value = value;
               return;
            }
         }
         _attributes.add(new XMLAttribute(name, value, false));
      }

   }


   static abstract class Characters
            extends
               OOToken {
      static final String OPEN_TAG  = "{{";
      static final String CLOSE_TAG = "}}";


      static List<OOToken.Characters> getTokens(final String string) {
         final List<OOToken.Characters> result = new ArrayList<OOToken.Characters>();

         String unprocessed = string;

         while (!unprocessed.isEmpty()) {
            final int indexOfOpen = unprocessed.indexOf(OOToken.Characters.OPEN_TAG);

            if (indexOfOpen == -1) {
               if (unprocessed.contains(OOToken.Characters.CLOSE_TAG)) {
                  result.add(new OOToken.Literal("UNBALANCED TAG DELIMITERS: \"" + unprocessed + "\""));
               }
               else {
                  if (!unprocessed.isEmpty()) {
                     result.add(new OOToken.Literal(unprocessed));
                  }
               }
               return result;
            }

            final String preOpen = unprocessed.substring(0, indexOfOpen);
            if (!preOpen.isEmpty()) {
               result.add(new OOToken.Literal(preOpen));
            }
            final String postOpen = unprocessed.substring(indexOfOpen + OOToken.Characters.OPEN_TAG.length());

            final int indexOfClose = postOpen.indexOf(OOToken.Characters.CLOSE_TAG);
            if (indexOfClose == -1) {
               result.add(new OOToken.Literal("UNBALANCED TAG DELIMITERS: \"" + unprocessed + "\""));
               return result;
            }

            final String tag = postOpen.substring(0, indexOfClose);

            result.add(new OOToken.Tag(tag));

            unprocessed = postOpen.substring(indexOfClose + OOToken.Characters.CLOSE_TAG.length());
         }

         return result;
      }

      protected final String _string;


      private Characters(final String string) {
         _string = string;
      }


      String getString() {
         return _string;
      }


      @Override
      void processCharacters(final String string) {
         throw new UnsupportedOperationException();
      }


      //      @Override
      //      protected void printXML(final Writer out,
      //                              final boolean pretty,
      //                              final int level) throws IOException {
      //         if (pretty) {
      //            out.write(StringUtils.spaces(level));
      //         }
      //
      //         out.write(OOUtils.escape(_string));
      //
      //         if (pretty) {
      //            out.write("\n");
      //         }
      //      }


      @Override
      OOToken processElement(final String name,
                             final Attributes atts,
                             final List<XMLAttribute> prefixMapping) {
         throw new UnsupportedOperationException();
      }


   }


   static final class Literal
            extends
               Characters {
      private Literal(final String string) {
         super(string);
      }


      @Override
      public String toString() {
         return "\"" + _string + "\"";
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         final String trimmedString = _string.trim();
         if (trimmedString.isEmpty()) {
            return;
         }
         if (trimmedString.equals("\n")) {
            return;
         }

         printIdentation(output, level);
         //output.write('"' + escape(_string) + '"');
         output.write('"' + OOUtils.escape(trimmedString) + '"');
         output.write("\n");
      }


      @Override
      protected OOTag.Literal createTag(final Set<OOToken> usedTokens) {
         return OOTag.Literal.createTag(this, usedTokens);
      }


   }


   static final class Tag
            extends
               Characters {
      static enum Type {
         FOR,
         END,
         EVALUATION;
      }

      final private Tag.Type _type;


      private Tag(final String string) {
         //super("TAG:" + OOToken.Characters.OPEN_TAG + string + OOToken.Characters.CLOSE_TAG);
         //super(OOToken.Characters.OPEN_TAG + string + OOToken.Characters.CLOSE_TAG);
         super(string.trim());

         final String lowerCaseContents = string.toLowerCase();
         if (lowerCaseContents.matches(OOToken.FOR_IN_REGEXP)) {
            _type = Tag.Type.FOR;
         }
         else if (lowerCaseContents.matches(" *end *")) {
            _type = Tag.Type.END;
         }
         else {
            _type = Tag.Type.EVALUATION;
         }
      }


      @Override
      boolean isForEnd() {
         return _type == Type.END;
      }


      @Override
      public String toString() {
         return _type + ":" + OOToken.Characters.OPEN_TAG + _string + OOToken.Characters.CLOSE_TAG;
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         printIdentation(output, level);
         output.write("{{ " + _type + ": ");
         output.write(_string);
         output.write(" }}\n");
      }


      @Override
      protected OOTag createTag(final Set<OOToken> usedTokens) throws OOSyntaxException {
         if (_type == Type.EVALUATION) {
            return OOTag.Evaluation.createTag(this, usedTokens);
         }
         if (_type == Type.FOR) {
            return OOTag.For.createTag(this, usedTokens);
         }
         if (_type == Type.END) {
            return OOTag.Literal.createTag("{{ END }} in incorrect place");
         }
         throw new IllegalArgumentException("Illegal Type: " + this);
      }


   }


   public OOToken.Compound getParent() {
      return _parent;
   }


   //   public static void main(final String[] args) {
   //      System.out.println("Token 0.2");
   //      System.out.println("---------\n");
   //
   //      System.out.println(Token.Characters.getTokens("L1"));
   //      System.out.println(Token.Characters.getTokens("L1{{expression1}}L2"));
   //      System.out.println(Token.Characters.getTokens("L1{{expression1}}L2{{expression2}}L3"));
   //      System.out.println(Token.Characters.getTokens("{{expression1}}L1{{expression2}}L2"));
   //      System.out.println(Token.Characters.getTokens("{{expression1}}L1{{expression2}}"));
   //      System.out.println(Token.Characters.getTokens("{{expression1}}{{expression2}}"));
   //
   //      System.out.println(Token.Characters.getTokens("{{ for each in collection }}Each:{{ each }}{{ end }}"));
   //   }

}
