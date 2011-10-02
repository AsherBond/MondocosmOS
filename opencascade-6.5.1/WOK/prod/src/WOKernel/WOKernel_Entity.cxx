// File:	WOKernel_Entity.cxx
// Created:	Thu Jun 29 13:56:44 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ProgramError.hxx>

#include <TColStd_HSequenceOfAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_PathIterator.hxx>
#include <WOKUtils_ParamItem.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_ShellManager.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_DBMSystem.hxx>

#include <WOKernel_FileTypeKeyWords.hxx>

#include <WOKernel_Entity.ixx>

#ifdef WNT
# define PATH_SEPARATOR "; \t\n"
#else
# define PATH_SEPARATOR ": \t\n"
#endif  // WNT
//=======================================================================
//function : WOKernel_Entity
//purpose  : WOKernel Entity initializer
//=======================================================================
WOKernel_Entity::WOKernel_Entity(const Handle(TCollection_HAsciiString)& aname, 
				 const Handle(WOKernel_Entity)& anesting) 
  : WOKernel_BaseEntity(aname, anesting), myopenstatus(Standard_False)
{
  myfullname = GetUniqueName();
}

//=======================================================================
//function : GetParams
//purpose  : 
//=======================================================================
void WOKernel_Entity::GetParams() 
{
  Handle(WOKernel_Entity) entity;

  GetParameters();

  if(!Nesting().IsNull())
    {
      // Entites quelconques

      entity = Session()->GetEntity(Nesting());

      Params().Set((Standard_CString)ENTITYVAR,      Name()->ToCString());
      Params().Set((Standard_CString)ENTITYPATHVAR,  FullName()->ToCString());
      Params().Set((Standard_CString)ENTITYTYPEVAR,  EntityCode()->ToCString());
      Params().Set((Standard_CString)NESTINGVAR,     entity->Name()->ToCString());
      Params().Set((Standard_CString)NESTINGPATHVAR, entity->FullName()->ToCString());
      Params().Set((Standard_CString)NESTINGTYPEVAR, entity->EntityCode()->ToCString());

      Params().Set((Standard_CString)STATIONVAR, WOKernel_Station::GetName(Session()->Station())->ToCString());
      Params().Set((Standard_CString)DBMSVAR, WOKernel_DBMSystem::GetName(Session()->DBMSystem())->ToCString());

      if(IsKind(STANDARD_TYPE(WOKernel_DevUnit)))
	{
	  Handle(TCollection_HAsciiString) sts = entity->EvalParameter((Standard_CString) STATIONS_SUFFIX,Standard_False);
	  Handle(TCollection_HAsciiString) dbs = entity->EvalParameter((Standard_CString) DBMSYSTEMS_SUFFIX,Standard_False);

	 if(sts.IsNull()) {
		  ErrorMsg() << "WOKernel_Entity::GetParams"
				   << "Parameter " << ParameterName((Standard_CString) STATIONS_SUFFIX) << " is not setted using current station : " << WOKernel_Station::GetName(Session()->Station()) << endm;
		  sts = new TCollection_HAsciiString(WOKernel_Station::GetName(Session()->Station()));
	  }
	  mystations = WOKernel_Station::GetHSeqOfStation(sts);

	  if(dbs.IsNull()) {
		  ErrorMsg() << "WOKernel_Entity::GetParams"
				   << "Parameter " << ParameterName((Standard_CString) DBMSYSTEMS_SUFFIX) << " is not setted using current dbmsystem : " << WOKernel_DBMSystem::GetName(Session()->DBMSystem()) << endm;
		  dbs = new TCollection_HAsciiString(WOKernel_DBMSystem::GetName(Session()->DBMSystem()));
	  }
	  mydbmss    = WOKernel_DBMSystem::GetHSeqOfDBMS(dbs);
	  
	  Params().Set(ParameterName("Stations")->ToCString(), sts->ToCString());
	  Params().Set(ParameterName("DBMSystems")->ToCString(), dbs->ToCString());
	}
      else
	{
	  mystations = WOKernel_Station::GetHSeqOfStation(EvalParameter("Stations",Standard_False));
	  mydbmss    = WOKernel_DBMSystem::GetHSeqOfDBMS(EvalParameter("DBMSystems",Standard_False));
	}

      Params().Set((Standard_CString)LOCALARCHVAR, Session()->Params().Eval((Standard_CString)LOCALARCHVAR)->ToCString());
    }
  else
    {
      // Session

      Params().Set((Standard_CString)ENTITYVAR,  Name()->ToCString());
      Params().Set((Standard_CString)ENTITYTYPEVAR, EntityCode()->ToCString());

    }

}

//=======================================================================
//function : GetParameters
//purpose  :  
//=======================================================================
void WOKernel_Entity::GetParameters()
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfAsciiString) aseq;
  Handle(TColStd_HSequenceOfAsciiString) subclasses = new TColStd_HSequenceOfAsciiString;
  Handle(TColStd_HSequenceOfAsciiString) dirs       = new TColStd_HSequenceOfAsciiString;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) libdir;
  Handle(WOKernel_Entity) entity;

  if(!Nesting().IsNull())
    {
      // Entites quelconques

      entity = Session()->GetEntity(Nesting());

      aseq = entity->Params().SubClasses();
      if(!aseq.IsNull())
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      subclasses->Append(aseq->Value(i));
	    }
	}
      subclasses->Append(Name()->ToCString());

      aseq =  entity->Params().SearchDirectories();
      if(!aseq.IsNull())
	{
	  for(i=1; i<=aseq->Length(); i++)
	    {
	      dirs->Append(aseq->Value(i));
	    }
	}
      //ChangeParams().SetSubClasses(subclasses);
      //ChangeParams().SetSearchDirectories(dirs);

      // on evalue le ADM
      //astr = EvalParameter("Adm", Standard_False);
      astr = entity->Params().Eval(ParameterName("Adm")->ToCString(), Standard_False);
      
      if(!astr.IsNull())
	{
	  dirs->Prepend(astr->ToCString());
	  TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
	  lastsub.AssignCat("@");
	  lastsub.AssignCat(astr->String());
	}
      ChangeParams().SetSubClasses(subclasses);
      ChangeParams().SetSearchDirectories(dirs);
    }
  else
    {
      // Session
      subclasses->Append(Name()->ToCString());

      // on evalue le WOK_LIBRARY
      astr = EvalParameter("WokLibPath", Standard_False);

      i=1;
      libdir = astr->Token(PATH_SEPARATOR, i);

      while(!libdir->IsEmpty())
	{
	  dirs->Append(libdir->ToCString());
	  i++;
	  libdir = astr->Token(PATH_SEPARATOR, i);
	}

      // on evalue le ADM
      astr = EvalParameter("Adm", Standard_False);

      if(!astr.IsNull())
	{
	  dirs->Prepend(astr->ToCString());
	  TCollection_AsciiString& lastsub = subclasses->ChangeValue(subclasses->Length());
	  lastsub.AssignCat("@");
	  lastsub.AssignCat(astr->String());
	}

      ChangeParams().SetSubClasses(subclasses);
      ChangeParams().SetSearchDirectories(dirs);
    }

}  


//=======================================================================
//function : SetParam
//purpose  : sets Entity parameters 
//=======================================================================
void WOKernel_Entity::SetParams(const WOKUtils_Param& aparam)
{
  myparams = aparam;
}

//=======================================================================
//function : ParameterName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Entity::ParameterName(const Standard_CString aparamname) const
{
  Handle(TCollection_HAsciiString) aname=new TCollection_HAsciiString;

  aname->AssignCat("%");
  aname->AssignCat(Name());
  aname->AssignCat("_");
  aname->AssignCat(aparamname);
  return aname;
}

//=======================================================================
//function : EvalParameter
//purpose  : Evaluates a parameter of Entity
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Entity::EvalParameter(const Standard_CString aparamname, const Standard_Boolean isnecessary) const
{
  Handle(TCollection_HAsciiString) result;

  result = myparams.Eval(ParameterName(aparamname)->ToCString(), Standard_False);

  if(result.IsNull() && isnecessary ) 
    {
      ErrorMsg() << "WOKernel_Entity::EvalParameter" 
	       << "Parameter " << aparamname << " could not be evaluated" << endm;
      Standard_ProgramError::Raise("WOKernel_Entity::EvalParameter");
    }
  return result;
}

//=======================================================================
//function : EvalDefaultParameterValue
//purpose  : look up for a default value to parameter aname
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Entity::EvalDefaultParameterValue(const Handle(TCollection_HAsciiString)& aname,
									    const Standard_Integer evaldepth)
{
  Handle(TCollection_HAsciiString) result, nesting, argval;
  Handle(TColStd_HSequenceOfHAsciiString) argseq;
  Standard_Integer nestlen = strlen(NESTING_PREFIX);
  Standard_Integer entlen  = strlen(ENTITY_PREFIX);
  Standard_Integer i, depth;
  TCollection_AsciiString astr;
  Handle(WOKernel_Entity) thenest;

  Params().LoadParamClass("DEFAULT",Params().SubClasses());

  if(Params().IsSet(ParameterName(aname->ToCString())->ToCString()))
    {
      result = EvalParameter(aname->ToCString());
      return result;
    }
  
  if(evaldepth > 20 )
    {
      ErrorMsg() << "WOKernel_Entity::EvalDefaultParameterValue" 
	       << "Too many levels in DEFAULT parameter evaluation" << endm;
      return result;
    }

  if(!evaldepth)
    {
      myparams.Set((Standard_CString)ENTITYVAR, Name()->ToCString());
      
      if(!Nesting().IsNull())
	{
	  thenest = Session()->GetEntity(Nesting());
	  nesting = thenest->Name();
	  myparams.Set((Standard_CString)NESTINGVAR, nesting->ToCString());
	}
      else
	{
	  myparams.Set((Standard_CString)NESTINGVAR, "NoNestingSetted");
	}
    }

  astr.AssignCat("DEFAULT_");
  astr.AssignCat(aname->ToCString());

  if(Params().IsSet(astr.ToCString()))
    {
      argseq = Params().GetArguments(astr.ToCString());
      depth = evaldepth;
      depth++;

      for(i=1; i<= argseq->Length(); i++)
	{
	  if(!myparams.IsSet(argseq->Value(i)->ToCString()))
	    {
	      if(!strncmp(argseq->Value(i)->ToCString(), NESTING_PREFIX, nestlen))
		{
		  argval.Nullify();
		  if(!thenest.IsNull())
		    {
		      argval = thenest->EvalDefaultParameterValue(argseq->Value(i)->SubString(nestlen+1, argseq->Value(i)->Length()), depth);
		    }

		  if(!argval.IsNull())
		    {
		      myparams.Set(argseq->Value(i)->ToCString(), argval->ToCString());
		    }
		  else
		    {
		      ErrorMsg() << "WOKernel_Entity::EvalDefaultParameterValue" 
			       << "Could not eval default value for argument : " <<  argseq->Value(i) << endm;
		      return result;
		    }
		}
	      else if(!strncmp(argseq->Value(i)->ToCString(), ENTITY_PREFIX, entlen))
		{
		  argval = EvalDefaultParameterValue(argseq->Value(i)->SubString(entlen+1, argseq->Value(i)->Length()), depth);
	      
		  if(!argval.IsNull())
		    {
		      myparams.Set(argseq->Value(i)->ToCString(), argval->ToCString());
		    }
		  else
		    {
		      ErrorMsg() << "WOKernel_Entity::EvalDefaultParameterValue" 
			       << "Could not eval default value for argument : " <<  argseq->Value(i) << endm;
		      return result;
		    }
		}
	      else
		{
		  ErrorMsg() << "WOKernel_Entity::EvalDefaultParameterValue" 
			   << "Default value Argument not set : " << argseq->Value(i) << endm;
		  return result;
		}
	    }
	}
  
      result = myparams.Eval(astr.ToCString());
    }
  
  myparams.UnSet((Standard_CString)ENTITYVAR);
  myparams.UnSet((Standard_CString)NESTINGVAR);

  return result;
}

//=======================================================================
//function : SetFileTypeBase
//purpose  : 
//=======================================================================
void WOKernel_Entity::SetFileTypeBase(const Handle(WOKernel_FileTypeBase)& abase)
{
  mytypes = abase;
}

//=======================================================================
//function : FileTypeBase
//purpose  : 
//=======================================================================
Handle(WOKernel_FileTypeBase) WOKernel_Entity::FileTypeBase() const
{
  return mytypes;
}

//=======================================================================
//function : GetFileType
//purpose  : 
//=======================================================================
Handle(WOKernel_FileType) WOKernel_Entity::GetFileType(const Handle(TCollection_HAsciiString)& atypename) const
{
  return mytypes->Type(atypename);
}

//=======================================================================
//function : GetFileType
//purpose  : 
//=======================================================================
Handle(WOKernel_FileType) WOKernel_Entity::GetFileType(const Standard_CString atypename) const
{
  return mytypes->Type(atypename);
}

//=======================================================================
//function : DumpBuildParameters
//purpose  : Creates file containing parameters used to create Entity
//=======================================================================
void WOKernel_Entity::DumpBuildParameters(const Handle(WOKUtils_HSequenceOfParamItem)& someparams) const
{
  Handle(WOKernel_File) deffile;
  Handle(WOKernel_FileType) deftype;

  if(!FileTypeBase()->IsType("DefinitionFile")) return;

  deftype  = GetFileType("DefinitionFile");

  if(deftype->IsFileDependent())
    {
      WarningMsg() << "WOKernel_Entity::DumpBuildParameters"
		 << "Incorrect DefinitionFile definition for " << UserPathName() << " : No file created" << endm;
      return;
    }

  deffile = new WOKernel_File(this, deftype);
  deffile->GetPath();

  // On "Dump" le fichier de config
  myparams.Write(deffile->Path(), someparams);
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKernel_Entity::BuildParameters(const Handle(WOKUtils_HSequenceOfParamItem)& someparams,
								       const Standard_Boolean usedefaults) 
{
  Standard_Integer i, apos;
  Handle(WOKUtils_HSequenceOfParamItem)   result = new WOKUtils_HSequenceOfParamItem;
  Handle(WOKernel_FileTypeBase)           abase  = new WOKernel_FileTypeBase;  
  Handle(TColStd_HSequenceOfHAsciiString) needed;
  Handle(TCollection_HAsciiString)        nullhandle, stations, dbmss, aname, avalue;
  

  Params().Set(Params().ClassLoadFlag(Name()->ToCString())->ToCString(), "");

  Params().Set(someparams);
  GetParams();
  Params().Set(someparams);

  abase->Load(Params());  
  SetFileTypeBase(abase);

  // evaluation des DBMS et STATIONS
  
  if(usedefaults)
    {
      dbmss    = EvalDefaultParameterValue(new TCollection_HAsciiString("DBMSystems"));
      stations = EvalDefaultParameterValue(new TCollection_HAsciiString("Stations"));
    }
  else
    {
      dbmss    = EvalParameter("DBMSystems");
      stations = EvalParameter("Stations");
    }

  mystations = WOKernel_Station::GetHSeqOfStation(stations);
  mydbmss    = WOKernel_DBMSystem::GetHSeqOfDBMS(dbmss);

  needed = FileTypeBase()->GetNeededParameters(nullhandle, Name(), mydbmss, mystations);
  WOKTools_MapOfHAsciiString amap;

  for(i=1; i<=needed->Length(); i++)
    {
      if(!amap.Contains(needed->Value(i)))
	{
	  aname = needed->Value(i);
	  avalue.Nullify();
	  if(usedefaults)
	    {
	      apos = aname->Search("_");
	      if(apos > 1)
		{
		  avalue = EvalDefaultParameterValue(aname->SubString(apos+1, aname->Length()));
		}
	    }
	  else
	    {
	      avalue = EvalParameter(needed->Value(i)->ToCString());
	    }

	  result->Append(WOKUtils_ParamItem(needed->Value(i), avalue));
	  amap.Add(needed->Value(i));
	}
    }

  return result;
}

//=======================================================================
//function : IsValidName
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Entity::IsValidName() const
{
  return WOKernel_Entity::IsValidName(Name());
}


//=======================================================================
//function : IsValidName
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Entity::IsValidName(const Handle(TCollection_HAsciiString)& aname) 
{
  Standard_Integer   i;
  Standard_Character c;
  
  if(aname.IsNull()) return Standard_False;
  
  for (i=1; i<= aname->Length(); i++)
    {
      c = aname->Value(i);
      if (  !IsAlphanumeric ( c ) && !IsEqual ( c, '-' ) && !IsEqual ( c, '.' )  )
	return Standard_False;
    }
  
  return Standard_True;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
void WOKernel_Entity::Build(const Handle(WOKUtils_HSequenceOfParamItem)& someparams)
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) dirs, files;
  Handle(TCollection_HAsciiString)        nesting ;
  Handle(WOKUtils_Path) apath;
  Handle(WOKernel_FileTypeBase) abase;

  if (!IsValidName()) {
    ErrorMsg() << "WOKernel_Entity::Build" 
      << "Invalid Name for entity : " << Name() << endm;
    return;
  }

  if(Session()->IsKnownEntity(FullName()))
    {
      ErrorMsg() << "WOKernel_Entity::Build" 
	       << "An Entity with name " << UserPathName() << " is already defined" << endm;
      return;
    }
    
  Session()->AddEntity(this);

  Params().Set(someparams);
  GetParams();
  Params().Set(someparams);
  Params().Set(Params().ClassLoadFlag(Name()->ToCString())->ToCString(), "");

  mystations = WOKernel_Station::GetHSeqOfStation(EvalParameter("Stations"));
  mydbmss    = WOKernel_DBMSystem::GetHSeqOfDBMS(EvalParameter("DBMSystems"));

  abase = new WOKernel_FileTypeBase;
  abase->Load(Params());  
  SetFileTypeBase(abase);

  // Preparation du shell
  Handle(WOKUtils_Shell) buildsh = WOKUtils_ShellManager::GetShell();

  if(!buildsh->IsLaunched()) buildsh->Launch();
  buildsh->Lock();

  // Execution du before

  if(Params().IsSet((Standard_CString) WOKENTITYBEFOREBUID))
    {
      Handle(TCollection_HAsciiString) cmd;

      cmd = Params().Eval((Standard_CString) WOKENTITYBEFOREBUID);

      if(!cmd.IsNull())
	{
	  buildsh->ClearOutput();
	  
	  WOK_TRACE {
	    VerboseMsg()("WOK_CREATE") << "WOKernel_Entity::Build" 
				     << "Launching before command : " << cmd << endm;
	  }
	  
	  buildsh->Execute(cmd);

	  if(buildsh->Status())
	    {
	      ErrorMsg() << "WOKernel_Entity::Build" 
		       << "Errors occured in BeforeBuild :" << endm;

	      Handle(TColStd_HSequenceOfHAsciiString) aseq = buildsh->Errors();
	      Standard_Integer i;

	      for(i=1; i<=aseq->Length(); i++)
		{
		  ErrorMsg() << "WOKernel_Entity::Build" << aseq->Value(i) << endm;
		}
	    }
	}
    }

  nesting = Session()->GetEntity(Nesting())->Name();

  dirs = FileTypeBase()->GetDirectories(this, mydbmss, mystations, Standard_True);
  
  for(i=1; i<=dirs->Length(); i++)
    {
      WOK_TRACE {
	VerboseMsg()("WOK_CREATE") << "WOKernel_Entity::Build" 
				 << "Creating directory : " << dirs->Value(i) << endm;
      }

      apath = new WOKUtils_Path(dirs->Value(i));
      if (!apath->CreateDirectory(Standard_True)) {
	ErrorMsg() << "WOKernel_Entity::Build" 
	  << "Enable to create directory " << dirs->Value(i) << endm;
      }
    }

  files = FileTypeBase()->GetFiles(this, mydbmss, mystations, Standard_True);

  for(i=1; i<=files->Length(); i++)
    {
      WOK_TRACE {
	VerboseMsg()("WOK_CREATE") << "WOKernel_Entity::Build" 
				 << "Creating file : " << files->Value(i) << endm;
      }
      
      apath = new WOKUtils_Path(files->Value(i));
      apath->CreateFile(Standard_True);
    }

  // Execution du after

  if(Params().IsSet((Standard_CString) WOKENTITYAFTERBUILD))
    {
      Handle(TCollection_HAsciiString) cmd;

      cmd = Params().Eval((Standard_CString) WOKENTITYAFTERBUILD);

      if(!cmd.IsNull())
	{
	  buildsh->ClearOutput();

	  WOK_TRACE {
	    VerboseMsg()("WOK_CREATE") << "WOKernel_Entity::Build" 
				     << "Launching after command : " << cmd << endm;
	  }
	  
	  buildsh->Execute(cmd);
	  
	  if(buildsh->Status())
	    {
	      ErrorMsg() << "WOKernel_Entity::Build" 
		       << "Errors occured in AfterBuild :" << endm;

	      Handle(TColStd_HSequenceOfHAsciiString) aseq = buildsh->Errors();
	      Standard_Integer i;

	      for(i=1; i<=aseq->Length(); i++)
		{
		  ErrorMsg() << "WOKernel_Entity::Build" << aseq->Value(i) << endm;
		}
	    }

	}
    }

  buildsh->UnLock();

  DumpBuildParameters(someparams);

  Session()->RemoveEntity(this);
  return;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKernel_Entity::Destroy()
{
  Handle(TColStd_HSequenceOfHAsciiString) dirs, files;
  Handle(TCollection_HAsciiString)        nesting ;
  Handle(WOKUtils_Path) apath;
  Standard_Integer i;

  if(!IsOpened())
    {
      ErrorMsg() << "WOKernel_Entity::Destroy" 
	       << UserPathName() << " has to be opened to be destroyed" << endm;
      return;
    }

  // Preparation du shell
  Handle(WOKUtils_Shell) buildsh = WOKUtils_ShellManager::GetShell();
  
  if(!buildsh->IsLaunched()) buildsh->Launch();
  buildsh->Lock();

  // Execution du before

  if(Params().IsSet((Standard_CString) WOKENTITYBEFOREDESTROY))
    {
      Handle(TCollection_HAsciiString) cmd;

      cmd = Params().Eval((Standard_CString) WOKENTITYBEFOREDESTROY);

      if(!cmd.IsNull())
	{
	  buildsh->ClearOutput();

	  WOK_TRACE {
	    VerboseMsg()("WOK_DESTROY") << "WOKernel_Entity::Destroy" 
				      << "Launching before command : " << cmd << endm;
	  }

	  buildsh->Execute(cmd);

	  if(buildsh->Status())
	    {
	      ErrorMsg() << "WOKernel_Entity::Destroy" 
		       << "Errors occured in BeforeDestroy :" << endm;

	      Handle(TColStd_HSequenceOfHAsciiString) aseq = buildsh->Errors();
	      Standard_Integer i;

	      for(i=1; i<=aseq->Length(); i++)
		{
		  ErrorMsg() << "WOKernel_Entity::Destroy" << aseq->Value(i) << endm;
		}
	    }

	}
    }

  nesting = Session()->GetEntity(Nesting())->Name();

  files = FileTypeBase()->GetFiles(this, mydbmss, mystations, Standard_True);

  for(i=1; i<=files->Length(); i++)
    {
      WOK_TRACE {
	VerboseMsg()("WOK_DESTROY") << "WOKernel_Entity::Destroy" 
				  << "Destroying file : " << files->Value(i) << endm;
      }
      
      apath = new WOKUtils_Path(files->Value(i));

      if(apath->Exists())
	apath->RemoveFile();
    }

  dirs = FileTypeBase()->GetDirectories(this, mydbmss, mystations, Standard_True);
  
  for(i=dirs->Length(); i>=1; i--)
    {
      WOK_TRACE {
	VerboseMsg()("WOK_DESTROY") << "WOKernel_Entity::Destroy" 
				  << "Destroying dir  : " << dirs->Value(i) << endm;
      }

      apath = new WOKUtils_Path(dirs->Value(i));

      if(apath->Exists())
	{
	  WOKUtils_PathIterator anit(apath, Standard_True);

	  while(anit.More())
	    {
	      // suprimer les enfants
	      Handle(WOKUtils_Path) apath = anit.PathValue();
	      
	      if(apath->IsDirectory())
		{
		  WarningMsg() << "WOKernel_Entity::Destroy" 
		    << "Removing dir  : " << apath->Name() << endm;
		  apath->RemoveDirectory(Standard_False);
		}
	      else
		{
		  WarningMsg() << "WOKernel_Entity::Destroy" 
		    << "Removing file  : " << apath->Name() << endm;
		  apath->RemoveFile();
		}
	      anit.Next();
	    }
	  apath->RemoveDirectory(Standard_False);
	}
    }


  // Execution du after

  if(Params().IsSet((Standard_CString) WOKENTITYAFTERDESTROY))
    {
      Handle(TCollection_HAsciiString) cmd;

      cmd = Params().Eval((Standard_CString) WOKENTITYAFTERDESTROY);

      if(!cmd.IsNull())
	{
	  buildsh->ClearOutput();

	  WOK_TRACE {
	    VerboseMsg()("WOK_DESTROY") << "WOKernel_Entity::Destroy" 
				      << "Launching after command : " << cmd << endm;
	  }
	  
	  buildsh->Execute(cmd);

	  if(buildsh->Status())
	    {
	      ErrorMsg() << "WOKernel_Entity::Destroy" 
		       << "Errors occured in AfterDestroy :" << endm;

	      Handle(TColStd_HSequenceOfHAsciiString) aseq = buildsh->Errors();
	      Standard_Integer i;

	      for(i=1; i<=aseq->Length(); i++)
		{
		  ErrorMsg() << "WOKernel_Entity::Destroy" << aseq->Value(i) << endm;
		}
	    }
	}
    }

  buildsh->UnLock();
  return;
}


//=======================================================================
//function : SetOpened
//purpose  : the Entity is flagged to be opened
//=======================================================================
void WOKernel_Entity::SetOpened() 
{
  myopenstatus = Standard_True;
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKernel_Entity::Reset()
{
  myparams.Clear();
  mytypes.Nullify();  
  mystations.Nullify();
  mydbmss.Nullify();
}

//=======================================================================
//function : SetClosed
//purpose  : the Entity is flagged to be closed
//=======================================================================
void WOKernel_Entity::SetClosed() 
{
  myopenstatus = Standard_False;
}



//=======================================================================
//function : NestedUniqueName
//purpose  : Calculates the unique name of an BaseEntity Nested in BaseEntity
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Entity::NestedUniqueName(const Handle(TCollection_HAsciiString)& aname) const
{
  Handle(TCollection_HAsciiString) auniqname = new TCollection_HAsciiString(FullName());

  auniqname->AssignCat(":");
  auniqname->AssignCat(aname);
  return auniqname;
}

//=======================================================================
//function : GetUnitqueName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKernel_Entity::GetUniqueName() const
{
  if(!Nesting().IsNull())
    {
      // pas un Atelier
      const Handle(WOKernel_Entity)& entity = Session()->GetEntity(Nesting());

      if(!entity.IsNull())
	{
	  Handle(TCollection_HAsciiString) aname = new TCollection_HAsciiString(entity->UserPathName());
	  if(aname->Length() != 1)
	    aname->AssignCat(":");
	  aname->AssignCat(Name());
	  return aname;
	}
      Standard_ProgramError::Raise("WOKernel_Entity::GetUniqueName : Nesting could not be found");
      return Handle(TCollection_HAsciiString)();
    }
  else
    {
      return new TCollection_HAsciiString(":");
    }
}

//=======================================================================
//function : DBMSystems
//purpose  : 
//=======================================================================
Handle(WOKernel_HSequenceOfDBMSID) WOKernel_Entity::DBMSystems() const
{
  return mydbmss;
}

//=======================================================================
//function : Stations
//purpose  : 
//=======================================================================
Handle(WOKernel_HSequenceOfStationID) WOKernel_Entity::Stations() const
{
  return mystations;
}

//=======================================================================
//function : Kill
//purpose  : 
//=======================================================================
void WOKernel_Entity::Kill() 
{
}
