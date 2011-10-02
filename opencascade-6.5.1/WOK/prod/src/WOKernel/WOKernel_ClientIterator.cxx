// File:	WOKernel_ClientIterator.cxx
// Created:	Thu Feb  6 16:28:45 1997
// Author:	Prestataire Pascal BABIN
//		<pba@voilax.paris1.matra-dtv.fr>

#include <Standard_NoMoreObject.hxx>
#include <Standard_NoSuchObject.hxx>

#include <TCollection_HAsciiString.hxx>

#include <WOKernel_UnitGraph.hxx>
#include <WOKernel_ClientIterator.ixx>


//=======================================================================
//function : WOKernel_ClientIterator
//purpose  : 
//=======================================================================

WOKernel_ClientIterator::WOKernel_ClientIterator (const Handle(WOKernel_UnitGraph)& aClientGraph,
						  const Handle(TCollection_HAsciiString)& anUd)
{
  myClientGraph  = aClientGraph;
  myCurrentUd    = anUd;
  myMore         = Standard_True;
  myIndex        = 0;
  GetClients();
}

//=======================================================================
//function : GetClients
//purpose  : 
//=======================================================================
void WOKernel_ClientIterator::GetClients()
{
  myClients = myClientGraph->Suppliers(myCurrentUd);

  if(!myClients->Length()) myMore = Standard_False;
  else myIndex = myClients->Length();  
}

//=======================================================================
//function : More
//purpose  : 
//=======================================================================

Standard_Boolean WOKernel_ClientIterator::More () const 
{
  return myMore;
}


//=======================================================================
//function : Next
//purpose  : 
//=======================================================================

void WOKernel_ClientIterator::Next ()  
{
  Handle(TCollection_HAsciiString) aClient = myClients->Value(myIndex);
  myIndex--;

  if (myIndex == 0) myMore = Standard_False;

}


//=======================================================================
//function : Value
//purpose  : 
//=======================================================================

Handle(TCollection_HAsciiString) WOKernel_ClientIterator::Value() 
{  
  return myClients->Value(myIndex);
}



