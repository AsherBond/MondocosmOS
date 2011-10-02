//--------------------------------------------------------------------
//
//  File Name : IGESDefs_AttributeDef.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDefs_AttributeDef.ixx>
#include <Standard_DimensionMismatch.hxx>
#include <TColStd_HArray1OfReal.hxx>
#include <TColStd_HArray1OfInteger.hxx>
#include <IGESData_HArray1OfIGESEntity.hxx>
#include <IGESGraph_HArray1OfTextDisplayTemplate.hxx>
#include <Interface_HArray1OfHAsciiString.hxx>
#include <Interface_Macros.hxx>


//  For each Attribute Value, according to Attribute Type :
// 0 -> Void, 1 -> Integer, 2 -> Real, 3 -> String, 4 -> Entity   6 -> Logical


    IGESDefs_AttributeDef::IGESDefs_AttributeDef ()    {  }


    void IGESDefs_AttributeDef::Init
  (const Handle(TCollection_HAsciiString)& aName,
   const Standard_Integer aListType,
   const Handle(TColStd_HArray1OfInteger)& attrTypes,
   const Handle(TColStd_HArray1OfInteger)& attrValueDataTypes,
   const Handle(TColStd_HArray1OfInteger)& attrValueCounts,
   const Handle(TColStd_HArray1OfTransient)& attrValues,
   const Handle(IGESDefs_HArray1OfHArray1OfTextDisplayTemplate)&
     attrValuePointers)
{
  Standard_Integer nb = attrTypes->Length();
  if (attrTypes->Lower() != 1 || attrValueDataTypes->Lower() != 1 ||
      attrValueDataTypes->Length() != nb ||
      attrValueCounts->Lower()     != 1  || attrValueCounts->Length() != nb)
    Standard_DimensionMismatch::Raise("IGESDefs_AttributeDef : Init");

  if (FormNumber() >= 1)
    if (attrValues->Lower() != 1 || attrValues->Length() != nb)
      Standard_DimensionMismatch::Raise("IGESDefs_AttributeDef : Init");

  if (FormNumber() == 2)
    if (attrValuePointers->Lower() != 1 || attrValuePointers->Length() != nb)
      Standard_DimensionMismatch::Raise("IGESDefs_AttributeDef : Init");
// Form 1 : attrValues defined  Form = 2 : attrValuePointers defined

  theName               = aName;
  theListType           = aListType;
  theAttrTypes          = attrTypes;
  theAttrValueDataTypes = attrValueDataTypes;
  theAttrValueCounts    = attrValueCounts;
  theAttrValues         = attrValues;
  theAttrValuePointers  = attrValuePointers;
  if      (attrValues.IsNull())        InitTypeAndForm(322,0);
  else if (attrValuePointers.IsNull()) InitTypeAndForm(322,1);
  else                                 InitTypeAndForm(322,2);
}

    Standard_Boolean  IGESDefs_AttributeDef::HasTableName () const
{
  return (!theName.IsNull());
}

    Handle(TCollection_HAsciiString)  IGESDefs_AttributeDef::TableName () const
{
  return theName;
}

    Standard_Integer  IGESDefs_AttributeDef::ListType () const
{
  return theListType;
}

    Standard_Integer  IGESDefs_AttributeDef::NbAttributes () const
{
  return theAttrTypes->Length();
}

    Standard_Integer  IGESDefs_AttributeDef::AttributeType
  (const Standard_Integer num) const
{
  return theAttrTypes->Value(num);
}

    Standard_Integer  IGESDefs_AttributeDef::AttributeValueDataType
  (const Standard_Integer num) const
{
  return theAttrValueDataTypes->Value(num);
}

    Standard_Integer  IGESDefs_AttributeDef::AttributeValueCount
  (const Standard_Integer num) const
{
  return theAttrValueCounts->Value(num);
}

    Standard_Boolean  IGESDefs_AttributeDef::HasValues () const
{
  return (!theAttrValues.IsNull());
}

    Standard_Boolean  IGESDefs_AttributeDef::HasTextDisplay () const
{
  return (!theAttrValuePointers.IsNull());
}

    Handle(IGESGraph_TextDisplayTemplate)
    IGESDefs_AttributeDef::AttributeTextDisplay
  (const Standard_Integer AttrNum, const Standard_Integer PointerNum) const
{
  Handle(IGESGraph_TextDisplayTemplate)  res;
  if (HasTextDisplay()) res =
    theAttrValuePointers->Value(AttrNum)->Value(PointerNum);
  return res;
}

    Handle(Standard_Transient)  IGESDefs_AttributeDef::AttributeList
  (const Standard_Integer AttrNum) const
{
  Handle(Standard_Transient) nulres;
  if (!HasValues()) return nulres;
  return theAttrValues->Value(AttrNum);
}

    Standard_Integer  IGESDefs_AttributeDef::AttributeAsInteger
  (const Standard_Integer AttrNum, const Standard_Integer ValueNum) const
{
  return GetCasted(TColStd_HArray1OfInteger,theAttrValues->Value(AttrNum))
    ->Value(ValueNum);
}

    Standard_Real  IGESDefs_AttributeDef::AttributeAsReal
  (const Standard_Integer AttrNum, const Standard_Integer ValueNum) const
{
  return GetCasted(TColStd_HArray1OfReal,theAttrValues->Value(AttrNum))
    ->Value(ValueNum);
}

    Handle(TCollection_HAsciiString)  IGESDefs_AttributeDef::AttributeAsString
  (const Standard_Integer AttrNum, const Standard_Integer ValueNum) const
{
  return GetCasted(Interface_HArray1OfHAsciiString,theAttrValues->Value(AttrNum))
    ->Value(ValueNum);
}

    Handle(IGESData_IGESEntity)  IGESDefs_AttributeDef::AttributeAsEntity
  (const Standard_Integer AttrNum, const Standard_Integer ValueNum) const
{
  return GetCasted(IGESData_HArray1OfIGESEntity,theAttrValues->Value(AttrNum))
    ->Value(ValueNum);
}

    Standard_Boolean  IGESDefs_AttributeDef::AttributeAsLogical
  (const Standard_Integer AttrNum, const Standard_Integer ValueNum) const
{
  return (GetCasted(TColStd_HArray1OfInteger,theAttrValues->Value(AttrNum))
	  ->Value(ValueNum) != 0);
}
