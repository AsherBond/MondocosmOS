// File:	WOKernel_UnitNesting.cxx
// Created:	Wed Jul 26 18:41:59 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_HSequenceOfDBMSID.hxx>
#include <WOKernel_HSequenceOfStationID.hxx>
#include <WOKernel_DataMapIteratorOfDataMapOfFileType.hxx>

#include <WOKernel_UnitNesting.ixx>

//=======================================================================
//function : WOKernel_UnitNesting
//purpose  : Intialize UN
//=======================================================================
WOKernel_UnitNesting::WOKernel_UnitNesting(const Handle(TCollection_HAsciiString)& aname, 
					   const Handle(WOKernel_Entity)& anesting) 
  : WOKernel_Entity(aname, anesting)
{
}


//=======================================================================
//function : Open
//purpose  : 
//=======================================================================
void WOKernel_UnitNesting::Open()
{
  if(IsOpened()) return;
  {
    Handle(TColStd_HSequenceOfHAsciiString)   aseq;
    Handle(TCollection_HAsciiString)          astr;

    // UnitTypeBase  
    if(mytypebase.LoadBase(Params())) return;

    SetFileTypeBase(Session()->GetFileTypeBase(this));

    // chargement de la liste des uds
    myunits = GetUnitList();
    
    SetOpened();
  }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetDevUnit
//purpose  : 
//=======================================================================
Handle(WOKernel_DevUnit) WOKernel_UnitNesting::GetDevUnit(const Standard_Character akey, 
							  const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_UnitTypeDescr) atype = mytypebase.GetTypeDescr(akey);
  Handle(WOKernel_DevUnit) result;

  if(atype.IsNull())
    {
      ErrorMsg() << "WOKernel_UnitNesting::GetDevUnit"
	       << "Could not find appropriate unit type for key : " << akey << endm;
      return result;
    }
  
  if(aname.IsNull())
    {
      ErrorMsg() << "WOKernel_UnitNesting::GetDevUnit"
	       << "Invalid unit name (null)" << endm;
      return result;
    }
  
  result = new WOKernel_DevUnit(atype, aname, this);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetDevUnit
//purpose  : 
//=======================================================================
Handle(WOKernel_DevUnit) WOKernel_UnitNesting::GetDevUnit(const Handle(TCollection_HAsciiString)& atype, 
							  const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(WOKernel_UnitTypeDescr) thetype = mytypebase.GetTypeDescr(atype);
  Handle(WOKernel_DevUnit) result;

  if(thetype.IsNull())
    {
      ErrorMsg() << "WOKernel_UnitNesting::GetDevUnit"
	       << "Could not find appropriate unit type for type : " << atype << endm;
      return result;
    }
  
  if(aname.IsNull())
    {
      ErrorMsg() << "WOKernel_UnitNesting::GetDevUnit"
	       << "Invalid unit name (null)" << endm;
      return result;
    }
  
  result = new WOKernel_DevUnit(thetype, aname, this);
  return result;
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKernel_UnitNesting::Close()
{
  if(!IsOpened()) return;

  Handle(WOKernel_DevUnit) aunit;
  Standard_Integer i;

  for(i=1; i<=myunits->Length(); i++)
    {
      aunit = Session()->GetDevUnit(myunits->Value(i));
      aunit->Close();
      Session()->RemoveEntity(aunit);
    }
  myunits.Nullify();
  mytypebase.Clear();
  Reset();
  SetClosed();
}

//=======================================================================
//function : Units
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_UnitNesting::Units() const 
{
  return myunits;
}

//=======================================================================
//function : DumpUnitList
//purpose  : 
//=======================================================================
void WOKernel_UnitNesting::DumpUnitList() const
{
  Standard_Integer i;
  Handle(WOKernel_File) afile;

  afile = GetUnitListFile();
  afile->GetPath();

  ofstream astream(afile->Path()->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKernel_UnitNesting::AddUnit" << "Could not open " << afile->Path()->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_Workshop::AddWorkbench");
    }
  
  for(i = 1 ; i <= myunits->Length() ; i++)
    {
      Handle(WOKernel_DevUnit) aunit = Session()->GetDevUnit(myunits->Value(i));
      astream << aunit->TypeCode() << " " << aunit->Name()->ToCString() << endl;
    }
  return;
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : KnownTypes
//purpose  : 
//=======================================================================
const WOKernel_UnitTypeBase& WOKernel_UnitNesting::KnownTypes() const
{
  return mytypebase;
}

//=======================================================================
//function : AddUnit
//purpose  : 
//=======================================================================
void WOKernel_UnitNesting::AddUnit(const Handle(WOKernel_DevUnit)& aunit)
{
  if(Session()->IsKnownEntity(aunit)) 
    {
      ErrorMsg() << "WOKernel_UnitNesting::AddUnit" << "There is already a unit called " << aunit->Name() << endm;
      Standard_ProgramError::Raise("WOKernel_UnitNesting::AddUnit");
    }

  myunits->Append(aunit->FullName());
  Session()->AddEntity(aunit);
  
  DumpUnitList();
  return;
}



//=======================================================================
//function : RemoveUnit
//purpose  : removes a unit in the list of units (i.e. updates UDLIST)
//=======================================================================
void WOKernel_UnitNesting::RemoveUnit(const Handle(WOKernel_DevUnit)& aunit)
{
  Standard_Integer i;
  
  for(i = 1 ; i <= myunits->Length() ; i++)
    {
      if(myunits->Value(i)->IsSameString(aunit->FullName()))
	{myunits->Remove(i);break;}
    }
  Session()->RemoveEntity(aunit);
  DumpUnitList();
  return;
}


