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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import es.igosoftware.ootemplate.OOToken.XMLAttribute;
import es.igosoftware.util.GStringUtils;


public abstract class OOTag {


   interface ICompound {
      public OOTag.Document getRoot();


      public boolean isUniqueChild(final OOTag child);


      public boolean isUniqueNotForChild(final OOTag child);


      public ICompound getParent();


      public void replaceWith(final OOTag newChild);


      public void replaceChild(final OOTag oldChild,
                               final OOTag newChild);


      public void replaceChildren(final List<OOTag> children);


      public void setDummy(final boolean dummy);


      public boolean isDummy();


      public boolean isP();


      public List<OOTag> getChildren();


      public boolean isFor();
   }


   static final String FOR_IN_REGEXP = " *for (.*) in (.*)";


   static OOTag.Document createDocumentTag(final OOToken.Document documentToken) throws OOSyntaxException {
      final Set<OOToken> usedTokens = new HashSet<OOToken>();
      final Document documentTag = documentToken.createTag(usedTokens);

      if (documentTag != null) {
         boolean changed;
         do {
            //System.out.println("\nFIXING LAYOUT");
            changed = documentTag.fixLayout();
         }
         while (changed);
      }

      return documentTag;
   }


   public boolean isP() {
      return false;
   }


   public boolean isTable() {
      return false;
   }


   public boolean isFor() {
      return false;
   }


   public boolean isDummy() {
      return false;
   }


   private static void printIdentation(final Writer output,
                                       final int level) throws IOException {
      output.write(GStringUtils.spaces(level * 2));
   }


   private static void appendErrorResult(final Writer output,
                                         final String tagContents,
                                         final String msg) throws IOException {
      output.append("{{ " + tagContents + " }} ERROR: " + msg);
   }


   protected OOTag.ICompound _parent;


   private OOTag() {

   }


   private OOTag(final OOToken token,
                 final Set<OOToken> usedTokens) {
      if ((token != null) && (usedTokens != null)) {
         usedTokens.add(token);
      }
   }


   protected abstract boolean fixLayout();


   protected abstract void evaluate(final Interpreter bsh,
                                    final Writer output,
                                    final ZipOutputStream outputZip,
                                    final Set<String> imagesToRemove,
                                    final List<String> imagesToAdd) throws IOException;


   OOTemplate getTemplate() {
      return getRoot()._template;
   }


   public OOTag.Document getRoot() {
      if (_parent == null) {
         // the only tag without a parent must be a Document
         return (OOTag.Document) this;
      }
      return _parent.getRoot();
   }


   public ICompound getParent() {
      return _parent;
   }


   void printParent(final Writer output) throws IOException {
      if (_parent == null) {
         return;
      }
      output.write("   (parent: ");
      output.write(_parent.toString());
      output.write(")");
   }


   @Override
   public abstract String toString();


   void replaceParent(final OOTag.ICompound parent) {
      _parent = parent;
   }


   void setParent(final OOTag.ICompound parent) {
      if (_parent != null) {
         throw new IllegalArgumentException(this + " already has a parent (" + _parent + ")");
      }
      _parent = parent;
   }


   public void replaceWith(final OOTag newChild) {
      if (_parent == null) {
         throw new IllegalArgumentException("Can't replace the root tag");
      }
      _parent.replaceChild(this, newChild);
   }


   void printStructure(final Writer output) throws IOException {
      printStructure(output, 0);
   }


   protected abstract void printStructure(final Writer output,
                                          final int level) throws IOException;


   abstract static class Compound
            extends
               OOTag
            implements
               OOTag.ICompound {
      protected final List<OOTag> _children = new ArrayList<OOTag>();
      private boolean             _dummy    = false;


      private Compound(final OOToken.Compound token,
                       final Set<OOToken> usedTokens) throws OOSyntaxException {
         super(token, usedTokens);

         final List<OOToken> tockenChildren = token.getChildren();
         for (final OOToken childToken : tockenChildren) {
            if (!usedTokens.contains(childToken)) {
               final OOTag childTag = childToken.createTag(usedTokens);
               if (childTag != null) {
                  addChild(childTag);
               }
            }
         }

         //         if (!tockenChildren.isEmpty() && _children.isEmpty()) {
         //            int TODO;
         //            // 
         //            throw new MustNotCreateException();
         //         }
      }


      private Compound() {
         super();
      }


      @Override
      public List<OOTag> getChildren() {
         return Collections.unmodifiableList(_children);
      }


      protected void addChild(final OOTag childTag) {
         childTag.setParent(this);
         _children.add(childTag);
      }


      private void printChildrenStructure(final Writer output,
                                          final int level) throws IOException {
         for (final OOTag child : _children) {
            child.printStructure(output, level + 1);
         }
      }


      @Override
      protected boolean fixLayout() {
         boolean changed = false;
         boolean anyChildChanged;
         do {
            anyChildChanged = false;
            for (final OOTag child : _children) {
               if (child.fixLayout()) {
                  changed = true;
                  anyChildChanged = true;
                  break;
               }
            }
         }
         while (anyChildChanged);

         return changed;
      }


      protected List<OOTag> getNotDummyChildren() {
         final List<OOTag> result = new ArrayList<OOTag>(_children.size());

         for (final OOTag child : _children) {
            if (!child.isDummy()) {
               result.add(child);
            }
         }

         return Collections.unmodifiableList(result);
      }


      protected List<OOTag> getNotDummyNotForChildren() {
         final List<OOTag> result = new ArrayList<OOTag>(_children.size());

         for (final OOTag child : _children) {
            if (!child.isDummy() && !child.isFor()) {
               result.add(child);
            }
         }

         return Collections.unmodifiableList(result);
      }


      protected List<OOTag> getNotDummyNotForNotPChildren() {
         final List<OOTag> result = new ArrayList<OOTag>(_children.size());

         for (final OOTag child : _children) {
            if (!child.isDummy() && !child.isFor() && !child.isP() && !child.isTable()) {
               result.add(child);
            }
         }

         return Collections.unmodifiableList(result);
      }


      @Override
      public boolean isUniqueChild(final OOTag child) {
         final List<OOTag> notDummyChildren = getNotDummyChildren();
         if (notDummyChildren.size() != 1) {
            return false;
         }
         return (notDummyChildren.get(0) == child);
      }


      @Override
      public boolean isUniqueNotForChild(final OOTag child) {
         final List<OOTag> notDummyNotForChildren = getNotDummyNotForChildren();
         if (notDummyNotForChildren.size() != 1) {
            return false;
         }
         return (notDummyNotForChildren.get(0) == child);
      }


      @Override
      protected final void printStructure(final Writer output,
                                          final int level) throws IOException {
         printIdentation(output, level);
         output.write("<");
         if (_dummy) {
            output.write("dummy-");
         }
         printStructureName(output);
         output.write(">");
         printParent(output);
         output.write("\n");
         printChildrenStructure(output, level);
      }


      protected abstract void printStructureName(final Writer output) throws IOException;


      @Override
      protected final void evaluate(final Interpreter bsh,
                                    final Writer output,
                                    final ZipOutputStream outputZip,
                                    final Set<String> imagesToRemove,
                                    final List<String> imagesToAdd) throws IOException {
         if (!_dummy) {
            printEvaluationOpen(output);
         }
         for (final OOTag child : _children) {
            child.evaluate(bsh, output, outputZip, imagesToRemove, imagesToAdd);
         }
         if (!_dummy) {
            printEvaluationClose(output);
         }
      }


      protected abstract void printEvaluationOpen(final Writer output) throws IOException;


      protected abstract void printEvaluationClose(final Writer output) throws IOException;


      @Override
      public void replaceChild(final OOTag oldChild,
                               final OOTag newChild) {
         final int indexOfOldChild = _children.indexOf(oldChild);
         if (indexOfOldChild == -1) {
            throw new IllegalArgumentException(oldChild + " is not a child of " + this);
         }
         _children.set(indexOfOldChild, newChild);
         newChild.replaceParent(this);
         oldChild.replaceParent(null);
      }


      @Override
      public void replaceChildren(final List<OOTag> newChildren) {
         _children.clear();
         for (final OOTag newChild : newChildren) {
            newChild.replaceParent(null);
            addChild(newChild);
         }
      }


      @Override
      public void setDummy(final boolean dummy) {
         _dummy = dummy;
      }


      @Override
      public boolean isDummy() {
         return _dummy;
      }
   }


   static final class Document
            extends
               OOTag.Compound {
      private OOTemplate _template;


      private Document(final OOToken.Document document,
                       final Set<OOToken> usedTokens) throws OOSyntaxException {
         super(document, usedTokens);
      }


      @Override
      protected void printStructureName(final Writer output) throws IOException {
         output.write("document");
      }


      void setTemplate(final OOTemplate template) {
         _template = template;
      }


      @Override
      protected void printEvaluationOpen(final Writer output) throws IOException {
         output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
      }


      @Override
      protected void printEvaluationClose(final Writer output) {
         // do nothing
      }


      @Override
      public String toString() {
         return "document";
      }


      public static OOTag.Document createTag(final OOToken.Document document,
                                             final Set<OOToken> usedTokens) throws OOSyntaxException {
         return new OOTag.Document(document, usedTokens);
      }


   }


   static final class Literal
            extends
               OOTag {
      private final String _contents;


      private Literal(final OOToken.Literal token,
                      final Set<OOToken> usedTokens) {
         super(token, usedTokens);
         _contents = token.getString();
      }


      private Literal(final String string) {
         super();
         _contents = string;
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         final String trimmedString = _contents.trim();
         if (trimmedString.isEmpty()) {
            return;
         }
         if (trimmedString.equals("\n")) {
            return;
         }

         printIdentation(output, level);
         //output.write('"' + escape(_contents) + '"');
         output.write('"' + OOUtils.escape(trimmedString) + '"');
         printParent(output);
         output.write("\n");
      }


      @Override
      protected void evaluate(final Interpreter bsh,
                              final Writer output,
                              final ZipOutputStream outputZip,
                              final Set<String> imagesToRemove,
                              final List<String> imagesToAdd) throws IOException {
         output.append(OOUtils.escape(_contents));
      }


      @Override
      protected boolean fixLayout() {
         return false;
      }


      @Override
      public String toString() {
         return '"' + _contents + '"';
      }


      public static OOTag.Literal createTag(final OOToken.Literal literal,
                                            final Set<OOToken> usedTokens) {
         return new OOTag.Literal(literal, usedTokens);
      }


      public static OOTag.Literal createTag(final String string) {
         return new OOTag.Literal(string);
      }
   }


   static final class XMLElement
            extends
               OOTag.Compound {

      private final String                     _name;
      private final List<OOToken.XMLAttribute> _attributes;


      private XMLElement(final OOToken.XMLElement token,
                         final Set<OOToken> usedTokens) throws OOSyntaxException {
         super(token, usedTokens);

         _name = token.getName();
         _attributes = token.getAttributes();
      }


      @Override
      protected boolean fixLayout() {
         if (super.fixLayout()) {
            return true;
         }


         if (_parent != null) {
            if (_parent.isFor() && isP() && getNotDummyNotForNotPChildren().isEmpty()) {
               setDummy(true);
               return true;
            }

            if (isP() && _parent.isP()) {
               //               System.out.println(this + " --> isP()=" + isP());
               //               System.out.println("   _parent: " + _parent + " --> isP()=" + _parent.isP());
               //               System.out.println("     _children: " + _parent.getChildren());
               if (_parent.isUniqueNotForChild(this)) {
                  _parent.setDummy(true);
                  return true;
               }
            }
         }

         return false;
      }


      @Override
      public boolean isP() {
         if (isDummy()) {
            return false;
         }
         if (_name.equals("text:p")) {
            return true;
         }
         if (_name.equals("p")) {
            return true;
         }
         if (_name.charAt(0) == 'p') {
            final String numberString = _name.substring(1);
            if (isNumber(numberString)) {
               return true;
            }
         }
         return false;
      }


      @Override
      public boolean isTable() {
         return _name.contains("table");
      }


      private static boolean isNumber(final String numberString) {
         //for (final String number : XMLElement.NUMBERS) {
         for (int i = 0; i <= 10; i++) {
            if (Integer.toString(i).equals(numberString)) {
               return true;
            }
         }
         return false;
      }


      @Override
      protected void printStructureName(final Writer output) throws IOException {
         output.write(_name);
      }


      @Override
      protected void printEvaluationOpen(final Writer output) throws IOException {
         output.write("<");
         output.write(_name);

         for (final OOToken.XMLAttribute attribute : _attributes) {
            attribute.printXML(output);
         }

         if (_children.isEmpty()) {
            output.write("/>");
         }
         else {
            output.write(">");
         }
      }


      @Override
      protected void printEvaluationClose(final Writer output) throws IOException {
         if (_children.isEmpty()) {
            return;
         }

         output.write("</");
         output.write(_name);
         output.write(">");
      }


      @Override
      public String toString() {
         return _name;
      }


      public static OOTag createTag(final OOToken.XMLElement xmlElement,
                                    final Set<OOToken> usedTokens) throws OOSyntaxException {

         // look for a draw:frame with only one draw:image inside
         if (xmlElement.getName().equals("draw:frame")) {
            final List<OOToken> children = xmlElement.getChildren();
            if (children.size() == 1) {
               final OOToken childToken = children.get(0);
               if (childToken instanceof OOToken.XMLElement) {
                  final OOToken.XMLElement childXMLToken = (OOToken.XMLElement) childToken;
                  if (childXMLToken.getName().equals("draw:image")) {
                     String expression = xmlElement.getAttributeValue("draw:name", null);
                     if (expression != null) {
                        expression = expression.trim();
                        if (expression.startsWith(OOToken.Characters.OPEN_TAG)
                            && expression.endsWith(OOToken.Characters.CLOSE_TAG)) {
                           expression = expression.substring(OOToken.Characters.OPEN_TAG.length(),
                                    expression.length() - OOToken.Characters.CLOSE_TAG.length());


                           //                           System.out.println("FOUND FRAME/IMAGE PAIR");
                           //                           System.out.println("    " + xmlElement);
                           //                           System.out.println("    " + childXMLToken);
                           //                           System.out.println("    expression=" + expression);

                           return new OOTag.ImageEvaluation(xmlElement, childXMLToken, expression);
                        }
                     }
                  }
               }
            }
         }

         return new OOTag.XMLElement(xmlElement, usedTokens);
      }
   }


   abstract static class Tag
            extends
               OOTag {
      protected final String _contents;


      private Tag(final OOToken.Tag token,
                  final Set<OOToken> usedTokens) {
         super(token, usedTokens);

         _contents = token.getString();
      }
   }


   static final class For
            extends
               OOTag.Tag
            implements
               ICompound {
      private final String      _variableName;
      private final String      _expression;
      private final List<OOTag> _children = new ArrayList<OOTag>();
      private final String      _errorMessage;


      private For(final OOToken.Tag forToken,
                  final Set<OOToken> usedTokens) throws OOSyntaxException {
         super(forToken, usedTokens);

         final Pattern pattern = Pattern.compile(OOTag.FOR_IN_REGEXP, Pattern.CASE_INSENSITIVE);
         final Matcher matcher = pattern.matcher(forToken.getString());
         matcher.matches();
         _variableName = matcher.group(1).trim();
         _expression = matcher.group(2);


         final List<OOToken> siblingsTokens = forToken.getTokensInTheSameGeneration();
         final LinkedList<OOToken> newChildren = new LinkedList<OOToken>();
         final int indexOfFor = siblingsTokens.indexOf(forToken);
         boolean endFound = false;
         for (int i = indexOfFor + 1; i < siblingsTokens.size(); i++) {
            final OOToken siblingToken = siblingsTokens.get(i);
            if (usedTokens.contains(siblingToken)) {
               continue;
            }
            if (siblingToken.isForEnd()) {
               usedTokens.add(siblingToken);
               endFound = true;
               break;
            }

            newChildren.add(siblingToken);
         }


         final List<OOToken.Compound> newChildrenParents = new ArrayList<OOToken.Compound>();
         for (final OOToken newChild : newChildren) {
            final OOToken.Compound newChildParent = newChild.getParent();
            if (!newChildrenParents.contains(newChildParent)) {
               newChildrenParents.add(newChildParent);
            }
         }
         //System.out.println("newChildrenParents=" + newChildrenParents);
         for (final OOToken.Compound parent : newChildrenParents) {
            if (parent.containsOnly(newChildren)) {
               final List<OOToken> parentChildren = parent.getChildren();

               //               int minIndex = Integer.MAX_VALUE;
               //               for (final OOToken parentChild : parentChildren) {
               //                  final int index = parentChildren.indexOf(parentChild);
               //                  if (index < minIndex) {
               //                     minIndex = index;
               //                  }
               //               }
               newChildren.removeAll(parentChildren);
               //System.out.println(minIndex);
               newChildren.addLast(parent);
            }
         }
         for (final OOToken newChild : newChildren) {
            usedTokens.add(newChild);

            final OOTag tagChild = newChild.createTag(usedTokens);
            if (tagChild != null) {
               addChild(tagChild);
            }
         }

         if (!endFound) {
            //_children.add(new OOTag.Literal(" {{ END }} not found ", usedTokens));
            _errorMessage = " {{ END }} not found ";
         }
         else {
            _errorMessage = null;
         }
      }


      @Override
      public boolean isFor() {
         return true;
      }


      private void addChild(final OOTag childTag) {
         childTag.setParent(this);
         _children.add(childTag);
      }


      @Override
      public List<OOTag> getChildren() {
         return Collections.unmodifiableList(_children);
      }


      private List<OOTag> getNotDummyChildren() {
         final List<OOTag> result = new ArrayList<OOTag>(_children.size());

         for (final OOTag child : _children) {
            if (!child.isDummy()) {
               result.add(child);
            }
         }

         return Collections.unmodifiableList(result);
      }


      @Override
      public boolean isUniqueChild(final OOTag child) {
         final List<OOTag> notDummyChildren = getNotDummyChildren();
         if (notDummyChildren.size() != 1) {
            return false;
         }
         return (notDummyChildren.get(0) == child);
      }


      private List<OOTag> getNotDummyNotForChildren() {
         final List<OOTag> result = new ArrayList<OOTag>(_children.size());

         for (final OOTag child : _children) {
            if (!child.isDummy() && !child.isFor()) {
               result.add(child);
            }
         }

         return Collections.unmodifiableList(result);
      }


      @Override
      public boolean isUniqueNotForChild(final OOTag child) {
         final List<OOTag> notDummyNotForChildren = getNotDummyNotForChildren();
         if (notDummyNotForChildren.size() != 1) {
            return false;
         }
         return (notDummyNotForChildren.get(0) == child);
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {

         printIdentation(output, level);
         output.write("{{ FOR: ");
         output.write(_contents);
         output.write(" }}");
         printParent(output);
         output.write("\n");


         for (final OOTag child : _children) {
            child.printStructure(output, level + 1);
         }
      }


      @Override
      protected void evaluate(final Interpreter bsh,
                              final Writer output,
                              final ZipOutputStream outputZip,
                              final Set<String> imagesToRemove,
                              final List<String> imagesToAdd) throws IOException {

         if (_errorMessage != null) {
            output.append(_errorMessage);
            return;
         }

         try {
            final Object iterableCandidate = bsh.eval(_expression);
            //            if (iterableCandidate == null) {
            //               return;
            //            }

            if ((iterableCandidate != null) && (iterableCandidate instanceof Iterable<?>)) {
               final Iterable<?> iterable = (Iterable<?>) iterableCandidate;
               for (final Object each : iterable) {
                  evaluateChildren(bsh, output, each, outputZip, imagesToRemove, imagesToAdd);
               }
            }
            else {
               final String errorMessage = "{{ " + _contents + " }} ERROR: Result of \"" + _expression + "\" ("
                                           + iterableCandidate + ") is not iterable";
               output.write(errorMessage);
               //evaluateChildren(bsh, output, errorMessage);
            }

         }
         catch (final EvalError e) {
            appendErrorResult(output, _contents, e.getMessage());
         }
      }


      private void evaluateChildren(final Interpreter bsh,
                                    final Writer output,
                                    final Object variableValue,
                                    final ZipOutputStream outputZip,
                                    final Set<String> imagesToRemove,
                                    final List<String> imagesToAdd) throws IOException, EvalError {
         for (final OOTag child : _children) {
            bsh.set(_variableName, variableValue);
            child.evaluate(bsh, output, outputZip, imagesToRemove, imagesToAdd);
            bsh.unset(_variableName);
         }
      }


      @Override
      protected boolean fixLayout() {


         boolean changed = false;
         boolean anyChildChanged;
         do {
            anyChildChanged = false;
            for (final OOTag child : _children) {
               if (child.fixLayout()) {
                  changed = true;
                  anyChildChanged = true;
                  break;
               }
            }
         }
         while (anyChildChanged);

         if (changed) {
            return true;
         }

         //         if (!_parent.isDummy() && _parent.isUniqueChild(this)) {
         //            _parent.setDummy(true);
         //            return true;
         //         }

         if (_parent.isUniqueChild(this)) {
            final OOTag.ICompound oldParent = _parent;

            oldParent.replaceWith(this);
            oldParent.replaceChildren(_children);
            _children.clear();

            addChild((OOTag) oldParent);

            return true;
         }

         return false;
      }


      @Override
      public void replaceChild(final OOTag oldChild,
                               final OOTag newChild) {
         final int indexOfOldChild = _children.indexOf(oldChild);
         if (indexOfOldChild == -1) {
            throw new IllegalArgumentException(oldChild + " is not a child of " + this);
         }
         _children.set(indexOfOldChild, newChild);
         newChild.replaceParent(this);
         oldChild.replaceParent(null);
      }


      @Override
      public void replaceChildren(final List<OOTag> newChildren) {
         //         for (final OOTag child : _children) {
         //            child.replaceParent(null);
         //         }
         _children.clear();
         for (final OOTag newChild : newChildren) {
            addChild(newChild);
         }
      }


      @Override
      public String toString() {
         return "FOR " + _variableName + " IN " + _expression;
      }


      @Override
      public void setDummy(final boolean dummy) {
         throw new IllegalArgumentException("FOR can't be dummy");
      }


      public static OOTag.For createTag(final OOToken.Tag tag,
                                        final Set<OOToken> usedTokens) throws OOSyntaxException {
         return new OOTag.For(tag, usedTokens);
      }
   }


   static final class Evaluation
            extends
               OOTag.Tag {
      private static final String EXPRESSION_FORMAT_DELIMITER = "#";

      private final String        _expression;
      private final String        _format;
      private MessageFormat       _messageFormat;


      private Evaluation(final OOToken.Tag token,
                         final Set<OOToken> usedTokens) throws OOSyntaxException {
         super(token, usedTokens);

         if (_contents.contains(Evaluation.EXPRESSION_FORMAT_DELIMITER)) {
            final int pos = _contents.indexOf(Evaluation.EXPRESSION_FORMAT_DELIMITER);

            _expression = _contents.substring(0, pos).trim();
            _format = _contents.substring(pos + 1).trim();
            //            System.out.println("Expression= *" + _expression + "*");
            //            System.out.println("Format= *" + _format + "*");

            if (_format.isEmpty()) {
               throw new OOSyntaxException("Invalid Expression-Format: {{ " + _contents + " }}");
            }
         }
         else {
            _expression = parseExpression(_contents);
            _format = null;
         }
      }


      private String parseExpression(final String expression) {
         // replace the "funny" quotes from OpenOffice with real double-quotes marks
         return expression.replace("”", "\"").replace("“", "\"");
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         printIdentation(output, level);
         output.write("{{ EVALUATION: ");
         output.write(_contents);
         output.write(" }}");
         printParent(output);
         output.write("\n");
      }


      @Override
      protected void evaluate(final Interpreter bsh,
                              final Writer output,
                              final ZipOutputStream outputZip,
                              final Set<String> imagesToRemove,
                              final List<String> imagesToAdd) throws IOException {
         try {
            final Object evaluation = bsh.eval(_expression);
            appendEvaluationResult(evaluation, output);
         }
         catch (final TargetError e) {
            if (getTemplate().isFormatNullPointerExceptionAsBlank() && (e.getTarget() instanceof NullPointerException)) {
               appendEvaluationResult("", output);
            }
            else {
               appendErrorResult(output, _contents, e.getMessage());
            }
         }
         catch (final EvalError e) {
            appendErrorResult(output, _contents, e.getMessage());
         }
      }


      private void appendEvaluationResult(final Object evaluation,
                                          final Writer output) throws IOException {
         output.append(format(evaluation));
      }


      private CharSequence format(final Object evaluation) {
         if (_format != null) {
            if (_messageFormat == null) {
               final String pattern = "{0," + _format + "}";

               try {
                  _messageFormat = new MessageFormat(pattern);
               }
               catch (final IllegalArgumentException e) {
                  return "" + evaluation + " ERROR: Invalid Format (" + _format + ")";
               }
            }

            return _messageFormat.format(new Object[] {
               evaluation
            });
         }

         if ((evaluation == null) && getTemplate().isFormatNullAsBlank()) {
            return "";
         }

         return "" + evaluation;
      }


      @Override
      protected boolean fixLayout() {
         return false;
      }


      @Override
      public String toString() {
         return "EVALUATION: " + _contents;
      }


      public static OOTag.Evaluation createTag(final OOToken.Tag tag,
                                               final Set<OOToken> usedTokens) throws OOSyntaxException {
         return new OOTag.Evaluation(tag, usedTokens);
      }

   }


   static final class ImageEvaluation
            extends
               OOTag {
      final private OOToken.XMLElement _frameToken;
      final private OOToken.XMLElement _imageToken;
      final private String             _expression;
      private boolean                  _firstEvaluation;


      private ImageEvaluation(final OOToken.XMLElement frameToken,
                              final OOToken.XMLElement imageToken,
                              final String expression) {
         _frameToken = frameToken;
         _imageToken = imageToken;
         _expression = expression;

         _firstEvaluation = true;
      }


      @Override
      protected void evaluate(final Interpreter bsh,
                              final Writer output,
                              final ZipOutputStream outputZip,
                              final Set<String> imagesToRemove,
                              final List<String> imagesToAdd) throws IOException {

         if (outputZip == null) {
            System.out.println("WARNING: Image Evaluation not supported without a outputZip");
            return;
         }

         if (_firstEvaluation) {
            for (final XMLAttribute att : _imageToken.getAttributes()) {
               if (att.getName().equals("xlink:href")) {
                  final String imageURL = att.getValue();
                  imagesToRemove.add(imageURL);
               }
            }
            _firstEvaluation = false;
         }


         String name = null;
         RenderedImage image = null;

         try {
            final Object evaluation = bsh.eval(_expression);

            if (evaluation == null) {
               if (!getTemplate().isFormatNullPointerExceptionAsBlank()) {
                  name = _expression + " evaluated to null";
                  image = createErrorImage(name);
               }
            }
            else {
               if (evaluation instanceof RenderedImage) {
                  name = _expression;
                  image = (RenderedImage) evaluation;
               }
               else if (evaluation instanceof OONamedImage) {
                  final OONamedImage namedImage = (OONamedImage) evaluation;
                  name = namedImage.getName();
                  image = namedImage.getImage();
               }
               else if (evaluation instanceof String) {
                  name = (String) evaluation;
                  image = ImageIO.read(new File(name));
               }
               else if (evaluation instanceof File) {
                  final File file = (File) evaluation;
                  name = file.getAbsolutePath();
                  image = ImageIO.read(file);
               }
               else {
                  name = "invalid result (" + evaluation.getClass() + ")";
                  image = createErrorImage(name);
               }
            }
         }
         catch (final TargetError e) {
            if (getTemplate().isFormatNullPointerExceptionAsBlank() && (e.getTarget() instanceof NullPointerException)) {
               //               name = "";
               //               image = createImage(name);
               return;
            }

            name = e.getTarget().toString();
            image = createErrorImage(name);
         }
         catch (final EvalError e) {
            name = e.toString();
            image = createErrorImage(name);
         }


         if (name == null) {
            name = "";
         }
         if (image == null) {
            image = createEmptyImage();
         }
         //String nombre = UUID.randomUUID().toString().replace("-", "");

         final String imageEntryName = ("1000" + UUID.randomUUID().toString().replaceAll("-", "")).substring(0, 32);

         final String imageFileName = "Pictures/" + imageEntryName + ".png";
         imagesToAdd.add(imageFileName);
         //final String imageFileName = "Pictures/" + nombre + ".png";
         saveImageIntoZip(imageFileName, image, outputZip);


         writeXML(name, imageFileName, output);
      }


      private void writeXML(final String name,
                            final String imageFileName,
                            final Writer output) throws IOException {
         _frameToken.setAttribute("draw:name", name);
         _imageToken.setAttribute("xlink:href", imageFileName);

         // open frame
         output.write("<");
         output.write(_frameToken.getName());
         for (final XMLAttribute attribute : _frameToken.getAttributes()) {
            attribute.printXML(output);
         }
         output.write(">");

         // image (open & close)
         output.write("<");
         output.write(_imageToken.getName());
         for (final XMLAttribute attribute : _imageToken.getAttributes()) {
            attribute.printXML(output);
         }
         output.write("/>");

         // close frame
         output.write("</");
         output.write(_frameToken.getName());
         output.write(">");
      }


      private void saveImageIntoZip(final String imageName,
                                    final RenderedImage image,
                                    final ZipOutputStream outputZip) throws IOException {
         final ZipEntry contentEntry = new ZipEntry(imageName);

         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         ImageIO.write(image, "png", outputStream);

         final byte[] outputBytes = outputStream.toByteArray();
         contentEntry.setSize(outputBytes.length);

         outputZip.putNextEntry(contentEntry);
         outputZip.write(outputBytes);
      }


      private RenderedImage createEmptyImage() {
         return createImage("", null);
      }


      private RenderedImage createErrorImage(final String msg) {
         return createImage(msg, Color.RED);
      }


      private RenderedImage createImage(final String msg,
                                        final Color color) {
         final BufferedImage image = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_ARGB);
         final Graphics2D g2d = image.createGraphics();
         if (color != null) {
            g2d.setBackground(color);
            g2d.clearRect(0, 0, 1024, 768);
         }
         if (msg != null) {
            g2d.drawString(msg, 5, 25);
         }
         return image;
      }


      @Override
      protected boolean fixLayout() {
         return false;
      }


      @Override
      protected void printStructure(final Writer output,
                                    final int level) throws IOException {
         printIdentation(output, level);
         output.write("IMAGE: {{" + OOUtils.escape(_expression) + "}}");
         printParent(output);
         output.write("\n");
      }


      @Override
      public String toString() {
         return "IMAGE: " + _expression;
      }

   }

}
