// File:	WOKernel_Warehouse.cxx
// Created:	Wed Jul 26 18:18:27 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>

#include <Standard_ProgramError.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_HSequenceOfDBMSID.hxx>
#include <WOKernel_HSequenceOfStationID.hxx>

#include <WOKernel_Warehouse.ixx>

//=======================================================================
//function : WOKernel_Warehouse
//purpose  : instantiates a Warehouse
//=======================================================================
WOKernel_Warehouse::WOKernel_Warehouse(const Handle(TCollection_HAsciiString)& aname, 
				       const Handle(WOKernel_Factory)& anesting) 
  : WOKernel_Entity(aname, anesting) 
{
}

//=======================================================================
//function : EntityCode
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Warehouse::EntityCode() const 
{
  static Handle(TCollection_HAsciiString) acode = new TCollection_HAsciiString("warehouse");
  return acode;
}

//=======================================================================
//function : Open
//purpose  : opens the warehouse
//=======================================================================
void WOKernel_Warehouse::Open()
{
  if(IsOpened()) return;
  {
    Reset();

    Handle(TColStd_HSequenceOfHAsciiString) aseq;
    Handle(WOKernel_Parcel) aparcel;
    Standard_Integer i;

    GetParams();
    SetFileTypeBase(Session()->GetFileTypeBase(this));

    Handle(WOKernel_File) parcellist = new WOKernel_File(this, GetFileType("ParcelListFile"));

    parcellist->GetPath();

    WOKUtils_AdmFile afile(parcellist->Path());

    aseq = afile.Read();

    
    myparcels = new TColStd_HSequenceOfHAsciiString;

    for(i=1 ; i <= aseq->Length() ; i++)
      {
	aparcel = new WOKernel_Parcel( aseq->Value(i), Handle(WOKernel_Warehouse)(this));
	myparcels->Append(aparcel->FullName());
	Session()->AddEntity(aparcel);
      }

    SetOpened();
  }
  return;
}

//=======================================================================
//function : Close
//purpose  : closes Warehouse and Nested Entities
//=======================================================================
void WOKernel_Warehouse::Close()
{
  if(!IsOpened()) return;

  Standard_Integer i;
  Handle(WOKernel_Parcel) aparcel;

  for(i=1; i<=myparcels->Length(); i++)
    {
      aparcel = Session()->GetParcel(myparcels->Value(i));
      if (!aparcel.IsNull()) {
	aparcel->Close();
	Session()->RemoveEntity(aparcel);
      }
    }
  Reset();
  SetClosed();
  return;
}

//=======================================================================
//function : AddParcel
//purpose  : 
//=======================================================================
void WOKernel_Warehouse::AddParcel(const Handle(WOKernel_Parcel)& aparcel)
{
  if(Session()->IsKnownEntity(aparcel)) 
    {
      ErrorMsg() << "WOKernel_Warehouse::AddParcel" << "There is already a parcel called " << aparcel->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Warehouse::AddParcel");
    }



  myparcels->Append(aparcel->FullName());
  Session()->AddEntity(aparcel);
  
  DumpParcelList();
}

//=======================================================================
//function : RemoveParcel
//purpose  : 
//=======================================================================
void WOKernel_Warehouse::RemoveParcel(const Handle(WOKernel_Parcel)& aparcel)
{
  Standard_Integer i;
  
  for(i = 1 ; i <= myparcels->Length() ; i++)
    {
      if(myparcels->Value(i)->IsSameString(aparcel->FullName()))
	{myparcels->Remove(i);break;}
    }
  Session()->RemoveEntity(aparcel);

  
  DumpParcelList();
}

//=======================================================================
//function : Parcels
//purpose  : Gives the list of available parcels in warehouse
//=======================================================================
 Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Warehouse::Parcels() const 
{
  return myparcels;
}


//=======================================================================
//function : DumpParcelList
//purpose  : 
//=======================================================================
void WOKernel_Warehouse::DumpParcelList() const
{
  Handle(WOKernel_File) theparcellist = new WOKernel_File(this,GetFileType("ParcelListFile"));
  theparcellist->GetPath();
  Handle(WOKUtils_Path) theparcellistpath = theparcellist->Path();
  if (!theparcellistpath->IsWriteAble()) {
    ErrorMsg() << "WOKernel_Warehouse::DumpParcelList"
      << "Enable to modify file " << theparcellistpath->Name() << endm;
    Standard_ProgramError::Raise("WOKernel_Warehouse::DumpParcelList");
  }
  else {
    fstream theparcellistfile(theparcellistpath->Name()->ToCString(),ios::out);
    if ( theparcellistfile.good() ) {
      for (Standard_Integer i=1; i<= myparcels->Length(); i++) {
	Handle(WOKernel_Parcel) aparcel = Session()->GetParcel(myparcels->Value(i));
	theparcellistfile << aparcel->Name()->ToCString() << endl;
      }
    }
    else {
      ErrorMsg() << "WOKernel_Parcel::DumpParcelList"
	<< "Enable to access file " << theparcellistpath->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Warehouse::DumpParcelList");
    } 
  }
}


