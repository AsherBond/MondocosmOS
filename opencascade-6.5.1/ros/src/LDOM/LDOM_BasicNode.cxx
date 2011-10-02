// File:      LDOM_BasicNode.cxx
// Created:   27.06.01 12:18:10
// Author:    Alexander GRIGORIEV
// Copyright: OpenCascade 2001
// History:

#include <LDOM_BasicNode.hxx>
#include <LDOM_BasicElement.hxx>
#include <LDOM_BasicAttribute.hxx>
#include <LDOM_BasicText.hxx>

//=======================================================================
//function : operator =
//purpose  : Assignment
//=======================================================================

LDOM_BasicNode& LDOM_BasicNode::operator = (const LDOM_BasicNode& anOther)
{
  myNodeType = anOther.getNodeType();
  mySibling  = anOther.GetSibling();
  return * this;
}

//=======================================================================
//function : GetSibling
//purpose  : also detaches NULL siblings
//=======================================================================

const LDOM_BasicNode * LDOM_BasicNode::GetSibling () const
{
  while (mySibling)
    if (mySibling -> isNull())
      (const LDOM_BasicNode *&) mySibling = mySibling -> mySibling;
    else break;
  return mySibling;
}

#ifdef DEB
#ifndef WNT
//=======================================================================
// Debug Function for DBX: use "print -p <Variable> or pp <Variable>"
//=======================================================================
#include <LDOM_OSStream.hxx>
#define FLITERAL 0x10
#define MAX_SIBLINGS 8
char * db_pretty_print (const LDOM_BasicNode * aBNode, int fl, char *)
{
  LDOM_OSStream out (128);
  switch (aBNode -> getNodeType()) {
  case LDOM_Node::ELEMENT_NODE:
    {
      const LDOM_BasicElement& aBElem = * (const LDOM_BasicElement *) aBNode;
      if ((fl & FLITERAL) == 0) out << "LDOM_BasicElement: ";
      out << '<' << aBElem.GetTagName();
      aBNode = aBElem.GetFirstChild();
      while (aBNode) {
        if (aBNode -> getNodeType() == LDOM_Node::ATTRIBUTE_NODE)
          out << ' ' <<
            db_pretty_print ((const LDOM_BasicAttribute *) aBNode, FLITERAL, 0);
        aBNode = aBNode -> GetSibling();
      }
      out << '>';
      if ((fl & FLITERAL) == 0) {
        aBNode = aBElem.GetFirstChild();
        int counter = MAX_SIBLINGS;
        if (aBNode) {
          out << endl << " == Children:" << endl;
          while (aBNode && counter--) {
            if (aBNode -> getNodeType() == LDOM_Node::ATTRIBUTE_NODE) break;
            out << "  *(LDOM_BasicNode*)" << aBNode << " : " <<
              db_pretty_print (aBNode, FLITERAL, 0) << endl;
            aBNode = aBNode -> GetSibling();
          }
        }
        aBNode = aBElem.GetSibling();
        if (aBNode) {
          out << " == Siblings:" << endl;
          counter = MAX_SIBLINGS;
          while (aBNode && counter--) {
            if (aBNode -> getNodeType() == LDOM_Node::ATTRIBUTE_NODE) break;
            out << "  *(LDOM_BasicNode*)" << aBNode << " : " <<
              db_pretty_print (aBNode, FLITERAL, 0) << endl;
            aBNode = aBNode -> GetSibling();
          }
        }
      }
      out << ends;
      break;
    }
  case LDOM_Node::ATTRIBUTE_NODE:
    {
      const LDOM_BasicAttribute& aBAtt = * (const LDOM_BasicAttribute *) aBNode;
      if ((fl & FLITERAL) == 0) out << "LDOM_BasicAttribute: ";
      out << aBAtt.GetName() << '='
        << db_pretty_print (&aBAtt.GetValue(), FLITERAL, 0) << ends;
      break;
    }
  case LDOM_Node::TEXT_NODE:
  case LDOM_Node::COMMENT_NODE:
  case LDOM_Node::CDATA_SECTION_NODE:
    {
      const LDOM_BasicText& aBText = * (const LDOM_BasicText *) aBNode;
      if ((fl & FLITERAL) == 0) out << "LDOM_BasicText: ";
      out << db_pretty_print (&aBText.GetData(), FLITERAL, 0) << ends;
      break;
    }
  default:
    out << "UNKNOWN" << ends;
    break;
  }
  return (char *)out.str();
}

char * db_pretty_print (const LDOM_Node * aBNode, int fl, char * c)
{
  return db_pretty_print (&aBNode -> Origin(), fl, c);
}

#endif
#endif
