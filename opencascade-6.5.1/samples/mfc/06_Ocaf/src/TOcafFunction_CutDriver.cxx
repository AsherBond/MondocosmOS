// File:	TOcafFunction_CutDriver.cxx
// Created:	Mon Dec 27 10:37:13 1999
// Author:	Vladislav ROMASHKO
//		<vro@flox.nnov.matra-dtv.fr>


#include <stdafx.h>
#include <TOcafFunction_CutDriver.hxx>

#include <TNaming_NamedShape.hxx>
#include <TNaming_Builder.hxx>

#include <BRepAlgoAPI_Cut.hxx>
#include <TCollection_AsciiString.hxx>
#include <TDF_Tool.hxx>

//=======================================================================
//function : GetID
//purpose  :
//=======================================================================

const Standard_GUID& TOcafFunction_CutDriver::GetID() {
  static Standard_GUID anID("22D22E52-D69A-11d4-8F1A-0060B0EE18E8");
  return anID;
}


//=======================================================================
//function : TPartStd_CutDriver
//purpose  : Creation of an instance of the driver. It's possible (and recommended)
//         : to have only one instance of a driver for the whole session.
//=======================================================================

TOcafFunction_CutDriver::TOcafFunction_CutDriver()
{}

//=======================================================================
//function : Validate
//purpose  : Validation of the object label, its arguments and its results.
//=======================================================================

void TOcafFunction_CutDriver::Validate(TFunction_Logbook& log) const
{
  // We validate the object label ( Label() ), all the arguments and the results of the object:
  log.SetValid(Label(), Standard_True);
}

//=======================================================================
//function : MustExecute
//purpose  : We call this method to check if the object was modified to
//         : be invoked. If the object label or an argument is modified,
//         : we must recompute the object - to call the method Execute().
//=======================================================================

Standard_Boolean TOcafFunction_CutDriver::MustExecute(const TFunction_Logbook& log) const
{
  // If the object's label is modified:
  if (log.IsModified(Label())) return Standard_True; 

  // Cut (in our simple case) has two arguments: The original shape, and the tool shape.
  // They are on the child labels of the cut's label:
  // So, OriginalNShape  - is attached to the first  child label
  //     ToolNShape - is attached to the second child label,
  //     .
  // Let's check them:
  Handle(TDF_Reference) OriginalRef;
  //TDF_Label aLabel = Label().FindChild(1);
/*
  BOOL f = Label().IsNull();
  int a = Label().NbChildren();
*/
  TCollection_AsciiString aEntry;
  TDF_Tool::Entry(Label(), aEntry);
  cout << "Entry: "<<aEntry.ToCString()<<endl;
  Label().FindChild(1).FindAttribute(TDF_Reference::GetID(),OriginalRef);
  if (log.IsModified(OriginalRef->Get()))   return Standard_True; // Original shape.

  Handle(TDF_Reference) ToolRef;
  Label().FindChild(2).FindAttribute(TDF_Reference::GetID(),ToolRef);
  if (log.IsModified(ToolRef->Get()))   return Standard_True; // Tool shape.
  
  // if there are no any modifications concerned the cut,
  // it's not necessary to recompute (to call the method Execute()):
  return Standard_False;
}

//=======================================================================
//function : Execute
//purpose  : 
//         : We compute the object and topologically name it.
//         : If during the execution we found something wrong,
//         : we return the number of the failure. For example:
//         : 1 - an attribute hasn't been found,
//         : 2 - algorithm failed,
//         : if there are no any mistakes occurred we return 0:
//         : 0 - no mistakes were found.
//=======================================================================

Standard_Integer TOcafFunction_CutDriver::Execute(TFunction_Logbook& log) const
{
  // Let's get the arguments (OriginalNShape, ToolNShape of the object):

	// First, we have to retrieve the TDF_Reference attributes to obtain the root labels of the OriginalNShape and the ToolNShape:
	Handle(TDF_Reference)  OriginalRef, ToolRef;
	if (!Label().FindChild(1).FindAttribute(TDF_Reference::GetID(), OriginalRef )) return 1;
	TDF_Label OriginalLab = OriginalRef->Get();
	if (!Label().FindChild(2).FindAttribute(TDF_Reference::GetID(), ToolRef)) return 1;
	TDF_Label ToolLab = ToolRef->Get();

	// Get the TNaming_NamedShape attributes of these labels
	Handle(TNaming_NamedShape) OriginalNShape, ToolNShape;
	if (!( OriginalLab.FindAttribute(TNaming_NamedShape::GetID(),OriginalNShape) ))
		Standard_Failure::Raise("TOcaf_Commands::CutObjects");		
	if (!( ToolLab.FindAttribute(TNaming_NamedShape::GetID(),ToolNShape) ))
		Standard_Failure::Raise("TOcaf_Commands::CutObjects");		

	// Now, let's get the TopoDS_Shape of these TNaming_NamedShape:
	TopoDS_Shape OriginalShape  = OriginalNShape->Get();
	TopoDS_Shape ToolShape = ToolNShape->Get();

// STEP 2:
	// Let's call for algorithm computing a cut operation:
	BRepAlgoAPI_Cut mkCut(OriginalShape, ToolShape);
	// Let's check if the Cut has been successfull:
	if (!mkCut.IsDone()) 
	{
		MessageBox(0,"Cut not done.","Cut Function Driver",MB_ICONERROR);
		return 2;
	}
	TopoDS_Shape ResultShape = mkCut.Shape();

	// Build a TNaming_NamedShape using built cut
	TNaming_Builder B(Label());
	B.Modify( OriginalShape, ResultShape);
// That's all:
  // If there are no any mistakes we return 0:
  return 0;
}

TOcafFunction_CutDriver::~TOcafFunction_CutDriver() {}
 


Standard_EXPORT Handle_Standard_Type& TOcafFunction_CutDriver_Type_()
{

    static Handle_Standard_Type aType1 = STANDARD_TYPE(TFunction_Driver);
  if ( aType1.IsNull()) aType1 = STANDARD_TYPE(TFunction_Driver);
  static Handle_Standard_Type aType2 = STANDARD_TYPE(MMgt_TShared);
  if ( aType2.IsNull()) aType2 = STANDARD_TYPE(MMgt_TShared);
  static Handle_Standard_Type aType3 = STANDARD_TYPE(Standard_Transient);
  if ( aType3.IsNull()) aType3 = STANDARD_TYPE(Standard_Transient);
 

  static Handle_Standard_Transient _Ancestors[]= {aType1,aType2,aType3,NULL};
  static Handle_Standard_Type _aType = new Standard_Type("TOcafFunction_CutDriver",
			                                 sizeof(TOcafFunction_CutDriver),
			                                 1,
			                                 (Standard_Address)_Ancestors,
			                                 (Standard_Address)NULL);

  return _aType;
}


// DownCast method
//   allow safe downcasting
//
const Handle(TOcafFunction_CutDriver) Handle(TOcafFunction_CutDriver)::DownCast(const Handle(Standard_Transient)& AnObject) 
{
  Handle(TOcafFunction_CutDriver) _anOtherObject;

  if (!AnObject.IsNull()) {
     if (AnObject->IsKind(STANDARD_TYPE(TOcafFunction_CutDriver))) {
       _anOtherObject = Handle(TOcafFunction_CutDriver)((Handle(TOcafFunction_CutDriver)&)AnObject);
     }
  }

  return _anOtherObject ;
}
const Handle(Standard_Type)& TOcafFunction_CutDriver::DynamicType() const 
{ 
  return STANDARD_TYPE(TOcafFunction_CutDriver) ; 
}
Standard_Boolean TOcafFunction_CutDriver::IsKind(const Handle(Standard_Type)& AType) const 
{ 
  return (STANDARD_TYPE(TOcafFunction_CutDriver) == AType || TFunction_Driver::IsKind(AType)); 
}

Handle_TOcafFunction_CutDriver::~Handle_TOcafFunction_CutDriver() {}


