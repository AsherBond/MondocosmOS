// File:	XCAFDoc_Location.cxx
// Created:	Tue Aug 15 11:14:56 2000
// Author:	data exchange team
//		<det@strelox.nnov.matra-dtv.fr>


#include <XCAFDoc_Location.ixx>

//=======================================================================
//function : Constructor
//purpose  : 
//=======================================================================

XCAFDoc_Location::XCAFDoc_Location()
{
}

//=======================================================================
//function : GetID
//purpose  : 
//=======================================================================

const Standard_GUID& XCAFDoc_Location::GetID() 
{
  static Standard_GUID LocationID ("efd212ef-6dfd-11d4-b9c8-0060b0ee281b");
  return LocationID; 
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

 Handle(XCAFDoc_Location) XCAFDoc_Location::Set(const TDF_Label& L,const TopLoc_Location& Loc) 
{
  Handle(XCAFDoc_Location) A;
  if (!L.FindAttribute (XCAFDoc_Location::GetID(), A)) {
    A = new XCAFDoc_Location ();
    L.AddAttribute(A);
  }
  A->Set (Loc); 
  return A;
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================

 void XCAFDoc_Location::Set(const TopLoc_Location& Loc) 
{
  Backup();
  myLocation = Loc;
}

//=======================================================================
//function : Get
//purpose  : 
//=======================================================================

 TopLoc_Location XCAFDoc_Location::Get() const
{
  return myLocation;
}

//=======================================================================
//function : ID
//purpose  : 
//=======================================================================

const Standard_GUID& XCAFDoc_Location::ID() const
{
  return GetID();
}

//=======================================================================
//function : Restore
//purpose  : 
//=======================================================================

 void XCAFDoc_Location::Restore(const Handle(TDF_Attribute)& With) 
{
  myLocation = Handle(XCAFDoc_Location)::DownCast(With)->Get();
}

//=======================================================================
//function : NewEmpty
//purpose  : 
//=======================================================================

 Handle(TDF_Attribute) XCAFDoc_Location::NewEmpty() const
{
  return new XCAFDoc_Location();
}

//=======================================================================
//function : Paste
//purpose  : 
//=======================================================================

 void XCAFDoc_Location::Paste(const Handle(TDF_Attribute)& Into,const Handle(TDF_RelocationTable)& /* RT */) const
{
  Handle(XCAFDoc_Location)::DownCast(Into)->Set(myLocation);

}

