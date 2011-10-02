// File:	WOKBuilder_MSTranslator.cxx
// Created:	Mon Sep 11 13:45:33 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_NotImplemented.hxx>

#include <Standard_ProgramError.hxx>

#include <OSD_SharedLibrary.hxx>
#include <WOKernel_File.hxx>

#ifndef DONT_COMPENSATE
#include <stdio.h>

#ifdef WNT
# include <io.h>
#endif  // WNT

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#ifdef HAVE_SYS_TYPES_H
# include <sys/types.h>
#endif

#if defined (HAVE_SYS_STAT_H) || defined (WNT)
# include <sys/stat.h>
#endif
#include <fcntl.h>

#if defined(HAVE_TIME_H) || defined(WNT)
# include <time.h>
#endif

#include <Standard_Stream.hxx>
#endif // DONT_COMPENSATE


#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <MS.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_InstClass.hxx>
#include <MS_HSequenceOfInstClass.hxx>
#include <MS_Package.hxx>
#include <MS_StdClass.hxx>
#include <MS_GenClass.hxx>
#include <MS_Error.hxx>
#include <MS_Alias.hxx>
#include <MS_Pointer.hxx>
#include <MS_Package.hxx>
#include <MS_Interface.hxx>
#include <MS_Client.hxx>
#include <MS_Schema.hxx>
#include <MS_Engine.hxx>
#include <MS_MemberMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_Param.hxx>
#include <MS_HArray1OfParam.hxx>

#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSActionID.hxx>

#include <WOKBuilder_MSTranslator.ixx>

#include <sys/types.h>
#include <time.h>


//=======================================================================
//function : WOKBuilder_MSTranslator
//purpose  : 
//=======================================================================
 WOKBuilder_MSTranslator::WOKBuilder_MSTranslator(const Handle(TCollection_HAsciiString)& aname,
						  const Handle(TCollection_HAsciiString)& ashared)
   : WOKBuilder_MSTool(aname, WOKUtils_Param())
{
  SetShared(ashared);
  mytranslator = NULL;
} 

//=======================================================================
//function : WOKBuilder_MSTranslator
//purpose  : 
//=======================================================================
WOKBuilder_MSTranslator::WOKBuilder_MSTranslator(const Handle(TCollection_HAsciiString)& aname,
						 const WOKUtils_Param& params)
  : WOKBuilder_MSTool(aname, params)
{
  mytranslator = NULL;
}


//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_MSTranslator::Load()
{
  Handle(TCollection_HAsciiString) astr;

  if(Shared().IsNull())
    {
      SetShared(EvalToolParameter("SHARED"));

      if(Shared().IsNull() == Standard_True)
	{
	  return;
	}
    }

  Handle(WOKUtils_Path) libpath = new WOKUtils_Path(Shared());

  if(!libpath->Exists())
    {
      libpath = Params().SearchFile(Shared());
      
      if(libpath.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_MSTranslator::Load"
		   << "Could not find file : " << Shared() << endm;
	  return;
	}
    }

  OSD_SharedLibrary ashared(libpath->Name()->ToCString());

  if(ashared.DlOpen(OSD_RTLD_NOW) == Standard_False)
    {
      ErrorMsg() << "WOKBuilder_MSTranslator::Load" << ashared.DlError() << endm;
      return;
    }

  mytranslator = (WOKBuilder_MSTranslatorPtr) ashared.DlSymb(Name()->ToCString());

  if( mytranslator == NULL) 
    {
      ErrorMsg() << "WOKBuilder_MSTranslator::Load" << ashared.DlError() << endm;
      return;
    }

}

//=======================================================================
//function : MSActionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSTranslator::MSActionStatus(const Handle(WOKBuilder_MSAction)& anaction, 
								  const Handle(WOKBuilder_Specification)& anewcdl) const 
{
  WOKBuilder_MSActionID anid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer decal = 0;
  switch(MSchema()->GetActionStatus(anid))
    {
    case WOKBuilder_HasFailed:
      return WOKBuilder_OutOfDate;

    case WOKBuilder_NotDefined:
    case WOKBuilder_OutOfDate:
    case WOKBuilder_UpToDate:
      if(!MSchema()->IsDefined(anid.Name())) 
	{
	  return WOKBuilder_NotDefined;
	}
      else
	{
	  Handle(WOKBuilder_MSAction) oldaction = MSchema()->GetAction(anid);
	  
	  switch(anid.Type())
	    {
	    case WOKBuilder_CompleteType:
	    case WOKBuilder_SchemaType:
	    case WOKBuilder_GenType:
	    case WOKBuilder_TypeUses:
	    case WOKBuilder_Inherits:
	      {
		const Handle(MS_Type)& atype = MSchema()->MetaSchema()->GetType(anid.Name());
		Handle(MS_Class) aclass = Handle(MS_Class)::DownCast(atype);

		if(!aclass.IsNull())
		  {
		    if(aclass->IsNested() ||atype->IsKind(STANDARD_TYPE(MS_Error))) return WOKBuilder_UpToDate;
		  }
		else
		  {
		    if(atype->IsKind(STANDARD_TYPE(MS_NatType))) return WOKBuilder_UpToDate;
		  }
	      }

	    case WOKBuilder_Package:
	    case WOKBuilder_Schema:
	    case WOKBuilder_Interface:
	    case WOKBuilder_Client:
	    case WOKBuilder_Engine:
	    case WOKBuilder_Executable:
	    case WOKBuilder_Component:
	    case WOKBuilder_DirectUses:
	    case WOKBuilder_Uses:
	    case WOKBuilder_SchUses :
	    case WOKBuilder_GlobEnt:

		 {
#ifndef DONT_COMPENSATE
		Handle(WOKBuilder_Specification) oldspec = oldaction->Entity()->File();		
		if(!oldspec.IsNull())
		  {
		    if(!oldspec->Path()->Name()->IsSameString(anewcdl->Path()->Name()))
		      {
			WOK_TRACE {
			  VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslator::MSActionStatus"
						    << anaction->Entity()->Name() << " is OutofDate because files are not the same" << endm;
			}
			return WOKBuilder_OutOfDate;
		      }
		    else
		      {
			  WOK_TRACE {
			     VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslator::MSActionStatus"
						    << "NewFile : " << anewcdl->Path()->Name() << " is same than old : " 
						    << oldspec->Path()->Name() << endm;
			     }
		      }
		  }  
    TCollection_AsciiString tempath1 = anewcdl->Path()->Name()->ToCString();
    Standard_CString tempath = tempath1.ToCString();
    Standard_Integer fd;

    if((fd=open(tempath, O_RDONLY)) == -1)
      {
        WarningMsg() << "WOKStep_MSFill::Execute"
                 << "Could not create : " << tempath << endm;
        perror(tempath);
      }
    else
      {
        close(fd);
      }

    if(fd != -1 )
      {
        struct stat buf;
        if(stat(tempath, &buf))
          {
            ErrorMsg() << "WOKStep_MSFill::Execute"
                     << "Could not stat : " << tempath << endm;
          }

        time_t curdate ;        
        curdate = time(NULL);

        if(curdate == -1)
          {
            ErrorMsg() << "WOKStep_MSFill::Execute"
                     << "Could not obtain current date" << endm;
          }        
       if(buf.st_atime - curdate > 0)
          {
            decal =  buf.st_atime - curdate;       
          }
      }  
#endif // DONT_COMPENSATE

		if(anewcdl->Path()->MDate()- decal > oldaction->Date())
		  {
		    WOK_TRACE {
		      VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslator::MSActionStatus"
						<< anaction->Entity()->Name()  << "is OutOfDate because of dates : " 
						<< "stored(" << (Standard_Integer) oldaction->Date() << ") file(" << (Standard_Integer) anewcdl->Path()->MDate() << ")" << endm;
		    }
		    return WOKBuilder_OutOfDate;
		  }
		else
		  {
		    WOK_TRACE {
		      VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslator::MSActionStatus"
						<< anaction->Entity()->Name()  << " is up to date : " 
						<< "stored(" << (Standard_Integer) oldaction->Date() << ") file(" << (Standard_Integer) anewcdl->Path()->MDate() << ")" << endm;
		    }
		  }
	      }
	      break;
	    case WOKBuilder_Instantiate:
	    case WOKBuilder_InstToStd:
	    case WOKBuilder_InterfaceTypes:
	    case WOKBuilder_SchemaTypes:
	    case WOKBuilder_PackageMethods:
	      break;
	    default:
	      ErrorMsg() << "WOKBuilder_MSTranslator::MSActionStatus"
		<< "Unknown action type : " << anid.Type() << endm;
	      Standard_ProgramError::Raise("WOKBuilder_MSTranslator::MSActionStatus : Unknown action type");
	      break;
	    }
	}
    }
  return WOKBuilder_UpToDate;
}

//=======================================================================
//function : Translate
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::Translate(const Handle(WOKBuilder_MSAction)& , 
							  const Handle(WOKBuilder_Specification)& afile,
							  Handle(TColStd_HSequenceOfHAsciiString)& globlist, 
							  Handle(TColStd_HSequenceOfHAsciiString)& unkowntypelist,
							  Handle(TColStd_HSequenceOfHAsciiString)& instlist,
							  Handle(TColStd_HSequenceOfHAsciiString)& genlist)
{
  if(mytranslator == NULL)
    {
      ErrorMsg() << "WOKBuilder_MSTranslator::Translate" << "Null Translator : Cannot Perform" << endm;
      return WOKBuilder_Failed;
    }
  
  globlist = new TColStd_HSequenceOfHAsciiString;
  unkowntypelist = new TColStd_HSequenceOfHAsciiString;
  instlist = new TColStd_HSequenceOfHAsciiString;
  genlist = new TColStd_HSequenceOfHAsciiString;

  if((*mytranslator)(MSchema()->MetaSchema(),afile->Path()->Name(),globlist,unkowntypelist,instlist,genlist)) 
    {
      ErrorMsg() << "WOKBuilder_MSTranslator::Translate" << "Errors occured" << endm;
      return WOKBuilder_Failed;
    }
  return WOKBuilder_Success;
}


void WOKBuilder_MSTranslator::AddAction(WOKBuilder_MSTranslatorIterator& anit, 
					const Handle(TCollection_HAsciiString)& aname,
					const WOKBuilder_MSActionType action)
{
  if(action != WOKBuilder_InstToStd) 
    {
      anit.AddInStack(aname,action);
    }
  else
    {
      anit.AddInStack(aname,action);

      Handle(MS_InstClass) instclass =  Handle(MS_InstClass)::DownCast(MSchema()->MetaSchema()->GetType(aname));
      
      if(!instclass.IsNull())
	{
	  // les ajouts ne sont necessaires que si la class est a insttostd
	  //  : Traduire la generique si l'instclass doir etre InstToStd
	  anit.AddInStack(instclass->GenClass(), WOKBuilder_GenType);
	}
    }
}


//=======================================================================
//function : BuildPackage
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildPackage(const Handle(WOKBuilder_MSAction)& anaction,
							     const Handle(WOKBuilder_Specification)& afile,
							     WOKBuilder_MSTranslatorIterator& anit) 
{
  Standard_Integer i;
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSTranslator::BuildPackage" 
	  << "Package     : " << afile->Path()->Name() << endm;

	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_DirectUses);
	      
	      for(i=1; i<=gentypes->Length(); i++)
		{
		  AddAction(anit,gentypes->Value(i), WOKBuilder_GenType);
		  AddAction(anit,gentypes->Value(i), WOKBuilder_CompleteType);
		}
	      for(i=1; i<=insttypes->Length(); i++)
		{
		  AddAction(anit,insttypes->Value(i), WOKBuilder_Instantiate);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_InstToStd);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_CompleteType);
		}
	      for(i=1; i<=inctypes->Length(); i++)
		{
		  AddAction(anit,inctypes->Value(i), WOKBuilder_CompleteType);
		}	
      
	      Handle(MS_Package) thepk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());
	      Handle(TColStd_HSequenceOfHAsciiString) expects = thepk->Excepts();
	      Handle(TCollection_HAsciiString) fullname;
	      
	      for(i=1; i<=expects->Length(); i++)
		{
		  fullname = MS::BuildFullName(anaction->Entity()->Name(), expects->Value(i));
		  AddAction(anit,fullname, WOKBuilder_CompleteType);
		}

	      AddAction(anit,anaction->Entity()->Name(), WOKBuilder_PackageMethods); 
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	Handle(MS_Package) thepk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());
	
	Handle(TColStd_HSequenceOfHAsciiString) uses = thepk->Uses();
	
	for(i=1; i<=uses->Length(); i++)
	  AddAction(anit,uses->Value(i), WOKBuilder_DirectUses);
	
	Handle(TColStd_HSequenceOfHAsciiString) classes = thepk->Classes();
	Handle(TCollection_HAsciiString) fullname;
	const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
	Handle(MS_StdClass) stdclass;
	
	// les classes

	for(i=1; i<=classes->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), classes->Value(i));

	    if(ameta->IsDefined(fullname))
	      {
		Handle(MS_Type) atype = ameta->GetType(fullname);
		
		if(atype->IsKind(STANDARD_TYPE(MS_InstClass)))
		  {
		    if(!Handle(MS_Class)::DownCast(atype)->IsNested())
		      {
			AddAction(anit,fullname, WOKBuilder_Instantiate);
			AddAction(anit,fullname, WOKBuilder_InstToStd);
			AddAction(anit,fullname, WOKBuilder_CompleteType);
		      }
		  }
		else if(atype->IsKind(STANDARD_TYPE(MS_GenClass)))
		  {
		    if(!Handle(MS_Class)::DownCast(atype)->IsNested())
		      {
			AddAction(anit,fullname, WOKBuilder_GenType);
			AddAction(anit,fullname, WOKBuilder_CompleteType);
		      }
		  }
		else 
		  {
		    stdclass = Handle(MS_StdClass)::DownCast(atype);
		    if(!stdclass.IsNull())
		      if(!stdclass->IsNested())
			AddAction(anit,fullname, WOKBuilder_CompleteType);
		  }
	      }
	    else
	      {
		WarningMsg() << "WOKBuilder_MSTranslator::BuildPackage" 
			 << "Type " << fullname << " is not defined" << endm;
		//MSchema()->ChangeActionToFailed(theid);
		//return WOKBuilder_Failed;
	      }
	  }

	// les alias
	
	Handle(TColStd_HSequenceOfHAsciiString) aliases = thepk->Aliases();
	Handle(MS_Alias) analias;

	for(i=1; i <= aliases->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), aliases->Value(i));
	    analias = Handle(MS_Alias)::DownCast(ameta->GetType(fullname));
	    AddAction(anit,analias->Type(), WOKBuilder_Inherits);
	  }

	// les pointeurs

	
	Handle(TColStd_HSequenceOfHAsciiString) pointers = thepk->Pointers();
	Handle(MS_Pointer) apointer;

	for(i=1; i <= pointers->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), pointers->Value(i));
	    apointer = Handle(MS_Pointer)::DownCast(ameta->GetType(fullname));
	    AddAction(anit,apointer->Type(), WOKBuilder_Inherits);
	  }

	// les exceptions

	Handle(TColStd_HSequenceOfHAsciiString) expects = thepk->Excepts();
	
	for(i=1; i<=expects->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), expects->Value(i));
	    AddAction(anit,fullname, WOKBuilder_CompleteType);
	  }

	AddAction(anit,anaction->Entity()->Name(), WOKBuilder_PackageMethods); 
      }
      break;
    default:
      return WOKBuilder_Failed;

    }

  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildSchema
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildSchema(const Handle(WOKBuilder_MSAction)& anaction,
							    const Handle(WOKBuilder_Specification)& afile,
							    WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type()); 
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
		<< "Schema      : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_SchUses);

	      AddAction(anit,anaction->Entity()->Name(), WOKBuilder_SchemaTypes);

	      const Handle(MS_Schema)& aschema = MSchema()->MetaSchema()->GetSchema(anaction->Entity()->Name());
	      Handle(TColStd_HSequenceOfHAsciiString) packages = aschema->GetPackages();

	      for(i=1; i<=packages->Length(); i++)
		AddAction(anit,packages->Value(i), WOKBuilder_SchUses);

	      Handle(TColStd_HSequenceOfHAsciiString) classes  = aschema->GetClasses();

	      for(i=1; i<=classes->Length(); i++)
		{
		  AddAction(anit,MSchema()->AssociatedEntity(classes->Value(i)), WOKBuilder_SchUses);
		  AddAction(anit,classes->Value(i), WOKBuilder_SchemaType);
		}
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Schema)& aschema = MSchema()->MetaSchema()->GetSchema(anaction->Entity()->Name());
	
	AddAction(anit,anaction->Entity()->Name(), WOKBuilder_SchemaTypes);

	Handle(TColStd_HSequenceOfHAsciiString) packages = aschema->GetPackages();
	for(i=1; i<=packages->Length(); i++)
	  AddAction(anit,packages->Value(i), WOKBuilder_SchUses);

	Handle(TColStd_HSequenceOfHAsciiString) classes  = aschema->GetClasses();
	
	for(i=1; i<=classes->Length(); i++)
	  {
	    AddAction(anit,MSchema()->AssociatedEntity(classes->Value(i)), WOKBuilder_SchUses);
	    AddAction(anit,classes->Value(i), WOKBuilder_SchemaType);
	  }
      }
      break;
    default:
      break;
    }
  return WOKBuilder_Success;
}


//=======================================================================
//function : BuildInterface
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildInterface(const Handle(WOKBuilder_MSAction)& anaction,
							       const Handle(WOKBuilder_Specification)& afile,
							       WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
		<< "Interface   : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_Package);

	      AddAction(anit,anaction->Entity()->Name(), WOKBuilder_InterfaceTypes);

	      const Handle(MS_Interface)& aninter = MSchema()->MetaSchema()->GetInterface(anaction->Entity()->Name());
	      Handle(TColStd_HSequenceOfHAsciiString) packages = aninter->Packages();
	      for(i=1; i<=packages->Length(); i++)
		AddAction(anit,packages->Value(i), WOKBuilder_Package);

	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Interface)& aninter = MSchema()->MetaSchema()->GetInterface(anaction->Entity()->Name());
	
	for(i=1; i<=aninter->Uses()->Length(); i++)
	  AddAction(anit,aninter->Uses()->Value(i), WOKBuilder_Package);

	AddAction(anit,anaction->Entity()->Name(), WOKBuilder_InterfaceTypes);

	Handle(TColStd_HSequenceOfHAsciiString) packages = aninter->Packages();
	for(i=1; i<=packages->Length(); i++)
	  AddAction(anit,packages->Value(i), WOKBuilder_Package);
      }
      break;
    default:
      break;
    }
  return WOKBuilder_Success;
}


//=======================================================================
//function : BuildClient
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildClient(const Handle(WOKBuilder_MSAction)& anaction,
							    const Handle(WOKBuilder_Specification)& afile,
							    WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
		<< "Client      : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      const Handle(MS_Client)& aclient = MSchema()->MetaSchema()->GetClient(anaction->Entity()->Name());

              Handle( TColStd_HSequenceOfHAsciiString ) uses = aclient -> Uses ();

              for (  i = 1; i <= uses -> Length (); ++i  )

               AddAction (  anit, uses -> Value ( i ), WOKBuilder_Client  );

	      Handle(TColStd_HSequenceOfHAsciiString) interfaces = aclient->Interfaces();
	      for(i=1; i<=interfaces->Length(); i++)
		AddAction(anit,interfaces->Value(i), WOKBuilder_Interface);

	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Client)& aclient = MSchema()->MetaSchema()->GetClient(anaction->Entity()->Name());
	
        Handle( TColStd_HSequenceOfHAsciiString ) uses = aclient -> Uses ();

        for (  i = 1; i <= uses -> Length (); ++i  )

         AddAction (  anit, uses -> Value ( i ), WOKBuilder_Client  );

	Handle(TColStd_HSequenceOfHAsciiString) interfaces = aclient->Interfaces();
	for(i=1; i<=interfaces->Length(); i++)
	  AddAction(anit,interfaces->Value(i), WOKBuilder_Interface);
      }
      break;
    default:
      break;
    }
  return WOKBuilder_Success;
}


//=======================================================================
//function : BuildEngine
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildEngine(const Handle(WOKBuilder_MSAction)& anaction,
							    const Handle(WOKBuilder_Specification)& afile,
							    WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());

  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSTranslator::BuildEngine" 
	  << "Engine      : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      for(i=1; i<=uses->Length(); i++)
		{
		  if(strcmp("Standard", uses->Value(i)->ToCString()))
		    AddAction(anit,uses->Value(i), WOKBuilder_Interface);
		}

	      AddAction(anit,new TCollection_HAsciiString("EngineInterface"), WOKBuilder_Interface);
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Engine)& aneng = MSchema()->MetaSchema()->GetEngine(anaction->Entity()->Name());
	
	for(i=1; i<=aneng->Interfaces()->Length(); i++)
	  {
	      AddAction(anit,aneng->Interfaces()->Value(i), WOKBuilder_Interface);
	  }

	AddAction(anit,new TCollection_HAsciiString("EngineInterface"), WOKBuilder_Interface);
      }
      break;
    default:
      break;
    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildDirectUses
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildSchUses(const Handle(WOKBuilder_MSAction)& anaction,
							     const Handle(WOKBuilder_Specification)& afile,
							     WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSTranslator::BuildDirectUses" 
	  << "Sch Uses    : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid,afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_SchUses);
	      
	      for(i=1; i<=insttypes->Length(); i++)
		{
		  Handle(MS_InstClass) instclass = Handle(MS_InstClass)::DownCast(MSchema()->MetaSchema()->GetType(insttypes->Value(i)));
		  if(!instclass.IsNull())
		    AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_Instantiate);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_InstToStd);
		}
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;

	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Package)& thepk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());

	Handle(TColStd_HSequenceOfHAsciiString) uses = thepk->Uses();

	for(i=1; i<=uses->Length(); i++)
	  AddAction(anit,uses->Value(i), WOKBuilder_SchUses);

	Handle(TColStd_HSequenceOfHAsciiString) classes = thepk->Classes();
	Handle(TCollection_HAsciiString) fullname;
	const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
	Handle(MS_Type) atype;

	for(i=1; i<=classes->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), classes->Value(i));
	    
	    atype = ameta->GetType(fullname);

	    Handle(MS_InstClass) instclass = Handle(MS_InstClass)::DownCast(atype);
	    if(!instclass.IsNull())
	      {
		if(!instclass->IsNested())
		  {
		    AddAction(anit,fullname, WOKBuilder_Instantiate);
		    AddAction(anit,fullname, WOKBuilder_InstToStd);
		  }
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}


//=======================================================================
//function : BuildDirectUses
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildDirectUses(const Handle(WOKBuilder_MSAction)& anaction,
							const Handle(WOKBuilder_Specification)& afile,
							       WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSTranslator::BuildDirectUses" 
	  << "Direct use  : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid,afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_Uses);
	      
	      for(i=1; i<=insttypes->Length(); i++)
		{
		  Handle(MS_InstClass) instclass = Handle(MS_InstClass)::DownCast(MSchema()->MetaSchema()->GetType(insttypes->Value(i)));
		  if(!instclass.IsNull())
		    AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_Instantiate);
		  AddAction(anit,insttypes->Value(i), WOKBuilder_InstToStd);
		}
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;

	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Package)& thepk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());

	Handle(TColStd_HSequenceOfHAsciiString) uses = thepk->Uses();

	for(i=1; i<=uses->Length(); i++)
	  AddAction(anit,uses->Value(i), WOKBuilder_Uses);

	Handle(TColStd_HSequenceOfHAsciiString) classes = thepk->Classes();
	Handle(TCollection_HAsciiString) fullname;
	const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
	Handle(MS_Type) atype;
	Handle(MS_InstClass) instclass;

	for(i=1; i<=classes->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), classes->Value(i));
	    
	    atype = ameta->GetType(fullname);
	    if(atype->IsKind(STANDARD_TYPE(MS_InstClass)))
	      {
		instclass = Handle(MS_InstClass)::DownCast(atype);

		if(!instclass->IsNested())
		  {
		    AddAction(anit,fullname, WOKBuilder_Instantiate);
		    AddAction(anit,fullname, WOKBuilder_InstToStd);
		  }
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}




//=======================================================================
//function : BuildUses
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildUses(const Handle(WOKBuilder_MSAction)& anaction,	
							const Handle(WOKBuilder_Specification)& afile,
						       WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;
  
	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
	  << "Used        : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      for(i=1; i<=uses->Length(); i++)
		AddAction(anit,uses->Value(i), WOKBuilder_Uses);
	      for(i=1; i<=insttypes->Length(); i++)
		AddAction(anit,insttypes->Value(i), WOKBuilder_Instantiate);
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;

	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Package)& thepk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());
	
	Handle(TColStd_HSequenceOfHAsciiString) uses = thepk->Uses();
	for(i=1; i<=uses->Length(); i++)
	  AddAction(anit,uses->Value(i), WOKBuilder_Uses);
	
	Handle(TColStd_HSequenceOfHAsciiString) classes = thepk->Classes();
	Handle(TCollection_HAsciiString) fullname;
	const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
	Handle(MS_Type) atype;
	Handle(MS_InstClass) instclass;
	
	for(i=1; i<=classes->Length(); i++)
	  {
	    fullname = MS::BuildFullName(anaction->Entity()->Name(), classes->Value(i));
	    atype = ameta->GetType(fullname);
	    instclass = Handle(MS_InstClass)::DownCast(atype);
	    if(!instclass.IsNull())
	      {
		if(!instclass->IsNested())
		  AddAction(anit,fullname, WOKBuilder_Instantiate);
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildGlobEnt
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildGlobEnt(const Handle(WOKBuilder_MSAction)& anaction,	
							  const Handle(WOKBuilder_Specification)& afile,
							  WOKBuilder_MSTranslatorIterator& ) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  
  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;
  
	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
	  << "Used        : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    anaction->Entity()->SetFile(afile);
	    MSchema()->ChangeAddAction(theid, afile);
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildExecutable
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildExecutable(const Handle(WOKBuilder_MSAction)& anaction,	
							  const Handle(WOKBuilder_Specification)& afile,
							  WOKBuilder_MSTranslatorIterator& ) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  
  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;
  
	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
		<< "Executable  : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    anaction->Entity()->SetFile(afile);
	    MSchema()->ChangeAddAction(theid, afile);
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildComponent
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildComponent(const Handle(WOKBuilder_MSAction)& anaction,	
							       const Handle(WOKBuilder_Specification)& afile,
							       WOKBuilder_MSTranslatorIterator& ) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  
  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;
  
	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::Execute" 
		<< "Component   : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    anaction->Entity()->SetFile(afile);
	    MSchema()->ChangeAddAction(theid, afile);
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      break;
    default:
      return WOKBuilder_Failed;

    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildInstantiate
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildInstantiate(const Handle(WOKBuilder_MSAction)& anaction,
								 const Handle(WOKBuilder_Specification)& afile,
								 WOKBuilder_MSTranslatorIterator& anit) 
{
  Handle(MS_InstClass) instclass =  Handle(MS_InstClass)::DownCast(MSchema()->MetaSchema()->GetType(anaction->Entity()->Name()));
  
  if(!instclass.IsNull())
    {
      // InstClass
      if(! instclass->IsAlreadyDone())
	{
	  instclass->Instantiates();
	  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
	  MSchema()->ChangeAddAction(theid, afile);
	}
      
      if(anit.IsInStack(instclass->FullName(), WOKBuilder_InstToStd))
	{
	  // les ajouts ne sont necessaires que si la class est a insttostd
	  //  : Traduire la generique si l'instclass doir etre InstToStd
	  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
	  anaction->Entity()->SetFile(afile);
	  AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
	}
    }
  return WOKBuilder_Success;
}


//=======================================================================
//function : BuildPackageMethods
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildPackageMethods(const Handle(WOKBuilder_MSAction)& anaction,
								    const Handle(WOKBuilder_Specification)& afile,	
								    WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i, j;
  const Handle(MS_Package)& apk = MSchema()->MetaSchema()->GetPackage(anaction->Entity()->Name());
  Handle(MS_ExternMet) method;

  if(apk.IsNull())
    {
      ErrorMsg() << "WOKBuilder_MSTranslatorIterator::BuildPackageMethods" 
	<< anaction->Entity()->Name() << " was not found or not an interface\n" << endm;
      return WOKBuilder_Failed;
    }
  
  for(i=1; i<=apk->Methods()->Length(); i++)
    {
      method = apk->Methods()->Value(i);
      Handle(MS_HArray1OfParam) params = method->Params();
      if(!params.IsNull()) {
	for(j=1; j<=params->Length(); j++)
	  {
	    const Handle(MS_Param)& param = params->Value(j);
	    AddAction(anit,param->TypeName(), WOKBuilder_TypeUses);
	  }
      }
      Handle(MS_Param) param = method->Returns();
      if(!param.IsNull())
	AddAction(anit,param->TypeName(), WOKBuilder_TypeUses);
    }
  anaction->Entity()->SetFile(afile);
  //MSchema()->ChangeAddAction(anaction);
  return WOKBuilder_Success;
}

//=======================================================================
//function : InterfaceTypes
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildInterfaceTypes(const  Handle(WOKBuilder_MSAction)& anaction,
								    const Handle(WOKBuilder_Specification)& afile,
								    WOKBuilder_MSTranslatorIterator& anit) 
{
  Standard_Integer i, j;
  Handle(TColStd_HSequenceOfHAsciiString) asequses;
  const Handle(MS_MetaSchema)& ameta  = MSchema()->MetaSchema();
  const Handle(MS_Interface)& aninter = ameta->GetInterface(anaction->Entity()->Name());

  if(aninter.IsNull())
    {
      ErrorMsg() << "WOKBuilder_MSTranslatorIterator::BuildInterface" 
	       << anaction->Entity()->Name() << " was not found or not an interface\n" << endm;
      return WOKBuilder_Failed;
    }

  for(i=1; i<=aninter->Uses()->Length(); i++)
    {
      const Handle(MS_Package)& apack = ameta->GetPackage(aninter->Uses()->Value(i));
      asequses = apack->Uses();

      for(j=1; j<=asequses->Length(); j++)
	{
	  AddAction(anit,asequses->Value(j), WOKBuilder_DirectUses);
	}
    }
  
  Handle(TColStd_HSequenceOfHAsciiString) classes  = aninter->Classes();
  for(i=1; i<=classes->Length(); i++)
    AddAction(anit,classes->Value(i), WOKBuilder_CompleteType);
  
  Handle(TColStd_HSequenceOfHAsciiString) methods  = aninter->Methods();
  Handle(TCollection_HAsciiString)        name;
  
  anaction->Entity()->SetFile(afile);
  //MSchema()->ChangeAddAction(anaction);

  for(i=1; i<=methods->Length(); i++)
    {
      name = MS::GetEntityNameFromMethodName( methods->Value(i));
      
      if(ameta->IsPackage(name))
	{
	  AddAction(anit,name, WOKBuilder_PackageMethods);
	}
      else if(ameta->IsDefined(name))
	{
	  AddAction(anit,name, WOKBuilder_CompleteType);
	}
      else
	{
	  ErrorMsg() << "WOKBuilder_MSTranslator::BuildInterfaceTypes" 
	    << "Name " << name << " is not a package name or a type name and is exported in " << anaction->Entity()->Name() << endm;
	  return WOKBuilder_Failed;
	}
    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : SchemaTypes
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildSchemaTypes(const  Handle(WOKBuilder_MSAction)& anaction,
								 const Handle(WOKBuilder_Specification)& ,
								 WOKBuilder_MSTranslatorIterator& anit) 
{
  Standard_Integer i, j;

  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
  const Handle(MS_Schema)&   aschema = ameta->GetSchema(anaction->Entity()->Name());

  if(aschema.IsNull())
    {
      ErrorMsg() << "WOKBuilder_MSTranslatorIterator::BuildSchema" 
	       << anaction->Entity()->Name() << " was not found or not an Schema\n" << endm;
      return WOKBuilder_Failed;
    }

  Handle(TColStd_HSequenceOfHAsciiString) packages = aschema->GetPackages();

  for(i=1; i<=packages->Length(); i++)
    {
      const Handle(MS_Package)& apack = ameta->GetPackage(packages->Value(i));

      Handle(TColStd_HSequenceOfHAsciiString) aseq = apack->Classes();

      for(j=1; j<=aseq->Length(); j++)
	{
	  Handle(TCollection_HAsciiString) astr = MS::BuildFullName(apack->Name(), aseq->Value(j));
	  AddAction(anit,astr, WOKBuilder_SchemaType);
	}
    }
  
  Handle(TColStd_HSequenceOfHAsciiString) classes  = aschema->GetClasses();
  for(i=1; i<=classes->Length(); i++)
    AddAction(anit,classes->Value(i), WOKBuilder_SchemaType);
  
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildInstToStd
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildInstToStd(const Handle(WOKBuilder_MSAction)& anaction,
							       const Handle(WOKBuilder_Specification)& afile,
							       WOKBuilder_MSTranslatorIterator& anit) 
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
  Handle(MS_InstClass) instclass =  Handle(MS_InstClass)::DownCast(ameta->GetType(anaction->Entity()->Name()));
  Handle(MS_StdClass)  stdclass;

  if(!instclass.IsNull())
    {
      // InstClass
      instclass->InstToStd();
      MSchema()->ChangeAddAction(theid, afile);
    }

  Handle(MS_GenClass) genClass   = Handle(MS_GenClass)::DownCast(ameta->GetType(instclass->GenClass()));
  Handle(TColStd_HSequenceOfHAsciiString) theGenTypes = instclass->GenTypes();
  Standard_Integer i;

  if(theGenTypes->Length() > instclass->InstTypes()->Length())
    {
      ErrorMsg() << "WOKBuilder_MSTranslator::BuildInstToStd"
	       << "Wrong instantiation types number in " << instclass->FullName() << ": please remedy" << endm;
      return WOKBuilder_Failed;
    }

  for(i=1; i<=theGenTypes->Length(); i++)
    {
      AddAction(anit,instclass->InstTypes()->Value(i), WOKBuilder_TypeUses);
    }

  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildGenClass
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildGenClass(const Handle(WOKBuilder_MSAction)& anaction,
							      const Handle(WOKBuilder_Specification)& afile,
							      WOKBuilder_MSTranslatorIterator& anit)
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i,j;

  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
 
  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::BuildGenClass" 
	  << "Generic     : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      // CLE
	      Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	      
	      if(!theclass.IsNull())
		{
		  Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
		  for(i=1; i<=aseq->Length(); i++)
		    AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
		  aseq = theclass->GetUsesNames();
		  for(i=1; i<=aseq->Length(); i++) {
		    if (MSchema()->MetaSchema()->IsDefined(aseq->Value(i))) {
		      Handle(MS_InstClass) instclass = Handle(MS_InstClass)::DownCast(ameta->GetType(aseq->Value(i)));
		      if(!instclass.IsNull()) {
			AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
			AddAction(anit,aseq->Value(i), WOKBuilder_Instantiate);
			AddAction(anit,aseq->Value(i), WOKBuilder_InstToStd);
		      }
		      else {
			AddAction(anit,aseq->Value(i), WOKBuilder_TypeUses);
		      }
		    }
		  }
		}
	      // END CLE

	      // JGA
	      Handle(MS_GenClass) genclass = Handle(MS_GenClass)::DownCast(ameta->GetType(anaction->Entity()->Name()));

	      if(!genclass.IsNull())
		{
		  Handle(TColStd_HSequenceOfHAsciiString) neststds = genclass->GetNestedStdClassesName();
		  
		  for(i=1; i<=neststds->Length(); i++)
		    {
		      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(genclass->Package()->Name(),
										    neststds->Value(i));
		      Handle(MS_Class) netclass =
			Handle(MS_Class)::DownCast(ameta->GetType(fullname));
		      
		      // Add nested inheritance
		      Handle(TColStd_HSequenceOfHAsciiString) inhseq = netclass->GetInheritsNames();
		      for(j=1; j<=inhseq->Length(); j++)
			AddAction(anit,inhseq->Value(j), WOKBuilder_TypeUses);
		      // Add nested uses
		      Handle(TColStd_HSequenceOfHAsciiString) usesseq = netclass->GetUsesNames();
		      for(j=1; j<=usesseq->Length(); j++)
			AddAction(anit,usesseq->Value(j), WOKBuilder_TypeUses);
		    }
		}
	      // END JGA

	      for(i=1; i<=gentypes->Length(); i++)
		AddAction(anit,gentypes->Value(i), WOKBuilder_GenType);
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	// CLE
	Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	
	if(!theclass.IsNull())
	  {
	    Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
	    for(i=1; i<=aseq->Length(); i++)
	      AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
	    aseq = theclass->GetUsesNames();
	    for(i=1; i<=aseq->Length(); i++) {
	      if (MSchema()->MetaSchema()->IsDefined(aseq->Value(i))) {
		Handle(MS_InstClass) instclass = Handle(MS_InstClass)::DownCast(ameta->GetType(aseq->Value(i)));
		if(!instclass.IsNull()) {
		  AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
		  AddAction(anit,aseq->Value(i), WOKBuilder_Instantiate);
		  AddAction(anit,aseq->Value(i), WOKBuilder_InstToStd);
		}
		else {
		  AddAction(anit,aseq->Value(i), WOKBuilder_TypeUses);
		}
	      }
	    }
	  }
	// END CLE

	// JGA
	Handle(MS_GenClass) genclass = Handle(MS_GenClass)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	
	if(!genclass.IsNull())
	  {
	    Handle(TColStd_HSequenceOfHAsciiString) neststds = genclass->GetNestedStdClassesName();
	    
	    for(i=1; i<=neststds->Length(); i++)
	      {
		Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(genclass->Package()->Name(),
									      neststds->Value(i));
		Handle(MS_Class) netclass =
		  Handle(MS_Class)::DownCast(MSchema()->MetaSchema()->GetType(fullname));
		
		// Add nested inheritance
		Handle(TColStd_HSequenceOfHAsciiString) inhseq = netclass->GetInheritsNames();
		for(j=1; j<=inhseq->Length(); j++)
		  AddAction(anit,inhseq->Value(j), WOKBuilder_TypeUses);
		// Add nested uses
		Handle(TColStd_HSequenceOfHAsciiString) usesseq = netclass->GetUsesNames();
		for(j=1; j<=usesseq->Length(); j++)
		  AddAction(anit,usesseq->Value(j), WOKBuilder_TypeUses);
	      }
	  }
	// END JGA
	
	Handle(MS_GenClass) agenclass = Handle(MS_GenClass)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	if(!agenclass.IsNull()) {
	  Handle(MS_InstClass) instclass;
	  Handle(TColStd_HSequenceOfHAsciiString) nestedinst = agenclass->GetNestedInsClassesName();
	  for(i=1; i<=nestedinst->Length(); i++)
	    {
	      Handle(TCollection_HAsciiString) fullname = MS::BuildFullName(agenclass->Package()->Name(), nestedinst->Value(i));
	    
	      instclass = Handle(MS_InstClass)::DownCast(MSchema()->MetaSchema()->GetType(fullname));
	    
	      if(! instclass.IsNull())
		AddAction(anit,instclass->GenClass(), WOKBuilder_GenType);
	    }
	}
      }
      break;
    default:
      return WOKBuilder_Failed;
    }

  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildCompleteType
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildCompleteType(const Handle(WOKBuilder_MSAction)& anaction,
								  const Handle(WOKBuilder_Specification)& afile,
								  WOKBuilder_MSTranslatorIterator& anit)
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;
  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::BuildCompleteType" 
	  << "Complete    : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile); 

	      Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	      
	      if(!theclass.IsNull())
		{
		  Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
		  for(i=1; i<=aseq->Length(); i++)
		    AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
		}
	      
	      for(i=1; i<=inctypes->Length(); i++)
		AddAction(anit,inctypes->Value(i), WOKBuilder_TypeUses);
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	Handle(MS_Type)  thetype = ameta->GetType(anaction->Entity()->Name());
	Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(thetype);
	if(!theclass.IsNull())
	   {
	     Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
	     for(i=1; i<=aseq->Length(); i++)
	       AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
	     
	     aseq = theclass->GetUsesNames();
	     for(i=1; i<=aseq->Length(); i++)
	       AddAction(anit, aseq->Value(i), WOKBuilder_TypeUses);
	   }
	else
	  {
	    Handle(MS_Alias) thealias = Handle(MS_Alias)::DownCast(thetype);
	    if(!thealias.IsNull())
	      {
		AddAction(anit,thealias->Type(), WOKBuilder_Inherits);
	      }
	    else
	      {
		Handle(MS_Pointer) thepointer = Handle(MS_Pointer)::DownCast(thetype);
		if(!thepointer.IsNull())
		  {
		    AddAction(anit,thepointer->Type(), WOKBuilder_Inherits);
		  }
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;
    }

  return WOKBuilder_Success;
} 

//=======================================================================
//function : BuildSchemaType
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildSchemaType(const Handle(WOKBuilder_MSAction)& anaction,
								  const Handle(WOKBuilder_Specification)& afile,
								  WOKBuilder_MSTranslatorIterator& anit)
{
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;
  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;

	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::BuildSchemaType" 
	  << "Schema Type : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile); 

	      Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	      
	      if(!theclass.IsNull())
		{
		  Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
		  for(i=1; i<=aseq->Length(); i++)
		    AddAction(anit, aseq->Value(i), WOKBuilder_SchemaType);
		}
	      
	      for(i=1; i<=inctypes->Length(); i++)
		AddAction(anit,inctypes->Value(i), WOKBuilder_SchemaType);
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	const Handle(MS_Type)& thetype = ameta->GetType(anaction->Entity()->Name());
	
	Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(thetype);
	if(!theclass.IsNull())
	   {
	     Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
	     for(i=1; i<=aseq->Length(); i++)
	       AddAction(anit, aseq->Value(i), WOKBuilder_SchemaType);
	     
	     aseq = theclass->GetUsesNames();
	     for(i=1; i<=aseq->Length(); i++)
	       AddAction(anit, aseq->Value(i), WOKBuilder_SchemaType);
	   }
	else
	  {
	    Handle(MS_Alias) thealias = Handle(MS_Alias)::DownCast(thetype);
	    if(!thealias.IsNull())
	      {
		AddAction(anit,thealias->Type(), WOKBuilder_SchemaType);
	      }
	    else
	      {
		Handle(MS_Pointer) thepointer = Handle(MS_Pointer)::DownCast(thetype);
		if(!thepointer.IsNull())
		  {
		    AddAction(anit,thepointer->Type(), WOKBuilder_SchemaType);
		  }
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;
    }

  return WOKBuilder_Success;
} 

//=======================================================================
//function : BuildTypeUsed
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildTypeUsed(const Handle(WOKBuilder_MSAction)& anaction,
							      const Handle(WOKBuilder_Specification)& afile,
							      WOKBuilder_MSTranslatorIterator& anit)
{
  const Handle(MS_MetaSchema)& ameta = MSchema()->MetaSchema();
  WOKBuilder_MSActionID theid(anaction->Entity()->Name(), anaction->Type());
  Standard_Integer i;

  switch(MSActionStatus(anaction, afile))
    {
    case WOKBuilder_OutOfDate:
      {
	MSchema()->RemoveAction(theid);
      }
    case WOKBuilder_NotDefined:
      {
	Handle(TColStd_HSequenceOfHAsciiString) uses, inctypes, insttypes, gentypes;
 
	InfoMsg() << "WOKBuilder_MSEntityTranslatorIterator::BuildTypeUsed" 
	  << "Type used   : " << afile->Path()->Name() << endm;
	
	switch(Translate(anaction, afile, uses, inctypes, insttypes, gentypes))
	  {
	  case WOKBuilder_Success:
	    {
	      anaction->Entity()->SetFile(afile);
	      MSchema()->ChangeAddAction(theid, afile);

	      Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(ameta->GetType(anaction->Entity()->Name()));
	      
	      if(!theclass.IsNull())
		{
		  Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
		  
		  for(i=1; i<=aseq->Length(); i++)
		    AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
		}
	    }
	    break;
	  case WOKBuilder_Failed:
	    MSchema()->ChangeActionToFailed(theid);
	  default:
	    return WOKBuilder_Failed;
	  }
      }
      break;
    case WOKBuilder_UpToDate:
      {
	Handle(MS_Type) thetype   = ameta->GetType(anaction->Entity()->Name());

	Handle(MS_Class) theclass = Handle(MS_Class)::DownCast(thetype);
	if(!theclass.IsNull())
	  {
	    Handle(TColStd_HSequenceOfHAsciiString) aseq = theclass->GetInheritsNames();
	    
	    for(i=1; i<=aseq->Length(); i++)
	      AddAction(anit, aseq->Value(i), WOKBuilder_Inherits);
	  }
	else
	  {
	    Handle(MS_Alias) thealias = Handle(MS_Alias)::DownCast(thetype);
	    if(!thealias.IsNull())
	      {
		AddAction(anit,thealias->Type(), WOKBuilder_Inherits);
	      }
	    else
	      {
		Handle(MS_Pointer) thepointer = Handle(MS_Pointer)::DownCast(thetype);
		if(!thepointer.IsNull())
		  {
		    AddAction(anit,thepointer->Type(), WOKBuilder_Inherits);
		  }
	      }
	  }
      }
      break;
    default:
      return WOKBuilder_Failed;
    }
  return WOKBuilder_Success;
}

//=======================================================================
//function : BuildInherits
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::BuildInherits(const Handle(WOKBuilder_MSAction)& anaction,
							      const Handle(WOKBuilder_Specification)& afile,
							      WOKBuilder_MSTranslatorIterator& anit)
{
  return BuildTypeUsed(anaction, afile, anit);
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSTranslator::Execute(const Handle(WOKBuilder_MSAction)& anaction, 
							const Handle(WOKBuilder_Specification)& afile,
							WOKBuilder_MSTranslatorIterator&             anit)
{
  
  switch(anaction->Type())
    {
    case WOKBuilder_Package:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Package    : " << anaction->Entity()->Name() <<endm;
      }
      return BuildPackage(anaction, afile, anit);

    case WOKBuilder_Schema:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Schema     : " << anaction->Entity()->Name() <<endm;
      }
      return BuildSchema(anaction, afile, anit);

    case WOKBuilder_Interface:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Interface  : " << anaction->Entity()->Name() <<endm;
      }
      return BuildInterface(anaction, afile, anit);

    case WOKBuilder_Client:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Client     : " << anaction->Entity()->Name() <<endm;
      }
      return BuildClient(anaction, afile, anit);

    case WOKBuilder_Engine:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Engine     : " << anaction->Entity()->Name() <<endm;
      }
      return BuildEngine(anaction, afile, anit);

    case WOKBuilder_Executable:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Executable : " << anaction->Entity()->Name() <<endm;
      }
      return BuildExecutable(anaction, afile, anit);

    case WOKBuilder_Component:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Component  : " << anaction->Entity()->Name() <<endm;
      }
      return BuildComponent(anaction, afile, anit);

    case WOKBuilder_DirectUses:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "DirectUses : " << anaction->Entity()->Name() <<endm;
      }
      return BuildDirectUses(anaction, afile, anit);

    case WOKBuilder_SchUses:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "SchUses    : " << anaction->Entity()->Name() <<endm;
      }
      return BuildSchUses(anaction, afile, anit);

    case WOKBuilder_Uses:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Uses       : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildUses(anaction, afile, anit);

    case WOKBuilder_GlobEnt:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Uses       : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildGlobEnt(anaction, afile, anit);

    case WOKBuilder_Instantiate:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Instantiate : " << anaction->Entity()->Name() <<endm;
      }
      return BuildInstantiate(anaction, afile, anit);

    case WOKBuilder_GenType:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "GenType : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildGenClass(anaction, afile, anit);

    case WOKBuilder_InterfaceTypes:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Interface types : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildInterfaceTypes(anaction, afile, anit);

    case WOKBuilder_SchemaTypes:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Schema types : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildSchemaTypes(anaction, afile, anit);

    case WOKBuilder_PackageMethods:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Package methods : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildPackageMethods(anaction, afile, anit);

    case WOKBuilder_InstToStd:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "InstToStd : " << anaction->Entity()->Name() <<endm;
      }
      return BuildInstToStd(anaction, afile, anit);

    case WOKBuilder_CompleteType:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "CompleteType : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildCompleteType(anaction, afile, anit);

    case WOKBuilder_SchemaType:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "SchemaType : " << anaction->Entity()->Name()  <<endm;
      }
      return BuildSchemaType(anaction, afile, anit);

    case WOKBuilder_Inherits:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "Inherits : " << anaction->Entity()->Name() <<endm;
      }
      return BuildInherits(anaction, afile, anit);

    case WOKBuilder_TypeUses:
      WOK_TRACE {
	VerboseMsg()("WOK_TRANSIT") << "WOKBuilder_MSTranslatorIterator::Execute" 
				  << "TypeUses : " << anaction->Entity()->Name() <<endm;
      }
      return BuildTypeUsed(anaction, afile, anit);

    default:
      Standard_ProgramError::Raise("WOKBuilder_MSTranslator::Execute : Unknown action type");
      return WOKBuilder_Failed;

    }
}

WOKBuilder_BuildStatus WOKBuilder_MSTranslator::Execute(void)
{
// Standard_NotImplemented::Raise("WOKBuilder_MSTranslator::Execute(void) not implemented") ;
// return WOKBuilder_Failed ;
 return WOKBuilder_MSTool::Execute() ;
}
