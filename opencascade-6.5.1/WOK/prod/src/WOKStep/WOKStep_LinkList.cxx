// File:	WOKStep_LinkList.cxx
// Created:	Fri Aug  2 10:06:51 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#ifndef WNT
# include <WOKBuilder_ArchiveLibrary.hxx>
# include <WOKBuilder_SharedLibrary.hxx>
#else
# include <WOKBuilder_StaticLibrary.hxx>
# include <WOKBuilder_ImportLibrary.hxx>
#endif // WNT

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKStep_LinkList.ixx>

//---> EUG4JR
Standard_Boolean g_fForceLib;
//<--- EUG4JR

//=======================================================================
//function : WOKStep_LinkList
//purpose  : 
//=======================================================================
 WOKStep_LinkList::WOKStep_LinkList(const Handle(WOKMake_BuildProcess)& abp,
				    const Handle(WOKernel_DevUnit)& aunit, 
				    const Handle(TCollection_HAsciiString)& acode,
				    const Standard_Boolean checked, 
				    const Standard_Boolean hidden) 
   : WOKMake_Step(abp,aunit, acode, checked, hidden)
{ 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LinkList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LinkList::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_LinkList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
#ifdef WNT
	case WOKUtils_RESFile:
#endif
	case WOKUtils_ObjectFile:  result = new WOKBuilder_ObjectFile(apath);     break;
#ifndef WNT
	case WOKUtils_ArchiveFile: result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:     result = new WOKBuilder_SharedLibrary(apath);  break;
#else
	case WOKUtils_LIBFile: 	   result = new WOKBuilder_StaticLibrary(apath); break;
	case WOKUtils_IMPFile:	   result = new WOKBuilder_ImportLibrary(apath); break;
#endif // WNT
	default:  
	  return Standard_False;
	}
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }

  if(!infile->IsPhysic())
    {
      if(!strcmp("msentity", infile->ID()->Token(":", 2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	}
      return Standard_True;
    }
  return Standard_False;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : LoadDependencies
//purpose  : 
//=======================================================================
void WOKStep_LinkList::LoadDependencies()
{
  // Do not load dependencies for this step
  // to avoid AcquitExecution bug
  return;
}


//=======================================================================
//function : GetUnitLibrary
//purpose  : 
//=======================================================================
Handle(WOKMake_OutputFile) WOKStep_LinkList::GetUnitLibrary(const Handle(WOKernel_DevUnit)& aunit)
{
  Handle(WOKUtils_Path)        apath;
  Handle(WOKMake_OutputFile)   alib;
  Handle(WOKernel_File)        afile;
  Handle(WOKernel_UnitNesting) anesting;
  WOKBuilder_LibReferenceType  reftype = WOKBuilder_FullPath;
  static Handle(TCollection_HAsciiString) libtype = new TCollection_HAsciiString("library");
  Handle(TCollection_HAsciiString) libname;

  anesting = aunit->Session()->GetUnitNesting(aunit->Nesting());

  if(anesting->IsKind(STANDARD_TYPE(WOKernel_Workbench)))
    {
    }
  else
    {
      if(anesting->IsKind(STANDARD_TYPE(WOKernel_Parcel)))
	{
	  reftype = WOKBuilder_LongRef;
	}
      else
	{
	  WarningMsg() << "WOKStep_LinkList::GetUnitLibrary" 
		     << "Unknown Nesting for " << aunit->UserPathName() << endm;
	}
    }



  if(WOKernel_IsPackage(aunit) ||
     WOKernel_IsNocdlpack(aunit) ||
     WOKernel_IsToolkit(aunit) ||
     WOKernel_IsSchema(aunit) ||
     WOKernel_IsClient(aunit) ||
     WOKernel_IsInterface(aunit) )
    {
#ifndef WNT
      Handle(WOKBuilder_SharedLibrary) thelib = new WOKBuilder_SharedLibrary(aunit->Name(), apath, WOKBuilder_FullPath);
#else
      Handle(WOKBuilder_ImportLibrary) thelib = new WOKBuilder_ImportLibrary(aunit->Name(), apath, WOKBuilder_FullPath);
#endif //WNT
      libname = thelib->GetLibFileName(Unit()->Params());

      afile = Locator()->Locate(aunit->Name(), libtype, libname);

      if(afile.IsNull())
	{
	  // verifier si c'est normal
	  Standard_Boolean mustExist = aunit->SearchInFileList(Locator(),libname);
	  if (mustExist)
	    {
	      ErrorMsg() << "WOKStep_LinkList::GetUnitLibrary" 
		       << "No library (" << libname << ") found for unit " << aunit->Name() << endm;	      
	      SetFailed();
	    }
	  else {
#ifdef DEB
	    WarningMsg() << "WOKStep_LinkList::GetUnitLibrary" 
		       << "No library (" << libname << ") in unit " << aunit->Name() << endm;
#endif
	  }
	}
      else
	{
	  alib = new WOKMake_OutputFile(afile->LocatorName(), afile, 
#ifndef WNT
					new WOKBuilder_SharedLibrary(aunit->Name(), 
								     new WOKUtils_Path(afile->Path()->DirName()), 
								     reftype),
#else
					new WOKBuilder_ImportLibrary(aunit->Name(), 
								     new WOKUtils_Path(afile->Path()->DirName()), 
								     reftype),
#endif
					afile->Path());
	  
	  if(!strcmp(Unit()->Name()->ToCString(), aunit->Name()->ToCString()))
	    {
	      alib->SetMember();
	    }
	  else
	    {
	      alib->SetExtern();
	    }
	  alib->SetLocateFlag(Standard_True);
	  alib->SetReference();
	}
    }
  else
    {
      WarningMsg() << "WOKStep_LinkList::GetUnitLibrary" 
		 << "Unit " << aunit->UserPathName() << " is not known to have a library" << endm;
    }

  return alib;
}

//=======================================================================
//function : ComputeExternals
//purpose  : 
//=======================================================================
void WOKStep_LinkList::ComputeExternals(const Handle(TCollection_HAsciiString)& aunit)
{
  WOKTools_MapOfHAsciiString amap;
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Handle(TCollection_HAsciiString)        externlib, astr;
  Handle(WOKernel_File)                   afile;
  Standard_Integer j;

  externlib = new TCollection_HAsciiString("EXTERNLIB");

  afile = Locator()->Locate(aunit, externlib, externlib);
  
  if(!afile.IsNull()) {
    WOKUtils_AdmFile thefile(afile->Path());

    Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(afile->LocatorName(),
							     afile,
							     new WOKBuilder_Miscellaneous(afile->Path()),
							     afile->Path());

    infile->SetDirectFlag(Standard_False);
    infile->SetLocateFlag(Standard_True);
    
    aseq = thefile.Read();
    
    for(j=1; j<=aseq->Length(); j++) {
      astr = aseq->Value(j);

      if(!amap.Contains(astr))
	{
	  amap.Add(astr);
	  
	  Handle(TCollection_HAsciiString) extid = new TCollection_HAsciiString(Unit()->Name());
	  extid->AssignCat(":external:");
	  extid->AssignCat(astr);

	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(extid,
								      Handle(WOKernel_File)(), 
								      Handle(WOKBuilder_Entity)(),
								      Handle(WOKUtils_Path)());
	  outfile->SetPhysicFlag(Standard_False);
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetReference();
	  outfile->SetExtern();
	  outfile->SetStepID(Standard_False);
	  AddExecDepItem(infile, outfile, Standard_True);
	}
    }
  }
  return;
}



//=======================================================================
//function : GetUnitContributionCodes
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LinkList::GetUnitContributionCodes(const Handle(WOKernel_DevUnit)& aunit) const
{
  Handle(TCollection_HAsciiString) atype = Unit()->Type();
  Handle(TCollection_HAsciiString) paramname = new TCollection_HAsciiString("%WOKSteps_");
  
  paramname->AssignCat(Unit()->Type());
  paramname->AssignCat("_LinksWith");

  Handle(TCollection_HAsciiString) codes = aunit->Params().Eval(paramname->ToCString());

  if(codes.IsNull())
    {
      WarningMsg() << "WOKStep_LinkList::GetUnitContributionCodes" 
		 << "Could not eval parameter " << paramname << " in unit " << aunit->UserPathName() << endm;
    }
  return codes;
}

//=======================================================================
//function : AddWorkbenchUnitContribution
//purpose  : 
//=======================================================================
void WOKStep_LinkList::AddWorkbenchUnitContribution(const Handle(WOKMake_InputFile)& theinfile,
						    const Handle(TCollection_HAsciiString)& unit)
{  
  Standard_Boolean failed = Standard_False;
  Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(unit);

  if(!aunit.IsNull() && !theinfile.IsNull())
    {
      Handle(TCollection_HAsciiString) codes = GetUnitContributionCodes(aunit);
      
      if(codes.IsNull())
	{
	  WarningMsg() << "WOKStep_LinkList::GetUnitContribution" 
	    << "Could not eval contribution of unit : " << aunit->UserPathName() << " in link of " << Unit()->UserPathName() << endm;
	}
      else
	{
	  Standard_Integer k=1;
	  Handle(TCollection_HAsciiString) code = codes->Token(" \t", k);
	  
	  while(!code->IsEmpty())
	    {
	      Handle(WOKMake_Step) step = BuildProcess()->GetAndAddStep(aunit, code, Handle(TCollection_HAsciiString)());
	      
	      if(!step.IsNull())
		{
		  InfoMsg() << "WOKStep_LinkList::GetUnitContribution" 
		    << "Processing step " << step->UniqueName() << endm;

//---> EUG4JR
          if (   !strcmp (  DynamicType () -> Name (), "WOKStep_TransitiveLinkList"  ) ||
                 !strcmp (  DynamicType () -> Name (), "WOKStep_DirectLinkList"      )
          ) g_fForceLib = Standard_True;
//<--- EUG4JR
		  Handle(WOKMake_HSequenceOfOutputFile) outfiles = step->OutputFileList();
//---> EUG4JR
          if ( g_fForceLib ) {

           if (  Unit () -> Params ().Value ( "%UseUnitLibraries" ).IsNull () &&
                 outfiles.IsNull ()
           ) {

            outfiles = new WOKMake_HSequenceOfOutputFile ();

            outfiles -> Append (  GetUnitLibrary ( aunit )  );

           }  // end if

           g_fForceLib = Standard_False;

          }  // end if
//<--- EUG4JR		  
		  if(outfiles.IsNull())
		    {
		      ErrorMsg() << "WOKStep_LinkList::GetUnitContribution" 
			<< "Could not obtain output list of step : " << code << " in unit " << aunit->UserPathName() << endm;
		      failed = Standard_True;
		    }
		  else
		    {
		      Standard_Integer j;

		      
		      for(j=1; j<=outfiles->Length(); j++)
			{
			  Handle(WOKMake_OutputFile) outfile = outfiles->Value(j);
			  Standard_Boolean add = Standard_False;
			  
			  if(!outfile->File().IsNull())
			    {
			      Handle(WOKUtils_Path) apath = outfile->File()->Path();
			      switch(apath->Extension())
				{
				case WOKUtils_ObjectFile:      add = Standard_True;break;
#ifndef WNT
				case WOKUtils_ArchiveFile:     add = Standard_True;break;
				case WOKUtils_DSOFile:         add = Standard_True;break;
#else
				case WOKUtils_IMPFile:         add = Standard_True;break;
				case WOKUtils_RESFile:         
				case WOKUtils_LIBFile:         add = Standard_True;break;
#endif  // WNT
				default:  
				  break;
				}


			      if(!strcmp(".ImplDep", outfile->File()->Path()->ExtensionName()->ToCString()))
				{
				  add = Standard_True;
				}

			      //  }
			      if(add)
				{
				  outfile->SetReference();
				  outfile->SetExtern();
				  outfile->SetLocateFlag(Standard_True);
				  AddExecDepItem(theinfile, outfile, Standard_True);
				}
			    }
			}
		    }
		}
	      else
		{
		  WarningMsg() << "WOKStep_ComputeLinkList" 
		    << "Ignoring invalid step code " << code << " in unit " << aunit->UserPathName() << endm;
		}
	      k++;
	      code = codes->Token(" \t", k);
	    }
	}
    }
  return;
}

//=======================================================================
//function : AddParcelUnitContribution
//purpose  : 
//=======================================================================
void WOKStep_LinkList::AddParcelUnitContribution(const Handle(WOKMake_InputFile)& theinfile,
						 const Handle(TCollection_HAsciiString)& unit)
{

  Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(unit);
 
  Handle(TCollection_HAsciiString) libtype = new TCollection_HAsciiString("library");
  
#ifndef WNT
  Handle(TCollection_HAsciiString) libname = WOKBuilder_SharedLibrary::GetLibFileName(Unit()->Params(), aunit->Name());
#else
  Handle(TCollection_HAsciiString) libname = WOKBuilder_ImportLibrary::GetLibFileName(Unit()->Params(), aunit->Name());
#endif //WNT
  
  Handle(WOKernel_File) lib = Locator()->Locate(aunit->Name(), libtype, libname);
  
  if(!lib.IsNull())
    {
#ifndef WNT
      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(lib->LocatorName(), lib, 
								  new WOKBuilder_SharedLibrary(lib->Path()), 
								  lib->Path());
#else
      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(lib->LocatorName(), lib, 
								  new WOKBuilder_ImportLibrary(lib->Path()), 
								  lib->Path());
#endif //WNT
      outfile->SetReference();
      outfile->SetExtern();
      outfile->SetLocateFlag(Standard_True);
      AddExecDepItem(theinfile, outfile, Standard_True);
    }

  return;
}



//=======================================================================
//function : AddUnitContribution
//purpose  : 
//=======================================================================
void WOKStep_LinkList::AddUnitContribution(const Handle(WOKMake_InputFile)& theinfile,
					   const Handle(TCollection_HAsciiString)& unit)
{
  Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(unit);
      
  if(!aunit.IsNull() && !theinfile.IsNull())
    {
      const Handle(WOKernel_UnitNesting)& nest = aunit->Session()->GetUnitNesting(aunit->Nesting());
		
      if(nest->IsKind(STANDARD_TYPE(WOKernel_Workbench)))
	{
	  AddWorkbenchUnitContribution(theinfile, unit);
	}
      else
	{
	  AddParcelUnitContribution(theinfile, unit);
	}
    }
}

