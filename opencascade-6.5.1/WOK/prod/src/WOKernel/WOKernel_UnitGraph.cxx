// File:	WOKernel_UnitGraph.cxx
// Created:	Tue Jan  9 16:39:33 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKernel_UnitGraph.ixx>


//=======================================================================
//function : WOKernel_UnitGraph
//purpose  : 
//=======================================================================
 WOKernel_UnitGraph::WOKernel_UnitGraph(const Handle(WOKernel_Workbench)& awb)
{
  mylocator = new WOKernel_Locator(awb);
}

//=======================================================================
//function : WOKernel_UnitGraph
//purpose  : 
//=======================================================================
 WOKernel_UnitGraph::WOKernel_UnitGraph(const Handle(WOKernel_Locator)& alocator)
: mylocator(alocator)

{
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================
void WOKernel_UnitGraph::Add(const Handle(TCollection_HAsciiString)& aname,
			     const Handle(TColStd_HSequenceOfHAsciiString)& suppliers)
{
  if (!myUDMap.IsBound(aname))
    myUDMap.Bind(aname,suppliers);
}

//=======================================================================
//function : Add
//purpose  : 
//=======================================================================
void WOKernel_UnitGraph::Add(const Handle(TCollection_HAsciiString)& aname,
			     const Handle(TCollection_HAsciiString)& supplier)
{
  if (!myUDMap.IsBound(aname))
    myUDMap.Bind(aname,new TColStd_HSequenceOfHAsciiString);

  const Handle(TColStd_HSequenceOfHAsciiString)& aseq = myUDMap.Find(aname);

  if(!aseq.IsNull()) aseq->Append(supplier);
}

//=======================================================================
//function : Remove
//purpose  : 
//=======================================================================
void WOKernel_UnitGraph::Remove(const Handle(TCollection_HAsciiString)& aname)
{
  if (myUDMap.IsBound(aname))
    myUDMap.UnBind(aname);
}

//=======================================================================
//function : Suppliers
//purpose  : 
//=======================================================================
const Handle(TColStd_HSequenceOfHAsciiString)& WOKernel_UnitGraph::Suppliers(const Handle(TCollection_HAsciiString)& aname) const
{
  if (myUDMap.IsBound(aname)) {
    return myUDMap.Find(aname);
  }
  else {
    static Handle(TColStd_HSequenceOfHAsciiString) res;
    return res;
  }
}

//=======================================================================
//function : Contains
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_UnitGraph::Contains(const Handle(TCollection_HAsciiString)& aname) const 
{
  return myUDMap.IsBound(aname);
}

//=======================================================================
//function : Locator
//purpose  : 
//=======================================================================
Handle(WOKernel_Locator) WOKernel_UnitGraph::Locator() const
{
  return mylocator;
}


