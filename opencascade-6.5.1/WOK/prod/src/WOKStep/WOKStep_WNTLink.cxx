
#define STRICT

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_StaticLibrary.hxx>
#include <WOKBuilder_ImportLibrary.hxx>
#include <WOKBuilder_ExportLibrary.hxx>
#include <WOKBuilder_DEFile.hxx>
#include <WOKBuilder_WNTLinker.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKMake_OutputFile.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKStep_DLLink.hxx>
#include <WOKStep_EXELink.hxx>

#include <WOKStep_WNTLink.ixx>


#ifdef WNT
#include <windows.h>
#endif // WNT

//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN

//=======================================================================
//function : WOKStep_WNTLink
//purpose  : 
//=======================================================================
WOKStep_WNTLink::WOKStep_WNTLink(const Handle(WOKMake_BuildProcess)&     abp,
				 const Handle(WOKernel_DevUnit)&         aUnit,
				 const Handle(TCollection_HAsciiString)& aCode,
				 const Standard_Boolean                  checked,
				 const Standard_Boolean                  hidden)
  : WOKStep_WNTCollect(abp, aUnit, aCode, checked, hidden) 
{
}  // end constructor

//=======================================================================
//function : 
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_WNTLink::HandleInputFile(const Handle(WOKMake_InputFile)& anItem)
{
  
  Handle(WOKBuilder_Entity) res;
  Handle(WOKUtils_Path)     path;
  
  if(!anItem->File().IsNull()) 
    {
      path = anItem->File()->Path();
doHandle:      
      switch(path->Extension()) 
	{
	case WOKUtils_RESFile:
	case WOKUtils_ObjectFile:
	  res = new WOKBuilder_ObjectFile(path);
	  break;
	case WOKUtils_LIBFile:
	  if(!path->BaseName()->IsSameString(Unit()->Name()))
	    res = new WOKBuilder_StaticLibrary(path);
	  else
	    return Standard_False;
	  break;
	case WOKUtils_IMPFile:
	  res = new WOKBuilder_ImportLibrary(path);
	  break;
	case WOKUtils_EXPFile:
	  res = new WOKBuilder_ExportLibrary(path);
	  break;
	case WOKUtils_DEFile:
	  if(path->BaseName()->IsSameString(Unit()->Name()))
	    res = new WOKBuilder_DEFile(path);
	  else
	    return Standard_False;
	  break;
	default:
	  return Standard_False;
	}

      anItem->SetBuilderEntity(res);
      anItem->SetDirectFlag(Standard_True);
      
      return Standard_True;
    }
  else if (!anItem->IsPhysic())
    return Standard_True;
  else {

   path = new WOKUtils_Path (  anItem -> ID ()  );
   goto doHandle;
  }  // end else
 return Standard_False; 
                                    
}


//=======================================================================
//function : ComputeExternals
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKStep_WNTLink::ComputeExternals(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString)  externals = new TColStd_HSequenceOfHAsciiString;

  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile) infile = execlist->Value(i);
      
      if(!infile->IsPhysic())
	{
	  if(!strcmp("external", infile->ID()->Token(":", 2)->ToCString()))
	    {
	      Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString("%");
	      astr->AssignCat(infile->ID()->Token(":", 3));
	      
	      Handle(TCollection_HAsciiString) external = Unit()->Params().Eval(astr->ToCString());

	      if(external.IsNull())
		{
		  WarningMsg() << "WOKStep_WNTLink::ComputeExternals" 
			     << "Skipped external " << infile->ID()->Token(":", 3) << "; could not eval  : " << astr << endm;
		}
	      else
		{
		  externals->Append(external);
		}
	    }
	}
    }
  return externals;
}


//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
//void WOKStep_WNTLink::Execute(const Handle(WOKMake_HSequenceOfInputFile)& anExecList)
void WOKStep_WNTLink::Execute(const Handle(WOKMake_HSequenceOfInputFile)&
#ifdef WNT
                              anExecList
#endif
                             )
{
#ifdef WNT
  
  Handle(WOKBuilder_DEFile)        defFile;
  Handle(WOKBuilder_ExportLibrary) expLib;
  Handle(WOKernel_FileType)        libType;


  if(IsKind(STANDARD_TYPE(WOKStep_DLLink)))
    {
      libType  = Unit()->GetFileType("library");
    }
  else if(IsKind(STANDARD_TYPE(WOKStep_EXELink)))
    {
      libType  = Unit()->GetFileType("executable");
    }

  if(libType.IsNull())
    {
      WarningMsg() << "WOKStep_WNTLink::Execute" 
		 << "Unknown link : library is produced" << endm;
      libType  = Unit()->GetFileType("library");
    }
  
  Handle(WOKBuilder_WNTLinker) tool =  Handle(WOKBuilder_WNTLinker)::DownCast(ComputeTool());
  
  myTarget = new TCollection_HAsciiString(OutputDir()->Name());

  Handle( TCollection_HAsciiString ) uName = new TCollection_HAsciiString (
                                                  SubCode ().IsNull () ? Unit () -> Name ()
                                                                       : SubCode ()
                                                 );
  uName -> ChangeAll ( '.', '_' );

  myTarget -> AssignCat ( uName );
  
  tool->SetTargetName(myTarget);
  
  Unit()->Params().Set("%DebugMode",Unit()->Session()->DebugMode() ? "True" : "False");

  Handle(TCollection_HAsciiString) exetype = Unit()->Params().Eval("%LINK_ExeType");

  if(exetype.IsNull())
    {
      Unit()->Params().Set("%LinkSubsystem", "CONSOLE");
    }
  else
    {
      Unit()->Params().Set("%LinkSubsystem", exetype->ToCString());
    }


  Handle( WOKernel_FileType        ) stadmtype = Unit () -> GetFileType ( "stadmfile" );
  Handle( TCollection_HAsciiString ) name =
   new TCollection_HAsciiString (  Unit () -> Name ()  );

  if (  !SubCode ().IsNull ()  ) {

   name -> AssignCat ( "_"          );
   name -> AssignCat (  SubCode ()  );

  }  // end if

  name -> AssignCat (  tool -> EvalCFExt ()  );
  
  Handle( WOKernel_File ) cmdFile = new WOKernel_File (  name, Unit (), stadmtype  );

  cmdFile -> GetPath ();
  Unit () -> Params ().Set ( "%CmdFileName", cmdFile -> Path () -> Name () -> ToCString ()  );

  if(!tool->OpenCommandFile()) 
    {
      SetFailed();
      return; 
    }

  tool->ProduceObjectList(ComputeObjectList(anExecList));
  tool->ProduceLibraryList(ComputeLibraryList(anExecList));
  tool->ProduceExternList(ComputeExternals(anExecList));

  for(int i = 1; i <= anExecList->Length(); ++i) 
    {
      defFile = Handle(WOKBuilder_DEFile)::DownCast(anExecList->Value(i)->BuilderEntity());

      if(!defFile.IsNull()) 
	{
	  tool->ProduceDEFile(defFile);
	  break;
	}
    } 

  if(WOKernel_IsToolkit(Unit()))
    {
      for(int i = 1; i <= anExecList->Length(); ++i) 
	{
	  expLib = Handle(WOKBuilder_ExportLibrary)::DownCast(anExecList->Value(i)->BuilderEntity());
	  
	  if(!expLib.IsNull()) 
	    {
	      Handle(WOKBuilder_HSequenceOfLibrary) expSeq = new WOKBuilder_HSequenceOfLibrary();
	      
	      expSeq->Append(expLib);
	      tool->ProduceLibraryList(expSeq);
	      break;
	      
	    }
	}
    }

  if(!WOKernel_IsExecutable(Unit())) 
    {
      DWORD                                   dwLen;
      Handle(TCollection_HAsciiString)        stubName;
      Handle(TCollection_HAsciiString)        buff;
      Handle(TColStd_HSequenceOfHAsciiString) seq = new TColStd_HSequenceOfHAsciiString;
      
      Handle(TCollection_HAsciiString) nodefstub = Unit()->Params().Eval("%LINK_NoDefStub");

      if(nodefstub.IsNull())
	{
	  stubName = Unit()->Params().Eval(WOKernel_IsToolkit(Unit()) ? "%STUBS_tkMain" : "%STUBS_uMain");
	  dwLen    = ExpandEnvironmentStrings(stubName->ToCString(), NULL, 0);

//POP
	  char* buffer = (char *) Standard::Allocate(dwLen+1);
//	  char* buffer = (char *) aStorageManager.Allocate(dwLen+1);
//POP
	  memset(buffer, 0, dwLen+1);
	  
	  ExpandEnvironmentStrings(stubName->ToCString(), buffer, dwLen);

      if (  buffer[ 0 ] == '/' && buffer[ 1 ] == '/'  )

       buffer[ 0 ] = buffer[ 1 ] = '\\';


	  buff     = new TCollection_HAsciiString(buffer);

//POP
	  //Standard::Free((void*&)buffer,dwLen+1);
	  Standard::Free((void*&)buffer);
//POP
	  seq->Append(buff);

	}
      
      stubName = Unit()->Params().Eval("%STUBS_Library");
      
      dwLen = ExpandEnvironmentStrings(stubName->ToCString(), NULL, 0);

//POP 
      char* buffer = (char *) Standard::Allocate(dwLen+1);
//      char* buffer = (char *) aStorageManager.Allocate(dwLen+1);
//POP

	  memset(buffer, 0, dwLen+1);
	  
      ExpandEnvironmentStrings(stubName->ToCString(), buffer, dwLen);
      
      if (  buffer[ 0 ] == '/' && buffer[ 1 ] == '/'  )

       buffer[ 0 ] = buffer[ 1 ] = '\\';

	  buff  = new TCollection_HAsciiString(buffer);
//POP
	  Standard::Free((void*&)buffer);
//POP
	  seq->Append(buff);
      

      if(WOKernel_IsToolkit(Unit()))
	{
	  

	  Handle(WOKernel_File) stubFile = new WOKernel_File(Unit()->Session()->DebugMode() ?
							       new TCollection_HAsciiString("_debug_stub.obj")
							     : new TCollection_HAsciiString("_release_stub.obj"),
							     Unit(),
							     Unit()->GetFileType("object"));
	  
	  stubFile->GetPath();
	  Unit()->Params().Set("%StubDir", stubFile->Path()->DirName()->ToCString());
	  
	  Handle(TCollection_HAsciiString) stubName = Unit()->Params().Eval("STUBS_FileNameDst");
	  
	  if(!stubName.IsNull()) seq->Append(stubName);
	}
      
      tool->ProduceExternList(seq);
    } 

  if(!tool->CloseCommandFile())
    {
      SetFailed();
      return;
    }

  tool->SetShell(Shell());
  
  switch(tool->Execute()) 
    {
    case WOKBuilder_Success:
//---> EUG4YAN
     if ( !g_fCompOrLnk )
//<--- EUG4YAN
      {
	Handle(WOKernel_File    ) libPath;
	Handle(WOKBuilder_Entity) outEnt;
	Handle(WOKMake_OutputFile) outFile;
	
	for(int i = 1; i <= tool->Produces()->Length(); ++i) 
	  {
	    outEnt  = tool->Produces()->Value(i);

	    if(outEnt->Path()->Exists())
	      {
		if(outEnt->IsKind(STANDARD_TYPE(WOKBuilder_ExportLibrary)))
		  {
		    outEnt->Path()->RemoveFile();
		    continue;
		  }

		libPath = new WOKernel_File(outEnt->Path()->FileName(), Unit(), libType);

		libPath->GetPath();
    
		outEnt->Path()->MoveTo(libPath->Path()); 
dummyStepFile:
		outFile = new WOKMake_OutputFile(libPath->LocatorName(), libPath, outEnt, libPath->Path());
		
		outFile->SetLocateFlag(Standard_True);
		outFile->SetProduction();
		
		for(int j = 1; j <= anExecList->Length(); ++j)
		  AddExecDepItem(anExecList->Value(j), outFile, Standard_True);

	      }
	    else if(WOKernel_IsToolkit(Unit()) && outEnt->Path()->Extension() == WOKUtils_LIBFile)
	      {
		libPath = new WOKernel_File(outEnt->Path()->FileName(), Unit(), libType);
		libPath->GetPath();
		goto dummyStepFile;
	      }
	  }
	SetSucceeded();
      }
      break;
    case WOKBuilder_Failed:
      SetFailed();
      break;
    }
//---> EUG4YAN
     if ( !g_fCompOrLnk )
//<--- EUG4YAN
  InfoMsg() << "WOKStep_WNTLink::Execute"
	  << "------------" << endm  << "" << endm;
  
#else
  ErrorMsg() << "WOKStep_WNTLink::Execute"
	   << "Step WOKStep_WNTLink is available only on WNT platforms." << endm;
  SetFailed();
#endif // WNT
}
