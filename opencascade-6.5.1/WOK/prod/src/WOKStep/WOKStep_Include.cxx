// File:	WOKStep_Include.cxx
// Created:	Thu Oct 26 20:10:02 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef WNT
# include <io.h>
#endif  // WNT

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Param.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKBuilder_Include.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_Include.ixx>

#ifdef WNT
# define WIN32_LEAN_AND_MEAN
# include <windows.h>
# include <tchar.h>
# include <WOKNT_WNT_BREAK.hxx>

extern "C" __declspec( dllimport ) int wokCP  ( int, char** );
extern "C" __declspec( dllimport ) int wokCMP ( int, char** );

#else
# define WOKStep_Include_SYMLINK 0
# if !WOKStep_Include_SYMLINK
#  include <OSD_File.hxx>
#  include <sys/types.h>
#  include <utime.h>
# endif  // !WOKStep_Include_SYMLINK
#endif  // WNT

//=======================================================================
//function : WOKStep_Include
//purpose  : 
//=======================================================================
 WOKStep_Include::WOKStep_Include(const Handle(WOKMake_BuildProcess)&     abp,
				  const Handle(WOKernel_DevUnit)&         aunit, 
				  const Handle(TCollection_HAsciiString)& acode, 
				  const Standard_Boolean checked, 
				  const Standard_Boolean hidden)
: WOKMake_Step(abp, aunit,  acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Include::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBADMFILE);
  return result; 
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Include::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBTMPDIR);
  return result; 
}


//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Include::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_HFile:  
	case WOKUtils_HXXFile:
	case WOKUtils_LXXFile:
	case WOKUtils_GXXFile:
	case WOKUtils_IDLFile:
	case WOKUtils_INCFile:
	  result = new WOKBuilder_Include(apath); break;
	default:  
	  return Standard_False;
	}
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }
  else
    {
      return Standard_False;
    }
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_Include::Execute(const Handle(WOKMake_HSequenceOfInputFile)& tobuild)
{
  Standard_Integer i;
  Handle(WOKernel_File) incfile;
  Handle(WOKernel_File) pubincfile;
  Handle(WOKernel_FileType) sourcetype = Unit()->FileTypeBase()->Type("source");
  Handle(WOKernel_FileType) pubinctype = Unit()->FileTypeBase()->Type("pubinclude");

  Handle(WOKMake_InputFile) infile;

  for(i=1; i<=tobuild->Length(); i++)
    {
#ifdef DEB
    cout << " pubinctype  publication : include number "<< i << " from " << tobuild->Length() << endl;
#endif
#ifdef WNT
      _TEST_BREAK();
#endif  // WNT
      infile = tobuild->Value(i);
      
      // include de type source local au wb
      pubincfile = new WOKernel_File(infile->File()->Name(), Unit(), pubinctype);
      pubincfile->GetPath();

      if(infile->File()->Nesting()->IsSameString(Unit()->FullName()))
	{
#ifndef WNT
# if WOKStep_Include_SYMLINK
          if(pubincfile->Path()->Exists())
            {
              pubincfile->Path()->RemoveFile();
            }
	  symlink(infile->File()->Path()->Name()->ToCString(), pubincfile->Path()->Name()->ToCString());
# else
          if ( ( !pubincfile -> Path () -> Exists () ||
                 !pubincfile -> Path () -> IsSameFile (  infile -> File () -> Path ()  )) &&
	       ( infile -> File () -> Path ()  -> Exists () )
	     ) {
	    OSD_Path pSrc (  infile -> File () -> Path () -> Name () -> String ()  );
	    OSD_File fSrc (  pSrc                                                  );
	    OSD_Path pDst (  pubincfile -> Path () -> Name () -> String ()         );

	    OSD_File fDst ( pDst ) ;
#ifdef DEB
	    cout << " pubinclude publication : before Chmod  " << endl;
#endif
	    if ( fDst.IsReadable() &&  !fDst.IsWriteable()) { 
	      if (  fDst.UserId() == fSrc.UserId() ) {
	       chmod (  pubincfile -> Path () -> Name () -> ToCString (), 00644  );
	      }
	    }
#ifdef DEB
	    cout << " pubinclude publication : before copy  " << endl;
#endif
	    fSrc.Copy ( pDst );

	    if (  fSrc.Failed ()  ) {
	      
	      ErrorMsg() << "WOKStep_Include :: Execute"
		       << "failed to copy '" << infile -> File () -> Path () -> Name ()
		       << "' to '"           << pubincfile        -> Path () -> Name ()
		       << "'" << endm;
	      
	      SetFailed ();

	      return;

	    } else {

	      InfoMsg() << "WOKStep_Include :: Execute"
		      << "Copied : '" << infile->File()->Path()->Name()
		      << "' to '" << pubincfile->Path()->Name()
		      << "'" << endm;
	    }  // end if
	    
	    struct utimbuf times;
	    struct stat    buf;
	    
	    stat (  infile -> File () -> Path () -> Name () -> ToCString (), &buf  );
	    
	    times.actime  = buf.st_atime;
	    times.modtime = buf.st_mtime; 
	    
	    utime (  pubincfile -> Path () -> Name () -> ToCString (), &times  ); 
	    
	  } else {

	    if ( !(infile -> File () -> Path ()  -> Exists () )) {
	      InfoMsg() << "WOKStep_Include :: Execute"
		<< "failed to copy '" << infile -> File () -> Path () -> Name ()
		  << "' to '"           << pubincfile        -> Path () -> Name ()
		    << "'" << endm;
	    }

          }  // end if
# endif  // WOKStep_Incluse_SYMLINK
#else
	  Standard_PCharacter args[ 4 ];

      args[ 0 ] = "wokCMP";
      args[ 1 ] = (Standard_PCharacter)infile -> File () -> Path () -> Name () -> ToCString ();
      args[ 2 ] = (Standard_PCharacter)pubincfile        -> Path () -> Name () -> ToCString ();

      if (  wokCMP ( 3, args )  ) {

       HANDLE   hFile;
       FILETIME cTime, aTime, wTime;

       args[ 0 ] = "wokCP";
       wokCP ( 3, args );

       hFile = CreateFileA (
                args[ 1 ], GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL
               );

       if ( hFile != INVALID_HANDLE_VALUE ) {

        if (  GetFileTime ( hFile, &cTime, &aTime, &wTime )  ) {

         CloseHandle ( hFile );
         hFile = CreateFileA (
                  args[ 2 ], GENERIC_WRITE, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL
                 );

         if ( hFile != INVALID_HANDLE_VALUE ) SetFileTime ( hFile, &cTime, &aTime, &wTime );

        }  // end if

        CloseHandle ( hFile );

       }  // end if

      }  // end if
#endif
	}
      else
       {
	 if ( pubincfile -> Path () -> Exists () ) {
	   pubincfile->Path()->RemoveFile();
	 }
       }

      
      pubincfile = Locator()->Locate(Unit()->Name(), pubinctype->Name(), infile->File()->Name());

      if(!pubincfile.IsNull())
	{
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(pubincfile->LocatorName(), pubincfile, 
								      Handle(WOKBuilder_Entity)(), pubincfile->Path());
	  outfile->SetProduction();
	  outfile->SetLocateFlag(Standard_True);
	  
	  AddExecDepItem(infile, outfile, Standard_True);
	}
    }

  SetSucceeded();
  return;
}

