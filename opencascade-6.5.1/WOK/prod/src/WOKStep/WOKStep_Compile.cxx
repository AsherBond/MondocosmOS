// File:	WOKStep_Compile.cxx
// Created:	Tue Aug 29 21:40:43 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HArray2OfInteger.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#ifndef WNT
# include <WOKUtils_RemoteShell.hxx>
#else
# include <WOKUtils_Shell.hxx>
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKBuilder_Compiler.hxx>
#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_MFile.hxx>
#include <WOKBuilder_CompilerIterator.hxx>
#include <WOKBuilder_HSequenceOfToolInShell.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_Compile.ixx>

#include <OSD_Protection.hxx>
#include <OSD_File.hxx>

//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN

//=======================================================================
//function : WOKStep_Compile
//purpose  : 
//=======================================================================
WOKStep_Compile::WOKStep_Compile(const Handle(WOKMake_BuildProcess)& abp,
				 const Handle(WOKernel_DevUnit)& aunit, 
				 const Handle(TCollection_HAsciiString)& acode, 
				 const Standard_Boolean checked, 
				 const Standard_Boolean hidden) 
: WOKStep_ProcessStep(abp,aunit, acode, checked, hidden), 
  myiterator(new TCollection_HAsciiString("CMPLRS"), aunit->Params())
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Compile::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Compile::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;    
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKStep_Compile::Init()
{
  if(IsToExecute())
    {

      WOKStep_ProcessStep::Init();
      // Set du debug mode
      if(Unit()->Session()->DebugMode()) 
	Unit()->Params().Set("%DebugMode", "True");
      else
	Unit()->Params().Set("%DebugMode", "False");
      
#ifdef WNT
      Handle(WOKernel_FileType) objtype = Unit()->FileTypeBase()->Type("object");
      Handle(TCollection_HAsciiString) debugDef = new TCollection_HAsciiString();
    
      Handle(WOKernel_File) pdbFile = new WOKernel_File(Unit()->Name(), Unit(), objtype);
      pdbFile->GetPath();
      Unit()->Params().Set("%PDBName", pdbFile->Path()->Name()->ToCString());
#endif  // WNT
  
      if(myiterator.LoadGroup())
	{
	  ErrorMsg() << "WOKStep_Compile::Init"
	    << "Could not load compilers definition" << endm;
	  SetFailed();
	  return;
	}
    }
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Compile::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
    }
  else if(!infile->LastPath().IsNull())
    {
      apath = infile->LastPath();
    }

  if(!apath.IsNull())
    {
      if(myiterator.IsTreatedExtension(apath->ExtensionName()))
	{
	  result = new WOKBuilder_Compilable(apath);
	}
      else
	return Standard_False;
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }  
  return Standard_False;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_Compile::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i,j;
  Handle(WOKMake_InputFile) infile;
  Handle(WOKMake_OutputFile) outfile;
  Handle(WOKMake_HSequenceOfInputFile) fails = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_HSequenceOfInputFile) succeeds = new WOKMake_HSequenceOfInputFile;
  Handle(WOKernel_FileType) objtype = Unit()->FileTypeBase()->Type("object");
  Handle(WOKernel_FileType) mtype   = Unit()->FileTypeBase()->Type("mfile");
  Handle(WOKernel_File) aoutfile;

  Handle(WOKUtils_HSequenceOfPath) incdirs = ComputeIncDirectories();
  Handle(WOKUtils_HSequenceOfPath) dbdirs  = ComputeDatabaseDirectories();

  // Set du debug mode
  // Obtention d'un shell
  Handle(WOKUtils_Shell) ashell = Shell();

  ashell->Lock();

  myiterator.Init(ashell, OutputDir(), incdirs, dbdirs);
#ifndef WNT
  static Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( "\n" );
#else
  static Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( "\r\n" );
#endif  // WNT
  Handle( TCollection_HAsciiString ) str = new TCollection_HAsciiString ();

  for(j=1; j<=execlist->Length(); j++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT

      infile = execlist->Value(j);
      
      Handle(WOKBuilder_Compilable) compilable = Handle(WOKBuilder_Compilable)::DownCast(infile->BuilderEntity());
//---> EUG4YAN
 if ( !g_fCompOrLnk )
//<--- EUG4YAN
      if(infile->File()->Nesting()->IsSameString(Unit()->FullName()))
	{
	  InfoMsg() << "WOKStep_Compile::Execute" << "-------> " << infile->File()->Name() << endm;
	}
      else
	{
	  InfoMsg() << "WOKStep_Compile::Execute" << "-------> " << infile->File()->UserPathName() << endm;
	}
      
      switch(myiterator.Execute(compilable))
	{
	case WOKBuilder_Success:
	  WOK_TRACE {
	    if(VerboseMsg()("WOK_COMPILE").IsSet())
	      {
		VerboseMsg() << "WOKStep_Compile::Execute" 
			   << compilable->Path()->Name() << " produces : " << endm;
		if (!myiterator.Produces().IsNull()) {
		  for(i=1; i<=myiterator.Produces()->Length(); i++)
		    {
		      VerboseMsg() << "WOKStep_Compile::Execute"
			<< "\t\t" << myiterator.Produces()->Value(i)->Path()->Name() << endm;
		    }
		}
	      }
	  }

          if (  !myiterator.CmdLine ().IsNull ()  ) {

           Standard_Integer                   i, j;
           Handle( TCollection_HAsciiString ) s = new TCollection_HAsciiString (
                                                       myiterator.CmdLine ()
                                                      );

           char const* ptr = s -> ToCString ();

           for ( i = 0, j = -1; i < s -> Length (); ++i, ++ptr )

            if (  ( *ptr == '\r' && i && *( ptr - 1 ) != '\\' ) ||
                  ( *ptr == '\n' && i && *( ptr - 1 ) != '\\' ) ||
                  *ptr == '>'                                   ||
                  *ptr == ';'
            ) {

             j = i;
             break;

            }  // end if

           if ( j != -1 ) {

            s = s -> SubString ( 1, j );

            if (  !s.IsNull () && !s -> IsEmpty ()  ) {

             s   -> RemoveAll ( '\\' );
#ifdef WNT
             s   -> RemoveAll ( '\r' );
#endif  // WNT
             s   -> RemoveAll ( '\n' );
             str -> AssignCat (   s  );
             str -> AssignCat (   NL );
           
            }  // end if

           }  // end if
          
          }  // end if


//---> EUG4YAN
 if ( !g_fCompOrLnk ) {
//<--- EUG4YAN
	  TreatOutput(infile,myiterator.Produces()); 

	  succeeds->Append(infile);
//---> EUG4YAN
 }  // end if
//<--- EUG4YAN
	  break;
	case WOKBuilder_Failed:
	  fails->Append(infile);
	  ErrorMsg() << "WOKStep_Compile::Execute" << "Failed    : " << infile->File()->Name() << endm;           
	  break;
        default: break;
	}
    }

  ashell->UnLock();

  if(execlist->Length() == 0)
    {
      SetUptodate();
      return;
    }
  
  if(fails->Length())
    {
      InfoMsg() << "WOKStep_Compile::Execute" 
	      << "----------------------- Compilation Report -----------------------" << endm;

      for(i=1; i<= fails->Length(); i++)
	{
	  InfoMsg() << "WOKStep_Compile::Execute" 
		  << "Failed : " << fails->Value(i)->File()->UserPathName() << endm;
	}
       InfoMsg() << "WOKStep_Compile::Execute" 
	       << "-----------------------------------------------------------------" << endm;
    }

 if (  g_fCompOrLnk && !str -> IsEmpty ()  ) {

  Handle( TCollection_HAsciiString ) s = new TCollection_HAsciiString (  Unit () -> Name ()  );

  if (  !SubCode ().IsNull ()  ) {

   s -> AssignCat ( "_"          );
   s -> AssignCat (  SubCode ()  );

  }  // end if

  s -> AssignCat ( ".comp" );
  Handle( WOKernel_File ) stadm = new WOKernel_File (
                                       s, Unit (), Unit () ->
                                                    GetFileType ( "stadmfile" )
                                      );

  stadm -> GetPath ();

//  OSD_Path p (  stadm -> Path () -> Name () -> ToCString ()  );
  TCollection_AsciiString atstr = stadm -> Path () -> Name () -> ToCString () ;
  OSD_Path p ( atstr );
  OSD_File f ( p );

  f.Build (
   OSD_WriteOnly, OSD_Protection ( OSD_RWXD, OSD_RWXD, OSD_R, OSD_R )
  );

  if (  !f.Failed ()  ) {

   f.Write (  str -> String (), str -> Length ()  );

   if (  f.Failed ()  ) {

    TCollection_AsciiString s;

    p.SystemName ( s );

    ErrorMsg() << "WOKStep_Compile :: Execute"
             << "could not create '" << new TCollection_HAsciiString ( s )
             << "'" << endm;

   }  // end if

   f.Close ();

  }  // end if

  return;

 }  // end if

  if(fails->Length() && succeeds->Length())
    {
      SetIncomplete();
      return;
    }
  if(fails->Length())
    {
      SetFailed();
      return;
    }
  SetSucceeded();
  return;
}


