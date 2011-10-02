// File:	WOKAPI_Unit.cxx
// Created:	Mon Aug 21 22:07:27 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <OSD_Host.hxx>

#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_Return.hxx>
#include <WOKTools_Options.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ShellManager.hxx>

#include <WOKernel_Station.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_SequenceOfSession.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_File.hxx>
#include <WOKAPI_Workbench.hxx>

#include <WOKAPI_Unit.ixx>

//=======================================================================
//function : WOKAPI_Unit
//purpose  : 
//=======================================================================
WOKAPI_Unit::WOKAPI_Unit()
{
}

//=======================================================================
//function : WOKAPI_Unit
//purpose  : 
//=======================================================================
WOKAPI_Unit::WOKAPI_Unit(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}

//=======================================================================
//function : WOKAPI_Unit
//purpose  : 
//=======================================================================
WOKAPI_Unit::WOKAPI_Unit(const WOKAPI_Session& asession, 
			 const Handle(TCollection_HAsciiString)& apath,
			 const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetDevUnit(apath,fatal,getit));
}

//=======================================================================
//function : TypeKey
//purpose  : 
//=======================================================================
Standard_Character WOKAPI_Unit::TypeKey() const
{
  if(!IsValid()) return 0;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_DevUnit) aunit = Handle(WOKernel_DevUnit)::DownCast(myEntity);

  return aunit->TypeCode();
}

//=======================================================================
//function : Type
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Unit::Type() const
{
  if(!IsValid()) return 0;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_DevUnit) aunit = Handle(WOKernel_DevUnit)::DownCast(myEntity);

  return aunit->Type();
}


//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Unit::BuildParameters(const WOKAPI_Session& asession, 
								   const Handle(TCollection_HAsciiString)& apath, 
								   const Standard_Character acode,
								   const Handle(WOKTools_HSequenceOfDefine)& defines, 
								   const Standard_Boolean usedefaults)
{
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_DevUnit)         Kunit;
  Handle(WOKernel_Workbench)       Kbench;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Workbench abench(asession,nestname);

  if(!abench.IsValid())
    {
      ErrorMsg() << "WOKAPI_Unit::Build"
	       << "Invalid nesting (" << nestname << ") to create workbench : " << name << endm;
      return aseq;
    }

  Kbench =  Handle(WOKernel_Workbench)::DownCast(abench.Entity());

  Kunit = Kbench->GetDevUnit(acode, name);

  Set(Kunit);

  aseq = GetBuildParameters(asession, name, abench, defines, usedefaults);
  
  return aseq;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Unit::Build(const WOKAPI_Session& asession,
				    const Handle(TCollection_HAsciiString)& apath, 
				    const Standard_Character acode,
				    const Handle(WOKTools_HSequenceOfDefine)& defines, 
				    const Standard_Boolean usedefaults)
{
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_DevUnit)         Kunit;
  Handle(WOKernel_Workbench)       Kbench;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Workbench abench(asession,nestname);

  if(!abench.IsValid())
    {
      ErrorMsg() << "WOKAPI_Unit::Build"
	       << "Invalid nesting (" << nestname << ") to create unit : " << name << endm;
      return Standard_True;
    }

  Kbench =  Handle(WOKernel_Workbench)::DownCast(abench.Entity());

  Kunit = Kbench->GetDevUnit(acode, name);
  
  if(Kunit.IsNull())
    {
      ErrorMsg() << "WOKAPI_Unit::Build"
	       << "Could not obtain unit : wrong type code : " << acode << endm;
      return Standard_True;
    }

  Set(Kunit);

  UpdateBeforeBuild(Kbench);

  if(!BuildEntity(asession, name, abench, defines, usedefaults))
    {
      Kbench->AddUnit(Kunit);
      Kunit->Open();
    }
  else return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Unit::Destroy()
{
  if(!IsValid()) return Standard_True;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_UnitNesting)  aunitnesting = myEntity->Session()->GetUnitNesting(myEntity->Nesting());

  UpdateBeforeDestroy(aunitnesting);
  if (!IsValid()) return Standard_True;

  Handle(WOKernel_DevUnit) aDevUnit = Handle(WOKernel_DevUnit)::DownCast(myEntity);
  aDevUnit->Open();
  aDevUnit->Destroy();
  aunitnesting->RemoveUnit(aDevUnit);

  myEntity.Nullify();
  return Standard_False;
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Unit::IsValid() const 
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)); 
}

//=======================================================================
//function : Files
//purpose  : 
//=======================================================================
void WOKAPI_Unit::Files(const WOKAPI_Locator& alocator, WOKAPI_SequenceOfFile& fileseq) const 
{
  Standard_Integer i;

  fileseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_DevUnit)     aunit    = Handle(WOKernel_DevUnit)::DownCast(myEntity);
  Handle(WOKernel_UnitNesting) anesting = aunit->Session()->GetUnitNesting(aunit->Nesting());

  Handle(TColStd_HSequenceOfHAsciiString) filelist;
  Handle(TCollection_HAsciiString) astr;

  if(aunit->FileList().IsNull())
    {
      aunit->ReadFileList(alocator.Locator());
    }

  filelist = aunit->FileList();
      
  Handle(WOKernel_File) afile;
  Handle(TCollection_HAsciiString) aname;
  Handle(TCollection_HAsciiString) atype;
  Handle(TCollection_HAsciiString) aunitname;
  WOKAPI_File apifile;

  for(i=1; i<=filelist->Length();i++)
    {
      astr = filelist->Value(i);
      aunitname = astr->Token(":", 1);
      atype     = astr->Token(":", 2);
      aname     = astr->Token(":", 3);

      afile = new WOKernel_File(aname, 
				aunit,
				aunit->GetFileType(atype));
      apifile.Set(afile);
      fileseq.Append(apifile);
    }
  return;
}
