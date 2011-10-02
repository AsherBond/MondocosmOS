// File:	WOKAPI_Session.cxx
// Created:	Tue Aug  1 15:43:42 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>
#include <Standard_Macro.hxx>

#include <OSD_Environment.hxx>
#include <OSD.hxx>

#include <TCollection_AsciiString.hxx>
#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_SearchList.hxx>
#include <WOKUtils_ParamItem.hxx>
#ifndef WNT

# include <WOKUtils_ProcessManager.hxx>
# include <WOKUtils_Signal.hxx>
#else
# include <WOKUtils_ShellManager.hxx>
# define WOKUtils_ProcessManager WOKUtils_ShellManager

#endif  // WNT

#include <WOKernel_StationID.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_BaseEntity.hxx>
#include <WOKernel_Entity.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_StationID.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_DBMSID.hxx>
#include <WOKernel_DataMapIteratorOfDataMapOfHAsciiStringOfFile.hxx>


#include <WOKMake_TriggerStep.hxx>


#include <WOKAPI_Factory.hxx>

#include <WOKAPI_Session.ixx>

static const Standard_CString WOK_CWE       = "%WOK_CWENTITY";
static const Standard_CString WOK_STATION   = "%WOK_STATION";
static const Standard_CString WOK_DBMS      = "%WOK_DBMS";
static const Standard_CString WOK_DEBUG     = "%WOK_DEBUG";


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKAPI_Session
//purpose  : instantiates a API SESSION
//=======================================================================
WOKAPI_Session::WOKAPI_Session()
{
  mypath   = new WOKUtils_Path;
}

//
//
// 
//  WOKAPI_Session MAIN Features
//
//
//

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Open
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Session::Open(const Handle(TCollection_HAsciiString)& alocation, const Handle(TCollection_HAsciiString)& astation) 
{
  OSD_Environment                  sessid("WOK_SESSIONID");
  OSD_Environment                  root("WOK_ROOTADMDIR");
  OSD_Environment                  libpath("WOK_LIBPATH");
  Handle(TCollection_HAsciiString) astr;
  Handle(WOKUtils_Path)            apath;
  Handle(WOKernel_Session)         asession;
  Handle(WOKernel_Factory)         afactory;
  Handle(WOKernel_Workshop)        aworkshop;
  Handle(WOKernel_Workbench)       aworkbench;
  Handle(WOKernel_DevUnit)         adevunit;
  Handle(TColStd_HSequenceOfAsciiString) aseq, dirseq;
  Standard_Integer i;
    
  Handle(TCollection_HAsciiString) sessionname = new TCollection_HAsciiString(sessid.Value());
    
  if(sessionname->IsEmpty() == Standard_True)
    {
      ErrorMsg() << "WOKAPI_Session::Open" << "Symbol WOK_SESSIONID is not setted" << endm;
      return 1;
    }
 Handle(TCollection_HAsciiString) rootname = new TCollection_HAsciiString(root.Value());
    
  if(rootname->IsEmpty() == Standard_True)
    {
      ErrorMsg() << "WOKAPI_Session::Open" << "Symbol WOK_ROOTADMDIR is not setted" << endm;
      return 1;
    }

  Handle(TCollection_HAsciiString) libpathname = new TCollection_HAsciiString(libpath.Value());
    
  if(libpathname->IsEmpty() == Standard_True)
    {
      ErrorMsg() << "WOKAPI_Session::Open" << "Symbol WOK_LIBPATH is not setted" << endm;
      return 1;
    }
   
  

  apath = new WOKUtils_Path(sessionname);
  if (!apath->CreateDirectory()) {
    ErrorMsg() << "WOKAPI_Session::Open" << "Unable to create directory " << sessionname->ToCString() << endm;
    return 1;
  }

  
  WOKernel_StationID astationID;

  if(!astation.IsNull())
    {
      astationID = WOKernel_Station::GetID(astation);
    }
  else
    {
      astationID = WOKernel_UnknownStation;
    }

  asession = new WOKernel_Session(new TCollection_HAsciiString("WOKSESSION"));
  asession->SetSession(asession);
  asession->AddEntity(asession);
  asession->SetStation(astationID);
  asession->Open(rootname,libpathname);

  myEntity = asession;
    
  aseq = Session()->Params().SearchDirectories();
  dirseq = new TColStd_HSequenceOfAsciiString;

  dirseq->Append(apath->Name()->ToCString());

  for(i=1; i<=aseq->Length(); i++)
    {
      dirseq->Append(aseq->Value(i));
    }
    
  Param().SetSearchDirectories(dirseq);

  apath = new WOKUtils_Path(apath->Name()->ToCString(), "WOK.edl");

  if (!apath->CreateFile()) {
    ErrorMsg() << "WOKAPI_Session::Open" << "Unable to create directory " << apath->Name()->ToCString() << endm;
    return 1;
  }
    
  SetPath(apath);

  //
  // Determination de DBMS
  //
    
  astr = Param().Eval(WOK_DBMS);

  if(!astr.IsNull())
    {
      Session()->SetDBMSystem(WOKernel_DBMSystem::GetID(astr));
    }
  else
    {
      Session()->SetDBMSystem(WOKernel_DFLT);
      Param().Set(WOK_DBMS,WOKernel_DBMSystem::GetName( WOKernel_DFLT )->ToCString());
    }

    
  //
  // Determination du debug mode
  //
    
  astr = Param().Eval(WOK_DEBUG);
    
  if(!astr.IsNull())
    {
      if(!strcmp(astr->ToCString(), "True"))
	{
	  Session()->SetDebugMode();
	}
      else
	if(!strcmp(astr->ToCString(), "False"))
	  {
	    Session()->UnsetDebugMode();
	  }
	else
	  {
	    ErrorMsg() << "WOKAPI_Session::Open" 
		     << "Wrong value : " << astr << " for session parameter : " << WOK_DEBUG << " (has to be : True|False" << endm;
	  }
    }
  else
    {
      Session()->UnsetDebugMode();
      Param().Set(WOK_DEBUG, "False");
    }
    
  //
  // Set de la station
  //
  Param().Set(WOK_STATION, WOKernel_Station::GetName(asession->Station())->ToCString());

  //
  // Ouverture de la CWE
  //


  if(mycwe.IsNull())
    {
      if(alocation.IsNull())
	{
	  astr = Param().Eval(WOK_CWE);
	  if(astr.IsNull())
	    {
	      astr = new TCollection_HAsciiString(":");
	    }
	  else if(astr->IsEmpty())
	    {
	      astr = new TCollection_HAsciiString(":");
	    }
	}
      else
	{
	  if(!alocation->IsEmpty())
	    {
	      astr = alocation;
	    }
	  else
	    {
	      astr = new TCollection_HAsciiString(":");
	    }
	}
      
      WOKAPI_Entity anent(*this, astr);
      
      if(anent.IsValid())
	{
	  mycwe = anent.UserPath();
	}
    }
  else
    {
      GetCWEntity();
    }

  // 
  //
  SaveToFile();
  //

  return 0; 
}

void WOKAPI_Session::Open(const WOKAPI_Session& aSession, 
			  const Handle(TCollection_HAsciiString)& apath) 
{
 WOKAPI_Entity::Open( aSession , apath ) ;
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKAPI_Session::Close()
{
  WOKAPI_Entity::Close();
  mypath.Nullify();
  myparams.Clear();
}

//=======================================================================
//function : GeneralFailure
//purpose  : 
//=======================================================================
void WOKAPI_Session::GeneralFailure(const Handle(Standard_Failure)& )
{
  
  static Standard_Boolean FailedInOpen;
  OSD::SetSignal();                  //==== ReArm the signals. =============
#ifndef WNT
  WOKUtils_Signal::Arm(WOKUtils_SIGINT,    (WOKUtils_SigHandler) NULL);
#endif  // WNT  
  try {
    OCC_CATCH_SIGNALS

    
    
    WOK_TRACE {
      VerboseMsg()("WOK_API") << "WOKAPI_Session::GeneralFailure" 
			    << "Killing processes" << endm;
    }
    WOKUtils_ProcessManager::KillAll();
    
    WOK_TRACE {
      VerboseMsg()("WOK_API") << "WOKAPI_Session::GeneralFailure" 
			    << "Reopen session" << endm;
    }
    Close();
    myEntity.Nullify();
    mypath.Nullify();
    myparams.Clear();
    mycwe.Nullify();
    WOKMake_TriggerStep::CurrentTriggerStep() = Handle(WOKMake_TriggerStep)();
    FailedInOpen = Standard_False;
    Open();
    
  }
  catch(Standard_Failure)  {
    Handle(Standard_Failure) E = Standard_Failure::Caught();
    ErrorMsg() << "WOKAPI_Session::GeneralFailure" 
	       << "Exception was raised : " << E->GetMessageString() << endm;
    ErrorMsg() << "WOKAPI_Session::GeneralFailure" 
	       << "Could not recover session after Failure : Session is reinitialized" << endm;
    
    WOKUtils_ProcessManager::UnArm();
    WOKMake_TriggerStep::CurrentTriggerStep() = Handle(WOKMake_TriggerStep)();
    if (!FailedInOpen) {
      myEntity.Nullify();
      mypath.Nullify();
      myparams.Clear();
      mycwe.Nullify();
      FailedInOpen = Standard_True;
      Open();
      FailedInOpen = Standard_False;
    }
    else {
      ErrorMsg() << "WOKAPI_Session::GeneralFailure" 
	<< "Session recovering Failed" << endm;
    }
  }
  return;
}

//=======================================================================
//function : IsValid
//purpose  :
//=======================================================================
Standard_Boolean WOKAPI_Session::IsValid() const
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Session));
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Path
//purpose  : returns path of session file
//=======================================================================
Handle(WOKUtils_Path) WOKAPI_Session::Path() const {return mypath;}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetPath
//purpose  : returns path of session file
//=======================================================================
void WOKAPI_Session::SetPath(const Handle(WOKUtils_Path)& apath)  {mypath=apath;}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Param
//purpose  : returns parameters of APISession
//=======================================================================
WOKUtils_Param WOKAPI_Session::Param() const {return myparams;}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SaveToFile
//purpose  : sauvegarde les parametres de Session dans le fichier
//=======================================================================
void WOKAPI_Session::SaveToFile()  const
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;

  if(myparams.IsSet(WOK_CWE))       aseq->Append(new TCollection_HAsciiString(WOK_CWE));
  if(myparams.IsSet(WOK_DBMS))      aseq->Append(new TCollection_HAsciiString(WOK_DBMS));
  if(myparams.IsSet(WOK_DEBUG))     aseq->Append(new TCollection_HAsciiString(WOK_DEBUG));

  if(!myparams.Write(mypath, aseq)) 
    {
      ErrorMsg() << "WOKAPI_Session::SaveToFile"
	<< "Could not save session parameters to file : " << mypath->Name() << endm;
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CWEntityName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Session::CWEntityName() const
{
  return mycwe;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetCWEntity
//purpose  : 
//=======================================================================
WOKAPI_Entity WOKAPI_Session::GetCWEntity() const
{
  
  if(!IsValid()) return WOKAPI_Entity();

  if(mycwe.IsNull()) return *this;
  
  if(!strcmp(mycwe->ToCString(), ":") || !strcmp(mycwe->ToCString(), "WOKSESSION:"))
    return *this;

  WOKAPI_Entity anentity;
  anentity.Set(GetEntity(mycwe));

  return anentity;
}

//=======================================================================
//function : SetCWEntity
//purpose  : 
//=======================================================================
void WOKAPI_Session::SetCWEntity(const WOKAPI_Entity& anent)
{
  if(anent.IsValid())
    {
      mycwe = anent.UserPath();
    }
  Param().Set(WOK_CWE, mycwe->ToCString());
  SaveToFile();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetEntity
//purpose  : 
//=======================================================================
Handle(WOKernel_Entity) WOKAPI_Session::GetEntity(const Handle(TCollection_HAsciiString)& apath, const Standard_Boolean fatal) const
{
  Handle(WOKernel_Entity) result;

  if(!IsValid()) return result;

  if(apath.IsNull()) return GetCWEntity().Entity();

  result = OpenPath(apath, !fatal);
  
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetFactory
//purpose  : 
//=======================================================================
Handle(WOKernel_Factory) WOKAPI_Session::GetFactory(const Handle(TCollection_HAsciiString)& apath,
						    const Standard_Boolean fatal,
						    const Standard_Boolean getit) const
{
  Handle(WOKernel_Factory) NULLRESULT, result;

  if(apath.IsNull())
    {
      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return NULLRESULT;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      if(getit)
	{
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Workbench))) Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Parcel)))    Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Warehouse))) Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Workshop)))  Kcwe = Session()->GetEntity(Kcwe->Nesting());
	}
      
      result = Handle(WOKernel_Factory)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetFactory"
		       << "Could not find any nesting factory to your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,!fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      if(getit)
	{
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))    anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Workbench)))  anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Parcel)))     anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Warehouse)))  anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Workshop)))   anentity = Session()->GetEntity(anentity->Nesting());
	}

      result = Handle(WOKernel_Factory)::DownCast(anentity);
      
      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetFactory"
		       << "Entity " << apath << " is not a factory" << endm;
	    }
	  return NULLRESULT;
	}
    }
}

//=======================================================================
//Function : GetWarehouse
//purpose  : 
//=======================================================================
Handle(WOKernel_Warehouse) WOKAPI_Session::GetWarehouse(const Handle(TCollection_HAsciiString)& apath, 
							const Standard_Boolean fatal,
							const Standard_Boolean getit) const
{
  Handle(WOKernel_Warehouse) NULLRESULT, result;

  if(apath.IsNull())
    {
      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return NULLRESULT;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      if(getit)
	{
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Parcel)))    Kcwe = Session()->GetEntity(Kcwe->Nesting());
	}
      
      result = Handle(WOKernel_Warehouse)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWarehouse"
		       << "Could not find any nesting warehouse to your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      if(getit)
	{
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Parcel)))    anentity = Session()->GetEntity(anentity->Nesting());
	}

      result = Handle(WOKernel_Warehouse)::DownCast(anentity);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWarehouse"
		       << "Entity " << apath << " is not a warehouse" << endm;
	    }
	  return NULLRESULT;
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetParcel
//purpose  : 
//=======================================================================
Handle(WOKernel_Parcel) WOKAPI_Session::GetParcel(const Handle(TCollection_HAsciiString)& apath, 
						  const Standard_Boolean fatal,
						  const Standard_Boolean getit) const
{
  Handle(WOKernel_Parcel) NULLRESULT, result;
  
  if(apath.IsNull())
    {
      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return NULLRESULT;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      if(getit)
	{
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_DevUnit))) Kcwe = Session()->GetEntity(Kcwe->Nesting());
	}

      result = Handle(WOKernel_Parcel)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetParcel"
		       << "Could not find any parcel from your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,!fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      if(getit)
	{
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   anentity = Session()->GetEntity(anentity->Nesting());
	}

      result = Handle(WOKernel_Parcel)::DownCast(anentity);

      if(!result.IsNull()) return result; 
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetParcel"
		       << "Entity " << apath << " is not a parcel" << endm;
	    }
	  return NULLRESULT;
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetWorkshop
//purpose  : 
//=======================================================================
Handle(WOKernel_Workshop) WOKAPI_Session::GetWorkshop(const Handle(TCollection_HAsciiString)& apath, 
					    const Standard_Boolean fatal,
					    const Standard_Boolean getit) const
{
  Handle(WOKernel_Workshop) NULLRESULT, result;

  if(apath.IsNull())
    {
      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return result;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      if(getit)
	{
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   Kcwe = Session()->GetEntity(Kcwe->Nesting());
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_Workbench))) Kcwe = Session()->GetEntity(Kcwe->Nesting());
	}

      result = Handle(WOKernel_Workshop)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWorkshop"
		       << "Could not find any nesting workshop to your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,!fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      if(getit)
	{
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))    anentity = Session()->GetEntity(anentity->Nesting());
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_Workbench)))  anentity = Session()->GetEntity(anentity->Nesting());
	}

      result = Handle(WOKernel_Workshop)::DownCast(anentity);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWorkshop"
		       << "Entity " << apath << " is not a workshop" << endm;
	    }
	  return NULLRESULT;
	}
    }
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetWorkbench
//purpose  : 
//=======================================================================
Handle(WOKernel_Workbench) WOKAPI_Session::GetWorkbench(const Handle(TCollection_HAsciiString)& apath, 
					      const Standard_Boolean fatal,
					      const Standard_Boolean getit) const
{
  Handle(WOKernel_Workbench) NULLRESULT, result;

  if(apath.IsNull())
    {
      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return result;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      if(getit)
	{
	  if(Kcwe->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))   Kcwe = Session()->GetEntity(Kcwe->Nesting());
	}
      
      result = Handle(WOKernel_Workbench)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWorkbench"
		       << "Could not find any workbench from your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,!fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      if(getit)
	{
	  if(anentity->IsKind(STANDARD_TYPE(WOKernel_DevUnit)))    anentity = Session()->GetEntity(anentity->Nesting());
	}
      
      result = Handle(WOKernel_Workbench)::DownCast(anentity);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetWorkbench"
		       << "Entity " << apath << " is not a workbench" << endm;
	    }
	  return NULLRESULT;
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetDevUnit
//purpose  : 
//=======================================================================
Handle(WOKernel_DevUnit) WOKAPI_Session::GetDevUnit(const Handle(TCollection_HAsciiString)& apath, 
				       const Standard_Boolean fatal,
//				       const Standard_Boolean getit) const 
				       const Standard_Boolean ) const 
{
  Handle(WOKernel_DevUnit) NULLRESULT, result;

  if(apath.IsNull())
    {

      WOKAPI_Entity cwe = GetCWEntity();

      if(!cwe.IsValid()) return result;

      Handle(WOKernel_Entity) Kcwe = cwe.Entity();

      result = Handle(WOKernel_DevUnit)::DownCast(Kcwe);

      if(!result.IsNull()) return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetDevUnit"
		       << "Could not find any Dev Unit from your current position : " << cwe.Entity()->UserPathName() << endm;
	    }
	  return NULLRESULT;
	}
      
    }
  else
    {
      Handle(WOKernel_Entity) anentity = OpenPath(apath,!fatal);
  
      if(anentity.IsNull()) return NULLRESULT;

      result = Handle(WOKernel_DevUnit)::DownCast(anentity);

      if(!result.IsNull())  return result;
      else
	{
	  if(fatal)
	    {
	      ErrorMsg() << "WOKAPI_Session::GetDevUnit"
		       << "Entity " << apath << " is not a devunit" << endm;
	    }
	  return NULLRESULT;
	}
    }
}

//=======================================================================
//function : Factories
//purpose  : 
//=======================================================================
void WOKAPI_Session::Factories( WOKAPI_SequenceOfFactory& factseq) const
{
  factseq.Clear();

  if(!IsValid()) return;

  if(!myEntity->IsOpened()) 
    {
      ErrorMsg() << "WOKAPI_Session::Factories" 
	       << "Internal Error : Session is not opened" << endm;
      return;
    }

  Handle(WOKernel_Session) Ksession = Handle(WOKernel_Session)::DownCast(myEntity);
  Handle(WOKernel_Factory) Kfact;
  Handle(TColStd_HSequenceOfHAsciiString) fseq;
  Standard_Integer i;

  fseq = Ksession->Factories();

  for(i=1; i<=fseq->Length(); i++)
    {
      Kfact = Ksession->GetFactory(fseq->Value(i));

      if(!Kfact.IsNull())
	{
	  WOKAPI_Factory factory;
	  factory.Set(Kfact);

	  factseq.Append(factory);
	}
    }
}

//=======================================================================
//function : Station
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Session::Station() const
{
  Handle(TCollection_HAsciiString) result;

  if(!IsValid()) return result;

  result = WOKernel_Station::GetName(Session()->Station());
  return result;
}

//=======================================================================
//function : SetStation
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Session::SetStation(const Handle(TCollection_HAsciiString)& astation)
{
  if(!IsValid()) return Standard_False;

  if(!WOKernel_Station::IsNameKnown(astation))
    {
      ErrorMsg() << "WOKAPI_Session::SetStation" 
	       << astation << " is not known as a " << endm;
      ErrorMsg() << "WOKAPI_Session::SetStation" 
	       << "Station is unchanged" << endm;
      return Standard_True;
    }

  WOKernel_StationID anid = WOKernel_Station::GetID(astation);
  Session()->SetStation(anid);
  Param().Set(WOK_STATION, WOKernel_Station::GetName(Session()->Station())->ToCString());
  SaveToFile();
  return Standard_False;
}

//=======================================================================
//function : DBMSystem
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Session::DBMSystem() const
{
  Handle(TCollection_HAsciiString) result;

  if(!IsValid()) return result;

  result = WOKernel_DBMSystem::GetName(Session()->DBMSystem());
  return result;
}

//=======================================================================
//function : SetDBMSystem
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Session::SetDBMSystem(const Handle(TCollection_HAsciiString)& adbms) 
{
  if(!IsValid()) return Standard_True; 

  if(!WOKernel_DBMSystem::IsNameKnown(adbms))
    {
      ErrorMsg() << "WOKAPI_Session::SetDBMSystem" 
	       << adbms << " is not known as a DBMSystem" << endm;
      ErrorMsg() << "WOKAPI_Session::SetDBMSystem" 
	       << "DBMS is unchanged" << endm;
      return Standard_True;
    }

  WOKernel_DBMSID anid = WOKernel_DBMSystem::GetID(adbms);
  Session()->SetDBMSystem(anid);
  
  Param().Set(WOK_DBMS, WOKernel_DBMSystem::GetName(Session()->DBMSystem())->ToCString());
  SaveToFile();
  return Standard_False;
}

//=======================================================================
//function : DebugMode
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Session::DebugMode() const
{
  if(!IsValid()) return Standard_False;

  return Session()->DebugMode();
}

//=======================================================================
//function : SetDebugMode
//purpose  : 
//=======================================================================
void WOKAPI_Session::SetDebugMode(const Standard_Boolean amode) 
{
  if(!IsValid()) return;

  if(amode)
    {
      Session()->SetDebugMode();
      Param().Set(WOK_DEBUG, "True");
    }
  else
    {
      Session()->UnsetDebugMode();
      Param().Set(WOK_DEBUG, "False");
    }
  SaveToFile();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OpenPath
//purpose  : 
//=======================================================================
Handle(WOKernel_Entity) WOKAPI_Session::OpenPath(const Handle(TCollection_HAsciiString)& apath, 
						 const Standard_Boolean besilent) const
{
  Handle(WOKernel_BaseEntity) abaseentity;
  Handle(WOKernel_Entity)     anentity;
  Standard_Integer pos,i;

  if(apath.IsNull()) 
    {
      ErrorMsg() << "WOKAPI_Session::OpenPath" << "Invalid NULL input" << endm;
      return anentity;
    }

  if (apath->IsEmpty()) apath->AssignCat(":");

  if(!strcmp(apath->ToCString(), ":") || !strcmp(apath->ToCString(), "WOKSESSION:") ) return myEntity;

  if(apath->Value(1) == ':')
    {
      // le path commence par : --> Il y a de grandes chances pour 
      // que celui-ci soit un full path connu: on tente le getEntity direct
      anentity = Session()->GetEntity(apath);
      if(!anentity.IsNull()) 
	{
	  anentity->Open();
	  return anentity;
	}
    }

  Handle(TColStd_HSequenceOfHAsciiString) aseq = Session()->GetMatchingEntities(apath);
  
  switch(aseq->Length())
    {
    case 1:
      abaseentity = Session()->GetEntity(aseq->Value(1));
      
      if(abaseentity->IsKind(STANDARD_TYPE(WOKernel_Entity)))
	{
	  anentity = Handle(WOKernel_Entity)::DownCast(abaseentity);
	  anentity->Open();
	}
      else
	{
	  if(!besilent)
	    {
	      ErrorMsg() << "WOKAPI_Session::OpenPath" << "You cannot move in " << apath << endm;
	    }
	}
      return anentity;


    case 0:
      {
      Handle(TCollection_HAsciiString) thepath = new TCollection_HAsciiString(":");
      Standard_Boolean end = Standard_False;
      i=1;

      pos = apath->Location(i,':',1,apath->Length());
      if (pos != 1) end = Standard_True;   

      while(!end)
	{
	  // if a full path is given -> try to Open it
	  if (pos == 0 || pos == apath->Length())
	    {
	      end = Standard_True;
	      if (pos ==0)
		thepath->AssignCat(apath->SubString(thepath->Length()+1,apath->Length()));
	      else
		if(apath->Length() > thepath->Length()+2)
		  thepath->AssignCat(apath->SubString(thepath->Length()+1,apath->Length()-1));

	      Handle(TColStd_HSequenceOfHAsciiString) anotherseq = Session()->GetMatchingEntities(thepath);
	      if(anotherseq->Length())
		anentity = OpenPath(thepath,besilent);
	      if(anentity.IsNull() || anotherseq->Length()==0)
		{
		  if(!besilent)
		    {
		      ErrorMsg() << "WOKAPI_Session::OpenPath" 
			       << "No entity is matching path: " << thepath << endm;
		    }
		  return Handle(WOKernel_Entity)();
		} 
	      return anentity;
	    }
	  else
	    {
	      if (pos > thepath->Length()+2)
		thepath->AssignCat(apath->SubString(thepath->Length()+1,pos-1));

	      //open the nesting
	      anentity = OpenPath(thepath,besilent);
	      if(anentity.IsNull())
		{
		  if(!besilent)
		    {
		      ErrorMsg() << "WOKAPI_Session::OpenPath" 
			       << "No entity is matching path: " << thepath << endm;
		    }
		  return Handle(WOKernel_Entity)();
		}
	    }
	  i++;
	  pos = apath->Location(i,':',1,apath->Length());
	}

      // else a partial path is given -> try ...

      // root
      thepath = new TCollection_HAsciiString(":");
      thepath->AssignCat(apath);

      anentity = OpenPath(thepath,besilent);
      if(anentity.IsNull() == Standard_False)
	return anentity;

      Handle(WOKernel_Entity) theentity = GetCWEntity().Entity();
      if(theentity.IsNull()) return Handle(WOKernel_Entity)();
      
      // N'y suis-je pas deja ??
      thepath = new TCollection_HAsciiString(theentity->Name());
      thepath->AssignCat(":");

      if(thepath->IsSameString(apath))
	  return theentity;
	  
      //N'y suis-je pas juste au dessus ??
      thepath = new TCollection_HAsciiString(theentity->FullName());
      if (strcmp(thepath->ToCString(), ":")) thepath->AssignCat(":");
      thepath->AssignCat(apath);

      anentity = OpenPath(thepath,besilent);
      if(anentity.IsNull() == Standard_False)
	  return anentity;

      //N'y suis-je pas juste a cote ??
      if(theentity->Nesting().IsNull() == Standard_False)
	{
	  thepath = new TCollection_HAsciiString(Session()->GetEntity(theentity->Nesting())->FullName());
	  if (strcmp(thepath->ToCString(), ":")) thepath->AssignCat(":");
	  thepath->AssignCat(apath);

	  anentity = OpenPath(thepath,besilent);
	  if(anentity.IsNull() == Standard_False)
	    return anentity;
	}

      //N'y suis-je pas en dessous ??		
      Handle(TCollection_HAsciiString) astring = new TCollection_HAsciiString(theentity->FullName());
      end = Standard_False;
      i=1;
      
      while(!end)
	{
	  pos = astring->Location(i,':',1,astring->Length());
	  
	  if (pos == 0)
	    end = Standard_True;
	  else
	    {
	      if (pos == 1) 
		thepath = new TCollection_HAsciiString(":");
	      else
		{
		  thepath = astring->SubString(1,pos-1);
		  thepath->AssignCat(":");
		}
	      thepath->AssignCat(apath);

	      anentity = OpenPath(thepath,besilent);
	      if(anentity.IsNull() == Standard_False)
		return anentity;
	    }
	  i++;
	}
      
      if(anentity.IsNull())
	{
	  Standard_Boolean fullpath = Standard_False;

	  if(apath->Value(apath->Length()) == ':') 
	    thepath = apath->SubString(1,apath->Length()-1);
	  else
	    thepath = apath;

	  Handle(TColStd_HSequenceOfHAsciiString) apartialseq = Session()->GetMatchingEntities(thepath,fullpath);
	  if(apartialseq->Length())
	    {
	      if(!besilent)
		{
		  ErrorMsg() << "WOKAPI_Session::OpenPath" 
		           << "No entity is matching your path be more precise in: " << endm;
		  for(i=1; i<=apartialseq->Length(); i++)
		    {
		      anentity = Session()->GetEntity(apartialseq->Value(i));
		      if(!anentity.IsNull())
			{
			  ErrorMsg() << "WOKAPI_Session::OpenPath" << "\t" << anentity->UserPathName() << endm;
			}
		    }
		}
	      return Handle(WOKernel_Entity)();
	    }
	  else
	    {
	      if(!besilent)
		{
		  ErrorMsg() << "WOKAPI_Session::OpenPath" 
		           << "No entity is matching path: " << apath << endm;
		}
	      return Handle(WOKernel_Entity)();
	    }
	}
      break;
    }

    default:
      if(!besilent)
	{
	  ErrorMsg() << "WOKAPI_Session::OpenPath" << "Abnormal Mulitple choice in Nesting : " << endm;
	  for(Standard_Integer i=1; i<=aseq->Length(); i++)
	    {
	      ErrorMsg() << "WOKAPI_Session::OpenPath" << "\t\t" << aseq->Value(i) << endm;
	    }
	}
      return Handle(WOKernel_Entity)();

    }

  return anentity;

}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsValidPath
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Session::IsValidPath(const Handle(TCollection_HAsciiString)& apath) const
{
  if(!IsValid()) return Standard_False;

  if(apath.IsNull()) 
    {
      return IsValidPath(GetCWEntity().UserPath());
    }

  Handle(WOKernel_Entity) entity = OpenPath(apath,Standard_True);

  if(entity.IsNull())
    return Standard_False;
  else
    return Standard_True;
}


//=======================================================================
//function : Destroy
//purpose  : bouchon
//=======================================================================
Standard_Boolean WOKAPI_Session::Destroy()  
{
  return Standard_False;
}
