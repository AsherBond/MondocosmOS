//--------------------------------------------------------------------
//
//  File Name : IGESDefs_AssociativityDef.cxx
//  Date      :
//  Author    : CKY / Contract Toubro-Larsen
//  Copyright : MATRA-DATAVISION 1993
//
//--------------------------------------------------------------------

#include <IGESDefs_AssociativityDef.ixx>


    IGESDefs_AssociativityDef::IGESDefs_AssociativityDef ()    {  }


    void  IGESDefs_AssociativityDef::Init
  (const Handle(TColStd_HArray1OfInteger)& requirements,
   const Handle(TColStd_HArray1OfInteger)& orders,
   const Handle(TColStd_HArray1OfInteger)& numItems,
   const Handle(IGESBasic_HArray1OfHArray1OfInteger)& items)
{
  Standard_Integer len = requirements->Length();
  if ( requirements->Lower() != 1 ||
      (orders->Lower()       != 1 || orders->Length()   != len) ||
      (numItems->Lower()     != 1 || numItems->Length() != len) ||
      (items->Lower()        != 1 || items->Length()    != len) )
    Standard_DimensionMismatch::Raise("IGESDefs_AssociativityDef : Init");

  theBackPointerReqs = requirements;
  theClassOrders     = orders;
  theNbItemsPerClass = numItems;
  theItems           = items;
  InitTypeAndForm(302,FormNumber());
//  FormNumber is free over 5000
}

    void IGESDefs_AssociativityDef::SetFormNumber (const Standard_Integer form)
{
  InitTypeAndForm(302,form);
}

    Standard_Integer  IGESDefs_AssociativityDef::NbClassDefs () const 
{
  return theBackPointerReqs->Length();
}

    Standard_Boolean  IGESDefs_AssociativityDef::IsBackPointerReq
  (const Standard_Integer ClassNum) const 
{
  return (theBackPointerReqs->Value(ClassNum) == 1);
//  1 True  2 False
}

    Standard_Integer  IGESDefs_AssociativityDef::BackPointerReq
  (const Standard_Integer ClassNum) const 
{
  return theBackPointerReqs->Value(ClassNum);
}

    Standard_Boolean  IGESDefs_AssociativityDef::IsOrdered
  (const Standard_Integer ClassNum) const 
{
  return (theClassOrders->Value(ClassNum) == 1);
//  1 True  2 False
}

    Standard_Integer  IGESDefs_AssociativityDef::ClassOrder
  (const Standard_Integer ClassNum) const 
{
  return theClassOrders->Value(ClassNum);
}

    Standard_Integer  IGESDefs_AssociativityDef::NbItemsPerClass
  (const Standard_Integer ClassNum) const 
{
  return theNbItemsPerClass->Value(ClassNum);
}

    Standard_Integer  IGESDefs_AssociativityDef::Item
  (const Standard_Integer ClassNum, const Standard_Integer ItemNum) const 
{
  return theItems->Value(ClassNum)->Value(ItemNum);
}
