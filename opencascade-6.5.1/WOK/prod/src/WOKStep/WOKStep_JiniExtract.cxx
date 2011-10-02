// Copyright: 	Matra-Datavision 1999
// File:	WOKSteps_JiniExtract.cxx
// Created:	Mon Mar 22 17:10:16 1999
// Author:	Arnaud BOUZY
//		<adn>
// Modification: Eugeny PLOTNIKOV <e-plotnikov@minsk.matra-dtv.fr> (ClassFile.cfg generation)

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Path.hxx>

#include <MS.hxx>
#include <MS_Client.hxx>
#include <MS_Interface.hxx>
#include <MS_Package.hxx>

#include <WOKBuilder_MSJiniExtractor.hxx>
#include <WOKBuilder_MSActionID.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_MSExtractorIterator.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_JiniExtract.ixx>

#include <OSD_File.hxx>
#include <OSD_Protection.hxx>
#include <OSD_Environment.hxx>

//=======================================================================
//function : WOKStep_JiniExtract
//purpose  : 
//=======================================================================
WOKStep_JiniExtract :: WOKStep_JiniExtract (
                        const Handle( WOKMake_BuildProcess)&        abp,
					    const Handle( WOKernel_DevUnit)&          aunit, 
					    const Handle( TCollection_HAsciiString )& acode, 
					    const Standard_Boolean                  checked, 
					    const Standard_Boolean                   hidden
                       ) : WOKStep_Extract ( abp, aunit, acode, checked, hidden ) {

 Handle( WOKBuilder_MSJiniExtractor ) anextractor =
  new WOKBuilder_MSJiniExtractor (  Unit () -> Params ()  );

 anextractor -> SetMSchema (  WOKBuilder_MSTool :: GetMSchema ()  );

 SetExtractor ( anextractor );

}  // end WOKStep_JiniExtract :: WOKStep_JiniExtract
//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_JiniExtract :: HandleInputFile (
                                         const Handle( WOKMake_InputFile )& infile
                                        ) {
//JR                                        ) const {

 if (  !infile -> IsPhysic ()  ) {

  if (   !strcmp (
           "CPPJini_COMPLETE", infile -> ID () -> Token ( ":", 2 ) -> ToCString ()
          )
  ) {

   infile -> SetDirectFlag ( Standard_True );
   infile -> SetBuilderEntity (
              new WOKBuilder_MSEntity (  infile -> ID () -> Token ( ":", 3 )  )
             );
   return Standard_True;

  }  // end if

  if (   !strcmp (
           "CPPJini_INCOMPLETE", infile -> ID () -> Token ( ":", 2 ) -> ToCString ()
          )
  ) {

   infile -> SetDirectFlag ( Standard_True );
   infile -> SetBuilderEntity (
              new WOKBuilder_MSEntity (  infile -> ID () -> Token ( ":", 3 )  )
             );
   return Standard_True;

  }  // end if

  if (   !strcmp (
           "CPPJini_SEMICOMPLETE", infile -> ID () -> Token ( ":", 2 ) -> ToCString ()
          )
  ) {

   infile -> SetDirectFlag ( Standard_True );
   infile -> SetBuilderEntity (
              new WOKBuilder_MSEntity (  infile -> ID () -> Token ( ":", 3 )  )
             );
   return Standard_True;

  }  // end if
      
 }  // end if

 return Standard_False;
 
}  // end WOKStep_JiniExtract :: HandleInputFile
//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKStep_JiniExtract::Init()
{
  Handle(WOKBuilder_MSJiniExtractor) extr = Handle(WOKBuilder_MSJiniExtractor)::DownCast(Extractor());

  if(IsToExecute())
    {
      extr->Load();
      extr->Init(Unit()->Name());
    }
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetInputFlow
//purpose  : 
//=======================================================================
void WOKStep_JiniExtract::GetInputFlow()
{
  static Handle(TCollection_HAsciiString) COMP  = new TCollection_HAsciiString("CPPJini_COMPLETE");
  static Handle(TCollection_HAsciiString) ICOMP = new TCollection_HAsciiString("CPPJini_INCOMPLETE");
  static Handle(TCollection_HAsciiString) SCOMP = new TCollection_HAsciiString("CPPJini_SEMICOMPLETE");

  Handle(WOKernel_File) NULLFILE;
  Handle(WOKUtils_Path) NULLPATH;
  Handle(WOKBuilder_Entity) NULLENT;

  Handle(WOKBuilder_MSJiniExtractor) ext = Handle(WOKBuilder_MSJiniExtractor)::DownCast(Extractor());

  //WOKMake_Step::GetInputFlow();
    
  WOKTools_MapIteratorOfMapOfHAsciiString cit(ext->CompleteTypes());

  while(cit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), COMP, cit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      cit.Next();
    }

  WOKTools_MapIteratorOfMapOfHAsciiString iit(ext->IncompleteTypes());

  while(iit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), ICOMP, iit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      iit.Next();
    }

  WOKTools_MapIteratorOfMapOfHAsciiString sit(ext->SemiCompleteTypes());
  while(sit.More())
    {
      
      Handle(TCollection_HAsciiString) id = WOKernel_File::FileLocatorName(Unit()->Name(), SCOMP, sit.Key());

      Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(id, NULLFILE,NULLENT, NULLPATH); 

      infile->SetLocateFlag(Standard_True);
      infile->SetDirectFlag(Standard_True);
      infile->SetPhysicFlag(Standard_False);

      if(HandleInputFile(infile))
	{
	  myinflow.Add(infile->ID(), infile);
	}
      sit.Next();
    }
  
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_JiniExtract::OutOfDateEntities()
{
  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Standard_Integer i;
  
  LoadDependencies();

  Handle(WOKBuilder_MSchema) ameta = Extractor()->MSchema();
  
  for(i=1; i<=myinflow.Extent(); i++)
    {
      Handle(WOKMake_InputFile) infile = myinflow(i);
      Handle(WOKBuilder_MSEntity) anent = Handle(WOKBuilder_MSEntity)::DownCast(infile->BuilderEntity());
      
      if(anent.IsNull())
	{
	  ErrorMsg() << "WOKStep_JiniExtract::OutOfDateEntities" 
		   << infile->ID() << " is not a MS Entity" << endm;
	  SetFailed();
	  return result;
	}
      
      Handle( TCollection_HAsciiString ) aName =
       new TCollection_HAsciiString (  anent -> Name ()  );

      aName -> AssignCat ( "@" );
      aName -> AssignCat (  Unit () -> Name ()  );
#if 0
      WOKBuilder_MSActionID anid(anent->Name(), Extractor()->ExtractorID());
#else
      WOKBuilder_MSActionID anid (  aName, Extractor () -> ExtractorID ()  );
#endif
      Handle(WOKBuilder_MSAction) anaction = ameta->GetAction(anid);
      
      switch(Extractor()->ExtractionStatus(anaction))
	{
	case WOKBuilder_OutOfDate:
	  result->Append(infile);
	  break;
	case WOKBuilder_UpToDate:
	  break;
	case WOKBuilder_NotDefined:
	  SetFailed();
	  return result;
        default: break;
	}
    }
  return result;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_JiniExtract :: Execute (  const Handle( WOKMake_HSequenceOfInputFile )& tobuild  ) {

 Standard_Boolean secondPass = Standard_False;
 Standard_Integer i, j, k, l;

 Handle( WOKernel_FileType ) sourcetype      = Unit () -> GetFileType ( "source"      );
 Handle( WOKernel_FileType ) privincludetype = Unit () -> GetFileType ( "privinclude" );
 Handle( WOKernel_FileType ) pubincludetype  = Unit () -> GetFileType ( "pubinclude"  );
 Handle( WOKernel_FileType ) derivatedtype   = Unit () -> GetFileType ( "derivated"   );
 Handle( WOKernel_FileType ) englispfiletype = Unit () -> GetFileType ( "englisp"     );
 Handle( WOKernel_FileType ) javatype;

 OSD_Environment env ( "WOK_USE_JAVA_DIRECTORY" );

 javatype = env.Value ().IsEmpty () ? Unit () -> GetFileType ( "derivated" )
                                    : Unit () -> GetFileType ( "javafile"  );

 Handle( TCollection_HAsciiString ) str;

 Handle( WOKMake_HSequenceOfInputFile ) buildSeq = tobuild;
 Handle( WOKMake_HSequenceOfInputFile ) aPackSeq = new WOKMake_HSequenceOfInputFile ();

 Handle( WOKBuilder_Command ) acmd =
  new WOKBuilder_Command (  new TCollection_HAsciiString ( "COMMAND" ), Unit () -> Params ()  );
 Handle( WOKUtils_Shell     ) ashell = Shell ();

 ashell -> Lock ();
 acmd -> SetShell ( ashell );

 Handle( WOKernel_File ) outfile, basefile;
 Extractor () -> Load ();
 Extractor () -> SetOutputDir (  OutputDir ()  );

 WOKBuilder_MSExtractorIterator anit (  WOKBuilder_MSTool :: GetMSchema (), Extractor ()  );
nextPass:  
 for ( j = 1; j <= buildSeq -> Length (); ++j ) {
   
  Handle( WOKBuilder_MSEntity      ) entity =
   Handle( WOKBuilder_MSEntity ) :: DownCast (  buildSeq -> Value ( j ) -> BuilderEntity ()  );
  Handle( TCollection_HAsciiString ) amode = buildSeq -> Value ( j ) -> ID () -> Token ( ":", 2 );

  if (  !secondPass &&
         Extractor () -> MSchema () -> MetaSchema () -> IsPackage (  entity -> Name ()  )
  ) {

   aPackSeq -> Append (  buildSeq -> Value ( j )  );
   continue;

  }  // end if
      
  switch (   anit.Execute (  entity, amode -> ToCString ()  )   ) {

   case WOKBuilder_Success: {
#ifdef WOK_VERBOSE
    if (  VerboseMsg() ( "WOK_EXTRACT" ).IsSet ()  ) {

     VerboseMsg() ( "WOK_EXTRACT" ) << "WOKStep_Extract::Execute"
                                  << entity -> Name ()
                                  << " produces : "
                                  << endm;

     for (  i = 1; i <= anit.Produces () -> Length (); ++i  )

      VerboseMsg() ( "WOK_EXTRACT" ) << "WOKStep_Extract::Execute" 
					               << "\t\t"
                                   << anit.Produces () -> Value ( i ) -> Path () -> Name ()
                                   << endm;
    }  // end if
#endif  // WOK_VERBOSE
    str = new TCollection_HAsciiString (  entity -> Name ()  );

    str -> AssignCat ( "@" );
    str -> AssignCat (  Unit () -> Name ()  );
#if 0
    WOKBuilder_MSActionID         anid (  entity -> Name (), WOKBuilder_ClientExtract  );
#else
    WOKBuilder_MSActionID         anid (  str, WOKBuilder_ClientExtract  );
#endif
    Handle( WOKBuilder_MSAction ) anaction = Extractor () -> MSchema () -> GetAction ( anid );
    Handle( WOKBuilder_Entity   ) outent;

    Extractor () -> MSchema () -> ChangeAddAction (
                                   anid, Handle( WOKBuilder_Specification ) ()
                                  );

    for ( i = 1; i <= anit.Produces () -> Length (); ++i ) {

     Standard_Boolean istemplate = Standard_False;

     outent = anit.Produces () -> Value ( i );

     switch (  outent -> Path () -> Extension ()  ) {

      case WOKUtils_HXXFile:

       outfile = new WOKernel_File (  outent -> Path () -> FileName (), Unit (), pubincludetype  );

      break;

      case WOKUtils_IXXFile:
      case WOKUtils_JXXFile:
      case WOKUtils_DDLFile:

       outfile = new WOKernel_File (  outent -> Path () -> FileName (), Unit (), privincludetype  );

      break;

      case WOKUtils_CXXFile: {

       TCollection_AsciiString name;
       OSD_Path                p (  outent -> Path () -> FileName () -> String ()  );

       name = p.Name ();
       name.ChangeAll ( '.', '_' );
       p.SetName ( name );
       p.SystemName ( name );

       outfile = new WOKernel_File (
                      new TCollection_HAsciiString ( name ), Unit (), derivatedtype
                     );

      } break;

      case WOKUtils_DATFile:

       outfile = new WOKernel_File (  outent -> Path () -> FileName (), Unit (), derivatedtype  );

      break;

      case WOKUtils_LispFile:

       outfile = new WOKernel_File(outent->Path()->FileName(), Unit(), englispfiletype);

      break;

      case WOKUtils_TemplateFile:

       outfile = new WOKernel_File (  outent -> Path () -> FileName (), Unit (), sourcetype  );
       istemplate = Standard_True;

      break;

      case WOKUtils_UnknownFile:

       if (   !strcmp (  outent -> Path () -> ExtensionName () -> ToCString (), ".java"  )   )

        outfile = new WOKernel_File (
                       outent -> Path () -> FileName (), Unit (), javatype
                      );

      break;

      default: break;

     }  // end switch
	      
     outfile -> GetPath ();
     basefile = Locator () -> Locate (
                               Unit () -> Name (), outfile -> TypeName (), outfile -> Name ()
                              );
	      
     WOKBuilder_BuildStatus astatus = WOKBuilder_Unbuilt;
		      
     if (  basefile.IsNull ()  )

      astatus = acmd -> Move (  outent -> Path (), outfile -> Path ()  );

     else if (   !outent -> Path () -> IsSameFile (  basefile -> Path ()  )   )

      astatus = acmd -> Move (  outent -> Path (), outfile -> Path ()  );

     else astatus = outent -> Path () -> RemoveFile () ? WOKBuilder_Unbuilt : WOKBuilder_Failed;
	      
     Handle( WOKMake_OutputFile ) out;

     switch ( astatus ) {

      case WOKBuilder_Success:

       outent -> SetPath (  outfile -> Path ()  );
       out  = new WOKMake_OutputFile (
                   outfile -> LocatorName (), outfile, outent, outfile -> Path ()
                  );
       out -> SetLocateFlag ( Standard_True );
       out -> SetProduction ();

       if ( !istemplate ) AddExecDepItem (  buildSeq -> Value ( j ), out, Standard_True );

       InfoMsg()  << "WOKStep_Extract::Execute"
                << "File : "
                << outfile -> Path () -> Name ()
                << " is modified"
                << endm;

      break;

      case WOKBuilder_Unbuilt:
#ifdef WOK_VERBOSE
       VerboseMsg() ( "WOK_EXTRACT" ) << "WOKStep_Extract::Execute" 
					                << "File : "
                                    << outfile -> Path () -> Name ()
                                    << " is unchanged"
                                    << endm;
#endif  // WOK_VERBOSE
       outent -> SetPath (  basefile -> Path ()  );
       out  = new WOKMake_OutputFile (
                   basefile -> LocatorName (), basefile, outent, basefile -> Path ()
                  );
       out -> SetLocateFlag ( Standard_True );
       out -> SetProduction ();

       if ( !istemplate ) AddExecDepItem (  buildSeq -> Value ( j ), out, Standard_True  );

      break;

      case WOKBuilder_Failed:

       SetFailed ();

       ErrorMsg() << "WOKStep_Extract::Execute"
                << "Failed    : "
                << outfile -> Name ()
                << endm;

      break;

      default: break;

     }  // end switch

    }  // end for

   } break;

   case WOKBuilder_Failed:

    ErrorMsg() << "WOKStep_Extract::Execute"
             << "Failed    : "
             << entity -> Name ()
             << endm;          

   break;

   default: break;

  }  // end switch

 }  // end for

 if (  !secondPass && aPackSeq -> Length () > 0  ) {

  secondPass = Standard_True;
  buildSeq   = aPackSeq;
  goto nextPass;

 }  // end if

 InfoMsg() << "WOKStep_Extract::Execute" << "Generating ClassFile.cfg" << endm;
#ifdef WNT
 Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( "\r\n" );
#else
 Handle( TCollection_HAsciiString ) NL = new TCollection_HAsciiString ( "\n"   );
#endif  // WNT
 Handle( TCollection_HAsciiString ) cfg = new TCollection_HAsciiString ( "[base]" );
 Handle( MS_Client                ) clt = Extractor() -> MSchema () -> MetaSchema () ->
                                           GetClient (  Unit () -> Name ()  );
 Handle( TColStd_HSequenceOfHAsciiString ) cltSeq = new TColStd_HSequenceOfHAsciiString ();
 WOKTools_MapOfHAsciiString                map;

 cltSeq -> Append (  Unit () -> Name ()  );
 cfg    -> AssignCat ( NL );
 str = new TCollection_HAsciiString(  Unit () -> Name ()  );
 str -> ChangeAll ( '.', '/' );
 cfg    -> AssignCat ( str );
 cfg    -> AssignCat ( NL  );

 for ( i = 1; i <= clt -> Uses () -> Length (); ++i ) {

  str = new TCollection_HAsciiString ( clt -> Uses () -> Value ( i )  );
  str -> ChangeAll ( '.', '/' );
  cltSeq -> Append (  clt -> Uses () -> Value ( i )  );
  cfg -> AssignCat ( str );
  cfg -> AssignCat ( NL  );

 }  // end for

 cfg -> AssignCat ( "[dependent]" );
 cfg -> AssignCat ( NL );

 for ( i = 1; i <= cltSeq -> Length (); ++i ) {

  clt = Extractor () -> MSchema () -> MetaSchema () -> GetClient (  cltSeq -> Value ( i )  );

  if (  !clt.IsNull ()  ) {

   str = new TCollection_HAsciiString (  clt -> Name ()  );
   str -> ChangeAll ( '.', '/' );
   cfg -> AssignCat ( str );
   cfg -> AssignCat ( NL  );

   Handle( TColStd_HSequenceOfHAsciiString ) intfSeq = clt -> Interfaces ();
   Handle( TColStd_HSequenceOfHAsciiString ) clasSeq;

   for ( j = 1; j <= intfSeq -> Length (); ++j ) {

    Handle( MS_Interface ) intf = Extractor () -> MSchema () -> MetaSchema () ->
                                   GetInterface (  intfSeq -> Value ( j )  );

    if (  !intf.IsNull ()  ) {

     clasSeq = intf -> Classes ();

     for ( k = 1; k <= clasSeq -> Length (); ++k  )

      if (   !map.Contains (  clasSeq -> Value ( k )  )   ) {

       cfg -> AssignCat ( "/" );
       cfg -> AssignCat (  clasSeq -> Value ( k )  );
       cfg -> AssignCat ( NL  );
       map.Add (  clasSeq -> Value ( k )  );

      }  // end if

    }  // end if

    Handle( TColStd_HSequenceOfHAsciiString ) packSeq = intf -> Packages ();

    for (  k = 1; k <= packSeq -> Length (); ++k  ) {

     Handle( MS_Package ) pack = Extractor () -> MSchema () -> MetaSchema () ->
                                  GetPackage (  packSeq -> Value ( k )  );

     if (  !pack.IsNull ()  ) {

      clasSeq = pack -> Classes ();

      for ( l = 1; l <= clasSeq -> Length (); ++l ) {

       str = new TCollection_HAsciiString (  pack -> Name ()  );

       str -> AssignCat ( "_" );
       str -> AssignCat (  clasSeq -> Value ( l )  );

       if (  !map.Contains ( str )  ) {

        cfg -> AssignCat ( "/" );
        cfg -> AssignCat ( str );
        cfg -> AssignCat ( NL  );
        map.Add ( str );

       }  // end if

      }  // end for

     }  // end if

    }  // end for

   }  // end for

  }  // end if

 }  // end for

 outfile = new WOKernel_File (
                new TCollection_HAsciiString ( "ClassFile.cfg" ), Unit (), javatype
               );
 outfile -> GetPath ();

 OSD_File f (   OSD_Path (  outfile -> Path () -> Name () -> String ()  )   );

 f.Build (  OSD_WriteOnly, OSD_Protection ( OSD_RWXD, OSD_RWXD, OSD_R, OSD_R )  );

 if (  !f.Failed ()  ) {

  f.Write (  cfg -> String (), cfg -> Length ()  );
  f.Close ();

 }  // end if

 ashell -> UnLock ();

 if (  Status () == WOKMake_Unprocessed ) SetSucceeded ();

}  // end WOKStep_JiniExtract :: Execute
