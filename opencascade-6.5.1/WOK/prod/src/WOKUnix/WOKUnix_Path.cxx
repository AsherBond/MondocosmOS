#ifndef WNT
// File:	WOKUnix_Path.cxx
// Created:	Tue May 30 09:17:00 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <sys/types.h>

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <stdio.h>

#include <WOKUnix_Path.ixx>

#include <WOKTools_Messages.hxx>

#include <TCollection_AsciiString.hxx>

#include <WOKUnix.hxx>

#include <WOKUnix_PathIterator.hxx>

#ifndef PATH_MAX
# define PATH_MAX 1024
#endif  // end PATH_MAX

//=======================================================================
//function : WOKUnix_Path
//purpose  : Empty Contructor
//=======================================================================
WOKUnix_Path::WOKUnix_Path()
{
  mystats.st_mtime = -1;
  myacces = Standard_False;
}

//=======================================================================
//function : WOKUnix_Path
//purpose  : Instantiates using a TCollection_AsciiString
//=======================================================================
WOKUnix_Path::WOKUnix_Path(const Standard_CString apath)
{
  mypath = new TCollection_HAsciiString(apath);
  mystats.st_mtime = -1;
  myacces = Standard_False;
}

//=======================================================================
//function : WOKUnix_Path
//purpose  : Instantiates using a TCollection_AsciiString
//=======================================================================
WOKUnix_Path::WOKUnix_Path(const Handle(TCollection_HAsciiString)& apath)
{
  mypath = apath;
  mystats.st_mtime = -1;
  myacces = Standard_False;
}

//=======================================================================
//function : WOKUnix_Path
//purpose  : Instantiates using a TCollection_AsciiString
//=======================================================================
WOKUnix_Path::WOKUnix_Path(const Handle(TCollection_HAsciiString)& adir, const Handle(TCollection_HAsciiString)& aname)
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  astr->AssignCat(adir);
  astr->AssignCat("/");
  astr->AssignCat(aname);
  mypath = astr;
  mystats.st_mtime = -1;
  myacces = Standard_False;
}

//=======================================================================
//function : WOKUnix_Path
//purpose  : Instantiates using a TCollection_AsciiString
//=======================================================================
WOKUnix_Path::WOKUnix_Path(const Standard_CString adir, const Standard_CString aname)
{
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  astr->AssignCat(adir);
  astr->AssignCat("/");
  astr->AssignCat(aname);
  mypath = astr;
  mystats.st_mtime = -1;
  myacces = Standard_False;
}

//=======================================================================
//function : SetName
//purpose  : set path
//=======================================================================
void WOKUnix_Path::SetName(const Handle(TCollection_HAsciiString)& apath)
{
  mypath = apath;
}

//=======================================================================
//function : BuildFDescr
//purpose  : build WOKUnix_FDescr
//=======================================================================
WOKUnix_FDescr WOKUnix_Path::BuildFDescr() const 
{
  WOKUnix_FDescr afd;
  OSD_Path        apath(mypath->String());


  afd.SetPath(apath);

  return afd;
}

//=======================================================================
//function : Exists
//purpose  : Test existency of path on FS
//=======================================================================
Standard_Boolean WOKUnix_Path::Exists() const 
{
  if(mypath.IsNull())
    {
      return Standard_False;
    }
  if(myacces) return  Standard_True;
  
  if(access(mypath->ToCString(), F_OK))  {return Standard_False;}

  return Standard_True;
}

//=======================================================================
//function : CreateDirectory
//purpose  : Creates path as a directory
//=======================================================================
Standard_Boolean WOKUnix_Path::CreateDirectory(const Standard_Boolean CreateParents) 
{
  TCollection_AsciiString afullname;
  
  if(Exists())
    {
      // l'inode existe deja
      if(!IsDirectory())
	{
	  ErrorMsg() << "WOKUnix_Path::CreateDirectory" 
	    << mypath << " exists and is not a directory" << endm; 
	  return Standard_False;
	}
      return Standard_True;
    }
  
  Handle(WOKUnix_Path) parent = new WOKUnix_Path(DirName());

  if(parent->Exists())
    {
      if(!parent->IsDirectory())
	{
	  ErrorMsg() << "WOKUnix_Path::CreateDirectory" 
	    << "Parent Directory " << parent->Name() << " exists and is not a directory" << endm; 
	  return Standard_False;
	}

     if(mkdir(mypath->ToCString(), 0777))
	{
	  ErrorMsg() << "WOKUnix_Path::CreateDirectory" 
	    << WOKUnix::LastSystemMessage() << endm;
	  ErrorMsg() << "WOKUnix_Path::CreateDirectory" 
	    << "Could not create directory : " << mypath << endm;
	  return Standard_False;
	}
      return Standard_True;
    }
  
  if(CreateParents == Standard_False)
    {
      ErrorMsg() << "WOKUnix_Path::CreateDirectory" 
	<< "Parent Directory " << parent->Name() << " does not exist" << endm;
      return Standard_False;
    }

  // j'ai le droit de creer les parents
  // ADN - attention, creation de path sur un disque invisible (bouclage !)
  if (!strcmp(parent->Name()->ToCString(),Name()->ToCString())) {
    return Standard_False;
  }

  if (parent->CreateDirectory(Standard_True)) {
    return CreateDirectory(Standard_False);
  }
  return Standard_False;
}

//=======================================================================
//function : CreateFile
//purpose  : Creates path as a file
//=======================================================================
Standard_Boolean WOKUnix_Path::CreateFile(const Standard_Boolean CreateParents) 
{
  TCollection_AsciiString afullname;
  Handle(WOKUnix_Path) parent = new WOKUnix_Path;

  if(Exists())
    {
      // l'inode existe deja
      if(!IsFile())
	{
	  ErrorMsg() << "WOKUnix_Path::CreateFile" 
	    << mypath << " exists and is not a file" << endm; 
	  return Standard_False;
	}
      else return Standard_True;
    }

  //if(apath.IsValid(mypath->ToCString()) == Standard_False) 
  //  {
  //    ErrorMsg() << "WOKUnix_Path::CreateFile" 
  //	       << mypath << " is invalid" << endm; 
  //    return Standard_False;
  //  }
  
  parent->SetName(DirName());
  
  if(parent->Exists())
    {
      if(!parent->IsDirectory())
	{
	  ErrorMsg() << "WOKUnix_Path::CreateFile" 
	    << "Parent Directory " << parent->Name() << " exists and is not a directory" << endm; 
	  return Standard_False;
	}

      Standard_Integer fd;
      
      if((fd=creat(mypath->ToCString(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH)) < 0)
	{
	  ErrorMsg() << "WOKUnix_Path::CreateFile" << WOKUnix::LastSystemMessage() << endm;
	  ErrorMsg() << "WOKUnix_Path::CreateFile" << "Could not create " << mypath << endm; 
	  return Standard_False;
	}
      close(fd);
      return Standard_True;
    }
  else if(CreateParents == Standard_False)
    {
      ErrorMsg() << "WOKUnix_Path::CreateFile" 
	<< "Parent Directory " 
	  << parent->Name()
	    << " does not exist" << endm;
      return Standard_False;
    }
  if (parent->CreateDirectory(Standard_True)) {
    return CreateFile(Standard_False);
  }
  return Standard_False;
}

//=======================================================================
//function : IsSymLink
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsSymLink()  
{
  if(mypath.IsNull()) return Standard_False;
  if(!CheckStats())     return Standard_False;

  struct stat statbuf;
  if(lstat(mypath->ToCString(), &statbuf))
    {
      return Standard_False;
    }
  return  S_ISLNK(statbuf.st_mode);
}

//=======================================================================
//function : IsFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsFile()  
{
  if(mypath.IsNull()) return Standard_False;
  if(!CheckStats())     return Standard_False;
  return  S_ISREG(mystats.st_mode);
}

//=======================================================================
//function : IsDirectory
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsDirectory()  
{
  if(mypath.IsNull()) return Standard_False;
  if(!CheckStats())     return Standard_False;

  return  S_ISDIR(mystats.st_mode);
}

//=======================================================================
//function : CreateSymLinkTo
//purpose  : 
//=======================================================================
Standard_Boolean  WOKUnix_Path::CreateSymLinkTo(const Handle(WOKUnix_Path)& apath) 
{
  if(apath.IsNull() || Name().IsNull())
    {
      ErrorMsg() << "WOKUnix_Path::CreateSymLinkTo" 
	<< "Unable to create symlink : Invalid arguments" << endm;
      return Standard_False;
    }

  if(symlink(apath->Name()->ToCString(), Name()->ToCString()))
    {
      ErrorMsg() << "WOKUnix_Path::CreateSymLinkTo" 
	<< WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_Path::CreateSymLinkTo" 
	<< "Unable to create " << Name() << " -> " << apath->Name() << endm;
      return Standard_False;      
    }
  return Standard_True;
}

//=======================================================================
//function : RemoveDirectory
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::RemoveDirectory(const Standard_Boolean RemoveChilds)
{
  if(Name().IsNull())
    {
      ErrorMsg() << "WOKUnix_Path::RemoveDirectory" 
	<< "Invalid null name" << endm;
      return Standard_False;
    }

  if(!RemoveChilds)
    {
      if(rmdir(Name()->ToCString()) != 0)
	{
	  ErrorMsg() << "WOKUnix_Path::RemoveDirectory" 
	    << WOKUnix::LastSystemMessage() << endm;
	  ErrorMsg() << "WOKUnix_Path::RemoveDirectory" 
	    << "Could not remove : " << Name() << endm;
	  return Standard_False;
	}
    }
  else
    {
      // WOKUtils_PathIterator ....
    }
  return Standard_True;
}

//=======================================================================
//function : RemoveFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::RemoveFile()
{
  if(Name().IsNull())
    {
      ErrorMsg() << "WOKUnix_Path::RemoveFile" 
	<< "Invalid null name" << endm;
      return Standard_False;
    }
  if(unlink(Name()->ToCString()))
    {
      ErrorMsg() << "WOKUnix_Path::RemoveFile" 
	<< WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_Path::RemoveFile" 
	<< "Failed to Remove : " << Name() << endm;
      return Standard_False;
    }
  return Standard_True;
}

//=======================================================================
//function : MoveTo
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::MoveTo(const Handle(WOKUnix_Path)& adestpath) 
{
  if(rename(mypath->ToCString(), adestpath->Name()->ToCString()))
    {
      ErrorMsg() << "WOKUnix_Path::MoveTo" 
	<< WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_Path::MoveTo" 
	<< "Failed to Move " << mypath->ToCString() << " to " << adestpath->Name()->ToCString()<< endm;
      return Standard_False;
    }
  else
    {
      mypath = adestpath->Name();
      return Standard_True;
    }
}

//=======================================================================
//function : ReducedPath
//purpose  : 
//=======================================================================
Handle(WOKUnix_Path) WOKUnix_Path::ReducedPath() const
{
  Handle(WOKUnix_Path) areduced;
  Handle(TCollection_HAsciiString) astr;

  if(Exists())
    {
#if !defined(__osf__) && !defined(DECOSF1) && !defined(__hpux) && !defined(HPUX)

      char abuffer[PATH_MAX];

      *abuffer = '\0';

      if(realpath(mypath->ToCString(), abuffer) == NULL)
	{
	  ErrorMsg() << "WOKUnix_Path::ReducedPath" << WOKUnix::LastSystemMessage() << endm;
	  return this;
	}

      astr = new TCollection_HAsciiString(abuffer);

      areduced = new WOKUnix_Path(astr);
      return areduced;
#else

      astr = new TCollection_HAsciiString(mypath);
      areduced = new WOKUnix_Path(astr);

      return areduced;

#endif

    }
  else
    {
      astr = new TCollection_HAsciiString(Name());
      areduced = new WOKUnix_Path(Name());
      return areduced;
    }
}

//=======================================================================
//function : IsSamePath
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsSamePath(const Handle(WOKUnix_Path)& another) const 
{
  Handle(WOKUnix_Path) mereduced = ReducedPath();
  Handle(WOKUnix_Path) otherreduced = another->ReducedPath();

  if(mereduced->Name()->IsSameString(otherreduced->Name())) return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : IsSameFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsSameFile(const Handle(WOKUnix_Path)& another) const 
{
  int fd1, fd2;
  long lg1, lg2;
  long nb;
  static char buf1[4096], buf2[4096];
  struct stat buf_stat;

  if((fd1 = open(Name()->ToCString(), O_RDONLY, 0)) < 0)
    {
      ErrorMsg() << "WOKUnix_Path::IsSameFile" << WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_Path::IsSameFile" << "Can't open " << Name() << endm;
      return Standard_False;
    }
  if((fd2 = open(another->Name()->ToCString(), O_RDONLY, 0)) < 0)
    {
      ErrorMsg() << "WOKUnix_Path::IsSameFile" << WOKUnix::LastSystemMessage() << endm;
      ErrorMsg() << "WOKUnix_Path::IsSameFile" << "Can't open " << another->Name() << endm;
      return Standard_False;
    }

  /* longueurs differentes ? */
  if(fstat(fd1, &buf_stat)) {close(fd1); close(fd2); return Standard_False;}
  lg1 = buf_stat.st_size;
  if(fstat(fd2, &buf_stat)) {close(fd1); close(fd2); return Standard_False;}
  lg2 = buf_stat.st_size;
  if(lg1 != lg2) {close(fd1); close(fd2); return Standard_False;}

  while(lg1 > 0)
    {
      if (lg1 > 4096)
	{nb = 4096; lg1 -= 4096;}
      else
	{nb = lg1;  lg1 = -1;}
      if((read(fd1, buf1, nb)) < 0) {close(fd1); close(fd2); return Standard_False;}
      if((read(fd2, buf2, nb)) < 0) {close(fd1); close(fd2); return Standard_False;}
      /* fichiers differents */
      if(memcmp(buf1, buf2, nb)) {close(fd1); close(fd2); return Standard_False;}
    }
  
  /* fichiers identiques */
  close(fd1);
  close(fd2);
  return Standard_True;  
}

//=======================================================================
//function : GetStats
//purpose  : Obtain file stats
//=======================================================================
Standard_Boolean WOKUnix_Path::GetStats()
{
  if(mystats.st_mtime != -1) return Standard_True;

  if(!Exists()) return Standard_False;
  else if(stat(mypath->ToCString(), &mystats))
    {
      ErrorMsg() << "WOKUnix_Path::GetStats" 
	<< WOKUnix::LastSystemMessage() << endm;
      return Standard_False;
    }
  else return Standard_True;
}

//=======================================================================
//function : IsOlder
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsOlder(const Handle(WOKUnix_Path)& another) 
{
  if(MDate() < another->MDate()) return Standard_True;
  return Standard_False;
}


//=======================================================================
//function : IsNewer
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsNewer(const Handle(WOKUnix_Path)& another) 
{
  if(MDate() > another->MDate()) return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : IsWriteAble
//purpose  : 
//=======================================================================
Standard_Boolean WOKUnix_Path::IsWriteAble() const
{
  
  if(mypath.IsNull())
    {
      return Standard_False;
    }
  
  if(access(mypath->ToCString(), R_OK | W_OK ))
    {
      return Standard_False;
    }

  return Standard_True;
}

//=======================================================================
//function : Extension
//purpose  : 
//=======================================================================
WOKUnix_Extension WOKUnix_Path::Extension() const
{
  Standard_CString ptr = strrchr(mypath->ToCString(), '.');

  if(ptr)
    {
      ptr++;

      // Specification Files
      if(!strcmp(ptr, "cdl"))       return WOKUnix_CDLFile;
      if(!strcmp(ptr, "odl"))       return WOKUnix_ODLFile;
      if(!strcmp(ptr, "idl"))       return WOKUnix_IDLFile;

      // Includes
      if(!strcmp(ptr, "hxx"))       return WOKUnix_HXXFile;
      if(!strcmp(ptr, "ixx"))       return WOKUnix_IXXFile;
      if(!strcmp(ptr, "jxx"))       return WOKUnix_JXXFile;
      if(!strcmp(ptr, "lxx"))       return WOKUnix_LXXFile;
      if(!strcmp(ptr, "gxx"))       return WOKUnix_GXXFile;
      if(!strcmp(ptr, "h"))         return WOKUnix_HFile;
      if(!strcmp(ptr, "pxx"))       return WOKUnix_PXXFile;
      if(!strcmp(ptr, "inc"))       return WOKUnix_INCFile;
      if(!strcmp(ptr, "INC"))       return WOKUnix_INCFile;

      // DBMS Files
      if(!strcmp(ptr, "ddl"))       return WOKUnix_DDLFile;
      if(!strcmp(ptr, "DB"))        return WOKUnix_DBFile;
      if(!strcmp(ptr, "FDDB"))      return WOKUnix_FDDBFile;
      if(!strcmp(ptr, "libschema")) return WOKUnix_LibSchemaFile;
      if(!strcmp(ptr, "asdb"))      return WOKUnix_AppSchemaFile;
      if(!strcmp(ptr, "ho2"))       return WOKUnix_HO2File;

      // Code Generators
      if(!strcmp(ptr, "lex"))       return WOKUnix_LexFile;
      if(!strcmp(ptr, "yacc"))      return WOKUnix_YaccFile;
      if(!strcmp(ptr, "lws"))       return WOKUnix_LWSFile;      
      if(!strcmp(ptr, "psw"))       return WOKUnix_PSWFile;

      // Compilable
      if(!strcmp(ptr, "cxx"))       return WOKUnix_CXXFile;
      if(!strcmp(ptr, "C"))         return WOKUnix_CXXFile;      
      if(!strcmp(ptr, "c"))         return WOKUnix_CFile;
      if(!strcmp(ptr, "f"))         return WOKUnix_F77File;

      // Machine Files
      if(!strcmp(ptr, "o"))         return WOKUnix_ObjectFile;
      if(!strcmp(ptr, "m"))         return WOKUnix_MFile;
      if(!strcmp(ptr, "a"))         return WOKUnix_ArchiveFile;

      if(!strcmp(ptr, "so"))        return WOKUnix_DSOFile;
      if(!strcmp(ptr, "sl"))        return WOKUnix_DSOFile;
      if(!strcmp(ptr, "dylib"))     return WOKUnix_DSOFile;
      
      // WNT Externsions
      if(!strcmp(ptr, "lib"))       return WOKUnix_LIBFile;
      if(!strcmp(ptr, "imp"))       return WOKUnix_IMPFile;
      if(!strcmp(ptr, "def"))       return WOKUnix_DEFile;
      if(!strcmp(ptr, "exp"))       return WOKUnix_EXPFile;

      if(!strcmp(ptr, "rc"))        return WOKUnix_RCFile;
      if(!strcmp(ptr, "res"))       return WOKUnix_RESFile;

      // Miscellaneous
      if(!strcmp(ptr, "Z"))         return WOKUnix_CompressedFile;
      if(!strcmp(ptr, "gz"))        return WOKUnix_CompressedFile;
      if(!strcmp(ptr, "dat"))       return WOKUnix_DATFile;
      if(!strcmp(ptr, "ll"))        return WOKUnix_LispFile;
      if(!strcmp(ptr, "ccl"))       return WOKUnix_LispFile;
      if(!strcmp(ptr, "xwd"))       return WOKUnix_IconFile;
      if(!strcmp(ptr, "txt"))       return WOKUnix_TextFile;
      if(!strcmp(ptr, "tar"))       return WOKUnix_TarFile;
      if(!strcmp(ptr, "csh"))       return WOKUnix_CSHFile;

      if(!strcmp(ptr, "template"))  return WOKUnix_TemplateFile;
      return WOKUnix_UnknownFile;
    }
  
  return WOKUnix_NoExtFile;
}


//=======================================================================
//function : ExtensionName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_Path::ExtensionName() const
{
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(mypath);
  
  pos = retVal->Length();
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      if( chr ==  '.') break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(pos, retVal->Length());
  return retVal;
}

//=======================================================================
//function : BaseName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_Path::BaseName() const
{
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(mypath);
  
  pos = retVal->Length();
  
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      
      if( chr ==  '/' ) break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(pos+1, retVal->Length());

  pos = retVal->Length();
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      if( chr == '.') break;
      --pos;
    }
  
  if(pos)
    retVal = retVal->SubString(1, pos-1);
  return retVal;
}

//=======================================================================
//function : DirName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_Path::DirName() const
{

  Standard_Integer                   pos, len;
  Standard_Character                 chr;
  Standard_Boolean                   fRetry;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(mypath);

  fRetry = Standard_False;
  pos    = len = retVal->Length();

  while(pos != 0)
    {
      chr = retVal -> Value(pos );
      
      if( chr == '/' )
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

//=======================================================================
//function : DirName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_Path::FileName() const
{
  Standard_Integer                   pos;
  Handle(TCollection_HAsciiString)   retVal = new TCollection_HAsciiString(mypath);
  
  pos = retVal->Length();
  
  while(pos != 0)
    {
      Standard_Character chr = retVal -> Value(pos);
      
      if( chr ==  '/' )
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

#endif
