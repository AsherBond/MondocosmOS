
#include <WOKStep_WNTK.ixx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_ShellManager.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <Standard_Stream.hxx>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif
#ifdef HAVE_STDLIB_H
# include <stdlib.h>
#endif

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKStep_WNTK
//purpose  : 
//=======================================================================
WOKStep_WNTK::WOKStep_WNTK(const Handle(WOKMake_BuildProcess)&     abp,
			   const Handle(WOKernel_DevUnit)&         aunit,
			   const Handle(TCollection_HAsciiString)& acode,
			   const Standard_Boolean                  checked,
			   const Standard_Boolean                  hidden)
  : WOKStep_TKList(abp, aunit, acode, checked, hidden) 
{
} 

  
//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_WNTK::HandleInputFile(const Handle(WOKMake_InputFile)& anItem)
{
  Handle(TCollection_HAsciiString) filePACKAGES;
  
  if(  anItem->IsLocateAble  () &&
       anItem->IsPhysic      () &&
       !anItem->File().IsNull()) 
    {
 
      filePACKAGES = Unit()->Params().Eval("%FILENAME_PACKAGES");

      if(filePACKAGES.IsNull())
	{
	  ErrorMsg() << "WOKStep_WNTK::HandleInputFile"
		   << "Could not eval parameter '%FILENAME_PACKAGES'" << endm;
	  SetFailed();
	  return Standard_False;
	}  

      if( anItem->File()->Name()->IsSameString(filePACKAGES) ) 
	{
	  anItem->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
    }
  return Standard_False;
}
 
//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_WNTK::Execute(const Handle(WOKMake_HSequenceOfInputFile)& anExecList) 
{
   if( anExecList->Length() > 1 ) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Too many input files in step" << endm;
       SetFailed();
       return;
     }

   Handle(TCollection_HAsciiString)      aStr;
   Handle(WOKernel_DevUnit)              unit;
   Handle(WOKernel_UnitNesting)          nest;
   Handle(WOKUtils_Path)                 path;

   Handle(WOKMake_InputFile) PACKAGES = anExecList->Value(1);
   
   WOKUtils_AdmFile aFile( PACKAGES->File()->Path() );
   
   Handle(TColStd_HSequenceOfHAsciiString) unitSeq = aFile.Read();
   
//HP   Unit()->Params().Set("%DebugMode",(char*)Unit()->Session()->DebugMode() ? "True" : "False"));
   const char *TrFa = Unit()->Session()->DebugMode() ? "True" : "False" ;
   Unit()->Params().Set("%DebugMode",(char *) TrFa);

   Handle(WOKernel_File) stubFile = new WOKernel_File(new TCollection_HAsciiString("__stub.c"), Unit(),
						      Unit()->GetFileType("object"));

   stubFile->GetPath();
   Unit()->Params().Set( "%StubDir", stubFile->Path()->DirName()->ToCString() );
   
   Handle(TCollection_HAsciiString) stubIn = Unit()->Params().Eval("STUBS_FileNameSrc");
   
   if(stubIn.IsNull()) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not eval parameter " << stubIn << endm;
       SetFailed();
       return;
     }

   Handle(TCollection_HAsciiString) stubOut = Unit()->Params().Eval("STUBS_FileNameDst");

   if(stubOut.IsNull()) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not eval parameter " << stubOut << endm;
       SetFailed();
       return;
     }

   Unit()->Params().Set( "%StubInput",  stubIn ->ToCString() );
   Unit()->Params().Set( "%StubOutput", stubOut->ToCString() );
   
   ofstream os( stubIn->ToCString(), ios::trunc );

   if(!os) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not create stub file" << endm;
       SetFailed();
       return;
     }
   
   os << "char* __COMPONENTS__[] = {\n";
   
   for(int i = 1; i <= unitSeq->Length(); ++i) 
     {
#ifdef WNT
       _TEST_BREAK();
#endif  // WNT 
       aStr = unitSeq->Value(i);

       aStr->LeftAdjust();
       aStr->RightAdjust();
     
       unit = Locator()->LocateDevUnit(aStr);
     
       if( unit.IsNull() ) 
	 {
	   ErrorMsg() << "WOKStep_WNTK::Execute"
		    << "Could not locate unit " << aStr << " listed in PACKAGES file" << endm;
	   SetFailed();
	   return;
	 }

       if( !(WOKernel_IsPackage(unit) || WOKernel_IsNocdlpack(unit) || WOKernel_IsDelivery(unit) || WOKernel_IsClient(unit) || WOKernel_IsSchema ( unit ))) 
	 {
	   ErrorMsg() << "WOKStep_WNTK::Execute"
		    << "Unit " << aStr << " of type " << unit->Type()
		    << " can not be listed in PACKAGES file" << endm;
	   SetFailed();
	   return;
	 }

       AddUnitContribution(PACKAGES, unit->Name());

       // Il faut linker avec les externals des uds de PACKAGES
       ComputeExternals(unit->Name());

       os << " \"" << aStr->ToCString() << "\"" ;
       os <<( ( i == unitSeq->Length() ) ? ",\n(char*)0\n};" : ",\n"  );
     }
   
   if( !os.good() ) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not create stub file" << endm;
       SetFailed();
       return;
     }
   
   os.close();
   
   Handle(WOKUtils_Shell)            sh = WOKUtils_ShellManager::GetShell();
   Handle(TCollection_HAsciiString) cmd = Unit()->Params().Eval("STUBS_COMPILE");

   if( cmd.IsNull() ) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not eval template STUBS_COMPILE" << endm;
       SetFailed();
       return;
     }

   sh->Execute(cmd);
   sh->ClearOutput();

   if(sh->Status()) 
     {
       ErrorMsg() << "WOKStep_WNTK::Execute"
		<< "Could not compile stub file" << endm;
       SetFailed();
       return;
     }
   SetSucceeded();
}

