// File:	WOKernel_ImplDepIterator.cxx
// Created:	Thu Feb  6 16:28:45 1997
// Author:	Prestataire Pascal BABIN
//		<pba@voilax.paris1.matra-dtv.fr>

#include <Standard_NoMoreObject.hxx>
#include <Standard_NoSuchObject.hxx>
#include <Standard_ProgramError.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKernel_ImplDepIterator.ixx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>


//=======================================================================
//function : WOKernel_ImplDepIterator
//purpose  : 
//=======================================================================

WOKernel_ImplDepIterator::WOKernel_ImplDepIterator (const Handle(WOKernel_UnitGraph) & aDependencyGraph,
						    const Handle(TCollection_HAsciiString)& anUd)
{
  myDepGraph  = aDependencyGraph;
  myCurrentUd = anUd;
  myMore      = Standard_True;
  myIndex     = 0;
  GetSuppliers();
}

//=======================================================================
//function : GetSuppliers
//purpose  : 
//=======================================================================
void WOKernel_ImplDepIterator::GetSuppliers()
{
  if(!myDepGraph->Contains(myCurrentUd))
    {
      Handle(WOKernel_DevUnit) aunit = myDepGraph->Locator()->LocateDevUnit(myCurrentUd);
      
      if(!aunit.IsNull()) 
	mySuppliers = aunit->ImplementationDepList(myDepGraph);
    }
  else
    {
      mySuppliers = myDepGraph->Suppliers(myCurrentUd);
    } 

  if (mySuppliers.IsNull()) {
    ErrorMsg() << "WOKernel_ImplDepIterator::GetSuppliers" << "Unable to get suppliers for unit " << myCurrentUd->ToCString() << endm;
    myMore = Standard_False;
  }
  else if(!mySuppliers->Length()) {
    myMore = Standard_False;
  }
  else {
    myIndex = mySuppliers->Length();  
  }
}

//=======================================================================
//function : More
//purpose  : 
//=======================================================================

Standard_Boolean WOKernel_ImplDepIterator::More () const 
{
  return myMore;
}


//=======================================================================
//function : Next
//purpose  : 
//=======================================================================

void WOKernel_ImplDepIterator::Next ()  
{
  Handle(TCollection_HAsciiString) aSupplier = mySuppliers->Value(myIndex);
  myIndex--;

  if (myIndex == 0) myMore = Standard_False;

}


//=======================================================================
//function : Value
//purpose  : 
//=======================================================================

Handle(TCollection_HAsciiString) WOKernel_ImplDepIterator::Value () 
{  
  return mySuppliers->Value(myIndex);
}



