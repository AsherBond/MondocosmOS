#ifdef WNT
#define STRICT
#include <windows.h>

#ifdef THIS
# undef THIS
#endif 

#ifdef CreateFile
# undef CreateFile
#endif 

#ifdef CreateDirectory
# undef CreateDirectory
#endif  

#ifdef RemoveDirectory
# undef RemoveDirectory
#endif 

#include <WOKNT_Path.ixx>

#include <WOKNT.hxx>
#include <WOKNT_PathIterator.hxx>

#include <WOKTools_Messages.hxx>
//#include <OSD_File.hxx>
//#include <OSD_Directory.hxx>
//#include <OSD_Protection.hxx>

#ifndef _INC_TCHAR
# include <tchar.h>
#endif  // _INC_TCHAR

#include <io.h>

#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <share.h>

extern "C" int wokCMP(int, char** );

//static int _writeable(OSD_SingleProtection );

WOKNT_Path::WOKNT_Path()
: myAttrGet(Standard_False), myDate(-1)
{
}  

WOKNT_Path::WOKNT_Path(const Handle(TCollection_HAsciiString)& aPath)
: myPath(aPath), myDate(-1), myAttrGet(Standard_False)
{
  myPath = aPath;
  myDate = -1;
}  

WOKNT_Path::WOKNT_Path(const Handle(TCollection_HAsciiString)& aDir, const Handle(TCollection_HAsciiString)& aName) 
: myDate(-1), myAttrGet(Standard_False)
{
  Handle(TCollection_HAsciiString) str = new TCollection_HAsciiString;
  
  str->AssignCat(aDir);
  str->AssignCat(TEXT("/"));
  str->AssignCat(aName);
  
  myPath = str;
}

WOKNT_Path::WOKNT_Path(const Standard_CString aDir, const Standard_CString aName) 
: myDate(-1), myAttrGet(Standard_False)
{
  Handle( TCollection_HAsciiString)str = new TCollection_HAsciiString;
  
  str -> AssignCat(aDir );
  str -> AssignCat( TEXT( "/") );
  str -> AssignCat(aName );
  
  myPath = str;
} 


Standard_Boolean WOKNT_Path::GetAttr() 
{
  if(myAttrGet) return Standard_True;
  if(myPath.IsNull()) return Standard_False;
  myAttrGet = Standard_True;
  if((myAttr = GetFileAttributes( myPath -> ToCString())) != 0xFFFFFFFF)
    return Standard_True;
  else
    return Standard_False;
}

Standard_Boolean WOKNT_Path::Exists()
{
  if(myPath.IsNull()) return Standard_False;
  CheckAttr();
  if(myAttr != 0xFFFFFFFF )
    return Standard_True;
  else
    return Standard_False;
} 

Standard_Boolean WOKNT_Path::CreateDirectory(const Standard_Boolean fCreateParents) 
{
  if(Exists())
    {
      if(!IsDirectory())
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::CreateDirectory (): ")<< myPath
	    << TEXT( " exists and is not a directory"       )<< endm;
	  return Standard_False;
	}
      return Standard_True;
    }
  
  Handle(WOKNT_Path) aParent = new WOKNT_Path(DirName());
  
  if(!aParent->Exists()) 
    {  
      if(fCreateParents) 
	{
	  if(!aParent->CreateDirectory(fCreateParents)) 
	    return Standard_False;
	}
      else
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::CreateDirectory") 
	    << "Parent directory " << aParent->Name() << TEXT(" does not exist")<< endm;
	  return Standard_False;
	}
    }
  else
    {
      if(!aParent->IsDirectory())
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::CreateDirectory") 
	    << "Parent  " << aParent->Name() << TEXT(" exists and is not a directory")<< endm;
	  return Standard_False;
	}
    }
  
#ifdef UNICODE
# define CreateDirectory CreateDirectoryW
#else
# define CreateDirectory CreateDirectoryA
#endif 
  
  if(CreateDirectory(myPath->ToCString(), NULL))
    return Standard_True;
  return Standard_False;

#ifdef CreateFile
# undef CreateFile
#endif  // CreateFile
  
#ifdef CreateDirectory
# undef CreateDirectory
#endif  // CreateFile
  
#ifdef RemoveDirectory
# undef RemoveDirectory
#endif  // CreateFile
  
}

Standard_Boolean WOKNT_Path::CreateFile(const Standard_Boolean fCreateParents) 
{
  if(Exists())
    {
      if(IsDirectory()) 
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::CreateFile" )
	    << myPath << TEXT( " exists and is a directory")<< endm;
	  return Standard_False;
	}
      return Standard_True;
    } 

  Handle(WOKNT_Path) aParent = new WOKNT_Path(DirName());
  
  if(aParent->Exists())
    {
      if(!aParent->IsDirectory()) 
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::CreateFile" )
	    << TEXT("Parent diectory ")<< aParent->Name() << TEXT(" exists and is not a directory")<< endm;
	  return Standard_False;
	}
    }
  else
    {
      aParent->CreateDirectory(fCreateParents);
    }

#ifdef UNICODE
# define CreateFile CreateFileW
#else
# define CreateFile CreateFileA
#endif 

  HANDLE theFile = CreateFile(myPath->ToCString(), GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
  
#ifdef CreateFile
# undef CreateFile
#endif  // CreateFile
  
  
  if(theFile == INVALID_HANDLE_VALUE) 
    {
      ErrorMsg() << TEXT( "WOKNT_Path::CreateFile")
	<< TEXT( "Creation of ") << myPath << " failed" << endm;
      return Standard_False;
    }
  else
    {
      CloseHandle(theFile);
      return Standard_True;
    }
}
  

Standard_Boolean WOKNT_Path::RemoveDirectory(const Standard_Boolean fRemoveChilds) 
{

  if( myPath.IsNull () )
    { 
      ErrorMsg() << TEXT( "WOKNT_Path	::RemoveDirectory (): " )
	<< TEXT( " invalid directory name(null )")<< endm;
      return Standard_False;
    }
  
  Standard_Boolean isempty = Standard_True;

  if(!fRemoveChilds)
    {
      WOKNT_PathIterator anit(this);
      
      if(anit.More())
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::RemoveDirectory" )
	    << TEXT( "Could not remove ")<< myPath  << ": directory is not empty" << endm;
	  return Standard_False;
	}
    } else {

      WOKNT_PathIterator anit(this);
      
      while(anit.More())
	{
	  Handle(WOKNT_Path) apath = anit.PathValue();

	  if(apath->IsDirectory())
	    {
	      if(!apath->RemoveDirectory(Standard_True)) isempty = Standard_False;
	    }
	  else
	    {
	      if(apath->RemoveFile()) isempty = Standard_False;
	    }
	  anit.Next();
	}

    }  // end if    

  if(isempty)
    {

#ifdef UNICODE
# define RemoveDirectory RemoveDirectoryW
#else
# define RemoveDirectory RemoveDirectoryA
#endif
      if(!RemoveDirectory(myPath->ToCString()))
	{
	  ErrorMsg() << TEXT( "WOKNT_Path::RemoveDirectory" )
	    << TEXT( "could not remove ") << myPath << ": " << WOKNT::SystemMessage(GetLastError()) << endm;
	  return Standard_False;
	}   
#ifdef RemoveDirectory
# undef RemoveDirectory
#endif
    }
  else
    {
      ErrorMsg() << TEXT( "WOKNT_Path::RemoveDirectory" )
	    << TEXT( "Could not empty ") << myPath  << endm;
      return Standard_False;
    }
  return Standard_True;
}

Standard_Boolean WOKNT_Path::RemoveFile()
{
  if( myPath.IsNull())
    {
      ErrorMsg() << TEXT( "WOKNT_Path::RemoveFile" )
	<< TEXT( "Invalid file name(null )")<< endm;
      return Standard_False;
    }

  if(!DeleteFile(myPath->ToCString()))
    {
      ErrorMsg() << TEXT( "WOKNT::RemoveFile" )
	<< TEXT( "Failed to remove ") << myPath << ": " << WOKNT::SystemMessage(GetLastError()) << endm;
      return Standard_False;
    }
  return Standard_True;
}

WOKNT_TimeStat WOKNT_Path::GetMDate () {

  int          fd;
  struct _stat buffer;
  BOOL         fOK = FALSE;

  __try {

    if( ( fd = _sopen (
		       myPath -> ToCString (), _O_RDONLY, _SH_DENYNO
		       )
	 )== -1
       ){
      
      fOK = TRUE;
      __leave;

    }  // end if

    if( _fstat(fd, &buffer)== -1 )__leave;

    fOK = TRUE;

  }  // end __try

  __finally {
    
    if(fd != -1)close(fd );

  }  // end __finally

  if(!fOK )

    OSD_OSDError::Raise( TEXT( "WOKNT_Path::GetMDate () failed") );

  return myDate = buffer.st_mtime;

}  // end WOKNT_Path::GetMDate

WOKNT_Extension WOKNT_Path::Extension () const {

  Standard_CString ptr = _tcsrchr( myPath -> ToCString (), TEXT( '.') );

  myPath -> RightAdjust ();

  if(ptr++ != NULL){
    
    if(  _tcscmp( ptr, TEXT( "cdl"     ))== 0  )return WOKNT_CDLFile;
    if(  _tcscmp( ptr, TEXT( "odl"     ))== 0  )return WOKNT_ODLFile;
    if(  _tcscmp( ptr, TEXT( "idl"     ))== 0  )return WOKNT_IDLFile;

    if(  _tcscmp( ptr, TEXT( "hxx"     ))== 0  )return WOKNT_HXXFile;
    if(  _tcscmp( ptr, TEXT( "ixx"     ))== 0  )return WOKNT_IXXFile;
    if(  _tcscmp( ptr, TEXT( "jxx"     ))== 0  )return WOKNT_JXXFile;
    if(  _tcscmp( ptr, TEXT( "lxx"     ))== 0  )return WOKNT_LXXFile;
    if(  _tcscmp( ptr, TEXT( "gxx"     ))== 0  )return WOKNT_GXXFile;
    if(  _tcscmp( ptr, TEXT( "pxx"     ))== 0  )return WOKNT_PXXFile;
    if(  _tcscmp( ptr, TEXT( "h"       ))== 0  )return WOKNT_HFile;
    if(  _tcscmp( ptr, TEXT( "inc"     ))== 0  )return WOKNT_INCFile;

    if(  _tcscmp( ptr, TEXT( "template"))== 0  )return WOKNT_TemplateFile;
    
    if(  _tcscmp( ptr, TEXT( "ddl"     ))== 0  )return WOKNT_DDLFile;
    if(  _tcscmp( ptr, TEXT( "DB"      ))== 0  )return WOKNT_DBFile;
    if(  _tcscmp( ptr, TEXT( "FDDB"    ))== 0  )return WOKNT_FDDBFile;
    if(  _tcscmp( ptr, TEXT( "libshema"))== 0  )return WOKNT_LibSchemaFile;
    if(  _tcscmp( ptr, TEXT( "asdb"    ))== 0  )return WOKNT_AppSchemaFile;
    if(  _tcscmp( ptr, TEXT( "ho2"     ))== 0  )return WOKNT_HO2File;

    if(  _tcscmp( ptr, TEXT( "lex"     ))== 0  )return WOKNT_LexFile;
    if(  _tcscmp( ptr, TEXT( "yacc"    ))== 0  )return WOKNT_YaccFile;
    if(  _tcscmp( ptr, TEXT( "lws"     ))== 0  )return WOKNT_LWSFile;
    if(  _tcscmp( ptr, TEXT( "psw"     ))== 0  )return WOKNT_PSWFile;

    if(  _tcscmp( ptr, TEXT( "cxx"     ))== 0  )return WOKNT_CXXFile;
    if(  _tcscmp( ptr, TEXT( "cpp"     ))== 0  )return WOKNT_CXXFile;
    if(  _tcscmp( ptr, TEXT( "c"       ))== 0  )return WOKNT_CFile;
    if(  _tcscmp( ptr, TEXT( "f"       ))== 0  )return WOKNT_F77File;

    if(  _tcscmp( ptr, TEXT( "obj"     ))== 0  )return WOKNT_ObjectFile;
    if(  _tcscmp( ptr, TEXT( "lib"     ))== 0  )return WOKNT_LIBFile;
    if(  _tcscmp( ptr, TEXT( "imp"     ))== 0  )return WOKNT_IMPFile;
    if(  _tcscmp( ptr, TEXT( "def"     ))== 0  )return WOKNT_DEFile;
    if(  _tcscmp( ptr, TEXT( "pdb"     ))== 0  )return WOKNT_PDBFile;
    if(  _tcscmp( ptr, TEXT( "dll"     ))== 0  )return WOKNT_DLLFile;
    if(  _tcscmp( ptr, TEXT( "exe"     ))== 0  )return WOKNT_EXEFile;
    if(  _tcscmp( ptr, TEXT( "exp"     ))== 0  )return WOKNT_EXPFile;

    if(  _tcscmp( ptr, TEXT( "rc"      ))== 0  )return WOKNT_RCFile;
    if(  _tcscmp( ptr, TEXT( "res"     ))== 0  )return WOKNT_RESFile;

    if(  _tcscmp( ptr, TEXT( "dat"     ))== 0  )return WOKNT_DATFile;
    if(  _tcscmp( ptr, TEXT( "xwd"     ))== 0  )return WOKNT_IconFile;
    if(  _tcscmp( ptr, TEXT( "txt"     ))== 0  )return WOKNT_TextFile;


    if(  _tcscmp( ptr, TEXT( "ll"       ))== 0  )return WOKNT_LispFile;
    if(  _tcscmp( ptr, TEXT( "m"       ))== 0  ) return WOKNT_MFile;

    return WOKNT_UnknownFile;

  } else

    return WOKNT_NoExtFile;

}  

Handle(TCollection_HAsciiString) WOKNT_Path::BaseName() const
{
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(myPath);
  
  pos = retVal->Length();
  
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      
      if( chr == TEXT( '\\') || chr == TEXT( '/')) break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(pos+1, retVal->Length());

  pos = retVal->Length();
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      if( chr == TEXT( '.')) break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(1, pos-1);
  return retVal;
}

Handle(TCollection_HAsciiString) WOKNT_Path::DirName() const {

  Standard_Integer                   pos, len;
  Standard_Character                 chr;
  Standard_Boolean                   fRetry;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(myPath);

  fRetry = Standard_False;
  pos    = len = retVal->Length();

  while(pos != 0)
    {
      chr = retVal -> Value(pos );
      
      if( chr == TEXT( '\\')|| chr == TEXT( '/'))
	{
	  if(fRetry || pos != len) break;
	
	  retVal -> Trunc(pos - 1 );
	  fRetry = Standard_True;
	}
      --pos;
    }

  if(pos > 1 )
    retVal -> Trunc(pos - 1 );
  return retVal;
}

Handle( TCollection_HAsciiString)WOKNT_Path::FileName() const 
{
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(myPath);
  
  pos = retVal->Length();
  
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      
      if( chr == TEXT( '\\') || chr == TEXT( '/'))
	{
	  if(pos == retVal->Length())
	    {
	      retVal->Trunc(retVal->Length()-1);
	    }
	  else
	    break;
	}
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(pos+1, retVal->Length());
  return retVal;
}

Handle( TCollection_HAsciiString)WOKNT_Path::ExtensionName () const 
{  
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(myPath);
  
  pos = retVal->Length();
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      if( chr == TEXT( '.')) break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(pos, retVal->Length());
  return retVal;
}

Standard_Boolean WOKNT_Path::MoveTo(const Handle(WOKNT_Path)& aDestPath) 
{
  if(!CheckAttr()) return Standard_False;

  if(!(myAttr&FILE_ATTRIBUTE_DIRECTORY)) 
    {
      if (!MoveFileEx(myPath->ToCString (), aDestPath->Name()->ToCString (), MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED))
	{
	  ErrorMsg() << TEXT("WOKNT_Path::MoveTo")
	    << WOKNT::SystemMessage(GetLastError()) << endm;
	  ErrorMsg() << TEXT("WOKNT_Path::MoveTo")
	    << TEXT("Failed to move ") << myPath << " to " << aDestPath->Name() << endm;
	  return Standard_False;
	}
    }
  else
    {
      if (!MoveFileEx(myPath->ToCString (), aDestPath->Name()->ToCString (), MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED))
	{
	    ErrorMsg() << TEXT("WOKNT_Path::MoveTo")
	      << WOKNT::SystemMessage(GetLastError()) << endm;
	    ErrorMsg() << TEXT("WOKNT_Path::MoveTo")
	      << TEXT("Failed to move ") << myPath << " to " << aDestPath->Name() << endm;
	    return Standard_False;
	}
    }
  return Standard_True;
} 

//=======================================================================
//function : ReducedPath
//purpose  : 
//=======================================================================
Handle(WOKNT_Path) WOKNT_Path::ReducedPath() const
{
  Handle(WOKNT_Path) areduced;
  Handle(TCollection_HAsciiString) astr;

  astr = new TCollection_HAsciiString(Name());
  areduced = new WOKNT_Path(astr);
  return areduced;
}

Standard_Boolean WOKNT_Path::IsSymLink ()  
{
  // stupid question on NT
  return Standard_False;
}

Standard_Boolean WOKNT_Path::IsOlder(const Handle(WOKNT_Path)& aName)
{
  return aName->MDate() > MDate();
} 

Standard_Boolean WOKNT_Path::IsNewer(const Handle(WOKNT_Path)& aName) 
{
  return aName -> MDate () < MDate ();
}

Standard_Boolean WOKNT_Path::IsSameFile(const Handle( WOKNT_Path )& aPath)const {

  Standard_PCharacter newArgs[ 3 ];

  if(myPath.IsNull()) return Standard_False;
  if(aPath.IsNull())  return Standard_False;
  if(aPath->Name().IsNull()) return Standard_False;
  
  newArgs[ 0 ] = "wokCMP";
  newArgs[ 1 ] = (Standard_PCharacter)myPath -> ToCString ();
  newArgs[ 2 ] = (Standard_PCharacter)aPath  -> Name () -> ToCString ();

  return !wokCMP(3, newArgs)? Standard_True : Standard_False;


}

Standard_Boolean WOKNT_Path::IsWriteAble ()  {
  CheckAttr();
  if(myAttr == 0xffffffff || myAttr & FILE_ATTRIBUTE_READONLY) return Standard_False;
  return Standard_True;
}

Standard_Boolean  WOKNT_Path::IsDirectory () 
{
  if(!CheckAttr())  return Standard_False;
  if(myAttr & FILE_ATTRIBUTE_DIRECTORY) return Standard_True;
  return Standard_False;
}

Standard_Boolean WOKNT_Path::IsFile () 
{
  if(!CheckAttr())             return Standard_False;
  if(myAttr & FILE_ATTRIBUTE_DIRECTORY) return Standard_False; 
  return Standard_True;
}
#endif
