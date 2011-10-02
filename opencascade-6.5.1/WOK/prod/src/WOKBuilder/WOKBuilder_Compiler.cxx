// File:	WOKBuilder_Compiler.cxx
// Created:	Wed Aug 23 20:09:00 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_Compiler.ixx>


#include <WOKBuilder_HSequenceOfEntity.hxx>


#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <EDL_API.hxx>

#include <stdio.h>

#include <OSD_Protection.hxx>
#include <OSD_File.hxx>
#include <WOKUtils_AdmFile.hxx>
//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN
//=======================================================================
//function : WOKBuilder_Compiler
//purpose  : 
//=======================================================================
WOKBuilder_Compiler::WOKBuilder_Compiler(const Handle(TCollection_HAsciiString)& aname, const WOKUtils_Param& params)
  : WOKBuilder_ToolInShell(aname, params)
{
}

//=======================================================================
//function : IncludeDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKBuilder_Compiler::IncludeDirectories() const 
{
  return myincdirs;
}

//=======================================================================
//function : SetIncludeDirectories
//purpose  : 
//=======================================================================
void WOKBuilder_Compiler::SetIncludeDirectories(const Handle(WOKUtils_HSequenceOfPath)& incdirs)
{
  Handle(TCollection_HAsciiString) afile;
  Handle(TCollection_HAsciiString) atempl;
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  Load();

  myincdirs = incdirs;

  atempl = new TCollection_HAsciiString("CMPLRS_IncDirective");

  for(Standard_Integer i=1; i<= IncludeDirectories()->Length(); i++)
    {
      Handle(TCollection_HAsciiString) directive;
      Params().Set("%IncDirectory", myincdirs->Value(i)->Name()->ToCString());

      directive = Params().Eval("CMPLRS_IncDirective");

      if(!directive.IsNull()) 
	{
	  astr->AssignCat(directive);
	}
      else
	{
	  WarningMsg() << "WOKBuilder_Compiler::SetIncludeDirectories" 
	    << "Could not eval database directive: CMPLRS_IncDirective" << endm;
	}
    }
  Params().Set("%IncDirectives", astr->ToCString());
  return;
}

//=======================================================================
//function : DatabaseDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKBuilder_Compiler::DatabaseDirectories() const 
{
  return mydbdirs;
}

//=======================================================================
//function : SetDatabaseDirectories
//purpose  : 
//=======================================================================
void WOKBuilder_Compiler::SetDatabaseDirectories(const Handle(WOKUtils_HSequenceOfPath)& dbdirs)
{
  Handle(TCollection_HAsciiString) afile;
  Handle(TCollection_HAsciiString) atempl;
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  Load();

  if(!dbdirs.IsNull())
    {
      mydbdirs = dbdirs;
      
      atempl = new TCollection_HAsciiString("CMPLRS_DBDirective");
      
      for(Standard_Integer i=1; i<= mydbdirs->Length(); i++)
	{
	  Handle(TCollection_HAsciiString) directive;

	  Params().Set("%DBDirectory", mydbdirs->Value(i)->Name()->ToCString());

	  directive = Params().Eval("CMPLRS_DBDirective");

	  if(!directive.IsNull()) 
	    {
	      astr->AssignCat(directive);
	    }
	  else
	    {
	      WarningMsg() << "WOKBuilder_Compiler::SetDatabaseDirectories" 
		<< "Could not eval database directive: CMPLRS_DBDirective" << endm;
	    }
	}
      Params().Set("%DBDirectives", astr->ToCString());
    }
  else
    {
      Params().Set("%DBDirectives", " ");
    }
  return;
}

//=======================================================================
//function : Compilable
//purpose  : 
//=======================================================================
Handle(WOKBuilder_Compilable) WOKBuilder_Compiler::Compilable() const
{
  return mysource;
}

//=======================================================================
//function : SetCompilable
//purpose  : 
//=======================================================================
void WOKBuilder_Compiler::SetCompilable(const Handle(WOKBuilder_Compilable)& afile)
{
  mysource = afile;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_Compiler::Execute()
{
#ifndef WNT
  static Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( " \\\n " );
  static Handle( TCollection_HAsciiString ) LF = new TCollection_HAsciiString ( "\n"     );
#else
  static Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( " \\\r\n " );
  static Handle( TCollection_HAsciiString ) LF = new TCollection_HAsciiString ( "\r\n"     );
#endif  // WNT
  int start;

#ifdef WNT
  start = 2;
#else
  start = 1;
#endif  // WNT

  Handle(TCollection_HAsciiString) astr;
  Handle(WOKBuilder_HSequenceOfEntity) aseq = new WOKBuilder_HSequenceOfEntity;
  Handle(WOKBuilder_ObjectFile) object;
  Handle(WOKBuilder_MFile)      mfile;
  

//---> EUG4YAN
 if ( !g_fCompOrLnk )
//<--- EUG4YAN
  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  Load();

  
  Params().Set("%Source",    Compilable()->Path()->Name()->ToCString());
  Params().Set("%BaseName",  Compilable()->Path()->BaseName()->ToCString());
  Params().Set("%TmpFile",   tmpnam(NULL));
  Params().Set("%OutputDir", OutputDir()->Name()->ToCString());

  astr = EvalToolTemplate(Template()->ToCString());

  WOK_TRACE {
    VerboseMsg()("WOK_CMPLRS") << "WOKBuilder_Compiler::Execute" 
			     << "Compilation line : " << endm;
    VerboseMsg()("WOK_CMPLRS") << "WOKBuilder_Compiler::Execute" 
			     << astr << endm;
  }
//---> EUG4YAN
 if ( !g_fCompOrLnk ) {
//<--- EUG4YAN
  Shell()->ClearOutput();
  Shell()->Execute(astr);
//---> EUG4YAN
 }  // end if
//<--- EUG4YAN
  myCmdLine = new TCollection_HAsciiString ( astr );
//---> EUG4YAN
 if ( !g_fCompOrLnk ) {
//<--- EUG4YAN
  Handle(TColStd_HSequenceOfHAsciiString) resseq = Shell()->Errors();

  if(Shell()->Status())
    {
      Standard_Boolean ph = ErrorMsg().PrintHeader();

      ErrorMsg() << "WOKBuilder_Compiler::Execute" << "Errors occured in Shell" << endm;
      ErrorMsg().DontPrintHeader();
      for(Standard_Integer i=start; i<= resseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_Compiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) ErrorMsg().DoPrintHeader();
      return WOKBuilder_Failed;
    }
  else
    {
      Standard_Boolean ph = InfoMsg().PrintHeader();
      InfoMsg().DontPrintHeader();
      for(Standard_Integer i=start; i<= resseq->Length(); i++)
	{
	  InfoMsg() << "WOKBuilder_Compiler::Execute" << resseq->Value(i) << endm;
	}
      if(ph) InfoMsg().DoPrintHeader();
    }
  Shell()->ClearOutput();

  SetProduction(EvalProduction());

  astr -> Clear ();

  for (  start = 1; start <= Produces () -> Length (); ++start  ) {

   Handle( WOKBuilder_Entity ) ent = Produces () -> Value ( start );

   if (  ent -> IsKind (
                 STANDARD_TYPE( WOKBuilder_ObjectFile )
                )
   ) {

    astr -> AssignCat (  ent -> Path () -> FileName ()  );
    astr -> AssignCat ( ": " );

   } else if (  ent -> IsKind (
                        STANDARD_TYPE( WOKBuilder_MFile )
                       )
          ) {

    WOKUtils_AdmFile mFile (  ent -> Path ()  );

    Handle( TColStd_HSequenceOfHAsciiString ) deps = mFile.Read ();

    for (  int i = 1; i <= deps -> Length (); ++i  ) {

     astr -> AssignCat ( NL );
     astr -> AssignCat (  deps -> Value ( i )  );

    }  // end for

    astr -> AssignCat ( LF );

   }  // end if

  }  // end for

  if (  !astr -> IsEmpty ()  ) {

//   OSD_Path dPath (  OutputDir () -> Name () -> ToCString ()  );
   TCollection_AsciiString atstr = OutputDir () -> Name () -> ToCString () ;
   OSD_Path dPath ( atstr );

   dPath.SetName (  Compilable () -> Path () -> BaseName () -> ToCString ()  );
   dPath.SetExtension ( ".d" );

   OSD_File dFile ( dPath );

   dFile.Build (
          OSD_WriteOnly,
          OSD_Protection ( OSD_RWXD, OSD_RWXD, OSD_R, OSD_R )
         );

   if (  !dFile.Failed ()  ) {

    dFile.Write (  astr -> String (), astr -> Length ()  );

    if (  dFile.Failed ()  ) {

     TCollection_AsciiString name;

     dPath.SystemName ( name );

     ErrorMsg() << "WOKBuilder_Compiler::Execute"
              << "could not create '" << new TCollection_HAsciiString ( name )
              << "'" << endm;

    }  // end if

    dFile.Close ();

   }  // end if

  }  // end if
//---> EUG4YAN
 }  // end if
//<--- EUG4YAN
  return WOKBuilder_Success;
}

