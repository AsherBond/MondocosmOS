// File:	WOKAPI_Entity.cxx
// Created:	Mon Apr  1 17:26:57 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <OSD_Environment.hxx>

#include <TColStd_HSequenceOfAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_InterpFileValue.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_PathIterator.hxx>

#include <WOKernel_Entity.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_FileTypeIterator.hxx>
#include <WOKernel_HSequenceOfDBMSID.hxx>
#include <WOKernel_HSequenceOfStationID.hxx>

#include <WOKAPI_Factory.hxx>
#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_Warehouse.hxx>
#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_SequenceOfParcel.hxx>
#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Unit.hxx>

#include <WOKAPI_Session.hxx>

#include <WOKAPI_Entity.ixx>

#ifdef WNT
static const Standard_CString PATH_SEPARATOR = ";";
#else
static const Standard_CString PATH_SEPARATOR = ":";
#endif

//=======================================================================
//function : WOKAPI_Entity
//purpose  : 
//=======================================================================
 WOKAPI_Entity::WOKAPI_Entity()
{
}

//=======================================================================
//function : WOKAPI_Entity
//purpose  : 
//=======================================================================
 WOKAPI_Entity::WOKAPI_Entity(const WOKAPI_Session& asession,
			      const Handle(TCollection_HAsciiString)& apath,
			      const Standard_Boolean fatal, const Standard_Boolean )
{
  Set(asession.GetEntity(apath,fatal));
}

//=======================================================================
//function : WOKAPI_Entity
//purpose  : 
//=======================================================================
WOKAPI_Entity::WOKAPI_Entity(const WOKAPI_Entity& anent) 
{
  myEntity = anent.Entity();
  if(!IsValid()) {
    myEntity.Nullify();
  }
}

//=======================================================================
//function : Session
//purpose  : 
//=======================================================================
Handle(WOKernel_Session) WOKAPI_Entity::Session() const 
{
  if(myEntity.IsNull()) return Handle(WOKernel_Session)();
  return myEntity->Session();
}

//=======================================================================
//function : Set
//purpose  : 
//=======================================================================
void WOKAPI_Entity::Set(const Handle(WOKernel_Entity)& anent)
{
  myEntity = anent;
  //if(!myEntity.IsNull()) myEntity->Open();
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsValid() const 
{
  return !myEntity.IsNull();
}

//=======================================================================
//function : IsAccessible
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsAccessible() const
{
  if(!IsValid()) return Standard_False;

  return Standard_True;
}

//=======================================================================
//function : IsWriteAble
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsWriteAble() const
{
  if(!IsValid()) return Standard_False;
 return Standard_True;
}


//=======================================================================
//function : IsSession
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsSession() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Session));
  return Standard_False;
}

//=======================================================================
//function : IsFactory
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsFactory() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Factory));
  return Standard_False;
}

//=======================================================================
//function : IsWarehouse
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsWarehouse() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Warehouse));
  return Standard_False;
}

//=======================================================================
//function : IsParcel
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsParcel() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Parcel));
  return Standard_False;
}

//=======================================================================
//function : IsWorkshop
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsWorkshop() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Workshop));
  return Standard_False;
}

//=======================================================================
//function : IsWorkbench
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsWorkbench() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_Workbench));
  return Standard_False;
}

//=======================================================================
//function : IsUnit
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsUnit() const
{
  if(!myEntity.IsNull()) 
    return myEntity->IsKind(STANDARD_TYPE(WOKernel_DevUnit));
  return Standard_False;
}

//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  aseq.Clear();
  return Standard_True;
}

//=======================================================================
//function : NestingEntity
//purpose  : 
//=======================================================================
WOKAPI_Entity WOKAPI_Entity::NestingEntity() const
{
  WOKAPI_Entity result;

  if(!IsValid()) return result;

  Handle(WOKernel_Session) asession = myEntity->Session();

  if(!myEntity->Nesting().IsNull())
    {
      Handle(WOKernel_Entity) thenesting;
      thenesting = asession->GetEntity(myEntity->Nesting());

      result.Set(thenesting);
    }
  else
    result.Set(asession);

  return result;
}



//=======================================================================
//function : Code
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::Code() const 
{
  Handle(TCollection_HAsciiString) result;
  if(IsValid())
    {
      if(!myEntity->IsOpened()) myEntity->Open();
      result = myEntity->EntityCode();
    }
  return result;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::Name() const
{
  Handle(TCollection_HAsciiString) result;
  if(IsValid())
    {
      result = myEntity->Name();
    }
  return result;
}

//=======================================================================
//function : UserPath
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::UserPath() const
{
  Handle(TCollection_HAsciiString) result;
  if(IsValid())
    {
      result = myEntity->UserPathName();
    }
  return result;
}


//=======================================================================
//function : BuildName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::BuildName(const Handle(TCollection_HAsciiString)& apath) const
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) name;

  i = apath->SearchFromEnd(":");

  if(i != -1)
    {
      name  = apath->SubString(i+1, apath->Length());
    }
  else
    {
      name  =  apath;
    }
  return name;
}


//=======================================================================
//function : BuildNesting
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::BuildNesting(const Handle(TCollection_HAsciiString)& apath) const 
{
  Standard_Integer i;
  Handle(TCollection_HAsciiString) name;

  i = apath->SearchFromEnd(":");

  if(i != -1)
    {
      name  = apath->SubString(1, i-1);
    }
  return name;
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Entity::GetBuildParameters(const WOKAPI_Session& asession,
									const Handle(TCollection_HAsciiString)& name,
									const WOKAPI_Entity& anesting,
									const Handle(WOKTools_HSequenceOfDefine)& defines,
									const Standard_Boolean usedefaults) const 
{
  Handle(WOKUtils_HSequenceOfParamItem) someparams = new WOKUtils_HSequenceOfParamItem;
  Handle(WOKUtils_HSequenceOfParamItem) result;
  Handle(TCollection_HAsciiString) aprefix = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) astr;
  Standard_Integer i;

  if(!anesting.IsValid())
    {
      return result;
    }

  WOKAPI_Entity anent(asession, myEntity->FullName(),Standard_False);

  if(anent.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::BuildParameters" 
	       << "There is already an entity with name : " << myEntity->UserPathName() << endm;
      return result;
    }

  aprefix->AssignCat("%");
  aprefix->AssignCat(name);
  aprefix->AssignCat("_");

  for(i=1; i<= defines->Length() ; i++)
    {
      astr = new TCollection_HAsciiString(aprefix);
      astr->AssignCat(defines->Value(i).Name());
      someparams->Append(WOKUtils_ParamItem(astr, defines->Value(i).Value()));
    }

  result = myEntity->BuildParameters(someparams, usedefaults);

  return result;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::BuildEntity(const WOKAPI_Session& asession,
				      const Handle(TCollection_HAsciiString)& aname, 
				      const WOKAPI_Entity& anesting,
				      const Handle(WOKTools_HSequenceOfDefine)& defines, 
				      const Standard_Boolean usedefaults,
				      const Standard_Boolean checkhome)  
{
  Handle(WOKUtils_HSequenceOfParamItem) someparams;
  Standard_Integer i;
  Standard_Boolean failed = Standard_False;

  if(!anesting.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::BuildParameters" 
	       << "Invalid Nesting : " << anesting.Entity()->UserPathName() << endm;
      return Standard_True;
    }

  WOKAPI_Entity anent(asession, myEntity->FullName(),Standard_False);

  if(anent.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::BuildParameters" 
	       << "There is already an entity with name : " << myEntity->UserPathName() << endm;
      return Standard_True;
    }

  someparams = GetBuildParameters(asession, aname, anesting, defines, usedefaults);

  Handle(TCollection_HAsciiString) namehome = new TCollection_HAsciiString("%");
  namehome->AssignCat(aname);
  namehome->AssignCat("_Home");
  for(i=1; i<=someparams->Length(); i++)
    {
      const WOKUtils_ParamItem& item = someparams->Value(i);

      if(item.Value().IsNull())
	{
	  ErrorMsg() << "WOKAPI_Entity::Build"
		   << "Needed parameter : " << item.Name() << " is not setted" << endm;
	  failed = Standard_True;
	}
      
      if (checkhome) {
	if (namehome->IsSameString(item.Name())) {
	  Handle(WOKUtils_Path) apathname = new WOKUtils_Path(item.Value());
	  if (!apathname->FileName()->IsSameString(aname)) {
	    failed = Standard_True;
	    ErrorMsg() << "WOKAPI_Entity::Build"
	      << "Invalid home directory " << apathname->Name() << " for entity " << Name() << endm;
	  }
	}
      }
    }

  if (!myEntity->IsValidName()) {
    ErrorMsg() << "WOKAPI_Entity::Build"
             << "Invalid name for entity : " << Name() << endm;
    failed = Standard_True;
  }    
  if(failed) return Standard_True;

  myEntity->Build(someparams);
  return Standard_False;
}

//=======================================================================
//function : UpdateBeforeBuild
//purpose  : 
//=======================================================================
void WOKAPI_Entity::UpdateBeforeBuild(const Handle(WOKernel_Entity)& anesting) 
{
  if(anesting.IsNull()) return;

  anesting->Close();
  anesting->Open();
  return;
}

//=======================================================================
//function : UpdateBeforeDestroy
//purpose  : 
//=======================================================================
void WOKAPI_Entity::UpdateBeforeDestroy(const Handle(WOKernel_Entity)& anesting) 
{
  if(!IsValid()) return ;

  Handle(WOKernel_Session) asession = myEntity->Session();

  if (!anesting.IsNull())
    {  
      Handle(TCollection_HAsciiString) afullname = myEntity->FullName();
      Handle(TCollection_HAsciiString) ausername = myEntity->UserPathName();

      myEntity.Nullify();
      
      anesting->Close();
      anesting->Open();
      
      myEntity = asession->GetEntity(afullname);

      if(!IsValid()) 
	{
	  ErrorMsg() << "WOKAPI_Entity::UpdateEntityList"
	           << "Entity " << ausername << " no longer exists" << endm;
	  return;
	}
    }

  return;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::Destroy() 
{
  if(!IsValid()) return Standard_True;

  // mettre a jour la EntityList
  Handle(WOKernel_Entity)  anesting  = myEntity->Session()->GetEntity(myEntity->Nesting());

  UpdateBeforeDestroy(anesting);
  if(!IsValid())  return Standard_True;

  myEntity->Open();
  myEntity->Destroy();

  return Standard_False;
}

//=======================================================================
//function : Open
//purpose  : 
//=======================================================================
void WOKAPI_Entity::Open(const WOKAPI_Session& aSession, 
			 const Handle(TCollection_HAsciiString)& apath) 
{
  WOKAPI_Entity anent(aSession, apath);

  if(anent.IsValid())
    {
      myEntity = anent.Entity();
    }
  else
    if(IsValid()) myEntity->Open();
  return;
}

//=======================================================================
//function : Close
//purpose  : 
//=======================================================================
void WOKAPI_Entity::Close()  
{
  if(IsValid())
    {
      myEntity->Close();
    }
}

//=======================================================================
//function : IsParameterSet
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsParameterSet(const Handle(TCollection_HAsciiString)& aname) const 
{
  if(aname.IsNull()) return Standard_False;
  if(IsValid())
    return myEntity->Params().IsSet(aname->ToCString());
  return Standard_False;
}
//=======================================================================
//function : 
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::EntityParameterName(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(aname.IsNull() || !IsValid())  return result;
  if(!myEntity->IsOpened()) myEntity->Open();
  result = myEntity->ParameterName(aname->ToCString());
  return result;
}

//=======================================================================
//function : ParameterValue
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::ParameterValue(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(aname.IsNull() || !IsValid())  return result;
  if(!myEntity->IsOpened()) myEntity->Open();
  result = myEntity->Params().Value(aname->ToCString());
  return result;
}

//=======================================================================
//function : ParameterEval
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::ParameterEval(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(aname.IsNull() || !IsValid())  return result;
  if(!myEntity->IsOpened()) myEntity->Open();
  result = myEntity->Params().Eval(aname->ToCString());
  return result;
}

//=======================================================================
//function : ParameterArguments
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_Entity::ParameterArguments(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq, result = new TColStd_HSequenceOfHAsciiString;
  Standard_Integer i;
  
  if(aname.IsNull() || !IsValid())  return result;
  if(!myEntity->IsOpened()) myEntity->Open();
  aseq = myEntity->Params().GetArguments(aname->ToCString());
  
  for(i=1; i<=aseq->Length(); i++)
    {
      result->Append(aseq->Value(i));
    }
  return result;
}


//=======================================================================
//function : ParameterClassValues
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Entity::ParameterClassValues(const Handle(TCollection_HAsciiString)& aclass) const 
{
  Handle(WOKUtils_HSequenceOfParamItem) result = new WOKUtils_HSequenceOfParamItem;

  if(aclass.IsNull() || !IsValid())  return result;
  if(!myEntity->IsOpened()) myEntity->Open();

  const WOKUtils_Param& prm = myEntity->Params();
  
  prm.LoadParamClass(aclass->ToCString(),prm.SubClasses());
  
  result = prm.GetClassValues(aclass->ToCString());
  
  return result;
}

//=======================================================================
//function : ParameterSearchList
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_Entity::ParameterSearchList() const 
{
  Handle(TColStd_HSequenceOfHAsciiString) result;

  if(!IsValid()) return result;
  if(!myEntity->IsOpened()) myEntity->Open();

  WOKUtils_Param prm = myEntity->Params();

  Handle(TColStd_HSequenceOfAsciiString) alist = prm.SearchDirectories();
  Standard_Integer i;

  result = new TColStd_HSequenceOfHAsciiString;

  for(i=1; i<=alist->Length(); i++)
    {
      result->Append(new TCollection_HAsciiString(alist->Value(i)));
    }
  
  return result;
}


//=======================================================================
//function : ParameterClasses
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_Entity::ParameterClasses() const
{
  if(!IsValid()) return Handle(TColStd_HSequenceOfHAsciiString)();
  if(!myEntity->IsOpened()) myEntity->Open();
  const WOKUtils_Param& params = myEntity->Params();
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfAsciiString) Classes = params.SubClasses();
  
  if(!Classes.IsNull())
    {
      for(Standard_Integer i=1; i<=Classes->Length(); i++)
	{    
	  result->Append(new TCollection_HAsciiString(Classes->Value(i)));
	}
    }
  return result;
}

//=======================================================================
//function : ParameterClassFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_Entity::ParameterClassFiles(const Handle(TCollection_HAsciiString)& aclass) const 
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  if(!IsValid()) return Handle(TColStd_HSequenceOfHAsciiString)();
  if(!myEntity->IsOpened()) myEntity->Open();
  const WOKUtils_Param& params = myEntity->Params();
  
  Handle(WOKUtils_Path) apath;
  
  apath = params.VisiblePath(params.ClassFile(aclass->ToCString()));
  
  if(!apath.IsNull())
    {
      result->Append(apath->Name());
    }
  
  Handle(TColStd_HSequenceOfAsciiString) Classes = params.SubClasses();
  
  if(!Classes.IsNull())
    {
      for(Standard_Integer i=1; i<=Classes->Length(); i++)
	{
	  Handle(TCollection_HAsciiString) afile = params.ClassSubFile(aclass->ToCString(),Classes->Value(i).ToCString());
	  
	  apath = new WOKUtils_Path(afile);
	  if (apath->Exists())
	    {
	      result->Append(apath->Name());
	    }
	}
    }
  return result;
}

//=======================================================================
//function : FindParameterFile
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::FindParameterFile(const Handle(TCollection_HAsciiString)& afile) const
{
  Handle(TCollection_HAsciiString) NULLRESULT;

  if(afile.IsNull() || !IsValid()) return NULLRESULT;

  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKUtils_Path) apath = myEntity->Params().SearchFile(afile);
  if(!apath.IsNull()) return apath->Name();
  else                return NULLRESULT;
}

//=======================================================================
//function : ParameterSet
//purpose  : 
//=======================================================================
void WOKAPI_Entity::ParameterSet(const Handle(TCollection_HAsciiString)& aname, 
				 const Handle(TCollection_HAsciiString)& avalue) const 
{
  if(!IsValid() || aname.IsNull() || avalue.IsNull()) return;

  if(aname->Value(1) != '%') 
    {
      ErrorMsg() << "WOKAPI_Entity::ParameterSet" << "Variable name must begin with %" << endm;
      return;
    }
  if(!myEntity->IsOpened()) myEntity->Open();
  myEntity->Params().Set(aname->ToCString(), avalue->ToCString());
  return;
}

//=======================================================================
//function : ParameterUnSet
//purpose  : 
//=======================================================================
void WOKAPI_Entity::ParameterUnSet(const Handle(TCollection_HAsciiString)& aname) const 
{
  if(!IsValid() || aname.IsNull()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  if(aname->Value(1) != '%') 
    {
      ErrorMsg() << "WOKAPI_Entity::ParameterUnSet" << "Variable name must begin with %" << endm;
      return;
    }

  myEntity->Params().UnSet(aname->ToCString());
  return;
}

//=======================================================================
//function : ParameterReset
//purpose  : 
//=======================================================================
void WOKAPI_Entity::ParameterReset() const 
{
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  myEntity->ChangeParams() = WOKUtils_Param();
  myEntity->GetParams();
}

//=======================================================================
//function : FileTypes
//purpose  : 
//=======================================================================
void WOKAPI_Entity::FileTypes(TColStd_SequenceOfHAsciiString& typeseq) const
{
  typeseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_FileTypeBase) abase = myEntity->FileTypeBase();

  if(abase.IsNull()) return;
  
  WOKernel_FileTypeIterator theit = abase->TypeIterator();

  while(theit.More())
    {
      typeseq.Append(theit.Key());
      theit.Next();
    }
  return;
}

//=======================================================================
//function : GetFiles
//purpose  : 
//=======================================================================
void WOKAPI_Entity::GetFiles(TColStd_SequenceOfHAsciiString& fileseq) const
{
  fileseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_FileTypeBase) abase = myEntity->FileTypeBase();

  if(abase.IsNull()) return;

  Handle(WOKernel_Entity) nesting = myEntity->Session()->GetEntity(myEntity->Nesting());

  Handle(TColStd_HSequenceOfHAsciiString) Kfileseq = 
    abase->GetFiles(myEntity,
		    myEntity->DBMSystems(), myEntity->Stations(), Standard_True);
  
  if(!Kfileseq.IsNull())
    
  for(Standard_Integer i=1; i<=Kfileseq->Length(); i++)
    {
      fileseq.Append(Kfileseq->Value(i));
    }
								    
  return;
}

//=======================================================================
//function : GetDirs
//purpose  : 
//=======================================================================
void WOKAPI_Entity::GetDirs(TColStd_SequenceOfHAsciiString& dirseq) const
{
  dirseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_FileTypeBase) abase = myEntity->FileTypeBase();

  if(abase.IsNull()) return;

  Handle(WOKernel_Entity) nesting = myEntity->Session()->GetEntity(myEntity->Nesting());

  Handle(TColStd_HSequenceOfHAsciiString) Kdirseq = 
    abase->GetDirectories(myEntity, myEntity->DBMSystems(), myEntity->Stations(),Standard_True);

  myEntity->Close();  
  myEntity->Open();  

  if(!Kdirseq.IsNull())
    
  for(Standard_Integer i=1; i<=Kdirseq->Length(); i++)
    {
      dirseq.Append(Kdirseq->Value(i));
    }
								    
  return;
}
//=======================================================================
//function : CheckDirs
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::CheckDirs(const Standard_Boolean createifmissing, const Standard_Boolean besilent) const
{
  Standard_Boolean status = Standard_True;

  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_FileTypeBase) abase = myEntity->FileTypeBase();

  if(abase.IsNull()) return Standard_False;

  Handle(WOKernel_Entity) nesting = myEntity->Session()->GetEntity(myEntity->Nesting());

  Handle(WOKernel_HSequenceOfDBMSID) bd = new WOKernel_HSequenceOfDBMSID;
  bd->Append(myEntity->Session()->DBMSystem());

  Handle(WOKernel_HSequenceOfStationID) st = new WOKernel_HSequenceOfStationID;
  st->Append(myEntity->Session()->Station());
  

  Handle(TColStd_HSequenceOfHAsciiString) Kdirseq = 
    abase->GetDirectories(myEntity, bd, st,Standard_True);
  
  myEntity->Close();  
  myEntity->Open();  

  if(!Kdirseq.IsNull())    
    for(Standard_Integer i=1; i<=Kdirseq->Length(); i++)
      {
	Handle(WOKUtils_Path) apath = new WOKUtils_Path(Kdirseq->Value(i));
	
	if(!apath->IsDirectory()) 
	  {
	    if(!apath->Exists())
	      {
		if(!besilent)
		  {
		    if(createifmissing)
		      {
			WarningMsg() << "WOKAPI_Entity::CheckDirs"
			  << "Creating missing directory " << Kdirseq->Value(i) << " in " << UserPath() << endm;
		      }
		    else
		      {
			WarningMsg() << "WOKAPI_Entity::CheckDirs"
			  << "Missing directory " << Kdirseq->Value(i) << " in " << UserPath() << endm;
		      }
		  }
		if(createifmissing)
		  {
		    if(!apath->CreateDirectory(Standard_True))
		      status = Standard_False;
		  }
		else
		  status = Standard_False;
	      }
	    else
	      {
		ErrorMsg() << "WOKAPI_Entity::CheckDirs"
		  << Kdirseq->Value(i) << " exists and is not a directory" << endm;
		status = Standard_False;
	      }
	  }
      }
								    
  return status;
}

//=======================================================================
//function : IsFileType
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsFileType(const Handle(TCollection_HAsciiString)& aname) const 
{
  if(aname.IsNull() || !IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();
  return !myEntity->GetFileType(aname).IsNull();
}

//=======================================================================
//function : IsFileTypeFileDependent
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Entity::IsFileTypeFileDependent(const Handle(TCollection_HAsciiString)& aname) const 
{
  if(aname.IsNull()) return Standard_False;
  if(!IsValid())     return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();
  Handle(WOKernel_FileType) afiletype = myEntity->GetFileType(aname);

  if(afiletype.IsNull()) 
    return Standard_False;
  else
    return (afiletype->IsFileDependent());
}

//=======================================================================
//function : GetFileTypeDefinition
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::GetFileTypeDefinition(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(IsValid() && !aname.IsNull())
    {
      if(!myEntity->IsOpened()) myEntity->Open();
      Handle(WOKernel_FileType) atype = myEntity->GetFileType(aname);
      if(!atype.IsNull())
	{
	  result = atype->GetDefinition();
	}
    }
  return result;
}

//=======================================================================
//function : GetFileTypeDirectory
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::GetFileTypeDirectory(const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(IsValid() && !aname.IsNull())
    {
      if(!myEntity->IsOpened()) myEntity->Open();
      if(IsFileType(aname))
	{
	  Handle(WOKernel_FileType) atype = myEntity->GetFileType(aname);
	  if(!atype.IsNull())
	    {
	      result = atype->GetDirectory(myEntity->Params());
	    }
	}
    }
  return result;
}

//=======================================================================
//function : GetFileTypeArguments
//purpose  : 
//=======================================================================
void WOKAPI_Entity::GetFileTypeArguments(const Handle(TCollection_HAsciiString)& aname,
					 TColStd_SequenceOfHAsciiString& argseq) const 
{
  argseq.Clear();
  
  if(IsValid() && !aname.IsNull())
    {
      if(!myEntity->IsOpened()) myEntity->Open();
      Handle(TColStd_HSequenceOfHAsciiString) aseq;
      Handle(WOKernel_FileType) atype = myEntity->GetFileType(aname);

      if(!atype.IsNull())
	{
	  Standard_Integer i;

	  aseq = atype->GetArguments();
	  
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      argseq.Append(aseq->Value(i));
	    }
	}
    }
}

//=======================================================================
//function : GetFilePath
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::GetFilePath(const Handle(TCollection_HAsciiString)& atype, 
							    const Handle(TCollection_HAsciiString)& aname) const 
{
  Handle(TCollection_HAsciiString) result;

  if(!IsValid() || aname.IsNull() || atype.IsNull()) return result;
  if(!myEntity->IsOpened()) myEntity->Open();

   Handle(WOKernel_FileType) thetype = myEntity->GetFileType(atype);

   if(!thetype.IsNull()) 
    {
      Handle(WOKernel_File) afile = new WOKernel_File(aname, myEntity, thetype); 
      afile->GetPath();
      result =  afile->Path()->Name();
    }
   return result;
}


//=======================================================================
//function : GetFilePath
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Entity::GetFilePath(const Handle(TCollection_HAsciiString)& atype) const 
{
  Handle(TCollection_HAsciiString) result;

  if(!IsValid() || atype.IsNull()) return result;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_FileType) thetype = myEntity->GetFileType(atype);

  if(!thetype.IsNull()) 
    {
      Handle(WOKernel_File) afile = new WOKernel_File( myEntity, thetype);
      afile->GetPath();
      result =  afile->Path()->Name();
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetInterpFiles
//purpose  : 
//=======================================================================
void WOKAPI_Entity::GetInterpFiles(Handle(WOKTools_HSequenceOfReturnValue)& scripts) const
{
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  static Handle(TCollection_HAsciiString) admfile = new TCollection_HAsciiString("admfile");
  Handle(TCollection_HAsciiString) script;
  Handle(TCollection_HAsciiString) scrpath;
  Handle(WOKUtils_Path) path;

  if(scripts.IsNull()) scripts = new WOKTools_HSequenceOfReturnValue;

  // CSHELL
  script  = WOKTools_InterpFileValue::FileName(WOKTools_CShell, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_CShell));


  // BOURNESHELL
  script  = WOKTools_InterpFileValue::FileName(WOKTools_BourneShell, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_BourneShell));

  // KornShell
  script  = WOKTools_InterpFileValue::FileName(WOKTools_KornShell, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_KornShell));

  // Tcl
  script  = WOKTools_InterpFileValue::FileName(WOKTools_TclInterp, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_TclInterp));

  // Emacs Lisp
  script  = WOKTools_InterpFileValue::FileName(WOKTools_EmacsLisp, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_EmacsLisp));

  // WNT
  script  = WOKTools_InterpFileValue::FileName(WOKTools_WNTCmd, Name());
  scrpath = GetFilePath(admfile, script);
  
  path = new WOKUtils_Path(scrpath);
  
  if(path->Exists())
    scripts->Append(new WOKTools_InterpFileValue(path->Name(), WOKTools_WNTCmd));

  return;
}

//=======================================================================
//function : CompletePath
//purpose  : auxiliary; completes the path by items found in its old value
//           without duplications with new items
//=======================================================================

static void CompletePath (WOKTools_MapOfHAsciiString &theMap,
			  const Handle(TCollection_HAsciiString) &thePrevPath,
			  Handle(TCollection_HAsciiString) &thePath)
{      
  // iterate by elements in previous value of the path
  Standard_Integer i=1;
  Handle(TCollection_HAsciiString) item = thePrevPath->Token(PATH_SEPARATOR,i);
  while ( ! item->IsEmpty() )
  {
    // if item is not yet in new value, add it
    if ( ! theMap.Contains(item) ) {
      theMap.Add(item);
      if ( thePath->Length() >0 )
	thePath->AssignCat ( PATH_SEPARATOR );
      thePath->AssignCat ( item );
    }
    i++;
    item = thePrevPath->Token(PATH_SEPARATOR,i);
  }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetEnvActions
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_Entity::GetEnvActions(const WOKAPI_Session& asession,
					      WOKTools_Return& returns) const
{
  WOKAPI_Factory  thefact(asession,UserPath());

  if(!thefact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
	       << "Could not determine factory : Nothing done" << endm;
      return 1;
    }

  WOKAPI_Workshop theshop(asession,UserPath());

  if(!theshop.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
	       << "Could not determine workshop : Nothing done" << endm;
      return 1;
    }

  WOKAPI_Workbench thebench(asession,UserPath());
      
  if(!thebench.IsValid())
    {
      ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
	       << "Could not determine workbench : Nothing done" << endm;
      return 1;
    }
      
  WOKAPI_SequenceOfWorkbench ancestors;

  thebench.Ancestors(ancestors);

  returns.AddSetEnvironment("STATION",     asession.Station()->ToCString());
  returns.AddSetEnvironment("TARGET_DBMS", asession.DBMSystem()->ToCString());
      
      
  WOKAPI_SequenceOfParcel parseq;
  Standard_Integer i;
      
  theshop.UsedParcels(parseq);
      
  for(i=1; i<=parseq.Length(); i++)
    {
      const WOKAPI_Parcel& parcel = parseq.Value(i);
	  
      WOKAPI_Unit deliv;
	      
      parcel.Delivery(deliv);
	      
      static Handle(TCollection_HAsciiString) hometype = new TCollection_HAsciiString("HomeDir");
	      
      Handle(TCollection_HAsciiString) homedir = parcel.GetFilePath(hometype);
	      
      if(homedir.IsNull())
	{
	  ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
		   << "Could not determine HomeDir of parcel : " << parcel.UserPath() << endm;
	  return 1;
	}
	      
      if(deliv.IsValid())
	{
	  const WOKAPI_Unit& unit = deliv;
		  
	  if(unit.IsValid())
	    {
	      Handle(TCollection_HAsciiString) pname = new TCollection_HAsciiString(unit.Name());
		      
	      pname->UpperCase();
	      pname->AssignCat("HOME");
		      
	      returns.AddSetEnvironment(pname, homedir);
	    }
	}
    }
      
  // Calcul du Path 
  static Handle(TCollection_HAsciiString) pathparamname = new TCollection_HAsciiString("%ENV_PATH");
  Handle(TCollection_HAsciiString) pathvarname = ParameterEval(pathparamname);
      
  if(pathvarname.IsNull())
    {
      ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
	       << "Could not eval %ENV_PATH (path environment variable name)" << endm;
      return 1;
    }
      
  OSD_Environment PATH(pathvarname->String());
  WOKTools_MapOfHAsciiString pathmap;
  Handle(TCollection_HAsciiString) prev_pathvalue = new TCollection_HAsciiString(PATH.Value());

  prev_pathvalue->ChangeAll('\\', '/');

  if(PATH.Value().IsEmpty())
    {
      WarningMsg() << "WOKAPI_Entity::GetEnvActions" 
	         << "Environment variable " << pathvarname << " is not setted" << endm;
    }
  
  // Calcul du LD_LIBRARY_PATH
  static Handle(TCollection_HAsciiString) ldpathparamname = new TCollection_HAsciiString("%ENV_LDPATH");
  Handle(TCollection_HAsciiString) ldpathvarname = ParameterEval(ldpathparamname);

  ldpathvarname->ChangeAll('\\', '/');
  
  if(ldpathvarname.IsNull())
    {
      ErrorMsg() << "WOKAPI_Entity::GetEnvActions" 
	       << "Could not eval %ENV_LDPATH (library path environment variable name)" << endm;
      return 1;
    }

  Standard_Boolean samevars = Standard_False;

  if(!strcmp(ldpathvarname->ToCString(), pathvarname->ToCString()))
    {
      samevars = Standard_True;
    }
      
  OSD_Environment LDPATH(ldpathvarname->String());
  WOKTools_MapOfHAsciiString ldmap;
  Handle(TCollection_HAsciiString) prev_ldpathvalue = new TCollection_HAsciiString(LDPATH.Value());

  if(LDPATH.Value().IsEmpty())
    {
      WarningMsg() << "WOKAPI_Entity::GetEnvActions" 
	         << "Environment variable " << ldpathvarname << " is not setted" << endm;    
    }

  // Ajout des WBs au PATH et LD_LIBRARY_PATH
  Handle(TCollection_HAsciiString) pathvalue = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString) ldpathvalue = new TCollection_HAsciiString;

  for(i=1; i<=ancestors.Length(); i++)
    {
      const WOKAPI_Workbench& anancestor = ancestors.Value(i);

      if(anancestor.IsValid())
	{
	  // pour l'instant j'ajoute le bin et le lib 
	  static Handle(TCollection_HAsciiString) bindirtype = new TCollection_HAsciiString("bindir");
	  Handle(TCollection_HAsciiString) bindir = anancestor.GetFilePath(bindirtype);
	      
	  Handle(WOKUtils_Path) binpath = new WOKUtils_Path(bindir);

	  WOKUtils_PathIterator anit(binpath);

	  if(!pathmap.Contains(binpath->Name()) && anit.More())
	    {
	      pathvalue->AssignCat(PATH_SEPARATOR);
	      pathvalue->AssignCat(binpath->Name());
	      pathmap.Add(binpath->Name());
	    }

	  static Handle(TCollection_HAsciiString) libdirtype = new TCollection_HAsciiString("libdir");
	  Handle(TCollection_HAsciiString) libdir = anancestor.GetFilePath(libdirtype);
	      
	  Handle(WOKUtils_Path) libpath = new WOKUtils_Path(libdir);

	  if(!samevars)
	    {
	      WOKUtils_PathIterator anit(libpath);
	      if(!ldmap.Contains(libpath->Name()) && anit.More())
		{
		  if (ldpathvalue->Length() > 0) {
		    ldpathvalue->AssignCat(PATH_SEPARATOR);
		  }
		  ldpathvalue->AssignCat(libpath->Name());
		  ldmap.Add(libpath->Name());
		}
	    }
	  else
	    {
	      WOKUtils_PathIterator anit(libpath);
	      if(!pathmap.Contains(libpath->Name()) && anit.More())
		{
		  pathvalue->AssignCat(PATH_SEPARATOR);
		  pathvalue->AssignCat(libpath->Name());
		  pathmap.Add(libpath->Name());
		}
	    }

	}
    }

  // Eventuel Ajout des ULs au PATH et LD_LIBRARY_PATH
  for(i=1; i<=parseq.Length(); i++)
    {
      const WOKAPI_Parcel& parcel = parseq.Value(i);
	  
      if(parcel.IsValid())
	{
	  // pour l'instant j'ajoute le bin et le lib 
	  static Handle(TCollection_HAsciiString) libdirtype = new TCollection_HAsciiString("libdir");
	  Handle(TCollection_HAsciiString) libdir = parcel.GetFilePath(libdirtype);
	  // pour l'instant j'ajoute le bin et le lib 
	  static Handle(TCollection_HAsciiString) bindirtype = new TCollection_HAsciiString("bindir");
	  Handle(TCollection_HAsciiString) dir = parcel.GetFilePath(bindirtype);
	      
	  Handle(WOKUtils_Path) libpath = new WOKUtils_Path(libdir);
	  Handle(WOKUtils_Path) path = new WOKUtils_Path(dir);

	  WOKUtils_PathIterator alibit(libpath);
	  WOKUtils_PathIterator abinit(path);

	  if(!samevars)
	    {
	      if(!ldmap.Contains(libpath->Name()) && alibit.More())
		{
		  if (ldpathvalue->Length() > 0) {
		    ldpathvalue->AssignCat(PATH_SEPARATOR);
		  }
		  ldpathvalue->AssignCat(libpath->Name());
		  ldmap.Add(libpath->Name());
		}
	    }
	  else
	    {
	      if(!pathmap.Contains(libpath->Name()) && alibit.More())
		{
		  pathvalue->AssignCat(PATH_SEPARATOR);
		  pathvalue->AssignCat(libpath->Name());
		  pathmap.Add(libpath->Name());
		}
	    }

	  if(!pathmap.Contains(path->Name()) && abinit.More())
	    {
	      pathvalue->AssignCat(PATH_SEPARATOR);
	      pathvalue->AssignCat(path->Name());
	      pathmap.Add(path->Name());
	    }
	  
	  
	}
    }

  if ( ! samevars )
    CompletePath ( ldmap, prev_ldpathvalue, ldpathvalue );
  CompletePath ( pathmap, prev_pathvalue, pathvalue );

  if(!samevars) returns.AddSetEnvironment(ldpathvarname, ldpathvalue);

  returns.AddSetEnvironment(pathvarname, pathvalue);

  // C'est fini pour les setenvs : commencer les source Toto.csh
      
  Handle(WOKTools_HSequenceOfReturnValue) values = returns.Values();

  thefact.GetInterpFiles(values);
  theshop.GetInterpFiles(values);

  for(i=1; i<=parseq.Length(); i++)
    {
      WOKAPI_Unit delivery;
	  
      parseq.Value(i).Delivery(delivery);

      delivery.GetInterpFiles(values);
    }
      
  for(i=ancestors.Length(); i>=1; i--)
    {
      ancestors.Value(i).GetInterpFiles(values);
    }

  return 0;
}


//Standard_Boolean WOKAPI_Entity::HomePage(const Standard_OStream& astream) const
Standard_Boolean WOKAPI_Entity::HomePage(const Standard_OStream& ) const
{
  return Standard_True;
}

//Standard_Boolean WOKAPI_Entity::ItemHRef(const Standard_OStream& astream) const
Standard_Boolean WOKAPI_Entity::ItemHRef(const Standard_OStream& ) const
{
  return Standard_True;
}

//Standard_Boolean WOKAPI_Entity::PageHeader(const Standard_OStream& astream) const
Standard_Boolean WOKAPI_Entity::PageHeader(const Standard_OStream& ) const
{
  return Standard_True;
}

//Standard_Boolean WOKAPI_Entity::PageFooter(const Standard_OStream& astream) const
Standard_Boolean WOKAPI_Entity::PageFooter(const Standard_OStream& ) const
{
  return Standard_True;
}
