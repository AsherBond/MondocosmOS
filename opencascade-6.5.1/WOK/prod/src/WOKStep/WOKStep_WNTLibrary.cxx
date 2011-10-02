#include <WOKStep_WNTLibrary.ixx>

#include <WOKernel_File.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_DEFile.hxx>
#include <WOKBuilder_WNTLibrarian.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKMake_OutputFile.hxx>

#include <WOKTools_Messages.hxx>

WOKStep_WNTLibrary::WOKStep_WNTLibrary(const Handle(WOKMake_BuildProcess)&     abp,
				       const Handle(WOKernel_DevUnit)&         aUnit,
				       const Handle(TCollection_HAsciiString)& aCode,
				       const Standard_Boolean                  checked,
				       const Standard_Boolean                  hidden)
: WOKStep_WNTCollect(abp, aUnit, aCode, checked, hidden) 
{
}  // end constructor

Standard_Boolean WOKStep_WNTLibrary::HandleInputFile (const Handle(WOKMake_InputFile)& anItem) 
{
  Handle(WOKBuilder_Entity) res;
  Handle(WOKUtils_Path)     path;
  
 if(!anItem->File().IsNull())
   {
     path = anItem->File()->Path();

     switch (path->Extension()) 
       {
       case WOKUtils_ObjectFile:
	 res = new WOKBuilder_ObjectFile(path);
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
  return Standard_False; 
}

void WOKStep_WNTLibrary::Execute (const Handle(WOKMake_HSequenceOfInputFile)& anExecList)
{
 Standard_Boolean          fDefFile = Standard_False;
 Handle(WOKBuilder_DEFile) defFile;

 Handle(WOKernel_FileType) libType = Unit()->GetFileType("library");

 Handle(TCollection_HAsciiString) target = OutputDir()->Name();

 Handle( TCollection_HAsciiString ) uName = new TCollection_HAsciiString (  Unit () -> Name ()  );

 uName -> ChangeAll ( '.', '_' );

 target -> AssignCat ( uName );
                        
 Handle(WOKBuilder_WNTLibrarian) tool = Handle(WOKBuilder_WNTLibrarian)::DownCast(ComputeTool());

 tool->SetTargetName(target);
 
 Handle( WOKernel_FileType        ) stadmtype = Unit () -> GetFileType ( "stadmfile" );
 Handle( TCollection_HAsciiString ) name =
  new TCollection_HAsciiString (  Unit () -> Name ()  );

 name -> AssignCat (  tool -> EvalCFExt ()  );
  
 Handle( WOKernel_File ) cmdFile = new WOKernel_File (  name, Unit (), stadmtype  );

 cmdFile -> GetPath ();
 Unit () -> Params ().Set ( "%CmdFileName", cmdFile -> Path () -> Name () -> ToCString ()  );

 if(!tool->OpenCommandFile()) 
   {
     SetFailed ();
     return; 
   } 

 tool->ProduceObjectList(ComputeObjectList(anExecList));

 for(Standard_Integer i=1; i<=anExecList->Length(); ++i )
   {
     defFile = Handle(WOKBuilder_DEFile)::DownCast(anExecList->Value(i)->BuilderEntity());

     if(!defFile.IsNull()) 
       {
	 Unit()->Params().Set("%LibraryDEFile", defFile->Path()->Name()->ToCString());
	 fDefFile = Standard_True;
	 break;
       }
 
   } 

 if(!fDefFile)
   Unit()->Params().Set( "%LibraryDEFile", "" );

 if(!tool->CloseCommandFile()) 
   {
     SetFailed ();
     return;
   }

 tool->SetShell(Shell());

 switch(tool->Execute()) 
   {
   case WOKBuilder_Success: 
     {
       Standard_Integer numRes  = 0;
       Standard_Integer numProd = tool->Produces()->Length();

       Handle(WOKernel_File)      libPath;
       Handle(WOKBuilder_Entity)  outEnt;
       Handle(WOKMake_OutputFile) outFile;

       for (Standard_Integer i=1; i<=numProd; ++i) 
	 {
	   outEnt = tool->Produces()->Value (i);

	   libPath = new WOKernel_File (outEnt->Path()->FileName(), Unit(), libType);
	   libPath->GetPath();
    
	   if(outEnt->Path()->Exists())
	     {
	       ++numRes;
	       outEnt->Path()->MoveTo(libPath->Path()); 

	       outFile = new WOKMake_OutputFile(libPath->LocatorName(), libPath, outEnt, libPath->Path());

	       outFile->SetLocateFlag(Standard_True);
	       outFile->SetProduction();

	       for (Standard_Integer j=1; j<=anExecList->Length(); ++j)
		 AddExecDepItem(anExecList->Value(j), outFile, Standard_True);

	     }  
	 }

       if(numRes != numProd)
   
	 WarningMsg() << "WOKStep_WNTLibrary :: Execute"
	   << tool -> Produces () -> Value ( 1 ) -> Path () -> FileName ()
	   << " does not contain exported symbols" << endm;
       
       SetSucceeded ();
     }
     break;
   case WOKBuilder_Failed:
     SetFailed();
     break;
   default: break;
   }
}

