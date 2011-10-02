// File:	TOcafFunction_BoxDriver.cxx
// Created:	Mon Dec 27 10:37:13 1999
// Author:	Vladislav ROMASHKO
//		<vro@flox.nnov.matra-dtv.fr>


#include <stdafx.h>
#include <TOcafFunction_BoxDriver.hxx>

//=======================================================================
//function : GetID
//purpose  :
//=======================================================================

const Standard_GUID& TOcafFunction_BoxDriver::GetID() {
  static Standard_GUID anID("22D22E51-D69A-11d4-8F1A-0060B0EE18E8");
  return anID;
}


//=======================================================================
//function : TPartStd_BoxDriver
//purpose  : Creation of an instance of the driver. It's possible (and recommended)
//         : to have only one instance of a driver for the whole session.
//=======================================================================

TOcafFunction_BoxDriver::TOcafFunction_BoxDriver()
{}

//=======================================================================
//function : Validate
//purpose  : Validation of the object label, its arguments and its results.
//=======================================================================

void TOcafFunction_BoxDriver::Validate(TFunction_Logbook& log) const
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
Standard_Boolean TOcafFunction_BoxDriver::MustExecute(const TFunction_Logbook& log) const
{
	// If the object's label is modified:
  if (log.IsModified(Label())) return Standard_True; 

  // Cut (in our simple case) has two arguments: The original shape, and the tool shape.
  // They are on the child labels of the box's label:
  // So, OriginalNShape  - is attached to the first  child label
  //     ToolNShape - is attached to the second child label.
  // 
  // Let's check them:
  if (log.IsModified(Label().FindChild(1))) return Standard_True; // width.
  if (log.IsModified(Label().FindChild(2))) return Standard_True; // length,
  if (log.IsModified(Label().FindChild(3))) return Standard_True; // width.
  if (log.IsModified(Label().FindChild(4))) return Standard_True; // length,
  if (log.IsModified(Label().FindChild(5))) return Standard_True; // width.
  if (log.IsModified(Label().FindChild(6))) return Standard_True; // length,
  
 // if there are no any modifications concerned the box,
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
Standard_Integer TOcafFunction_BoxDriver::Execute(TFunction_Logbook& log) const
{
	// Get the values of dimension and position attributes 
	Handle(TDataStd_Real) TSR;
	Standard_Real x,y,z,l,h,w;
	if (!Label().FindChild(1).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	l=TSR->Get();

	if (!Label().FindChild(2).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	h=TSR->Get();

	if (!Label().FindChild(3).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	w=TSR->Get();

	if (!Label().FindChild(4).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	x=TSR->Get();

	if (!Label().FindChild(5).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	y=TSR->Get();

	if (!Label().FindChild(6).FindAttribute(TDataStd_Real::GetID(), TSR )) return 1;
	z=TSR->Get();

	// Build a box using the dimension and position attributes 
	BRepPrimAPI_MakeBox mkBox( gp_Pnt(x, y ,z), l, h ,w);
	TopoDS_Shape ResultShape = mkBox.Shape();


	// Build a TNaming_NamedShape using built box
	TNaming_Builder B(Label());
	B.Generated(ResultShape);
// That's all:
  // If there are no any mistakes we return 0:
  return 0;
}

TOcafFunction_BoxDriver::~TOcafFunction_BoxDriver() {}
 


Standard_EXPORT Handle_Standard_Type& TOcafFunction_BoxDriver_Type_()
{

    static Handle_Standard_Type aType1 = STANDARD_TYPE(TFunction_Driver);
  if ( aType1.IsNull()) aType1 = STANDARD_TYPE(TFunction_Driver);
  static Handle_Standard_Type aType2 = STANDARD_TYPE(MMgt_TShared);
  if ( aType2.IsNull()) aType2 = STANDARD_TYPE(MMgt_TShared);
  static Handle_Standard_Type aType3 = STANDARD_TYPE(Standard_Transient);
  if ( aType3.IsNull()) aType3 = STANDARD_TYPE(Standard_Transient);
 

  static Handle_Standard_Transient _Ancestors[]= {aType1,aType2,aType3,NULL};
  static Handle_Standard_Type _aType = new Standard_Type("TOcafFunction_BoxDriver",
			                                 sizeof(TOcafFunction_BoxDriver),
			                                 1,
			                                 (Standard_Address)_Ancestors,
			                                 (Standard_Address)NULL);

  return _aType;
}


// DownCast method
//   allow safe downcasting
//
const Handle(TOcafFunction_BoxDriver) Handle(TOcafFunction_BoxDriver)::DownCast(const Handle(Standard_Transient)& AnObject) 
{
  Handle(TOcafFunction_BoxDriver) _anOtherObject;

  if (!AnObject.IsNull()) {
     if (AnObject->IsKind(STANDARD_TYPE(TOcafFunction_BoxDriver))) {
       _anOtherObject = Handle(TOcafFunction_BoxDriver)((Handle(TOcafFunction_BoxDriver)&)AnObject);
     }
  }

  return _anOtherObject ;
}
const Handle(Standard_Type)& TOcafFunction_BoxDriver::DynamicType() const 
{ 
  return STANDARD_TYPE(TOcafFunction_BoxDriver) ; 
}
Standard_Boolean TOcafFunction_BoxDriver::IsKind(const Handle(Standard_Type)& AType) const 
{ 
  return (STANDARD_TYPE(TOcafFunction_BoxDriver) == AType || TFunction_Driver::IsKind(AType)); 
}

Handle_TOcafFunction_BoxDriver::~Handle_TOcafFunction_BoxDriver() {}


