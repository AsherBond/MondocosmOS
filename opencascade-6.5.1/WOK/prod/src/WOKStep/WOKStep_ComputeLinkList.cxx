// File:	WOKStep_ComputeLinkList.cxx
// Created:	Tue Sep  3 20:45:03 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>



#include <TColStd_HSequenceOfHAsciiString.hxx>


#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_Miscellaneous.hxx>

#ifndef WNT
# include <WOKBuilder_ArchiveLibrary.hxx>
# include <WOKBuilder_SharedLibrary.hxx>
#else
# include <WOKNT_WNT_BREAK.hxx>
# include <WOKBuilder_StaticLibrary.hxx>
# include <WOKBuilder_ImportLibrary.hxx>
#endif // WNT

#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKStep_ComputeLinkList.ixx>

//=======================================================================
//function : WOKStep_ComputeLinkList
//purpose  : 
//=======================================================================
WOKStep_ComputeLinkList::WOKStep_ComputeLinkList(const Handle(WOKMake_BuildProcess)& abp,
						 const Handle(WOKernel_DevUnit)& aunit, 
						 const Handle(TCollection_HAsciiString)& acode, 
						 const Standard_Boolean checked, 
						 const Standard_Boolean hidden) 
  : WOKStep_LinkList(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ComputeLinkList::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ComputeLinkList::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_ComputeLinkList::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKUtils_Path)     apath;
  Handle(WOKBuilder_Entity) result;
  
  if(infile->IsStepID())
    return Standard_True;

  if(!infile->IsPhysic())
    return Standard_True;

  if(!infile->File().IsNull())
    {

      if(!strcmp(".ImplDep", infile->File()->Path()->ExtensionName()->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  result = new WOKBuilder_Miscellaneous(infile->File()->Path());
	  infile->SetBuilderEntity(result);
	  return Standard_True;
	}

      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_ObjectFile:      result = new WOKBuilder_ObjectFile(apath);      break;
#ifndef WNT
	case WOKUtils_ArchiveFile:     result = new WOKBuilder_ArchiveLibrary(apath);  break;
	case WOKUtils_DSOFile:         result = new WOKBuilder_SharedLibrary(apath);   break;
#else
	case WOKUtils_IMPFile:         result = new WOKBuilder_ImportLibrary(apath);   break;
	case WOKUtils_RESFile:         
	case WOKUtils_LIBFile:         result = new WOKBuilder_StaticLibrary(apath);   break;
#endif  // WNT
	default:  
	  return Standard_False;
	}
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ComputeLinkList::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) {
  Standard_Integer i,j;
  Standard_Boolean failed = Standard_False;

  Handle(TColStd_HSequenceOfHAsciiString) deplist, thelist = new TColStd_HSequenceOfHAsciiString;
  Handle(WOKMake_InputFile) theinfile;

  WOKTools_MapOfHAsciiString excludemap;

  if(WOKernel_IsToolkit(Unit()))
    {
      Handle(TCollection_HAsciiString) pkgstype = new TCollection_HAsciiString("PACKAGES");
      Handle(TCollection_HAsciiString) PACKAGESname = Unit()->Params().Eval("%FILENAME_PACKAGES");
      Handle(WOKernel_File) PACKAGES = Locator()->Locate(Unit()->Name(), pkgstype, PACKAGESname);

      if(PACKAGES.IsNull())
	{
	  ErrorMsg() << "WOKStep_ComputeLinkList::Execute"
		   << "Could not find PACKAGES file for toolkit : " << Unit()->Name() << endm;
	  SetFailed();
	  return;
	}
      else
	{
	  WOKUtils_AdmFile afile(PACKAGES->Path());
	  Handle(TColStd_HSequenceOfHAsciiString) udsoftk;
	  
	  udsoftk = afile.Read();
	  
	  if(udsoftk.IsNull())
	    {
	      ErrorMsg() << "WOKStep_ComputeLinkList::Execute" 
		       << "Could not read file " << PACKAGES->Path()->Name() << endm;
	      SetFailed();
	      return;
	    }
	  
	  Standard_Integer j;
	  for(j=1; j<=udsoftk->Length(); j++)
	    {
	      
	      const Handle(WOKernel_DevUnit)& aunit = Locator()->LocateDevUnit(udsoftk->Value(j));
	      
	      if(aunit.IsNull())
		{
		  ErrorMsg() << "WOKStep_ComputeLinkList::Execute" 
			   << "Unknown unit (" << udsoftk->Value(j) << ") listed in PACKAGES of : " << Unit()->Name() << endm;
		  SetFailed();
		  return;
		}
	      else
		excludemap.Add(aunit->Name());
	        ComputeExternals(aunit->Name());
	    }
	}
    }


  for(i=1; i<=execlist->Length(); i++)
    {
#ifdef WNT
      _TEST_BREAK();
#endif  // WNT
      Handle(WOKMake_InputFile) infile = execlist->Value(i);

      if(infile->IsPhysic())
	{
	  if(infile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_Miscellaneous)))
	    {
	      theinfile = infile;
	      Handle(TColStd_HSequenceOfHAsciiString) list;
	      
	      list = Unit()->ReadImplDepFile(infile->File()->Path(), Locator());
	      
	      for(j=1; j<=list->Length(); j++)
		thelist->Append(list->Value(j));
	    }
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(infile);
	  
	  outfile->SetReference();
	  outfile->SetExtern();
	  
	  Handle(WOKernel_DevUnit) unit = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
	  if(!unit.IsNull())
	    {
	      if(!strcmp(unit->Name()->ToCString(), Unit()->Name()->ToCString()))
		outfile->SetMember();
	    }
	  
	  AddExecDepItem(infile, outfile, Standard_True);
	}
      else 
	{
	  Handle(TCollection_HAsciiString) aunit  = infile->ID()->Token(":", 1);
	  Handle(TCollection_HAsciiString) atype  = infile->ID()->Token(":", 2);
	  Handle(TCollection_HAsciiString) aname  = infile->ID()->Token(":", 3);
	  
	  if(!strcmp(atype->ToCString(), "external"))
	    {
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(infile);
	      outfile->SetReference();
	      
	      AddExecDepItem(infile, outfile, Standard_True);
	    }
	  else if(!strcmp(atype->ToCString(), "mslibrary"))
	    {
	      Handle(WOKernel_DevUnit) unit = Locator()->LocateDevUnit(aunit);

	      if(unit.IsNull())
		{
		  ErrorMsg() << "WOKStep_ComputeExecList::Execute" 
			   << "Cannot find the uses library unit " << aunit << endm;
		  SetFailed();
		  return;
		}
	      else
		{
		  Handle(WOKMake_OutputFile) outfile = GetUnitLibrary(unit);
		  
		  if(!outfile.IsNull())
		    AddExecDepItem(infile, outfile, Standard_True);
		  else
		    {
//                      cout << "WOKStep_ComputeLinkList::Execute GetUnitLibrary returns outfile.IsNull() ==> SetFailed()/return was suppressed(JR)" << endl ;
//		      SetFailed();
//		      return;
		    }
		}
	    }
	}
    }

  if(theinfile.IsNull()) 
    {
      Handle(WOKMake_HSequenceOfInputFile) theinfiles = InputFileList();
      for (Standard_Integer k=1; theinfile.IsNull() && (k<= theinfiles->Length()) ; k++) {
	Handle(WOKMake_InputFile) aninfile = theinfiles->Value(k);
	if(!aninfile->File().IsNull()) {
	  if(!strcmp(".ImplDep", aninfile->File()->Path()->ExtensionName()->ToCString())) {
	    theinfile = aninfile;
	  }
	}
      }
      if (!theinfile.IsNull()) {
	theinfile->File()->GetPath();
	Handle(TColStd_HSequenceOfHAsciiString) list = Unit()->ReadImplDepFile(theinfile->File()->Path(), Locator());
	for(j=1; j<=list->Length(); j++)
	  thelist->Append(list->Value(j));
      }
      else {
	ErrorMsg() << "WOKStep_ComputeExecList::Execute" 
	  << "Cannot find the Input ImplDep file" << endm;
	SetFailed();
	return;
      }
    }
  
  Handle(TCollection_HAsciiString) thename = new TCollection_HAsciiString(Unit()->Name());

  if(!SubCode().IsNull())
    {
      thename->AssignCat("_");
      thename->AssignCat(SubCode());
    }

  deplist = ComputeDependency( thename, thelist);
  
  if(deplist.IsNull()) 
    {
      SetFailed();
      return;
    }


  for(j=1; j<=deplist->Length(); j++)
    {
      ComputeExternals(deplist->Value(j));
    }

#ifdef WNT
  // sur NT il faut linker la DLL du PK avec ses externals
  ComputeExternals(Unit()->Name());
#endif


    for(i=deplist->Length(); i>=1; i--)

      {
#ifdef WNT
	_TEST_BREAK();
#endif  // WNT

	const Handle(TCollection_HAsciiString)& adep = deplist->Value(i);

	if(!excludemap.Contains(adep))
	  {
	    AddUnitContribution(theinfile, adep);
	  }
      }
  
  if(failed) SetFailed();
  else       SetSucceeded();
  return;
}
